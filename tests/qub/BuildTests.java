package qub;

public class BuildTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(Build.class, () ->
        {
            runner.testGroup("main(String[])", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Build.main((String[])null), new PreConditionFailure("args cannot be null."));
                });
            });

            runner.testGroup("main(Console)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> main((Console)null), new PreConditionFailure("console cannot be null."));
                });

                runner.test("with /? command line argument", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    try (final Console console = createConsole(output, "/?"))
                    {
                        main(console);
                    }
                    test.assertSuccess("Usage: qub-build\n  Used to compile and package source code projects.\n", output.getText());
                });

                runner.test("with -? command line argument", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    try (final Console console = createConsole(output, "-?"))
                    {
                        main(console);
                    }
                    test.assertSuccess("Usage: qub-build\n  Used to compile and package source code projects.\n", output.getText());
                });

                runner.test("with no project.json in the current folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        test.assertThrows(() -> main(console),
                            new FileNotFoundException("/project.json"));
                    }
                });

                runner.test("with empty project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    currentFolder.createFile("project.json");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        test.assertThrows(() -> main(console),
                            new RuntimeException(new java.text.ParseException("The root of a project.json file must be a JSON object.", 0)));
                    }
                });

                runner.test("with empty array project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.createFile("project.json"), "[]");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        test.assertThrows(() -> main(console),
                            new RuntimeException(new java.text.ParseException("The root of a project.json file must be a JSON object.", 0)));
                    }
                });

                runner.test("with empty object project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{}");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        test.assertThrows(() -> main(console),
                            new PreConditionFailure("javaVersion (null) must be 1.8."));
                    }
                });

                runner.test("with java sources version set to \"1.8\" but no \"sources\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": { \"sources\": { \"version\": \"1.8\" } } }");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        test.assertThrows(() -> main(console),
                            new FolderNotFoundException("/sources"));
                    }
                });

                runner.test("with empty \"sources\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": { \"sources\": { \"version\": \"1.8\" } } }");
                    currentFolder.createFolder("sources");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        test.assertThrows(() -> main(console),
                            new NotFoundException("No java source files found in /sources."));
                    }
                });

                runner.test("with non-empty \"sources\" folder and no \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": { \"sources\": { \"version\": \"1.8\" } } }");
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder.getFile("sources/A.java"), "A.java source");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertSuccess(true, outputs.exists());
                    test.assertEqual(Iterable.create("/outputs/sources", "/outputs/sources/A.class"), outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));
                    final File aClass = outputs.getFile("sources/A.class").throwErrorOrGetValue();
                    test.assertSuccess(true, aClass.exists());
                    test.assertSuccess(aClass.getContentByteReadStream().then((ByteReadStream contents) -> contents.asCharacterReadStream()).thenResult(CharacterReadStream::readEntireString),
                        (String aClassContents) ->
                        {
                            test.assertEqual("A.java source", aClassContents);
                        });
                });

                runner.test("with non-empty \"sources\" folder and with existing and empty \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": { \"sources\": { \"version\": \"1.8\" } } }");
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder.getFile("sources/A.java"), "A.java source");
                    currentFolder.createFolder("outputs");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertSuccess(true, outputs.exists());
                    test.assertEqual(Iterable.create("/outputs/sources", "/outputs/sources/A.class"), outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));
                    final File aClass = outputs.getFile("sources/A.class").throwErrorOrGetValue();
                    test.assertSuccess(true, aClass.exists());
                    test.assertSuccess(aClass.getContentByteReadStream().then((ByteReadStream contents) -> contents.asCharacterReadStream()).thenResult(CharacterReadStream::readEntireString),
                        (String aClassContents) ->
                        {
                            test.assertEqual("A.java source", aClassContents);
                        });
                });

                runner.test("with source file newer than existing class file", (Test test) ->
                {
                    final ManualClock clock = new ManualClock(test.getMainAsyncRunner(), DateTime.date(2000, 1, 2));
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": { \"sources\": { \"version\": \"1.8\" } } }");
                    final File classFile = currentFolder.getFile("outputs/sources/A.class").throwErrorOrGetValue();
                    setFileContents(classFile, "A.java source");
                    test.assertSuccess(DateTime.date(2000, 1, 2), classFile.getLastModified());

                    clock.advance(Duration.minutes(1));

                    final File sourceFile = currentFolder.getFile("sources/A.java").throwErrorOrGetValue();
                    setFileContents(sourceFile, "A.java source");
                    test.assertSuccess(DateTime.date(2000, 1, 2).plus(Duration.minutes(1)), sourceFile.getLastModified());

                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertSuccess(true, outputs.exists());
                    test.assertEqual(Iterable.create("/outputs/sources", "/outputs/sources/A.class"), outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));
                    test.assertSuccess(true, classFile.exists());
                    test.assertSuccess(DateTime.date(2000, 1, 2).plus(Duration.minutes(1)), classFile.getLastModified());
                    test.assertSuccess(classFile.getContentByteReadStream().then((ByteReadStream contents) -> contents.asCharacterReadStream()).thenResult(CharacterReadStream::readEntireString),
                        (String aClassContents) ->
                        {
                            test.assertEqual("A.java source", aClassContents);
                        });
                });

                runner.test("with source file older than existing class file", runner.skip(), (Test test) ->
                {
                    final ManualClock clock = new ManualClock(test.getMainAsyncRunner(), DateTime.date(2000, 1, 2));
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": { \"sources\": { \"version\": \"1.8\" } } }");

                    final File sourceFile = currentFolder.getFile("sources/A.java").throwErrorOrGetValue();
                    setFileContents(sourceFile, "A.java source");
                    test.assertSuccess(DateTime.date(2000, 1, 2), sourceFile.getLastModified());

                    clock.advance(Duration.minutes(1));

                    final File classFile = currentFolder.getFile("outputs/sources/A.class").throwErrorOrGetValue();
                    setFileContents(classFile, "A.java source (blah)");
                    test.assertSuccess(DateTime.date(2000, 1, 2).plus(Duration.minutes(1)), classFile.getLastModified());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertSuccess(true, outputs.exists());
                    test.assertEqual(Iterable.create("/outputs/sources", "/outputs/sources/A.class"), outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));
                    test.assertSuccess(true, classFile.exists());
                    test.assertSuccess(DateTime.date(2000, 1, 2).plus(Duration.minutes(1)), classFile.getLastModified());
                    test.assertSuccess(classFile.getContentByteReadStream().then((ByteReadStream contents) -> contents.asCharacterReadStream()).thenResult(CharacterReadStream::readEntireString),
                        (String aClassContents) ->
                        {
                            test.assertEqual("A.java source (blah)", aClassContents);
                        });
                });


            });
        });
    }

    private static InMemoryCharacterStream getInMemoryCharacterStream(Test test)
    {
        return new InMemoryCharacterStream(test.getParallelAsyncRunner());
    }

    private static Folder getInMemoryCurrentFolder(Test test)
    {
        PreCondition.assertNotNull(test, "test");

        return getInMemoryCurrentFolder(test, test.getClock());
    }

    private static Folder getInMemoryCurrentFolder(Test test, Clock clock)
    {
        PreCondition.assertNotNull(test, "test");
        PreCondition.assertNotNull(clock, "clock");

        final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getParallelAsyncRunner(), clock);
        fileSystem.createRoot("/");
        return fileSystem.getFolder("/").throwErrorOrGetValue();
    }

    private static void setFileContents(Result<File> fileResult, String contents)
    {
        fileResult.then((File file) -> setFileContents(file, contents));
    }

    private static void setFileContents(File file, String contents)
    {
        CharacterEncoding.UTF_8.encode(contents).then(file::setContents);
    }

    private static Console createConsole(CharacterWriteStream output, Folder currentFolder, String... commandLineArguments)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(currentFolder, "currentFolder");
        PreCondition.assertNotNull(commandLineArguments, "commandLineArguments");

        final Console result = createConsole(output, commandLineArguments);
        result.setFileSystem(currentFolder.getFileSystem());
        result.setCurrentFolderPath(currentFolder.getPath());

        return result;
    }

    private static Console createConsole(CharacterWriteStream output, String... commandLineArguments)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(commandLineArguments, "commandLineArguments");

        final Console result = createConsole(commandLineArguments);
        result.setOutput(output);

        return result;
    }

    private static Console createConsole(String... commandLineArguments)
    {
        final Console result = new Console(Iterable.create(commandLineArguments));
        result.setLineSeparator("\n");

        return result;
    }

    private static void main(Console console)
    {
        final Build build = new Build();
        build.setJavaCompiler(new FakeJavaCompiler());
        build.main(console);
    }
}
