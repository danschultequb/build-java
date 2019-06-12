package qub;

public class FakeJavaCompiler extends JavaCompiler
{
    public int exitCode;
    public Iterable<JavaCompilerIssue> issues;

    public Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder rootFolder, Folder outputFolder, Process process)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(rootFolder, "rootFolder");
        PreCondition.assertNotNull(outputFolder, "outputFolder");
        PreCondition.assertNotNull(process, "process");

        final Iterable<String> arguments = getArguments(sourceFiles, rootFolder, outputFolder);
        QubBuild.verboseLog(process, "Running javac " + Strings.join(' ', arguments) + "...").await();
        for (final File sourceFile : sourceFiles)
        {
            sourceFile.copyTo(QubBuild.getClassFile(sourceFile, rootFolder, outputFolder));
        }

        return Result.success(
            new JavaCompilationResult(
                exitCode,
                "",
                "",
                issues));
    }
}
