package qub;

/**
 * A JavaCompiler that uses the actual javac executable.
 */
public class JavacJavaCompiler extends JavaCompiler
{
    private final ProcessFactory processFactory;

    public JavacJavaCompiler(ProcessFactory processFactory)
    {
        PreCondition.assertNotNull(processFactory, "processFactory");

        this.processFactory = processFactory;
    }

    @Override
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
}
