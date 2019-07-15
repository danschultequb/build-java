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

    public Result<BuildJSONSourceFile> getSourceFile(Path path)
    {
        PreCondition.assertNotNull(path, "path");

        return pathMap == null
            ? Result.error(new NotFoundException("No source file found in the BuildJSON object with the path " + Strings.escapeAndQuote(path.toString()) + "."))
            : pathMap.get(path)
                .catchErrorResult(NotFoundException.class, () ->
                    Result.error(new NotFoundException("No source file found in the BuildJSON object with the path " + Strings.escapeAndQuote(path.toString()) + ".")));
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
                JSON.object(writeStream, (JSONObjectBuilder parseJsonBuilder) ->
                {
                    final ProjectJSON projectJson = getProjectJson();
                    if (projectJson != null)
                    {
                        parseJsonBuilder.objectProperty("project.json", projectJson::write);
                    }

                    for (final BuildJSONSourceFile parseJSONSourceFile : getSourceFiles())
                    {
                        parseJSONSourceFile.writeJson(parseJsonBuilder);
                    }
                });
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
