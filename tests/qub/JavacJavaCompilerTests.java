package qub;

public class JavacJavaCompilerTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(JavacJavaCompiler.class, () ->
        {
            JavaCompilerTests.test(runner, JavacJavaCompiler::new);

            runner.testGroup("compile()", () ->
            {
                runner.test("with null sourceFiles", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Iterable<File> sourceFiles = null;
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .awaitError();
                    final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                    final Process process = new Process();
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, process), new PreConditionFailure("sourceFiles cannot be null."));
                });

                runner.test("with empty sourceFiles", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Iterable<File> sourceFiles = Iterable.create();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .awaitError();
                    final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                    final Process process = new Process();
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, process), new PreConditionFailure("sourceFiles cannot be empty."));
                });

                runner.test("with null rootFolder", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .awaitError();
                    final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").awaitError());
                    final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                    final Process process = new Process();
                    test.assertThrows(() -> compiler.compile(sourceFiles, null, outputFolder, process), new PreConditionFailure("rootFolder cannot be null."));
                });

                runner.test("with null process", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .awaitError();
                    final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").awaitError());
                    final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                    final Process process = null;
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, process).awaitError(),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with no PATH environment variable", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .awaitError();
                    final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").awaitError());
                    final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                    final Process process = new Process();
                    process.setEnvironmentVariables(Map.create());
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, process).awaitError(),
                        new FileNotFoundException("javac"));
                });

                runner.test("with empty PATH environment variable", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .awaitError();
                    final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").awaitError());
                    final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                    final Process process = new Process();
                    process.setEnvironmentVariables(Map.<String,String>create().set("PATH", ""));
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, process).awaitError(),
                        new FileNotFoundException("javac"));
                });

                runner.test("with Java file that doesn't exist", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.createFolder("temp"))
                        .awaitError();
                    try
                    {
                        final File aJava = rootFolder.getFile("sources/A.java").awaitError();
                        final Iterable<File> sourceFiles = Iterable.create(aJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, new Process()),
                            (JavaCompilationResult result) ->
                            {
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
                                test.assertSuccess(false, QubBuild.getClassFile(aJava, rootFolder, outputFolder).exists());
                            });
                    }
                    finally
                    {
                        test.assertSuccess(rootFolder.delete());
                    }
                });

                runner.test("with empty Java file", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.createFolder("temp"))
                        .awaitError();
                    try
                    {
                        final File bJava = rootFolder.getFile("sources/B.java").awaitError();
                        bJava.create();
                        final Iterable<File> sourceFiles = Iterable.create(bJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, new Process()),
                            (JavaCompilationResult result) ->
                            {
                                test.assertNotNull(result);
                                test.assertEqual(0, result.exitCode);
                                test.assertEqual("", result.output);
                                test.assertEqual("", result.error);
                                test.assertEqual(Iterable.create(), result.issues);
                                test.assertSuccess(false, QubBuild.getClassFile(bJava, rootFolder, outputFolder).exists());
                            });
                    }
                    finally
                    {
                        test.assertSuccess(rootFolder.delete());
                    }
                });

                runner.test("with no errors", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.createFolder("temp"))
                        .awaitError();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").awaitError();
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
                        final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, new Process()),
                            (JavaCompilationResult result) ->
                            {
                                test.assertNotNull(result);
                                test.assertEqual(0, result.exitCode);
                                test.assertEqual("", result.output);
                                test.assertEqual("", result.error);
                                test.assertEqual(Iterable.create(), result.issues);
                                test.assertSuccess(true, QubBuild.getClassFile(cJava, rootFolder, outputFolder).exists());
                            });
                    }
                    finally
                    {
                        test.assertSuccess(rootFolder.delete());
                    }
                });

                runner.test("with \"error: class MyTestClass is public, should be declared in a file named MyTestClass.java\"", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.createFolder("temp"))
                        .awaitError();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").awaitError();
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
                        final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, new Process()),
                            (JavaCompilationResult result) ->
                            {
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
                                            "sources\\C.java",
                                            1, 8,
                                            "class MyTestClass is public, should be declared in a file named MyTestClass.java")),
                                    result.issues);
                                test.assertSuccess(false, QubBuild.getClassFile(cJava, rootFolder, outputFolder).exists());
                            });
                    }
                    finally
                    {
                        test.assertSuccess(rootFolder.delete());
                    }
                });

                runner.test("with \"error: class, interface, or enum expected\"", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.createFolder("temp"))
                        .awaitError();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").awaitError();
                        cJava.setContentsAsString("Im not a valid Java file");
                        final Iterable<File> sourceFiles = Iterable.create(cJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, new Process()),
                            (JavaCompilationResult result) ->
                            {
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
                                        "sources\\C.java",
                                        1, 1,
                                        "class, interface, or enum expected")),
                                    result.issues);
                                test.assertSuccess(false, QubBuild.getClassFile(cJava, rootFolder, outputFolder).exists());
                            });
                    }
                    finally
                    {
                        test.assertSuccess(rootFolder.delete());
                    }
                });

                runner.test("with \"error: ';' expected\" and \"error: reached end of file while parsing\"", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.createFolder("temp"))
                        .awaitError();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").awaitError();
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
                        final Folder outputFolder = rootFolder.getFolder("outputs").awaitError();
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, new Process()),
                            (JavaCompilationResult result) ->
                            {
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
                                            "sources\\C.java",
                                            6, 17,
                                            "';' expected"),
                                        JavaCompiler.error(
                                            "sources\\C.java",
                                            7, 4,
                                            "reached end of file while parsing")),
                                    result.issues);
                                test.assertSuccess(false, QubBuild.getClassFile(cJava, rootFolder, outputFolder).exists());
                            });
                    }
                    finally
                    {
                        test.assertSuccess(rootFolder.delete());
                    }
                });
            });
        });
    }
}
