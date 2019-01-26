package qub;

public class ParseJSONSourceFileTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(ParseJSONSourceFile.class, () ->
        {
            runner.test("constructor()", (Test test) ->
            {
                final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile();
                test.assertNull(parseJsonSourceFile.getRelativePath());
                test.assertNull(parseJsonSourceFile.getLastModified());
                test.assertNull(parseJsonSourceFile.getDependencies());
            });

            runner.testGroup("equals(Object)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertFalse(parseJsonSourceFile.equals((Object)null));
                });

                runner.test("with non-ParseJSONSourceFile", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertFalse(parseJsonSourceFile.equals("Hello"));
                });

                runner.test("with different relativePath", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertFalse(
                        parseJsonSourceFile.equals((Object)
                            new ParseJSONSourceFile()
                                .setRelativePath(Path.parse("b"))
                                .setLastModified(DateTime.utc(5))));
                });

                runner.test("with different lastModified", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertFalse(
                        parseJsonSourceFile.equals((Object)
                            new ParseJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.utc(6))));
                });

                runner.test("with different dependencies", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertFalse(
                        parseJsonSourceFile.equals((Object)
                            new ParseJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.utc(6))
                                .setDependencies(Iterable.empty())));
                });

                runner.test("with equal", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertTrue(
                        parseJsonSourceFile.equals((Object)
                            new ParseJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.utc(5))));
                });

                runner.test("with same", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertTrue(parseJsonSourceFile.equals((Object)parseJsonSourceFile));
                });
            });

            runner.testGroup("equals(Object)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertFalse(parseJsonSourceFile.equals((ParseJSONSourceFile)null));
                });

                runner.test("with different relativePath", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertFalse(
                        parseJsonSourceFile.equals(
                            new ParseJSONSourceFile()
                                .setRelativePath(Path.parse("b"))
                                .setLastModified(DateTime.utc(5))));
                });

                runner.test("with different lastModified", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertFalse(
                        parseJsonSourceFile.equals(
                            new ParseJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.utc(6))));
                });

                runner.test("with different dependencies", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertFalse(
                        parseJsonSourceFile.equals(
                            new ParseJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.utc(6))
                                .setDependencies(Iterable.empty())));
                });

                runner.test("with equal", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertTrue(
                        parseJsonSourceFile.equals(
                            new ParseJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.utc(5))));
                });

                runner.test("with same", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(5));
                    test.assertTrue(parseJsonSourceFile.equals(parseJsonSourceFile));
                });
            });

            runner.testGroup("toString()", () ->
            {
                runner.test("with no properties", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile();
                    test.assertEqual("{}", parseJsonSourceFile.toString());
                });

                runner.test("with all properties", (Test test) ->
                {
                    final ParseJSONSourceFile parseJsonSourceFile = new ParseJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.utc(20))
                        .setDependencies(Iterable.create(Path.parse("b"), Path.parse("c")));
                    test.assertEqual(
                        "{\"relativePath\":\"a\",\"lastModified\":20,\"dependencies\":[\"b\",\"c\"]}",
                        parseJsonSourceFile.toString());
                });
            });
        });
    }
}
