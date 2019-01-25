package qub;

public class FakeJavaCompiler implements JavaCompiler
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

    @Override
    public Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder sourceFolder, Folder outputFolder, String javaVersion, Console console)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(sourceFolder, "sourceFolder");
        PreCondition.assertNotNull(outputFolder, "outputFolder");
        PreCondition.assertNotNull(console, "console");

        for (final File sourceFile : sourceFiles)
        {
            final Path sourceFileRelativePath = sourceFile.relativeTo(sourceFolder);
            final File classFile = outputFolder.getFile(sourceFileRelativePath.changeFileExtension(".class")).throwErrorOrGetValue();
            sourceFile.copyTo(classFile.getPath());
        }

        final JavaCompilationResult result = new JavaCompilationResult();
        result.setExitCode(getExitCode());

        return Result.success(result);
    }
}
