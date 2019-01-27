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
                    setFileContents(currentFolder, "project.json", "");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        test.assertThrows(() -> main(console),
                            new NotFoundException("No root was found in the JSON document."));
                    }
                });

                runner.test("with empty array project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "[]");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        test.assertThrows(() -> main(console),
                            new WrongTypeException("Expected the root of the JSON document to be an object."));
                    }
                });

                runner.test("with empty object project.json", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "{}");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        "Compiling...\n No language specified in project.json. Nothing to compile.\n Done (0.0 Seconds)\n",
                        output.getText().throwErrorOrGetValue());
                });

                runner.test("with java sources version set to \"1.8\" but no \"sources\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        "Compiling...\n No java source files found in /.\n Done (0.0 Seconds)\n",
                        output.getText().throwErrorOrGetValue());
                });

                runner.test("with empty \"sources\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    currentFolder.createFolder("sources");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    test.assertEqual(
                        "Compiling...\n No java source files found in /.\n Done (0.0 Seconds)\n",
                        output.getText().throwErrorOrGetValue());
                });

                runner.test("with non-empty \"sources\" folder and no \"outputs\" folder", (Test test) ->
                {
                    final InMemoryCharacterStream output = getInMemoryCharacterStream(test);
                    final Folder currentFolder = getInMemoryCurrentFolder(test);
                    setFileContents(currentFolder, "project.json", "{ \"java\": {} }");
                    currentFolder.createFolder("sources");
                    setFileContents(currentFolder, "sources/A.java", "A.java source");
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/parse.json",
                            "/outputs/A.class"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.arrayProperty("dependencies");
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
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("bin").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/bin/parse.json",
                            "/bin/A.class"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.arrayProperty("dependencies");
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
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/parse.json",
                            "/outputs/A.class"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.arrayProperty("dependencies");
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
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/parse.json",
                            "/outputs/A.class",
                            "/outputs/B.class"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString),
                        "Wrong files in outputs folders");
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual("B.java source", getFileContents(outputs, "B.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "B.class").getMillisecondsSinceEpoch());
                    test.assertEqual(
                        JSON.object(parse ->
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
                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }
                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/parse.json",
                            "/outputs/A.class",
                            "/outputs/B.class"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString),
                        "Wrong files in outputs folders");
                    test.assertEqual("A.java source", getFileContents(outputs, "A.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "A.class").getMillisecondsSinceEpoch());
                    test.assertEqual("B.java source", getFileContents(outputs, "B.class"));
                    test.assertEqual(0, getFileLastModified(outputs, "B.class").getMillisecondsSinceEpoch());
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.arrayProperty("dependencies");
                            });
                            parse.objectProperty("tests/B.java", bJava ->
                            {
                                bJava.numberProperty("lastModified", 0);
                                bJava.arrayProperty("dependencies");
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

                    try (final Console console = createConsole(output, currentFolder, clock))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString),
                        "Wrong files in outputs folder");
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(classFile));
                    test.assertEqual("A.java source", getFileContents(classFile));
                    final File parseFile = outputs.getFile("parse.json").throwErrorOrGetValue();
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile));
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.arrayProperty("dependencies");
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

                    try (final Console console = createConsole(output, currentFolder, clock))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/sources",
                            "/outputs/parse.json",
                            "/outputs/sources/A.class"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));
                    test.assertEqual(beforeClockAdvance, getFileLastModified(classFile), "Wrong A.class file lastModified");
                    test.assertEqual("A.java source", getFileContents(classFile));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseJsonFile));
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.arrayProperty("dependencies");
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

                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));
                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(classFile));
                    test.assertEqual("A.java source", getFileContents(classFile));
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                                aJava.arrayProperty("dependencies");
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

                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java source, depends on A", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                                aJava.arrayProperty("dependencies");
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

                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java source", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.arrayProperty("dependencies");
                            });
                            parse.objectProperty("sources/B.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                                aJava.arrayProperty("dependencies");
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

                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source, depends on B", getFileContents(aClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(bClassFile));
                    test.assertEqual("B.java source", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.stringArrayProperty("dependencies", Iterable.create("sources/B.java"));
                            });
                            parse.objectProperty("sources/B.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                                aJava.arrayProperty("dependencies");
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

                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source, depends on B", getFileContents(aClassFile));

                    test.assertEqual(0, getFileLastModified(bClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("B.java source", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/B.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.arrayProperty("dependencies");
                            });
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                                aJava.stringArrayProperty("dependencies", Iterable.create("sources/B.java"));
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

                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(aClassFile));
                    test.assertEqual("A.java source, depends on B", getFileContents(aClassFile));

                    test.assertEqual(false, bClassFile.exists().throwErrorOrGetValue(),
                        "Class file of deleted source file should have been deleted.");

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
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

                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/parse.json",
                            "/outputs/B.class"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));

                    test.assertEqual(0, getFileLastModified(aClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("A.java source", getFileContents(aClassFile));

                    final File bClassFile = outputs.getFile("B.class").throwErrorOrGetValue();
                    test.assertEqual(60000, getFileLastModified(bClassFile).getMillisecondsSinceEpoch());
                    test.assertEqual("B.java source", getFileContents(bClassFile));

                    test.assertEqual(clock.getCurrentDateTime(), getFileLastModified(parseFile), "Wrong parse.json file lastModified");
                    test.assertEqual(
                        JSON.object(parse ->
                        {
                            parse.objectProperty("sources/A.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 0);
                                aJava.arrayProperty("dependencies");
                            });
                            parse.objectProperty("sources/B.java", aJava ->
                            {
                                aJava.numberProperty("lastModified", 60000);
                                aJava.arrayProperty("dependencies");
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

                    try (final Console console = createConsole(output, currentFolder))
                    {
                        main(console);
                    }

                    final Folder outputs = currentFolder.getFolder("outputs").throwErrorOrGetValue();
                    test.assertEqual(
                        Iterable.create(
                            "/outputs/A.class",
                            "/outputs/B.class",
                            "/outputs/C.class",
                            "/outputs/parse.json"),
                        outputs.getFilesAndFoldersRecursively().throwErrorOrGetValue().map(FileSystemEntry::toString));

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
                                cJava.stringArrayProperty("dependencies");
                            });
                        }).toString(),
                        getFileContents(parseFile),
                        "Wrong parse.json file contents");
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
        return fileSystem.getFolder("/").throwErrorOrGetValue();
    }

    private static File setFileContents(Folder folder, String relativeFilePath, String contents)
    {
        final File file = folder.getFile(relativeFilePath).throwErrorOrGetValue();
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
        return getFileLastModified(folder.getFile(relativeFilePath).throwErrorOrGetValue());
    }

    private static DateTime getFileLastModified(File file)
    {
        return file.getLastModified().throwErrorOrGetValue();
    }

    private static String getFileContents(Result<File> file)
    {
        return getFileContents(file.throwErrorOrGetValue());
    }

    private static String getFileContents(File file)
    {
        return file.getContentByteReadStream()
            .then((ByteReadStream contents) -> contents.asCharacterReadStream())
            .thenResult(CharacterReadStream::readEntireString)
            .throwErrorOrGetValue();
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
        final Build build = new Build();
        build.setJavaCompiler(new FakeJavaCompiler());
        build.main(console);
    }
}
