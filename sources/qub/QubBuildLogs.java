package qub;

public interface QubBuildLogs
{
    String actionName = "logs";
    String actionDescription = "Show the logs folder.";

    static void run(QubProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        final Folder qubBuildDataFolder = process.getQubProjectDataFolder().await();
        final Folder qubBuildLogsFolder = QubBuild.getLogsFolder(qubBuildDataFolder);
        if (!qubBuildLogsFolder.exists().await())
        {
            process.getOutputWriteStream().writeLine("The logs folder (" + qubBuildLogsFolder + ") doesn't exist.").await();
        }
        else
        {
            final Path qubBuildLogsFolderPath = qubBuildLogsFolder.getPath();
            final DefaultApplicationLauncher applicationLauncher = process.getDefaultApplicationLauncher();
            applicationLauncher.openFileWithDefaultApplication(qubBuildLogsFolderPath).await();
        }
    }
}
