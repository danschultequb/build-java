package qub;

public interface QubBuild
{
    String applicationName = "qub-build";
    String applicationDescription = "Used to compile source code projects.";

    static void main(String[] args)
    {
        QubProcess.run(args, QubBuild::run);
    }

    static void run(QubProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        final CommandLineActions<QubProcess> actions = process.<QubProcess>createCommandLineActions()
            .setApplicationName(QubBuild.applicationName)
            .setApplicationDescription(QubBuild.applicationDescription);

        actions.addAction(QubBuildCompile.actionName, QubBuildCompile::getParameters, QubBuildCompile::run)
            .setDescription(QubBuildCompile.actionDescription)
            .setDefaultAction();

        CommandLineLogsAction.add(actions);

        actions.run(process);
    }

    static String getActionFullName(String actionName)
    {
        PreCondition.assertNotNullAndNotEmpty(actionName, "actionName");

        return QubBuild.applicationName + " " + actionName;
    }

    static Folder getLogsFolder(Folder qubBuildDataFolder)
    {
        PreCondition.assertNotNull(qubBuildDataFolder, "qubBuildDataFolder");

        return qubBuildDataFolder.getFolder("logs").await();
    }

    /**
     * Get all of the Java source files in the provided projectFolder that match the conditions
     * specified in the provided ProjectJSONJava configuration object.
     * @param projectFolder The root folder of the project.
     * @param projectJsonJava The ProjectJSON Java configuration object.
     * @return The matching Java source files.
     */
    static Result<Iterable<File>> getJavaSourceFiles(Folder projectFolder, ProjectJSONJava projectJsonJava)
    {
        PreCondition.assertNotNull(projectFolder, "projectFolder");
        PreCondition.assertNotNull(projectJsonJava, "projectJsonJava");

        return Result.create(() ->
        {
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

            return projectFolder.getFilesRecursively().await()
                .where(sourceFileMatcher)
                .toList();
        });
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
    static Result<Iterable<File>> getJavaClassFiles(Folder outputsFolder)
    {
        PreCondition.assertNotNull(outputsFolder, "outputsFolder");

        return Result.create(() ->
        {
            return outputsFolder.getFilesRecursively().await()
                .where((File file) -> ".class".equalsIgnoreCase(file.getFileExtension()))
                .toList();
        });
    }

    static Result<File> getJavaSourceFile(Folder outputsFolder, File classFile, Iterable<File> sourceFiles)
    {
        PreCondition.assertNotNull(outputsFolder, "outputsFolder");
        PreCondition.assertNotNull(classFile, "classFile");
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");

        return Result.create(() ->
        {
            final Indexable<String> classFileSegments = classFile.getPath().getSegments();

        });
    }
}