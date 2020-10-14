package qub;

public class BuildJSON
{
    private static final String projectJsonPropertyName = "project.json";

    private final JSONObject json;

    private BuildJSON(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        this.json = json;
    }

    public static BuildJSON create()
    {
        return new BuildJSON(JSONObject.create());
    }

    public static Result<BuildJSON> parse(File parseJSONFile)
    {
        PreCondition.assertNotNull(parseJSONFile, "parseJSONFile");

        return Result.create(() ->
        {
            return BuildJSON.parse(JSON.parseObject(parseJSONFile).await()).await();
        });
    }

    public static Result<BuildJSON> parse(ByteReadStream readStream)
    {
        PreCondition.assertNotNull(readStream, "readStream");
        PreCondition.assertNotDisposed(readStream, "readStream");

        return Result.create(() ->
        {
            return BuildJSON.parse(JSON.parseObject(readStream).await()).await();
        });
    }

    public static Result<BuildJSON> parse(CharacterReadStream readStream)
    {
        PreCondition.assertNotNull(readStream, "readStream");
        PreCondition.assertNotDisposed(readStream, "readStream");

        return Result.create(() ->
        {
            return BuildJSON.parse(JSON.parseObject(readStream).await()).await();
        });
    }

    public static Result<BuildJSON> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "character");

        return Result.create(() ->
        {
            return BuildJSON.parse(JSON.parseObject(characters).await()).await();
        });
    }

    public static Result<BuildJSON> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create(() ->
        {
            return new BuildJSON(json);
        });
    }

    public BuildJSON setProjectJson(ProjectJSON projectJson)
    {
        this.json.setObjectOrNull(BuildJSON.projectJsonPropertyName, projectJson == null ? null : projectJson.toJson());
        return this;
    }

    public ProjectJSON getProjectJson()
    {
        final JSONObject projectJson = this.json.getObject(BuildJSON.projectJsonPropertyName)
            .catchError()
            .await();
        return projectJson == null ? null : ProjectJSON.create(projectJson);
    }

    public BuildJSON setSourceFiles(Iterable<BuildJSONSourceFile> sourceFiles)
    {
        PreCondition.assertNotNull(sourceFiles, "sourceFiles");

        final Iterable<String> propertyNames = this.json.getPropertyNames()
            .where((String propertyName) -> !propertyName.equalsIgnoreCase("project.json"))
            .toList();
        for (final String propertyName : propertyNames)
        {
            this.json.remove(propertyName);
        }
        if (!Iterable.isNullOrEmpty(sourceFiles))
        {
            for (final BuildJSONSourceFile sourceFile : sourceFiles)
            {
                this.json.set(sourceFile.toJsonProperty());
            }
        }
        return this;
    }

    public Iterable<BuildJSONSourceFile> getSourceFiles()
    {
        return json.getProperties()
            .where(property -> !property.getName().equals(BuildJSON.projectJsonPropertyName))
            .map((JSONProperty property) -> BuildJSONSourceFile.parse(property).await())
            .toList();
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
            BuildJSONSourceFile result = null;
            final Iterable<BuildJSONSourceFile> sourceFiles = this.getSourceFiles();
            if (!Iterable.isNullOrEmpty(sourceFiles))
            {
                result = sourceFiles.first((BuildJSONSourceFile sourceFile) -> sourceFile.getRelativePath().equals(relativePath));
            }
            if (result == null)
            {
                throw new NotFoundException("No source file found in the BuildJSON object with the path " + Strings.escapeAndQuote(relativePath.toString()) + ".");
            }
            return result;
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
        return this.toString(JSONFormat.consise);
    }

    public String toString(JSONFormat format)
    {
        PreCondition.assertNotNull(format, "format");

        return this.toJson().toString(format);
    }
}
