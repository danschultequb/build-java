package qub;

/**
 * The signature of a published project.
 */
public class ProjectSignature
{
    private final static String publisherPropertyName = "publisher";
    private final static String projectPropertyName = "project";
    private final static String versionPropertyName = "version";

    private final String publisher;
    private final String project;
    private final String version;

    /**
     * Create a new QubProjectSignature object.
     * @param publisher The publisher of the project.
     * @param project The name of the project.
     * @param version The version of the published project.
     */
    public ProjectSignature(String publisher, String project, String version)
    {
        PreCondition.assertNotNullAndNotEmpty(publisher, "publisher");
        PreCondition.assertNotNullAndNotEmpty(project, "project");
        PreCondition.assertNotNullAndNotEmpty(version, "version");

        this.publisher = publisher;
        this.project = project;
        this.version = version;
    }

    /**
     * Get the publisher of the project.
     * @return The publisher of the project.
     */
    public String getPublisher()
    {
        return this.publisher;
    }

    /**
     * Get the name of the project.
     * @return The name of the project.
     */
    public String getProject()
    {
        return this.project;
    }

    /**
     * Get the version of the published project.
     * @return The version of the published project.
     */
    public String getVersion()
    {
        return this.version;
    }

    @Override
    public String toString()
    {
        return this.toStringIgnoreVersion() + "@" + this.version;
    }

    public String toStringIgnoreVersion()
    {
        return this.publisher + "/" + this.project;
    }

    public JSONObject toJson()
    {
        return JSON.object(this::toJson);
    }

    public void toJson(JSONObjectBuilder json)
    {
        PreCondition.assertNotNull(json, "json");

        json.stringProperty(ProjectSignature.publisherPropertyName, this.publisher);
        json.stringProperty(ProjectSignature.projectPropertyName, this.project);
        json.stringProperty(ProjectSignature.versionPropertyName, this.version);
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof ProjectSignature && this.equals((ProjectSignature)rhs);
    }

    /**
     * Get whether or not this project signature is equal to the provided project signature.
     * @param rhs The project signature to compare to this project signature.
     * @return Whether or not this project signature is equal to the provided project signature.
     */
    public boolean equals(ProjectSignature rhs)
    {
        return this.equalsIgnoreVersion(rhs) &&
            Comparer.equal(this.version, rhs.version);
    }

    /**
     * Get whether or not this project signature is equal to the provided project signature,
     * ignoring both signatures' versions.
     * @param rhs The project signature to compare to this project signature.
     * @return Whether or not this project signature is equal to the provided project signature,
     * ignoring both signatures' versions.
     */
    public boolean equalsIgnoreVersion(ProjectSignature rhs)
    {
        return rhs != null &&
            Comparer.equal(this.publisher, rhs.publisher) &&
            Comparer.equal(this.project, rhs.project);
    }

    /**
     * Parse a QubProjectSignature object from the provided text.
     * @param text The text to parse into a QubProjectSignature.
     * @return The parsed QubProjectSignature object.
     */
    public static Result<ProjectSignature> parse(String text)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");

        return Result.create(() ->
        {
            final int slashIndex = text.indexOf('/');
            if (slashIndex == 0)
            {
                throw new ParseException("No publisher found in " + Strings.escapeAndQuote(text) + ".");
            }

            if (slashIndex == -1 || slashIndex == text.length() - 1)
            {
                throw new ParseException("No project found in " + Strings.escapeAndQuote(text) + ".");
            }

            final int atIndex = text.indexOf('@', slashIndex + 1);
            if (atIndex == slashIndex + 1)
            {
                throw new ParseException("No project found in " + Strings.escapeAndQuote(text) + ".");
            }

            if (atIndex == -1 || atIndex == text.length() - 1)
            {
                throw new ParseException("No version found in " + Strings.escapeAndQuote(text) + ".");
            }

            final String publisher = text.substring(0, slashIndex);
            final String project = text.substring(slashIndex + 1, atIndex);
            final String version = text.substring(atIndex + 1);
            return new ProjectSignature(publisher, project, version);
        });
    }

    public static Result<ProjectSignature> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create(() ->
        {
            final String publisher = json.getStringPropertyValue(ProjectSignature.publisherPropertyName).await();
            final String project = json.getStringPropertyValue(ProjectSignature.projectPropertyName).await();
            final String version = json.getStringPropertyValue(ProjectSignature.versionPropertyName).await();
            return new ProjectSignature(publisher, project, version);
        });
    }
}
