package qub;

/**
 * The result of compiling Java source code files.
 */
public class JavaCompilationResult
{
    private int exitCode;

    /**
     * Get the exit code that was returned from the compilation process.
     * @return The exit code that was returned from the compilation process.
     */
    public int getExitCode()
    {
        return exitCode;
    }

    /**
     * Set the exit code that was returned from the compilation process.
     * @param exitCode The exit code that was returned from the compilation process.
     */
    public void setExitCode(int exitCode)
    {
        this.exitCode = exitCode;
    }
}
