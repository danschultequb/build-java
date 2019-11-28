package qub;

/**
 * An individual source file referenced within a build.json file.
 */
public class BuildJSONSourceFile
{
    private static final String lastModifiedPropertyName = "lastModifiedNanoseconds";
    private static final String dependenciesPropertyName = "dependencies";
    private static final String issuesPropertyName = "issues";

    private Path relativePath;
    private DateTime lastModified;
    private Iterable<Path> dependencies;
    private List<JavaCompilerIssue> issues;

    public BuildJSONSourceFile()
    {
        this.issues = List.create();
    }

    /**
     * Get the path to the source file from the project root folder.
     * @return The path to the source file from the project root folder.
     */
    public Path getRelativePath()
    {
        return relativePath;
    }

    public BuildJSONSourceFile setRelativePath(String relativePath)
    {
        PreCondition.assertNotNullAndNotEmpty(relativePath, "relativePath");

        return this.setRelativePath(Path.parse(relativePath));
    }

    /**
     * Set the path to the source file from the project root folder.
     * @param relativePath The path to the source file from the project root folder.
     */
    public BuildJSONSourceFile setRelativePath(Path relativePath)
    {
        PreCondition.assertNotNull(relativePath, "relativePath");

        this.relativePath = relativePath;
        return this;
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
    public BuildJSONSourceFile setLastModified(DateTime lastModified)
    {
        this.lastModified = lastModified;
        return this;
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
    public BuildJSONSourceFile setDependencies(Iterable<Path> dependencies)
    {
        this.dependencies = dependencies;
        return this;
    }

    /**
     * Add the provided issue to the source file.
     * @param issue The issue to add to the source file.
     * @return This object for method chaining.
     */
    public BuildJSONSourceFile addIssue(JavaCompilerIssue issue)
    {
        PreCondition.assertNotNull(issue, "issue");

        this.issues.add(issue);
        return this;
    }

    public BuildJSONSourceFile setIssues(Iterable<JavaCompilerIssue> issues)
    {
        PreCondition.assertNotNull(issues, "issues");

        this.issues = List.create(issues);

        return this;
    }

    /**
     * Get the issues that have been added to the source file.
     * @return The issues that have been added to the source file.
     */
    public Iterable<JavaCompilerIssue> getIssues()
    {
        return this.issues;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof BuildJSONSourceFile && equals((BuildJSONSourceFile)rhs);
    }

    public boolean equals(BuildJSONSourceFile rhs)
    {
        return rhs != null &&
            Comparer.equal(relativePath, rhs.relativePath) &&
            Comparer.equal(lastModified, rhs.lastModified) &&
            Comparer.equal(dependencies, rhs.dependencies) &&
            Comparer.equal(issues, rhs.issues);
    }

    @Override
    public String toString()
    {
        return Strings.escapeAndQuote(this.relativePath) + ":" + this.toJson().toString();
    }

    /**
     * Get the JSON representation of this object.
     * @return The JSON representation of this object.
     */
    public JSONObject toJson()
    {
        return JSON.object(this::toJson);
    }

    /**
     * Write this BuildJSONSourceFile in its JSON representation to the provided JSONObjectBuilder.
     * @param buildJsonSourceFile The JSONObjectBuilder to write this BuildJSONSourceFile to in its
     *                            JSON representation.
     */
    public void toJson(JSONObjectBuilder buildJsonSourceFile)
    {
        PreCondition.assertNotNull(buildJsonSourceFile, "json");

        if (lastModified != null)
        {
            buildJsonSourceFile.numberProperty(BuildJSONSourceFile.lastModifiedPropertyName, (long)lastModified.getDurationSinceEpoch().toNanoseconds().getValue());
        }
        if (!Iterable.isNullOrEmpty(dependencies))
        {
            buildJsonSourceFile.stringArrayProperty(BuildJSONSourceFile.dependenciesPropertyName, dependencies.map(Path::toString));
        }
        if (!Iterable.isNullOrEmpty(issues))
        {
            buildJsonSourceFile.arrayProperty(BuildJSONSourceFile.issuesPropertyName, issuesJson ->
            {
                for (final JavaCompilerIssue issue : issues)
                {
                    issuesJson.objectElement(issueJson ->
                    {
                        issueJson.stringProperty("sourceFilePath", issue.sourceFilePath);
                        issueJson.numberProperty("lineNumber", issue.lineNumber);
                        issueJson.numberProperty("columnNumber", issue.columnNumber);
                        issueJson.stringProperty("type", issue.type.toString());
                        issueJson.stringProperty("message", issue.message);
                    });
                }
            });
        }
    }

    public void toJsonProperty(JSONObjectBuilder buildJson)
    {
        PreCondition.assertNotNull(buildJson, "buildJson");

        buildJson.objectProperty(this.getRelativePath().toString(), this::toJson);
    }

    /**
     * Parse a BuildJSONSourceFile from the provided JSONProperty.
     * @param sourceFileProperty The JSONProperty to parse a JSONSourceFile from.
     * @return The parsed BuildJSONSourceFile.
     */
    public static Result<BuildJSONSourceFile> parse(JSONProperty sourceFileProperty)
    {
        PreCondition.assertNotNull(sourceFileProperty, "sourceFileProperty");

        return Result.create(() ->
        {
            final Path relativePath = Path.parse(sourceFileProperty.getName());
            final JSONObject sourceFileObject = sourceFileProperty.getObjectValue().await();
            final Double lastModifiedNanoseconds = sourceFileObject.getNumberPropertyValue(BuildJSONSourceFile.lastModifiedPropertyName)
                .catchError()
                .await();
            final DateTime lastModified = lastModifiedNanoseconds == null ? null : DateTime.createFromDurationSinceEpoch(Duration.nanoseconds(lastModifiedNanoseconds));
            final JSONArray dependenciesArray = sourceFileObject.getArrayPropertyValue(BuildJSONSourceFile.dependenciesPropertyName)
                .catchError()
                .await();
            final Iterable<Path> dependencies = dependenciesArray == null
                ? Iterable.create()
                : dependenciesArray
                    .getElements()
                    .instanceOf(JSONQuotedString.class)
                    .map(JSONQuotedString::toUnquotedString)
                    .map(Path::parse)
                    .toList();
            final JSONArray issuesArray = sourceFileObject.getArrayPropertyValue(BuildJSONSourceFile.issuesPropertyName)
                .catchError()
                .await();
            final Iterable<JavaCompilerIssue> issues = issuesArray == null
                ? Iterable.create()
                : issuesArray
                    .getElements()
                    .instanceOf(JSONObject.class)
                    .map((JSONObject issueJson) -> JavaCompilerIssue.parse(issueJson).await())
                    .toList();
            return new BuildJSONSourceFile()
                .setRelativePath(relativePath)
                .setLastModified(lastModified)
                .setDependencies(dependencies)
                .setIssues(issues);
        });
    }

    /**
     * Create a new Iterable of BuildJSONSourceFile based on the provided sourceFiles.
     * @param sourceFiles The source files to create BuildJSONSourceFile objects from.
     * @param rootFolder The folder that the BuildJSONSourceFile objects are being created relative
     *                   to.
     * @return The created BuildJSONSourceFile objects.
     */
    public static Iterable<BuildJSONSourceFile> create(Iterable<File> sourceFiles, Folder rootFolder)
    {
        PreCondition.assertNotNull(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(rootFolder, "rootFolder");

        final Iterable<BuildJSONSourceFile> result = sourceFiles
            .map((File sourceFile) -> BuildJSONSourceFile.create(sourceFile, rootFolder, sourceFiles))
            .toList();

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Create a new BuildJSONSourceFile based on the provided sourceFile.
     * @param sourceFile The source file to create a BuildJSONSourceFile from.
     * @param rootFolder The folder that the BuildJSONSourceFile is being created relative to.
     * @param sourceFiles The source files that the sourceFile may depend on.
     * @return The created BuildJSONSourceFile from the provided source file.
     */
    public static BuildJSONSourceFile create(File sourceFile, Folder rootFolder, Iterable<File> sourceFiles)
    {
        PreCondition.assertNotNull(sourceFile, "sourceFile");
        PreCondition.assertNotNull(rootFolder, "rootFolder");

        final BuildJSONSourceFile result = new BuildJSONSourceFile();
        result.setRelativePath(sourceFile.relativeTo(rootFolder));
        result.setLastModified(sourceFile.getLastModified().await());

        final String sourceFileContents = sourceFile.getContentsAsString().await();
        final Iterable<String> sourceFileWords = Strings.getWords(sourceFileContents);

        final List<Path> sourceFileDependencyPaths = List.create();
        for (final File otherSourceFile : sourceFiles)
        {
            if (sourceFile != otherSourceFile)
            {
                final String otherSourceFileClassName = otherSourceFile.getNameWithoutFileExtension();
                if (sourceFileWords.contains(otherSourceFileClassName))
                {
                    final Path otherSourceFilePath = otherSourceFile.relativeTo(rootFolder);
                    sourceFileDependencyPaths.add(otherSourceFilePath);
                }
            }
        }
        result.setDependencies(sourceFileDependencyPaths);

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
