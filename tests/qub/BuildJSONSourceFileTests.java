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

            runner.testGroup("setRelativePath(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertThrows(() -> sourceFile.setRelativePath((String)null),
                        new PreConditionFailure("relativePath cannot be null."));
                    test.assertEqual(null, sourceFile.getRelativePath());
                });

                runner.test("with empty", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertThrows(() -> sourceFile.setRelativePath(""),
                        new PreConditionFailure("relativePath cannot be empty."));
                    test.assertEqual(null, sourceFile.getRelativePath());
                });

                runner.test("with absolute path", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertThrows(() -> sourceFile.setRelativePath("/hello/there.java"),
                        new PreConditionFailure("relativePath.isRooted() cannot be true."));
                    test.assertEqual(null, sourceFile.getRelativePath());
                });

                runner.test("with relative path", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertSame(sourceFile, sourceFile.setRelativePath("hello/there.java"));
                    test.assertEqual(Path.parse("hello/there.java"), sourceFile.getRelativePath());
                });
            });

            runner.testGroup("setRelativePath(Path)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertThrows(() -> sourceFile.setRelativePath((Path)null),
                        new PreConditionFailure("relativePath cannot be null."));
                    test.assertEqual(null, sourceFile.getRelativePath());
                });

                runner.test("with absolute path", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertThrows(() -> sourceFile.setRelativePath(Path.parse("/hello/there.java")),
                        new PreConditionFailure("relativePath.isRooted() cannot be true."));
                    test.assertEqual(null, sourceFile.getRelativePath());
                });

                runner.test("with relative path", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertSame(sourceFile, sourceFile.setRelativePath(Path.parse("hello/there.java")));
                    test.assertEqual(Path.parse("hello/there.java"), sourceFile.getRelativePath());
                });
            });

            runner.testGroup("setLastModified(DateTime)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertSame(sourceFile, sourceFile.setLastModified(null));
                    test.assertNull(sourceFile.getLastModified());
                });

                runner.test("with non-null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    final DateTime now = test.getClock().getCurrentDateTime();
                    test.assertSame(sourceFile, sourceFile.setLastModified(now));
                    test.assertSame(now, sourceFile.getLastModified());
                });
            });

            runner.testGroup("setDependencies(Iterable<Path>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertSame(sourceFile, sourceFile.setDependencies(null));
                    test.assertNull(sourceFile.getDependencies());
                });

                runner.test("with empty", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    final Iterable<Path> dependencies = Iterable.create();
                    test.assertSame(sourceFile, sourceFile.setDependencies(dependencies));
                    test.assertSame(dependencies, sourceFile.getDependencies());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    final Iterable<Path> dependencies = Iterable.create(Path.parse("hello/there.java"));
                    test.assertSame(sourceFile, sourceFile.setDependencies(dependencies));
                    test.assertSame(dependencies, sourceFile.getDependencies());
                });
            });

            runner.testGroup("addIssue(JavaCompilerIssue)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertThrows(() -> sourceFile.addIssue(null),
                        new PreConditionFailure("issue cannot be null."));
                    test.assertEqual(Iterable.create(), sourceFile.getIssues());
                });

                runner.test("with non-null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    final JavaCompilerIssue issue = new JavaCompilerIssue("a.java", 1, 2, Issue.Type.Warning, "Hello!");
                    test.assertSame(sourceFile, sourceFile.addIssue(issue));
                    test.assertEqual(Iterable.create(issue), sourceFile.getIssues());
                });
            });

            runner.testGroup("setIssues(Iterable<JavaCompilerIssue>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertThrows(() -> sourceFile.setIssues(null),
                        new PreConditionFailure("issues cannot be null."));
                    test.assertEqual(Iterable.create(), sourceFile.getIssues());
                });

                runner.test("with empty", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    test.assertSame(sourceFile, sourceFile.setIssues(Iterable.create()));
                    test.assertEqual(Iterable.create(), sourceFile.getIssues());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = new BuildJSONSourceFile();
                    final Iterable<JavaCompilerIssue> issues = Iterable.create(new JavaCompilerIssue("a.java", 1, 2, Issue.Type.Warning, "Hello!"));
                    test.assertSame(sourceFile, sourceFile.setIssues(issues));
                    test.assertEqual(issues, sourceFile.getIssues());
                    test.assertNotSame(issues, sourceFile.getIssues());
                });
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<BuildJSONSourceFile,Object,Boolean> equalsTest = (BuildJSONSourceFile sourceFile, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + sourceFile + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, sourceFile.equals(rhs));
                    });
                };

                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    null,
                    false);
                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    "Hello",
                    false);
                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("b"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    false);
                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(6))),
                    false);
                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)))
                        .setDependencies(Iterable.create()),
                    false);
                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)))
                        .addIssue(new JavaCompilerIssue("a.java", 1, 2, Issue.Type.Warning, "Help!")),
                    false);
                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    new BuildJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    true);
            });

            runner.testGroup("equals(BuildJSONSourceFile)", () ->
            {
                final Action3<BuildJSONSourceFile,BuildJSONSourceFile,Boolean> equalsTest = (BuildJSONSourceFile sourceFile, BuildJSONSourceFile rhs, Boolean expected) ->
                {
                    runner.test("with " + sourceFile + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, sourceFile.equals(rhs));
                    });
                };

                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    null,
                    false);
                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("b"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    false);
                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(6))),
                    false);
                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5)))
                        .setDependencies(Iterable.create()),
                    false);
                equalsTest.run(
                    new BuildJSONSourceFile()
                        .setRelativePath(Path.parse("a"))
                        .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    new BuildJSONSourceFile()
                                .setRelativePath(Path.parse("a"))
                                .setLastModified(DateTime.createFromDurationSinceEpoch(Duration.milliseconds(5))),
                    true);
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
                        "\"a\":{\"lastModified\":\"1970-01-01T00:00:00.000000020Z\",\"dependencies\":[\"b\",\"c\"]}",
                        parseJsonSourceFile.toString());
                });
            });
        });
    }
}
