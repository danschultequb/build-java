package qub;

public interface JavacArguments<T>
{
    /**
     * Get the path to the folder that this ProcessBuilder will run the executable in.
     * @return The path to the folder that this ProcessBuilder will run the executable in.
     */
    Path getWorkingFolderPath();

    /**
     * Add the provided arguments to the list of arguments that will be provided to the executable
     * when this ProcessBuilder is run.
     * @param arguments The arguments to add.
     * @return This object for method chaining.
     */
    T addArguments(String... arguments);

    /**
     * Set the folder that the javac process will write its output files to.
     * @param outputFolderPath The folder path that the javac process will write its output files
     *                         to.
     * @return This object for method chaining.
     */
    default T addOutputFolder(String outputFolderPath)
    {
        PreCondition.assertNotNullAndNotEmpty(outputFolderPath, "outputFolder");

        return this.addOutputFolder(Path.parse(outputFolderPath));
    }

    /**
     * Set the folder that the javac process will write its output files to.
     * @param outputFolderPath The folder path that the javac process will write its output files
     *                         to.
     * @return This object for method chaining.
     */
    default T addOutputFolder(Path outputFolderPath)
    {
        PreCondition.assertNotNull(outputFolderPath, "outputFolder");

        final Path workingFolderPath = this.getWorkingFolderPath();
        if (workingFolderPath != null && outputFolderPath.isRooted())
        {
            outputFolderPath = outputFolderPath.relativeTo(workingFolderPath);
        }
        return this.addArguments("-d", outputFolderPath.toString());
    }

    /**
     * Set the folder that the javac process will write its output files to.
     * @param outputFolder The folder that the javac process will write its output files to.
     * @return This object for method chaining.
     */
    default T addOutputFolder(Folder outputFolder)
    {
        PreCondition.assertNotNull(outputFolder, "outputFolder");

        final Path outputFolderPath = outputFolder.getPath().relativeTo(this.getWorkingFolderPath());
        return this.addOutputFolder(outputFolderPath);
    }

    /**
     * Add the -Xlint:unchecked argument.
     * @return This object for method chaining.
     */
    default T addXlintUnchecked()
    {
        return this.addArguments("-Xlint:unchecked");
    }

    /**
     * Add the -Xlint:deprecation argument.
     * @return This object for method chaining.
     */
    default T addXlintDeprecation()
    {
        return this.addArguments("-Xlint:deprecation");
    }

    /**
     * Set the version of Java that will be used for the source parameter.
     * @param javaSourceVersion The version of Java that will be used for the source version.
     * @return This object for method chaining.
     */
    default T addJavaSourceVersion(String javaSourceVersion)
    {
        PreCondition.assertNotNullAndNotEmpty(javaSourceVersion, "javaSourceVersion");

        return this.addArguments("-source", javaSourceVersion);
    }

    /**
     * Set the version of Java that will be used for the target parameter.
     * @param javaTargetVersion The version of Java that will be used for the target version.
     * @return This object for method chaining.
     */
    default T addJavaTargetVersion(String javaTargetVersion)
    {
        PreCondition.assertNotNullAndNotEmpty(javaTargetVersion, "javaTargetVersion");

        return this.addArguments("-target", javaTargetVersion);
    }

    /**
     * Set the classpath to the runtime jar for the currently set java version.
     * @param bootClasspath The classpath to the runtime jar for the currently set java version.
     * @return This object for method chaining.
     */
    default T addBootClasspath(String bootClasspath)
    {
        PreCondition.assertNotNullAndNotEmpty(bootClasspath, "bootClasspath");

        return this.addBootClasspath(Path.parse(bootClasspath));
    }

    /**
     * Set the classpath to the runtime jar for the currently set java version.
     * @param bootClasspath The classpath to the runtime jar for the currently set java version.
     * @return This object for method chaining.
     */
    default T addBootClasspath(Path bootClasspath)
    {
        PreCondition.assertNotNull(bootClasspath, "bootClasspath");

         return this.addArguments("-bootclasspath", bootClasspath.toString());
    }

    /**
     * Set the classpath to the runtime jar for the currently set java version.
     * @param bootClasspath The classpath to the runtime jar for the currently set java version.
     * @return This object for method chaining.
     */
    default T addBootClasspath(File bootClasspath)
    {
        PreCondition.assertNotNull(bootClasspath, "bootClasspath");

        return this.addBootClasspath(bootClasspath.toString());
    }

