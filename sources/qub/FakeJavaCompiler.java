package qub;

public class FakeJavaCompiler extends JavaCompiler
{
    private int exitCode;
    private Iterable<JavaCompilerIssue> issues;

    public int getExitCode()
    {
        return exitCode;
    }

    public FakeJavaCompiler setExitCode(int exitCode)
    {
        this.exitCode = exitCode;
        return this;
    }

    public Iterable<JavaCompilerIssue> getIssues()
    {
        return issues;
    }

    public FakeJavaCompiler setIssues(Iterable<JavaCompilerIssue> issues)
    {
        this.issues = issues;
        return this;
    }

    @Override
    public FakeJavaCompiler setVerbose(CommandLineParameterVerbose verbose)
    {
        super.setVerbose(verbose);
        return this;
    }

    public Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder rootFolder, Folder outputFolder, Process process)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(rootFolder, "rootFolder");
        PreCondition.assertNotNull(outputFolder, "outputFolder");
        PreCondition.assertNotNull(process, "process");

        final Iterable<String> arguments = getArguments(sourceFiles, rootFolder, outputFolder);
        writeVerboseLine("Running javac " + Strings.join(' ', arguments) + "...").await();
        for (final File sourceFile : sourceFiles)
        {
            sourceFile.copyTo(QubBuild.getClassFile(sourceFile, rootFolder, outputFolder));
        }

        return Result.success(
            new JavaCompilationResult(
                getExitCode(),
                "",
                "",
                getIssues()));
    }
}
