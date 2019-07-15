package qub;

/**
 * An issue that occurred while compiling Java source code.
 */
public class JavaCompilerIssue
{
    /**
     * The path to the source file that contains this issue. This path will be relative to the
     * project root folder.
     */
    public final String sourceFilePath;
    /**
     * The line number that the issue occurred at.
     */
    public final int lineNumber;
    /**
     * The column number that the issue occurred at.
     */
    public final int columnNumber;
    /**
     * The type of the issue.
     */
    public final Issue.Type type;
    /**
     * The message that explains the issue.
     */
    public final String message;

    public JavaCompilerIssue(String sourceFilePath, int lineNumber, int columnNumber, Issue.Type type, String message)
    {
        this.sourceFilePath = sourceFilePath;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.type = type;
        this.message = message;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JavaCompilerIssue && equals((JavaCompilerIssue)rhs);
    }

    public boolean equals(JavaCompilerIssue rhs)
    {
        return rhs != null &&
            Comparer.equal(sourceFilePath, rhs.sourceFilePath) &&
            Comparer.equal(lineNumber, rhs.lineNumber) &&
            Comparer.equal(columnNumber, rhs.columnNumber) &&
            Comparer.equal(type, rhs.type) &&
            Comparer.equal(message, rhs.message);
    }

    @Override
    public String toString()
    {
        return JSON.object(json ->
        {
            json.stringProperty("sourceFilePath", sourceFilePath);
            json.numberProperty("lineNumber", lineNumber);
            json.numberProperty("columnNumber", columnNumber);
            json.stringProperty("type", type.toString());
            json.stringProperty("message", message);
        }).toString();
    }
}
