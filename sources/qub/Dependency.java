package qub;

/**
 * A dependency within a project.json file.
 */
public class Dependency
{
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
        return JSON.object(object ->
        {
            object.stringProperty("publisher", Objects.toString(publisher));
            object.stringProperty("project", Objects.toString(project));
            object.stringProperty("version", Objects.toString(version));
        }).toString();
    }
}
