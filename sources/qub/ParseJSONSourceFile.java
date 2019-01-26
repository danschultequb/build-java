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
     * Create a new Iterable of ParseJSONSourceFile based on the provided sourceFiles.
     * @param sourceFiles The source files to create ParseJSONSourceFile objects from.
     * @param rootFolder The folder that the ParseJSONSourceFile objects are being created relative
     *                   to.
     * @return The created ParseJSONSourceFile objects.
     */
    public static Iterable<ParseJSONSourceFile> create(Iterable<File> sourceFiles, Folder rootFolder)
    {
        PreCondition.assertNotNull(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(rootFolder, "rootFolder");

        final Iterable<ParseJSONSourceFile> result = sourceFiles
            .map((File sourceFile) -> ParseJSONSourceFile.create(sourceFile, rootFolder, sourceFiles));

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Create a new ParseJSONSourceFile based on the provided sourceFile.
     * @param sourceFile The source file to create a ParseJSONSourceFile from.
     * @param rootFolder The folder that the ParseJSONSourceFile is being created relative to.
     * @param sourceFiles The source files that the sourceFile may depend on.
     * @return The created ParseJSONSourceFile from the provided source file.
     */
    public static ParseJSONSourceFile create(File sourceFile, Folder rootFolder, Iterable<File> sourceFiles)
    {
        PreCondition.assertNotNull(sourceFile, "sourceFile");
        PreCondition.assertNotNull(rootFolder, "rootFolder");

        final ParseJSONSourceFile result = new ParseJSONSourceFile();
        result.setRelativePath(sourceFile.relativeTo(rootFolder));
        result.setLastModified(sourceFile.getLastModified().throwErrorOrGetValue());
        try (final CharacterReadStream contentCharacterReadStream = sourceFile.getContentByteReadStream().throwErrorOrGetValue().asCharacterReadStream())
        {
            final String contents = contentCharacterReadStream.readEntireString().throwErrorOrGetValue();
            result.setDependencies(sourceFiles
                .where((File otherSourceFile) ->
                    otherSourceFile != sourceFile &&
                        contents.contains(otherSourceFile.getNameWithoutFileExtension()))
                .map((File sourceFileDependency) -> sourceFileDependency.relativeTo(rootFolder)));
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Write this ParseJSONSourceFile in its JSON representation to the provided JSONObjectBuilder.
     * @param builder The JSONObjectBuilder to write this ParseJSONSourceFile to in its JSON
     *                representation.
     */
    public void writeJson(JSONObjectBuilder builder)
    {
        PreCondition.assertNotNull(builder, "builder");

        builder.objectProperty(getRelativePath().toString(), (JSONObjectBuilder parseJsonSourceFileBuilder) ->
        {
            parseJsonSourceFileBuilder.numberProperty("lastModified", getLastModified().getMillisecondsSinceEpoch());
            parseJsonSourceFileBuilder.stringArrayProperty("dependencies", getDependencies().map(Path::toString));
        });
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
