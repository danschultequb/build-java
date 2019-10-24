package qub;

public class FakeJavacProcessRun implements FakeProcessRun
{
    private final FakeProcessRun fakeProcessRun;
    private final List<Path> sourceFilePaths;
    private final List<JavaCompilerIssue> issues;
    private Path outputFolderPath;
    private FileSystem fileSystem;

    public FakeJavacProcessRun()
    {
        this.fakeProcessRun = new BasicFakeProcessRun(JavacProcessBuilder.executablePath);
        this.sourceFilePaths = List.create();
        this.issues = List.create();
    }

    @Override
    public Path getExecutablePath()
    {
        return this.fakeProcessRun.getExecutablePath();
    }

    @Override
    public FakeJavacProcessRun addArgument(String argument)
    {
        this.fakeProcessRun.addArgument(argument);
        return this;
    }

    @Override
    public FakeJavacProcessRun addArguments(String... arguments)
    {
        this.fakeProcessRun.addArguments(arguments);
        return this;
    }

    @Override
    public FakeJavacProcessRun addArguments(Iterable<String> arguments)
    {
        this.fakeProcessRun.addArguments(arguments);
        return this;
    }

    @Override
    public Iterable<String> getArguments()
    {
        return this.fakeProcessRun.getArguments();
    }

    @Override
    public FakeJavacProcessRun setWorkingFolder(String workingFolderPath)
    {
        this.fakeProcessRun.setWorkingFolder(workingFolderPath);
        return this;
    }

    @Override
    public FakeJavacProcessRun setWorkingFolder(Path workingFolderPath)
    {
        this.fakeProcessRun.setWorkingFolder(workingFolderPath);
        return this;
    }

    @Override
    public FakeJavacProcessRun setWorkingFolder(Folder workingFolder)
    {
        this.fakeProcessRun.setWorkingFolder(workingFolder);
        return this;
    }

    @Override
    public Path getWorkingFolderPath()
    {
        return this.fakeProcessRun.getWorkingFolderPath();
    }

    @Override
    public FakeJavacProcessRun setFunction(int exitCode)
    {
        this.fakeProcessRun.setFunction(exitCode);
        return this;
    }

    @Override
    public FakeJavacProcessRun setFunction(Action0 action)
    {
        this.fakeProcessRun.setFunction(action);
        return this;
    }

    @Override
    public FakeJavacProcessRun setFunction(Function0<Integer> function)
    {
        this.fakeProcessRun.setFunction(function);
        return this;
    }

    @Override
    public FakeJavacProcessRun setFunction(Action1<ByteWriteStream> action)
    {
        this.fakeProcessRun.setFunction(action);
        return this;
    }

    @Override
    public FakeJavacProcessRun setFunction(Function1<ByteWriteStream,Integer> function)
    {
        this.fakeProcessRun.setFunction(function);
        return this;
    }

    @Override
    public FakeJavacProcessRun setFunction(Action2<ByteWriteStream,ByteWriteStream> action)
    {
        this.fakeProcessRun.setFunction(action);
        return this;
    }

    @Override
    public FakeJavacProcessRun setFunction(Function2<ByteWriteStream,ByteWriteStream,Integer> function)
    {
        this.fakeProcessRun.setFunction(function);
        return this;
    }

    @Override
    public FakeJavacProcessRun setFunction(Action3<ByteReadStream,ByteWriteStream,ByteWriteStream> action)
    {
        this.fakeProcessRun.setFunction(action);
        return this;
    }

    @Override
    public FakeJavacProcessRun setFunction(Function3<ByteReadStream,ByteWriteStream,ByteWriteStream,Integer> function)
    {
        this.fakeProcessRun.setFunction(function);
        return this;
    }

    @Override
    public Function3<ByteReadStream,ByteWriteStream,ByteWriteStream,Integer> getFunction()
    {
        return this.fakeProcessRun.getFunction();
    }

