package qub;

public class ParseJSON
{
    private Iterable<ParseJSONSourceFile> sourceFiles;
    private Map<Path,ParseJSONSourceFile> pathMap;

    public void setSourceFiles(Iterable<ParseJSONSourceFile> sourceFiles)
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
        result.setSourceFiles(rootObject.getProperties().map(ParseJSONSourceFile::parse));

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
