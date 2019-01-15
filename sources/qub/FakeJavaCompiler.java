package qub;

public class FakeJavaCompiler implements JavaCompiler
{
    private int exitCode;

    public void setExitCode(int exitCode)
    {
        this.exitCode = exitCode;
    }

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
            .then(() ->
            {
                outputFolder.create();
                for (final File javaSourceFile : javaSourceFiles.get())
                {
                    final Path javaSourceFileSourcePath = javaSourceFile.getPath();
                    final Path javaSourceFileRelativeToSourceFolder = javaSourceFileSourcePath.relativeTo(sourceFolder);
                    final Path javaSourceFileOutputPath = outputFolder.getPath().concatenateSegment(javaSourceFileRelativeToSourceFolder).changeFileExtension(".class");
                    javaSourceFile.copyTo(javaSourceFileOutputPath);
                }

                final JavaCompilationResult result = new JavaCompilationResult();
                result.setExitCode(exitCode);

                final Duration compilationDuration = stopwatch.stop().toSeconds();
                console.writeLine(" Done (" + compilationDuration.toString("0.0") + ")");

                return result;
            });
    }
}
