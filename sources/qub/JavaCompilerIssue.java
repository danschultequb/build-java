package qub;

/**
 * An issue that occurred while compiling Java source code.
 */
public class JavaCompilerIssue
{
    private static final String sourceFilePathPropertyName = "sourceFilePath";
    private static final String lineNumberPropertyName = "lineNumber";
    private static final String columnNumberPropertyName = "columnNumber";
    private static final String typePropertyName = "type";
    private static final String messagePropertyName = "message";

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

    public JavaCompilerIssue(Path sourceFilePath, int lineNumber, int columnNumber, Issue.Type type, String message)
    {
        this(sourceFilePath.toString(), lineNumber, columnNumber, type, message);
    }

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
        return this.toJson().toString();
    }

    /**
     * Get the JSON representation of this JavaCompilerIssue.
     * @return The JSON representation of this JavaCompilerIssue.
     */
    public JSONObject toJson()
    {
        return JSONObject.create()
            .setString(JavaCompilerIssue.sourceFilePathPropertyName, sourceFilePath)
            .setNumber(JavaCompilerIssue.lineNumberPropertyName, lineNumber)
            .setNumber(JavaCompilerIssue.columnNumberPropertyName, columnNumber)
            .setString(JavaCompilerIssue.typePropertyName, type.toString())
            .setString(JavaCompilerIssue.messagePropertyName, message);
    }

    public static Result<JavaCompilerIssue> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create2(() ->
        {
            final String sourceFilePath = json.getString(JavaCompilerIssue.sourceFilePathPropertyName).await();
            final int lineNumber = json.getNumber(JavaCompilerIssue.lineNumberPropertyName).await().intValue();
            final int columnNumber = json.getNumber(JavaCompilerIssue.columnNumberPropertyName).await().intValue();
            final String typeString = json.getString(JavaCompilerIssue.typePropertyName).await();
            final Issue.Type type = Strings.isNullOrEmpty(typeString) ? null : Issue.Type.valueOf(typeString);
            final String message = json.getString(JavaCompilerIssue.messagePropertyName).await();
            return new JavaCompilerIssue(sourceFilePath, lineNumber, columnNumber, type, message);
        });
    }

    /**
     * Get a new JavaCompilerIssue error object.
     * @param sourceFilePath The path to the source file.
     * @param lineNumber The line number.
     * @param columnNumber The column number within the line.
     * @param errorMessage The message that describes the error.
     * @return The created issue.
     */
    public static JavaCompilerIssue error(String sourceFilePath, int lineNumber, int columnNumber, String errorMessage)
    {
        return new JavaCompilerIssue(sourceFilePath, lineNumber, columnNumber, Issue.Type.Error, errorMessage);
    }

    /**
     * Get a new JavaCompilerIssue warning object.
     * @param sourceFilePath The path to the source file.
     * @param lineNumber The line number.
     * @param columnNumber The column number within the line.
     * @param warningMessage The message that describes the warning.
     * @return The created issue.
     */
    public static JavaCompilerIssue warning(String sourceFilePath, int lineNumber, int columnNumber, String warningMessage)
    {
        return new JavaCompilerIssue(sourceFilePath, lineNumber, columnNumber, Issue.Type.Warning, warningMessage);
    }
}
