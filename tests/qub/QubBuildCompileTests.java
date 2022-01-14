package qub;

public interface QubBuildCompileTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubBuildCompile.class, () ->
        {
            runner.testGroup("getParameters(DesktopProcess)", () ->
            {
                runner.test("with null process", (Test test) ->
                {
                    final DesktopProcess process = null;
                    final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});
                    test.assertThrows(() -> QubBuildCompile.getParameters(process, action),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with null action", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create())
                    {
                        final CommandLineAction action = null;
                        test.assertThrows(() -> QubBuildCompile.getParameters(process, action),
                            new PreConditionFailure("action cannot be null."));
                    }
                });

                runner.test("with no arguments", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create())
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with positional folder to build argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("/folder/to/build/"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual("/folder/to/build/", parameters.getFolderToBuild().toString());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with named folder to build argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--folder=/folder/to/build/"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual("/folder/to/build/", parameters.getFolderToBuild().toString());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--warnings"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings= argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--warnings="))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings=Show argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--warnings=Show"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings=error argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--warnings=error"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Error, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings=HIDE argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--warnings=HIDE"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Hide, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings=spam argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--warnings=spam"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --buildjson argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--buildjson"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --buildjson= argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--buildjson="))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --buildjson=false argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--buildjson=false"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertFalse(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --buildjson=true argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--buildjson=true"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --verbose argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--verbose"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertTrue(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --verbose= argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--verbose="))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertTrue(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --verbose=false argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--verbose=false"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --verbose=true argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--verbose=true"))
                    {
                        process.getTypeLoader()
                            .addTypeContainer("fake.MainClassFullName", "/qub/fake/main-java/versions/7/main-java.jar");
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {});

                        final QubBuildCompileParameters parameters = QubBuildCompile.getParameters(process, action);
                        test.assertNotNull(parameters);
                        test.assertEqual(process.getCurrentFolder(), parameters.getFolderToBuild());
                        test.assertSame(process.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutputWriteStream());
                        test.assertNotNull(parameters.getProcessFactory());
                        test.assertTrue(parameters.getBuildJson());
                        test.assertTrue(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --help argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("--help"))
                    {
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {})
                            .setDescription("Compile source code files.");
                        test.assertNull(QubBuildCompile.getParameters(process, action));
                        test.assertEqual(
                            Iterable.create(
                                "Usage: fake-action-name [[--folder=]<folder-path-to-build>] [--warnings=<show|error|hide>] [--buildjson] [--verbose] [--profiler] [--help]",
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

                runner.test("with -? command line argument", (Test test) ->
                {
                    try (final FakeDesktopProcess process = FakeDesktopProcess.create("-?"))
                    {
                        final CommandLineAction action = CommandLineAction.create("fake-action-name", (DesktopProcess actionProcess) -> {})
                            .setDescription("Compile source code files.");
                        test.assertNull(QubBuildCompile.getParameters(process, action));
                        test.assertEqual(
                            Iterable.create(
                                "Usage: fake-action-name [[--folder=]<folder-path-to-build>] [--warnings=<show|error|hide>] [--buildjson] [--verbose] [--profiler] [--help]",
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

            runner.testGroup("run(QubBuildCompileParameters)", () ->
            {
                runner.test("with null parameters", (Test test) ->
                {
                    test.assertThrows(() -> QubBuildCompile.run((QubBuildCompileParameters)null),
                        new PreConditionFailure("parameters cannot be null."));
                });

                runner.test("with no project.json file in the folder to build",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(1, QubBuildCompile.run(parameters));

                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: The file at \"/project.json\" doesn't exist."),
                        Strings.getLines(output.getText().await()));
                    test.assertEqual(
                        Iterable.create(),
                        Strings.getLines(process.getErrorWriteStream().getText().await()));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: The file at \"/project.json\" doesn't exist."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                });

                runner.test("with no project.json file in the folder to build with verbose logs",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process)
                        .setVerbose(VerboseCharacterToByteWriteStream.create(output));

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: The file at \"/project.json\" doesn't exist."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: The file at \"/project.json\" doesn't exist."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                });

                runner.test("with empty project.json",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString("").await();

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: Missing object left curly bracket ('{')."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: Missing object left curly bracket ('{')."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                });

                runner.test("with empty array project.json",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString("[]").await();

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: Expected object left curly bracket ('{')."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: Expected object left curly bracket ('{')."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                });

                runner.test("with empty object project.json",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create().toString())
                        .await();

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: No language specified in project.json. Nothing to compile."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: No language specified in project.json. Nothing to compile."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                });

                runner.test("with empty object java property and no sources folder",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: No java source files found in /."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: No java source files found in /."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                });

                runner.test("with empty object java property and empty sources folder",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    currentFolder.createFolder("sources").await();

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: No java source files found in /."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: No java source files found in /."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                });

                runner.test("with non-empty \"sources\" folder and no \"outputs\" folder",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(Duration.zero, aClassFile.getLastModified().await().getDurationSinceEpoch());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        QubBuildCompileTests.getBuildJSONFileContent(outputsFolder));
                });

                runner.test("with non-empty \"sources\" folder and custom \"outputs\" folder",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = currentFolder.getFolder("bin").await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setOutputFolder("bin"))
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Updating bin/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d bin -Xlint:unchecked -Xlint:deprecation -classpath /bin/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(Duration.zero, aClassFile.getLastModified().await().getDurationSinceEpoch());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create().setOutputFolder("bin")))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        QubBuildCompileTests.getBuildJSONFileContent(outputsFolder));
                });

                runner.test("with non-empty \"sources\" folder and with existing and empty \"outputs\" folder",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(Duration.zero, aClassFile.getLastModified().await().getDurationSinceEpoch());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        QubBuildCompileTests.getBuildJSONFileContent(outputsFolder));
                });

                runner.test("with multiple source files and with existing and empty \"outputs\" folder",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(aJavaFile, bJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 2 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java sources/B.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "B.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(Duration.zero, aClassFile.getLastModified().await().getDurationSinceEpoch());
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(Duration.zero, bClassFile.getLastModified().await().getDurationSinceEpoch());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        QubBuildCompileTests.getBuildJSONFileContent(outputsFolder));
                });

                runner.test("with multiple source folders",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final Folder testsFolder = QubBuildCompileTests.getTestsFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File bJavaFile = testsFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(aJavaFile, bJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 2 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java tests/B.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "B.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(Duration.zero, aClassFile.getLastModified().await().getDurationSinceEpoch());
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(Duration.zero, bClassFile.getLastModified().await().getDurationSinceEpoch());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        QubBuildCompileTests.getBuildJSONFileContent(outputsFolder));
                });

                runner.test("with source file with same age as existing class file but no build.json file",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();

                    clock.advance(Duration.minutes(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        QubBuildCompileTests.getBuildJSONFileContent(outputsFolder));
                });

                runner.test("with source file with same age as existing class file and with build.json file",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString());

                    clock.advance(Duration.minutes(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"));

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "No files need to be compiled.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(Duration.zero, aClassFile.getLastModified().await().getDurationSinceEpoch());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with source file newer than existing class file and with build.json file",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    aJavaFile.setContentsAsString("A.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with one source file with one error",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.minutes(1));

                    aJavaFile.setContentsAsString("A.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile)
                            .addCompilerIssues(new JavaCompilerIssue("sources\\A.java", 1, 20, Issue.Type.Error, "This doesn't look right to me."))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file...",
                            "1 Error:",
                            "sources/A.java (Line 1): This doesn't look right to me."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java...",
                            "VERBOSE: sources\\A.java:1: error: This doesn't look right to me.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:                    ^",
                            "VERBOSE: Compilation finished.",
                            "1 Error:",
                            "sources/A.java (Line 1): This doesn't look right to me.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(Duration.zero, aClassFile.getLastModified().await().getDurationSinceEpoch());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create("sources/A.java")
                                    .setLastModified(clock.getCurrentDateTime())
                                    .addIssue(
                                        new JavaCompilerIssue(
                                            "sources/A.java",
                                            1,
                                            20,
                                            Issue.Type.Error,
                                            "This doesn't look right to me."))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with one unmodified source file with one warning",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .addIssue(new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 1, 20, Issue.Type.Warning, "Are you sure?"))))
                            .toString());

                    clock.advance(Duration.minutes(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"));

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled.",
                            "1 Unmodified Warning:",
                            "sources/A.java (Line 1): Are you sure?"),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Has warnings",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "No files need to be compiled.",
                            "1 Unmodified Warning:",
                            "sources/A.java (Line 1): Are you sure?",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(Duration.zero, aClassFile.getLastModified().await().getDurationSinceEpoch());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .addIssue(new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 1, 20, Issue.Type.Warning, "Are you sure?"))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with one modified source file with one warning",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .addIssue(new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 1, 20, Issue.Type.Warning, "Are you sure?"))))
                            .toString());

                    clock.advance(Duration.minutes(1));

                    aJavaFile.setContentsAsString("A.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile)
                            .addCompilerIssues(new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 1, 20, Issue.Type.Warning, "Are you still sure?"))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file...",
                            "1 Warning:",
                            "sources/A.java (Line 1): Are you still sure?"),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java...",
                            "VERBOSE: sources/A.java:1: warning: Are you still sure?",
                            "VERBOSE: Fake code line",
                            "VERBOSE:                    ^",
                            "VERBOSE: Compilation finished.",
                            "1 Warning:",
                            "sources/A.java (Line 1): Are you still sure?",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create("sources/A.java")
                                    .setLastModified(clock.getCurrentDateTime())
                                    .addIssue(new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 1, 20, Issue.Type.Warning, "Are you still sure?"))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with two source files with one error and one warning",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder testsFolder = QubBuildCompileTests.getTestsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.minutes(1));

                    aJavaFile.setContentsAsString("A.java source").await();

                    final File aTestsJavaFile = testsFolder.getFile("ATests.java").await();
                    aTestsJavaFile.setContentsAsString("ATests.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(aTestsJavaFile, aJavaFile)
                            .addCompilerIssues(
                                    new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 1, 5, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue(aTestsJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files...",
                            "1 Warning:",
                            "tests/ATests.java (Line 10): Can't be this.",
                            "1 Error:",
                            "sources/A.java (Line 1): Are you sure?"),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: /tests/ATests.java - New file",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Added source files:",
                            "VERBOSE: /tests/ATests.java",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 2 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ tests/ATests.java sources/A.java...",
                            "VERBOSE: sources/A.java:1: error: Are you sure?",
                            "VERBOSE: Fake code line",
                            "VERBOSE:     ^",
                            "VERBOSE: tests/ATests.java:10: warning: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: Compilation finished.",
                            "1 Warning:",
                            "tests/ATests.java (Line 10): Can't be this.",
                            "1 Error:",
                            "sources/A.java (Line 1): Are you sure?",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "ATests.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aTestsClassFile = outputsFolder.getFile("ATests.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual("ATests.java bytecode", aTestsClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aTestsClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 1, 5, Issue.Type.Error, "Are you sure?")),
                                BuildJSONSourceFile.create(aTestsJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(aTestsJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with multiple source files with errors and warnings",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder testsFolder = QubBuildCompileTests.getTestsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.minutes(1));

                    aJavaFile.setContentsAsString("A.java source").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source").await();
                    final File aTestsJavaFile = testsFolder.getFile("ATests.java").await();
                    aTestsJavaFile.setContentsAsString("ATests.java source").await();
                    final File cJavaFile = testsFolder.getFile("C.java").await();
                    cJavaFile.setContentsAsString("C.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(bJavaFile, aTestsJavaFile, cJavaFile, aJavaFile)
                            .addCompilerIssues(
                                    new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 12, 2, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue(bJavaFile.relativeTo(currentFolder), 1, 5, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."),
                                    new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 20, 7, Issue.Type.Error, "Can't be this."),
                                    new JavaCompilerIssue(aTestsJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(3, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 4 files...",
                            "2 Warnings:",
                            "tests/ATests.java (Line 10): Can't be this.",
                            "tests/C.java (Line 10): Can't be this.",
                            "3 Errors:",
                            "sources/A.java (Line 12): Are you sure?",
                            "sources/B.java (Line 1): Are you sure?",
                            "tests/C.java (Line 20): Can't be this."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: /sources/B.java - New file",
                            "VERBOSE: /tests/ATests.java - New file",
                            "VERBOSE: /tests/C.java - New file",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Added source files:",
                            "VERBOSE: /sources/B.java",
                            "VERBOSE: /tests/ATests.java",
                            "VERBOSE: /tests/C.java",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 4 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/B.java tests/ATests.java tests/C.java sources/A.java...",
                            "VERBOSE: sources/A.java:12: error: Are you sure?",
                            "VERBOSE: Fake code line",
                            "VERBOSE:  ^",
                            "VERBOSE: sources/B.java:1: error: Are you sure?",
                            "VERBOSE: Fake code line",
                            "VERBOSE:     ^",
                            "VERBOSE: tests/C.java:10: warning: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: tests/C.java:20: error: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: tests/ATests.java:10: warning: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: Compilation finished.",
                            "2 Warnings:",
                            "tests/ATests.java (Line 10): Can't be this.",
                            "tests/C.java (Line 10): Can't be this.",
                            "3 Errors:",
                            "sources/A.java (Line 12): Are you sure?",
                            "sources/B.java (Line 1): Are you sure?",
                            "tests/C.java (Line 20): Can't be this.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "ATests.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aTestsClassFile = outputsFolder.getFile("ATests.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual("ATests.java bytecode", aTestsClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aTestsClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 12, 2, Issue.Type.Error, "Are you sure?")),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(bJavaFile.relativeTo(currentFolder), 1, 5, Issue.Type.Error, "Are you sure?")),
                                BuildJSONSourceFile.create(aTestsJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(aTestsJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this.")),
                                BuildJSONSourceFile.create(cJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .setIssues(Iterable.create(
                                        new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."),
                                        new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 20, 7, Issue.Type.Error, "Can't be this.")))
                            ))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with multiple source files with errors and warnings and -warnings=show",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder testsFolder = QubBuildCompileTests.getTestsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.minutes(1));

                    aJavaFile.setContentsAsString("A.java source").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source").await();
                    final File aTestsJavaFile = testsFolder.getFile("ATests.java").await();
                    aTestsJavaFile.setContentsAsString("ATests.java source").await();
                    final File cJavaFile = testsFolder.getFile("C.java").await();
                    cJavaFile.setContentsAsString("C.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(bJavaFile, aTestsJavaFile, cJavaFile, aJavaFile)
                            .addCompilerIssues(
                                    new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 12, 2, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue(bJavaFile.relativeTo(currentFolder), 1, 5, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."),
                                    new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 20, 7, Issue.Type.Error, "Can't be this."),
                                    new JavaCompilerIssue(aTestsJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process)
                        .setWarnings(Warnings.Show);

                    test.assertEqual(3, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 4 files...",
                            "2 Warnings:",
                            "tests/ATests.java (Line 10): Can't be this.",
                            "tests/C.java (Line 10): Can't be this.",
                            "3 Errors:",
                            "sources/A.java (Line 12): Are you sure?",
                            "sources/B.java (Line 1): Are you sure?",
                            "tests/C.java (Line 20): Can't be this."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: /sources/B.java - New file",
                            "VERBOSE: /tests/ATests.java - New file",
                            "VERBOSE: /tests/C.java - New file",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Added source files:",
                            "VERBOSE: /sources/B.java",
                            "VERBOSE: /tests/ATests.java",
                            "VERBOSE: /tests/C.java",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 4 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/B.java tests/ATests.java tests/C.java sources/A.java...",
                            "VERBOSE: sources/A.java:12: error: Are you sure?",
                            "VERBOSE: Fake code line",
                            "VERBOSE:  ^",
                            "VERBOSE: sources/B.java:1: error: Are you sure?",
                            "VERBOSE: Fake code line",
                            "VERBOSE:     ^",
                            "VERBOSE: tests/C.java:10: warning: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: tests/C.java:20: error: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: tests/ATests.java:10: warning: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: Compilation finished.",
                            "2 Warnings:",
                            "tests/ATests.java (Line 10): Can't be this.",
                            "tests/C.java (Line 10): Can't be this.",
                            "3 Errors:",
                            "sources/A.java (Line 12): Are you sure?",
                            "sources/B.java (Line 1): Are you sure?",
                            "tests/C.java (Line 20): Can't be this.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "ATests.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aTestsClassFile = outputsFolder.getFile("ATests.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual("ATests.java bytecode", aTestsClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aTestsClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 12, 2, Issue.Type.Error, "Are you sure?")),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(bJavaFile.relativeTo(currentFolder), 1, 5, Issue.Type.Error, "Are you sure?")),
                                BuildJSONSourceFile.create(aTestsJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(aTestsJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this.")),
                                BuildJSONSourceFile.create(cJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .setIssues(Iterable.create(
                                        new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."),
                                        new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 20, 7, Issue.Type.Error, "Can't be this.")))
                            ))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with multiple source files with errors and warnings and -warnings=hide",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder testsFolder = QubBuildCompileTests.getTestsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.minutes(1));

                    aJavaFile.setContentsAsString("A.java source").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source").await();
                    final File aTestsJavaFile = testsFolder.getFile("ATests.java").await();
                    aTestsJavaFile.setContentsAsString("ATests.java source").await();
                    final File cJavaFile = testsFolder.getFile("C.java").await();
                    cJavaFile.setContentsAsString("C.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(bJavaFile, aTestsJavaFile, cJavaFile, aJavaFile)
                            .addCompilerIssues(
                                    new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 12, 2, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue(bJavaFile.relativeTo(currentFolder), 1, 5, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."),
                                    new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 20, 7, Issue.Type.Error, "Can't be this."),
                                    new JavaCompilerIssue(aTestsJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process)
                        .setWarnings(Warnings.Hide);

                    test.assertEqual(3, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 4 files...",
                            "3 Errors:",
                            "sources/A.java (Line 12): Are you sure?",
                            "sources/B.java (Line 1): Are you sure?",
                            "tests/C.java (Line 20): Can't be this."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: /sources/B.java - New file",
                            "VERBOSE: /tests/ATests.java - New file",
                            "VERBOSE: /tests/C.java - New file",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Added source files:",
                            "VERBOSE: /sources/B.java",
                            "VERBOSE: /tests/ATests.java",
                            "VERBOSE: /tests/C.java",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 4 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/B.java tests/ATests.java tests/C.java sources/A.java...",
                            "VERBOSE: sources/A.java:12: error: Are you sure?",
                            "VERBOSE: Fake code line",
                            "VERBOSE:  ^",
                            "VERBOSE: sources/B.java:1: error: Are you sure?",
                            "VERBOSE: Fake code line",
                            "VERBOSE:     ^",
                            "VERBOSE: tests/C.java:10: warning: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: tests/C.java:20: error: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: tests/ATests.java:10: warning: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: Compilation finished.",
                            "3 Errors:",
                            "sources/A.java (Line 12): Are you sure?",
                            "sources/B.java (Line 1): Are you sure?",
                            "tests/C.java (Line 20): Can't be this.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "ATests.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aTestsClassFile = outputsFolder.getFile("ATests.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual("ATests.java bytecode", aTestsClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aTestsClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 12, 2, Issue.Type.Error, "Are you sure?")),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(bJavaFile.relativeTo(currentFolder), 1, 5, Issue.Type.Error, "Are you sure?")),
                                BuildJSONSourceFile.create(aTestsJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1))),
                                BuildJSONSourceFile.create(cJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 20, 7, Issue.Type.Error, "Can't be this."))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with multiple source files with errors and warnings and -warnings=error",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder testsFolder = QubBuildCompileTests.getTestsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.minutes(1));

                    aJavaFile.setContentsAsString("A.java source").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source").await();
                    final File aTestsJavaFile = testsFolder.getFile("ATests.java").await();
                    aTestsJavaFile.setContentsAsString("ATests.java source").await();
                    final File cJavaFile = testsFolder.getFile("C.java").await();
                    cJavaFile.setContentsAsString("C.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(bJavaFile, aTestsJavaFile, cJavaFile, aJavaFile)
                            .addCompilerIssues(
                                    new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 12, 2, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue(bJavaFile.relativeTo(currentFolder), 1, 5, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."),
                                    new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 20, 7, Issue.Type.Error, "Can't be this."),
                                    new JavaCompilerIssue(aTestsJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Warning, "Can't be this."))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process)
                        .setWarnings(Warnings.Error);

                    test.assertEqual(3, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 4 files...",
                            "5 Errors:",
                            "sources/A.java (Line 12): Are you sure?",
                            "sources/B.java (Line 1): Are you sure?",
                            "tests/ATests.java (Line 10): Can't be this.",
                            "tests/C.java (Line 20): Can't be this.",
                            "tests/C.java (Line 10): Can't be this."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: /sources/B.java - New file",
                            "VERBOSE: /tests/ATests.java - New file",
                            "VERBOSE: /tests/C.java - New file",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Added source files:",
                            "VERBOSE: /sources/B.java",
                            "VERBOSE: /tests/ATests.java",
                            "VERBOSE: /tests/C.java",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 4 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/B.java tests/ATests.java tests/C.java sources/A.java...",
                            "VERBOSE: sources/A.java:12: error: Are you sure?",
                            "VERBOSE: Fake code line",
                            "VERBOSE:  ^",
                            "VERBOSE: sources/B.java:1: error: Are you sure?",
                            "VERBOSE: Fake code line",
                            "VERBOSE:     ^",
                            "VERBOSE: tests/C.java:10: warning: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: tests/C.java:20: error: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: tests/ATests.java:10: warning: Can't be this.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:       ^",
                            "VERBOSE: Compilation finished.",
                            "5 Errors:",
                            "sources/A.java (Line 12): Are you sure?",
                            "sources/B.java (Line 1): Are you sure?",
                            "tests/ATests.java (Line 10): Can't be this.",
                            "tests/C.java (Line 20): Can't be this.",
                            "tests/C.java (Line 10): Can't be this.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "ATests.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aTestsClassFile = outputsFolder.getFile("ATests.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual("ATests.java bytecode", aTestsClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aTestsClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(aJavaFile.relativeTo(currentFolder), 12, 2, Issue.Type.Error, "Are you sure?")),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(bJavaFile.relativeTo(currentFolder), 1, 5, Issue.Type.Error, "Are you sure?")),
                                BuildJSONSourceFile.create(aTestsJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue(aTestsJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Error, "Can't be this.")),
                                BuildJSONSourceFile.create(cJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .setIssues(Iterable.create(
                                        new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 20, 7, Issue.Type.Error, "Can't be this."),
                                        new JavaCompilerIssue(cJavaFile.relativeTo(currentFolder), 10, 7, Issue.Type.Error, "Can't be this.")))
                            ))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with two source files newer than their existing class files",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    bClassFile.setContentsAsString("B.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    aJavaFile.setContentsAsString("A.java source").await();
                    bJavaFile.setContentsAsString("B.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(aJavaFile, bJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: /sources/B.java - Last modified: 1970-01-01T00:00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "VERBOSE: /sources/B.java",
                            "Compiling 2 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java sources/B.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "B.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), bClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with one modified source file and one unmodified and independent source file",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    bClassFile.setContentsAsString("B.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    aJavaFile.setContentsAsString("A.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(aJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: /sources/B.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "B.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), bClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with one modified source file and one unmodified and dependent source file",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source, depends on A").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    bClassFile.setContentsAsString("B.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(aJavaFile.relativeTo(currentFolder)))))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    aJavaFile.setContentsAsString("A.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(aJavaFile, bJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - Last modified: 1970-01-01T00:00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: /sources/B.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 2 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java sources/B.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "B.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), bClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(aJavaFile.relativeTo(currentFolder)))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with one unmodified source file and another modified and dependant source file",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    bClassFile.setContentsAsString("B.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(aJavaFile.relativeTo(currentFolder)))))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    bJavaFile.setContentsAsString("B.java source, depends on A").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(bJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: /sources/B.java - Last modified: 1970-01-01T00:00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/B.java",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/B.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "B.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), bClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(aJavaFile.relativeTo(currentFolder)))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with one deleted source file and another unmodified and dependant source file",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source, depends on A").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    bClassFile.setContentsAsString("B.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(aJavaFile.relativeTo(currentFolder)))))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(bJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/B.java - No changes or issues",
                            "VERBOSE: Deleted source files:",
                            "VERBOSE: /sources/A.java",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Source files with deleted dependencies:",
                            "VERBOSE: /sources/B.java",
                            "VERBOSE: Source files with modified dependencies:",
                            "VERBOSE: /sources/B.java",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/B.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "B.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), bClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(aJavaFile.relativeTo(currentFolder)))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with one new source file and another unmodified and independent source file",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source").await();
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    bClassFile.setContentsAsString("B.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(aJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - New file",
                            "VERBOSE: /sources/B.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Added source files:",
                            "VERBOSE: /sources/A.java",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "B.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), bClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("N depends on nothing, A depends on B, B depends on C, C.java is modified: A, B, and C should be compiled",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source, depends on B").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source, depends on C").await();
                    final File cJavaFile = sourcesFolder.getFile("C.java").await();
                    final File nJavaFile = sourcesFolder.getFile("N.java").await();
                    nJavaFile.setContentsAsString("N.java source").await();

                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    bClassFile.setContentsAsString("B.java bytecode").await();
                    final File cClassFile = outputsFolder.getFile("C.class").await();
                    cClassFile.setContentsAsString("C.java bytecode").await();
                    final File nClassFile = outputsFolder.getFile("N.class").await();
                    nClassFile.setContentsAsString("N.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(bJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(cJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(cJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(nJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    cJavaFile.setContentsAsString("C.java source").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(cJavaFile, bJavaFile, aJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 3 files..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: /sources/B.java - No changes or issues",
                            "VERBOSE: /sources/C.java - Last modified: 1970-01-01T00:00:01Z",
                            "VERBOSE:                 - Last built:    1970-01-01T00:00Z",
                            "VERBOSE: /sources/N.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Modified source files:",
                            "VERBOSE: /sources/C.java",
                            "Compiling 3 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/C.java sources/B.java sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "B.class",
                            "C.class",
                            "N.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), bClassFile.getLastModified().await());
                    test.assertEqual("C.java bytecode", cClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), cClassFile.getLastModified().await());
                    test.assertEqual("N.java bytecode", nClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), nClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(bJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(cJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(cJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(nJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("N depends on nothing, A depends on B, B depends on C, C.java is deleted: A and B should be compiled",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source, depends on B").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source, depends on C").await();
                    final File cJavaFile = sourcesFolder.getFile("C.java").await();
                    final File nJavaFile = sourcesFolder.getFile("N.java").await();
                    nJavaFile.setContentsAsString("N.java source").await();

                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    bClassFile.setContentsAsString("B.java bytecode").await();
                    final File cClassFile = outputsFolder.getFile("C.class").await();
                    cClassFile.setContentsAsString("C.java bytecode").await();
                    final File nClassFile = outputsFolder.getFile("N.class").await();
                    nClassFile.setContentsAsString("N.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(bJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(cJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(cJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(nJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(bJavaFile, aJavaFile)
                            .addCompilerIssues(
                                    new JavaCompilerIssue(bJavaFile.relativeTo(currentFolder), 1, 25, Issue.Type.Error, "Missing definition for C."))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files...",
                            "1 Error:",
                            "sources/B.java (Line 1): Missing definition for C."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: /sources/B.java - No changes or issues",
                            "VERBOSE: /sources/N.java - No changes or issues",
                            "VERBOSE: Deleted source files:",
                            "VERBOSE: /sources/C.java",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Source files with deleted dependencies:",
                            "VERBOSE: /sources/B.java",
                            "VERBOSE: Source files with modified dependencies:",
                            "VERBOSE: /sources/B.java",
                            "Compiling 2 files...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/B.java sources/A.java...",
                            "VERBOSE: sources/B.java:1: error: Missing definition for C.",
                            "VERBOSE: Fake code line",
                            "VERBOSE:                         ^",
                            "VERBOSE: Compilation finished.",
                            "1 Error:",
                            "sources/B.java (Line 1): Missing definition for C.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "B.class",
                            "N.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), bClassFile.getLastModified().await());
                    test.assertEqual("N.java bytecode", nClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), nClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(bJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(cJavaFile.relativeTo(currentFolder)))
                                    .addIssue(new JavaCompilerIssue(bJavaFile.relativeTo(currentFolder), 1, 25, Issue.Type.Error, "Missing definition for C.")),
                                BuildJSONSourceFile.create(nJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("N depends on nothing, A depends on B, B depends on C, C.class is deleted: C should be compiled",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    outputsFolder.create().await();
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source, depends on B").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source, depends on C").await();
                    final File cJavaFile = sourcesFolder.getFile("C.java").await();
                    cJavaFile.setContentsAsString("C.java source").await();
                    final File nJavaFile = sourcesFolder.getFile("N.java").await();
                    nJavaFile.setContentsAsString("N.java source").await();

                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    bClassFile.setContentsAsString("B.java bytecode").await();
                    final File cClassFile = outputsFolder.getFile("C.class").await();
                    final File nClassFile = outputsFolder.getFile("N.class").await();
                    nClassFile.setContentsAsString("N.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(bJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(cJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(cJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(nJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(cJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: /sources/B.java - No changes or issues",
                            "VERBOSE: /sources/C.java - No changes or issues",
                            "VERBOSE: /sources/N.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Source files with missing class files:",
                            "VERBOSE: /sources/C.java",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/C.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "B.class",
                            "C.class",
                            "N.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), bClassFile.getLastModified().await());
                    test.assertEqual("C.java bytecode", cClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), cClassFile.getLastModified().await());
                    test.assertEqual("N.java bytecode", nClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), nClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(bJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(cJavaFile.relativeTo(currentFolder))),
                                BuildJSONSourceFile.create(cJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                BuildJSONSourceFile.create(nJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("nothing gets compiled when project.json publisher changes",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setPublisher("old-publisher")
                                    .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setPublisher("new-publisher")
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"));

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "No files need to be compiled.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setPublisher("new-publisher")
                                    .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("nothing gets compiled when project.json project changes",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setProject("old-project")
                                    .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setProject("new-project")
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"));

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "No files need to be compiled.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setProject("new-project")
                                    .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("nothing gets compiled when project.json version changes",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setVersion("old-version")
                                    .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setVersion("new-version")
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"));

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "No files need to be compiled.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setVersion("new-version")
                                    .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("everything gets compiled when project.json java version changes from 11 to 1.8",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final Folder javaFolder = currentFolder.getFileSystem().createFolder("/java/").await();
                    final Folder jdk11Folder = javaFolder.createFolder("jdk-11.0.1").await();
                    final Folder jre8Folder = javaFolder.createFolder("jre1.8.0_192").await();

                    process.getEnvironmentVariables()
                        .set("JAVA_HOME", jdk11Folder.toString());

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setVersion("11")))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setVersion("1.8"))
                                .toString())
                        .await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addJavaSourceVersion("1.8")
                            .addJavaTargetVersion("1.8")
                            .addBootClasspath(jre8Folder.getFile("lib/rt.jar").await())
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile.relativeTo(currentFolder))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -source 1.8 -target 1.8 -bootclasspath /java/jre1.8.0_192/lib/rt.jar -classpath /outputs/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setVersion("1.8")))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("everything gets compiled when project.json java version changes from 11 to 8",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final Folder javaFolder = currentFolder.getFileSystem().createFolder("/java/").await();
                    final Folder jdk11Folder = javaFolder.createFolder("jdk-11.0.1").await();
                    final Folder jre8Folder = javaFolder.createFolder("jre1.8.0_192").await();

                    process.getEnvironmentVariables()
                        .set("JAVA_HOME", jdk11Folder.toString());

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setVersion("11")))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setVersion("8"))
                                .toString())
                        .await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addJavaSourceVersion("8")
                            .addJavaTargetVersion("8")
                            .addBootClasspath(jre8Folder.getFile("lib/rt.jar").await())
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile.relativeTo(currentFolder))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -source 8 -target 8 -bootclasspath /java/jre1.8.0_192/lib/rt.jar -classpath /outputs/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setVersion("8")))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("nothing gets compiled when project.json java version changes from 1.8 to 8",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final Folder javaFolder = currentFolder.getFileSystem().createFolder("/java/").await();
                    final Folder jdk11Folder = javaFolder.createFolder("jdk-11.0.1").await();
                    javaFolder.createFolder("jre1.8.0_192").await();

                    process.getEnvironmentVariables()
                        .set("JAVA_HOME", jdk11Folder.toString());

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setVersion("1.8")))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(aJavaFile.getLastModified().await())))
                            .toString());
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setVersion("8"))
                                .toString())
                        .await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"));

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "No files need to be compiled.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setVersion("8")))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("nothing gets compiled when project.json java version changes from 8 to 1.8",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final Folder javaFolder = currentFolder.getFileSystem().createFolder("/java/").await();
                    final Folder jdk11Folder = javaFolder.createFolder("jdk-11.0.1").await();
                    final Folder jre8Folder = javaFolder.createFolder("jre1.8.0_192").await();

                    process.getEnvironmentVariables()
                        .set("JAVA_HOME", jdk11Folder.toString());

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setVersion("8")))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setVersion("1.8"))
                                .toString())
                        .await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"));

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "No files need to be compiled.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setVersion("1.8")))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("nothing gets compiled when project.json java dependency is added",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final QubFolder qubFolder = process.getQubFolder().await();
                    final QubProjectVersionFolder projectVersionFolder = qubFolder.getProjectVersionFolder("a", "b", "c").await();
                    projectVersionFolder.getCompiledSourcesFile().await().create().await();

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setDependencies(Iterable.create(
                                        projectVersionFolder.getProjectSignature().await())))
                                .toString())
                        .await();

                    clock.advance(Duration.minutes(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"));

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "No files need to be compiled.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setDependencies(Iterable.create(
                                            projectVersionFolder.getProjectSignature().await()))))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("everything gets compiled when project.json java dependency is removed",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final QubFolder qubFolder = process.getQubFolder().await();
                    final QubProjectVersionFolder projectVersionFolder = qubFolder.getProjectVersionFolder("a", "b", "c").await();

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setDependencies(Iterable.create(
                                            projectVersionFolder.getProjectSignature().await()))))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();

                    clock.advance(Duration.minutes(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile.relativeTo(currentFolder))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("everything gets compiled when project.json java dependency version is changed",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final QubFolder qubFolder = process.getQubFolder().await();
                    final QubProjectFolder projectFolder = qubFolder.getProjectFolder("a", "b").await();
                    final QubProjectVersionFolder cVersionFolder = projectFolder.getProjectVersionFolder("c").await();
                    cVersionFolder.getCompiledSourcesFile().await().create().await();
                    final QubProjectVersionFolder dVersionFolder = projectFolder.getProjectVersionFolder("d").await();
                    dVersionFolder.getCompiledSourcesFile().await().create().await();

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setDependencies(Iterable.create(
                                            cVersionFolder.getProjectSignature().await()))))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setDependencies(Iterable.create(
                                        dVersionFolder.getProjectSignature().await())))
                                .toString())
                        .await();

                    clock.advance(Duration.minutes(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(Iterable.create(outputsFolder.toString(), dVersionFolder.getCompiledSourcesFile().await().toString()))
                            .addSourceFile(aJavaFile.relativeTo(currentFolder))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/;/qub/a/b/versions/d/b.jar sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setDependencies(Iterable.create(
                                            dVersionFolder.getProjectSignature().await()))))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with deleted source file with anonymous classes",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    final File bJavaFile = sourcesFolder.getFile("B.java").await();
                    bJavaFile.setContentsAsString("B.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    aClassFile.setContentsAsString("A.java bytecode").await();
                    final File a1ClassFile = outputsFolder.getFile("A$1.class").await();
                    a1ClassFile.setContentsAsString("A.java anonymous class 1 bytecode").await();
                    final File a2ClassFile = outputsFolder.getFile("A$2.class").await();
                    a2ClassFile.setContentsAsString("A.java anonymous class 2 bytecode").await();
                    final File bClassFile = outputsFolder.getFile("B.class").await();
                    bClassFile.setContentsAsString("B.java bytecode").await();
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    buildJsonFile.setContentsAsString(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime()),
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(clock.getCurrentDateTime())))
                            .toString());

                    clock.advance(Duration.seconds(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFiles(bJavaFile)
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(0, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/B.java - No changes or issues",
                            "VERBOSE: Deleted source files:",
                            "VERBOSE: /sources/A.java",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "No files need to be compiled.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertEqual(
                        Iterable.create(
                            "B.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("B.java bytecode", bClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.zero), bClassFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(bJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with project.json dependency with publisher that doesn't exist",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final QubFolder qubFolder = process.getQubFolder().await();
                    final QubProjectFolder projectFolder = qubFolder.getProjectFolder("a", "b").await();
                    final QubProjectVersionFolder cVersionFolder = projectFolder.getProjectVersionFolder("c").await();

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setDependencies(Iterable.create(
                                        cVersionFolder.getProjectSignature().await())))
                                .toString())
                        .await();

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: No publisher folder named \"a\" found in the Qub folder (/qub/)."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: No publisher folder named \"a\" found in the Qub folder (/qub/)."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertFalse(outputsFolder.exists().await());
                });

                runner.test("with project.json dependency with project that doesn't exist",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final QubFolder qubFolder = process.getQubFolder().await();
                    final QubProjectFolder projectFolder = qubFolder.getProjectFolder("a", "b").await();
                    final QubProjectVersionFolder cVersionFolder = projectFolder.getProjectVersionFolder("c").await();
                    cVersionFolder.getPublisherFolder().await().create().await();

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setDependencies(Iterable.create(
                                        cVersionFolder.getProjectSignature().await())))
                                .toString())
                        .await();

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    test.assertEqual(1, QubBuildCompile.run(parameters));
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: No project folder named \"b\" found in the \"a\" publisher folder (/qub/a/)."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: No project folder named \"b\" found in the \"a\" publisher folder (/qub/a/)."),
                        QubBuildCompileTests.getLogFileContentLines(process));

                    test.assertFalse(outputsFolder.exists().await());
                });

                runner.test("with one-hop transitive dependency",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final QubFolder qubFolder = process.getQubFolder().await();

                    final QubProjectVersionFolder bProjectVersionFolder = qubFolder.getProjectVersionFolder("a", "b", "1").await();
                    bProjectVersionFolder.getCompiledSourcesFile().await().create().await();

                    final QubProjectVersionFolder cProjectVersionFolder = qubFolder.getProjectVersionFolder("a", "c", "2").await();
                    cProjectVersionFolder.getCompiledSourcesFile().await().create().await();
                    cProjectVersionFolder.getProjectJSONFile().await().setContentsAsString(
                        ProjectJSON.create()
                            .setProject(cProjectVersionFolder.getProjectName().await())
                            .setPublisher(cProjectVersionFolder.getPublisherName().await())
                            .setVersion(cProjectVersionFolder.getVersion().await())
                            .setJava(ProjectJSONJava.create()
                                .setDependencies(Iterable.create(
                                    bProjectVersionFolder.getProjectSignature().await())))
                            .toString())
                        .await();

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setDependencies(Iterable.create(
                                        cProjectVersionFolder.getProjectSignature().await())))
                                .toString())
                        .await();

                    clock.advance(Duration.minutes(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(Iterable.create(
                                outputsFolder.toString(),
                                cProjectVersionFolder.getCompiledSourcesFile().await().toString(),
                                bProjectVersionFolder.getCompiledSourcesFile().await().toString()))
                            .addSourceFile(aJavaFile.relativeTo(currentFolder))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/;/qub/a/c/versions/2/c.jar;/qub/a/b/versions/1/b.jar sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setDependencies(Iterable.create(
                                            cProjectVersionFolder.getProjectSignature().await()))))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with two-hop transitive dependency",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final QubFolder qubFolder = process.getQubFolder().await();

                    final QubProjectVersionFolder bProjectVersionFolder = qubFolder.getProjectVersionFolder("a", "b", "1").await();
                    bProjectVersionFolder.getCompiledSourcesFile().await().create().await();

                    final QubProjectVersionFolder cProjectVersionFolder = qubFolder.getProjectVersionFolder("a", "c", "2").await();
                    cProjectVersionFolder.getCompiledSourcesFile().await().create().await();
                    cProjectVersionFolder.getProjectJSONFile().await().setContentsAsString(
                        ProjectJSON.create()
                            .setProject(cProjectVersionFolder.getProjectName().await())
                            .setPublisher(cProjectVersionFolder.getPublisherName().await())
                            .setVersion(cProjectVersionFolder.getVersion().await())
                            .setJava(ProjectJSONJava.create()
                                .setDependencies(Iterable.create(
                                    bProjectVersionFolder.getProjectSignature().await())))
                            .toString())
                        .await();

                    final QubProjectVersionFolder eProjectVersionFolder = qubFolder.getProjectVersionFolder("d", "e", "3").await();
                    eProjectVersionFolder.getCompiledSourcesFile().await().create().await();
                    eProjectVersionFolder.getProjectJSONFile().await().setContentsAsString(
                        ProjectJSON.create()
                            .setProject(eProjectVersionFolder.getProjectName().await())
                            .setPublisher(eProjectVersionFolder.getPublisherName().await())
                            .setVersion(eProjectVersionFolder.getVersion().await())
                            .setJava(ProjectJSONJava.create()
                                .setDependencies(Iterable.create(
                                    cProjectVersionFolder.getProjectSignature().await())))
                            .toString())
                        .await();

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setDependencies(Iterable.create(
                                        eProjectVersionFolder.getProjectSignature().await())))
                                .toString())
                        .await();

                    clock.advance(Duration.minutes(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(Iterable.create(
                                outputsFolder.toString(),
                                eProjectVersionFolder.getCompiledSourcesFile().await().toString(),
                                cProjectVersionFolder.getCompiledSourcesFile().await().toString(),
                                bProjectVersionFolder.getCompiledSourcesFile().await().toString()))
                            .addSourceFile(aJavaFile.relativeTo(currentFolder))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/;/qub/d/e/versions/3/e.jar;/qub/a/c/versions/2/c.jar;/qub/a/b/versions/1/b.jar sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    final File aClassFile = outputsFolder.getFile("A.class").await();
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), aClassFile.getLastModified().await());
                    final File buildJsonFile = QubBuildCompileTests.getBuildJSONFile(outputsFolder);
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(
                                ProjectJSON.create()
                                    .setJava(ProjectJSONJava.create()
                                        .setDependencies(Iterable.create(
                                            eProjectVersionFolder.getProjectSignature().await()))))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        buildJsonFile.getContentsAsString().await());
                    test.assertEqual(clock.getCurrentDateTime(), buildJsonFile.getLastModified().await());
                });

                runner.test("with multiple versions of same project dependency",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);
                    final QubFolder qubFolder = process.getQubFolder().await();

                    final QubProjectVersionFolder b1ProjectVersionFolder = qubFolder.getProjectVersionFolder("a", "b", "1").await();
                    b1ProjectVersionFolder.getCompiledSourcesFile().await().create().await();

                    final QubProjectVersionFolder b2ProjectVersionFolder = qubFolder.getProjectVersionFolder("a", "b", "2").await();
                    b2ProjectVersionFolder.getCompiledSourcesFile().await().create().await();

                    final QubProjectVersionFolder cProjectVersionFolder = qubFolder.getProjectVersionFolder("a", "c", "2").await();
                    cProjectVersionFolder.getCompiledSourcesFile().await().create().await();
                    cProjectVersionFolder.getProjectJSONFile().await().setContentsAsString(
                        ProjectJSON.create()
                            .setProject(cProjectVersionFolder.getProjectName().await())
                            .setPublisher(cProjectVersionFolder.getPublisherName().await())
                            .setVersion(cProjectVersionFolder.getVersion().await())
                            .setJava(ProjectJSONJava.create()
                                .setDependencies(Iterable.create(
                                    b1ProjectVersionFolder.getProjectSignature().await())))
                            .toString())
                        .await();

                    final QubProjectVersionFolder eProjectVersionFolder = qubFolder.getProjectVersionFolder("d", "e", "3").await();
                    eProjectVersionFolder.getCompiledSourcesFile().await().create().await();
                    eProjectVersionFolder.getProjectJSONFile().await().setContentsAsString(
                        ProjectJSON.create()
                            .setProject(eProjectVersionFolder.getProjectName().await())
                            .setPublisher(eProjectVersionFolder.getPublisherName().await())
                            .setVersion(eProjectVersionFolder.getVersion().await())
                            .setJava(ProjectJSONJava.create()
                                .setDependencies(Iterable.create(
                                    cProjectVersionFolder.getProjectSignature().await(),
                                    b2ProjectVersionFolder.getProjectSignature().await())))
                            .toString())
                        .await();

                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create()
                                    .setDependencies(Iterable.create(
                                        eProjectVersionFolder.getProjectSignature().await())))
                                .toString())
                        .await();

                    clock.advance(Duration.minutes(1));

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(Iterable.create(
                                outputsFolder.toString(),
                                eProjectVersionFolder.getCompiledSourcesFile().await().toString(),
                                cProjectVersionFolder.getCompiledSourcesFile().await().toString(),
                                b1ProjectVersionFolder.getCompiledSourcesFile().await().toString()))
                            .addSourceFile(aJavaFile.relativeTo(currentFolder))
                            .setCompileFunctionAutomatically());

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: Found more than one required version for package a/b:",
                            "1. a/b@1",
                            "    from d/e@3",
                            "     from a/c@2",
                            "2. a/b@2",
                            "    from d/e@3",
                            ""),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: Found more than one required version for package a/b:",
                            "1. a/b@1",
                            "    from d/e@3",
                            "     from a/c@2",
                            "2. a/b@2",
                            "    from d/e@3",
                            ""),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(1, exitCode);

                    test.assertFalse(outputsFolder.exists().await());
                });

                runner.test("with source code file modified after compilation but before build.json file updated",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final InMemoryCharacterToByteStream output = process.getOutputWriteStream();
                    final ManualClock clock = process.getClock();
                    final Folder currentFolder = process.getCurrentFolder();
                    final Folder outputsFolder = QubBuildCompileTests.getOutputsFolder(currentFolder);
                    final Folder sourcesFolder = QubBuildCompileTests.getSourcesFolder(currentFolder);

                    QubBuildCompileTests.getProjectJsonFile(currentFolder)
                        .setContentsAsString(
                            ProjectJSON.create()
                                .setJava(ProjectJSONJava.create())
                                .toString())
                        .await();
                    final File aJavaFile = sourcesFolder.getFile("A.java").await();
                    aJavaFile.setContentsAsString("A.java source").await();
                    final File aClassFile = outputsFolder.getFile("A.class").await();

                    process.getProcessFactory()
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addVersion()
                            .setVersionFunctionAutomatically("javac 14.0.1\r\n"))
                        .add(new FakeJavacProcessRun()
                            .setWorkingFolder(currentFolder)
                            .addOutputFolder(outputsFolder)
                            .addXlintUnchecked()
                            .addXlintDeprecation()
                            .addClasspath(outputsFolder)
                            .addSourceFile(aJavaFile)
                            .setFunction(() ->
                            {
                                aClassFile.setContentsAsString("A.java bytecode").await();
                                clock.advance(Duration.seconds(1));
                                aJavaFile.setContentsAsString("A.java source 2").await();
                            }));

                    clock.advance(Duration.minutes(1));

                    final QubBuildCompileParameters parameters = QubBuildCompileTests.getParameters(process);

                    final int exitCode = QubBuildCompile.run(parameters);
                    test.assertFalse(output.isDisposed());
                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        QubBuildCompileTests.getOutputLines(output));
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Getting javac version...",
                            "VERBOSE: Running /: javac --version...",
                            "VERBOSE: javac 14.0.1",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Compiling all source files.",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs/ sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        QubBuildCompileTests.getLogFileContentLines(process));
                    test.assertEqual(0, exitCode);

                    test.assertEqual(
                        Iterable.create(
                            "A.class",
                            "build.json"),
                        QubBuildCompileTests.getOutputsFolderFilePathStrings(outputsFolder),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", aClassFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)), aClassFile.getLastModified().await());
                    test.assertEqual("A.java source 2", aJavaFile.getContentsAsString().await());
                    test.assertEqual(DateTime.createFromDurationSinceEpoch(Duration.seconds(61)), aJavaFile.getLastModified().await());
                    test.assertEqual(
                        BuildJSON.create()
                            .setJavacVersion("14.0.1")
                            .setProjectJson(ProjectJSON.create().setJava(ProjectJSONJava.create()))
                            .setSourceFiles(Iterable.create(
                                BuildJSONSourceFile.create(aJavaFile.relativeTo(currentFolder))
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(JSONFormat.pretty),
                        QubBuildCompileTests.getBuildJSONFileContent(outputsFolder));
                });
            });
        });
    }

    static File getProjectJsonFile(Folder currentFolder)
    {
        return currentFolder.getFile("project.json").await();
    }

    static Folder getOutputsFolder(Folder currentFolder)
    {
        return currentFolder.getFolder("outputs").await();
    }

    static Folder getSourcesFolder(Folder currentFolder)
    {
        return currentFolder.getFolder("sources").await();
    }

    static Folder getTestsFolder(Folder currentFolder)
    {
        return currentFolder.getFolder("tests").await();
    }

    static QubBuildCompileParameters getParameters(FakeDesktopProcess process)
    {
        final CharacterToByteWriteStream output = process.getOutputWriteStream();
        final Folder currentFolder = process.getCurrentFolder();
        final EnvironmentVariables environmentVariables = process.getEnvironmentVariables();
        final FakeProcessFactory processFactory = process.getProcessFactory();
        final QubFolder qubFolder = process.getQubFolder().await();
        final Folder projectDataFolder = process.getQubProjectDataFolder().await();
        return new QubBuildCompileParameters(output, currentFolder, environmentVariables, processFactory, qubFolder, projectDataFolder);
    }

    static Iterable<String> getOutputLines(InMemoryCharacterStream output)
    {
        return Strings.getLines(output.getText().await());
    }

    static Iterable<String> getLogFileContentLines(DesktopProcess process)
    {
        final Folder projectDataFolder = process.getQubProjectDataFolder().await();
        return Strings.getLines(projectDataFolder.getFile("logs/1.log").await().getContentsAsString().await());
    }

    static Iterable<String> getOutputsFolderFilePathStrings(Folder outputsFolder)
    {
        return outputsFolder.iterateFilesRecursively()
            .map((File entry) -> entry.getPath().relativeTo(outputsFolder).toString())
            .toList();
    }

    static File getBuildJSONFile(Folder outputsFolder)
    {
        return outputsFolder.getFile("build.json").await();
    }

    static String getBuildJSONFileContent(Folder outputsFolder)
    {
        return QubBuildCompileTests.getBuildJSONFile(outputsFolder).getContentsAsString().await();
    }
}
