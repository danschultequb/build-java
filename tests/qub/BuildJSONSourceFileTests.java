package qub;

public interface BuildJSONSourceFileTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(BuildJSONSourceFile.class, () ->
        {
            runner.test("constructor()", (Test test) ->
            {
                final BuildJSONSourceFile parseJsonSourceFile = BuildJSONSourceFile.create("a");
                test.assertEqual("a", parseJsonSourceFile.getRelativePath().toString());
                test.assertNull(parseJsonSourceFile.getLastModified());
                test.assertNull(parseJsonSourceFile.getDependencies());
            });

            runner.testGroup("setLastModified(DateTime)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = BuildJSONSourceFile.create("a");
                    test.assertThrows(() -> sourceFile.setLastModified(null),
                        new PreConditionFailure("lastModified cannot be null."));
                    test.assertNull(sourceFile.getLastModified());
                });

                runner.test("with non-null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = BuildJSONSourceFile.create("a");
                    final DateTime now = DateTime.create(2020, 3, 17, 12, 40);
                    final BuildJSONSourceFile setLastModifiedResult = sourceFile.setLastModified(now);
                    test.assertSame(sourceFile, setLastModifiedResult);
                    test.assertEqual(now, sourceFile.getLastModified());
                });
            });

            runner.testGroup("setDependencies(Iterable<Path>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = BuildJSONSourceFile.create("a");
                    test.assertThrows(() -> sourceFile.setDependencies(null),
                        new PreConditionFailure("dependencies cannot be null."));
                    test.assertNull(sourceFile.getDependencies());
                });

                runner.test("with empty", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = BuildJSONSourceFile.create("a");
                    final Iterable<Path> dependencies = Iterable.create();
                    final BuildJSONSourceFile setDependenciesResult = sourceFile.setDependencies(dependencies);
                    test.assertSame(sourceFile, setDependenciesResult);
                    test.assertEqual(dependencies, sourceFile.getDependencies());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = BuildJSONSourceFile.create("a");
                    final Iterable<Path> dependencies = Iterable.create(Path.parse("hello/there.java"));
                    final BuildJSONSourceFile setDependenciesResult = sourceFile.setDependencies(dependencies);
                    test.assertSame(sourceFile, setDependenciesResult);
                    test.assertEqual(dependencies, sourceFile.getDependencies());
                });
            });

            runner.testGroup("addIssue(JavaCompilerIssue)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = BuildJSONSourceFile.create("a");
                    test.assertThrows(() -> sourceFile.addIssue(null),
                        new PreConditionFailure("issue cannot be null."));
                    test.assertEqual(Iterable.create(), sourceFile.getIssues());
                });

                runner.test("with non-null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = BuildJSONSourceFile.create("a");
                    final JavaCompilerIssue issue = new JavaCompilerIssue("a.java", 1, 2, Issue.Type.Warning, "Hello!");
                    test.assertSame(sourceFile, sourceFile.addIssue(issue));
                    test.assertEqual(Iterable.create(issue), sourceFile.getIssues());
                });
            });

            runner.testGroup("setIssues(Iterable<JavaCompilerIssue>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = BuildJSONSourceFile.create("a");
                    test.assertThrows(() -> sourceFile.setIssues(null),
                        new PreConditionFailure("issues cannot be null."));
                    test.assertEqual(Iterable.create(), sourceFile.getIssues());
                });

                runner.test("with empty", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = BuildJSONSourceFile.create("a");
                    test.assertSame(sourceFile, sourceFile.setIssues(Iterable.create()));
                    test.assertEqual(Iterable.create(), sourceFile.getIssues());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final BuildJSONSourceFile sourceFile = BuildJSONSourceFile.create("a");
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
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    null,
                    false);
                equalsTest.run(
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    "Hello",
                    false);
                equalsTest.run(
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    BuildJSONSourceFile.create("b")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    false);
                equalsTest.run(
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(6))),
                    false);
                equalsTest.run(
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5)))
                        .setDependencies(Iterable.create()),
                    false);
                equalsTest.run(
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5)))
                        .addIssue(new JavaCompilerIssue("a.java", 1, 2, Issue.Type.Warning, "Help!")),
                    false);
                equalsTest.run(
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
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
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    null,
                    false);
                equalsTest.run(
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    BuildJSONSourceFile.create("b")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    false);
                equalsTest.run(
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(6))),
                    false);
                equalsTest.run(
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5)))
                        .setDependencies(Iterable.create()),
                    false);
                equalsTest.run(
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.milliseconds(5))),
                    true);
            });

            runner.testGroup("toString()", () ->
            {
                runner.test("with no properties", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = BuildJSONSourceFile.create("a");
                    test.assertEqual("\"a\":{}", parseJsonSourceFile.toString());
                });

                runner.test("with all properties", (Test test) ->
                {
                    final BuildJSONSourceFile parseJsonSourceFile = BuildJSONSourceFile.create("a")
                        .setLastModified(DateTime.epoch.plus(Duration.nanoseconds(20)))
                        .setDependencies(Iterable.create(Path.parse("b"), Path.parse("c")));
                    test.assertEqual(
                        "\"a\":{\"lastModified\":\"1970-01-01T00:00:00.000000020Z\",\"dependencies\":[\"b\",\"c\"]}",
                        parseJsonSourceFile.toString());
                });
            });
        });
    }
}
