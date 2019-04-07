package qub;

public class ParseJSONTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(ParseJSON.class, () ->
        {
            runner.test("constructor()", (Test test) ->
            {
                final ParseJSON parseJson = new ParseJSON();
                test.assertNull(parseJson.getSourceFiles());
            });

            runner.testGroup("setSourceFiles()", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ParseJSON parseJson = new ParseJSON();
                    parseJson.setSourceFiles(null);
                    test.assertNull(parseJson.getSourceFiles());
                });

                runner.test("with empty", (Test test) ->
                {
                    final ParseJSON parseJson = new ParseJSON();
                    parseJson.setSourceFiles(Iterable.create());
                    test.assertEqual(Iterable.create(), parseJson.getSourceFiles());
                });

                runner.test("with source file with null relative path", (Test test) ->
                {
                    final ParseJSON parseJson = new ParseJSON();
                    parseJson.setSourceFiles(Iterable.create(
                        new ParseJSONSourceFile()
                            .setLastModified(DateTime.utc(5))));
                    test.assertEqual(
                        Iterable.create(
                            new ParseJSONSourceFile()
                            .setLastModified(DateTime.utc(5))),
                        parseJson.getSourceFiles());
                });
            });

            runner.testGroup("getSourceFile()", () ->
            {
                runner.test("with null path", (Test test) ->
                {
                    final ParseJSON parseJson = new ParseJSON();
                    test.assertThrows(() -> parseJson.getSourceFile(null), new PreConditionFailure("path cannot be null."));
                });

                runner.test("with null sourceFiles", (Test test) ->
                {
                    final ParseJSON parseJson = new ParseJSON();
                    parseJson.setSourceFiles(null);
                    test.assertThrows(() -> parseJson.getSourceFile(Path.parse("sources/A.java")).await(),
                        new NotFoundException("No source file found in the ParseJSON object with the path \"sources/A.java\"."));
                });

                runner.test("with empty sourceFiles", (Test test) ->
                {
                    final ParseJSON parseJson = new ParseJSON();
                    parseJson.setSourceFiles(Iterable.create());
                    test.assertThrows(() -> parseJson.getSourceFile(Path.parse("sources/A.java")).await(),
                        new NotFoundException("No source file found in the ParseJSON object with the path \"sources/A.java\"."));
                });

                runner.test("with non-empty sourceFiles and non-matching sourceFilePath", (Test test) ->
                {
                    final ParseJSON parseJson = new ParseJSON();
                    parseJson.setSourceFiles(Iterable.create(
                        new ParseJSONSourceFile()
                            .setRelativePath(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.utc(10))));
                    test.assertThrows(() -> parseJson.getSourceFile(Path.parse("sources/B.java")).await(),
                        new NotFoundException("No source file found in the ParseJSON object with the path \"sources/B.java\"."));
                });

                runner.test("with non-empty sourceFiles and matching sourceFilePath", (Test test) ->
                {
                    final ParseJSON parseJson = new ParseJSON();
                    parseJson.setSourceFiles(Iterable.create(
                        new ParseJSONSourceFile()
                            .setRelativePath(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.utc(10))));
                    test.assertEqual(
                        new ParseJSONSourceFile()
                            .setRelativePath(Path.parse("sources/A.java"))
                            .setLastModified(DateTime.utc(10)),
                        parseJson.getSourceFile(Path.parse("sources/A.java")).await());
                });
            });
        });
    }
}
