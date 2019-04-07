package qub;

/**
 * A JavaCompiler that uses the actual javac executable.
 */
public class JavacJavaCompiler extends JavaCompiler
{
    @Override
    public Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder rootFolder, Folder outputFolder, Process process)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(rootFolder, "rootFolder");
        PreCondition.assertNotNull(outputFolder, "outputFolder");
        PreCondition.assertNotNull(process, "process");

        return Result.create(() ->
        {
            final ProcessBuilder javac = process.getProcessBuilder("javac").await();
            javac.setWorkingFolder(rootFolder);

            final InMemoryByteStream stdout = new InMemoryByteStream();
            javac.redirectOutput(stdout);

            final InMemoryByteStream stderr = new InMemoryByteStream();
            javac.redirectError(stderr);

            javac.addArguments(getArguments(sourceFiles, rootFolder, outputFolder));

            QubBuild.verboseLog(process, " Running " + javac.getCommand() + "...").await();
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
                            Integers.parse(lineNumberString)
                                .then((Integer lineNumber) ->
                                {
                                    final int thirdColon = errorLine.indexOf(':', secondColon + 1);
                                    final String issueTypeString = errorLine.substring(secondColon + 1, thirdColon).trim();
                                    final Issue.Type issueType = issueTypeString.equalsIgnoreCase(Issue.Type.Error.toString()) ? Issue.Type.Error : Issue.Type.Warning;
                                    final String message = errorLine.substring(thirdColon + 1).trim();

                                    if (errorLines.hasCurrent())
                                    {
                                        final String codeLine = errorLines.takeCurrent();
                                        if (errorLines.hasCurrent())
                                        {
                                            final String caretLine = errorLines.takeCurrent();
                                            final int caretIndex = caretLine.indexOf('^');
                                            final int columnNumber = caretIndex + 1;

                                            issues.add(new JavaCompilerIssue(sourceFilePath, lineNumber, columnNumber, issueType, message));
                                        }
                                    }
                                })
                                .catchError(NumberFormatException.class, () ->
                                {
                                });
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
