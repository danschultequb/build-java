package qub;

public interface QubBuild
{
    static void main(String[] args)
    {
        DesktopProcess.run(args, QubBuild::run);
    }

    static void run(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        process.createCommandLineActions()
            .setApplicationName("qub-build")
            .setApplicationDescription("Used to compile source code projects.")
            .addAction(QubBuildCompile::addAction)
            .addAction(CommandLineLogsAction::addAction)
            .run();
    }

    /**
     * Get all of the Java source files in the provided projectFolder that match the conditions
     * specified in the provided ProjectJSONJava configuration object.
     * @param projectFolder The root folder of the project.
     * @param projectJsonJava The ProjectJSON Java configuration object.
     * @return The matching Java source files.
     */
    static Iterator<File> iterateJavaSourceFiles(Folder projectFolder, ProjectJSONJava projectJsonJava)
    {
        PreCondition.assertNotNull(projectFolder, "projectFolder");
        PreCondition.assertNotNull(projectJsonJava, "projectJsonJava");

        Function1<File,Boolean> sourceFileMatcher;
        final Iterable<PathPattern> sourceFilePatterns = projectJsonJava.getSourceFiles();
        if (!Iterable.isNullOrEmpty(sourceFilePatterns))
        {
            sourceFileMatcher = (File file) -> sourceFilePatterns.contains((PathPattern pattern) -> pattern.isMatch(file.getPath().relativeTo(projectFolder)));
        }
        else
        {
            sourceFileMatcher = (File file) -> ".java".equalsIgnoreCase(file.getFileExtension());
        }

        return projectFolder.iterateFilesRecursively()
            .where(sourceFileMatcher);
    }

    /**
     * Get the folder that compiled Java class files will be output to.
     * @param projectFolder The root folder of the project.
     * @param projectJsonJava The ProjectJSON Java configuration object.
     * @return The folder that compiled Java class files will be output to.
     */
    static Result<Folder> getJavaOutputsFolder(Folder projectFolder, ProjectJSONJava projectJsonJava)
    {
        PreCondition.assertNotNull(projectFolder, "projectFolder");
        PreCondition.assertNotNull(projectJsonJava, "projectJsonJava");

        return Result.create(() ->
        {
            String outputFolderName = projectJsonJava.getOutputFolder();
            if (Strings.isNullOrEmpty(outputFolderName))
            {
                outputFolderName = "outputs";
            }
            return projectFolder.getFolder(outputFolderName).await();
        });
    }

    /**
     * Get the existing class files in the provided outputs folder.
     * @param outputsFolder The outputsFolder where class files are written to.
     * @return The existing class files in the provided outputs folder.
     */
    static Iterator<File> iterateJavaClassFiles(Folder outputsFolder)
    {
        PreCondition.assertNotNull(outputsFolder, "outputsFolder");

        return outputsFolder.iterateFilesRecursively()
            .where((File file) -> ".class".equalsIgnoreCase(file.getFileExtension()));
    }
}