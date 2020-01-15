package qub;

public class BuildJSON
{
    private static final String projectJsonPropertyName = "project.json";

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

        return Result.create(() ->
        {
            if (this.pathMap == null)
            {
                throw new NotFoundException("No source file found in the BuildJSON object with the path " + Strings.escapeAndQuote(relativePath.toString()) + ".");
            }
            return this.pathMap.get(relativePath)
                .convertError(NotFoundException.class, () -> new NotFoundException("No source file found in the BuildJSON object with the path " + Strings.escapeAndQuote(relativePath.toString()) + "."))
                .await();
        });
    }

    public JSONObject toJson()
    {
        final JSONObject result = JSONObject.create();

        final ProjectJSON projectJson = this.getProjectJson();
        if (projectJson != null)
        {
            result.set(BuildJSON.projectJsonPropertyName, projectJson.toJson());
        }

        result.setAll(this.getSourceFiles().map(BuildJSONSourceFile::toJsonProperty));

        PostCondition.assertNotNull(result, "result");

        return result;
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

        return Result.create(() ->
        {
            BuildJSON result;
            try (final ByteReadStream byteReadStream = parseJSONFile.getContentByteReadStream().await())
            {
                result = BuildJSON.parse(byteReadStream).await();
            }
            return result;
        });
    }

    public static Result<BuildJSON> parse(ByteReadStream readStream)
    {
        PreCondition.assertNotNull(readStream, "readStream");
        PreCondition.assertFalse(readStream.isDisposed(), "readStream.isDisposed()");

        return BuildJSON.parse(readStream.asCharacterReadStream());
    }

    public static Result<BuildJSON> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "character");

        return JSON.parseObject(characters)
            .then((JSONObject object) -> BuildJSON.parse(object).await());
    }

    public static Result<BuildJSON> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create(() ->
        {
            final JSONObject projectJsonObject = json.getObject(BuildJSON.projectJsonPropertyName)
                .catchError()
                .await();
            final ProjectJSON projectJson = projectJsonObject == null ? null : ProjectJSON.parse(projectJsonObject).await();
            final Iterable<BuildJSONSourceFile> buildJSONSourceFiles = json.getProperties()
                .where(property -> !property.getName().equals(BuildJSON.projectJsonPropertyName))
                .map((JSONObjectProperty property) -> BuildJSONSourceFile.parse(property).await())
                .toList();
            return new BuildJSON()
                .setProjectJson(projectJson)
                .setSourceFiles(buildJSONSourceFiles);
        });
    }
}
