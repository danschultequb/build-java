package qub;

public class FakeJarCreator extends JarCreator
{
    @Override
    public Result<File> createJarFile(Console console, boolean isVerbose)
    {
        return Result.create(() ->
        {
            final Folder outputsFolder = getOutputsFolder();
            final String jarName = getJarName();
            final File jarFile = outputsFolder.getFile(jarName + ".jar").await();

            try (final LineWriteStream writeStream = jarFile.getContentCharacterWriteStream().await().asLineWriteStream())
            {
                final File manifestFile = getManifestFile();
                if (manifestFile != null)
                {
                    writeStream.writeLine("Manifest file:").await();
                    writeStream.writeLine(manifestFile.relativeTo(outputsFolder).toString()).await();
                    writeStream.writeLine().await();
                }

                final Iterable<File> classFiles = getClassFiles();
                if (!Iterable.isNullOrEmpty(classFiles))
                {
                    writeStream.writeLine("Class files:").await();
                    for (final File classFile : classFiles)
                    {
                        writeStream.writeLine(classFile.relativeTo(outputsFolder).toString()).await();
                    }
                    writeStream.writeLine().await();
                }

                final Iterable<File> sourceFiles = getSourceFiles();
                if (!Iterable.isNullOrEmpty(sourceFiles))
                {
                    writeStream.writeLine("Source files:").await();
                    for (final File sourceFile : sourceFiles)
                    {
                        writeStream.writeLine(sourceFile.relativeTo(outputsFolder).toString()).await();
                    }
                    writeStream.writeLine().await();
                }
            }

            return jarFile;
        });

    }
}
