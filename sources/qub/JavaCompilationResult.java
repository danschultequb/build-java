package qub;

/**
 * The result of compiling Java source code files.
 */
public class JavaCompilationResult
{
    /**
     * The exit code that was returned form the compilation process.
     */
    public final int exitCode;
    /**
     * The erros and warnings that were emitted during compilation.
     */
    public final Iterable<JavaCompilerIssue> issues;

    public JavaCompilationResult(int exitCode, Iterable<JavaCompilerIssue> issues)
    {
        this.exitCode = exitCode;
        this.issues = issues;
    }
}
