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
                        .throwErrorOrGetValue();
                    final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                    final Console console = new Console();
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, console), new PreConditionFailure("sourceFiles cannot be null."));
                });

                runner.test("with empty sourceFiles", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Iterable<File> sourceFiles = Iterable.empty();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .throwErrorOrGetValue();
                    final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                    final Console console = new Console();
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, console), new PreConditionFailure("sourceFiles cannot be empty."));
                });

                runner.test("with null rootFolder", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .throwErrorOrGetValue();
                    final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").throwErrorOrGetValue());
                    final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                    final Console console = new Console();
                    test.assertThrows(() -> compiler.compile(sourceFiles, null, outputFolder, console), new PreConditionFailure("rootFolder cannot be null."));
                });

                runner.test("with null console", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .throwErrorOrGetValue();
                    final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").throwErrorOrGetValue());
                    final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                    final Console console = null;
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, console), new PreConditionFailure("console cannot be null."));
                });

                runner.test("with no PATH environment variable", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .throwErrorOrGetValue();
                    final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").throwErrorOrGetValue());
                    final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                    final Console console = new Console();
                    console.setEnvironmentVariables(Map.create());
                    test.assertError(new FileNotFoundException("javac"), compiler.compile(sourceFiles, rootFolder, outputFolder, console));
                });

                runner.test("with empty PATH environment variable", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.getFolder("temp"))
                        .throwErrorOrGetValue();
                    final Iterable<File> sourceFiles = Iterable.create(rootFolder.getFile("sources/A.java").throwErrorOrGetValue());
                    final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                    final Console console = new Console();
                    console.setEnvironmentVariables(Map.<String,String>create().set("PATH", ""));
                    test.assertError(new FileNotFoundException("javac"), compiler.compile(sourceFiles, rootFolder, outputFolder, console));
                });

                runner.test("with Java file that doesn't exist", (Test test) ->
                {
                    final JavacJavaCompiler compiler = new JavacJavaCompiler();
                    final Folder rootFolder = test.getProcess()
                        .getCurrentFolder()
                        .thenResult((Folder currentFolder) -> currentFolder.createFolder("temp"))
                        .throwErrorOrGetValue();
                    try
                    {
                        final File aJava = rootFolder.getFile("sources/A.java").throwErrorOrGetValue();
                        final Iterable<File> sourceFiles = Iterable.create(aJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                        final Console console = new Console();
                        final InMemoryLineStream output = new InMemoryLineStream();
                        console.setOutput(output);
                        final InMemoryLineStream error = new InMemoryLineStream();
                        console.setError(error);
                        final Result<JavaCompilationResult> result = compiler.compile(sourceFiles, rootFolder, outputFolder, console);
                        test.assertSuccess(result,
                            (JavaCompilationResult compilationResult) ->
                            {
                                test.assertNotNull(compilationResult);
                                test.assertEqual(2, compilationResult.exitCode);
                                test.assertSuccess("", output.getText());
                                test.assertSuccess(
                                    Iterable.create(
                                        "",
                                        "error: file not found: sources\\A.java",
                                        "Usage: javac <options> <source files>",
                                        "use --help for a list of possible options"),
                                    error.getText().then((String text) -> Strings.getLines(text)));
                                test.assertSuccess(false, Build.getClassFile(aJava, rootFolder, outputFolder).exists());
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
                        .throwErrorOrGetValue();
                    try
                    {
                        final File bJava = rootFolder.getFile("sources/B.java").throwErrorOrGetValue();
                        bJava.create();
                        final Iterable<File> sourceFiles = Iterable.create(bJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                        final Console console = new Console();
                        final InMemoryLineStream output = new InMemoryLineStream();
                        console.setOutput(output);
                        final InMemoryLineStream error = new InMemoryLineStream();
                        console.setError(error);
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, console),
                            (JavaCompilationResult result) ->
                            {
                                test.assertNotNull(result);
                                test.assertEqual(0, result.exitCode);
                                test.assertSuccess("", output.getText());
                                test.assertSuccess("", error.getText());
                                test.assertEqual(Iterable.empty(), result.issues);
                                test.assertSuccess(false, Build.getClassFile(bJava, rootFolder, outputFolder).exists());
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
                        .throwErrorOrGetValue();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").throwErrorOrGetValue();
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
                        final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                        final Console console = new Console();
                        final InMemoryLineStream output = new InMemoryLineStream();
                        console.setOutput(output);
                        final InMemoryLineStream error = new InMemoryLineStream();
                        console.setError(error);
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, console),
                            (JavaCompilationResult result) ->
                            {
                                test.assertNotNull(result);
                                test.assertEqual(0, result.exitCode);
                                test.assertSuccess("", output.getText());
                                test.assertSuccess("", error.getText());
                                test.assertEqual(Iterable.empty(), result.issues);
                                test.assertSuccess(true, Build.getClassFile(cJava, rootFolder, outputFolder).exists());
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
                        .throwErrorOrGetValue();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").throwErrorOrGetValue();
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
                        final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                        final Console console = new Console();
                        final InMemoryLineStream output = new InMemoryLineStream();
                        console.setOutput(output);
                        final InMemoryLineStream error = new InMemoryLineStream();
                        console.setError(error);
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, console),
                            (JavaCompilationResult result) ->
                            {
                                test.assertNotNull(result);
                                test.assertEqual(1, result.exitCode);
                                test.assertSuccess("", output.getText());
                                test.assertSuccess(
                                    Iterable.create(
                                        "",
                                        "sources\\C.java:1: error: class MyTestClass is public, should be declared in a file named MyTestClass.java",
                                        "public class MyTestClass",
                                        "       ^",
                                        "1 error"),
                                    error.getText().then((String text) -> Strings.getLines(text)));
                                test.assertEqual(
                                    Iterable.create(
                                        JavaCompiler.error(
                                            "sources\\C.java",
                                            1, 8,
                                            "class MyTestClass is public, should be declared in a file named MyTestClass.java")),
                                    result.issues);
                                test.assertSuccess(false, Build.getClassFile(cJava, rootFolder, outputFolder).exists());
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
                        .throwErrorOrGetValue();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").throwErrorOrGetValue();
                        cJava.setContentsAsString("Im not a valid Java file");
                        final Iterable<File> sourceFiles = Iterable.create(cJava);
                        final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                        final Console console = new Console();
                        final InMemoryLineStream output = new InMemoryLineStream();
                        console.setOutput(output);
                        final InMemoryLineStream error = new InMemoryLineStream();
                        console.setError(error);
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, console),
                            (JavaCompilationResult result) ->
                            {
                                test.assertNotNull(result);
                                test.assertEqual(1, result.exitCode);
                                test.assertSuccess("", output.getText());
                                test.assertSuccess(
                                    Iterable.create(
                                        "",
                                        "sources\\C.java:1: error: class, interface, or enum expected",
                                        "Im not a valid Java file",
                                        "^",
                                        "1 error"),
                                    error.getText().then((String text) -> Strings.getLines(text)));
                                test.assertEqual(Iterable.create(
                                    JavaCompiler.error(
                                        "sources\\C.java",
                                        1, 1,
                                        "error: class, interface, or enum expected")),
                                    result.issues);
                                test.assertSuccess(false, Build.getClassFile(cJava, rootFolder, outputFolder).exists());
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
                        .throwErrorOrGetValue();
                    try
                    {
                        final File cJava = rootFolder.getFile("sources/C.java").throwErrorOrGetValue();
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
                        final Folder outputFolder = rootFolder.getFolder("outputs").throwErrorOrGetValue();
                        final Console console = new Console();
                        final InMemoryLineStream output = new InMemoryLineStream();
                        console.setOutput(output);
                        final InMemoryLineStream error = new InMemoryLineStream();
                        console.setError(error);
                        test.assertSuccess(compiler.compile(sourceFiles, rootFolder, outputFolder, console),
                            (JavaCompilationResult result) ->
                            {
                                test.assertNotNull(result);
                                test.assertEqual(1, result.exitCode);
                                test.assertSuccess("", output.getText());
                                test.assertSuccess(
                                    Iterable.create(
                                        "",
                                        "sources\\C.java:6: error: \';\' expected",
                                        "    return value",
                                        "                ^",
                                        "sources\\C.java:7: error: reached end of file while parsing",
                                        "  }",
                                        "   ^",
                                        "2 errors"),
                                    error.getText().then((String text) -> Strings.getLines(text)));
                                test.assertEqual(
                                    Iterable.create(
                                        JavaCompiler.error(
                                            "sources\\C.java",
                                            6, 15,
                                            "';' expected"),
                                        JavaCompiler.error(
                                            "sources\\C.java",
                                            7, 4,
                                            "reached end of file while parsing")),
                                    result.issues);
                                test.assertSuccess(false, Build.getClassFile(cJava, rootFolder, outputFolder).exists());
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
