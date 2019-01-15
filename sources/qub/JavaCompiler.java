package qub;

/**
 * An interface used to interact with a Java compiler.
 */
public interface JavaCompiler
{
    /**
     * Compile all of the Java files found in the provided sourceFolder using the provided Java
     * version. All of the compilation output files will be put into the outputFolder.
     * @param sourceFolder The source folder to look for Java files in.
     * @param javaVersion The version of Java to use to compile the Java files.
     * @param outputFolder The output folder where the compiled results will be placed.
     * @param console The Console or Process to use.
     * @return The result of the compilation.
     */
    Result<JavaCompilationResult> compile(Folder sourceFolder, String javaVersion, Folder outputFolder, Console console);
}
