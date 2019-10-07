package qub;

/**
 * A JavaCompiler that uses the actual javac executable.
 */
public class JavaCompiler
{
    private final ProcessFactory processFactory;
    private String version;
    private String bootClasspath;
    private Iterable<Dependency> dependencies;
    private Folder qubFolder;
    private Integer maximumErrors;
    private Integer maximumWarnings;
    private VerboseCharacterWriteStream verbose;

    public JavaCompiler(ProcessFactory processFactory)
    {
        PreCondition.assertNotNull(processFactory, "processFactory");

        this.processFactory = processFactory;
    }public String getVersion()
    {
        return version;
    }

    public JavaCompiler setVersion(String version)
    {
        this.version = version;
        return this;
    }

    public String getBootClasspath()
    {
        return bootClasspath;
    }

    public JavaCompiler setBootClasspath(String bootClasspath)
    {
        this.bootClasspath = bootClasspath;
        return this;
    }

    public JavaCompiler setDependencies(Iterable<Dependency> dependencies)
    {
        this.dependencies = dependencies;
        return this;
    }

    public Iterable<Dependency> getDependencies()
    {
        return dependencies;
    }

    public JavaCompiler setQubFolder(Folder qubFolder)
    {
        this.qubFolder = qubFolder;
        return this;
    }

    public Folder getQubFolder()
    {
        return qubFolder;
    }

    /**
     * Set the maximum number of errors that the compiler should report before halting compilation.
     * If nothing is specified (maximumErrors is null), then the default (100) will be used.
     * @param maximumErrors The maximum number of errors that the compiler should report before
     *                      halting compilation.
     * @return This object for method chaining.
     */
    public JavaCompiler setMaximumErrors(Integer maximumErrors)
    {
        this.maximumErrors = maximumErrors;
        return this;
    }

    /**
     * Get the maximum number of errors that the compiler should report before halting compilation.
     * If nothing is specified (maximumErrors is null), then the default (100) will be used.
     * @return The maximum number of errors that the compiler should report before halting
     * compilation.
     */
    public Integer getMaximumErrors()
    {
        return maximumErrors;
    }

    /**
     * Set the maximum number of warnings that the compiler should report. If nothing is specified
     * (maximumWarnings is null), then the default (100) will be used.
     * @param maximumWarnings The maximum number of warnings that the compiler should report.
     * @return This object for method chaining.
     */
    public JavaCompiler setMaximumWarnings(Integer maximumWarnings)
    {
        this.maximumWarnings = maximumWarnings;
        return this;
    }

    /**
     * Get the maximum number of warnings that the compiler should report. If nothing is specified
     * (maximumWarnings is null), then the default (100) will be used.
     * @return The maximum number of warnings that the compiler should report.
     */
    public Integer getMaximumWarnings()
    {
        return maximumWarnings;
    }

    public VerboseCharacterWriteStream getVerbose()
    {
        return verbose;
    }

    public JavaCompiler setVerbose(VerboseCharacterWriteStream verbose)
    {
        this.verbose = verbose;
        return this;
    }

    public Result<Void> writeVerboseLine(String message)
    {
        return Result.create(() ->
        {
            if (verbose != null)
            {
                verbose.writeLine(message).await();
            }
        });
    }

