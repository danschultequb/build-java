package qub;

public interface QubBuildTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubBuild.class, runner.skip("Just until new JSON is in use"), () ->
        {
            runner.testGroup("main(String[])", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubBuild.main((String[])null),
                        new PreConditionFailure("args cannot be null."));
                });
            });

            runner.testGroup("getParameters(Process)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubBuild.getParameters(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with no arguments", (Test test) ->
                {
                    try (final Console console = createConsole())
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with positional folder to build argument", (Test test) ->
                {
                    try (final Console console = createConsole("/folder/to/build/"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual("/folder/to/build/", parameters.getFolderToBuild().toString());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with named folder to build argument", (Test test) ->
                {
                    try (final Console console = createConsole("--folder=/folder/to/build/"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual("/folder/to/build/", parameters.getFolderToBuild().toString());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings argument", (Test test) ->
                {
                    try (final Console console = createConsole("--warnings"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings= argument", (Test test) ->
                {
                    try (final Console console = createConsole("--warnings="))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings=Show argument", (Test test) ->
                {
                    try (final Console console = createConsole("--warnings=Show"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings=error argument", (Test test) ->
                {
                    try (final Console console = createConsole("--warnings=error"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Error, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings=HIDE argument", (Test test) ->
                {
                    try (final Console console = createConsole("--warnings=HIDE"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Hide, parameters.getWarnings());
                    }
                });

                runner.test("with --warnings=spam argument", (Test test) ->
                {
                    try (final Console console = createConsole("--warnings=spam"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --buildjson argument", (Test test) ->
                {
                    try (final Console console = createConsole("--buildjson"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --buildjson= argument", (Test test) ->
                {
                    try (final Console console = createConsole("--buildjson="))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --buildjson=false argument", (Test test) ->
                {
                    try (final Console console = createConsole("--buildjson=false"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertFalse(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --buildjson=true argument", (Test test) ->
                {
                    try (final Console console = createConsole("--buildjson=true"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --verbose argument", (Test test) ->
                {
                    try (final Console console = createConsole("--verbose"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertTrue(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --verbose= argument", (Test test) ->
                {
                    try (final Console console = createConsole("--verbose="))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertTrue(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --verbose=false argument", (Test test) ->
                {
                    try (final Console console = createConsole("--verbose=false"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertFalse(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --verbose=true argument", (Test test) ->
                {
                    try (final Console console = createConsole("--verbose=true"))
                    {
                        final QubBuildParameters parameters = QubBuild.getParameters(console);
                        test.assertNotNull(parameters);
                        test.assertEqual(console.getCurrentFolder().await(), parameters.getFolderToBuild());
                        test.assertSame(console.getEnvironmentVariables(), parameters.getEnvironmentVariables());
                        test.assertSame(console.getOutputCharacterWriteStream(), parameters.getOutputCharacterWriteStream());
                        test.assertTrue(parameters.getProcessFactory() instanceof RealProcessFactory);
                        test.assertTrue(parameters.getBuildJson());
                        test.assertTrue(parameters.getVerbose().isVerbose());
                        test.assertEqual(Warnings.Show, parameters.getWarnings());
                    }
                });

                runner.test("with --help argument", (Test test) ->
                {
                    final InMemoryCharacterStream output = new InMemoryCharacterStream();
                    try (final Console console = createConsole(output, "--help"))
                    {
                        test.assertNull(QubBuild.getParameters(console));
                    }
                    test.assertEqual(
                        Iterable.create(
                            "Usage: qub-build [[--folder=]<folder-path-to-build>] [--warnings=<show|error|hide>] [--buildjson] [--verbose] [--profiler] [--help]",
                            "  Used to compile and package source code projects.",
                            "  --folder: The folder to build. The current folder will be used if this isn't defined.",
                            "  --warnings: How to handle build warnings. Can be either \"show\", \"error\", or \"hide\". Defaults to \"show\".",
                            "  --buildjson: Whether or not to read and write a build.json file. Defaults to true.",
                            "  --verbose(v): Whether or not to show verbose logs.",
                            "  --profiler: Whether or not this application should pause before it is run to allow a profiler to be attached.",
                            "  --help(?): Show the help message for this application."),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with -? command line argument", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    try (final Console console = createConsole(output, "-?"))
                    {
                        test.assertNull(QubBuild.getParameters(console));
                    }
                    test.assertEqual(
                        Iterable.create(
                            "Usage: qub-build [[--folder=]<folder-path-to-build>] [--warnings=<show|error|hide>] [--buildjson] [--verbose] [--profiler] [--help]",
                            "  Used to compile and package source code projects.",
                            "  --folder: The folder to build. The current folder will be used if this isn't defined.",
                            "  --warnings: How to handle build warnings. Can be either \"show\", \"error\", or \"hide\". Defaults to \"show\".",
                            "  --buildjson: Whether or not to read and write a build.json file. Defaults to true.",
                            "  --verbose(v): Whether or not to show verbose logs.",
                            "  --profiler: Whether or not this application should pause before it is run to allow a profiler to be attached.",
                            "  --help(?): Show the help message for this application."),
                        Strings.getLines(output.getText().await()));
                });
            });

            runner.testGroup("main(Process)", () ->
            {
                runner.test("with null process", (Test test) ->
                {
                    test.assertThrows(() -> QubBuild.main((Process)null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with no project.json in the unnamed specified folder command line argument", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    try (final Console console = createConsole(output, currentFolder, "/fake/folder/", "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: The file at \"/fake/folder/project.json\" doesn't exist."),
                        Strings.getLines(output.getText().await()).skipLast());
                });

                runner.test("with no project.json in the named specified folder command line argument", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    try (final Console console = createConsole(output, currentFolder, "-folder=/fake/folder/", "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: The file at \"/fake/folder/project.json\" doesn't exist."),
                        Strings.getLines(output.getText().await()).skipLast());
                });

                runner.test("with no project.json in the specified folder with -verbose before folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    try (final Console console = createConsole(output, currentFolder, "-verbose", "/fake/folder/", "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }
                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: The file at \"/fake/folder/project.json\" doesn't exist."),
                        Strings.getLines(output.getText().await()).skipLast());
                });

                runner.test("with no project.json in the current folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: The file at \"/project.json\" doesn't exist."),
                        Strings.getLines(output.getText().await()).skipLast());
                });

                runner.test("with empty project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "");
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: No root was found in the JSON document."),
                        Strings.getLines(output.getText().await()).skipLast());
                });

                runner.test("with empty array project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "[]");
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: Expected the root of the JSON document to be an object."),
                        Strings.getLines(output.getText().await()).skipLast());
                });

                runner.test("with empty object project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().toString());
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: No language specified in project.json. Nothing to compile."),
                        Strings.getLines(output.getText().await()).skipLast());
                });

                runner.test("with java sources version set to \"1.8\" but no \"sources\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: No java source files found in /."),
                        Strings.getLines(output.getText().await()).skipLast());
                });

                runner.test("with empty \"sources\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    currentFolder.createFolder("sources").await();
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }
                    test.assertEqual(
                        Iterable.create(
                            "ERROR: No java source files found in /."),
                        Strings.getLines(output.getText().await()).skipLast());
                });

                runner.test("with non-empty \"sources\" folder and no \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFile("sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", getFileContents(outputs, "A.class"));
                    test.assertEqual(Duration.zero, getFileLastModified(outputs, "A.class").getDurationSinceEpoch());
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(outputs, "build.json"));
                });

                runner.test("with non-empty \"sources\" folder and custom \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "{ \"java\": { \"outputFolder\": \"bin\" } }");
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final Folder bin = currentFolder.getFolder("bin").await();
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(bin)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/bin")
                                .addSourceFile("sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/bin/A.class",
                            "/bin/build.json"),
                        bin.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", getFileContents(bin, "A.class"));
                    test.assertEqual(Duration.zero, getFileLastModified(bin, "A.class").getDurationSinceEpoch());
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()
                                    .setOutputFolder("bin")))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(bin, "build.json"));
                });

                runner.test("with non-empty \"sources\" folder and with existing and empty \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": {} }");
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder.getFile("sources/A.java"), "A.java source");
                    final Folder outputs = currentFolder.createFolder("outputs").await();
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFile("sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", getFileContents(outputs, "A.class"));
                    test.assertEqual(Duration.zero, getFileLastModified(outputs, "A.class").getDurationSinceEpoch());
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(outputs, "build.json"));
                });

                runner.test("with multiple source files and with existing and empty \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": {} }");
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source");
                    final Folder outputs = currentFolder.createFolder("outputs").await();
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java", "sources/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folders");
                    test.assertEqual("A.java bytecode", getFileContents(outputs, "A.class"));
                    test.assertEqual(Duration.zero, getFileLastModified(outputs, "A.class").getDurationSinceEpoch());
                    test.assertEqual("B.java bytecode", getFileContents(outputs, "B.class"));
                    test.assertEqual(Duration.zero, getFileLastModified(outputs, "B.class").getDurationSinceEpoch());
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(outputs, "build.json"));
                });

                runner.test("with multiple source folders", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": {} }");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "tests/B.java", "B.java source");
                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java", "tests/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folders");
                    test.assertEqual("A.java bytecode", getFileContents(outputs, "A.class"));
                    test.assertEqual(Duration.zero, getFileLastModified(outputs, "A.class").getDurationSinceEpoch());
                    test.assertEqual("B.java bytecode", getFileContents(outputs, "B.class"));
                    test.assertEqual(Duration.zero, getFileLastModified(outputs, "B.class").getDurationSinceEpoch());
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                new BuildJSONSourceFile()
                                    .setRelativePath("tests/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(outputs, "build.json"));
                });

                runner.test("with source file with same age as existing class file but no build.json file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File classFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File sourceFile = setFileContents(currentFolder, "sources/A.java", "A.java source");

                    clock.advance(Duration.minutes(1));

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    try (final Console console = createConsole(output, currentFolder, clock, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(classFile));
                    test.assertEqual("A.java bytecode", getFileContents(classFile));
                    final File parseFile = outputs.getFile("build.json").await();
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile));
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(parseFile));
                });

                runner.test("with source file with same age as existing class file and with build.json file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File classFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File sourceFile = setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json",
                        new BuildJSON()
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create())))
                            .toString());

                    final DateTime beforeClockAdvance = clock.getCurrentDateTime();
                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, clock, "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        Strings.getLines(output.getText().await()).skipLast());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(beforeClockAdvance, getFileLastModified(classFile), "Wrong A.class file lastModified");
                    test.assertEqual("A.java source", getFileContents(classFile));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile));
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile));
                });

                runner.test("with source file newer than existing class file and with build.json file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File classFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    clock.advance(Duration.minutes(1));
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File parseFile = setFileContents(currentFolder, "outputs/build.json",
                        new BuildJSON()
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create())))
                            .toString());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(classFile));
                    test.assertEqual("A.java bytecode", getFileContents(classFile));
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))))
                            .toString(),
                        getFileContents(parseFile), "Wrong build.json file contents");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile));
                });

                runner.test("with one source file with one error", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File classFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    clock.advance(Duration.minutes(1));
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File parseFile = setFileContents(currentFolder, "outputs/build.json",
                        new BuildJSON()
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create())))
                            .toString());
                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java")
                                .addCompilerIssues(new JavaCompilerIssue("sources\\A.java", 1, 20, Issue.Type.Error, "This doesn't look right to me."))
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file...",
                            "1 Error:",
                            "sources/A.java (Line 1): This doesn't look right to me."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime().minus(Duration.minutes(1)), getFileLastModified(classFile));
                    test.assertEqual("A.java source", getFileContents(classFile));
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(clock.getCurrentDateTime())
                                    .addIssue(
                                        new JavaCompilerIssue(
                                            "sources/A.java",
                                            1,
                                            20,
                                            Issue.Type.Error,
                                            "This doesn't look right to me."))))
                            .toString(),
                        getFileContents(parseFile), "Wrong build.json file contents");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile));
                });

                runner.test("with one source file with one warning", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File classFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    clock.advance(Duration.minutes(1));
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json",
                        new BuildJSON()
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create())))
                            .toString());
                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java")
                                .addCompilerIssues(new JavaCompilerIssue("sources\\A.java", 1, 20, Issue.Type.Warning, "Are you sure?"))
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file...",
                            "1 Warning:",
                            "sources/A.java (Line 1): Are you sure?"),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(classFile));
                    test.assertEqual("A.java bytecode", getFileContents(classFile));
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(clock.getCurrentDateTime())
                                    .addIssue(new JavaCompilerIssue("sources/A.java", 1, 20, Issue.Type.Warning, "Are you sure?"))))
                            .toString(),
                        getFileContents(buildJsonFile), "Wrong build.json file contents");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile));
                });

                runner.test("with two source files with one error and one warning", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    clock.advance(Duration.minutes(1));
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aTestsSourceFile = setFileContents(currentFolder, "tests/ATests.java", "ATests.java source");
                    final File aTestsClassFile = currentFolder.getFile("outputs/ATests.class").await();
                    final File buildFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                            .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                            .setDependencies(Iterable.create())))
                        .toString());
                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("tests/ATests.java", "sources/A.java")
                                .addCompilerIssues(
                                    new JavaCompilerIssue("sources\\A.java", 1, 5, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue("tests\\ATests.java", 10, 7, Issue.Type.Warning, "Can't be this."))
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files...",
                            "1 Warning:",
                            "tests/ATests.java (Line 10): Can't be this.",
                            "1 Error:",
                            "sources/A.java (Line 1): Are you sure?"),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/ATests.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime().minus(Duration.minutes(1)), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source", getFileContents(aClassFile));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aTestsClassFile));
                    test.assertEqual("ATests.java bytecode", getFileContents(aTestsClassFile));
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("sources/A.java", 1, 5, Issue.Type.Error, "Are you sure?")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("tests/ATests.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("tests/ATests.java", 10, 7, Issue.Type.Warning, "Can't be this."))))
                            .toString(),
                        getFileContents(buildFile), "Wrong build.json file contents");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildFile));
                });

                runner.test("with multiple source files with errors and warnings", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    clock.advance(Duration.minutes(1));
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source");
                    setFileContents(currentFolder, "tests/ATests.java", "ATests.java source");
                    setFileContents(currentFolder, "tests/C.java", "C.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json",
                        new BuildJSON()
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create())))
                            .toString());
                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File aTestsClass = outputs.getFile("ATests.class").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/B.java", "tests/ATests.java", "tests/C.java", "sources/A.java")
                                .addCompilerIssues(
                                    new JavaCompilerIssue("sources\\A.java", 12, 2, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue("sources\\B.java", 1, 5, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue("tests\\C.java", 10, 7, Issue.Type.Warning, "Can't be this."),
                                    new JavaCompilerIssue("tests\\C.java", 20, 7, Issue.Type.Error, "Can't be this."),
                                    new JavaCompilerIssue("tests\\ATests.java", 10, 7, Issue.Type.Warning, "Can't be this."))
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(3, console.getExitCode());
                    }

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
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/ATests.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime().minus(Duration.minutes(1)), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source", getFileContents(aClassFile));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aTestsClass));
                    test.assertEqual("ATests.java bytecode", getFileContents(aTestsClass));
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("sources/A.java", 12, 2, Issue.Type.Error, "Are you sure?")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("sources/B.java", 1, 5, Issue.Type.Error, "Are you sure?")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("tests/ATests.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("tests/ATests.java", 10, 7, Issue.Type.Warning, "Can't be this.")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("tests/C.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .setIssues(Iterable.create(
                                        new JavaCompilerIssue("tests/C.java", 10, 7, Issue.Type.Warning, "Can't be this."),
                                        new JavaCompilerIssue("tests/C.java", 20, 7, Issue.Type.Error, "Can't be this.")))
                            ))
                            .toString(),
                        getFileContents(buildJsonFile), "Wrong build.json file contents");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile));
                });

                runner.test("with multiple source files with errors and warnings and -warnings=show", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    clock.advance(Duration.minutes(1));
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source");
                    setFileContents(currentFolder, "tests/ATests.java", "ATests.java source");
                    setFileContents(currentFolder, "tests/C.java", "C.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json",
                        new BuildJSON()
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create())))
                            .toString());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File aTestsClassFile = outputs.getFile("ATests.class").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson", "-warnings=show"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/B.java", "tests/ATests.java", "tests/C.java", "sources/A.java")
                                .addCompilerIssues(
                                    new JavaCompilerIssue("sources\\A.java", 12, 2, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue("sources\\B.java", 1, 5, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue("tests\\C.java", 10, 7, Issue.Type.Warning, "Can't be this."),
                                    new JavaCompilerIssue("tests\\C.java", 20, 7, Issue.Type.Error, "Can't be this."),
                                    new JavaCompilerIssue("tests\\ATests.java", 10, 7, Issue.Type.Warning, "Can't be this."))
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(3, console.getExitCode());
                    }

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
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/ATests.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime().minus(Duration.minutes(1)), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source", getFileContents(aClassFile));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aTestsClassFile));
                    test.assertEqual("ATests.java bytecode", getFileContents(aTestsClassFile));
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("sources/A.java", 12, 2, Issue.Type.Error, "Are you sure?")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("sources/B.java", 1, 5, Issue.Type.Error, "Are you sure?")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("tests/ATests.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("tests/ATests.java", 10, 7, Issue.Type.Warning, "Can't be this.")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("tests/C.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .setIssues(Iterable.create(
                                        new JavaCompilerIssue("tests/C.java", 10, 7, Issue.Type.Warning, "Can't be this."),
                                        new JavaCompilerIssue("tests/C.java", 20, 7, Issue.Type.Error, "Can't be this.")))
                            ))
                            .toString(),
                        getFileContents(buildJsonFile), "Wrong build.json file contents");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile));
                });

                runner.test("with multiple source files with errors and warnings and -warnings=hide", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    clock.advance(Duration.minutes(1));
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source");
                    setFileContents(currentFolder, "tests/ATests.java", "ATests.java source");
                    setFileContents(currentFolder, "tests/C.java", "C.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json",
                        new BuildJSON()
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create())))
                            .toString());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File aTestsClassFile = outputs.getFile("ATests.class").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson", "-warnings=hide"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/B.java", "tests/ATests.java", "tests/C.java", "sources/A.java")
                                .addCompilerIssues(
                                    new JavaCompilerIssue("sources\\A.java", 12, 2, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue("sources\\B.java", 1, 5, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue("tests\\C.java", 10, 7, Issue.Type.Warning, "Can't be this."),
                                    new JavaCompilerIssue("tests\\C.java", 20, 7, Issue.Type.Error, "Can't be this."),
                                    new JavaCompilerIssue("tests\\ATests.java", 10, 7, Issue.Type.Warning, "Can't be this."))
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(3, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 4 files...",
                            "3 Errors:",
                            "sources/A.java (Line 12): Are you sure?",
                            "sources/B.java (Line 1): Are you sure?",
                            "tests/C.java (Line 20): Can't be this."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/ATests.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime().minus(Duration.minutes(1)), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source", getFileContents(aClassFile));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aTestsClassFile));
                    test.assertEqual("ATests.java bytecode", getFileContents(aTestsClassFile));
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("sources/A.java", 12, 2, Issue.Type.Error, "Are you sure?")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("sources/B.java", 1, 5, Issue.Type.Error, "Are you sure?")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("tests/ATests.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1))),
                                new BuildJSONSourceFile()
                                    .setRelativePath("tests/C.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .setIssues(Iterable.create(
                                        new JavaCompilerIssue("tests/C.java", 20, 7, Issue.Type.Error, "Can't be this.")))
                            ))
                            .toString(),
                        getFileContents(buildJsonFile), "Wrong build.json file contents");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile));
                });

                runner.test("with multiple source files with errors and warnings and -warnings=error", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    clock.advance(Duration.minutes(1));
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source");
                    setFileContents(currentFolder, "tests/ATests.java", "ATests.java source");
                    setFileContents(currentFolder, "tests/C.java", "C.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json",
                        new BuildJSON()
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create())))
                            .toString());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File aTestsClassFile = outputs.getFile("ATests.class").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson", "-warnings=error"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/B.java", "tests/ATests.java", "tests/C.java", "sources/A.java")
                                .addCompilerIssues(
                                    new JavaCompilerIssue("sources\\A.java", 12, 2, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue("sources\\B.java", 1, 5, Issue.Type.Error, "Are you sure?"),
                                    new JavaCompilerIssue("tests\\C.java", 10, 7, Issue.Type.Warning, "Can't be this."),
                                    new JavaCompilerIssue("tests\\C.java", 20, 7, Issue.Type.Error, "Can't be this."),
                                    new JavaCompilerIssue("tests\\ATests.java", 10, 7, Issue.Type.Warning, "Can't be this."))
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(3, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 4 files...",
                            "5 Errors:",
                            "sources/A.java (Line 12): Are you sure?",
                            "sources/B.java (Line 1): Are you sure?",
                            "tests/ATests.java (Line 10): Can't be this.",
                            "tests/C.java (Line 20): Can't be this.",
                            "tests/C.java (Line 10): Can't be this."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/ATests.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime().minus(Duration.minutes(1)), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source", getFileContents(aClassFile));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aTestsClassFile));
                    test.assertEqual("ATests.java bytecode", getFileContents(aTestsClassFile));
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("sources/A.java", 12, 2, Issue.Type.Error, "Are you sure?")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("sources/B.java", 1, 5, Issue.Type.Error, "Are you sure?")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("tests/ATests.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .addIssue(new JavaCompilerIssue("tests/ATests.java", 10, 7, Issue.Type.Error, "Can't be this.")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("tests/C.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .setIssues(Iterable.create(
                                        new JavaCompilerIssue("tests/C.java", 20, 7, Issue.Type.Error, "Can't be this."),
                                        new JavaCompilerIssue("tests/C.java", 10, 7, Issue.Type.Error, "Can't be this.")))
                            ))
                            .toString(),
                        getFileContents(buildJsonFile), "Wrong build.json file contents");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile));
                });

                runner.test("with multiple source files newer than their existing class files and with build.json file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(clock.getCurrentDateTime())
                                .setDependencies(Iterable.create()),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(clock.getCurrentDateTime())
                                .setDependencies(Iterable.create(Path.parse("sources/A.java")))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on A");
                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java", "sources/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java bytecode", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(clock.getCurrentDateTime()),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(Path.parse("sources/A.java")))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with one modified source file and another unmodified and undependant source file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(clock.getCurrentDateTime())
                                .setDependencies(Iterable.create()),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(clock.getCurrentDateTime())
                                .setDependencies(Iterable.create())))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/B.java", "B.java source");
                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java bytecode", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with one modified source file and another unmodified and dependant source file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(clock.getCurrentDateTime())
                                .setDependencies(Iterable.create(Path.parse("sources/B.java"))),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(clock.getCurrentDateTime())
                                .setDependencies(Iterable.create())))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/B.java", "B.java source");
                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/B.java", "sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java bytecode", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(Path.parse("sources/B.java"))),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with one unmodified source file and another modified and dependant source file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create()),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create())))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");
                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(Duration.zero, getFileLastModified(bClassFile).getDurationSinceEpoch());
                    test.assertEqual("B.java source", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(clock.getCurrentDateTime())
                                    .setDependencies(Iterable.create(Path.parse("sources/B.java"))),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with one deleted source file and another unmodified and dependant source file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": { \"shortcutName\": \"foo\" } }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source, depends on B");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create(Path.parse("sources/B.java"))),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create())))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(false, bClassFile.exists().await(),
                        "Class file of deleted source file should have been deleted.");

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()
                                    .setShortcutName("foo")))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(Path.parse("sources/B.java")))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with one new source file and another unmodified and undependant source file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create())))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/B.java", "B.java source");
                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File bClassFile = outputs.getFile("B.class").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(Duration.minutes(1), getFileLastModified(bClassFile).getDurationSinceEpoch());
                    test.assertEqual("B.java bytecode", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("N depends on nothing, A depends on B, B depends on C, C.java is modified: A, B, and C should be compiled", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    setFileContents(currentFolder, "sources/N.java", "N.java source");
                    setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on C");

                    final File nClassFile = setFileContents(currentFolder, "outputs/N.class", "N.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source, depends on B");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source, depends on C");
                    final File cClassFile = setFileContents(currentFolder, "outputs/C.class", "C.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/N.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create(Path.parse("sources/B.java"))),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create(Path.parse("sources/C.java"))),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/C.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/C.java", "C.java source");
                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/C.java", "sources/B.java", "sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 3 files..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/C.class",
                            "/outputs/N.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(nClassFile).getDurationSinceEpoch());
                    test.assertEqual("N.java source", getFileContents(nClassFile));

                    test.assertEqual(Duration.minutes(1), getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(Duration.minutes(1), getFileLastModified(bClassFile).getDurationSinceEpoch());
                    test.assertEqual("B.java bytecode", getFileContents(bClassFile));

                    test.assertEqual(Duration.minutes(1), getFileLastModified(cClassFile).getDurationSinceEpoch());
                    test.assertEqual("C.java bytecode", getFileContents(cClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(Path.parse("sources/B.java"))),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(Path.parse("sources/C.java"))),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/C.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1))),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/N.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("N depends on nothing, A depends on B, B depends on C, C.java is deleted: A, B, and C should be compiled", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    setFileContents(currentFolder, "sources/N.java", "N.java source");
                    setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on C");

                    final File nClassFile = setFileContents(currentFolder, "outputs/N.class", "N.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source, depends on B");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source, depends on C");
                    final File cClassFile = setFileContents(currentFolder, "outputs/C.class", "C.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/N.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create(Path.parse("sources/B.java"))),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create(Path.parse("sources/C.java"))),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/C.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/B.java", "sources/A.java")
                                .addCompilerIssues(
                                    new JavaCompilerIssue("sources\\B.java", 1, 25, Issue.Type.Error, "Missing definition for C."))
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files...",
                            "1 Error:",
                            "sources/B.java (Line 1): Missing definition for C."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/N.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(nClassFile).getDurationSinceEpoch());
                    test.assertEqual("N.java source", getFileContents(nClassFile));

                    test.assertEqual(Duration.minutes(1), getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(Duration.zero, getFileLastModified(bClassFile).getDurationSinceEpoch());
                    test.assertEqual("B.java source, depends on C", getFileContents(bClassFile));

                    test.assertFalse(cClassFile.exists().await());

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(Path.parse("sources/B.java"))),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(Path.parse("sources/C.java")))
                                    .addIssue(new JavaCompilerIssue("sources/B.java", 1, 25, Issue.Type.Error, "Missing definition for C.")),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/N.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("N depends on nothing, A depends on B, B depends on C, C.class is deleted: C should be compiled", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    setFileContents(currentFolder, "sources/N.java", "N.java source");
                    setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on C");
                    setFileContents(currentFolder, "sources/C.java", "C.java source");

                    final File nClassFile = setFileContents(currentFolder, "outputs/N.class", "N.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source, depends on B");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source, depends on C");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/N.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create(Path.parse("sources/B.java"))),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create(Path.parse("sources/C.java"))),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/C.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File cClassFile = outputs.getFile("C.class").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFile("sources/C.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/C.class",
                            "/outputs/N.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(nClassFile).getDurationSinceEpoch());
                    test.assertEqual("N.java source", getFileContents(nClassFile));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source, depends on B", getFileContents(aClassFile));

                    test.assertEqual(Duration.zero, getFileLastModified(bClassFile).getDurationSinceEpoch());
                    test.assertEqual("B.java source, depends on C", getFileContents(bClassFile));

                    test.assertEqual(Duration.minutes(1), getFileLastModified(cClassFile).getDurationSinceEpoch());
                    test.assertEqual("C.java bytecode", getFileContents(cClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(Path.parse("sources/B.java"))),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(Path.parse("sources/C.java"))),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/C.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/N.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with no QUB_HOME environment variable specified", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setEnvironmentVariables(new EnvironmentVariables());
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "ERROR: A QUB_HOME folder path environment variable must be specified."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertFalse(currentFolder.getFolder("outputs").await().exists().await());
                });

                runner.test("nothing gets compiled when project.json publisher changes", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setProjectJson(new ProjectJSON()
                            .setPublisher("a")
                            .setJava(new ProjectJSONJava()))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setPublisher("b")
                        .setJava(new ProjectJSONJava())
                        .toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        Strings.getLines(output.getText().await()).skipLast());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setPublisher("b")
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("nothing gets compiled when project.json project changes", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setProjectJson(new ProjectJSON()
                            .setProject("a")
                            .setJava(new ProjectJSONJava()))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setProject("b")
                        .setJava(new ProjectJSONJava())
                        .toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        Strings.getLines(output.getText().await()).skipLast());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setProject("b")
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("nothing gets compiled when project.json version changes", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setProjectJson(new ProjectJSON()
                            .setVersion("a")
                            .setJava(new ProjectJSONJava()))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setVersion("b")
                        .setJava(new ProjectJSONJava())
                        .toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        Strings.getLines(output.getText().await()).skipLast());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setVersion("b")
                                .setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("everything gets compiled when project.json java version changes from 11 to 1.8", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    final Folder javaFolder = currentFolder.getFileSystem().createFolder("/java/").await();
                    final Folder jdk11Folder = javaFolder.createFolder("jdk-11.0.1").await();
                    javaFolder.createFolder("jre1.8.0_192");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setProjectJson(new ProjectJSON()
                            .setJava(new ProjectJSONJava()
                                .setVersion("11")))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setJava(new ProjectJSONJava()
                            .setVersion("1.8"))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console
                            .setEnvironmentVariables(new EnvironmentVariables()
                                .set("JAVA_HOME", jdk11Folder.toString())
                                .set("QUB_HOME", "/qub/"))
                            .setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                                .add(new FakeJavacProcessRun()
                                    .setWorkingFolder(currentFolder)
                                    .addOutputFolder(outputs)
                                    .addXlintUnchecked()
                                    .addXlintDeprecation()
                                    .addJavaSourceVersion("1.8")
                                    .addJavaTargetVersion("1.8")
                                    .addBootClasspath("/java/jre1.8.0_192/lib/rt.jar")
                                    .addClasspath("/outputs")
                                    .addSourceFile("sources/A.java")
                                    .setFunctionAutomatically()));

                        QubBuild.main(console);

                        test.assertEqual(
                            Iterable.create(
                                "Compiling 1 file..."),
                            Strings.getLines(output.getText().await()).skipLast());

                         test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.seconds(60), getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()
                                    .setVersion("1.8")))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("everything gets compiled when project.json java version changes from 11 to 8", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    final Folder javaFolder = currentFolder.getFileSystem().createFolder("/java/").await();
                    final Folder jdk11Folder = javaFolder.createFolder("jdk-11.0.1").await();
                    javaFolder.createFolder("jre1.8.0_192");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setProjectJson(new ProjectJSON()
                            .setJava(new ProjectJSONJava()
                                .setVersion("11")))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setJava(new ProjectJSONJava()
                            .setVersion("8"))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console
                            .setEnvironmentVariables(new EnvironmentVariables()
                                .set("JAVA_HOME", jdk11Folder.toString())
                                .set("QUB_HOME", "/qub/"))
                            .setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                                .add(new FakeJavacProcessRun()
                                    .setWorkingFolder(currentFolder)
                                    .addOutputFolder(outputs)
                                    .addXlintUnchecked()
                                    .addXlintDeprecation()
                                    .addJavaSourceVersion("8")
                                    .addJavaTargetVersion("8")
                                    .addBootClasspath("/java/jre1.8.0_192/lib/rt.jar")
                                    .addClasspath("/outputs")
                                    .addSourceFile("sources/A.java")
                                    .setFunctionAutomatically()));
                        QubBuild.main(console);
                         test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.seconds(60), getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()
                                    .setVersion("8")))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("nothing gets compiled when project.json java version changes from 1.8 to 8", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    final Folder javaFolder = currentFolder.getFileSystem().createFolder("/java/").await();
                    final Folder jdk11Folder = javaFolder.createFolder("jdk-11.0.1").await();
                    javaFolder.createFolder("jre1.8.0_192");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setProjectJson(new ProjectJSON()
                            .setJava(new ProjectJSONJava()
                                .setVersion("1.8")))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setJava(new ProjectJSONJava()
                            .setVersion("8"))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console
                            .setEnvironmentVariables(new EnvironmentVariables()
                                .set("JAVA_HOME", jdk11Folder.toString())
                                .set("QUB_HOME", "/qub/"));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        Strings.getLines(output.getText().await()).skipLast());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()
                                    .setVersion("8")))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("nothing gets compiled when project.json java version changes from 8 to 1.8", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    final Folder javaFolder = currentFolder.getFileSystem().createFolder("/java/").await();
                    final Folder jdk11Folder = javaFolder.createFolder("jdk-11.0.1").await();
                    javaFolder.createFolder("jre1.8.0_192");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setProjectJson(new ProjectJSON()
                            .setJava(new ProjectJSONJava()
                                .setVersion("8")))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setJava(new ProjectJSONJava()
                            .setVersion("1.8"))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console
                            .setEnvironmentVariables(new EnvironmentVariables()
                                .set("JAVA_HOME", jdk11Folder.toString())
                                .set("QUB_HOME", "/qub/"));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        Strings.getLines(output.getText().await()).skipLast());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()
                                    .setVersion("1.8")))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("nothing gets compiled when project.json java dependency is added", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setProjectJson(new ProjectJSON()
                            .setJava(new ProjectJSONJava()))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setJava(new ProjectJSONJava()
                            .setDependencies(Iterable.create(
                                new ProjectSignature("a", "b", "c"))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, clock, "-buildjson"))
                    {
                        final Folder qubFolder = console.getFileSystem().getFolder("/qub/").await();
                        console.setEnvironmentVariables(new EnvironmentVariables()
                            .set("QUB_HOME", qubFolder.toString()));
                        qubFolder.createFile("a/b/c/b.jar").await();

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "No files need to be compiled."),
                        Strings.getLines(output.getText().await()).skipLast());

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()
                                    .setDependencies(Iterable.create(
                                        new ProjectSignature("a", "b", "c")))))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("everything gets compiled when project.json java dependency is removed", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setProjectJson(new ProjectJSON()
                            .setJava(new ProjectJSONJava()
                                .setDependencies(Iterable.create(
                                    new ProjectSignature("a", "b", "c")))))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                            .setRelativePath("sources/A.java")
                            .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());

                    clock.advance(Duration.minutes(1));

                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console
                            .setEnvironmentVariables(new EnvironmentVariables()
                                .set("QUB_HOME", "/qub/"))
                            .setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                                .add(new FakeJavacProcessRun()
                                    .setWorkingFolder(currentFolder)
                                    .addOutputFolder(outputs)
                                    .addXlintUnchecked()
                                    .addXlintDeprecation()
                                    .addClasspath("/outputs")
                                    .addSourceFile("sources/A.java")
                                    .setFunctionAutomatically()));
                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.seconds(60), getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("everything gets compiled when project.json java dependency version is changed", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setProjectJson(new ProjectJSON()
                            .setJava(new ProjectJSONJava()
                                .setDependencies(Iterable.create(
                                    new ProjectSignature("a", "b", "c")))))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                            .setRelativePath("sources/A.java")
                            .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setJava(new ProjectJSONJava()
                            .setDependencies(Iterable.create(
                                new ProjectSignature("a", "b", "d"))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        final Folder qubFolder = console.getFileSystem().getFolder("/qub/").await();
                        qubFolder.createFile("a/b/d/b.jar").await();

                        console
                            .setEnvironmentVariables(new EnvironmentVariables()
                                .set("QUB_HOME", qubFolder.toString()))
                            .setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                                .add(new FakeJavacProcessRun()
                                    .setWorkingFolder(currentFolder)
                                    .addOutputFolder(outputs)
                                    .addXlintUnchecked()
                                    .addXlintDeprecation()
                                    .addClasspath(Iterable.create("/outputs", "/qub/a/b/d/b.jar"))
                                    .addSourceFile("sources/A.java")
                                    .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.seconds(60), getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON()
                                .setJava(new ProjectJSONJava()
                                .setDependencies(Iterable.create(
                                    new ProjectSignature("a", "b", "d")))))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with multiple source files newer than their existing class files and with build.json file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create()),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create(Path.parse("sources/A.java")))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on A");
                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java", "sources/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 2 files..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java bytecode", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1))),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))
                                    .setDependencies(Iterable.create(Path.parse("sources/A.java")))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with deleted source file and --verbose", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "--verbose"))
                    {
                        QubBuild.main(console);

                        test.assertEqual(
                            Iterable.create(
                                "VERBOSE: Parsing project.json...",
                                "VERBOSE: Parsing outputs/build.json...",
                                "VERBOSE: /sources/A.java - No changes or issues",
                                "VERBOSE: Deleted source files:",
                                "VERBOSE: /sources/B.java",
                                "VERBOSE: Updating outputs/build.json...",
                                "VERBOSE: Setting project.json...",
                                "VERBOSE: Setting source files...",
                                "VERBOSE: Detecting java source files to compile...",
                                "No files need to be compiled.",
                                "VERBOSE: Writing build.json file...",
                                "VERBOSE: Done writing build.json file."),
                            Strings.getLines(output.getText().await()).skipLast());

                        test.assertEqual(0, console.getExitCode());
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertFalse(bClassFile.exists().await());

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with new source file and --verbose", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                            .setRelativePath("sources/A.java")
                            .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                            .setDependencies(Iterable.create())))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/B.java", "B.java source");

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File bClassFile = outputs.getFile("B.class").await();

                    try (final Console console = createConsole(output, currentFolder, "--verbose"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFile("sources/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "VERBOSE: Parsing outputs/build.json...",
                            "VERBOSE: /sources/A.java - No changes or issues",
                            "VERBOSE: /sources/B.java - New file",
                            "VERBOSE: Updating outputs/build.json...",
                            "VERBOSE: Setting project.json...",
                            "VERBOSE: Setting source files...",
                            "VERBOSE: Detecting java source files to compile...",
                            "VERBOSE: Added source files:",
                            "VERBOSE: /sources/B.java",
                            "Compiling 1 file...",
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs sources/B.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java bytecode", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                        .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))))
                        .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with modified source file and --verbose", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "--verbose"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFile("sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
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
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs sources/A.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.seconds(60), getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.minutes(1)))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with unmodified source file with issues and --verbose", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                            .setRelativePath("sources/A.java")
                            .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                            .setIssues(Iterable.create(
                                new JavaCompilerIssue("sources/A.java", 12, 2, Issue.Type.Error, "Are you sure?")))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "--verbose"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFile("sources/A.java")
                                .addCompilerIssues(
                                    new JavaCompilerIssue("sources\\A.java", 12, 2, Issue.Type.Error, "Are you sure?"))
                                .setFunctionAutomatically()));

                        QubBuild.main(console);

                        test.assertEqual(
                            Iterable.create(
                                "VERBOSE: Parsing project.json...",
                                "VERBOSE: Parsing outputs/build.json...",
                                "VERBOSE: /sources/A.java - Has issues",
                                "VERBOSE: Updating outputs/build.json...",
                                "VERBOSE: Setting project.json...",
                                "VERBOSE: Setting source files...",
                                "VERBOSE: Detecting java source files to compile...",
                                "VERBOSE: Source files that previously contained issues:",
                                "VERBOSE: /sources/A.java",
                                "Compiling 1 file...",
                                "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs sources/A.java...",
                                "VERBOSE: Compilation finished.",
                                "1 Error:",
                                "sources/A.java (Line 12): Are you sure?",
                                "VERBOSE: Writing build.json file...",
                                "VERBOSE: Done writing build.json file."),
                            Strings.getLines(output.getText().await()).skipLast());

                        test.assertEqual(1, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setIssues(Iterable.create(
                                    new JavaCompilerIssue("sources/A.java", 12, 2, Issue.Type.Error, "Are you sure?")))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with unmodified source file with deleted dependency and --verbose", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on A");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source, depends on A");
                    final File buildJsonFile = setFileContents(currentFolder, "outputs/build.json", new BuildJSON()
                        .setSourceFiles(Iterable.create(
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/A.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                            new BuildJSONSourceFile()
                                .setRelativePath("sources/B.java")
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                .setDependencies(Iterable.create(Path.parse("sources/A.java")))))
                        .toString());

                    clock.advance(Duration.minutes(1));

                    final Folder outputs = currentFolder.getFolder("outputs").await();

                    try (final Console console = createConsole(output, currentFolder, "--verbose"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFile("sources/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
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
                            "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs sources/B.java...",
                            "VERBOSE: Compilation finished.",
                            "VERBOSE: Writing build.json file...",
                            "VERBOSE: Done writing build.json file."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertFalse(aClassFile.exists().await());

                    test.assertEqual(Duration.seconds(60), bClassFile.getLastModified().await().getDurationSinceEpoch());

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(buildJsonFile), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(Path.parse("sources/A.java")))))
                            .toString(),
                        getFileContents(buildJsonFile),
                        "Wrong build.json file contents");
                });

                runner.test("with partial-name dependency match and -buildjson", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/AB.java", "AB.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on AB");

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File aClassFile = outputs.getFile("A.class").await();
                    final File abClassFile = outputs.getFile("AB.class").await();
                    final File bClassFile = outputs.getFile("B.class").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java", "sources/AB.java", "sources/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 3 files..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/AB.class",
                            "/outputs/B.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(Duration.zero, getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(Duration.zero, getFileLastModified(abClassFile).getDurationSinceEpoch());
                    test.assertEqual("AB.java bytecode", getFileContents(abClassFile));

                    test.assertEqual(Duration.zero, getFileLastModified(bClassFile).getDurationSinceEpoch());
                    test.assertEqual("B.java bytecode", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(outputs, "build.json"), "Wrong build.json file lastModified");
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/AB.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero)),
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/B.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))
                                    .setDependencies(Iterable.create(Path.parse("sources/AB.java")))))
                            .toString(),
                        getFileContents(outputs, "build.json"),
                        "Wrong build.json file contents");
                });

                runner.test("with multiple source files and -buildjson=false", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on A");

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File aClassFile = outputs.getFile("A.class").await();
                    final File bClassFile = outputs.getFile("B.class").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson=false"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java", "sources/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);

                        test.assertEqual(
                            Iterable.create(
                                "Compiling 2 files..."),
                            Strings.getLines(output.getText().await()).skipLast());

                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java bytecode", getFileContents(bClassFile));

                    test.assertFalse(outputs.fileExists("build.json").await());
                });

                runner.test("with existing outputs folder and -buildjson=false", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on A");

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    outputs.createFile("blah.txt").await();
                    final File aClassFile = outputs.getFile("A.class").await();
                    final File bClassFile = outputs.getFile("B.class").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson=false"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java", "sources/B.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);

                        test.assertEqual(
                            Iterable.create(
                                "Compiling 2 files..."),
                            Strings.getLines(output.getText().await()).skipLast());

                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java bytecode", getFileContents(bClassFile));

                    test.assertFalse(outputs.fileExists("build.json").await());
                });

                runner.test("with project.json dependency with publisher that doesn't exist", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setProject("fake-project")
                        .setJava(new ProjectJSONJava()
                            .setDependencies(Iterable.create(
                                new ProjectSignature("fake-qub", "qub-java", "1"))))
                        .toString());
                    setFileContents(currentFolder, "sources/A.java", "A.java source");

                    try (final Console console = createConsole(output, currentFolder, "-buildjson=false"))
                    {
                        console.setEnvironmentVariables(new EnvironmentVariables()
                            .set("QUB_HOME", "/qub_home/"));
                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "ERROR: No publisher folder named \"fake-qub\" found in the Qub folder (/qub_home/)."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertFalse(currentFolder.folderExists("outputs").await());
                });

                runner.test("with project.json dependency with project that doesn't exist", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON()
                        .setProject("fake-project")
                        .setJava(new ProjectJSONJava()
                            .setDependencies(Iterable.create(
                                new ProjectSignature("qub", "fake-qub-java", "1"))))
                        .toString());
                    setFileContents(currentFolder, "sources/A.java", "A.java source");

                    final Folder qubFolder = currentFolder.getFileSystem().createFolder("/qub_home/").await();
                    qubFolder.createFolder("qub").await();

                    try (final Console console = createConsole(output, currentFolder, "-buildjson=false"))
                    {
                        console.setEnvironmentVariables(new EnvironmentVariables()
                            .set("QUB_HOME", qubFolder.toString()));
                        QubBuild.main(console);

                        test.assertEqual(
                            Iterable.create(
                                "ERROR: No project folder named \"fake-qub-java\" found in the \"qub\" publisher folder (/qub_home/qub)."),
                            Strings.getLines(output.getText().await()).skipLast());

                        test.assertEqual(1, console.getExitCode());
                    }

                    test.assertFalse(currentFolder.folderExists("outputs").await());
                });

                runner.test("with transitive dependency", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    final ProjectJSON aProjectJSON = new ProjectJSON()
                        .setProject("a")
                        .setPublisher("me")
                        .setVersion("1")
                        .setJava(new ProjectJSONJava());
                    final ProjectJSON bProjectJSON = new ProjectJSON()
                        .setProject("b")
                        .setPublisher("me")
                        .setVersion("2")
                        .setJava(new ProjectJSONJava()
                            .setDependencies(Iterable.create(
                                new ProjectSignature("me", "a", "1"))));
                    final ProjectJSON cProjectJson = new ProjectJSON()
                        .setProject("c")
                        .setPublisher("me")
                        .setVersion("3")
                        .setJava(new ProjectJSONJava()
                            .setDependencies(Iterable.create(
                                new ProjectSignature("me", "b", "2"))));
                    setFileContents(currentFolder, "project.json", cProjectJson.toString());
                    setFileContents(currentFolder, "sources/A.java", "A.java source");

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File aClassFile = outputs.getFile("A.class").await();

                    try (final Console console = createConsole(output, currentFolder, "-verbose"))
                    {
                        console.setEnvironmentVariables(new EnvironmentVariables()
                            .set("QUB_HOME", "/qub/"));
                        final Folder publisherFolder = console.getFileSystem().getFolder("/qub/me/").await();
                        publisherFolder.create().await();
                        publisherFolder.setFileContentsAsString("b/2/project.json", bProjectJSON.toString()).await();
                        publisherFolder.createFile("b/2/b.jar").await();
                        publisherFolder.setFileContentsAsString("a/1/project.json", aProjectJSON.toString()).await();
                        publisherFolder.createFile("a/1/a.jar").await();

                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath(Iterable.create("/outputs", "/qub/me/b/2/b.jar", "/qub/me/a/1/a.jar"))
                                .addSourceFile("sources/A.java")
                                .setFunctionAutomatically()));

                        QubBuild.main(console);

                        test.assertEqual(
                            Iterable.create(
                                "VERBOSE: Parsing project.json...",
                                "VERBOSE: Updating outputs/build.json...",
                                "VERBOSE: Setting project.json...",
                                "VERBOSE: Setting source files...",
                                "VERBOSE: Detecting java source files to compile...",
                                "VERBOSE: Compiling all source files.",
                                "Compiling 1 file...",
                                "VERBOSE: Running /: javac -d outputs -Xlint:unchecked -Xlint:deprecation -classpath /outputs;/qub/me/b/2/b.jar;/qub/me/a/1/a.jar sources/A.java...",
                                "VERBOSE: Compilation finished.",
                                "VERBOSE: Writing build.json file...",
                                "VERBOSE: Done writing build.json file."),
                            Strings.getLines(output.getText().await()).skipLast());

                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs
                            .getFilesAndFoldersRecursively().await()
                            .map(FileSystemEntry::toString));
                });

                runner.test("with multiple versions of same project dependency", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    final ProjectJSON a1ProjectJSON = new ProjectJSON()
                        .setProject("a")
                        .setPublisher("me")
                        .setVersion("1")
                        .setJava(new ProjectJSONJava());
                    final ProjectJSON a2ProjectJSON = new ProjectJSON()
                        .setProject("a")
                        .setPublisher("me")
                        .setVersion("2")
                        .setJava(new ProjectJSONJava());
                    final ProjectJSON bProjectJSON = new ProjectJSON()
                        .setProject("b")
                        .setPublisher("me")
                        .setVersion("2")
                        .setJava(new ProjectJSONJava()
                            .setDependencies(Iterable.create(
                                new ProjectSignature("me", "a", "2"))));
                    final ProjectJSON cProjectJson = new ProjectJSON()
                        .setProject("c")
                        .setPublisher("me")
                        .setVersion("3")
                        .setJava(new ProjectJSONJava()
                            .setDependencies(Iterable.create(
                                new ProjectSignature("me", "a", "1"),
                                new ProjectSignature("me", "b", "2"))));
                    currentFolder.getFile("project.json").await()
                        .setContentsAsString(cProjectJson.toString());
                    setFileContents(currentFolder, "sources/A.java", "A.java source");

                    try (final Console console = createConsole(output, currentFolder, "-verbose"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()));
                        console.setEnvironmentVariables(new EnvironmentVariables()
                            .set("QUB_HOME", "/qub/"));
                        final Folder publisherFolder = console.getFileSystem().getFolder("/qub/me/").await();
                        publisherFolder.create().await();
                        publisherFolder.setFileContentsAsString("b/2/project.json", bProjectJSON.toString()).await();
                        publisherFolder.createFile("b/2/b.jar").await();
                        publisherFolder.setFileContentsAsString("a/1/project.json", a1ProjectJSON.toString()).await();
                        publisherFolder.createFile("a/1/a.jar").await();
                        publisherFolder.setFileContentsAsString("a/2/project.json", a2ProjectJSON.toString()).await();
                        publisherFolder.createFile("a/2/a.jar").await();

                        QubBuild.main(console);
                        test.assertEqual(1, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "VERBOSE: Parsing project.json...",
                            "ERROR: Found more than one required version for package me/a:",
                            "1. me/a@1",
                            "2. me/a@2",
                            "    from me/b@2",
                            ""),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertFalse(currentFolder.folderExists("outputs").await());
                });

                runner.test("with source file modified during build", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", new ProjectJSON().setJava(new ProjectJSONJava()).toString());
                    currentFolder.createFolder("sources");
                    final File aJavaFile = setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    final File aClassFile = outputs.getFile("A.class").await();

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-buildjson"))
                    {
                        console.setProcessFactory(new FakeProcessFactory(test.getParallelAsyncRunner(), console.getCurrentFolderPath())
                            .add(new FakeJavacProcessRun()
                                .setWorkingFolder(currentFolder)
                                .addOutputFolder(outputs)
                                .addXlintUnchecked()
                                .addXlintDeprecation()
                                .addClasspath("/outputs")
                                .addSourceFilePathStrings("sources/A.java")
                                .setFunction(() ->
                                {
                                    aClassFile.setContentsAsString("A.java bytecode").await();
                                    clock.advance(Duration.seconds(1));
                                    aJavaFile.setContentsAsString("A.java source 2").await();
                                })));

                        QubBuild.main(console);
                        test.assertEqual(0, console.getExitCode());
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling 1 file..."),
                        Strings.getLines(output.getText().await()).skipLast());

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/build.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java bytecode", getFileContents(aClassFile));
                    test.assertEqual(Duration.seconds(60), getFileLastModified(aClassFile).getDurationSinceEpoch());
                    test.assertEqual("A.java source 2", getFileContents(aJavaFile));
                    test.assertEqual(Duration.seconds(61), getFileLastModified(aJavaFile).getDurationSinceEpoch());
                    test.assertEqual(
                        new BuildJSON()
                            .setProjectJson(new ProjectJSON().setJava(new ProjectJSONJava()))
                            .setSourceFiles(Iterable.create(
                                new BuildJSONSourceFile()
                                    .setRelativePath("sources/A.java")
                                    .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.zero))))
                            .toString(),
                        getFileContents(outputs, "build.json"));
                });
            });
        });
    }

    static ManualClock getManualClock(Test test)
    {
        PreCondition.assertNotNull(test, "test");

        return new ManualClock(DateTime.createFromDurationSinceEpoch(Duration.zero), test.getMainAsyncRunner());
    }

    static InMemoryCharacterStream getInMemoryCharacterStream(Test test)
    {
        return new InMemoryCharacterStream();
    }

    static Folder getInMemoryCurrentFolder(Test test)
    {
        return getInMemoryCurrentFolder(test, getManualClock(test));
    }

    static Folder getInMemoryCurrentFolder(Test test, Clock clock)
    {
        PreCondition.assertNotNull(test, "test");
        PreCondition.assertNotNull(clock, "clock");

        final InMemoryFileSystem fileSystem = new InMemoryFileSystem(clock);
        fileSystem.createRoot("/").await();
        return fileSystem.getFolder("/").await();
    }

    static File setFileContents(Folder folder, String relativeFilePath, String contents)
    {
        final File file = folder.getFile(relativeFilePath).await();
        setFileContents(file, contents);
        return file;
    }

    static void setFileContents(Result<File> fileResult, String contents)
    {
        fileResult.then((File file) -> setFileContents(file, contents)).await();
    }

    static void setFileContents(File file, String contents)
    {
        final byte[] byteContents = Strings.isNullOrEmpty(contents)
            ? new byte[0]
            : CharacterEncoding.UTF_8.encode(contents).await();
        file.setContents(byteContents).await();
    }

    static String getFileContents(Folder folder, String relativeFilePath)
    {
        return getFileContents(folder.getFile(relativeFilePath));
    }

    static DateTime getFileLastModified(Folder folder, String relativeFilePath)
    {
        return getFileLastModified(folder.getFile(relativeFilePath).await());
    }

    static DateTime getFileLastModified(File file)
    {
        return file.getLastModified().await();
    }

    static String getFileContents(Result<File> file)
    {
        return getFileContents(file.await());
    }

    static String getFileContents(File file)
    {
        return file.getContentByteReadStream()
            .then((ByteReadStream contents) -> contents.asCharacterReadStream())
            .thenResult(CharacterReadStream::readEntireString)
            .await();
    }

    static Console createConsole(CharacterWriteStream output, Folder currentFolder, Clock clock, String... commandLineArguments)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(currentFolder, "currentFolder");
        PreCondition.assertNotNull(clock, "clock");
        PreCondition.assertNotNull(commandLineArguments, "commandLineArguments");

        final Console result = createConsole(output, currentFolder, commandLineArguments);
        result.setClock(clock);

        return result;
    }

    static Console createConsole(CharacterWriteStream output, Folder currentFolder, String... commandLineArguments)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(currentFolder, "currentFolder");
        PreCondition.assertNotNull(commandLineArguments, "commandLineArguments");

        final Console result = createConsole(output, commandLineArguments);
        result.setFileSystem(currentFolder.getFileSystem());
        result.setCurrentFolderPath(currentFolder.getPath());

        return result;
    }

    static Console createConsole(CharacterWriteStream output, String... commandLineArguments)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(commandLineArguments, "commandLineArguments");

        final Console result = createConsole(commandLineArguments);
        result.setOutputCharacterWriteStream(output);

        return result;
    }

    static Console createConsole(String... commandLineArguments)
    {
        final Console result = Console.create(commandLineArguments);
        result.setLineSeparator("\n");

        return result;
    }
}