    /**
     * Set the classpath argument of the javac process.
     * @param classpath The classpath argument of the javac process.
     * @return This object for method chaining.
     */
    default T addClasspath(String classpath)
    {
        PreCondition.assertNotNullAndNotEmpty(classpath, "classpath");

        return this.addArguments("-classpath", classpath);
    }

    /**
     * Set the classpath argument of the javac process. The values of the provided Iterable will be
     * separated by semi-colons (;).
     * @param classpath The classpath argument of the javac process.
     * @return This object for method chaining.
     */
    default T addClasspath(Iterable<String> classpath)
    {
        PreCondition.assertNotNullAndNotEmpty(classpath, "classpath");

        return this.addClasspath(Strings.join(';', classpath));
    }

    /**
     * Set the maximum number of errors that will be returned by the javac process.
     * @param maximumErrors The maximum number of errors that will be returned by the javac process.
     * @return This object for method chaining.
     */
    default T addMaximumErrors(int maximumErrors)
    {
        PreCondition.assertGreaterThan(maximumErrors, 0, "maximumErrors");

        return this.addArguments("-Xmaxerrs", Integers.toString(maximumErrors));
    }

    /**
     * Set the maximum number of warnings that will be returned by the javac process.
     * @param maximumWarnings The maximum number of warnings that will be returned by the javac
     *                        process.
     * @return This object for method chaining.
     */
    default T addMaximumWarnings(int maximumWarnings)
    {
        PreCondition.assertGreaterThan(maximumWarnings, 0, "maximumWarnings");

        return this.addArguments("-Xmaxwarns", Integers.toString(maximumWarnings));
    }

    /**
     * Add a source file to compile.
     * @param sourceFilePathString The path to the source file to compile.
     * @return This object for method chaining.
     */
    default T addSourceFile(String sourceFilePathString)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePathString, "sourceFilePathString");

        return this.addSourceFile(Path.parse(sourceFilePathString));
    }

    /**
     * Add a source file to compile.
     * @param sourceFilePath The path to the source file to compile.
     * @return This object for method chaining.
     */
    default T addSourceFile(Path sourceFilePath)
    {
        PreCondition.assertNotNull(sourceFilePath, "sourceFilePath");

        if (sourceFilePath.isRooted())
        {
            sourceFilePath = sourceFilePath.relativeTo(this.getWorkingFolderPath());
        }

        return this.addArguments(sourceFilePath.toString());
    }

    /**
     * Add a source file to compile.
     * @param sourceFile The source file to compile.
     * @return This object for method chaining.
     */
    default T addSourceFile(File sourceFile)
    {
        PreCondition.assertNotNull(sourceFile, "sourceFile");

        return this.addSourceFile(sourceFile.getPath());
    }

    /**
     * Add source files to compile.
     * @param sourceFilePathStrings The source files to compile.
     * @return This object for method chaining.
     */
    default T addSourceFilePathStrings(String... sourceFilePathStrings)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePathStrings, "sourceFilePathStrings");

        T result = null;
        for (final String sourceFilePathString : sourceFilePathStrings)
        {
            result = this.addSourceFile(sourceFilePathString);
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Add source files to compile.
     * @param sourceFilePathStrings The source files to compile.
     * @return This object for method chaining.
     */
    default T addSourceFilePathStrings(Iterable<String> sourceFilePathStrings)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePathStrings, "sourceFilePathStrings");

        T result = null;
        for (final String sourceFilePathString : sourceFilePathStrings)
        {
            result = this.addSourceFile(sourceFilePathString);
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Add source files to compile.
     * @param sourceFilePaths The source files to compile.
     * @return This object for method chaining.
     */
    default T addSourceFilePaths(Iterable<Path> sourceFilePaths)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePaths, "sourceFilePaths");

        T result = null;
        for (final Path sourceFilePath : sourceFilePaths)
        {
            result = this.addSourceFile(sourceFilePath);
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Add source files to compile.
     * @param sourceFiles The source files to compile.
     * @return This object for method chaining.
     */
    default T addSourceFiles(Iterable<File> sourceFiles)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");

        T result = null;
        for (final File sourceFile : sourceFiles)
        {
            result = this.addSourceFile(sourceFile);
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
