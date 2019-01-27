package qub;

/**
 * An abstract class used to interact with a Java compiler.
 */
public abstract class JavaCompiler
{
    private String bootClasspath;

    public String getBootClasspath()
    {
        return bootClasspath;
    }

    /**
     * Check that this system has the proper JRE installed to compile to the provided Java version.
     * @param javaVersion The Java version to check for.
     * @param console The console to use.
     * @return Whether or not the proper JRE is installed to compile to the provided Java version.
     */
    public Result<Void> checkJavaVersion(String javaVersion, Console console)
    {
        PreCondition.assertNotNull(console, "console");

        Result<Void> result;

        if (Strings.isNullOrEmpty(javaVersion))
        {
            result = Result.success();
        }
        else
        {
            final String javaHome = console.getEnvironmentVariable("JAVA_HOME");
            if (Strings.isNullOrEmpty(javaHome))
            {
                result = Result.error(new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified."));
            }
            else if (javaVersion.equals("1.8") || javaVersion.equals("8"))
            {
                final Folder javaFolder = console.getFileSystem().getFolder(javaHome).throwErrorOrGetValue().getParentFolder().throwErrorOrGetValue();
                final Iterable<Folder> jreAndJdkFolders = javaFolder.getFolders().throwErrorOrGetValue();
                final Iterable<Folder> jre18Folders = jreAndJdkFolders.where((Folder jreOrJdkFolder) -> jreOrJdkFolder.getName().startsWith("jre1.8.0_"));
                if (!jre18Folders.any())
                {
                    result = Result.error(new NotFoundException("No installed JREs found for Java version " + Strings.escapeAndQuote(javaVersion) + "."));
                }
                else
                {
                    final Folder jre18Folder = jre18Folders.maximum((Folder lhs, Folder rhs) -> Comparison.from(lhs.getName().compareTo(rhs.getName())));
                    result = jre18Folder.getFile("lib/rt.jar")
                        .then((File bootClasspathFile) ->
                        {
                            this.bootClasspath = bootClasspathFile.toString();
                            return null;
                        });
                }
            }
            else
            {
                result = Result.error(new NotFoundException("No bootclasspath runtime jar file could be found for Java version " + Strings.escapeAndQuote(javaVersion) + "."));
            }
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Compile all of the provided Java files using the provided Java version. All of the compiled
     * class files will be put into the outputFolder.
     * @param sourceFiles The source files to compile.
     * @param rootFolder The folder that contains all of the source files to compile.
     * @param outputFolder The output folder where the compiled results will be placed.
     * @param javaVersion The version of Java to use to compile the Java files.
     * @param console The Console or Process to use.
     * @return The result of the compilation.
     */
    public abstract Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder rootFolder, Folder outputFolder, String javaVersion, Console console);
}
