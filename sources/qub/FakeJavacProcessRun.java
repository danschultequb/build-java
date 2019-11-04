package qub;

public class FakeJavacProcessRun extends FakeProcessRunDecorator<FakeJavacProcessRun> implements JavacArguments<FakeJavacProcessRun>
{
    private final List<Path> sourceFilePaths;
    private final List<JavaCompilerIssue> issues;
    private Path outputFolderPath;
    private FileSystem fileSystem;

    public FakeJavacProcessRun()
    {
        super(new BasicFakeProcessRun(JavacProcessBuilder.executablePath));
        this.sourceFilePaths = List.create();
        this.issues = List.create();
    }

    @Override
    public FakeJavacProcessRun setWorkingFolder(Folder workingFolder)
    {
        PreCondition.assertNotNull(workingFolder, "workingFolder");
        PreCondition.assertTrue(this.fileSystem == null || this.fileSystem == workingFolder.getFileSystem(), "this.fileSystem == null || this.fileSystem == workingFolder.getFileSystem()");

        this.fileSystem = workingFolder.getFileSystem();

        return super.setWorkingFolder(workingFolder);
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
    public FakeJavacProcessRun addOutputFolder(Path outputFolderPath)
    {
        PreCondition.assertNotNull(outputFolderPath, "outputFolder");

        final Path workingFolderPath = this.getWorkingFolderPath();
        if (workingFolderPath != null && outputFolderPath.isRooted())
        {
            outputFolderPath = outputFolderPath.relativeTo(workingFolderPath);
        }
        this.outputFolderPath = outputFolderPath;
        return JavacArguments.super.addOutputFolder(outputFolderPath);
    }

    /**
     * Set the folder that the javac process will write its output files to.
     * @param outputFolder The folder that the javac process will write its output files to.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addOutputFolder(Folder outputFolder)
    {
        PreCondition.assertNotNull(outputFolder, "outputFolder");
        PreCondition.assertTrue(this.fileSystem == null || this.fileSystem == outputFolder.getFileSystem(), "this.fileSystem == null || this.fileSystem == outputFolder.getFileSystem()");

        this.fileSystem = outputFolder.getFileSystem();

        return JavacArguments.super.addOutputFolder(outputFolder);
    }

    /**
     * Set the classpath to the runtime jar for the currently set java version.
     * @param bootClasspath The classpath to the runtime jar for the currently set java version.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addBootClasspath(File bootClasspath)
    {
        PreCondition.assertNotNull(bootClasspath, "bootClasspath");
        PreCondition.assertTrue(this.fileSystem == null || this.fileSystem == bootClasspath.getFileSystem(), "this.fileSystem == null || this.fileSystem == bootClasspath.getFileSystem()");

        this.fileSystem = bootClasspath.getFileSystem();

        return JavacArguments.super.addBootClasspath(bootClasspath);
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

        return JavacArguments.super.addSourceFile(sourceFilePath);
    }

    /**
     * Add a source file to compile.
     * @param sourceFile The source file to compile.
     * @return This object for method chaining.
     */
    public FakeJavacProcessRun addSourceFile(File sourceFile)
    {
        PreCondition.assertNotNull(sourceFile, "sourceFile");
        PreCondition.assertTrue(this.fileSystem == null || this.fileSystem == sourceFile.getFileSystem(), "this.fileSystem == null || this.fileSystem == sourceFile.getFileSystem()");

        this.fileSystem = sourceFile.getFileSystem();

        return JavacArguments.super.addSourceFile(sourceFile);
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
