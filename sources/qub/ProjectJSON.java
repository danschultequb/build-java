package qub;

public class ProjectJSON
{
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
     * Set the Java options.
     * @param java The Java options.
     */
    public void setJava(ProjectJSONJava java)
    {
        this.java = java;
    }

    /**
     * Get the Java options.
     * @return The Java options.
     */
    public ProjectJSONJava getJava()
    {
        return java;
    }

    public void write(JSONObjectBuilder builder)
    {
        PreCondition.assertNotNull(builder, "builder");

        if (!Strings.isNullOrEmpty(publisher))
        {
            builder.stringProperty("publisher", publisher);
        }
        if (!Strings.isNullOrEmpty(project))
        {
            builder.stringProperty("project", project);
        }
        if (!Strings.isNullOrEmpty(version))
        {
            builder.stringProperty("version", version);
        }
        if (java != null)
        {
            builder.objectProperty("java", java::write);
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

        return projectJSONFile.getContentByteReadStream()
            .thenResult(ProjectJSON::parse);
    }

    /**
     * Parse a ProjectJSON object from the provided ByteReadStream.
     * @param readStream The ByteReadStream to parse.
     * @return The result of attempting to parse a ProjectJSON object.
     */
    public static Result<ProjectJSON> parse(ByteReadStream readStream)
    {
        PreCondition.assertNotNull(readStream, "readStream");
        PreCondition.assertFalse(readStream.isDisposed(), "readStream.isDisposed()");

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

        rootObject.getUnquotedStringPropertyValue("publisher").then(projectJson::setPublisher);
        rootObject.getUnquotedStringPropertyValue("project").then(projectJson::setProject);
        rootObject.getUnquotedStringPropertyValue("version").then(projectJson::setVersion);

        rootObject.getObjectPropertyValue("java")
            .then((JSONObject javaObject) -> ProjectJSONJava.parse(javaObject))
            .then(projectJson::setJava);

        return projectJson;
    }
}
