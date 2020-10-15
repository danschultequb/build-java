package qub;

public class BuildJSON
{
    private static final String projectJsonPropertyName = "project.json";
    private static final String javacVersionPropertyName = "javacVersion";
    private static final String sourceFilesPropertyName = "sourceFiles";

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

    public BuildJSON setJavacVersion(String javacVersion)
    {
        PreCondition.assertNotNullAndNotEmpty(javacVersion, "javacVersion");

        this.json.setString(BuildJSON.javacVersionPropertyName, javacVersion);
        return this;
    }

    public BuildJSON setJavacVersion(VersionNumber javacVersion)
    {
        PreCondition.assertNotNull(javacVersion, "javacVersion");

        return this.setJavacVersion(javacVersion.toString());
    }

    public VersionNumber getJavacVersion()
    {
        return this.json.getString(BuildJSON.javacVersionPropertyName)
            .then((String javacVersionString) -> VersionNumber.parse(javacVersionString).await())
            .catchError()
            .await();
    }

    public BuildJSON setSourceFiles(Iterable<BuildJSONSourceFile> sourceFiles)
    {
        PreCondition.assertNotNull(sourceFiles, "sourceFiles");

        final JSONObject sourceFilesJson = JSONObject.create();
        if (!Iterable.isNullOrEmpty(sourceFiles))
        {
            for (final BuildJSONSourceFile sourceFile : sourceFiles)
            {
                sourceFilesJson.set(sourceFile.toJsonProperty());
            }
        }
        this.json.setObject(BuildJSON.sourceFilesPropertyName, sourceFilesJson);

        return this;
    }

    public Iterable<BuildJSONSourceFile> getSourceFiles()
    {
        final JSONObject sourceFilesJson = json.getObject(BuildJSON.sourceFilesPropertyName)
            .catchError(() -> JSONObject.create())
            .await();
        return sourceFilesJson.getProperties()
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
    public Result<BuildJSONSourceFile> getSourceFile(String relativePath)
    {
        PreCondition.assertNotNullAndNotEmpty(relativePath, "relativePath");

        return this.getSourceFile(Path.parse(relativePath));
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
        PreCondition.assertFalse(relativePath.isRooted(), "relativePath.isRooted()");

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
        return this.json;
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
