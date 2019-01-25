package qub;

/**
 * An individual source file referenced within a parse.json file.
 */
public class ParseJSONSourceFile
{
    private Path relativePath;
    private DateTime lastModified;
    private Iterable<Path> dependencies;

    /**
     * Get the path to the source file from the project root folder.
     * @return The path to the source file from the project root folder.
     */
    public Path getRelativePath()
    {
        return relativePath;
    }

    /**
     * Set the path to the source file from the project root folder.
     * @param relativePath The path to the source file from the project root folder.
     */
    public void setRelativePath(Path relativePath)
    {
        this.relativePath = relativePath;
    }

    /**
     * Get the last time the source file was modified.
     * @return The last time the source file was modified.
     */
    public DateTime getLastModified()
    {
        return lastModified;
    }

    /**
     * Set the last time the source file was modified.
     * @param lastModified The last time the source file was modified.
     */
    public void setLastModified(DateTime lastModified)
    {
        this.lastModified = lastModified;
    }

    /**
     * Get the relative paths to the source files that this source file depends on.
     * @return The relative paths to the source files that this source file depends on.
     */
    public Iterable<Path> getDependencies()
    {
        return dependencies;
    }

    /**
     * Set the relative paths to the source files that this source file depends on.
     * @param dependencies The relative paths to the source files that this source file depends on.
     */
    public void setDependencies(Iterable<Path> dependencies)
    {
        this.dependencies = dependencies;
    }

    /**
     * Parse a ParseJSONSourceFile from the provided JSONProperty.
     * @param sourceFileProperty The JSONProperty to parse a JSONSourceFile from.
     * @return The parsed ParseJSONSourceFile.
     */
    public static ParseJSONSourceFile parse(JSONProperty sourceFileProperty)
    {
        PreCondition.assertNotNull(sourceFileProperty, "sourceFileProperty");

        final ParseJSONSourceFile result = new ParseJSONSourceFile();

        result.setRelativePath(Path.parse(sourceFileProperty.getName()));
        sourceFileProperty.getObjectValue()
            .then((JSONObject sourceFileObject) ->
            {
                sourceFileObject.getNumberPropertyValue("lastModified")
                    .then((Double lastModifiedMillisecondsSinceEpoch) ->
                    {
                        result.setLastModified(DateTime.local(lastModifiedMillisecondsSinceEpoch.longValue()));
                    });
                sourceFileObject.getArrayPropertyValue("dependencies")
                    .then((JSONArray dependenciesArray) ->
                    {
                        result.setDependencies(dependenciesArray.getElements()
                            .instanceOf(JSONQuotedString.class)
                            .map(JSONQuotedString::toUnquotedString)
                            .map(Path::parse));
                    });
            });

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
