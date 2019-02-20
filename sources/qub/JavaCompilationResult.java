package qub;

/**
 * The result of compiling Java source code files.
 */
public class JavaCompilationResult
{
    /**
     * The exit code that was returned from the compilation process.
     */
    public final int exitCode;
    /**
     * The text that was written to the standard output stream.
     */
    public final String output;
    /**
     * The text that was written to the standard error stream.
     */
    public final String error;
    /**
     * The errors and warnings that were emitted during compilation.
     */
    public final Iterable<JavaCompilerIssue> issues;

    /**
     * Create a new JavaCompilationResult object.
     * @param exitCode The exit code that was returned from the compilation process.
     * @param output The text that was written to the standard output stream.
     * @param error The text that was written to the standard error stream.
     * @param issues The errors and warnings that were emitted during compilation.
     */
    public JavaCompilationResult(int exitCode, String output, String error, Iterable<JavaCompilerIssue> issues)
    {
        this.exitCode = exitCode;
        this.output = output;
        this.error = error;
        this.issues = issues;
    }
}
