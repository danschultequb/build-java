package qub;

/**
 * A JavaCompiler that uses the actual javac executable.
 */
public class JavacJavaCompiler implements JavaCompiler
{
    @Override
    public Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder sourceFolder, Folder outputFolder, String javaVersion, Console console)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");
        PreCondition.assertNotNull(sourceFolder, "sourceFolder");
        PreCondition.assertNotNull(outputFolder, "outputFolder");
        PreCondition.assertNotNull(console, "console");

        return console.getProcessBuilder("javac")
            .then((ProcessBuilder javac) ->
            {
                final Value<Boolean> wroteNewLineBeforeOutputOrError = new Value<>();

                javac.redirectOutput(new NewLineBeforeFirstWriteByteWriteStream(console.getOutputAsByteWriteStream(), wroteNewLineBeforeOutputOrError));
                javac.redirectError(new NewLineBeforeFirstWriteByteWriteStream(console.getErrorAsByteWriteStream(), wroteNewLineBeforeOutputOrError));

                javac.addArguments("-d", outputFolder.getPath().toString());
                javac.addArgument("-Xlint:unchecked");
                javac.addArgument("-Xlint:deprecation");

                if (!Strings.isNullOrEmpty(javaVersion))
                {
                    javac.addArguments("-source", javaVersion);
                    javac.addArguments("-target", javaVersion);

                    if (javaVersion.equals("1.8") || javaVersion.equals("8"))
                    {
                        final Folder javaFolder = console.getFileSystem().getFolder("C:/Program Files/Java/").throwErrorOrGetValue();
                        final Iterable<Folder> jreAndJdkFolders = javaFolder.getFolders().throwErrorOrGetValue();
                        final Iterable<Folder> jre18Folders = jreAndJdkFolders.where((Folder jreOrJdkFolder) -> jreOrJdkFolder.getName().startsWith("jre1.8.0_"));
                        final Folder jre18Folder = jre18Folders.maximum((Folder lhs, Folder rhs) -> Comparison.from(lhs.getName().compareTo(rhs.getName())));
                        javac.addArguments("-bootclasspath", jre18Folder.getPath().concatenateSegment("lib/rt.jar").toString());
                    }
                }

                javac.addArguments(sourceFiles.map(FileSystemEntry::toString));

                final JavaCompilationResult result = new JavaCompilationResult();
                result.setExitCode(javac.run());

                return result;
            });
    }
}
