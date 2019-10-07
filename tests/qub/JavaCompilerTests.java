package qub;

public interface JavaCompilerTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(JavaCompiler.class, () ->
        {
            runner.testGroup("checkJavaVersion()", () ->
            {
                runner.test("with null javaVersion", (Test test) ->
                {
                    final String javaVersion = null;
                    final EnvironmentVariables environmentVariables = new EnvironmentVariables();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.checkJavaVersion(javaVersion, environmentVariables, fileSystem).await();
                });

                runner.test("with empty javaVersion", (Test test) ->
                {
                    final String javaVersion = "";
                    final EnvironmentVariables environmentVariables = new EnvironmentVariables();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.checkJavaVersion(javaVersion, environmentVariables, fileSystem).await();
                });

                runner.test("with null environmentVariables", (Test test) ->
                {
                    final String javaVersion = "spam";
                    final EnvironmentVariables environmentVariables = null;
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    test.assertThrows(() -> compiler.checkJavaVersion(javaVersion, environmentVariables, fileSystem),
                        new PreConditionFailure("environmentVariables cannot be null."));
                });

                runner.test("with null fileSystem", (Test test) ->
                {
                    final String javaVersion = "spam";
                    final EnvironmentVariables environmentVariables = new EnvironmentVariables();
                    final InMemoryFileSystem fileSystem = null;
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    test.assertThrows(() -> compiler.checkJavaVersion(javaVersion, environmentVariables, fileSystem),
                        new PreConditionFailure("fileSystem cannot be null."));
                });

                runner.test("with no JAVA_HOME environment variable", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final EnvironmentVariables environmentVariables = new EnvironmentVariables();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    test.assertThrows(() -> compiler.checkJavaVersion(javaVersion, environmentVariables, fileSystem).await(),
                        new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."));
                });

                runner.test("with empty JAVA_HOME environment variable", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final EnvironmentVariables environmentVariables = new EnvironmentVariables()
                        .set("JAVA_HOME", "");
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    test.assertThrows(() -> compiler.checkJavaVersion(javaVersion, environmentVariables, fileSystem).await(),
                        new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."));
                });

                runner.test("with unrecognized java version", (Test test) ->
                {
                    final String javaVersion = "spam";
                    final Process process = new Process();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    fileSystem.createFolder("/my/Java/jdk-11.0.1").await();
                    final EnvironmentVariables environmentVariables = new EnvironmentVariables()
                        .set("JAVA_HOME", "/my/Java/jdk-11.0.1");
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    test.assertThrows(() -> compiler.checkJavaVersion(javaVersion, environmentVariables, fileSystem).await(),
                        new NotFoundException("No bootclasspath runtime jar file could be found for Java version \"spam\"."));
                });

                runner.test("with not installed java version", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    fileSystem.createFolder("/my/Java/jdk-11.0.1").await();
                    final EnvironmentVariables environmentVariables = new EnvironmentVariables()
                        .set("JAVA_HOME", "/my/Java/jdk-11.0.1");
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    test.assertThrows(() -> compiler.checkJavaVersion(javaVersion, environmentVariables, fileSystem).await(),
                        new NotFoundException("No installed JREs found for Java version \"1.8\"."));
                });

                runner.test("with multiple matching versions java version", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    fileSystem.createFolder("/my/Java/jdk-11.0.1").await();
                    fileSystem.createFolder("/my/Java/jre1.8.0_192").await();
                    fileSystem.createFolder("/my/Java/jre1.8.0_201").await();
                    final EnvironmentVariables environmentVariables = new EnvironmentVariables()
                        .set("JAVA_HOME", "/my/Java/jdk-11.0.1");
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    test.assertNull(compiler.checkJavaVersion(javaVersion, environmentVariables, fileSystem).await());
                    test.assertEqual("/my/Java/jre1.8.0_201/lib/rt.jar", compiler.getBootClasspath());
                });
            });

            runner.testGroup("getArguments()", () ->
            {
                runner.test("with null sourceFiles", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = null;
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    test.assertThrows(() -> compiler.getArguments(sourceFiles, rootFolder, outputFolder),
                        new PreConditionFailure("sourceFiles cannot be null."));
                });

                runner.test("with empty sourceFiles", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create();
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    test.assertThrows(() -> compiler.getArguments(sourceFiles, rootFolder, outputFolder),
                        new PreConditionFailure("sourceFiles cannot be empty."));
                });

                runner.test("with null rootFolder", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = null;
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    test.assertThrows(() -> compiler.getArguments(sourceFiles, rootFolder, outputFolder),
                        new PreConditionFailure("rootFolder cannot be null."));
                });

                runner.test("with null outputFolder", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = null;
                    test.assertThrows(() -> compiler.getArguments(sourceFiles, rootFolder, outputFolder),
                        new PreConditionFailure("outputFolder cannot be null."));
                });

                runner.test("with one source file", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-classpath", "/outputs",
                            "sources/A.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });

                runner.test("with two source files", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await(),
                        fileSystem.getFile("/sources/B.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-classpath", "/outputs",
                            "sources/A.java",
                            "sources/B.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });

                runner.test("with java version specified", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.setVersion("1.7");
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-source", "1.7",
                            "-target", "1.7",
                            "-classpath", "/outputs",
                            "sources/A.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });

                runner.test("with one dependency with no qubFolder specified", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.setVersion("1.7");
                    compiler.setDependencies(
                        Iterable.create(
                            new Dependency()
                                .setProject("a")
                                .setPublisher("b")
                                .setVersion("c")));
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    test.assertThrows(() -> compiler.getArguments(sourceFiles, rootFolder, outputFolder),
                        new NotFoundException("Cannot resolve project dependencies without a qubFolder."));
                });

                runner.test("with one dependency", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.setVersion("1.7");
                    compiler.setDependencies(
                        Iterable.create(
                            new Dependency()
                                .setProject("a")
                                .setPublisher("b")
                                .setVersion("c")));
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    compiler.setQubFolder(fileSystem.getFolder("/qub").await());
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-source", "1.7",
                            "-target", "1.7",
                            "-classpath", "/outputs;/qub/b/a/c/a.jar",
                            "sources/A.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });

                runner.test("with two dependencies", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.setVersion("1.7");
                    compiler.setDependencies(
                        Iterable.create(
                            new Dependency()
                                .setProject("a")
                                .setPublisher("b")
                                .setVersion("c"),
                            new Dependency()
                                .setProject("x")
                                .setPublisher("y")
                                .setVersion("z")));
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    compiler.setQubFolder(fileSystem.getFolder("/qub").await());
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-source", "1.7",
                            "-target", "1.7",
                            "-classpath", "/outputs;/qub/b/a/c/a.jar;/qub/y/x/z/x.jar",
                            "sources/A.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });

                runner.test("with negative maximumErrors", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.setVersion("1.7");
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    compiler.setMaximumErrors(-4);
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-source", "1.7",
                            "-target", "1.7",
                            "-classpath","/outputs",
                            "-Xmaxerrs", "-4",
                            "sources/A.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });

                runner.test("with zero maximumErrors", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.setVersion("1.7");
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    compiler.setMaximumErrors(0);
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-source", "1.7",
                            "-target", "1.7",
                            "-classpath","/outputs",
                            "-Xmaxerrs", "0",
                            "sources/A.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });

                runner.test("with positive maximumErrors", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.setVersion("1.7");
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    compiler.setMaximumErrors(10);
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-source", "1.7",
                            "-target", "1.7",
                            "-classpath","/outputs",
                            "-Xmaxerrs", "10",
                            "sources/A.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });

                runner.test("with negative maximumWarnings", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.setVersion("1.7");
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    compiler.setMaximumWarnings(-5);
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-source", "1.7",
                            "-target", "1.7",
                            "-classpath","/outputs",
                            "-Xmaxwarns", "-5",
                            "sources/A.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });

                runner.test("with zero maximumWarnings", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.setVersion("1.7");
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    compiler.setMaximumWarnings(0);
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-source", "1.7",
                            "-target", "1.7",
                            "-classpath","/outputs",
                            "-Xmaxwarns", "0",
                            "sources/A.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });

                runner.test("with positive maximumWarnings", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
                    compiler.setVersion("1.7");
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();
                    compiler.setMaximumWarnings(11);
                    test.assertEqual(
                        Iterable.create(
                            "-d", "/outputs",
                            "-Xlint:unchecked",
                            "-Xlint:deprecation",
                            "-source", "1.7",
                            "-target", "1.7",
                            "-classpath","/outputs",
                            "-Xmaxwarns", "11",
                            "sources/A.java"
                        ),
                        compiler.getArguments(sourceFiles, rootFolder, outputFolder));
                });
            });

            runner.testGroup("compile()", () ->
            {
                runner.test("with null sourceFiles", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
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

                runner.test("with empty sourceFiles", (Test test) ->
                {
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
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
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
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
                    final JavaCompiler compiler = new JavaCompiler(process.getProcessFactory());
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
                    final JavaCompiler compiler = new JavaCompiler(process.getProcessFactory());
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
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
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
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
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
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
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
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
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
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
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
                    final JavaCompiler compiler = new JavaCompiler(test.getProcess().getProcessFactory());
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
