package qub;

public interface QubBuildLogsTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubBuildLogs.class, () ->
        {
            runner.testGroup("run(QubProcess)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubBuildLogs.run(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with non-existing qub folder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();

                    try (final QubProcess process = QubBuildLogsTests.createProcess(fileSystem, output))
                    {
                        QubBuildLogs.run(process);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "The logs folder (C:/qub/qub/test-java/data/logs/) doesn't exist."),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with non-existing qub publisher folder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();

                    try (final QubProcess process = QubBuildLogsTests.createProcess(fileSystem, output))
                    {
                        final QubProjectVersionFolder projectVersionFolder = process.getQubProjectVersionFolder().await();
                        final QubFolder qubFolder = projectVersionFolder.getQubFolder().await();
                        qubFolder.create().await();

                        QubBuildLogs.run(process);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "The logs folder (C:/qub/qub/test-java/data/logs/) doesn't exist."),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with non-existing qub project folder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();

                    try (final QubProcess process = QubBuildLogsTests.createProcess(fileSystem, output))
                    {
                        final QubProjectVersionFolder projectVersionFolder = process.getQubProjectVersionFolder().await();
                        final QubPublisherFolder publisherFolder = projectVersionFolder.getPublisherFolder().await();
                        publisherFolder.create().await();

                        QubBuildLogs.run(process);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "The logs folder (C:/qub/qub/test-java/data/logs/) doesn't exist."),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with non-existing qub project versions folder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();

                    try (final QubProcess process = QubBuildLogsTests.createProcess(fileSystem, output))
                    {
                        final QubProjectVersionFolder projectVersionFolder = process.getQubProjectVersionFolder().await();
                        final QubProjectFolder projectFolder = projectVersionFolder.getProjectFolder().await();
                        projectFolder.create().await();

                        QubBuildLogs.run(process);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "The logs folder (C:/qub/qub/test-java/data/logs/) doesn't exist."),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with non-existing qub project data folder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();

                    try (final QubProcess process = QubBuildLogsTests.createProcess(fileSystem, output))
                    {
                        final QubProjectVersionFolder projectVersionFolder = process.getQubProjectVersionFolder().await();
                        final Folder projectDataFolder = projectVersionFolder.getProjectDataFolder().await();
                        final Folder projectVersionsFolder = projectDataFolder.getParentFolder().await();
                        projectVersionsFolder.create().await();

                        QubBuildLogs.run(process);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "The logs folder (C:/qub/qub/test-java/data/logs/) doesn't exist."),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with non-existing qub project logs folder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();

                    try (final QubProcess process = QubBuildLogsTests.createProcess(fileSystem, output))
                    {
                        final QubProjectVersionFolder projectVersionFolder = process.getQubProjectVersionFolder().await();
                        final Folder projectDataFolder = projectVersionFolder.getProjectDataFolder().await();
                        projectDataFolder.create().await();

                        QubBuildLogs.run(process);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "The logs folder (C:/qub/qub/test-java/data/logs/) doesn't exist."),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with existing qub project logs folder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();

                    try (final QubProcess process = QubBuildLogsTests.createProcess(fileSystem, output))
                    {
                        final QubProjectVersionFolder projectVersionFolder = process.getQubProjectVersionFolder().await();
                        final Folder projectDataFolder = projectVersionFolder.getProjectDataFolder().await();
                        projectDataFolder.create().await();

                        QubBuildLogs.run(process);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "The logs folder (C:/qub/qub/test-java/data/logs/) doesn't exist."),
                        Strings.getLines(output.getText().await()));
                });
            });
        });
    }

    static QubProcess createProcess(InMemoryFileSystem fileSystem, InMemoryCharacterToByteStream output)
    {
        PreCondition.assertNotNull(fileSystem, "fileSystem");
        PreCondition.assertNotNull(output, "output");

        final QubProcess process = QubProcess.create();

        final QubProjectVersionFolder projectVersionFolder = process.getQubProjectVersionFolder().await();
        final QubFolder qubFolder = projectVersionFolder.getQubFolder().await();

        fileSystem.createRoot(qubFolder.getPath().getRoot().await()).await();
        process.setFileSystem(fileSystem);

        final EnvironmentVariables environmentVariables = new EnvironmentVariables()
            .set("QUB_HOME", qubFolder.toString());
        process.setEnvironmentVariables(environmentVariables);

        process.setOutputWriteStream(output);

        return process;
    }
}
