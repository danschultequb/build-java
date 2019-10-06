package qub;

public interface JavacJavaCompilerTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(JavacJavaCompiler.class, () ->
        {
            JavaCompilerTests.test(runner, (Test test) -> new JavacJavaCompiler(test.getProcess().getProcessFactory()));

            runner.testGroup("compile()", () ->
            {
                runner.test("with null sourceFiles", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(test.getProcess().getProcessFactory());
                    final Iterable<File> sourceFiles = null;
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, Warnings.Show),
                            new PreConditionFailure("sourceFiles cannot be null."));
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });

                runner.test("with empty sourceFiles", runner.skip(), (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(test.getProcess().getProcessFactory());
                    final Iterable<File> sourceFiles = Iterable.create();
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, Warnings.Show),
                            new PreConditionFailure("sourceFiles cannot be empty."));
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });

                runner.test("with null rootFolder", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(test.getProcess().getProcessFactory());
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").await());
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        test.assertThrows(() -> compiler.compile(sourceFiles, null, outputFolder, Warnings.Show),
                            new PreConditionFailure("rootFolder cannot be null."));
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });

                runner.test("with no PATH environment variable", (Test test) ->
                {
                    final Process process = new Process();
                    process.setEnvironmentVariables(new EnvironmentVariables());
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(process.getProcessFactory());
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").await());
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, Warnings.Show).await(),
                            new FileNotFoundException("javac"));
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });

                runner.test("with empty PATH environment variable", (Test test) ->
                {
                    final Process process = new Process();
                    process.setEnvironmentVariables(new EnvironmentVariables().set("PATH", ""));
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(process.getProcessFactory());
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").await());
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, Warnings.Show).await(),
                            new FileNotFoundException("javac"));
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });

                runner.test("with Java file that doesn't exist", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(test.getProcess().getProcessFactory());
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final File aJava = rootFolder.getFile("sources/A.java").await();
                        final Iterable<File> sourceFiles = Iterable.create(aJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        final JavaCompilationResult result = compiler.compile(sourceFiles, rootFolder, outputFolder, Warnings.Show).await();
                        test.assertNotNull(result);
                        test.assertEqual(2, result.exitCode);
                        test.assertEqual("", result.output);
                        test.assertEqual(
                            Iterable.create(
                                "error: file not found: sources\\A.java",
                                "Usage: javac <options> <source files>",
                                "use --help for a list of possible options"),
                            Strings.getLines(result.error));
                        test.assertEqual(Iterable.create(), result.issues);
                        test.assertFalse(QubBuild.getClassFile(aJava, rootFolder, outputFolder).exists().await());
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });

                runner.test("with empty Java file", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(test.getProcess().getProcessFactory());
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final File bJava = rootFolder.getFile("sources/B.java").await();
                        bJava.create();
                        final Iterable<File> sourceFiles = Iterable.create(bJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        final JavaCompilationResult result = compiler.compile(sourceFiles, rootFolder, outputFolder, Warnings.Show).await();
                        test.assertNotNull(result);
                        test.assertEqual(0, result.exitCode);
                        test.assertEqual("", result.output);
                        test.assertEqual("", result.error);
                        test.assertEqual(Iterable.create(), result.issues);
                        test.assertFalse(QubBuild.getClassFile(bJava, rootFolder, outputFolder).exists().await());
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });

                runner.test("with no errors", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(test.getProcess().getProcessFactory());
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").await();
                        cJava.setContentsAsString(
                            Strings.join(
                                '\n',
                                Iterable.create(
                                    "public class C",
                                    "{",
                                    "  private int value;",
                                    "  public int getValue()",
                                    "  {",
                                    "    return value;",
                                    "  }",
                                    "}")));
                        final Iterable<File> sourceFiles = Iterable.create(cJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        final JavaCompilationResult result = compiler.compile(sourceFiles, rootFolder, outputFolder, Warnings.Show).await();
                        test.assertNotNull(result);
                        test.assertEqual(0, result.exitCode);
                        test.assertEqual("", result.output);
                        test.assertEqual("", result.error);
                        test.assertEqual(Iterable.create(), result.issues);
                        test.assertTrue(QubBuild.getClassFile(cJava, rootFolder, outputFolder).exists().await());
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });

                runner.test("with \"error: class MyTestClass is public, should be declared in a file named MyTestClass.java\"", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(test.getProcess().getProcessFactory());
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").await();
                        cJava.setContentsAsString(
                            Strings.join(
                                '\n',
                                Iterable.create(
                                    "public class MyTestClass",
                                    "{",
                                    "  private int value;",
                                    "  public int getValue()",
                                    "  {",
                                    "    return value;",
                                    "  }",
                                    "}")));
                        final Iterable<File> sourceFiles = Iterable.create(cJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        final JavaCompilationResult result = compiler.compile(sourceFiles, rootFolder, outputFolder, Warnings.Show).await();
                        test.assertNotNull(result);
                        test.assertEqual(1, result.exitCode);
                        test.assertEqual("", result.output);
                        test.assertEqual(
                            Iterable.create(
                                "sources\\C.java:1: error: class MyTestClass is public, should be declared in a file named MyTestClass.java",
                                "public class MyTestClass",
                                "       ^",
                                "1 error"),
                            Strings.getLines(result.error));
                        test.assertEqual(
                            Iterable.create(
                                JavaCompiler.error(
                                    "sources/C.java",
                                    1, 8,
                                    "class MyTestClass is public, should be declared in a file named MyTestClass.java")),
                            result.issues);
                        test.assertFalse(QubBuild.getClassFile(cJava, rootFolder, outputFolder).exists().await());
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });

                runner.test("with \"error: class, interface, or enum expected\"", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(test.getProcess().getProcessFactory());
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").await();
                        cJava.setContentsAsString("Im not a valid Java file");
                        final Iterable<File> sourceFiles = Iterable.create(cJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        final JavaCompilationResult result = compiler.compile(sourceFiles, rootFolder, outputFolder, Warnings.Show).await();
                        test.assertNotNull(result);
                        test.assertEqual(1, result.exitCode);
                        test.assertEqual("", result.output);
                        test.assertEqual(
                            Iterable.create(
                                "sources\\C.java:1: error: class, interface, or enum expected",
                                "Im not a valid Java file",
                                "^",
                                "1 error"),
                            Strings.getLines(result.error));
                        test.assertEqual(Iterable.create(
                            JavaCompiler.error(
                                "sources/C.java",
                                1, 1,
                                "class, interface, or enum expected")),
                            result.issues);
                        test.assertFalse(QubBuild.getClassFile(cJava, rootFolder, outputFolder).exists().await());
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });

                runner.test("with \"error: ';' expected\" and \"error: reached end of file while parsing\"", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler(test.getProcess().getProcessFactory());
                    final Folder currentFolder = test.getProcess().getCurrentFolder().await();
                    final Folder rootFolder = currentFolder.createFolder("temp").await();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").await();
                        cJava.setContentsAsString(
                            Strings.join(
                                '\n',
                                Iterable.create(
                                    "public class MyTestClass",
                                    "{",
                                    "  private int value;",
                                    "  public int getValue()",
                                    "  {",
                                    "    return value",
                                    "  }",
                                    "")));
                        final Iterable<File> sourceFiles = Iterable.create(cJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").await();
                        final JavaCompilationResult result = compiler.compile(sourceFiles, rootFolder, outputFolder, Warnings.Show).await();
                        test.assertNotNull(result);
                        test.assertEqual(1, result.exitCode);
                        test.assertEqual("", result.output);
                        test.assertEqual(
                            Iterable.create(
                                "sources\\C.java:6: error: \';\' expected",
                                "    return value",
                                "                ^",
                                "sources\\C.java:7: error: reached end of file while parsing",
                                "  }",
                                "   ^",
                                "2 errors"),
                            Strings.getLines(result.error));
                        test.assertEqual(
                            Iterable.create(
                                JavaCompiler.error(
                                    "sources/C.java",
                                    6, 17,
                                    "';' expected"),
                                JavaCompiler.error(
                                    "sources/C.java",
                                    7, 4,
                                    "reached end of file while parsing")),
                            result.issues);
                        test.assertFalse(QubBuild.getClassFile(cJava, rootFolder, outputFolder).exists().await());
                    }
                    finally
                    {
                        test.assertNull(rootFolder.delete().await());
                    }
                });
            });
        });
    }
}
