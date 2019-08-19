package qub;

public class ProjectJSONJava
{
    public final static String mainClassPropertyName = "mainClass";
    public final static String shortcutNamePropertyName = "shortcutName";
    public final static String captureVMArgumentsPropertyName = "captureVMArguments";
    public final static String versionPropertyName = "version";
    public final static String outputFolderPropertyName = "outputFolder";
    public final static String sourceFilesPropertyName = "sourceFiles";
    public final static String maximumErrorsPropertyName = "maximumErrors";
    public final static String maximumWarningsPropertyName = "maximumWarnings";
    public final static String dependenciesPropertyName = "dependencies";

    private String mainClass;
    private String shortcutName;
    private Boolean captureVMArguments;
    private String version;
    private String outputFolder;
    private Integer maximumErrors;
    private Integer maximumWarnings;
    private Iterable<PathPattern> sourceFilePatterns;
    private Iterable<Dependency> dependencies;

    /**
     * Get the main class.
     * @return The main class.
     */
    public String getMainClass()
    {
        return mainClass;
    }

    /**
     * Set the main class.
     * @param mainClass The main class.
     */
    public ProjectJSONJava setMainClass(String mainClass)
    {
        this.mainClass = mainClass;
        return this;
    }

    /**
     * Get the shortcut name.
     * @return The shortcut name.
     */
    public String getShortcutName()
    {
        return shortcutName;
    }

    /**
     * Set the shortcut name.
     * @param shortcutName The shortcut name.
     */
    public ProjectJSONJava setShortcutName(String shortcutName)
    {
        this.shortcutName = shortcutName;
        return this;
    }

    /**
     * Get whether or not the Java VM arguments should be exposed to the application.
     * @return Whether or not the Java VM arguments should be exposed to the application.
     */
    public Boolean getCaptureVMArguments()
    {
        return captureVMArguments;
    }

    /**
     * Set whether or not the Java VM arguments should be exposed to the application.
     * @param captureVMArguments Whether or not the Java VM arguments should be exposed to the
     *                           application.
     * @return This object for method chaining.
     */
    public ProjectJSONJava setCaptureVMArguments(Boolean captureVMArguments)
    {
        this.captureVMArguments = captureVMArguments;
        return this;
    }

    /**
     * Get the Java version to use when building source files.
     * @return The Java version to use when building source files.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Set the Java version to use when building source files.
     * @param version The Java version to use when building source files.
     */
    public ProjectJSONJava setVersion(String version)
    {
        this.version = version;
        return this;
    }

    /**
     * Get the name of the outputFolder.
     * @return The name of the outputFolder.
     */
    public String getOutputFolder()
    {
        return outputFolder;
    }

    /**
     * Set the name of the outputFolder.
     * @param outputFolder The name of the outputFolder.
     */
    public ProjectJSONJava setOutputFolder(String outputFolder)
    {
        this.outputFolder = outputFolder;
        return this;
    }

    public Iterable<PathPattern> getSourceFilePatterns()
    {
        return sourceFilePatterns;
    }

    /**
     * Set the maximum number of errors that the compiler should report before halting compilation.
     * If nothing is specified (maximumErrors is null), then the default (100) will be used.
     * @param maximumErrors The maximum number of errors that the compiler should report before
     *                      halting compilation.
     * @return This object for method chaining.
     */
    public ProjectJSONJava setMaximumErrors(Integer maximumErrors)
    {
        this.maximumErrors = maximumErrors;
        return this;
    }

    /**
     * Get the maximum number of errors that the compiler should report before halting compilation.
     * If nothing is specified (maximumErrors is null), then the default (100) will be used.
     * @return The maximum number of errors that the compiler should report before halting
     * compilation.
     */
    public Integer getMaximumErrors()
    {
        return maximumErrors;
    }

    /**
     * Set the maximum number of warnings that the compiler should report. If nothing is specified
     * (maximumWarnings is null), then the default (100) will be used.
     * @param maximumWarnings The maximum number of warnings that the compiler should report.
     * @return This object for method chaining.
     */
    public ProjectJSONJava setMaximumWarnings(Integer maximumWarnings)
    {
        this.maximumWarnings = maximumWarnings;
        return this;
    }

    /**
     * Get the maximum number of warnings that the compiler should report. If nothing is specified
     * (maximumWarnings is null), then the default (100) will be used.
     * @return The maximum number of warnings that the compiler should report.
     */
    public Integer getMaximumWarnings()
    {
        return maximumWarnings;
    }

    public ProjectJSONJava setSourceFilePatterns(Iterable<PathPattern> sourceFilePatterns)
    {
        this.sourceFilePatterns = sourceFilePatterns;
        return this;
    }

