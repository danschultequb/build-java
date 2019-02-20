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
