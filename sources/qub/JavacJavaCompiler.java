package qub;

/**
 * A JavaCompiler that uses the actual javac executable.
 */
public class JavacJavaCompiler extends JavaCompiler
{
    @Override
    public Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder rootFolder, Folder outputFolder, Console console)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(rootFolder, "rootFolder");
        PreCondition.assertNotNull(outputFolder, "outputFolder");
        PreCondition.assertNotNull(console, "console");

        return console.getProcessBuilder("javac")
            .thenResult((ProcessBuilder javac) ->
            {
                javac.setWorkingFolder(rootFolder);

                final Value<Boolean> wroteNewLineBeforeOutputOrError = new Value<>();

                javac.redirectOutput(new NewLineBeforeFirstWriteByteWriteStream(console.getOutputAsByteWriteStream(), wroteNewLineBeforeOutputOrError));
                javac.redirectError(new NewLineBeforeFirstWriteByteWriteStream(console.getErrorAsByteWriteStream(), wroteNewLineBeforeOutputOrError));

                javac.addArguments(getArguments(sourceFiles, rootFolder, outputFolder));

                return javac.run().then((Integer exitCode) ->
                    new JavaCompilationResult(
                        exitCode,
                        Iterable.empty()));
            });
    }
}
