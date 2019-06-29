package qub;

public class FakeJavaCompilerTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(FakeJavaCompiler.class, () ->
        {
            JavaCompilerTests.test(runner, FakeJavaCompiler::new);

            runner.test("constructor()", (Test test) ->
            {
                final FakeJavaCompiler compiler = new FakeJavaCompiler();
                test.assertEqual(0, compiler.getExitCode());
                test.assertEqual(null, compiler.getIssues());
                test.assertNull(compiler.getVerbose());
            });

            runner.testGroup("compile()", () ->
            {
                runner.test("with null sourceFiles", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = null;
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();

                    final FakeJavaCompiler compiler = new FakeJavaCompiler();
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, new Console()), new PreConditionFailure("sourceFiles cannot be null."));
                });

                runner.test("with empty sourceFiles", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create();
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();

                    final FakeJavaCompiler compiler = new FakeJavaCompiler();
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, new Console()), new PreConditionFailure("sourceFiles cannot be empty."));
                });

                runner.test("with null outputFolder", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.createFile("/sources/a/B.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = null;

                    final FakeJavaCompiler compiler = new FakeJavaCompiler();
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, new Console()), new PreConditionFailure("outputFolder cannot be null."));
                });

                runner.test("with null console", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Iterable<File> sourceFiles = Iterable.create(
                        fileSystem.createFile("/sources/a/B.java").await());
                    final Folder rootFolder = fileSystem.getFolder("/").await();
                    final Folder outputFolder = fileSystem.getFolder("/outputs").await();

                    final FakeJavaCompiler compiler = new FakeJavaCompiler();
                    test.assertThrows(() -> compiler.compile(sourceFiles, rootFolder, outputFolder, null), new PreConditionFailure("process cannot be null."));
                });
            });
        });
    }
}
