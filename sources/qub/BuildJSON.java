package qub;

public class BuildJSON
{
    private ProjectJSON projectJson;
    private Iterable<BuildJSONSourceFile> sourceFiles;
    private Map<Path,BuildJSONSourceFile> pathMap;

    public BuildJSON setProjectJson(ProjectJSON projectJson)
    {
        this.projectJson = projectJson;
        return this;
    }

    public ProjectJSON getProjectJson()
    {
        return projectJson;
    }

    public BuildJSON setSourceFiles(Iterable<BuildJSONSourceFile> sourceFiles)
    {
        this.sourceFiles = sourceFiles;
        if (sourceFiles == null)
        {
            this.pathMap = null;
        }
        else
        {
            final MutableMap<Path,BuildJSONSourceFile> pathMap = Map.create();
            for (final BuildJSONSourceFile source : sourceFiles)
            {
                final Path sourcePath = source.getRelativePath();
                if (sourcePath != null)
                {
                    pathMap.set(sourcePath, source);
                }
            }
            this.pathMap = pathMap;
        }
        return this;
    }

    public Iterable<BuildJSONSourceFile> getSourceFiles()
    {
        return sourceFiles;
    }

    /**
     * Get the BuildJSONSourceFile that matches the provided relative path. The path should be
     * relative to the project folder.
     * @param relativePath The path to the source file. This should be relative to the project
     *                     folder.
     * @return The BuildJSONSourceFile that is associated with the provided relative path.
     */
    public Result<BuildJSONSourceFile> getSourceFile(Path relativePath)
    {
        PreCondition.assertNotNull(relativePath, "relativePath");

        return pathMap == null
            ? Result.error(new NotFoundException("No source file found in the BuildJSON object with the path " + Strings.escapeAndQuote(relativePath.toString()) + "."))
            : pathMap.get(relativePath)
                .catchErrorResult(NotFoundException.class, () ->
                    Result.error(new NotFoundException("No source file found in the BuildJSON object with the path " + Strings.escapeAndQuote(relativePath.toString()) + ".")));
    }

    public JSONObject toJson()
    {
        return JSON.object((JSONObjectBuilder buildJsonBuilder) ->
        {
            final ProjectJSON projectJson = getProjectJson();
            if (projectJson != null)
            {
                buildJsonBuilder.objectProperty("project.json", projectJson::write);
            }

            for (final BuildJSONSourceFile buildJSONSourceFile : getSourceFiles())
            {
                buildJSONSourceFile.writeJson(buildJsonBuilder);
            }
        });
    }

    @Override
    public String toString()
    {
        return this.toJson().toString();
    }

    /**
     * Write this BuildJSON object to the provided file.
     * @param file The file to write this BuildJSON object to.
     * @return The result of writing this BuildJSON object.
     */
    public Result<Void> write(File file)
    {
        PreCondition.assertNotNull(file, "file");

        return Result.create(() ->
        {
            try (final CharacterWriteStream writeStream = new BufferedByteWriteStream(file.getContentByteWriteStream().await()).asCharacterWriteStream())
            {
                writeStream.write(this.toString()).await();
            }
        });
    }

    public static Result<BuildJSON> parse(File parseJSONFile)
    {
        PreCondition.assertNotNull(parseJSONFile, "parseJSONFile");

        return parseJSONFile.getContentByteReadStream()
            .thenResult(BuildJSON::parse);
    }

    public static Result<BuildJSON> parse(ByteReadStream readStream)
    {
        PreCondition.assertNotNull(readStream, "readStream");
        PreCondition.assertFalse(readStream.isDisposed(), "readStream.isDisposed()");

        return parse(readStream.asCharacterReadStream());
    }

    public static Result<BuildJSON> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "character");

        return JSON.parse(characters).getRootObject().then((JSONObject rootObject) -> parse(rootObject));
    }

    public static BuildJSON parse(JSONObject rootObject)
    {
        PreCondition.assertNotNull(rootObject, "rootObject");

        final BuildJSON result = new BuildJSON();
        rootObject.getObjectPropertyValue("project.json")
            .then((JSONObject jsonObject) -> ProjectJSON.parse(jsonObject))
            .then(result::setProjectJson);
        result.setSourceFiles(rootObject.getProperties()
            .where(property -> !property.getName().equals("project.json"))
            .map(BuildJSONSourceFile::parse));

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
