package qub;

/**
 * A dependency within a project.json file.
 */
public class Dependency
{
    private final static String publisherPropertyName = "publisher";
    private final static String projectPropertyName = "project";
    private final static String versionPropertyName = "version";

    private String publisher;
    private String project;
    private String version;

    /**
     * Get the publisher of this dependency.
     * @return The publisher of this dependency.
     */
    public String getPublisher()
    {
        return publisher;
    }

    /**
     * Set the publisher of this dependency package.
     * @param publisher The publisher of this dependency.
     */
    public Dependency setPublisher(String publisher)
    {
        this.publisher = publisher;

        return this;
    }

    /**
     * Get the name of this dependency package.
     * @return The name of this dependency package.
     */
    public String getProject()
    {
        return project;
    }

    /**
     * Set the name of this dependency package.
     * @param project The name of this dependency package.
     */
    public Dependency setProject(String project)
    {
        this.project = project;

        return this;
    }

    /**
     * Get the range of versions that are allowed for this dependency.
     * @return The range of versions that are allowed for this dependency.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Set the range of versions that are allowed for this dependency.
     * @param version The range of versions that are allowed for this dependency.
     */
    public Dependency setVersion(String version)
    {
        this.version = version;

        return this;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof Dependency && equals((Dependency)rhs);
    }

    public boolean equals(Dependency rhs)
    {
        return rhs != null &&
            Comparer.equal(publisher, rhs.publisher) &&
            Comparer.equal(project, rhs.project) &&
            Comparer.equal(version, rhs.version);
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
    }

    public static Dependency parse(JSONObject dependencyObject)
    {
        final Dependency dependency = new Dependency();
        dependencyObject.getStringPropertyValue(publisherPropertyName)
            .then(dependency::setPublisher)
            .catchError()
            .await();
        dependencyObject.getStringPropertyValue(projectPropertyName)
            .then(dependency::setProject)
            .catchError()
            .await();
        dependencyObject.getStringPropertyValue(versionPropertyName)
            .then(dependency::setVersion)
            .catchError()
            .await();
        return dependency;
    }
}
