package qub;

/**
 * A ProcessBuilder that is specific to the javac application.
 */
public class JavacProcessBuilder extends ProcessBuilderDecorator<JavacProcessBuilder> implements JavacArguments<JavacProcessBuilder>
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
     * Run the javac process that this builder has constructed.
     * @param warnings How compilation warnings should be handled.
     * @param verbose The stream that verbose logs should be written to.
     * @return The parsed result of running the javac process.
     */
    public Result<JavaCompilationResult> compile(Warnings warnings, CharacterWriteStream verbose)
    {
        PreCondition.assertNotNull(warnings, "warnings");
        PreCondition.assertNotNull(verbose, "verbose");

        return Result.create(() ->
        {
            final InMemoryCharacterToByteStream stdout = InMemoryCharacterToByteStream.create();
            this.redirectOutput(stdout);

            final InMemoryCharacterToByteStream stderr = InMemoryCharacterToByteStream.create();
            this.redirectError(stderr);

            verbose.writeLine("Running " + this.getCommand() + "...").await();
            final Integer exitCode = this.run().await();
            final String outputText = stdout.getText().await();
            final String errorText = stderr.getText().await();
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
