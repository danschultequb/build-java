package qub;

/**
 * A ProcessBuilder that is specific to the javac application.
 */
public class JavacProcessBuilder extends ProcessBuilderDecorator<JavacProcessBuilder>
{
    public static final String executablePathString = "javac";
    public static final Path executablePath = Path.parse(JavacProcessBuilder.executablePathString);

    private JavacProcessBuilder(ProcessBuilder processBuilder)
    {
        super(processBuilder);
    }

    /**
     * Get a JavacProcessBuilder from the provided Process.
     * @param process The Process to get the JavacProcessBuilder from.
     * @return The JavacProcessBuilder.
     */
    public static Result<JavacProcessBuilder> get(Process process)
    {
        PreCondition.assertNotNull(process, "process");

        return JavacProcessBuilder.get(process.getProcessFactory());
    }

    /**
     * Get a JavacProcessBuilder from the provided ProcessFactory.
     * @param processFactory The ProcessFactory to get the JavacProcessBuilder from.
     * @return The JavacProcessBuilder.
     */
    public static Result<JavacProcessBuilder> get(ProcessFactory processFactory)
    {
        PreCondition.assertNotNull(processFactory, "processFactory");

        return Result.create(() ->
        {
            return new JavacProcessBuilder(processFactory.getProcessBuilder(JavacProcessBuilder.executablePathString).await());
        });
    }

    /**
     * Set the folder that the javac process will write its output files to.
     * @param outputFolder The folder that the javac process will write its output files to.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addOutputFolder(Folder outputFolder)
    {
        PreCondition.assertNotNull(outputFolder, "outputFolder");

        final Path outputFolderPath = outputFolder.getPath().relativeTo(this.getWorkingFolderPath());
        return this.addArguments("-d", outputFolderPath.toString());
    }

    /**
     * Add the -Xlint:unchecked argument.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addXlintUnchecked()
    {
        return this.addArgument("-Xlint:unchecked");
    }

    /**
     * Add the -Xlint:deprecation argument.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addXlintDeprecation()
    {
        return this.addArgument("-Xlint:deprecation");
    }

    /**
     * Set the version of Java that will be used for the source parameter.
     * @param javaSourceVersion The version of Java that will be used for the source version.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addJavaSourceVersion(String javaSourceVersion)
    {
        PreCondition.assertNotNullAndNotEmpty(javaSourceVersion, "javaSourceVersion");

        return this.addArguments("-source", javaSourceVersion);
    }

    /**
     * Set the version of Java that will be used for the target parameter.
     * @param javaTargetVersion The version of Java that will be used for the target version.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addJavaTargetVersion(String javaTargetVersion)
    {
        PreCondition.assertNotNullAndNotEmpty(javaTargetVersion, "javaTargetVersion");

        return this.addArguments("-target", javaTargetVersion);
    }

    /**
     * Set the classpath to the runtime jar for the currently set java version.
     * @param bootClasspath The classpath to the runtime jar for the currently set java version.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addBootClasspath(String bootClasspath)
    {
        PreCondition.assertNotNullAndNotEmpty(bootClasspath, "bootClasspath");

        return this.addArguments("-bootclasspath", bootClasspath);
    }

    /**
     * Set the classpath to the runtime jar for the currently set java version.
     * @param bootClasspath The classpath to the runtime jar for the currently set java version.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addBootClasspath(Path bootClasspath)
    {
        PreCondition.assertNotNull(bootClasspath, "bootClasspath");

        return this.addBootClasspath(bootClasspath.toString());
    }

    /**
     * Set the classpath to the runtime jar for the currently set java version.
     * @param bootClasspath The classpath to the runtime jar for the currently set java version.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addBootClasspath(File bootClasspath)
    {
        PreCondition.assertNotNull(bootClasspath, "bootClasspath");

        return this.addBootClasspath(bootClasspath.toString());
    }

    /**
     * Set the classpath argument of the javac process.
     * @param classpath The classpath argument of the javac process.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addClasspath(String classpath)
    {
        PreCondition.assertNotNullAndNotEmpty(classpath, "classpath");

        return this.addArguments("-classpath", classpath);
    }

    /**
     * Set the classpath argument of the javac process. The values of the provided Iterable will be
     * separated by semi-colons (;).
     * @param classpath The classpath argument of the javac process.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addClasspath(Iterable<String> classpath)
    {
        PreCondition.assertNotNullAndNotEmpty(classpath, "classpath");

        return this.addClasspath(Strings.join(';', classpath));
    }

    /**
     * Set the maximum number of errors that will be returned by the javac process.
     * @param maximumErrors The maximum number of errors that will be returned by the javac process.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addMaximumErrors(int maximumErrors)
    {
        PreCondition.assertGreaterThan(maximumErrors, 0, "maximumErrors");

        return this.addArguments("-Xmaxerrs", Integers.toString(maximumErrors));
    }

    /**
     * Set the maximum number of warnings that will be returned by the javac process.
     * @param maximumWarnings The maximum number of warnings that will be returned by the javac
     *                        process.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addMaximumWarnings(int maximumWarnings)
    {
        PreCondition.assertGreaterThan(maximumWarnings, 0, "maximumWarnings");

        return this.addArguments("-Xmaxwarns", Integers.toString(maximumWarnings));
    }

    /**
     * Add a source file to compile.
     * @param sourceFilePathString The path to the source file to compile.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addSourceFile(String sourceFilePathString)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePathString, "sourceFilePathString");

        return this.addSourceFile(Path.parse(sourceFilePathString));
    }

    /**
     * Add a source file to compile.
     * @param sourceFilePath The path to the source file to compile.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addSourceFile(Path sourceFilePath)
    {
        PreCondition.assertNotNull(sourceFilePath, "sourceFilePath");

        if (sourceFilePath.isRooted())
        {
            sourceFilePath = sourceFilePath.relativeTo(this.getWorkingFolderPath());
        }

        return this.addArgument(sourceFilePath.toString());
    }

    /**
     * Add a source file to compile.
     * @param sourceFile The source file to compile.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addSourceFile(File sourceFile)
    {
        PreCondition.assertNotNull(sourceFile, "sourceFile");

        return this.addSourceFile(sourceFile.getPath());
    }

    /**
     * Add source files to compile.
     * @param sourceFilePathStrings The source files to compile.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addSourceFilePathStrings(Iterable<String> sourceFilePathStrings)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePathStrings, "sourceFilePathStrings");

        for (final String sourceFilePathString : sourceFilePathStrings)
        {
            this.addSourceFile(sourceFilePathString);
        }

        return this;
    }

    /**
     * Add source files to compile.
     * @param sourceFilePaths The source files to compile.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addSourceFilePaths(Iterable<Path> sourceFilePaths)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePaths, "sourceFilePaths");

        for (final Path sourceFilePath : sourceFilePaths)
        {
            this.addSourceFile(sourceFilePath);
        }

        return this;
    }

    /**
     * Add source files to compile.
     * @param sourceFiles The source files to compile.
     * @return This object for method chaining.
     */
    public JavacProcessBuilder addSourceFiles(Iterable<File> sourceFiles)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");

