package qub;

/**
 * An interface used to interact with a Java compiler.
 */
public interface JavaCompiler
{
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
    Result<JavaCompilationResult> compile(Iterable<File> sourceFiles, Folder rootFolder, Folder outputFolder, String javaVersion, Console console);
}
