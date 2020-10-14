package qub;

public interface BuildJSONTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(BuildJSON.class, () ->
        {
            runner.test("create()", (Test test) ->
            {
                final BuildJSON buildJson = BuildJSON.create();
                test.assertNotNull(buildJson);
                test.assertNull(buildJson.getProjectJson());
                test.assertEqual(Iterable.create(), buildJson.getSourceFiles());
            });

            runner.testGroup("setSourceFiles()", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    test.assertThrows(() -> buildJson.setSourceFiles(null),
                        new PreConditionFailure("sourceFiles cannot be null."));
                    test.assertEqual(Iterable.create(), buildJson.getSourceFiles());
                });

                runner.test("with empty", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    final BuildJSON setSourceFilesResult = buildJson.setSourceFiles(Iterable.create());
                    test.assertSame(buildJson, setSourceFilesResult);
                    test.assertEqual(Iterable.create(), buildJson.getSourceFiles());
                });

                runner.test("with source file with null relative path", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    final BuildJSON setSourceFilesResult = buildJson.setSourceFiles(Iterable.create(
                        BuildJSONSourceFile.create("sources/A.java")
                            .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5)))));
                    test.assertSame(buildJson, setSourceFilesResult);
                    test.assertEqual(
                        Iterable.create(
                            BuildJSONSourceFile.create("sources/A.java")
                                .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5)))),
                        buildJson.getSourceFiles());
                });
            });

            runner.testGroup("getSourceFile(String)", () ->
            {
                runner.test("with null relativePath", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    test.assertThrows(() -> buildJson.getSourceFile((String)null),
                        new PreConditionFailure("relativePath cannot be null."));
                });

                runner.test("with null relativePath", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    test.assertThrows(() -> buildJson.getSourceFile(""),
                        new PreConditionFailure("relativePath cannot be empty."));
                });

                runner.test("with empty sourceFiles", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    buildJson.setSourceFiles(Iterable.create());
                    test.assertThrows(() -> buildJson.getSourceFile("sources/A.java").await(),
                        new NotFoundException("No source file found in the BuildJSON object with the path \"sources/A.java\"."));
                });

                runner.test("with non-empty sourceFiles and non-matching sourceFilePath", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    buildJson.setSourceFiles(Iterable.create(
                        BuildJSONSourceFile.create(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.epoch.plus(Duration.milliseconds(10)))));
                    test.assertThrows(() -> buildJson.getSourceFile("sources/B.java").await(),
                        new NotFoundException("No source file found in the BuildJSON object with the path \"sources/B.java\"."));
                });

                runner.test("with non-empty sourceFiles and matching sourceFilePath", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    buildJson.setSourceFiles(Iterable.create(
                        BuildJSONSourceFile.create(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.epoch.plus(Duration.milliseconds(10)))));
                    test.assertEqual(
                        BuildJSONSourceFile.create(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.epoch.plus(Duration.milliseconds(10))),
                        buildJson.getSourceFile("sources/A.java").await());
                });
            });

            runner.testGroup("getSourceFile(Path)", () ->
            {
                runner.test("with null relativePath", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    test.assertThrows(() -> buildJson.getSourceFile((Path)null),
                        new PreConditionFailure("relativePath cannot be null."));
                });

                runner.test("with empty sourceFiles", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    buildJson.setSourceFiles(Iterable.create());
                    test.assertThrows(() -> buildJson.getSourceFile(Path.parse("sources/A.java")).await(),
                        new NotFoundException("No source file found in the BuildJSON object with the path \"sources/A.java\"."));
                });

                runner.test("with non-empty sourceFiles and non-matching sourceFilePath", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    buildJson.setSourceFiles(Iterable.create(
                        BuildJSONSourceFile.create(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.epoch.plus(Duration.milliseconds(10)))));
                    test.assertThrows(() -> buildJson.getSourceFile(Path.parse("sources/B.java")).await(),
                        new NotFoundException("No source file found in the BuildJSON object with the path \"sources/B.java\"."));
                });

                runner.test("with non-empty sourceFiles and matching sourceFilePath", (Test test) ->
                {
                    final BuildJSON buildJson = BuildJSON.create();
                    buildJson.setSourceFiles(Iterable.create(
                        BuildJSONSourceFile.create(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.epoch.plus(Duration.milliseconds(10)))));
                    test.assertEqual(
                        BuildJSONSourceFile.create(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.epoch.plus(Duration.milliseconds(10))),
                        buildJson.getSourceFile(Path.parse("sources/A.java")).await());
                });
            });

            runner.testGroup("toJson()", () ->
            {
                final Action2<BuildJSON,JSONObject> toJsonTest = (BuildJSON buildJson, JSONObject expected) ->
                {
                    runner.test("with " + buildJson, (Test test) ->
                    {
                        test.assertEqual(expected, buildJson.toJson());
                    });
                };

                toJsonTest.run(
                    BuildJSON.create(),
                    JSONObject.create());
                toJsonTest.run(
                    BuildJSON.create()
                        .setProjectJson(ProjectJSON.create()),
                    JSONObject.create()
                        .setObject("project.json", JSONObject.create()));
                toJsonTest.run(
                    BuildJSON.create()
                        .setProjectJson(ProjectJSON.create()
                            .setProject("hello")),
                    JSONObject.create()
                        .setObject("project.json", JSONObject.create()
                            .setString("project", "hello")));
                toJsonTest.run(
                    BuildJSON.create()
                        .setSourceFiles(Iterable.create()),
                    JSONObject.create()
                        .setObject("sourceFiles", JSONObject.create()));
                toJsonTest.run(
                    BuildJSON.create()
                        .setSourceFiles(Iterable.create(
                            BuildJSONSourceFile.create("hello.java"))),
                    JSONObject.create()
                        .setObject("sourceFiles", JSONObject.create()
                            .setObject("hello.java", JSONObject.create())));
                toJsonTest.run(
                    BuildJSON.create()
                        .setSourceFiles(Iterable.create(
                            BuildJSONSourceFile.create("hello.java")
                                .setLastModified(DateTime.epoch))),
                    JSONObject.create()
                        .setObject("sourceFiles", JSONObject.create()
                            .setObject("hello.java", JSONObject.create()
                                .setString("lastModified", "1970-01-01T00:00Z"))));
                toJsonTest.run(
                    BuildJSON.create()
                        .setProjectJson(ProjectJSON.create()
                            .setProject("hello"))
                        .setSourceFiles(Iterable.create(
                            BuildJSONSourceFile.create("hello.java")
                                .setLastModified(DateTime.epoch))),
                    JSONObject.create()
                        .setObject("project.json", JSONObject.create()
                            .setString("project", "hello"))
                        .setObject("sourceFiles", JSONObject.create()
                            .setObject("hello.java", JSONObject.create()
                                .setString("lastModified", "1970-01-01T00:00Z"))));
            });
        });
    }
}
