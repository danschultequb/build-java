package qub;

/**
 * An abstract class used to interact with a Java compiler.
 */
public abstract class JavaCompiler
{
    private String version;
    private String bootClasspath;
    private Iterable<Dependency> dependencies;
    private Folder qubFolder;
    private Integer maximumErrors;
    private Integer maximumWarnings;

    public String getVersion()
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

    /**
     * Check that this system has the proper JRE installed to compile to the provided Java version.
     * @param javaVersion The Java version to check for.
     * @param process The process to use.
     * @return Whether or not the proper JRE is installed to compile to the provided Java version.
     */
    public Result<Void> checkJavaVersion(String javaVersion, Process process)
    {
        PreCondition.assertNotNull(process, "process");

        Result<Void> result = Result.success();

        if (!Strings.isNullOrEmpty(javaVersion))
        {
            final String javaHome = process.getEnvironmentVariable("JAVA_HOME");
            if (Strings.isNullOrEmpty(javaHome))
            {
                result = Result.error(new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."));
            }
            else if (QubBuild.isJava8(javaVersion))
            {
                setVersion("8");

                final Folder javaFolder = process.getFileSystem().getFolder(javaHome).awaitError().getParentFolder().awaitError();
                final Iterable<Folder> jreAndJdkFolders = javaFolder.getFolders().awaitError();
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
                final String dependencyRelativePath =
                    dependency.getPublisher() + "/" +
                    dependency.getProject() + "/" +
                    dependency.getVersion() + "/" +
                    dependency.getProject() + ".jar";
                return qubFolder.getFile(dependencyRelativePath).await().toString();
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
     * @param process The process to use.
     * @return The result of the compilation.
     */
    public abstract Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder rootFolder, Folder outputFolder, Process process);

    public static JavaCompilerIssue error(String sourceFilePath, int lineNumber, int columnNumber, String errorMessage)
    {
        return new JavaCompilerIssue(sourceFilePath, lineNumber, columnNumber, Issue.Type.Error, errorMessage);
    }

    public static JavaCompilerIssue warning(String sourceFilePath, int lineNumber, int columnNumber, String warningMessage)
    {
        return new JavaCompilerIssue(sourceFilePath, lineNumber, columnNumber, Issue.Type.Warning, warningMessage);
    }
}