    /**
     * Automatically set the function based on the values that have been provided.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun setFunctionAutomatically()
    {
        PreCondition.assertNotNull(this.fileSystem, "this.fileSystem");

        return this.setFunction((ByteWriteStream javacOutput, ByteWriteStream javacError) ->
        {
            final Iterable<JavaCompilerIssue> errors = this.issues.where((JavaCompilerIssue issue) -> issue.type == Issue.Type.Error);
            final Set<Path> errorSourceFilePaths = errors.map((JavaCompilerIssue issue) -> Path.parse(issue.sourceFilePath)).toSet();

            final Path workingFolderPath = this.getWorkingFolderPath();

            final Path outputFolderPath = this.outputFolderPath.isRooted()
                ? this.outputFolderPath
                : workingFolderPath.resolve(this.outputFolderPath).await();
            final Folder outputsFolder = this.fileSystem.getFolder(outputFolderPath).await();
            for (final Path sourceFilePath : this.sourceFilePaths)
            {
                if (!errorSourceFilePaths.contains(sourceFilePath))
                {
                    final Path sourceFileRelativeToSourceFolderPath = Path.parse(Strings.join('/', sourceFilePath.getSegments().skipFirst()));
                    final Path classFileRelativePath = sourceFileRelativeToSourceFolderPath.changeFileExtension(".class");
                    final File classFile = outputsFolder.getFile(classFileRelativePath).await();
                    classFile.setContentsAsString(sourceFilePath.getSegments().last() + " bytecode").await();
                }
            }

            if (this.issues.any())
            {
                final CharacterWriteStream errorStream = javacError.asCharacterWriteStream();

                for (final JavaCompilerIssue issue : this.issues)
                {
                    errorStream.writeLine(issue.sourceFilePath + ":" + issue.lineNumber + ": " + issue.type.toString().toLowerCase() + ": " + issue.message).await();
                    errorStream.writeLine("Fake code line").await();
                    errorStream.writeLine(Strings.repeat(' ', issue.columnNumber - 1) + "^").await();
                }
            }

            return errors.getCount();
        });
    }

    /**
     * Set the folder that the javac process will write its output files to.
     * @param outputFolderPath The folder path that the javac process will write its output files
     *                         to.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addOutputFolder(String outputFolderPath)
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
    public FakeJavacProcessRun addOutputFolder(Path outputFolderPath)
    {
        PreCondition.assertNotNull(outputFolderPath, "outputFolder");

        final Path workingFolderPath = this.getWorkingFolderPath();
        if (workingFolderPath != null && outputFolderPath.isRooted())
        {
            outputFolderPath = outputFolderPath.relativeTo(workingFolderPath);
        }
        this.outputFolderPath = outputFolderPath;

        return this.addArguments("-d", outputFolderPath.toString());
    }

    /**
     * Set the folder that the javac process will write its output files to.
     * @param outputFolder The folder that the javac process will write its output files to.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addOutputFolder(Folder outputFolder)
    {
        PreCondition.assertNotNull(outputFolder, "outputFolder");

        this.fileSystem = outputFolder.getFileSystem();

        return this.addOutputFolder(outputFolder.getPath());
    }

    /**
     * Add the -Xlint:unchecked argument.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addXlintUnchecked()
    {
        return this.addArgument("-Xlint:unchecked");
    }

    /**
     * Add the -Xlint:deprecation argument.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addXlintDeprecation()
    {
        return this.addArgument("-Xlint:deprecation");
    }

    /**
     * Set the version of Java that will be used for the source parameter.
     * @param javaSourceVersion The version of Java that will be used for the source version.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addJavaSourceVersion(String javaSourceVersion)
    {
        PreCondition.assertNotNullAndNotEmpty(javaSourceVersion, "javaSourceVersion");

        return this.addArguments("-source", javaSourceVersion);
    }

    /**
     * Set the version of Java that will be used for the target parameter.
     * @param javaTargetVersion The version of Java that will be used for the target version.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addJavaTargetVersion(String javaTargetVersion)
    {
        PreCondition.assertNotNullAndNotEmpty(javaTargetVersion, "javaTargetVersion");

        return this.addArguments("-target", javaTargetVersion);
    }

    /**
     * Set the classpath to the runtime jar for the currently set java version.
     * @param bootClasspath The classpath to the runtime jar for the currently set java version.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addBootClasspath(String bootClasspath)
    {
        PreCondition.assertNotNullAndNotEmpty(bootClasspath, "bootClasspath");

        return this.addArguments("-bootclasspath", bootClasspath);
    }

    /**
     * Set the classpath to the runtime jar for the currently set java version.
     * @param bootClasspath The classpath to the runtime jar for the currently set java version.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addBootClasspath(Path bootClasspath)
    {
        PreCondition.assertNotNull(bootClasspath, "bootClasspath");

        return this.addBootClasspath(bootClasspath.toString());
    }

    /**
     * Set the classpath to the runtime jar for the currently set java version.
     * @param bootClasspath The classpath to the runtime jar for the currently set java version.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addBootClasspath(File bootClasspath)
    {
        PreCondition.assertNotNull(bootClasspath, "bootClasspath");

        this.fileSystem = bootClasspath.getFileSystem();

        return this.addBootClasspath(bootClasspath.toString());
    }

    /**
     * Set the classpath argument of the javac process.
     * @param classpath The classpath argument of the javac process.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addClasspath(String classpath)
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
    public FakeJavacProcessRun addClasspath(Iterable<String> classpath)
    {
        PreCondition.assertNotNullAndNotEmpty(classpath, "classpath");

        return this.addClasspath(Strings.join(';', classpath));
    }

    /**
     * Set the maximum number of errors that will be returned by the javac process.
     * @param maximumErrors The maximum number of errors that will be returned by the javac process.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addMaximumErrors(int maximumErrors)
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
    public FakeJavacProcessRun addMaximumWarnings(int maximumWarnings)
    {
        PreCondition.assertGreaterThan(maximumWarnings, 0, "maximumWarnings");

        return this.addArguments("-Xmaxwarns", Integers.toString(maximumWarnings));
    }

    /**
     * Add a source file to compile.
     * @param sourceFilePathString The path to the source file to compile.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addSourceFile(String sourceFilePathString)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePathString, "sourceFilePathString");

        return this.addSourceFile(Path.parse(sourceFilePathString));
    }

    /**
     * Add a source file to compile.
     * @param sourceFilePath The path to the source file to compile.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addSourceFile(Path sourceFilePath)
    {
        PreCondition.assertNotNull(sourceFilePath, "sourceFilePath");

        final Path workingFolderPath = this.getWorkingFolderPath();
        if (workingFolderPath != null && sourceFilePath.isRooted())
        {
            sourceFilePath = sourceFilePath.relativeTo(workingFolderPath);
        }
        this.sourceFilePaths.add(sourceFilePath);

        return this.addArgument(sourceFilePath.toString());
    }

    /**
     * Add a source file to compile.
     * @param sourceFile The source file to compile.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addSourceFile(File sourceFile)
    {
        PreCondition.assertNotNull(sourceFile, "sourceFile");

        return this.addSourceFile(sourceFile.getPath());
    }

    /**
     * Add source files to compile.
     * @param sourceFilePathStrings The source files to compile.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addSourceFilePathStrings(String... sourceFilePathStrings)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePathStrings, "sourceFilePathStrings");

        for (final String sourceFilePathString : sourceFilePathStrings)
        {
            this.addSourceFile(sourceFilePathString);
        }

        return this;
    }

    /**
     * Add source files to compile.
     * @param sourceFilePathStrings The source files to compile.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addSourceFilePathStrings(Iterable<String> sourceFilePathStrings)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePathStrings, "sourceFilePathStrings");

        for (final String sourceFilePathString : sourceFilePathStrings)
        {
            this.addSourceFile(sourceFilePathString);
        }

        return this;
    }

    /**
     * Add source files to compile.
     * @param sourceFilePaths The source files to compile.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addSourceFilePaths(Iterable<Path> sourceFilePaths)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFilePaths, "sourceFilePaths");

        for (final Path sourceFilePath : sourceFilePaths)
        {
            this.addSourceFile(sourceFilePath);
        }

        return this;
    }

    /**
     * Add source files to compile.
     * @param sourceFiles The source files to compile.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addSourceFiles(Iterable<File> sourceFiles)
    {
        PreCondition.assertNotNullAndNotEmpty(sourceFiles, "sourceFiles");

        for (final File sourceFile : sourceFiles)
        {
            this.addSourceFile(sourceFile);
        }

        return this;
    }

    /**
     * Add the following issues to the automatically generated function.
     * @param issues The issues to add to the automatically generated function.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addCompilerIssues(JavaCompilerIssue... issues)
    {
        PreCondition.assertNotNullAndNotEmpty(issues, "issues");

        this.issues.addAll(issues);

        return this;
    }
}
