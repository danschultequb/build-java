package qub;

/**
 * An individual source file referenced within a build.json file.
 */
public class BuildJSONSourceFile
{
    private static final String lastModifiedPropertyName = "lastModified";
    private static final String dependenciesPropertyName = "dependencies";
    private static final String issuesPropertyName = "issues";

    private final JSONProperty jsonProperty;

    private BuildJSONSourceFile(JSONProperty jsonProperty)
    {
        PreCondition.assertNotNull(jsonProperty, "jsonProperty");
        PreCondition.assertInstanceOf(jsonProperty.getValue(), JSONObject.class, "jsonProperty.getValue()");

        this.jsonProperty = jsonProperty;
    }

    public static BuildJSONSourceFile create(String sourceFileRelativePath)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFileRelativePath, "sourceFileRelativePath");

        return BuildJSONSourceFile.create(Path.parse(sourceFileRelativePath));
    }

    public static BuildJSONSourceFile create(Path sourceFileRelativePath)
    {
        PreCondition.assertNotNull(sourceFileRelativePath, "sourceFileRelativePath");
        PreCondition.assertFalse(sourceFileRelativePath.isRooted(), "sourceFileRelativePath.isRooted()");

        final JSONProperty json = JSONProperty.create(sourceFileRelativePath.toString(), JSONObject.create());
        return new BuildJSONSourceFile(json);
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
            return new BuildJSONSourceFile(sourceFileProperty);
        });
    }

    /**
     * Get the path to the source file from the project root folder.
     * @return The path to the source file from the project root folder.
     */
    public Path getRelativePath()
    {
        return Path.parse(this.jsonProperty.getName());
    }

    private JSONObject getPropertyValue()
    {
        return this.jsonProperty.getObjectValue().await();
    }

    /**
     * Get the last time the source file was modified.
     * @return The last time the source file was modified.
     */
    public DateTime getLastModified()
    {
        final String lastModifiedString = this.getPropertyValue().getString(BuildJSONSourceFile.lastModifiedPropertyName)
            .catchError()
            .await();
        return Strings.isNullOrEmpty(lastModifiedString)
            ? null
            : DateTime.parse(lastModifiedString).catchError().await();
    }

    /**
     * Set the last time the source file was modified.
     * @param lastModified The last time the source file was modified.
     */
    public BuildJSONSourceFile setLastModified(DateTime lastModified)
    {
        PreCondition.assertNotNull(lastModified, "lastModified");

        this.getPropertyValue().setString(BuildJSONSourceFile.lastModifiedPropertyName, lastModified.toString());
        return this;
    }

    /**
     * Get the relative paths to the source files that this source file depends on.
     * @return The relative paths to the source files that this source file depends on.
     */
    public Iterable<Path> getDependencies()
    {
        final JSONArray dependenciesArray = this.getPropertyValue().getArray(BuildJSONSourceFile.dependenciesPropertyName)
            .catchError()
            .await();
        List<Path> result = null;
        if (dependenciesArray != null)
        {
            result = List.create();
            for (final JSONSegment dependencySegment : dependenciesArray)
            {
                final JSONString dependency = (JSONString)dependencySegment;
                final String dependencyPathString = dependency.getValue();
                final Path dependencyPath = Path.parse(dependencyPathString);
                result.add(dependencyPath);
            }
        }
        return result;
    }

    /**
     * Set the relative paths to the source files that this source file depends on.
     * @param dependencies The relative paths to the source files that this source file depends on.
     */
    public BuildJSONSourceFile setDependencies(Iterable<Path> dependencies)
    {
        PreCondition.assertNotNull(dependencies, "dependencies");

        this.getPropertyValue().setArray(BuildJSONSourceFile.dependenciesPropertyName,
            JSONArray.create(dependencies.map(Path::toString).map(JSONString::get)));
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

        final JSONObject propertyValue = this.getPropertyValue();
        JSONArray issuesArray = propertyValue.getArray(BuildJSONSourceFile.issuesPropertyName)
            .catchError()
            .await();
        if (issuesArray == null)
        {
            issuesArray = JSONArray.create();
            propertyValue.setArray(BuildJSONSourceFile.issuesPropertyName, issuesArray);
        }
        issuesArray.add(issue.toJson());

        return this;
    }

    public BuildJSONSourceFile setIssues(Iterable<JavaCompilerIssue> issues)
    {
        PreCondition.assertNotNull(issues, "issues");

        final JSONObject propertyValue = this.getPropertyValue();
        propertyValue.setArray(BuildJSONSourceFile.issuesPropertyName, JSONArray.create(issues.map(JavaCompilerIssue::toJson)));

        return this;
    }

    /**
     * Get the issues that have been added to the source file.
     * @return The issues that have been added to the source file.
     */
    public Iterable<JavaCompilerIssue> getIssues()
    {
        final JSONObject propertyValue = this.getPropertyValue();
        final JSONArray issuesArray = propertyValue.getArray(BuildJSONSourceFile.issuesPropertyName)
            .catchError()
            .await();
        final Iterable<JavaCompilerIssue> result = issuesArray == null
            ? Iterable.create()
            : issuesArray
                .instanceOf(JSONObject.class)
                .map((JSONObject issueJson) -> JavaCompilerIssue.parse(issueJson).await())
                .toList();

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof BuildJSONSourceFile && equals((BuildJSONSourceFile)rhs);
    }

    public boolean equals(BuildJSONSourceFile rhs)
    {
        return rhs != null &&
            Comparer.equal(this.getRelativePath().toString(), rhs.getRelativePath().toString()) &&
            Comparer.equal(this.getLastModified(), rhs.getLastModified()) &&
            Comparer.equal(this.getDependencies(), rhs.getDependencies()) &&
            Comparer.equal(this.getIssues(), rhs.getIssues());
    }

    @Override
    public String toString()
    {
        return this.toString(JSONFormat.consise);
    }

    public String toString(JSONFormat format)
    {
        PreCondition.assertNotNull(format, "format");

        return Strings.escapeAndQuote(this.getRelativePath()) + ":" + format.getAfterPropertySeparator() + this.toJson().toString(format);
    }

    /**
     * Get the JSON representation of this object.
     * @return The JSON representation of this object.
     */
    public JSONObject toJson()
    {
        final JSONObject result = JSONObject.create();

        final DateTime lastModified = this.getLastModified();
        if (lastModified != null)
        {
            result.setString(BuildJSONSourceFile.lastModifiedPropertyName, lastModified.toString());
        }

        final Iterable<Path> dependencies = this.getDependencies();
        if (!Iterable.isNullOrEmpty(dependencies))
        {
            result.set(BuildJSONSourceFile.dependenciesPropertyName, JSONArray.create(dependencies.map(Path::toString).map(JSONString::get)));
        }

        final Iterable<JavaCompilerIssue> issues = this.getIssues();
        if (!Iterable.isNullOrEmpty(issues))
        {
            result.set(BuildJSONSourceFile.issuesPropertyName, JSONArray.create(issues.map(JavaCompilerIssue::toJson)));
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    public JSONProperty toJsonProperty()
    {
        return this.jsonProperty;
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

        final Path sourceFileRelativePath = sourceFile.relativeTo(rootFolder);
        final BuildJSONSourceFile result = BuildJSONSourceFile.create(sourceFileRelativePath);
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
        if (sourceFileDependencyPaths.any())
        {
            result.setDependencies(sourceFileDependencyPaths);
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
