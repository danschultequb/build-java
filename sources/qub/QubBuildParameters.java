package qub;

public class QubBuildParameters
{
    private final CharacterWriteStream output;
    private final Folder folderToBuild;
    private final EnvironmentVariables environmentVariables;
    private final ProcessFactory processFactory;
    private Warnings warnings;
    private boolean useBuildJson;
    private VerboseCharacterWriteStream verbose;
    private JavaCompiler javaCompiler;

    public QubBuildParameters(CharacterWriteStream output, Folder folderToBuild, EnvironmentVariables environmentVariables, ProcessFactory processFactory)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(folderToBuild, "folderToBuild");
        PreCondition.assertNotNull(environmentVariables, "environmentVariables");
        PreCondition.assertNotNull(processFactory, "processFactory");

        this.output = output;
        this.folderToBuild = folderToBuild;
        this.environmentVariables = environmentVariables;
        this.processFactory = processFactory;

        this.warnings = Warnings.Show;
        this.useBuildJson = true;
        this.verbose = new VerboseCharacterWriteStream(false, output);
        this.javaCompiler = new JavacJavaCompiler(processFactory);
    }

    public CharacterWriteStream getOutput()
    {
        return this.output;
    }

    public Folder getFolderToBuild()
    {
        return this.folderToBuild;
    }

    public EnvironmentVariables getEnvironmentVariables()
    {
        return this.environmentVariables;
    }

    public ProcessFactory getProcessFactory()
    {
        return this.processFactory;
    }

    public JavaCompiler getJavaCompiler()
    {
        return this.javaCompiler;
    }

    public QubBuildParameters setWarnings(Warnings warnings)
    {
        PreCondition.assertNotNull(warnings, "warnings");

        this.warnings = warnings;

        return this;
    }

    public Warnings getWarnings()
    {
        return this.warnings;
    }

    public QubBuildParameters setUseBuildJson(boolean useBuildJson)
    {
        this.useBuildJson = useBuildJson;

        return this;
    }

    public boolean getUseBuildJson()
    {
        return this.useBuildJson;
    }

    public QubBuildParameters setVerbose(VerboseCharacterWriteStream verbose)
    {
        PreCondition.assertNotNull(verbose, "verbose");

        this.verbose = verbose;

        return this;
    }

    public VerboseCharacterWriteStream getVerbose()
    {
        return this.verbose;
    }
}
