package qub;

public class ParseJSON
{
    private ProjectJSON projectJson;
    private Iterable<ParseJSONSourceFile> sourceFiles;
    private Map<Path,ParseJSONSourceFile> pathMap;

    public ParseJSON setProjectJson(ProjectJSON projectJson)
    {
        this.projectJson = projectJson;
        return this;
    }

    public ProjectJSON getProjectJson()
    {
        return projectJson;
    }

    public ParseJSON setSourceFiles(Iterable<ParseJSONSourceFile> sourceFiles)
    {
        this.sourceFiles = sourceFiles;
        if (sourceFiles == null)
        {
            this.pathMap = null;
        }
        else
        {
            final MutableMap<Path,ParseJSONSourceFile> pathMap = Map.create();
            for (final ParseJSONSourceFile source : sourceFiles)
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

    public Iterable<ParseJSONSourceFile> getSourceFiles()
    {
        return sourceFiles;
    }

    public Result<ParseJSONSourceFile> getSourceFile(Path path)
    {
        PreCondition.assertNotNull(path, "path");

        return pathMap == null
            ? Result.error(new NotFoundException("No source file found in the ParseJSON object with the path " + Strings.escapeAndQuote(path.toString()) + "."))
            : pathMap.get(path)
                .catchErrorResult(NotFoundException.class, () ->
                    Result.error(new NotFoundException("No source file found in the ParseJSON object with the path " + Strings.escapeAndQuote(path.toString()) + ".")));
    }

    /**
     * Write this ParseJSON object to the provided file.
     * @param file The file to write this ParseJSON object to.
     * @return The result of writing this ParseJSON object.
     */
    public Result<Void> write(File file)
    {
        PreCondition.assertNotNull(file, "file");

        return Result.create(() ->
        {
            try (final CharacterWriteStream writeStream = file.getContentCharacterWriteStream().await())
            {
                JSON.object(writeStream, (JSONObjectBuilder parseJsonBuilder) ->
                {
                    final ProjectJSON projectJson = getProjectJson();
                    if (projectJson != null)
                    {
                        parseJsonBuilder.objectProperty("project.json", projectJson::write);
                    }

                    for (final ParseJSONSourceFile parseJSONSourceFile : getSourceFiles())
                    {
                        parseJSONSourceFile.writeJson(parseJsonBuilder);
                    }
                });
            }
        });
    }

    public static Result<ParseJSON> parse(File parseJSONFile)
    {
        PreCondition.assertNotNull(parseJSONFile, "parseJSONFile");

        return parseJSONFile.getContentByteReadStream()
            .thenResult(ParseJSON::parse);
    }

    public static Result<ParseJSON> parse(ByteReadStream readStream)
    {
        PreCondition.assertNotNull(readStream, "readStream");
        PreCondition.assertFalse(readStream.isDisposed(), "readStream.isDisposed()");

        return parse(readStream.asCharacterReadStream());
    }

    public static Result<ParseJSON> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "character");

        return JSON.parse(characters).getRootObject().then((JSONObject rootObject) -> parse(rootObject));
    }

    public static ParseJSON parse(JSONObject rootObject)
    {
        PreCondition.assertNotNull(rootObject, "rootObject");

        final ParseJSON result = new ParseJSON();
        rootObject.getObjectPropertyValue("project.json")
            .then((JSONObject jsonObject) -> ProjectJSON.parse(jsonObject))
            .then(result::setProjectJson);
        result.setSourceFiles(rootObject.getProperties()
            .where(property -> !property.getName().equals("project.json"))
            .map(ParseJSONSourceFile::parse));

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