        for (final File sourceFile : sourceFiles)
        {
            this.addSourceFile(sourceFile);
        }

        return this;
    }

    /**
     * Run the javac process that this builder has constructed.
     * @param warnings How compilation warnings should be handled.
     * @param verbose The stream that verbose logs should be written to.
     * @return The parsed result of running the javac process.
     */
    public Result<JavaCompilationResult> compile(Warnings warnings, VerboseCharacterWriteStream verbose)
    {
        PreCondition.assertNotNull(warnings, "warnings");
        PreCondition.assertNotNull(verbose, "verbose");

        return Result.create(() ->
        {
            final InMemoryByteStream stdout = new InMemoryByteStream();
            this.redirectOutput(stdout);

            final InMemoryByteStream stderr = new InMemoryByteStream();
            this.redirectError(stderr);

            verbose.writeLine("Running " + this.getCommand() + "...").await();
            final Integer exitCode = this.run().await();
            final String outputText = stdout.asCharacterReadStream().getText().await();
            final String errorText = stderr.asCharacterReadStream().getText().await();
            final Iterable<JavaCompilerIssue> issues = JavacProcessBuilder.parseIssues(errorText, warnings);

            return new JavaCompilationResult(
                exitCode,
                outputText,
                errorText,
                issues);
        });
    }

    /**
     * Parse JavaCompilerIssue objects from the provided error text.
     * @param errorText The error text that was written by a javac process.
     * @param warnings How warnings should be treated.
     * @return The parsed JavacCompilerIssues objects.
     */
    public static Iterable<JavaCompilerIssue> parseIssues(String errorText, Warnings warnings)
    {
        PreCondition.assertNotNull(errorText, "errorText");
        PreCondition.assertNotNull(warnings, "warnings");

        final List<JavaCompilerIssue> result = List.create();

        if (!Strings.isNullOrEmpty(errorText))
        {
            final Iterator<String> errorLines = Strings.getLines(errorText).iterate();
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
                                    result.add(new JavaCompilerIssue(normalizedSourceFilePath, lineNumber, columnNumber, issueType, message));
                                }
                            }
                        }
                    }
                }
            }
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Find the boot classpath runtime jar file that should be used during compilation. If this
     * function returns a null File, then that means that there is no boot classpath to add.
     * @param javaVersion The version of java to find the boot classpath for.
     * @param javaHomeFolder The folder that is specified in the environment variables JAVA_HOME
     *                       value.
     * @return The boot classpath runtime jar file that should be used during compilation, or null
     * if no boot classpath runtime jar file is needed.
     */
    public static Result<File> findBootClasspathFromJavaHomeFolder(String javaVersion, Folder javaHomeFolder)
    {
        PreCondition.assertNotNullAndNotEmpty(javaVersion, "javaVersion");
        PreCondition.assertNotNull(javaHomeFolder, "javaHomeFolder");

        return Result.create(() ->
        {
            File result;
            if (QubBuild.isJava8(javaVersion))
            {
                final Folder javaFolder = javaHomeFolder.getParentFolder().await();
                final Iterable<Folder> jreAndJdkFolders = javaFolder.getFolders().await();
                final Iterable<Folder> jre18Folders = jreAndJdkFolders.where((Folder jreOrJdkFolder) -> jreOrJdkFolder.getName().startsWith("jre1.8.0_"));
                if (!jre18Folders.any())
                {
                    throw new NotFoundException("No installed JREs found for Java version " + Strings.escapeAndQuote(javaVersion) + ".");
                }
                else
                {
                    final Folder jre18Folder = jre18Folders.maximum((Folder lhs, Folder rhs) -> Comparison.from(lhs.getName().compareTo(rhs.getName())));
                    result = jre18Folder.getFile("lib/rt.jar").await();
                }
            }
            else if (QubBuild.isJava11(javaVersion))
            {
                result = null;
            }
            else
            {
                throw new NotFoundException("No bootclasspath runtime jar file could be found for Java version " + Strings.escapeAndQuote(javaVersion) + ".");
            }

            return result;
        });
    }
}
