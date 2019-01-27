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
                    final Console console = new Console();
                    final JavaCompiler compiler = creator.run();
                    test.assertSuccess(compiler.checkJavaVersion(javaVersion, console));
                });

                runner.test("with empty javaVersion", (Test test) ->
                {
                    final String javaVersion = null;
                    final Console console = new Console();
                    final JavaCompiler compiler = creator.run();
                    test.assertSuccess(compiler.checkJavaVersion(javaVersion, console));
                });

                runner.test("with null console", (Test test) ->
                {
                    final String javaVersion = "spam";
                    final Console console = null;
                    final JavaCompiler compiler = creator.run();
                    test.assertThrows(() -> compiler.checkJavaVersion(javaVersion, console), new PreConditionFailure("console cannot be null."));
                });

                runner.test("with no JAVA_HOME environment variable", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final Console console = new Console();
                    final JavaCompiler compiler = creator.run();
                    console.setEnvironmentVariables(Map.create());
                    test.assertError(
                        new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."),
                        compiler.checkJavaVersion(javaVersion, console));
                });

                runner.test("with null JAVA_HOME environment variable", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final Console console = new Console();
                    console.setEnvironmentVariables(Map.<String,String>create().set("JAVA_HOME", null));
                    final JavaCompiler compiler = creator.run();
                    test.assertError(
                        new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."),
                        compiler.checkJavaVersion(javaVersion, console));
                });

                runner.test("with empty JAVA_HOME environment variable", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final Console console = new Console();
                    console.setEnvironmentVariables(Map.<String,String>create().set("JAVA_HOME", ""));
                    final JavaCompiler compiler = creator.run();
                    test.assertError(
                        new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."),
                        compiler.checkJavaVersion(javaVersion, console));
                });

                runner.test("with unrecognized java version", (Test test) ->
                {
                    final String javaVersion = "spam";
                    final Console console = new Console();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    fileSystem.createFolder("/my/Java/jdk-11.0.1");
                    console.setFileSystem(fileSystem);
                    console.setEnvironmentVariables(Map.<String,String>create().set("JAVA_HOME", "/my/Java/jdk-11.0.1"));
                    final JavaCompiler compiler = creator.run();
                    test.assertError(
                        new NotFoundException("No bootclasspath runtime jar file could be found for Java version \"spam\"."),
                        compiler.checkJavaVersion(javaVersion, console));
                });

                runner.test("with not installed java version", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final Console console = new Console();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    fileSystem.createFolder("/my/Java/jdk-11.0.1");
                    console.setFileSystem(fileSystem);
                    console.setEnvironmentVariables(Map.<String,String>create().set("JAVA_HOME", "/my/Java/jdk-11.0.1"));
                    final JavaCompiler compiler = creator.run();
                    test.assertError(
                        new NotFoundException("No installed JREs found for Java version \"1.8\"."),
                        compiler.checkJavaVersion(javaVersion, console));
                });

                runner.test("with multiple matching versions java version", (Test test) ->
                {
                    final String javaVersion = "1.8";
                    final Console console = new Console();
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getMainAsyncRunner());
                    fileSystem.createRoot("/");
                    fileSystem.createFolder("/my/Java/jdk-11.0.1");
                    fileSystem.createFolder("/my/Java/jre1.8.0_192");
                    fileSystem.createFolder("/my/Java/jre1.8.0_201");
                    console.setFileSystem(fileSystem);
                    console.setEnvironmentVariables(Map.<String,String>create().set("JAVA_HOME", "/my/Java/jdk-11.0.1"));
                    final JavaCompiler compiler = creator.run();
                    test.assertSuccess(null, compiler.checkJavaVersion(javaVersion, console));
                    test.assertEqual("/my/Java/jre1.8.0_201/lib/rt.jar", compiler.getBootClasspath());
                });
            });
        });
    }
}
