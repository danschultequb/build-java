package qub;

public class JavaJarCreator extends JarCreator
{
    @Override
    public Result<File> createJarFile(Console console, boolean isVerbose)
    {
        return Result.create(() ->
        {
            final ProcessBuilder jar = console.getProcessBuilder("jar").await();
            jar.redirectOutput(console.getOutputAsByteWriteStream());
            jar.redirectError(console.getErrorAsByteWriteStream());

            final Folder outputsFolder = getOutputsFolder();
            jar.setWorkingFolder(outputsFolder);

            String jarArguments = "cf";
            final File manifestFile = getManifestFile();
            if (manifestFile != null)
            {
                jarArguments += 'm';
            }
            jar.addArgument(jarArguments);

            final File jarFile = outputsFolder.getFile(getJarName() + ".jar").await();
            jar.addArgument(jarFile.getPath().toString());

            if (manifestFile != null)
            {
                jar.addArgument(manifestFile.relativeTo(outputsFolder).toString());
            }

            jar.addArguments(outputsFolder.getFilesRecursively().await()
                .where((File outputFile) -> Comparer.equal(outputFile.getFileExtension(), ".class"))
                .map((File outputClassFile) -> outputClassFile.relativeTo(outputsFolder).toString()));

            jar.addArguments(getSourceFiles()
                .map((File javaSourceFile) -> javaSourceFile.relativeTo(outputsFolder).toString()));

            if (isVerbose)
            {
                console.writeLine(jar.getCommand());
            }

            jar.run().await();

            return jarFile;
        });
    }
}
