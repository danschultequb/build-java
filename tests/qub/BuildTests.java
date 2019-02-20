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
                    test.assertEqual(
                        "Usage: qub-build [[-folder=]<folder-path-to-build>] [-verbose]\n" +
                        "  Used to compile and package source code projects.\n" +
                        "  -folder: The folder to build. This can be specified either with the -folder\n" +
                        "           argument name or without it.\n" +
                        "  -verbose: Show verbose logs.\n",
                        output.getText().await());
                });

                runner.test("with -? command line argument", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    try (final Console console = createConsole(output, "-?"))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        "Usage: qub-build [[-folder=]<folder-path-to-build>] [-verbose]\n" +
                            "  Used to compile and package source code projects.\n" +
                            "  -folder: The folder to build. This can be specified either with the -folder\n" +
                            "           argument name or without it.\n" +
                            "  -verbose: Show verbose logs.\n",
                        output.getText().await());
                });

                runner.test("with no project.json in the unnamed specified folder command line argument", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    try (final Console console = createConsole(output, currentFolder, "/fake/folder/", "-parsejson"))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        Iterable.create(
                            "Compiling...",
                            " The file at \"/fake/folder/project.json\" doesn't exist.",
                            " Done (0.0 Seconds)"),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with no project.json in the named specified folder command line argument", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    try (final Console console = createConsole(output, currentFolder, "-folder=/fake/folder/", "-parsejson"))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        Iterable.create(
                            "Compiling...",
                            " The file at \"/fake/folder/project.json\" doesn't exist.",
                            " Done (0.0 Seconds)"),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with no project.json in the specified folder with -verbose before folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    try (final Console console = createConsole(output, currentFolder, "-verbose", "/fake/folder/", "-parsejson"))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        Iterable.create(
                            "Compiling...",
                            "VERBOSE:  Parsing project.json...",
                            " The file at \"/fake/folder/project.json\" doesn't exist.",
                            " Done (0.0 Seconds)"),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with no project.json in the current folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        Iterable.create(
                            "Compiling...",
                            " The file at \"/project.json\" doesn't exist.",
                            " Done (0.0 Seconds)"),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with empty project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "");
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        Iterable.create(
                            "Compiling...",
                            " No root was found in the JSON document.",
                            " Done (0.0 Seconds)"),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with empty array project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "[]");
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        Iterable.create(
                            "Compiling...",
                            " Expected the root of the JSON document to be an object.",
                            " Done (0.0 Seconds)"),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with empty object project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "{}");
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        "Compiling...\n No language specified in project.json. Nothing to compile.\n Done (0.0 Seconds)\n",
                        output.getText().await());
                });

                runner.test("with java sources version set to \"1.8\" but no \"sources\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    final Array<String> outputLines = Strings.getLines(output.getText().await()).toArray();
                    test.assertEqual("Compiling...", outputLines.get(0));
                    test.assertEqual(" No java source files found in /.", outputLines.get(1));
                    test.assertContains(outputLines.get(2), " Done (0.");
                    test.assertContains(outputLines.get(2), " Seconds)");
                });

                runner.test("with empty \"sources\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    currentFolder.createFolder("sources");
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        "Compiling...\n No java source files found in /.\n Done (0.0 Seconds)\n",
                        output.getText().await());
                });

                runner.test("with non-empty \"sources\" folder and no \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(outputs, "parse.json"));
                });

                runner.test("with non-empty \"sources\" folder and custom \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "{ \"java\": { \"outputFolder\": \"bin\" } }");
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("bin").await();
                    test.assertEqual(
                        Iterable.create(
                            "/bin/A.class",
                            "/bin/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java", java ->
                                {
                                    java.stringProperty("outputFolder", "bin");
                                });
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(outputs, "parse.json"));
                });

                runner.test("with non-empty \"sources\" folder and with existing and empty \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": {} }");
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder.getFile("sources/A.java"), "A.java source");
                    currentFolder.createFolder("outputs");
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(outputs, "parse.json"));
                });

                runner.test("with multiple source files and with existing and empty \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": {} }");
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source");
                    currentFolder.createFolder("outputs");
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folders");
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual("B.java source", getFileContents(outputs, "B.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "B.class").getMillisecondsSinceEpoch());
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                            parse.objectProperty("sources/B.java", bJava ->
                            {
                                bJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(outputs, "parse.json"));
                });

                runner.test("with multiple source folders", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder.getFile("project.json"), "{ \"java\": {} }");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "tests/B.java", "B.java source");
                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folders");
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual("B.java source", getFileContents(outputs, "B.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "B.class").getMillisecondsSinceEpoch());
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                            parse.objectProperty("tests/B.java", bJava ->
                            {
                                bJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(outputs, "parse.json"));
                });

                runner.test("with source file with same age as existing class file but no parse.json file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File classFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File sourceFile = setFileContents(currentFolder, "sources/A.java", "A.java source");

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, clock, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(classFile));
                    test.assertEqual("A.java source", getFileContents(classFile));
                    final File parseFile = outputs.getFile("parse.json").await();
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile));
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseFile));
                });

                runner.test("with source file with same age as existing class file and with parse.json file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File classFile = setFileContents(currentFolder, "outputs/sources/A.class", "A.java source");
                    final File sourceFile = setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.arrayProperty("dependencies");
                        });
                    }).toString());

                    final DateTime beforeClockAdvance = clock.getCurrentDateTime();
                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, clock, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/sources",
                            "/outputs/parse.json",
                            "/outputs/sources/A.class"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(beforeClockAdvance, getFileLastModified(classFile), "Wrong A.class file lastModified");
                    test.assertEqual("A.java source", getFileContents(classFile));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile));
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile));
                });

                runner.test("with source file newer than existing class file and with parse.json file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File classFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    clock.advance(Duration.minutes(1));
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File parseFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.arrayProperty("dependencies");
                        });
                    }).toString());

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(classFile));
                    test.assertEqual("A.java source", getFileContents(classFile));
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                            });
                        }).toString(),
                        getFileContents(parseFile), "Wrong parse.json file contents");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile));
                });

                runner.test("with one source file with one error", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final FakeJavaCompiler compiler = new FakeJavaCompiler();
                    compiler.exitCode = 1;
                    compiler.issues = Iterable.create(
                        new JavaCompilerIssue(
                            "/sources/A.java",
                            1, 5,
                            Issue.Type.Error,
                            "This doesn't look right to me."));
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File classFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    clock.advance(Duration.minutes(1));
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File parseFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.arrayProperty("dependencies");
                        });
                    }).toString());

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console, compiler);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Compiling...",
                            " /sources/A.java (Line 1): This doesn't look right to me.",
                            " Done (0.0 Seconds)"),
                        Strings.getLines(output.getText().await()));
                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(classFile));
                    test.assertEqual("A.java source", getFileContents(classFile));
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                            });
                        }).toString(),
                        getFileContents(parseFile), "Wrong parse.json file contents");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile));
                });

                runner.test("with multiple source files newer than their existing class files and with parse.json file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    final File parseFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.arrayProperty("dependencies");
                        });
                        parse.objectProperty("sources/B.java", bJava ->
                        {
                            bJava.numberProperty("lastModified", 0);
                            bJava.stringArrayProperty("dependencies", Iterable.create("sources/A.java"));
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on A");

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java source, depends on A", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                            });
                            parse.objectProperty("sources/B.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                                aJava.stringArrayProperty("dependencies", Iterable.create("sources/A.java"));
                            });
                        }).toString(),
                        getFileContents(parseFile),
                        "Wrong parse.json file contents");
                });

                runner.test("with one modified source file and another unmodified and undependant source file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    final File aJavaFile = setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File parseFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.arrayProperty("dependencies");
                        });
                        parse.objectProperty("sources/B.java", bJava ->
                        {
                            bJava.numberProperty("lastModified", 0);
                            bJava.arrayProperty("dependencies");
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    final File bJavaFile = setFileContents(currentFolder, "sources/B.java", "B.java source");

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java source", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                            parse.objectProperty("sources/B.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                            });
                        }).toString(),
                        getFileContents(parseFile),
                        "Wrong parse.json file contents");
                });

                runner.test("with one modified source file and another unmodified and dependant source file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    final File aJavaFile = setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");
                    final File parseFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.stringArrayProperty("dependencies", Iterable.create("sources/B.java"));
                        });
                        parse.objectProperty("sources/B.java", bJava ->
                        {
                            bJava.numberProperty("lastModified", 0);
                            bJava.arrayProperty("dependencies");
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    final File bJavaFile = setFileContents(currentFolder, "sources/B.java", "B.java source");

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source, depends on B", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java source", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.stringArrayProperty("dependencies", Iterable.create("sources/B.java"));
                            });
                            parse.objectProperty("sources/B.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                            });
                        }).toString(),
                        getFileContents(parseFile),
                        "Wrong parse.json file contents");
                });

                runner.test("with one unmodified source file and another modified and dependant source file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    final File bJavaFile = setFileContents(currentFolder, "sources/B.java", "B.java source");
                    final File parseFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.arrayProperty("dependencies");
                        });
                        parse.objectProperty("sources/B.java", bJava ->
                        {
                            bJava.numberProperty("lastModified", 0);
                            bJava.arrayProperty("dependencies");
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    final File aJavaFile = setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source, depends on B", getFileContents(aClassFile));

                    test.assertEqual(0, getFileLastModified(bClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("B.java source", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                                aJava.stringArrayProperty("dependencies", Iterable.create("sources/B.java"));
                            });
                            parse.objectProperty("sources/B.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseFile),
                        "Wrong parse.json file contents");
                });

                runner.test("with one deleted source file and another unmodified and dependant source file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": { \"shortcutName\": \"foo\" } }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source, depends on B");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    final File aJavaFile = setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");
                    final File parseFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.stringArrayProperty("dependencies", Iterable.create("sources/B.java"));
                        });
                        parse.objectProperty("sources/B.java", bJava ->
                        {
                            bJava.numberProperty("lastModified", 0);
                            bJava.arrayProperty("dependencies");
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source, depends on B", getFileContents(aClassFile));

                    test.assertEqual(false, bClassFile.exists().await(),
                        "Class file of deleted source file should have been deleted.");

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java", java ->
                                {
                                    java.stringProperty("shortcutName", "foo");
                                });
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.stringArrayProperty("dependencies", Iterable.create("sources/B.java"));
                            });
                        }).toString(),
                        getFileContents(parseFile),
                        "Wrong parse.json file contents");
                });

                runner.test("with one new source file and another unmodified and undependant source file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File aJavaFile = setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File parseFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.arrayProperty("dependencies");
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    final File bJavaFile = setFileContents(currentFolder, "sources/B.java", "B.java source");

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    final File bClassFile = outputs.getFile("B.class").await();
                    test.assertEqual(60000, getFileLastModified(bClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("B.java source", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                            parse.objectProperty("sources/B.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                            });
                        }).toString(),
                        getFileContents(parseFile),
                        "Wrong parse.json file contents");
                });

                runner.test("A depends on B, B depends on C, C is modified, only B and C should be compiled", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aJavaFile = setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");
                    final File bJavaFile = setFileContents(currentFolder, "sources/B.java", "B.java source, depends on C");

                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source, depends on B");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source, depends on C");
                    final File cClassFile = setFileContents(currentFolder, "outputs/C.class", "C.java source");
                    final File parseFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.stringArrayProperty("dependencies", Iterable.create("sources/B.java"));
                        });
                        parse.objectProperty("sources/B.java", bJava ->
                        {
                            bJava.numberProperty("lastModified", 0);
                            bJava.stringArrayProperty("dependencies", Iterable.create("sources/C.java"));
                        });
                        parse.objectProperty("sources/C.java", cJava ->
                        {
                            cJava.numberProperty("lastModified", 0);
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    final File cJavaFile = setFileContents(currentFolder, "sources/C.java", "C.java source");

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/C.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source, depends on B", getFileContents(aClassFile));

                    test.assertEqual(60000, getFileLastModified(bClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("B.java source, depends on C", getFileContents(bClassFile));

                    test.assertEqual(60000, getFileLastModified(cClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("C.java source", getFileContents(cClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.stringArrayProperty("dependencies", Iterable.create("sources/B.java"));
                            });
                            parse.objectProperty("sources/B.java", bJava ->
                            {
                                bJava.numberProperty("lastModified", 0);
                                bJava.stringArrayProperty("dependencies", Iterable.create("sources/C.java"));
                            });
                            parse.objectProperty("sources/C.java", cJava ->
                            {
                                cJava.numberProperty("lastModified", 60000);
                            });
                        }).toString(),
                        getFileContents(parseFile),
                        "Wrong parse.json file contents");
                });

                runner.test("with no QUB_HOME environment variable specified", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": { \"dependencies\": [ { \"publisher\": \"qub\", \"project\": \"qub-java\", \"version\": \"foo\" } ] } }");
                    setFileContents(currentFolder, "sources/A.java", "A.java source, depends on B");

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }
                });

                runner.test("nothing gets compiled when project.json publisher changes", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parseJson ->
                    {
                        parseJson.objectProperty("project.json", projectJson ->
                        {
                            projectJson.stringProperty("publisher", "a");
                            projectJson.objectProperty("java");
                        });
                        parseJson.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                        });
                    }).toString());
                    setFileContents(currentFolder, "project.json", JSON.object(projectJson ->
                    {
                        projectJson.stringProperty("publisher", "b");
                        projectJson.objectProperty("java");
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.stringProperty("publisher", "b");
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile),
                        "Wrong parse.json file contents");
                });

                runner.test("nothing gets compiled when project.json project changes", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parseJson ->
                    {
                        parseJson.objectProperty("project.json", projectJson ->
                        {
                            projectJson.stringProperty("project", "a");
                            projectJson.objectProperty("java");
                        });
                        parseJson.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                        });
                    }).toString());
                    setFileContents(currentFolder, "project.json", JSON.object(projectJson ->
                    {
                        projectJson.stringProperty("project", "b");
                        projectJson.objectProperty("java");
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.stringProperty("project", "b");
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile),
                        "Wrong parse.json file contents");
                });

                runner.test("nothing gets compiled when project.json version changes", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parseJson ->
                    {
                        parseJson.objectProperty("project.json", projectJson ->
                        {
                            projectJson.stringProperty("version", "a");
                            projectJson.objectProperty("java");
                        });
                        parseJson.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                        });
                    }).toString());
                    setFileContents(currentFolder, "project.json", JSON.object(projectJson ->
                    {
                        projectJson.stringProperty("version", "b");
                        projectJson.objectProperty("java");
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.stringProperty("version", "b");
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile),
                        "Wrong parse.json file contents");
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
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parseJson ->
                    {
                        parseJson.objectProperty("project.json", projectJson ->
                        {
                            projectJson.objectProperty("java", java ->
                            {
                                java.stringProperty("version", "11");
                            });
                        });
                        parseJson.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                        });
                    }).toString());
                    setFileContents(currentFolder, "project.json", JSON.object(projectJson ->
                    {
                        projectJson.objectProperty("java", java ->
                        {
                            java.stringProperty("version", "1.8");
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        console.setEnvironmentVariables(Map.<String,String>create()
                            .set("JAVA_HOME", jdk11Folder.toString()));
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(60000, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java", java ->
                                {
                                    java.stringProperty("version", "1.8");
                                });
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile),
                        "Wrong parse.json file contents");
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
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parseJson ->
                    {
                        parseJson.objectProperty("project.json", projectJson ->
                        {
                            projectJson.objectProperty("java", java ->
                            {
                                java.stringProperty("version", "11");
                            });
                        });
                        parseJson.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                        });
                    }).toString());
                    setFileContents(currentFolder, "project.json", JSON.object(projectJson ->
                    {
                        projectJson.objectProperty("java", java ->
                        {
                            java.stringProperty("version", "8");
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        console.setEnvironmentVariables(Map.<String,String>create()
                            .set("JAVA_HOME", jdk11Folder.toString()));
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(60000, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java", java ->
                                {
                                    java.stringProperty("version", "8");
                                });
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile),
                        "Wrong parse.json file contents");
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
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parseJson ->
                    {
                        parseJson.objectProperty("project.json", projectJson ->
                        {
                            projectJson.objectProperty("java", java ->
                            {
                                java.stringProperty("version", "1.8");
                            });
                        });
                        parseJson.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                        });
                    }).toString());
                    setFileContents(currentFolder, "project.json", JSON.object(projectJson ->
                    {
                        projectJson.objectProperty("java", java ->
                        {
                            java.stringProperty("version", "8");
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        console.setEnvironmentVariables(Map.<String,String>create()
                            .set("JAVA_HOME", jdk11Folder.toString()));
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java", java ->
                                {
                                    java.stringProperty("version", "8");
                                });
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile),
                        "Wrong parse.json file contents");
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
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parseJson ->
                    {
                        parseJson.objectProperty("project.json", projectJson ->
                        {
                            projectJson.objectProperty("java", java ->
                            {
                                java.stringProperty("version", "8");
                            });
                        });
                        parseJson.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                        });
                    }).toString());
                    setFileContents(currentFolder, "project.json", JSON.object(projectJson ->
                    {
                        projectJson.objectProperty("java", java ->
                        {
                            java.stringProperty("version", "1.8");
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        console.setEnvironmentVariables(Map.<String,String>create()
                            .set("JAVA_HOME", jdk11Folder.toString()));
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java", java ->
                                {
                                    java.stringProperty("version", "1.8");
                                });
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile),
                        "Wrong parse.json file contents");
                });

                runner.test("nothing gets compiled when project.json java dependency is added", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parseJson ->
                    {
                        parseJson.objectProperty("project.json", projectJson ->
                        {
                            projectJson.objectProperty("java");
                        });
                        parseJson.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                        });
                    }).toString());
                    setFileContents(currentFolder, "project.json", JSON.object(projectJson ->
                    {
                        projectJson.objectProperty("java", java ->
                        {
                            java.arrayProperty("dependencies", dependencies ->
                            {
                                dependencies.objectElement(dependency ->
                                {
                                    dependency.stringProperty("publisher", "a");
                                    dependency.stringProperty("project", "b");
                                    dependency.stringProperty("version", "c");
                                });
                            });
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        console.setEnvironmentVariables(Map.<String,String>create()
                            .set("QUB_HOME", "/qub/"));
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java", java ->
                                {
                                    java.arrayProperty("dependencies", dependencies ->
                                    {
                                        dependencies.objectElement(dependency ->
                                        {
                                            dependency.stringProperty("publisher", "a");
                                            dependency.stringProperty("project", "b");
                                            dependency.stringProperty("version", "c");
                                        });
                                    });
                                });
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile),
                        "Wrong parse.json file contents");
                });

                runner.test("everything gets compiled when project.json java dependency is removed", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parseJson ->
                    {
                        parseJson.objectProperty("project.json", projectJson ->
                        {
                            projectJson.objectProperty("java", java ->
                            {
                                java.arrayProperty("dependencies", dependencies ->
                                {
                                    dependencies.objectElement(dependency ->
                                    {
                                        dependency.stringProperty("publisher", "a");
                                        dependency.stringProperty("project", "b");
                                        dependency.stringProperty("version", "c");
                                    });
                                });
                            });
                        });
                        parseJson.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                        });
                    }).toString());
                    setFileContents(currentFolder, "project.json", JSON.object(projectJson ->
                    {
                        projectJson.objectProperty("java");
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        console.setEnvironmentVariables(Map.<String,String>create()
                            .set("QUB_HOME", "/qub/"));
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(60000, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile),
                        "Wrong parse.json file contents");
                });

                runner.test("everything gets compiled when project.json java dependency version is changed", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File parseJsonFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parseJson ->
                    {
                        parseJson.objectProperty("project.json", projectJson ->
                        {
                            projectJson.objectProperty("java", java ->
                            {
                                java.arrayProperty("dependencies", dependencies ->
                                {
                                    dependencies.objectElement(dependency ->
                                    {
                                        dependency.stringProperty("publisher", "a");
                                        dependency.stringProperty("project", "b");
                                        dependency.stringProperty("version", "c");
                                    });
                                });
                            });
                        });
                        parseJson.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                        });
                    }).toString());
                    setFileContents(currentFolder, "project.json", JSON.object(projectJson ->
                    {
                        projectJson.objectProperty("java", java ->
                        {
                            java.arrayProperty("dependencies", dependencies ->
                            {
                                dependencies.objectElement(dependency ->
                                {
                                    dependency.stringProperty("publisher", "a");
                                    dependency.stringProperty("project", "b");
                                    dependency.stringProperty("version", "d");
                                });
                            });
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        console.setEnvironmentVariables(Map.<String,String>create()
                            .set("QUB_HOME", "/qub/"));
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(60000, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java", java ->
                                {
                                    java.arrayProperty("dependencies", dependencies ->
                                    {
                                        dependencies.objectElement(dependency ->
                                        {
                                            dependency.stringProperty("publisher", "a");
                                            dependency.stringProperty("project", "b");
                                            dependency.stringProperty("version", "d");
                                        });
                                    });
                                });
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                        }).toString(),
                        getFileContents(parseJsonFile),
                        "Wrong parse.json file contents");
                });

                runner.test("with multiple source files newer than their existing class files and with parse.json file", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aClassFile = setFileContents(currentFolder, "outputs/A.class", "A.java source");
                    final File bClassFile = setFileContents(currentFolder, "outputs/B.class", "B.java source");
                    final File parseFile = setFileContents(currentFolder, "outputs/parse.json", JSON.object(parse ->
                    {
                        parse.objectProperty("sources/A.java", aJava ->
                        {
                            aJava.numberProperty("lastModified", 0);
                            aJava.arrayProperty("dependencies");
                        });
                        parse.objectProperty("sources/B.java", bJava ->
                        {
                            bJava.numberProperty("lastModified", 0);
                            bJava.stringArrayProperty("dependencies", Iterable.create("sources/A.java"));
                        });
                    }).toString());

                    clock.advance(Duration.minutes(1));

                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on A");

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java source, depends on A", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                            });
                            parse.objectProperty("sources/B.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                                aJava.stringArrayProperty("dependencies", Iterable.create("sources/A.java"));
                            });
                        }).toString(),
                        getFileContents(parseFile),
                        "Wrong parse.json file contents");
                });

                runner.test("with partial-name dependency match and -parsejson", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    final File aJavaFile = setFileContents(currentFolder, "sources/A.java", "A.java source");
                    final File abJavaFile = setFileContents(currentFolder, "sources/AB.java", "AB.java source");
                    final File bJavaFile = setFileContents(currentFolder, "sources/B.java", "B.java source, depends on AB");

                    try (final Console console = createConsole(output, currentFolder, "-parsejson"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/AB.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));

                    test.assertEqual(0, getFileLastModified(outputs, "AB.class").getMillisecondsSinceEpoch());
                    test.assertEqual("AB.java source", getFileContents(outputs, "AB.class"));

                    test.assertEqual(0, getFileLastModified(outputs, "B.class").getMillisecondsSinceEpoch());
                    test.assertEqual("B.java source, depends on AB", getFileContents(outputs, "B.class"));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(outputs, "parse.json"), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("project.json", projectJson ->
                            {
                                projectJson.objectProperty("java");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                            });
                            parse.objectProperty("sources/AB.java", abJava ->
                            {
                                abJava.numberProperty("lastModified", 0);
                            });
                            parse.objectProperty("sources/B.java", bJava ->
                            {
                                bJava.numberProperty("lastModified", 0);
                                bJava.stringArrayProperty("dependencies", Iterable.create("sources/AB.java"));
                            });
                        }).toString(),
                        getFileContents(outputs, "parse.json"),
                        "Wrong parse.json file contents");
                });

                runner.test("with multiple source files and -parsejson=false", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on A");

                    try (final Console console = createConsole(output, currentFolder, "-parsejson=false"))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(outputs, "A.class"));
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(outputs, "B.class"));
                    test.assertEqual("B.java source, depends on A", getFileContents(outputs, "B.class"));

                    test.assertFalse(outputs.fileExists("parse.json").await());
                });

                runner.test("with existing outputs folder and -parsejson=false", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on A");

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    outputs.createFile("blah.txt").await();

                    try (final Console console = createConsole(output, currentFolder, "-parsejson=false"))
                    {
                        main(console);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(outputs, "A.class"));
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(outputs, "B.class"));
                    test.assertEqual("B.java source, depends on A", getFileContents(outputs, "B.class"));

                    test.assertFalse(outputs.fileExists("parse.json").await());
                });

                runner.test("with existing outputs folder, -parsejson=false, and -createjar=false", (Test test) ->
                {
                    final ManualClock clock = getManualClock(test);
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test, clock);
                    setFileContents(currentFolder, "project.json", "{ \"project\": \"fake-project\", \"java\": {} }");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    setFileContents(currentFolder, "sources/B.java", "B.java source, depends on A");

                    final Folder outputs = currentFolder.getFolder("outputs").await();
                    outputs.createFile("blah.txt").await();

                    try (final Console console = createConsole(output, currentFolder, "-parsejson=false", "-createjar=true"))
                    {
                        main(console);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class"),
                        outputs.getFilesAndFoldersRecursively().await().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(outputs, "A.class"));
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(outputs, "B.class"));
                    test.assertEqual("B.java source, depends on A", getFileContents(outputs, "B.class"));

                    test.assertFalse(outputs.fileExists("parse.json").await());
                });
            });
        });
    }

    private static ManualClock getManualClock(Test test)
    {
        PreCondition.assertNotNull(test, "test");

        return new ManualClock(test.getMainAsyncRunner(), DateTime.utc(0));
    }

    private static InMemoryCharacterStream getInMemoryCharacterStream(Test test)
    {
        return new InMemoryCharacterStream(test.getParallelAsyncRunner());
    }

    private static Folder getInMemoryCurrentFolder(Test test)
    {
        return getInMemoryCurrentFolder(test, getManualClock(test));
    }

    private static Folder getInMemoryCurrentFolder(Test test, Clock clock)
    {
        PreCondition.assertNotNull(test, "test");
        PreCondition.assertNotNull(clock, "clock");

        final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getParallelAsyncRunner(), clock);
        fileSystem.createRoot("/");
        return fileSystem.getFolder("/").await();
    }

    private static File setFileContents(Folder folder, String relativeFilePath, String contents)
    {
        final File file = folder.getFile(relativeFilePath).await();
        setFileContents(file, contents);
        return file;
    }

    private static void setFileContents(Result<File> fileResult, String contents)
    {
        fileResult.then((File file) -> setFileContents(file, contents));
    }

    private static void setFileContents(File file, String contents)
    {
        CharacterEncoding.UTF_8.encode(contents).then(file::setContents);
    }

    private static String getFileContents(Folder folder, String relativeFilePath)
    {
        return getFileContents(folder.getFile(relativeFilePath));
    }

    private static DateTime getFileLastModified(Folder folder, String relativeFilePath)
    {
        return getFileLastModified(folder.getFile(relativeFilePath).await());
    }

    private static DateTime getFileLastModified(File file)
    {
        return file.getLastModified().await();
    }

    private static String getFileContents(Result<File> file)
    {
        return getFileContents(file.await());
    }

    private static String getFileContents(File file)
    {
        return file.getContentByteReadStream()
            .then((ByteReadStream contents) -> contents.asCharacterReadStream())
            .thenResult(CharacterReadStream::readEntireString)
            .await();
    }

    private static Console createConsole(CharacterWriteStream output, Folder currentFolder, Clock clock, String... commandLineArguments)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(currentFolder, "currentFolder");
        PreCondition.assertNotNull(clock, "clock");
        PreCondition.assertNotNull(commandLineArguments, "commandLineArguments");

        final Console result = createConsole(output, currentFolder, commandLineArguments);
        result.setClock(clock);

        return result;
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
        PreCondition.assertNotNull(console, "console");

        main(console, new FakeJavaCompiler());
    }

    private static void main(Console console, JavaCompiler compiler)
    {
        PreCondition.assertNotNull(console, "console");
        PreCondition.assertNotNull(compiler, "compiler");

        final Build build = new Build();
        build.setJavaCompiler(compiler);
        build.main(console);
    }
}
