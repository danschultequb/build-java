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

            runner.testGroup("setMainClass(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setMainClass(null);
                    test.assertEqual(null, projectJSON.getMainClass());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setMainClass("");
                    test.assertEqual("", projectJSON.getMainClass());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setMainClass("apples");
                    test.assertEqual("apples", projectJSON.getMainClass());
                });
            });

            runner.testGroup("setShortcutName(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setShortcutName(null);
                    test.assertEqual(null, projectJSON.getShortcutName());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setShortcutName("");
                    test.assertEqual("", projectJSON.getShortcutName());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setShortcutName("apples");
                    test.assertEqual("apples", projectJSON.getShortcutName());
                });
            });

            runner.testGroup("setJavaSourcesVersion(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setJavaSourcesVersion((String)null);
                    test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setJavaSourcesVersion("");
                    test.assertEqual("", projectJSON.getJavaSourcesVersion());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setJavaSourcesVersion("apples");
                    test.assertEqual("apples", projectJSON.getJavaSourcesVersion());
                });
            });

            runner.testGroup("setJavaTestsVersion(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setJavaTestsVersion((String)null);
                    test.assertEqual(null, projectJSON.getJavaTestsVersion());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setJavaTestsVersion("");
                    test.assertEqual("", projectJSON.getJavaTestsVersion());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setJavaTestsVersion("apples");
                    test.assertEqual("apples", projectJSON.getJavaTestsVersion());
                });
            });

            runner.testGroup("setJavaDependencies(Iterable<Dependency>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setJavaDependencies(null);
                    test.assertEqual(null, projectJSON.getJavaDependencies());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    projectJSON.setJavaDependencies(Iterable.empty());
                    test.assertEqual(Iterable.empty(), projectJSON.getJavaDependencies());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final ProjectJSON projectJSON = new ProjectJSON();
                    final Iterable<Dependency> dependencies = Iterable.create(
                        new Dependency().setProject("a").setPublisher("b").setVersionRange("c"),
                        new Dependency().setProject("d").setPublisher("e").setVersionRange("f"));
                    projectJSON.setJavaDependencies(dependencies);
                    test.assertEqual(dependencies, projectJSON.getJavaDependencies());
                });
            });

            runner.testGroup("parse(File)", () ->
            {
                runner.test("with null file", (Test test) ->
                {
                    test.assertThrows(() -> ProjectJSON.parse(null), new PreConditionFailure("projectJSONFile cannot be null."));
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
                    test.assertError(new ParseException("The root of a project.json file must be a JSON object.", 0), file.thenResult(ProjectJSON::parse));
                });

                runner.test("with non-JSON object file contents", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("hello there").then(file::setContents));
                    test.assertError(new ParseException("The root of a project.json file must be a JSON object.", 0), fileResult.thenResult(ProjectJSON::parse));
                });

                runner.test("with empty JSON array", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Result<File> fileResult = fileSystem.createFile("/project.json");
                    fileResult.then((File file) -> CharacterEncoding.UTF_8.encode("[]").then(file::setContents));
                    test.assertError(new ParseException("The root of a project.json file must be a JSON object.", 0), fileResult.thenResult(ProjectJSON::parse));
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual("a", projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual("1.8", projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual("1.8", projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual("1.8", projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual("1.8", projectJSON.getJavaTestsVersion());
                            test.assertEqual(null, projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(Iterable.create(), projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(Iterable.create(new Dependency()), projectJSON.getJavaDependencies());
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
                            test.assertEqual(null, projectJSON.getShortcutName());
                            test.assertEqual(null, projectJSON.getMainClass());
                            test.assertEqual(null, projectJSON.getJavaSourcesVersion());
                            test.assertEqual(null, projectJSON.getJavaTestsVersion());
                            test.assertEqual(Iterable.create(new Dependency().setPublisher("a").setProject("b").setVersionRange("c")), projectJSON.getJavaDependencies());
                        });
                });
            });
        });
    }
}
