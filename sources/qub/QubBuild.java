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

        actions.addAction(QubBuildLogs.actionName, QubBuildLogs::run)
            .setDescription(QubBuildLogs.actionDescription);

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
}