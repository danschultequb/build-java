package qub;

public class ProjectJSON
{
    private String publisher;
    private String project;
    private String version;
    private String mainClass;
    private String shortcutName;
    private String javaSourcesVersion;
    private String javaTestsVersion;
    private Iterable<Dependency> javaDependencies;

    /**
     * Get the publisher.
     * @return The publisher.
     */
    public String getPublisher()
    {
        return publisher;
    }

    /**
     * Set the publisher.
     * @param publisher The publisher.
     */
    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    /**
     * Get the project name.
     * @return The name of the project.
     */
    public String getProject()
    {
        return project;
    }

    /**
     * Set the project name.
     * @param project The name of the project.
     */
    public void setProject(String project)
    {
        this.project = project;
    }

    /**
     * Get the version.
     * @return The version.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Set the version.
     * @param version The version.
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

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
     * Get the Java version to use when building the source files.
     * @return The Java version to use when building the source files.
     */
    public String getJavaSourcesVersion()
    {
        return javaSourcesVersion;
    }

    /**
     * Set the Java version to use when building the source files.
     * @param javaSourcesVersion The Java version to use when building the source files.
     */
    public void setJavaSourcesVersion(String javaSourcesVersion)
    {
        this.javaSourcesVersion = javaSourcesVersion;
    }

    /**
     * Set the Java version to use when building the source files.
     * @param javaSourcesVersion The Java version to use when building the source files.
     */
    public void setJavaSourcesVersion(Double javaSourcesVersion)
    {
        PreCondition.assertNotNull(javaSourcesVersion, "javaSourcesVersion");

        setJavaSourcesVersion(javaSourcesVersion.toString());
    }

    /**
     * Get the Java version to use when building the test files.
     * @return The Java version to use when building the test files.
     */
    public String getJavaTestsVersion()
    {
        return javaTestsVersion;
    }

    /**
     * Set the Java version to use when building the test files.
     * @param javaTestsVersion The Java version to use when building the test files.
     */
    public void setJavaTestsVersion(String javaTestsVersion)
    {
        this.javaTestsVersion = javaTestsVersion;
    }

    /**
     * Set the Java version to use when building the test files.
     * @param javaTestsVersion The Java version to use when building the test files.
     */
    public void setJavaTestsVersion(Double javaTestsVersion)
    {
        PreCondition.assertNotNull(javaTestsVersion, "javaTestsVersion");

        setJavaTestsVersion(javaTestsVersion.toString());
    }

    /**
     * Get the Java dependencies for this project.
     * @return The Java dependencies for this project.
     */
    public Iterable<Dependency> getJavaDependencies()
    {
        return javaDependencies;
    }

    /**
     * Set the Java dependencies for this project.
     * @param javaDependencies The Java dependencies for this project.
     */
    public void setJavaDependencies(Iterable<Dependency> javaDependencies)
    {
        this.javaDependencies = javaDependencies;
    }

    /**
     * Parse a ProjectJSON object from the provided project.json file.
     * @param projectJSONFile The file to parse.
     * @return The parsed ProjectJSON object.
     */
    public static Result<ProjectJSON> parse(File projectJSONFile)
    {
        PreCondition.assertNotNull(projectJSONFile, "projectJSONFile");

        return projectJSONFile.getContentByteReadStream()
            .then((ByteReadStream byteReadStream) -> byteReadStream.asCharacterReadStream())
            .then((CharacterReadStream characterReadStream) -> JSON.parse(characterReadStream))
            .thenResult((JSONDocument jsonDocument) ->
            {
                return jsonDocument.getRootObject()
                    .catchErrorResult(() -> { return Result.error(new java.text.ParseException("The root of a project.json file must be a JSON object.", 0)); });
            })
            .then((JSONObject root) ->
            {
                ProjectJSON projectJson = new ProjectJSON();

                root.getUnquotedStringPropertyValue("publisher").then(projectJson::setPublisher);
                root.getUnquotedStringPropertyValue("project").then(projectJson::setProject);
                root.getUnquotedStringPropertyValue("version").then(projectJson::setVersion);

                root.getObjectPropertyValue("java").then((JSONObject java) ->
                {
                    java.getUnquotedStringPropertyValue("mainClass").then(projectJson::setMainClass);
                    java.getUnquotedStringPropertyValue("shortcutName").then(projectJson::setShortcutName);

                    java.getObjectPropertyValue("sources").then((JSONObject sources) ->
                    {
                        sources.getUnquotedStringPropertyValue("version")
                            .then((Action1<String>)projectJson::setJavaSourcesVersion)
                            .catchError(WrongTypeException.class, () ->
                            {
                                sources.getNumberPropertyValue("version")
                                    .then((Action1<Double>)projectJson::setJavaSourcesVersion);
                            });
                    });

                    java.getObjectPropertyValue("tests").then((JSONObject tests) ->
                    {
                        tests.getUnquotedStringPropertyValue("version")
                            .then((Action1<String>)projectJson::setJavaTestsVersion)
                            .catchError(WrongTypeException.class, () ->
                            {
                                tests.getNumberPropertyValue("version")
                                    .then((Action1<Double>)projectJson::setJavaTestsVersion);
                            });
                    });

                    java.getArrayPropertyValue("dependencies").then((JSONArray dependenciesArray) ->
                    {
                        projectJson.setJavaDependencies(dependenciesArray.getElements()
                            .instanceOf(JSONObject.class)
                            .map((JSONObject dependencyObject) ->
                            {
                                final Dependency dependency = new Dependency();
                                dependencyObject.getUnquotedStringPropertyValue("publisher").then(dependency::setPublisher);
                                dependencyObject.getUnquotedStringPropertyValue("project").then(dependency::setProject);
                                dependencyObject.getUnquotedStringPropertyValue("version").then(dependency::setVersionRange);
                                return dependency;
                            }));
                    });
                });

                return projectJson;
            });
    }
}
