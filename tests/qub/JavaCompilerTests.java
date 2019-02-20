package qub;

public class JavaCompilerTests
{
    public static void test(TestRunner runner, Function0<JavaCompiler> creator)
    {
        runner.testGroup(JavaCompiler.class, () ->
        {
            runner.testGroup("checkJavaVersion()", () ->
            {
                runner.test("with null javaVersion", (Test test) ->
                {
                    final String javaVersion = null;
                    final Process process = new Process();
                    final JavaCompiler compiler = creator.run();
                    test.assertSuccess(compiler.checkJavaVersion(javaVersion, process));
                });

                runner.test("with empty javaVersion", (Test test) ->
                {
                    final String javaVersion = null;
                    final Process process = new Process();
                    final JavaCompiler compiler = creator.run();
                    test.assertSuccess(compiler.checkJavaVersion(javaVersion, process));
                });

                runner.test("with null process", (Test test) ->
                {
                    final String javaVersion = "spam";
                    final Process process = null;
                    final JavaCompiler compiler = creator.run();
                    test.assertThrows(() -> compiler.checkJavaVersion(javaVersion, process), new PreConditionFailure("process cannot be null."));
                });

                runner.test("with no JAVA_HOME environment variable", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final Process process = new Process();
                    final JavaCompiler compiler = creator.run();
                    process.setEnvironmentVariables(Map.create());
                    test.assertError(
                        new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."),
                        compiler.checkJavaVersion(javaVersion, process));
                });

                runner.test("with null JAVA_HOME environment variable", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final Process process = new Process();
                    process.setEnvironmentVariables(Map.<String,String>create().set("JAVA_HOME", null));
                    final JavaCompiler compiler = creator.run();
                    test.assertError(
                        new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."),
                        compiler.checkJavaVersion(javaVersion, process));
                });

                runner.test("with empty JAVA_HOME environment variable", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final Process process = new Process();
                    process.setEnvironmentVariables(Map.<String,String>create().set("JAVA_HOME", ""));
                    final JavaCompiler compiler = creator.run();
                    test.assertError(
                        new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."),
                        compiler.checkJavaVersion(javaVersion, process));
                });

                runner.test("with unrecognized java version", (Test test) ->
                {
                    final String javaVersion = "spam";
                    final Process process = new Process();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    fileSystem.createFolder("/my/Java/jdk-11.0.1");
                    process.setFileSystem(fileSystem);
                    process.setEnvironmentVariables(Map.<String,String>create().set("JAVA_HOME", "/my/Java/jdk-11.0.1"));
                    final JavaCompiler compiler = creator.run();
                    test.assertError(
                        new NotFoundException("No bootclasspath runtime jar file could be found for Java version \"spam\"."),
                        compiler.checkJavaVersion(javaVersion, process));
                });

                runner.test("with not installed java version", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final Process process = new Process();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    fileSystem.createFolder("/my/Java/jdk-11.0.1");
                    process.setFileSystem(fileSystem);
                    process.setEnvironmentVariables(Map.<String,String>create().set("JAVA_HOME", "/my/Java/jdk-11.0.1"));
                    final JavaCompiler compiler = creator.run();
                    test.assertError(
                        new NotFoundException("No installed JREs found for Java version \"1.8\"."),
                        compiler.checkJavaVersion(javaVersion, process));
                });

                runner.test("with multiple matching versions java version", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final Process process = new Process();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    fileSystem.createFolder("/my/Java/jdk-11.0.1");
                    fileSystem.createFolder("/my/Java/jre1.8.0_192");
                    fileSystem.createFolder("/my/Java/jre1.8.0_201");
                    process.setFileSystem(fileSystem);
                    process.setEnvironmentVariables(Map.<String,String>create().set("JAVA_HOME", "/my/Java/jdk-11.0.1"));
                    final JavaCompiler compiler = creator.run();
                    test.assertSuccess(null, compiler.checkJavaVersion(javaVersion, process));
                    test.assertEqual("/my/Java/jre1.8.0_201/lib/rt.jar", compiler.getBootClasspath());
                });
            });

            runner.testGroup("getArguments()", () ->
            {
                runner.test("with null sourceFiles", (Test test) ->
                {
                    final JavaCompiler compiler = creator.run();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Iterable<File> sourceFiles = null;
                    final Folder rootFolder = fileSystem.getFolder("/").throwErrorOrGetValue();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").throwErrorOrGetValue();
                    test.assertThrows(() -> compiler.getArguments(sourceFiles, rootFolder, outputFolder),
                        new PreConditionFailure("sourceFiles cannot be null."));
                });

                runner.test("with empty sourceFiles", (Test test) ->
                {
                    final JavaCompiler compiler = creator.run();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Iterable<File> sourceFiles = Iterable.create();
                    final Folder rootFolder = fileSystem.getFolder("/").throwErrorOrGetValue();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").throwErrorOrGetValue();
                    test.assertThrows(() -> compiler.getArguments(sourceFiles, rootFolder, outputFolder),
                        new PreConditionFailure("sourceFiles cannot be empty."));
                });

                runner.test("with null rootFolder", (Test test) ->
                {
                    final JavaCompiler compiler = creator.run();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").throwErrorOrGetValue());
                    final Folder rootFolder = null;
                    final Folder outputFolder = fileSystem.getFolder("/outputs").throwErrorOrGetValue();
                    test.assertThrows(() -> compiler.getArguments(sourceFiles, rootFolder, outputFolder),
                        new PreConditionFailure("rootFolder cannot be null."));
                });

                runner.test("with null outputFolder", (Test test) ->
                {
                    final JavaCompiler compiler = creator.run();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").throwErrorOrGetValue());
                    final Folder rootFolder = fileSystem.getFolder("/").throwErrorOrGetValue();
                    final Folder outputFolder = null;
                    test.assertThrows(() -> compiler.getArguments(sourceFiles, rootFolder, outputFolder),
                        new PreConditionFailure("outputFolder cannot be null."));
                });

                runner.test("with one source file", (Test test) ->
                {
                    final JavaCompiler compiler = creator.run();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").throwErrorOrGetValue());
                    final Folder rootFolder = fileSystem.getFolder("/").throwErrorOrGetValue();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").throwErrorOrGetValue();
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
                    final JavaCompiler compiler = creator.run();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").throwErrorOrGetValue(),
                        fileSystem.getFile("/sources/B.java").throwErrorOrGetValue());
                    final Folder rootFolder = fileSystem.getFolder("/").throwErrorOrGetValue();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").throwErrorOrGetValue();
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
                    final JavaCompiler compiler = creator.run();
                    compiler.setVersion("1.7");
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").throwErrorOrGetValue());
                    final Folder rootFolder = fileSystem.getFolder("/").throwErrorOrGetValue();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").throwErrorOrGetValue();
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
                    final JavaCompiler compiler = creator.run();
                    compiler.setVersion("1.7");
                    compiler.setDependencies(
                        Iterable.create(
                            new Dependency()
                                .setProject("a")
                                .setPublisher("b")
                                .setVersion("c")));
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").throwErrorOrGetValue());
                    final Folder rootFolder = fileSystem.getFolder("/").throwErrorOrGetValue();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").throwErrorOrGetValue();
                    test.assertThrows(() -> compiler.getArguments(sourceFiles, rootFolder, outputFolder),
                        new NotFoundException("Cannot resolve project dependencies without a qubFolder."));
                });

                runner.test("with one dependency", (Test test) ->
                {
                    final JavaCompiler compiler = creator.run();
                    compiler.setVersion("1.7");
                    compiler.setDependencies(
                        Iterable.create(
                            new Dependency()
                                .setProject("a")
                                .setPublisher("b")
                                .setVersion("c")));
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").throwErrorOrGetValue());
                    final Folder rootFolder = fileSystem.getFolder("/").throwErrorOrGetValue();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").throwErrorOrGetValue();
                    compiler.setQubFolder(fileSystem.getFolder("/qub").throwErrorOrGetValue());
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
                    final JavaCompiler compiler = creator.run();
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
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.getFile("/sources/A.java").throwErrorOrGetValue());
                    final Folder rootFolder = fileSystem.getFolder("/").throwErrorOrGetValue();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").throwErrorOrGetValue();
                    compiler.setQubFolder(fileSystem.getFolder("/qub").throwErrorOrGetValue());
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
            });
        });
    }
}
