package qub;

public class DependencyTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(Dependency.class, () ->
        {
            runner.test("constructor", (Test test) ->
            {
                final Dependency dependency = new Dependency();
                test.assertNull(dependency.getPublisher());
                test.assertNull(dependency.getProject());
                test.assertNull(dependency.getVersion());
            });

            runner.testGroup("setPublisher(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    test.assertSame(dependency, dependency.setPublisher(null));
                    test.assertEqual(null, dependency.getPublisher());
                });

                runner.test("with empty", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    test.assertSame(dependency, dependency.setPublisher(""));
                    test.assertEqual("", dependency.getPublisher());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    test.assertSame(dependency, dependency.setPublisher("oranges"));
                    test.assertEqual("oranges", dependency.getPublisher());
                });
            });

            runner.testGroup("setProject(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    test.assertSame(dependency, dependency.setProject(null));
                    test.assertEqual(null, dependency.getProject());
                });

                runner.test("with empty", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    test.assertSame(dependency, dependency.setProject(""));
                    test.assertEqual("", dependency.getProject());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    test.assertSame(dependency, dependency.setProject("oranges"));
                    test.assertEqual("oranges", dependency.getProject());
                });
            });

            runner.testGroup("setVersion(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    test.assertSame(dependency, dependency.setVersion(null));
                    test.assertEqual(null, dependency.getVersion());
                });

                runner.test("with empty", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    test.assertSame(dependency, dependency.setVersion(""));
                    test.assertEqual("", dependency.getVersion());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    test.assertSame(dependency, dependency.setVersion("oranges"));
                    test.assertEqual("oranges", dependency.getVersion());
                });
            });

            runner.testGroup("equals(Object)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final Dependency dependency = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    test.assertFalse(dependency.equals((Object)null));
                });

                runner.test("with String", (Test test) ->
                {
                    final Dependency dependency = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    test.assertFalse(dependency.equals((Object)"hello"));
                });

                runner.test("with different publisher", (Test test) ->
                {
                    final Dependency lhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    final Dependency rhs = new Dependency().setPublisher("x").setProject("b").setVersion("c");
                    test.assertFalse(lhs.equals((Object)rhs));
                });

                runner.test("with different project", (Test test) ->
                {
                    final Dependency lhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    final Dependency rhs = new Dependency().setPublisher("a").setProject("y").setVersion("c");
                    test.assertFalse(lhs.equals((Object)rhs));
                });

                runner.test("with different versionRange", (Test test) ->
                {
                    final Dependency lhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    final Dependency rhs = new Dependency().setPublisher("a").setProject("b").setVersion("z");
                    test.assertFalse(lhs.equals((Object)rhs));
                });

                runner.test("with equal Dependency", (Test test) ->
                {
                    final Dependency lhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    final Dependency rhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    test.assertTrue(lhs.equals((Object)rhs));
                });

                runner.test("with same Dependency", (Test test) ->
                {
                    final Dependency lhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    test.assertTrue(lhs.equals((Object)lhs));
                });
            });

            runner.testGroup("equals(Dependency)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final Dependency dependency = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    test.assertFalse(dependency.equals((Dependency)null));
                });

                runner.test("with different publisher", (Test test) ->
                {
                    final Dependency lhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    final Dependency rhs = new Dependency().setPublisher("x").setProject("b").setVersion("c");
                    test.assertFalse(lhs.equals(rhs));
                });

                runner.test("with different project", (Test test) ->
                {
                    final Dependency lhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    final Dependency rhs = new Dependency().setPublisher("a").setProject("y").setVersion("c");
                    test.assertFalse(lhs.equals(rhs));
                });

                runner.test("with different versionRange", (Test test) ->
                {
                    final Dependency lhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    final Dependency rhs = new Dependency().setPublisher("a").setProject("b").setVersion("z");
                    test.assertFalse(lhs.equals(rhs));
                });

                runner.test("with equal Dependency", (Test test) ->
                {
                    final Dependency lhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    final Dependency rhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    test.assertTrue(lhs.equals(rhs));
                });

                runner.test("with same Dependency", (Test test) ->
                {
                    final Dependency lhs = new Dependency().setPublisher("a").setProject("b").setVersion("c");
                    test.assertTrue(lhs.equals(lhs));
                });
            });

            runner.testGroup("toString()", () ->
            {
                runner.test("with no properties", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    test.assertEqual("{}", dependency.toString());
                });

                runner.test("with all properties", (Test test) ->
                {
                    final Dependency dependency = new Dependency();
                    dependency.setPublisher("a");
                    dependency.setProject("b");
                    dependency.setVersion("c");
                    test.assertEqual("{\"publisher\":\"a\",\"project\":\"b\",\"version\":\"c\"}", dependency.toString());
                });
            });
        });
    }
}
