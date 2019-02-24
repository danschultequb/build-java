package qub;

public abstract class JarCreator
{
    private Folder outputsFolder;
    private File manifestFile;
    private Iterable<File> classFiles;
    private Iterable<File> sourceFiles;
    private String jarName;

    public JarCreator setOutputsFolder(Folder outputsFolder)
    {
        PreCondition.assertNotNull(outputsFolder, "outputsFolder");

        this.outputsFolder = outputsFolder;

        return this;
    }

    public Folder getOutputsFolder()
    {
        final Folder result = outputsFolder;

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Set the manifest file that will be packaged in the jar file.
     * @param manifestFile The manifest file that will be packaged in the jar file.
     */
    public JarCreator setManifestFile(File manifestFile)
    {
        PreCondition.assertNotNull(manifestFile, "manifestFile");

        this.manifestFile = manifestFile;

        return this;
    }

    /**
     * Get the manifest file that will be packaged in the jar file, or null if no manifest file will be added.
     * @return The manifest file that will be packaged in the jar file, or null if no manifest file
     * will be added.
     */
    public File getManifestFile()
    {
        return manifestFile;
    }

    public JarCreator setClassFiles(Iterable<File> classFiles)
    {
        PreCondition.assertNotNull(classFiles, "classFiles");

        this.classFiles = classFiles;

        return this;
    }

    public Iterable<File> getClassFiles()
    {
        final Iterable<File> result = classFiles;

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    public JarCreator setSourceFiles(Iterable<File> sourceFiles)
    {
        PreCondition.assertNotNull(sourceFiles, "sourceFiles");

        this.sourceFiles = sourceFiles;

        return this;
    }

    public Iterable<File> getSourceFiles()
    {
        final Iterable<File> result = sourceFiles;

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Set the name of the jar file that will be created.
     * @param jarName The name of the jar file that will be created.
     */
    public JarCreator setJarName(String jarName)
    {
        PreCondition.assertNotNullAndNotEmpty(jarName, "jarName");

        this.jarName = jarName;

        return this;
    }

    /**
     * Get the name of the jar file that will be created.
     * @return The name of the jar file that will be created.
     */
    public String getJarName()
    {
        final String result = jarName;

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }

    public abstract Result<File> createJarFile(Console console, boolean isVerbose);
}
