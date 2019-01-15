package qub;

/**
 * A JavaCompiler that uses the actual javac executable.
 */
public class JavacJavaCompiler implements JavaCompiler
{
    @Override
    public Result<JavaCompilationResult> compile(Folder sourceFolder, String javaVersion, Folder outputFolder, Console console)
    {
        PreCondition.assertNotNull(sourceFolder, "sourceFolder");
        PreCondition.assertEqual("1.8", javaVersion, "javaVersion");
        PreCondition.assertNotNull(outputFolder, "outputFolder");

        final Stopwatch stopwatch = console.getStopwatch();
        stopwatch.start();

        console.write("Compiling " + sourceFolder.getName() + "...");

        final Value<Iterable<File>> javaSourceFiles = Value.create();
        return Build.getJavaSourceFiles(sourceFolder, javaSourceFiles)
                .thenResult(() -> console.getProcessBuilder("javac"))
                .then((ProcessBuilder javac) ->
                {
                    final Value<Boolean> wroteNewLineBeforeOutputOrError = new Value<>();

                    javac.redirectOutput(new NewLineBeforeFirstWriteByteWriteStream(console.getOutputAsByteWriteStream(), wroteNewLineBeforeOutputOrError));
                    javac.redirectError(new NewLineBeforeFirstWriteByteWriteStream(console.getErrorAsByteWriteStream(), wroteNewLineBeforeOutputOrError));

                    javac.addArguments("-d", outputFolder.getPath().toString());
                    javac.addArgument("-Xlint:unchecked");
                    javac.addArgument("-Xlint:deprecation");

                    if (!Strings.isNullOrEmpty(javaVersion))
                    {
                        javac.addArguments("-source", javaVersion);
                        javac.addArguments("-target", javaVersion);

                        if (javaVersion.equals("1.8") || javaVersion.equals("8"))
                        {
                            final Folder javaFolder = console.getFileSystem().getFolder("C:/Program Files/Java/").throwErrorOrGetValue();
                            javac.addArguments("-bootclasspath",
                                    javaFolder.getPath()
                                            .concatenateSegment("jre1.8.0_192/lib/rt.jar")
                                            .toString());
                        }
                    }

                    javac.addArguments(javaSourceFiles.get().map(FileSystemEntry::toString));

                    outputFolder.create().throwError();

                    final JavaCompilationResult result = new JavaCompilationResult();
                    result.setExitCode(javac.run());

                    final Duration compilationDuration = stopwatch.stop().toSeconds();
                    console.writeLine(" Done (" + compilationDuration.toString("0.0") + ")");

                    return result;
                });
    }
}
