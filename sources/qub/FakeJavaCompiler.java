package qub;

public class FakeJavaCompiler extends JavaCompiler
{
    private int exitCode;

    public int getExitCode()
    {
        return exitCode;
    }

    public void setExitCode(int exitCode)
    {
        this.exitCode = exitCode;
    }

    public Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder rootFolder, Folder outputFolder, Console console)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(rootFolder, "rootFolder");
        PreCondition.assertNotNull(outputFolder, "outputFolder");
        PreCondition.assertNotNull(console, "console");

        for (final File sourceFile : sourceFiles)
        {
            sourceFile.copyTo(Build.getClassFile(sourceFile, rootFolder, outputFolder));
        }

        return Result.success(
            new JavaCompilationResult(
                getExitCode(),
                Iterable.empty()));
    }
}
