package qub;

public interface ProjectJSONTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(ProjectJSON.class, () ->
        {
            runner.test("constructor", (Test test) ->
            {
                final ProjectJSON projectJSON = new ProjectJSON();
                test.assertNull(projectJSON.getPublisher());
            });

            runner.testGroup("setPublisher(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    test.assertSame(projectJSON, projectJSON.setPublisher(null));
                    test.assertNull(projectJSON.getPublisher());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    test.assertSame(projectJSON, projectJSON.setPublisher(""));
                    test.assertEqual("", projectJSON.getPublisher());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    test.assertSame(projectJSON, projectJSON.setPublisher("apples"));
                    test.assertEqual("apples", projectJSON.getPublisher());
                });
            });

            runner.testGroup("setProject(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    test.assertSame(projectJSON, projectJSON.setProject(null));
                    test.assertNull(projectJSON.getProject());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    test.assertSame(projectJSON, projectJSON.setProject(""));
                    test.assertEqual("", projectJSON.getProject());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    test.assertSame(projectJSON, projectJSON.setProject("apples"));
                    test.assertEqual("apples", projectJSON.getProject());
                });
            });

            runner.testGroup("setVersion(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    test.assertSame(projectJSON, projectJSON.setVersion(null));
                    test.assertNull(projectJSON.getVersion());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    test.assertSame(projectJSON, projectJSON.setVersion(""));
                    test.assertEqual("", projectJSON.getVersion());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    test.assertSame(projectJSON, projectJSON.setVersion("apples"));
                    test.assertEqual("apples", projectJSON.getVersion());
                });
            });

            runner.testGroup("setJava()", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    test.assertSame(projectJSON, projectJSON.setJava(null));
                    test.assertNull(projectJSON.getJava());
                });

                runner.test("with non-null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    final ProjectJSONJava java = new ProjectJSONJava();
                    test.assertSame(projectJSON, projectJSON.setJava(java));
                    test.assertSame(java, projectJSON.getJava());
                });
            });

            runner.testGroup("parse(File)", () ->
            {
                runner.test("with null file", (Test test) ->
                {
                    test.assertThrows(() -> ProjectJSON.parse((File)null),
                        new PreConditionFailure("projectJSONFile cannot be null."));
                });

                runner.test("with non-existing root", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final File file = fileSystem.getFile("/project.json").await();
                    test.assertThrows(() -> ProjectJSON.parse(file).await(),
                        new RootNotFoundException("/"));
                });

                runner.test("with non-existing file", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.getFile("/project.json").await();
                    test.assertThrows(() -> ProjectJSON.parse(file).await(),
                        new FileNotFoundException("/project.json"));
                });

                runner.test("with empty file", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    test.assertThrows(() -> ProjectJSON.parse(file).await(),
                        new NotFoundException("No root was found in the JSON document."));
                });

                runner.test("with non-JSON file contents", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("hello there").await();
                    test.assertThrows(() -> ProjectJSON.parse(file).await(),
                        new NotFoundException("No root was found in the JSON document."));
                });

                runner.test("with empty JSON array", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("[]").await();
                    test.assertThrows(() -> ProjectJSON.parse(file).await(),
                        new WrongTypeException("Expected the root of the JSON document to be an object."));
                });

                runner.test("with empty JSON object", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{}").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    test.assertNull(projectJSON.getJava());
                });

                runner.test("with number publisher", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"publisher\": 20 }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    test.assertNull(projectJSON.getJava());
                });

                runner.test("with quoted-string publisher", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"publisher\": \"bananas\" }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertEqual("bananas", projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    test.assertNull(projectJSON.getJava());
                });

                runner.test("with number project", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"project\": 20 }").await();
                    ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    test.assertNull(projectJSON.getJava());
                });

                runner.test("with quoted-string project", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"project\": \"bananas\" }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertEqual("bananas", projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    test.assertNull(projectJSON.getJava());
                });

                runner.test("with number version", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"version\": 20 }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    test.assertNull(projectJSON.getJava());
                });

                runner.test("with quoted-string version", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"version\": \"bananas\" }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertEqual("bananas", projectJSON.getVersion());
                    test.assertNull(projectJSON.getJava());
                });

                runner.test("with empty java object", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": {} }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with java main class", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"mainClass\": \"a\" } }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertEqual("a", java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with empty java sources object", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"sources\": {} } }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with java sources version quoted-string", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"sources\": { \"version\": \"1.8\" } } }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with java sources version number", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"sources\": { \"version\": 1.8 } } }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with empty java tests object", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"tests\": {} } }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with java tests version quoted-string", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"tests\": { \"version\": \"1.8\" } } }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with java tests version number", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"tests\": { \"version\": 1.8 } } }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with empty java dependencies", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"dependencies\": [] } }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertEqual(Iterable.create(), java.getDependencies());
                });

                runner.test("with one empty java dependency", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"dependencies\": [ {} ] } }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertEqual(Iterable.create(new Dependency()), java.getDependencies());
                });

                runner.test("with one non-empty java dependency", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"dependencies\": [ { \"publisher\": \"a\", \"project\": \"b\", \"version\": \"c\" } ] } }").await();
                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());
                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertEqual(
                        Iterable.create(
                            new Dependency()
                                .setPublisher("a")
                                .setProject("b")
                                .setVersion("c")),
                        java.getDependencies());
                });

                runner.test("with maximumErrors string value", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"maximumErrors\": \"50\" } }").await();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());

                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getMaximumErrors());
                    test.assertNull(java.getMaximumWarnings());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with maximumErrors negative number value", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"maximumErrors\": -2 } }").await();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());

                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertEqual(-2, java.getMaximumErrors());
                    test.assertNull(java.getMaximumWarnings());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with maximumErrors zero number value", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"maximumErrors\": 0 } }").await();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());

                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertEqual(0, java.getMaximumErrors());
                    test.assertNull(java.getMaximumWarnings());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with maximumErrors 100 number value", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"maximumErrors\": 100 } }").await();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());

                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertEqual(100, java.getMaximumErrors());
                    test.assertNull(java.getMaximumWarnings());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with maximumWarnings string value", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"maximumWarnings\": \"50\" } }").await();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());

                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getMaximumErrors());
                    test.assertNull(java.getMaximumWarnings());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with maximumWarnings negative number value", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"maximumWarnings\": -2 } }").await();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());

                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getMaximumErrors());
                    test.assertEqual(-2, java.getMaximumWarnings());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with maximumWarnings zero number value", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"maximumWarnings\": 0 } }").await();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());

                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getMaximumErrors());
                    test.assertEqual(0, java.getMaximumWarnings());
                    test.assertNull(java.getDependencies());
                });

                runner.test("with maximumWarnings 100 number value", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").await();
                    file.setContentsAsString("{ \"java\": { \"maximumWarnings\": 100 } }").await();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).await();
                    test.assertNotNull(projectJSON);
                    test.assertNull(projectJSON.getPublisher());
                    test.assertNull(projectJSON.getProject());
                    test.assertNull(projectJSON.getVersion());

                    final ProjectJSONJava java = projectJSON.getJava();
                    test.assertNotNull(java);
                    test.assertNull(java.getVersion());
                    test.assertNull(java.getMainClass());
                    test.assertNull(java.getShortcutName());
                    test.assertNull(java.getMaximumErrors());
                    test.assertEqual(100, java.getMaximumWarnings());
                    test.assertNull(java.getDependencies());
                });
            });
        });
    }
}