    /**
     * Check that this system has the proper JRE installed to compile to the provided Java version.
     * @param javaVersion The Java version to check for.
     * @return Whether or not the proper JRE is installed to compile to the provided Java version.
     */
    public Result<Void> checkJavaVersion(String javaVersion, EnvironmentVariables environmentVariables, FileSystem fileSystem)
    {
        PreCondition.assertNotNull(environmentVariables, "environmentVariables");
        PreCondition.assertNotNull(fileSystem, "fileSystem");

        Result<Void> result = Result.success();

        if (!Strings.isNullOrEmpty(javaVersion))
        {
            final String javaHome = environmentVariables.get("JAVA_HOME")
                .catchError(NotFoundException.class)
                .await();
            if (Strings.isNullOrEmpty(javaHome))
            {
                result = Result.error(new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."));
            }
            else if (QubBuild.isJava8(javaVersion))
            {
                setVersion("8");

                final Folder javaFolder = fileSystem.getFolder(javaHome).await().getParentFolder().await();
                final Iterable<Folder> jreAndJdkFolders = javaFolder.getFolders().await();
                final Iterable<Folder> jre18Folders = jreAndJdkFolders.where((Folder jreOrJdkFolder) -> jreOrJdkFolder.getName().startsWith("jre1.8.0_"));
                if (!jre18Folders.any())
                {
                    result = Result.error(new NotFoundException("No installed JREs found for Java version " + Strings.escapeAndQuote(javaVersion) + "."));
                }
                else
                {
                    final Folder jre18Folder = jre18Folders.maximum((Folder lhs, Folder rhs) -> Comparison.from(lhs.getName().compareTo(rhs.getName())));
                    jre18Folder.getFile("lib/rt.jar")
                        .then((File bootClasspathFile) ->
                        {
                            setBootClasspath(bootClasspathFile.toString());
                        });
                }
            }
            else if (QubBuild.isJava11(javaVersion))
            {
                setVersion("11");
            }
            else
            {
                result = Result.error(new NotFoundException("No bootclasspath runtime jar file could be found for Java version " + Strings.escapeAndQuote(javaVersion) + "."));
            }
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Get the arguments that will be used to invoke the Java compiler.
     * @param sourceFiles The source files to compile.
     * @param rootFolder The root of the project folder.
     * @param outputFolder The folder where the compiled files will be put.
     * @return The arguments that will be used to invoke the Java compiler.
     */
    public Iterable<String> getArguments(Iterable<File> sourceFiles, Folder rootFolder, Folder outputFolder)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(rootFolder, "rootFolder");
        PreCondition.assertNotNull(outputFolder, "outputFolder");

        final List<String> result = List.create();

        result.addAll("-d", outputFolder.toString());
        result.add("-Xlint:unchecked");
        result.add("-Xlint:deprecation");

        final String version = getVersion();
        if (!Strings.isNullOrEmpty(version))
        {
            result.addAll("-source", version);
            result.addAll("-target", version);
        }

        final String bootClasspath = getBootClasspath();
        if (!Strings.isNullOrEmpty(bootClasspath))
        {
            result.addAll("-bootclasspath", bootClasspath);
        }

        final List<String> classPaths = List.create(outputFolder.toString());
        final Iterable<Dependency> dependencies = getDependencies();
        if (!Iterable.isNullOrEmpty(dependencies))
        {
            if (qubFolder == null)
            {
                throw new NotFoundException("Cannot resolve project dependencies without a qubFolder.");
            }

            classPaths.addAll(dependencies.map((Dependency dependency) ->
            {
                return QubBuild.resolveDependencyReference(qubFolder, dependency).toString();
            }));
        }
        result.addAll("-classpath", Strings.join(';', classPaths));

        if (maximumErrors != null)
        {
            result.addAll("-Xmaxerrs", maximumErrors.toString());
        }

        if (maximumWarnings != null)
        {
            result.addAll("-Xmaxwarns", maximumWarnings.toString());
        }

        result.addAll(sourceFiles.map((File sourceFile) -> sourceFile.relativeTo(rootFolder).toString()));

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }

    /**
     * Compile all of the provided Java files using the provided Java version. All of the compiled
     * class files will be put into the outputFolder.
     * @param sourceFiles The source files to compile.
     * @param rootFolder The folder that contains all of the source files to compile.
     * @param outputFolder The output folder where the compiled results will be placed.
     * @param warnings How warnings should be treated.
     * @return The result of the compilation.
     */
    public Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder rootFolder, Folder outputFolder, Warnings warnings)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(rootFolder, "rootFolder");
        PreCondition.assertNotNull(outputFolder, "outputFolder");
        PreCondition.assertNotNull(warnings, "warnings");

        return Result.create(() ->
        {
            final ProcessBuilder javac = this.processFactory.getProcessBuilder("javac").await();
            javac.setWorkingFolder(rootFolder);

            final InMemoryByteStream stdout = new InMemoryByteStream();
            javac.redirectOutput(stdout);

            final InMemoryByteStream stderr = new InMemoryByteStream();
            javac.redirectError(stderr);

            javac.addArguments(getArguments(sourceFiles, rootFolder, outputFolder));

            writeVerboseLine("Running " + javac.getCommand() + "...").await();
            final Integer exitCode = javac.run().await();
            final String output = stdout.asCharacterReadStream().getText().await();
            final String error = stderr.asCharacterReadStream().getText().await();
            final List<JavaCompilerIssue> issues = List.create();

            if (!Strings.isNullOrEmpty(error))
            {
                final Iterator<String> errorLines = Strings.getLines(error).iterate();
                errorLines.next();
                while (errorLines.hasCurrent())
                {
                    final String errorLine = errorLines.takeCurrent();
                    final int firstColon = errorLine.indexOf(':');
                    if (firstColon >= 0)
                    {
                        final String sourceFilePath = errorLine.substring(0, firstColon);
                        final int secondColon = errorLine.indexOf(':', firstColon + 1);
                        if (secondColon >= 0)
                        {
                            final String lineNumberString = errorLine.substring(firstColon + 1, secondColon);
                            final Integer lineNumber = Integers.parse(lineNumberString)
                                .catchError(NumberFormatException.class)
                                .await();
                            if (lineNumber != null)
                            {
                                final int thirdColon = errorLine.indexOf(':', secondColon + 1);
                                final String issueTypeString = errorLine.substring(secondColon + 1, thirdColon).trim();
                                final Issue.Type issueType = (warnings == Warnings.Error || issueTypeString.equalsIgnoreCase(Issue.Type.Error.toString()))
                                    ? Issue.Type.Error
                                    : Issue.Type.Warning;
                                final String message = errorLine.substring(thirdColon + 1).trim();

                                if (errorLines.hasCurrent())
                                {
                                    // Take source code line.
                                    errorLines.takeCurrent();

                                    if (errorLines.hasCurrent())
                                    {
                                        final String caretLine = errorLines.takeCurrent();
                                        final int caretIndex = caretLine.indexOf('^');
                                        final int columnNumber = caretIndex + 1;

                                        final String normalizedSourceFilePath = Path.parse(sourceFilePath).normalize().toString();
                                        issues.add(new JavaCompilerIssue(normalizedSourceFilePath, lineNumber, columnNumber, issueType, message));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return new JavaCompilationResult(
                exitCode,
                output,
                error,
                issues);
        });
    }

    public static JavaCompilerIssue error(String sourceFilePath, int lineNumber, int columnNumber, String errorMessage)
    {
        return new JavaCompilerIssue(sourceFilePath, lineNumber, columnNumber, Issue.Type.Error, errorMessage);
    }

    public static JavaCompilerIssue warning(String sourceFilePath, int lineNumber, int columnNumber, String warningMessage)
    {
        return new JavaCompilerIssue(sourceFilePath, lineNumber, columnNumber, Issue.Type.Warning, warningMessage);
    }
}
