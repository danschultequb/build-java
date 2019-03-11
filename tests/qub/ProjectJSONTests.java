package qub;

import java.text.ParseException;

public class ProjectJSONTests
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
                    projectJSON.setPublisher(null);
                    test.assertEqual(null, projectJSON.getPublisher());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setPublisher("");
                    test.assertEqual("", projectJSON.getPublisher());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setPublisher("apples");
                    test.assertEqual("apples", projectJSON.getPublisher());
                });
            });

            runner.testGroup("setProject(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setProject(null);
                    test.assertEqual(null, projectJSON.getProject());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setProject("");
                    test.assertEqual("", projectJSON.getProject());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setProject("apples");
                    test.assertEqual("apples", projectJSON.getProject());
                });
            });

            runner.testGroup("setVersion(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setVersion(null);
                    test.assertEqual(null, projectJSON.getVersion());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setVersion("");
                    test.assertEqual("", projectJSON.getVersion());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setVersion("apples");
                    test.assertEqual("apples", projectJSON.getVersion());
                });
            });

            runner.testGroup("setJava()", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setJava(null);
                    test.assertNull(projectJSON.getJava());
                });

                runner.test("with non-null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    final ProjectJSONJava java = new ProjectJSONJava();
                    projectJSON.setJava(java);
                    test.assertSame(java, projectJSON.getJava());
                });
            });

            runner.testGroup("parse(File)", () ->
            {
                runner.test("with null file", (Test test) ->
                {
                    test.assertThrows(() -> ProjectJSON.parse((File)null), new PreConditionFailure("projectJSONFile cannot be null."));
                });

                runner.test("with non-existing file", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    final Result<File> file = fileSystem.getFile("/project.json");
                    test.assertError(new FileNotFoundException("/project.json"), file.thenResult(ProjectJSON::parse));
                });

                runner.test("with empty file", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> file = fileSystem.createFile("/project.json");
                    test.assertError(new NotFoundException("No root was found in the JSON document."), file.thenResult(ProjectJSON::parse));
                });

                runner.test("with non-JSON file contents", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("hello there").then(file::setContents));
                    test.assertError(new NotFoundException("No root was found in the JSON document."), fileResult.thenResult(ProjectJSON::parse));
                });

                runner.test("with empty JSON array", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("[]").then(file::setContents));
                    test.assertError(new WrongTypeException("Expected the root of the JSON document to be an object."), fileResult.thenResult(ProjectJSON::parse));
                });

                runner.test("with empty JSON object", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{}").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            test.assertEqual(null, projectJSON.getJava());
                        });
                });

                runner.test("with number publisher", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"publisher\": 20 }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            test.assertEqual(null, projectJSON.getJava());
                        });
                });

                runner.test("with quoted-string publisher", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"publisher\": \"bananas\" }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual("bananas", projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            test.assertEqual(null, projectJSON.getJava());
                        });
                });

                runner.test("with number project", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"project\": 20 }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            test.assertEqual(null, projectJSON.getJava());
                        });
                });

                runner.test("with quoted-string project", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"project\": \"bananas\" }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual("bananas", projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            test.assertEqual(null, projectJSON.getJava());
                        });
                });

                runner.test("with number version", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"version\": 20 }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            test.assertEqual(null, projectJSON.getJava());
                        });
                });

                runner.test("with quoted-string version", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"version\": \"bananas\" }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual("bananas", projectJSON.getVersion());
                            test.assertEqual(null, projectJSON.getJava());
                        });
                });

                runner.test("with empty java object", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": {} }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            final ProjectJSONJava java = projectJSON.getJava();
                            test.assertNotNull(java);
                            test.assertNull(java.getVersion());
                            test.assertNull(java.getMainClass());
                            test.assertNull(java.getShortcutName());
                            test.assertNull(java.getDependencies());
                        });
                });

                runner.test("with java main class", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": { \"mainClass\": \"a\" } }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            final ProjectJSONJava java = projectJSON.getJava();
                            test.assertNotNull(java);
                            test.assertNull(java.getVersion());
                            test.assertEqual("a", java.getMainClass());
                            test.assertNull(java.getShortcutName());
                            test.assertNull(java.getDependencies());
                        });
                });

                runner.test("with empty java sources object", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": { \"sources\": {} } }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            final ProjectJSONJava java = projectJSON.getJava();
                            test.assertNotNull(java);
                            test.assertNull(java.getVersion());
                            test.assertNull(java.getMainClass());
                            test.assertNull(java.getShortcutName());
                            test.assertNull(java.getDependencies());
                        });
                });

                runner.test("with java sources version quoted-string", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": { \"sources\": { \"version\": \"1.8\" } } }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            final ProjectJSONJava java = projectJSON.getJava();
                            test.assertNotNull(java);
                            test.assertNull(java.getVersion());
                            test.assertNull(java.getMainClass());
                            test.assertNull(java.getShortcutName());
                            test.assertNull(java.getDependencies());
                        });
                });

                runner.test("with java sources version number", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": { \"sources\": { \"version\": 1.8 } } }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            final ProjectJSONJava java = projectJSON.getJava();
                            test.assertNotNull(java);
                            test.assertNull(java.getVersion());
                            test.assertNull(java.getMainClass());
                            test.assertNull(java.getShortcutName());
                            test.assertNull(java.getDependencies());
                        });
                });

                runner.test("with empty java tests object", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": { \"tests\": {} } }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            final ProjectJSONJava java = projectJSON.getJava();
                            test.assertNotNull(java);
                            test.assertNull(java.getVersion());
                            test.assertNull(java.getMainClass());
                            test.assertNull(java.getShortcutName());
                            test.assertNull(java.getDependencies());
                        });
                });

                runner.test("with java tests version quoted-string", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": { \"tests\": { \"version\": \"1.8\" } } }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            final ProjectJSONJava java = projectJSON.getJava();
                            test.assertNotNull(java);
                            test.assertNull(java.getVersion());
                            test.assertNull(java.getMainClass());
                            test.assertNull(java.getShortcutName());
                            test.assertNull(java.getDependencies());
                        });
                });

                runner.test("with java tests version number", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": { \"tests\": { \"version\": 1.8 } } }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            final ProjectJSONJava java = projectJSON.getJava();
                            test.assertNotNull(java);
                            test.assertNull(java.getVersion());
                            test.assertNull(java.getMainClass());
                            test.assertNull(java.getShortcutName());
                            test.assertNull(java.getDependencies());
                        });
                });

                runner.test("with empty java dependencies", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": { \"dependencies\": [] } }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            final ProjectJSONJava java = projectJSON.getJava();
                            test.assertNotNull(java);
                            test.assertNull(java.getVersion());
                            test.assertNull(java.getMainClass());
                            test.assertNull(java.getShortcutName());
                            test.assertEqual(Iterable.create(), java.getDependencies());
                        });
                });

                runner.test("with one empty java dependency", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": { \"dependencies\": [ {} ] } }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
                            final ProjectJSONJava java = projectJSON.getJava();
                            test.assertNotNull(java);
                            test.assertNull(java.getVersion());
                            test.assertNull(java.getMainClass());
                            test.assertNull(java.getShortcutName());
                            test.assertEqual(Iterable.create(new Dependency()), java.getDependencies());
                        });
                });

                runner.test("with one non-empty java dependency", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("{ \"java\": { \"dependencies\": [ { \"publisher\": \"a\", \"project\": \"b\", \"version\": \"c\" } ] } }").then(file::setContents));
                    test.assertSuccess(fileResult.thenResult(ProjectJSON::parse),
                        (ProjectJSON projectJSON) ->
                        {
                            test.assertNotNull(projectJSON);
                            test.assertEqual(null, projectJSON.getPublisher());
                            test.assertEqual(null, projectJSON.getProject());
                            test.assertEqual(null, projectJSON.getVersion());
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
                });

                runner.test("with maximumErrors string value", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").awaitError();
                    file.setContentsAsString("{ \"java\": { \"maximumErrors\": \"50\" } }").awaitError();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).awaitError();
                    test.assertNotNull(projectJSON);
                    test.assertEqual(null, projectJSON.getPublisher());
                    test.assertEqual(null, projectJSON.getProject());
                    test.assertEqual(null, projectJSON.getVersion());

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
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").awaitError();
                    file.setContentsAsString("{ \"java\": { \"maximumErrors\": -2 } }").awaitError();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).awaitError();
                    test.assertNotNull(projectJSON);
                    test.assertEqual(null, projectJSON.getPublisher());
                    test.assertEqual(null, projectJSON.getProject());
                    test.assertEqual(null, projectJSON.getVersion());

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
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").awaitError();
                    file.setContentsAsString("{ \"java\": { \"maximumErrors\": 0 } }").awaitError();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).awaitError();
                    test.assertNotNull(projectJSON);
                    test.assertEqual(null, projectJSON.getPublisher());
                    test.assertEqual(null, projectJSON.getProject());
                    test.assertEqual(null, projectJSON.getVersion());

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
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").awaitError();
                    file.setContentsAsString("{ \"java\": { \"maximumErrors\": 100 } }").awaitError();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).awaitError();
                    test.assertNotNull(projectJSON);
                    test.assertEqual(null, projectJSON.getPublisher());
                    test.assertEqual(null, projectJSON.getProject());
                    test.assertEqual(null, projectJSON.getVersion());

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
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").awaitError();
                    file.setContentsAsString("{ \"java\": { \"maximumWarnings\": \"50\" } }").awaitError();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).awaitError();
                    test.assertNotNull(projectJSON);
                    test.assertEqual(null, projectJSON.getPublisher());
                    test.assertEqual(null, projectJSON.getProject());
                    test.assertEqual(null, projectJSON.getVersion());

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
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").awaitError();
                    file.setContentsAsString("{ \"java\": { \"maximumWarnings\": -2 } }").awaitError();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).awaitError();
                    test.assertNotNull(projectJSON);
                    test.assertEqual(null, projectJSON.getPublisher());
                    test.assertEqual(null, projectJSON.getProject());
                    test.assertEqual(null, projectJSON.getVersion());

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
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").awaitError();
                    file.setContentsAsString("{ \"java\": { \"maximumWarnings\": 0 } }").awaitError();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).awaitError();
                    test.assertNotNull(projectJSON);
                    test.assertEqual(null, projectJSON.getPublisher());
                    test.assertEqual(null, projectJSON.getProject());
                    test.assertEqual(null, projectJSON.getVersion());

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
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final File file = fileSystem.createFile("/project.json").awaitError();
                    file.setContentsAsString("{ \"java\": { \"maximumWarnings\": 100 } }").awaitError();

                    final ProjectJSON projectJSON = ProjectJSON.parse(file).awaitError();
                    test.assertNotNull(projectJSON);
                    test.assertEqual(null, projectJSON.getPublisher());
                    test.assertEqual(null, projectJSON.getProject());
                    test.assertEqual(null, projectJSON.getVersion());

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
