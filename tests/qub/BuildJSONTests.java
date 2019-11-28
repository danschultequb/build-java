package qub;

public interface BuildJSONTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(BuildJSON.class, () ->
        {
            runner.test("constructor()", (Test test) ->
            {
                final BuildJSON buildJson = new BuildJSON();
                test.assertNull(buildJson.getProjectJson());
                test.assertNull(buildJson.getSourceFiles());
            });

            runner.testGroup("setSourceFiles()", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSON buildJson = new BuildJSON();
                    buildJson.setSourceFiles(null);
                    test.assertNull(buildJson.getSourceFiles());
                });

                runner.test("with empty", (Test test) ->
                {
                    final BuildJSON buildJson = new BuildJSON();
                    buildJson.setSourceFiles(Iterable.create());
                    test.assertEqual(Iterable.create(), buildJson.getSourceFiles());
                });

                runner.test("with source file with null relative path", (Test test) ->
                {
                    final BuildJSON buildJson = new BuildJSON();
                    buildJson.setSourceFiles(Iterable.create(
                        new BuildJSONSourceFile()
                            .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)))));
                    test.assertEqual(
                        Iterable.create(
                            new BuildJSONSourceFile()
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)))),
                        buildJson.getSourceFiles());
                });
            });

            runner.testGroup("getSourceFile()", () ->
            {
                runner.test("with null relativePath", (Test test) ->
                {
                    final BuildJSON buildJson = new BuildJSON();
                    test.assertThrows(() -> buildJson.getSourceFile(null),
                        new PreConditionFailure("relativePath cannot be null."));
                });

                runner.test("with null sourceFiles", (Test test) ->
                {
                    final BuildJSON buildJson = new BuildJSON();
                    buildJson.setSourceFiles(null);
                    test.assertThrows(() -> buildJson.getSourceFile(Path.parse("sources/A.java")).await(),
                        new NotFoundException("No source file found in the BuildJSON object with the path \"sources/A.java\"."));
                });

                runner.test("with empty sourceFiles", (Test test) ->
                {
                    final BuildJSON buildJson = new BuildJSON();
                    buildJson.setSourceFiles(Iterable.create());
                    test.assertThrows(() -> buildJson.getSourceFile(Path.parse("sources/A.java")).await(),
                        new NotFoundException("No source file found in the BuildJSON object with the path \"sources/A.java\"."));
                });

                runner.test("with non-empty sourceFiles and non-matching sourceFilePath", (Test test) ->
                {
                    final BuildJSON buildJson = new BuildJSON();
                    buildJson.setSourceFiles(Iterable.create(
                        new BuildJSONSourceFile()
                            .setRelativePath(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(10)))));
                    test.assertThrows(() -> buildJson.getSourceFile(Path.parse("sources/B.java")).await(),
                        new NotFoundException("No source file found in the BuildJSON object with the path \"sources/B.java\"."));
                });

                runner.test("with non-empty sourceFiles and matching sourceFilePath", (Test test) ->
                {
                    final BuildJSON buildJson = new BuildJSON();
                    buildJson.setSourceFiles(Iterable.create(
                        new BuildJSONSourceFile()
                            .setRelativePath(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(10)))));
                    test.assertEqual(
                        new BuildJSONSourceFile()
                            .setRelativePath(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(10))),
                        buildJson.getSourceFile(Path.parse("sources/A.java")).await());
                });
            });
        });
    }
}
