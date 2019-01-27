package qub;

public class ProjectJSONJava
{
    private String mainClass;
    private String shortcutName;
    private String version;
    private String outputFolder;
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
            builder.stringProperty("mainClass", mainClass);
        }
        if (!Strings.isNullOrEmpty(shortcutName))
        {
            builder.stringProperty("shortcutName", shortcutName);
        }
        if (!Strings.isNullOrEmpty(version))
        {
            builder.stringProperty("version", version);
        }
        if (!Strings.isNullOrEmpty(outputFolder))
        {
            builder.stringProperty("outputFolder", outputFolder);
        }
        if (!Iterable.isNullOrEmpty(dependencies))
        {
            builder.arrayProperty("dependencies", dependenciesBuilder ->
            {
                for (final Dependency dependency : dependencies)
                {
                    dependenciesBuilder.objectElement(dependencyBuilder ->
                    {
                        if (!Strings.isNullOrEmpty(dependency.getPublisher()))
                        {
                            dependencyBuilder.stringProperty("publisher", dependency.getPublisher());
                        }
                        if (!Strings.isNullOrEmpty(dependency.getProject()))
                        {
                            dependencyBuilder.stringProperty("project", dependency.getProject());
                        }
                        if (!Strings.isNullOrEmpty(dependency.getVersion()))
                        {
                            dependencyBuilder.stringProperty("version", dependency.getVersion());
                        }
                    });
                }
            });
        }
    }

    public static ProjectJSONJava parse(JSONObject javaObject)
    {
        PreCondition.assertNotNull(javaObject, "javaObject");

        final ProjectJSONJava result = new ProjectJSONJava();
        javaObject.getUnquotedStringPropertyValue("mainClass").then(result::setMainClass);
        javaObject.getUnquotedStringPropertyValue("shortcutName").then(result::setShortcutName);
        javaObject.getUnquotedStringPropertyValue("version")
            .catchErrorResult(WrongTypeException.class, () -> javaObject.getNumberPropertyValue("version").then(Object::toString))
            .then(result::setVersion);
        javaObject.getUnquotedStringPropertyValue("outputFolder").then(result::setOutputFolder);
        javaObject.getArrayPropertyValue("dependencies").then((JSONArray dependenciesArray) ->
        {
            result.setDependencies(dependenciesArray.getElements()
                .instanceOf(JSONObject.class)
                .map((JSONObject dependencyObject) ->
                {
                    final Dependency dependency = new Dependency();
                    dependencyObject.getUnquotedStringPropertyValue("publisher").then(dependency::setPublisher);
                    dependencyObject.getUnquotedStringPropertyValue("project").then(dependency::setProject);
                    dependencyObject.getUnquotedStringPropertyValue("version").then(dependency::setVersion);
                    return dependency;
                }));
        });

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
