package qub;

public class ProjectJSONJava
{
    private String mainClass;
    private String shortcutName;
    private String version;
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
    public void setMainClass(String mainClass)
    {
        this.mainClass = mainClass;
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
    public void setShortcutName(String shortcutName)
    {
        this.shortcutName = shortcutName;
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
    public void setVersion(String version)
    {
        this.version = version;
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
    public void setDependencies(Iterable<Dependency> dependencies)
    {
        this.dependencies = dependencies;
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
