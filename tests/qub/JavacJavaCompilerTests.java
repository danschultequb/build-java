package qub;

public class JavacJavaCompilerTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(JavacJavaCompiler.class, () ->
        {
            JavaCompilerTests.test(runner, JavacJavaCompiler::new);
        });
    }
}
