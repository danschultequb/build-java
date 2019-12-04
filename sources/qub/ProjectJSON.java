package qub;

public class ProjectJSON
{
    private static final String publisherPropertyName = "publisher";
    private static final String projectPropertyName = "project";
    private static final String versionPropertyName = "version";
    private static final String javaPropertyName = "java";

    private String publisher;
    private String project;
    private String version;
    private ProjectJSONJava java;

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
    public ProjectJSON setPublisher(String publisher)
    {
        this.publisher = publisher;
        return this;
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
    public ProjectJSON setProject(String project)
    {
        this.project = project;
        return this;
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
    public ProjectJSON setVersion(String version)
    {
        this.version = version;
        return this;
    }

    /**
     * Set the Java options.
     * @param java The Java options.
     */
    public ProjectJSON setJava(ProjectJSONJava java)
    {
        this.java = java;
        return this;
    }

    /**
     * Get the Java options.
     * @return The Java options.
     */
    public ProjectJSONJava getJava()
    {
        return java;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof ProjectJSON && this.equals((ProjectJSON)rhs);
    }

    public boolean equals(ProjectJSON rhs)
    {
        return rhs != null &&
            Comparer.equal(this.publisher, rhs.publisher) &&
            Comparer.equal(this.project, rhs.project) &&
            Comparer.equal(this.version, rhs.version) &&
            Comparer.equal(this.java, rhs.java);
    }

    @Override
    public String toString()
    {
        return JSON.object(this::write).toString();
    }

    public void write(JSONObjectBuilder builder)
    {
        PreCondition.assertNotNull(builder, "builder");

        if (!Strings.isNullOrEmpty(publisher))
        {
            builder.stringProperty(publisherPropertyName, publisher);
        }
        if (!Strings.isNullOrEmpty(project))
        {
            builder.stringProperty(projectPropertyName, project);
        }
        if (!Strings.isNullOrEmpty(version))
        {
            builder.stringProperty(versionPropertyName, version);
        }
        if (java != null)
        {
            builder.objectProperty(javaPropertyName, java::write);
        }
    }

    /**
     * Parse a ProjectJSON object from the provided project.json file.
     * @param projectJSONFile The file to parse.
     * @return The parsed ProjectJSON object.
     */
    public static Result<ProjectJSON> parse(File projectJSONFile)
    {
        PreCondition.assertNotNull(projectJSONFile, "projectJSONFile");

        return Result.create(() ->
        {
            ProjectJSON result;
            try (ByteReadStream readStream = projectJSONFile.getContentByteReadStream().await())
            {
                result = ProjectJSON.parse(readStream).await();
            }

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }

    /**
     * Parse a ProjectJSON object from the provided ByteReadStream.
     * @param readStream The ByteReadStream to parse.
     * @return The result of attempting to parse a ProjectJSON object.
     */
    public static Result<ProjectJSON> parse(ByteReadStream readStream)
    {
        PreCondition.assertNotNull(readStream, "readStream");
        PreCondition.assertNotDisposed(readStream, "readStream.isDisposed()");

        return parse(readStream.asCharacterReadStream());
    }

    /**
     * Parse a ProjectJSON object from the provided characters.
     * @param characters The characters to parse.
     * @return The result of attempting to parse a ProjectJSON object.
     */
    public static Result<ProjectJSON> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return JSON.parse(characters)
            .getRootObject()
            .then((JSONObject rootObject) -> ProjectJSON.parse(rootObject));
    }

    /**
     * Parse a ProjectJSON object from the provided JSONObject.
     * @param rootObject The rootObject to parse.
     * @return The result of attempting to parse a JSON object.
     */
    public static ProjectJSON parse(JSONObject rootObject)
    {
        PreCondition.assertNotNull(rootObject, "rootObject");

        ProjectJSON projectJson = new ProjectJSON();

        rootObject.getStringPropertyValue(publisherPropertyName)
            .then(projectJson::setPublisher)
            .catchError()
            .await();
        rootObject.getStringPropertyValue(projectPropertyName)
            .then(projectJson::setProject)
            .catchError()
            .await();
        rootObject.getStringPropertyValue(versionPropertyName)
            .then(projectJson::setVersion)
            .catchError()
            .await();
        rootObject.getObjectPropertyValue(javaPropertyName)
            .then((JSONObject javaObject) -> projectJson.setJava(ProjectJSONJava.parse(javaObject)))
            .catchError()
            .await();

        return projectJson;
    }
}
