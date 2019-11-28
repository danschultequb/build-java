package qub;

public interface BuildJSONSourceFileTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(BuildJSONSourceFile.class, () ->
        {
            runner.test("constructor()", (Test test) ->
            {
                final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile();
                test.assertNull(parseJsonSourceFile.getRelativePath());
                test.assertNull(parseJsonSourceFile.getLastModified());
                test.assertNull(parseJsonSourceFile.getDependencies());
            });

            runner.testGroup("equals(Object)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertFalse(parseJsonSourceFile.equals((Object)null));
                });

                runner.test("with non-BuildJSONSourceFile", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertFalse(parseJsonSourceFile.equals("Hello"));
                });

                runner.test("with different relativePath", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertFalse(
                        parseJsonSourceFile.equals((Object)
                            new BuildJSONSourceFile()
                                .setRelativePath(Path.parse("b"))
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)))));
                });

                runner.test("with different lastModified", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertFalse(
                        parseJsonSourceFile.equals((Object)
                            new BuildJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(6)))));
                });

                runner.test("with different dependencies", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertFalse(
                        parseJsonSourceFile.equals((Object)
                            new BuildJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(6)))
                                .setDependencies(Iterable.create())));
                });

                runner.test("with equal", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertTrue(
                        parseJsonSourceFile.equals((Object)
                            new BuildJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)))));
                });

                runner.test("with same", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertTrue(parseJsonSourceFile.equals((Object)parseJsonSourceFile));
                });
            });

            runner.testGroup("equals(Object)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertFalse(parseJsonSourceFile.equals((BuildJSONSourceFile)null));
                });

                runner.test("with different relativePath", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertFalse(
                        parseJsonSourceFile.equals(
                            new BuildJSONSourceFile()
                                .setRelativePath(Path.parse("b"))
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)))));
                });

                runner.test("with different lastModified", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertFalse(
                        parseJsonSourceFile.equals(
                            new BuildJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(6)))));
                });

                runner.test("with different dependencies", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertFalse(
                        parseJsonSourceFile.equals(
                            new BuildJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(6)))
                                .setDependencies(Iterable.create())));
                });

                runner.test("with equal", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertTrue(
                        parseJsonSourceFile.equals(
                            new BuildJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)))));
                });

                runner.test("with same", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)));
                    test.assertTrue(parseJsonSourceFile.equals(parseJsonSourceFile));
                });
            });

            runner.testGroup("toString()", () ->
            {
                runner.test("with no properties", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile();
                    test.assertEqual("null:{}", parseJsonSourceFile.toString());
                });

                runner.test("with all properties", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.nanoseconds(20)))
                        .setDependencies(Iterable.create(Path.parse("b"), Path.parse("c")));
                    test.assertEqual(
                        "\"a\":{\"lastModifiedNanoseconds\":20,\"dependencies\":[\"b\",\"c\"]}",
                        parseJsonSourceFile.toString());
                });
            });
        });
    }
}
