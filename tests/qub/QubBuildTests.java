package qub;

public interface QubBuildTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubBuild.class, () ->
        {
            runner.testGroup("main(String[])", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubBuild.main((String[])null),
                        new PreConditionFailure("args cannot be null."));
                });
            });

            runner.testGroup("run(DesktopProcess)", () ->
            {
                runner.test("with null process", (Test test) ->
                {
                    test.assertThrows(() -> QubBuild.run(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with foo -?", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("foo", "-?"))
                    {
                        QubBuild.run(process);

                        test.assertEqual(
                            Iterable.create(
                                "Unrecognized action: \"foo\"",
                                "",
                                "Usage: qub-build [--action=]<action-name> [--help]",
                                "  Used to compile source code projects.",
                                "  --action(a): The name of the action to invoke.",
                                "  --help(?):   Show the help message for this application.",
                                "",
                                "Actions:",
                                "  compile (default): Compile source code files.",
                                "  logs:              Show the logs folder."),
                            Strings.getLines(process.getOutputWriteStream().getText().await()));
                    }
                });

                runner.test("with -?", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("-?"))
                    {
                        QubBuild.run(process);

                        test.assertEqual(
                            Iterable.create(
                                "Usage: qub-build [--action=]<action-name> [--help]",
                                "  Used to compile source code projects.",
                                "  --action(a): The name of the action to invoke.",
                                "  --help(?):   Show the help message for this application.",
                                "",
                                "Actions:",
                                "  compile (default): Compile source code files.",
                                "  logs:              Show the logs folder."),
                            Strings.getLines(process.getOutputWriteStream().getText().await()));
                    }
                });

                runner.test("with compile -?", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("compile", "-?"))
                    {
                        QubBuild.run(process);

                        test.assertEqual(
                            Iterable.create(
                                "Usage: qub-build compile [[--folder=]<folder-path-to-build>] [--warnings=<show|error|hide>] [--buildjson] [--verbose] [--profiler] [--help]",
                                "  Compile source code files.",
                                "  --folder:     The folder to build. The current folder will be used if this isn't defined.",
                                "  --warnings:   How to handle build warnings. Can be either \"show\", \"error\", or \"hide\". Defaults to \"show\".",
                                "  --buildjson:  Whether or not to read and write a build.json file. Defaults to true.",
                                "  --verbose(v): Whether or not to show verbose logs.",
                                "  --profiler:   Whether or not this application should pause before it is run to allow a profiler to be attached.",
                                "  --help(?):    Show the help message for this application."),
                            Strings.getLines(process.getOutputWriteStream().getText().await()));
                    }
                });
            });

            runner.testGroup("getActionFullName(String)", () ->
            {
                final Action2<String,Throwable> getActionFullNameErrorTest = (String actionName, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(actionName), (Test test) ->
                    {
                        test.assertThrows(() -> QubBuild.getActionFullName(actionName), expected);
                    });
                };

                getActionFullNameErrorTest.run(null, new PreConditionFailure("actionName cannot be null."));
                getActionFullNameErrorTest.run("", new PreConditionFailure("actionName cannot be empty."));

                final Action1<String> getActionFullNameTest = (String actionName) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(actionName), (Test test) ->
                    {
                        test.assertEqual("qub-build " + actionName, QubBuild.getActionFullName(actionName));
                    });
                };

                getActionFullNameTest.run("hello");
                getActionFullNameTest.run("compile");
                getActionFullNameTest.run("logs");
            });

            runner.testGroup("getLogsFolder(Folder)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubBuild.getLogsFolder(null),
                        new PreConditionFailure("qubBuildDataFolder cannot be null."));
                });

                runner.test("with non-null", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder folder = fileSystem.getFolder("/parent/folder/").await();
                    final Folder logsFolder = QubBuild.getLogsFolder(folder);
                    test.assertNotNull(logsFolder);
                    test.assertEqual("/parent/folder/logs/", logsFolder.toString());
                    test.assertFalse(logsFolder.exists().await());
                });
            });

            runner.testGroup("getJavaSourceFiles(Folder,ProjectJSONJava)", () ->
            {
                runner.test("with null projectFolder", (Test test) ->
                {
                    final Folder projectFolder = null;
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create();
                    test.assertThrows(() -> QubBuild.getJavaSourceFiles(projectFolder, projectJsonJava),
                        new PreConditionFailure("projectFolder cannot be null."));
                });

                runner.test("with null projectJsonJava", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.getFolder("/project/folder/").await();
                    final ProjectJSONJava projectJsonJava = null;
                    test.assertThrows(() -> QubBuild.getJavaSourceFiles(projectFolder, projectJsonJava),
                        new PreConditionFailure("projectJsonJava cannot be null."));
                });

                runner.test("with non-existing projectFolder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.getFolder("/project/folder/").await();
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create();
                    test.assertThrows(() -> QubBuild.getJavaSourceFiles(projectFolder, projectJsonJava).await(),
                        new FolderNotFoundException(projectFolder));
                });

                runner.test("with empty projectFolder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.createFolder("/project/folder/").await();
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create();
                    final Iterable<File> javaSourceFiles = QubBuild.getJavaSourceFiles(projectFolder, projectJsonJava).await();
                    test.assertEqual(Iterable.create(), javaSourceFiles);
                });

                runner.test("with Java file at root of projectFolder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.createFolder("/project/folder/").await();
                    final File javaFile = projectFolder.createFile("A.java").await();
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create();
                    final Iterable<File> javaSourceFiles = QubBuild.getJavaSourceFiles(projectFolder, projectJsonJava).await();
                    test.assertEqual(Iterable.create(javaFile), javaSourceFiles);
                });

                runner.test("with Java file in subfolder of projectFolder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.createFolder("/project/folder/").await();
                    final File javaFile = projectFolder.createFile("subfolder/A.java").await();
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create();
                    final Iterable<File> javaSourceFiles = QubBuild.getJavaSourceFiles(projectFolder, projectJsonJava).await();
                    test.assertEqual(Iterable.create(javaFile), javaSourceFiles);
                });

                runner.test("with Java file that doesn't match source pattern", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.createFolder("/project/folder/").await();
                    final File javaFile = projectFolder.createFile("subfolder/B.java").await();
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create()
                        .setSourceFiles(PathPattern.parse("**/A*.java"));
                    final Iterable<File> javaSourceFiles = QubBuild.getJavaSourceFiles(projectFolder, projectJsonJava).await();
                    test.assertEqual(Iterable.create(), javaSourceFiles);
                });

                runner.test("with Java file that matches source pattern", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.createFolder("/project/folder/").await();
                    final File javaFile = projectFolder.createFile("subfolder/A.java").await();
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create()
                        .setSourceFiles(PathPattern.parse("**/A*.java"));
                    final Iterable<File> javaSourceFiles = QubBuild.getJavaSourceFiles(projectFolder, projectJsonJava).await();
                    test.assertEqual(Iterable.create(javaFile), javaSourceFiles);
                });
            });

            runner.testGroup("getJavaOutputsFolder(Folder,ProjectJSONJava)", () ->
            {
                runner.test("with null projectFolder", (Test test) ->
                {
                    final Folder projectFolder = null;
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create();
                    test.assertThrows(() -> QubBuild.getJavaOutputsFolder(projectFolder, projectJsonJava),
                        new PreConditionFailure("projectFolder cannot be null."));
                });

                runner.test("with null projectJsonJava", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.getFolder("/project/folder/").await();
                    final ProjectJSONJava projectJsonJava = null;
                    test.assertThrows(() -> QubBuild.getJavaOutputsFolder(projectFolder, projectJsonJava),
                        new PreConditionFailure("projectJsonJava cannot be null."));
                });

                runner.test("with non-existing projectFolder and no outputs folder specified", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.getFolder("/project/folder/").await();
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create();
                    final Folder outputsFolder = QubBuild.getJavaOutputsFolder(projectFolder, projectJsonJava).await();
                    test.assertEqual("/project/folder/outputs/", outputsFolder.toString());
                });

                runner.test("with no outputs folder specified", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.createFolder("/project/folder/").await();
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create();
                    final Folder outputsFolder = QubBuild.getJavaOutputsFolder(projectFolder, projectJsonJava).await();
                    test.assertEqual("/project/folder/outputs/", outputsFolder.toString());
                });

                runner.test("with outputs folder specified", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder projectFolder = fileSystem.createFolder("/project/folder/").await();
                    final ProjectJSONJava projectJsonJava = ProjectJSONJava.create()
                        .setOutputFolder("otherOutputs");
                    final Folder outputsFolder = QubBuild.getJavaOutputsFolder(projectFolder, projectJsonJava).await();
                    test.assertEqual("/project/folder/otherOutputs/", outputsFolder.toString());
                });
            });

            runner.testGroup("getJavaClassFiles(Folder)", () ->
            {
                runner.test("with null outputsFolder", (Test test) ->
                {
                    final Folder outputsFolder = null;
                    test.assertThrows(() -> QubBuild.getJavaClassFiles(outputsFolder),
                        new PreConditionFailure("outputsFolder cannot be null."));
                });

                runner.test("with non-existing outputsFolder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder outputsFolder = fileSystem.getFolder("/project/folder/outputs/").await();
                    test.assertThrows(() -> QubBuild.getJavaClassFiles(outputsFolder).await(),
                        new FolderNotFoundException(outputsFolder));
                });

                runner.test("with empty outputsFolder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder outputsFolder = fileSystem.createFolder("/project/folder/outputs/").await();
                    test.assertEqual(Iterable.create(), QubBuild.getJavaClassFiles(outputsFolder).await());
                });

                runner.test("with non-empty outputsFolder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder outputsFolder = fileSystem.createFolder("/project/folder/outputs/").await();
                    final File aClassFile = outputsFolder.createFile("A.class").await();
                    test.assertEqual(Iterable.create(aClassFile), QubBuild.getJavaClassFiles(outputsFolder).await());
                });
            });
        });
    }
}
