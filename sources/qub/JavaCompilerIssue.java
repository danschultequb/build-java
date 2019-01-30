package qub;

public class JavaCompilerIssue
{
    public final String sourceFilePath;
    public final int lineNumber;
    public final int columnNumber;
    public final Issue.Type type;
    public final String message;

    public JavaCompilerIssue(String sourceFilePath, int lineNumber, int columnNumber, Issue.Type type, String message)
    {
        this.sourceFilePath = sourceFilePath;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.type = type;
        this.message = message;
    }
}