    /**
     * Get the dependencies for this project.
     * @return The dependencies for this project.
     */
    public Iterable<Dependency> getDependencies()
    {
        return dependencies;
    }

    /**
     * Set the dependencies for this project.
     * @param dependencies The dependencies for this project.
     */
    public ProjectJSONJava setDependencies(Iterable<Dependency> dependencies)
    {
        this.dependencies = dependencies;
        return this;
    }

    public void write(JSONObjectBuilder builder)
    {
        PreCondition.assertNotNull(builder, "builder");

        if (!Strings.isNullOrEmpty(mainClass))
        {
            builder.stringProperty(mainClassPropertyName, mainClass);
        }
        if (!Strings.isNullOrEmpty(shortcutName))
        {
            builder.stringProperty(shortcutNamePropertyName, shortcutName);
        }
        if (captureVMArguments != null)
        {
            builder.booleanProperty(captureVMArgumentsPropertyName, captureVMArguments);
        }
        if (!Strings.isNullOrEmpty(version))
        {
            builder.stringProperty(versionPropertyName, version);
        }
        if (!Strings.isNullOrEmpty(outputFolder))
        {
            builder.stringProperty(outputFolderPropertyName, outputFolder);
        }
        if (!Iterable.isNullOrEmpty(sourceFilePatterns))
        {
            builder.stringArrayProperty(sourceFilesPropertyName, sourceFilePatterns.map(PathPattern::toString));
        }
        if (maximumErrors != null)
        {
            builder.numberProperty(maximumErrorsPropertyName, maximumErrors);
        }
        if (maximumWarnings != null)
        {
            builder.numberProperty(maximumWarningsPropertyName, maximumWarnings);
        }
        if (!Iterable.isNullOrEmpty(dependencies))
        {
            builder.arrayProperty(dependenciesPropertyName, dependenciesBuilder ->
            {
                for (final Dependency dependency : dependencies)
                {
                    dependenciesBuilder.objectElement(dependency::write);
                }
            });
        }
    }

    public static ProjectJSONJava parse(JSONObject javaObject)
    {
        PreCondition.assertNotNull(javaObject, "javaObject");

        final ProjectJSONJava result = new ProjectJSONJava();
        javaObject.getUnquotedStringPropertyValue(mainClassPropertyName)
            .then(result::setMainClass)
            .catchError()
            .await();
        javaObject.getUnquotedStringPropertyValue(shortcutNamePropertyName)
            .then(result::setShortcutName)
            .catchError()
            .await();
        javaObject.getUnquotedStringPropertyValue(versionPropertyName)
            .catchErrorResult(WrongTypeException.class, () -> javaObject.getNumberPropertyValue("version").then(Object::toString))
            .then(result::setVersion)
            .catchError()
            .await();
        javaObject.getUnquotedStringPropertyValue(outputFolderPropertyName)
            .then(result::setOutputFolder)
            .catchError()
            .await();
        javaObject.getUnquotedStringPropertyValue(sourceFilesPropertyName)
            .then((String sourceFilesPattern) ->
            {
                if (!Strings.isNullOrEmpty(sourceFilesPattern))
                {
                    result.setSourceFilePatterns(Iterable.create(PathPattern.parse(sourceFilesPattern)));
                }
            })
            .catchError(WrongTypeException.class, () ->
            {
                javaObject.getArrayPropertyValue(sourceFilesPropertyName)
                    .then((JSONArray sourceFilesArray) ->
                    {
                        result.setSourceFilePatterns(sourceFilesArray.getElements()
                            .instanceOf(JSONQuotedString.class)
                            .map(JSONQuotedString::toUnquotedString)
                            .where(value -> !Strings.isNullOrEmpty(value))
                            .map(PathPattern::parse));
                    });
            })
            .catchError()
            .await();
        javaObject.getNumberPropertyValue(maximumErrorsPropertyName)
            .then((Double maximumErrors) -> result.setMaximumErrors(maximumErrors.intValue()))
            .catchError()
            .await();
        javaObject.getNumberPropertyValue(maximumWarningsPropertyName)
            .then((Double maximumWarnings) -> result.setMaximumWarnings(maximumWarnings.intValue()))
            .catchError()
            .await();
        javaObject.getArrayPropertyValue(dependenciesPropertyName)
            .then((JSONArray dependenciesArray) ->
            {
                result.setDependencies(dependenciesArray.getElements()
                    .instanceOf(JSONObject.class)
                    .map(Dependency::parse));
            })
            .catchError()
            .await();

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
