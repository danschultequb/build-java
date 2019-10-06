package qub;

public interface QubBuild
{
    static void main(String[] args)
    {
        Console.run(args, QubBuild::main);
    }

    static void main(Console console)
    {
        PreCondition.assertNotNull(console, "console");

        final QubBuildParameters parameters = QubBuild.getParameters(console);
        if (parameters != null)
        {
            console.showDuration(() ->
            {
                console.setExitCode(QubBuild.run(parameters));
            });
        }
    }

    /**
     * Get QubBuildParameters from the arguments provided to the process. This will return null if
     * the help message is shown.
     * @param process The process to get QubBuildParameters from.
     * @return The QubBuildParameters.
     */
    static QubBuildParameters getParameters(Process process)
    {
        PreCondition.assertNotNull(process, "process");

        final CommandLineParameters parameters = process.createCommandLineParameters()
            .setApplicationName("qub-build")
            .setApplicationDescription("Used to compile and package source code projects.");
        final CommandLineParameter<Folder> folderToBuildParameter = parameters.addPositionalFolder("folder", process)
            .setValueName("<folder-path-to-build>")
            .setDescription("The folder to build. The current folder will be used if this isn't defined.");
        final CommandLineParameter<Warnings> warningsParameter = parameters.addEnum("warnings", Warnings.Show)
            .setValueName("<show|error|hide>")
            .setDescription("How to handle build warnings. Can be either \"show\", \"error\", or \"hide\". Defaults to \"show\".");
        final CommandLineParameterBoolean buildJsonParameter = parameters.addBoolean("buildjson", true)
            .setDescription("Whether or not to read and write a build.json file. Defaults to true.");
        final CommandLineParameterVerbose verboseParameter = parameters.addVerbose(process);
        final CommandLineParameterProfiler profiler = parameters.addProfiler(process, QubBuild.class);
        final CommandLineParameterHelp help = parameters.addHelp();

        QubBuildParameters result = null;
        if (!help.showApplicationHelpLines(process).await())
        {
            profiler.await();

            final CharacterWriteStream output = process.getOutputCharacterWriteStream();
            final Folder folderToBuild = folderToBuildParameter.getValue().await();
            final EnvironmentVariables environmentVariables = process.getEnvironmentVariables();
            final ProcessFactory processFactory = process.getProcessFactory();
            result = new QubBuildParameters(output, folderToBuild, environmentVariables, processFactory)
                .setWarnings(warningsParameter.getValue().await())
                .setUseBuildJson(buildJsonParameter.getValue().await())
                .setVerbose(verboseParameter.getVerboseCharacterWriteStream().await());
        }

        return result;
    }

    static int run(QubBuildParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final CharacterWriteStream output = parameters.getOutput();
        final Folder folderToBuild = parameters.getFolderToBuild();
        final EnvironmentVariables environmentVariables = parameters.getEnvironmentVariables();
        final Warnings warnings = parameters.getWarnings();
        final boolean useBuildJson = parameters.getUseBuildJson();
        final VerboseCharacterWriteStream verbose = parameters.getVerbose();
        final JavaCompiler javaCompiler = parameters.getJavaCompiler();

        IntegerValue exitCode = new IntegerValue(0);
        final String qubHome = environmentVariables.get("QUB_HOME")
            .catchError(NotFoundException.class)
            .await();
        if (Strings.isNullOrEmpty(qubHome))
        {
            error(output, exitCode, "A QUB_HOME folder path environment variable must be specified.").await();
        }

        final File projectJsonFile = folderToBuild.getFile("project.json").await();
        final FileSystem fileSystem = folderToBuild.getFileSystem();

        verbose.writeLine("Parsing " + projectJsonFile.relativeTo(folderToBuild).toString() + "...").await();
        final ProjectJSON projectJson = ProjectJSON.parse(projectJsonFile)
            .catchError((Throwable error) -> error(output, exitCode, Exceptions.unwrap(error).getMessage()).await())
            .await();
        if (exitCode.equals(0) && projectJson != null)
        {
            final ProjectJSONJava projectJsonJava = projectJson.getJava();
            if (projectJsonJava == null)
            {
                error(output, exitCode, "No language specified in project.json. Nothing to compile.").await();
            }

            if (exitCode.equals(0))
            {
                javaCompiler.setVerbose(verbose);
                javaCompiler.checkJavaVersion(projectJsonJava.getVersion(), environmentVariables, fileSystem)
                    .catchError((Throwable error) -> error(output, exitCode, error.getMessage()).await())
                    .await();
            }

            if (exitCode.equals(0))
            {
                javaCompiler.setMaximumErrors(projectJsonJava.getMaximumErrors());
                javaCompiler.setMaximumWarnings(projectJsonJava.getMaximumWarnings());

                Iterable<Dependency> dependencies = projectJsonJava.getDependencies();
                if (!Iterable.isNullOrEmpty(dependencies))
                {
                    final Folder qubFolder = fileSystem.getFolder(qubHome).await();
                    javaCompiler.setQubFolder(qubFolder);

                    final Map<Dependency,Iterable<Dependency>> dependencyMap = getAllDependencies(qubFolder, dependencies);
                    dependencies = dependencyMap.getKeys();

                    final Set<Dependency> errorDependencies = Set.create();
                    for (final Dependency dependency : dependencies)
                    {
                        if (!errorDependencies.contains(dependency))
                        {
                            final Iterable<Dependency> matchingDependencies = dependencies.where((Dependency otherDependency) ->
                                Comparer.equal(dependency.getPublisher(), otherDependency.getPublisher()) &&
                                    Comparer.equal(dependency.getProject(), otherDependency.getProject()))
                                .toList();
                            if (matchingDependencies.getCount() > 1)
                            {
                                errorDependencies.addAll(matchingDependencies);
                                final InMemoryCharacterStream errorMessage = new InMemoryCharacterStream();
                                errorMessage.writeLine("Found more than one required version for package " + dependency.getPublisher() + "/" + dependency.getProject() + ":").await();
                                int number = 0;
                                final String initialIndent = Strings.repeat(' ', Integers.toString(matchingDependencies.getCount()).length() + 2);
                                for (final Dependency matchingDependency : matchingDependencies)
                                {
                                    ++number;
                                    errorMessage.writeLine(number + ". " + matchingDependency.getPublisher() + "/" + matchingDependency.getProject() + "@" + matchingDependency.getVersion()).await();
                                    final Iterable<Dependency> path = dependencyMap.get(matchingDependency).await();
                                    String indent = initialIndent;
                                    for (final Dependency pathDependency : path)
                                    {
                                        indent += "  ";
                                        errorMessage.writeLine(indent + "from " + matchingDependency.getPublisher() + "/" + pathDependency.getProject() + "@" + pathDependency.getVersion()).await();
                                    }
                                }
                                error(output, exitCode, errorMessage.getText().await()).await();
                            }
                        }
                    }

                    for (final Dependency dependency : dependencies)
                    {
                        final Folder publisherFolder = qubFolder.getFolder(dependency.getPublisher()).await();
                        if (!publisherFolder.exists().await())
                        {
                            error(output, exitCode, "No publisher folder named " + Strings.escapeAndQuote(dependency.getPublisher()) + " found in the Qub folder (" + qubFolder + ").").await();
                        }
                        else
                        {
                            final Folder projectFolder = publisherFolder.getFolder(dependency.getProject()).await();
                            if (!projectFolder.exists().await())
                            {
                                error(output, exitCode, "No project folder named " + Strings.escapeAndQuote(dependency.getProject()) + " found in the " + Strings.escapeAndQuote(dependency.getPublisher()) + " publisher folder (" + publisherFolder + ").").await();
                            }
                            else
                            {
                                final Folder versionFolder = projectFolder.getFolder(dependency.getVersion()).await();
                                if (!versionFolder.exists().await())
                                {
                                    error(output, exitCode, "No version folder named " + Strings.escapeAndQuote(dependency.getVersion()) + " found in the " + Strings.escapeAndQuote(dependency.getProject()) + " project folder (" + projectFolder + ").").await();
                                }
                                else
                                {
                                    final File dependencyFile = versionFolder.getFile(dependency.getProject() + ".jar").await();
                                    if (!dependencyFile.exists().await())
                                    {
                                        error(output, exitCode, "No dependency file named " + Strings.escapeAndQuote(dependencyFile.getName()) + " found in the " + Strings.escapeAndQuote(dependency.getVersion()) + " version folder (" + versionFolder + ").").await();
                                    }
                                }
                            }
                        }
                    }
                    javaCompiler.setDependencies(dependencies);
                }
            }

            if (exitCode.equals(0))
            {
                Function1<File,Boolean> sourceFileMatcher;
                final Iterable<PathPattern> sourceFilePatterns = projectJsonJava.getSourceFilePatterns();
                if (!Iterable.isNullOrEmpty(sourceFilePatterns))
                {
                    sourceFileMatcher = (File file) -> sourceFilePatterns.contains((PathPattern pattern) -> pattern.isMatch(file.getPath().relativeTo(folderToBuild)));
                }
                else
                {
                    sourceFileMatcher = (File file) -> ".java".equalsIgnoreCase(file.getFileExtension());
                }
                final Iterable<File> javaSourceFiles = getJavaSourceFiles(folderToBuild, sourceFileMatcher)
                    .catchError((Throwable error) ->
                    {
                        error(output, exitCode, error.getMessage());
                        return Iterable.create();
                    })
                    .await();

                if (exitCode.equals(0))
                {
                    String outputFolderName = "outputs";
                    if (!Strings.isNullOrEmpty(projectJsonJava.getOutputFolder()))
                    {
                        outputFolderName = projectJsonJava.getOutputFolder();
                    }
                    final Folder outputsFolder = folderToBuild.getFolder(outputFolderName).await();
                    final File buildJsonFile = outputsFolder.getFile("build.json").await();
                    final List<File> newJavaSourceFiles = List.create();
                    final List<File> deletedJavaSourceFiles = List.create();
                    final List<File> modifiedJavaSourceFiles = List.create();
                    final List<File> nonModifiedJavaSourceFiles = List.create();
                    final List<File> issuedJavaSourceFiles = List.create();
                    final List<BuildJSONSourceFile> buildJsonSourceFiles = List.create();
                    boolean compileEverything;
                    final BuildJSON updatedBuildJson = new BuildJSON();
                    if (!useBuildJson)
                    {
                        compileEverything = true;
                        outputsFolder.delete()
                            .catchError(FolderNotFoundException.class)
                            .await();
                        outputsFolder.create().await();
                    }
                    else
                    {
                        if (!outputsFolder.exists().await())
                        {
                            compileEverything = true;
                            newJavaSourceFiles.addAll(javaSourceFiles);
                            buildJsonSourceFiles.addAll(BuildJSONSourceFile.create(javaSourceFiles, folderToBuild));
                        }
                        else
                        {
                            verbose.writeLine("Parsing " + buildJsonFile.relativeTo(folderToBuild).toString() + "...").await();

                            final BuildJSON buildJson = BuildJSON.parse(buildJsonFile)
                                .catchError(FileNotFoundException.class)
                                .await();
                            if (buildJson == null)
                            {
                                compileEverything = true;
                                newJavaSourceFiles.addAll(javaSourceFiles);
                                buildJsonSourceFiles.addAll(BuildJSONSourceFile.create(javaSourceFiles, folderToBuild));
                            }
                            else
                            {
                                compileEverything = shouldCompileEverything(buildJson.getProjectJson(), projectJson);

                                for (final File javaSourceFile : javaSourceFiles)
                                {
                                    final Path javaSourceFileRelativePath = javaSourceFile.relativeTo(folderToBuild);

                                    final BuildJSONSourceFile buildJsonSource = buildJson.getSourceFile(javaSourceFileRelativePath)
                                        .catchError(NotFoundException.class)
                                        .await();
                                    if (buildJsonSource == null)
                                    {
                                        newJavaSourceFiles.add(javaSourceFile);
                                        buildJsonSourceFiles.add(BuildJSONSourceFile.create(javaSourceFile, folderToBuild, javaSourceFiles));
                                    }
                                    else if (!javaSourceFile.getLastModified().await().equals(buildJsonSource.getLastModified()))
                                    {
                                        modifiedJavaSourceFiles.add(javaSourceFile);
                                        buildJsonSourceFiles.add(BuildJSONSourceFile.create(javaSourceFile, folderToBuild, javaSourceFiles));
                                    }
                                    else if (!Iterable.isNullOrEmpty(buildJsonSource.getIssues()))
                                    {
                                        issuedJavaSourceFiles.add(javaSourceFile);
                                        buildJsonSourceFiles.add(BuildJSONSourceFile.create(javaSourceFile, folderToBuild, javaSourceFiles));
                                    }
                                    else
                                    {
                                        nonModifiedJavaSourceFiles.add(javaSourceFile);
                                        buildJsonSourceFiles.add(buildJsonSource);
                                    }
                                }

                                for (final BuildJSONSourceFile buildJsonSource : buildJson.getSourceFiles())
                                {
                                    final Path buildJsonSourceFilePath = buildJsonSource.getRelativePath();
                                    final File buildJsonSourceFile = folderToBuild.getFile(buildJsonSourceFilePath).await();
                                    if (!javaSourceFiles.contains(buildJsonSourceFile))
                                    {
                                        deletedJavaSourceFiles.add(buildJsonSourceFile);
                                    }
                                }

                                writeFileList(verbose, deletedJavaSourceFiles, "Deleted source files").await();
                                for (final File deletedSourceFile : deletedJavaSourceFiles)
                                {
                                    getClassFile(deletedSourceFile, folderToBuild, outputsFolder)
                                        .delete()
                                        .catchError(FileNotFoundException.class)
                                        .await();
                                }
                            }
                        }

                        verbose.writeLine("Updating " + buildJsonFile.relativeTo(folderToBuild).toString() + "...").await();
                        verbose.writeLine("Setting project.json...").await();
                        updatedBuildJson.setProjectJson(projectJson);
                        verbose.writeLine("Setting source files...").await();
                        updatedBuildJson.setSourceFiles(buildJsonSourceFiles);
                    }

                    verbose.writeLine("Detecting java source files to compile...").await();
                    final Set<File> javaSourceFilesToCompile = Set.create();
                    if (compileEverything)
                    {
                        verbose.writeLine("Compiling all source files.").await();
                        javaSourceFilesToCompile.addAll(javaSourceFiles);
                    }
                    else
                    {
                        writeFileList(verbose, newJavaSourceFiles, "Added source files").await();
                        javaSourceFilesToCompile.addAll(newJavaSourceFiles);

                        writeFileList(verbose, modifiedJavaSourceFiles, "Modified source files").await();
                        javaSourceFilesToCompile.addAll(modifiedJavaSourceFiles);

                        writeFileList(verbose, issuedJavaSourceFiles, "Source files that previously contained issues").await();
                        javaSourceFilesToCompile.addAll(issuedJavaSourceFiles);

                        final List<File> javaSourceFilesWithDeletedDependencies = List.create();
                        for (final File nonModifiedJavaSourceFile : nonModifiedJavaSourceFiles)
                        {
                            final Path relativePath = nonModifiedJavaSourceFile.relativeTo(folderToBuild);
                            final BuildJSONSourceFile parseJSONSourceFile = updatedBuildJson.getSourceFile(relativePath).await();
                            final Iterable<Path> dependencies = parseJSONSourceFile.getDependencies();
                            if (!Iterable.isNullOrEmpty(dependencies))
                            {
                                final Iterable<File> dependencyFiles = dependencies.map((Path dependencyPath) -> folderToBuild.getFile(dependencyPath).await());
                                for (final File dependencyFile : dependencyFiles)
                                {
                                    if (deletedJavaSourceFiles.contains(dependencyFile))
                                    {
                                        javaSourceFilesWithDeletedDependencies.add(nonModifiedJavaSourceFile);
                                        javaSourceFilesToCompile.add(nonModifiedJavaSourceFile);
                                        break;
                                    }
                                }
                            }
                        }
                        writeFileList(verbose, javaSourceFilesWithDeletedDependencies, "Source files with deleted dependencies").await();

                        final List<File> javaSourceFilesWithModifiedDependencies = List.create();
                        final List<File> filesToNotCompile = List.create(nonModifiedJavaSourceFiles);
                        boolean filesToNotCompileChanged = true;
                        while(filesToNotCompileChanged && filesToNotCompile.any())
                        {
                            filesToNotCompileChanged = false;
                            for (final File fileToNotCompile : List.create(filesToNotCompile))
                            {
                                final Path relativePath = fileToNotCompile.relativeTo(folderToBuild);
                                final BuildJSONSourceFile parseJSONSourceFile = updatedBuildJson.getSourceFile(relativePath).await();
                                final Iterable<Path> dependencies = parseJSONSourceFile.getDependencies();
                                if (!Iterable.isNullOrEmpty(dependencies))
                                {
                                    final Iterable<File> dependencyFiles = dependencies.map((Path dependencyPath) -> folderToBuild.getFile(dependencyPath).await());
                                    for (final File dependencyFile : dependencyFiles)
                                    {
                                        if (javaSourceFilesToCompile.contains(dependencyFile))
                                        {
                                            filesToNotCompileChanged = true;
                                            javaSourceFilesWithModifiedDependencies.add(fileToNotCompile);
                                            javaSourceFilesToCompile.add(fileToNotCompile);
                                            filesToNotCompile.remove(fileToNotCompile);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        writeFileList(verbose, javaSourceFilesWithDeletedDependencies, "Source files with modified dependencies").await();

                        final List<File> javaSourceFilesWithMissingClassFiles = List.create();
                        for (final File nonModifiedJavaSourceFile : nonModifiedJavaSourceFiles)
                        {
                            final File classFile = getClassFile(nonModifiedJavaSourceFile, folderToBuild, outputsFolder);
                            if (!classFile.exists().await() && !javaSourceFilesToCompile.contains(nonModifiedJavaSourceFile))
                            {
                                javaSourceFilesWithMissingClassFiles.add(nonModifiedJavaSourceFile);
                                javaSourceFilesToCompile.add(nonModifiedJavaSourceFile);
                            }
                        }
                        writeFileList(verbose, javaSourceFilesWithMissingClassFiles, "Source files with missing class files").await();
                    }

                    if (Iterable.isNullOrEmpty(javaSourceFilesToCompile))
                    {
                        output.writeLine("No files need to be compiled.").await();
                    }
                    else
                    {
                        final int filesToCompileCount = javaSourceFilesToCompile.getCount();
                        output.writeLine("Compiling " + filesToCompileCount + " file" + (filesToCompileCount == 1 ? "" : "s") + "...").await();
                        final JavaCompilationResult compilationResult = javaCompiler
                            .compile(javaSourceFilesToCompile, folderToBuild, outputsFolder, warnings)
                            .await();
                        exitCode.set(compilationResult.exitCode);

                        verbose.writeLine("Compilation finished.").await();
                        if (!Iterable.isNullOrEmpty(compilationResult.issues))
                        {
                            final Iterable<JavaCompilerIssue> sortedIssues = compilationResult.issues
                                .order((JavaCompilerIssue lhs, JavaCompilerIssue rhs) -> lhs.sourceFilePath.compareTo(rhs.sourceFilePath) < 0);

                            final Iterable<JavaCompilerIssue> warningIssues = sortedIssues.where((JavaCompilerIssue issue) -> issue.type == Issue.Type.Warning);
                            final int warningCount = warningIssues.getCount();
                            if (warningCount > 0 && warnings == Warnings.Show)
                            {
                                output.writeLine(warningCount + " Warning" + (warningCount == 1 ? "" : "s") + ":").await();
                                for (final JavaCompilerIssue warning : warningIssues)
                                {
                                    output.writeLine(warning.sourceFilePath + " (Line " + warning.lineNumber + "): " + warning.message).await();
                                    final BuildJSONSourceFile sourceFile = updatedBuildJson.getSourceFile(Path.parse(warning.sourceFilePath)).await();
                                    sourceFile.addIssue(warning);
                                }
                            }

                            final Iterable<JavaCompilerIssue> errors = sortedIssues.where((JavaCompilerIssue issue) -> issue.type == Issue.Type.Error);
                            final int errorCount = errors.getCount();
                            if (errorCount > 0)
                            {
                                output.writeLine(errorCount + " Error" + (errorCount == 1 ? "" : "s") + ":").await();
                                for (final JavaCompilerIssue error : errors)
                                {
                                    output.writeLine(error.sourceFilePath + " (Line " + error.lineNumber + "): " + error.message).await();
                                    final BuildJSONSourceFile sourceFile = updatedBuildJson.getSourceFile(Path.parse(error.sourceFilePath)).await();

                                    sourceFile.addIssue(error);
                                }
                            }
                        }
                    }

                    if (useBuildJson)
                    {
                        verbose.writeLine("Writing build.json file...").await();
                        updatedBuildJson.write(buildJsonFile).await();
                        verbose.writeLine("Done writing build.json file...").await();
                    }
                }
            }
        }

        return exitCode.getAsInt();
    }

    public static Result<Void> writeFileList(VerboseCharacterWriteStream verbose, Iterable<File> files, String description)
    {
        return Result.create(() ->
        {
            if (verbose.isVerbose() && !Iterable.isNullOrEmpty(files))
            {
                verbose.writeLine(description + ":").await();
                for (final File file : files)
                {
                    verbose.writeLine(file.getPath().toString()).await();
                }
            }
        });
    }

    static boolean shouldCompileEverything(ProjectJSON oldProjectJson, ProjectJSON newProjectJson)
    {
        boolean result = false;

        if (oldProjectJson != null && oldProjectJson.getJava() != null &&
            newProjectJson != null && newProjectJson.getJava() != null)
        {
            final ProjectJSONJava oldProjectJsonJava = oldProjectJson.getJava();
            final ProjectJSONJava newProjectJsonJava = newProjectJson.getJava();

            if (!result)
            {
                final String oldProjectJsonJavaVersion = oldProjectJsonJava.getVersion();
                final String newProjectJsonJavaVersion = newProjectJsonJava.getVersion();
                if (!Comparer.equal(oldProjectJsonJavaVersion, newProjectJsonJavaVersion))
                {
                    result = !isJava8(oldProjectJsonJavaVersion) || !isJava8(newProjectJsonJavaVersion);
                }
            }

            if (!result)
            {
                final Iterable<Dependency> oldProjectJsonJavaDependencies = oldProjectJsonJava.getDependencies();
                final Iterable<Dependency> newProjectJsonJavaDependencies = newProjectJsonJava.getDependencies();
                if (!Iterable.isNullOrEmpty(oldProjectJsonJavaDependencies))
                {
                    result = Iterable.isNullOrEmpty(newProjectJsonJavaDependencies) ||
                        oldProjectJsonJavaDependencies.contains((Dependency oldDependency) ->
                            !newProjectJsonJavaDependencies.contains(oldDependency));
                }
            }
        }

        return result;
    }

    static boolean isJava8(String javaVersion)
    {
        return Strings.isOneOf(javaVersion, Iterable.create("8", "1.8", "8.0"));
    }

    static boolean isJava11(String javaVersion)
    {
        return Strings.isOneOf(javaVersion, Iterable.create("11", "11.0"));
    }

    /**
     * Get all of the Java source files found in the provided folder.
     * @param folder The folder to look for Java source files in.
     * @return All of the Java source files found in the provided folder.
     */
    static Result<Iterable<File>> getJavaSourceFiles(Folder folder, Function1<File,Boolean> sourceFileMatcher)
    {
        PreCondition.assertNotNull(folder, "folder");
        PreCondition.assertNotNull(sourceFileMatcher, "sourceFileMatcher");

        return Result.create(() ->
        {
            final Iterable<File> files = folder.getFilesRecursively().await();
            final Iterable<File> javaSourceFiles = files
                .where(sourceFileMatcher)
                .toList();
            if (!javaSourceFiles.any())
            {
                throw new NotFoundException("No java source files found in " + folder + ".");
            }
            return javaSourceFiles;
        });
    }

    static File getClassFile(File sourceFile, Folder rootFolder, Folder outputFolder)
    {
        final Path sourceFileRelativeToRootPath = sourceFile.relativeTo(rootFolder);
        final String sourceFileRelativePathFirstSegment = sourceFileRelativeToRootPath.getSegments().first();
        final Folder sourceFolder = rootFolder.getFolder(sourceFileRelativePathFirstSegment).await();
        final Path sourceFileRelativeToSourcePath = sourceFile.relativeTo(sourceFolder);
        return outputFolder.getFile(sourceFileRelativeToSourcePath.changeFileExtension(".class")).await();
    }

    static Result<Void> error(CharacterWriteStream output, IntegerValue exitCode, String message)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(exitCode, "exitCode");
        PreCondition.assertNotNull(message, "message");

        return Result.create(() ->
        {
            output.writeLine("ERROR: " + message).await();
            exitCode.increment();
        });
    }

    static String getDependencyRelativeFolderPathString(Dependency dependency)
    {
        PreCondition.assertNotNull(dependency, "dependency");

        return dependency.getPublisher() + "/" +
            dependency.getProject() + "/" +
            dependency.getVersion();
    }

    static Path getDependencyRelativeFolderPath(Dependency dependency)
    {
        PreCondition.assertNotNull(dependency, "dependency");

        return Path.parse(getDependencyRelativeFolderPathString(dependency));
    }

    static Folder getDependencyFolder(Folder qubFolder, Dependency dependency)
    {
        PreCondition.assertNotNull(qubFolder, "qubFolder");
        PreCondition.assertNotNull(dependency, "dependency");

        return qubFolder.getFolder(getDependencyRelativeFolderPath(dependency)).await();
    }

    static File getDependencyProjectJsonFile(Folder qubFolder, Dependency dependency)
    {
        PreCondition.assertNotNull(qubFolder, "qubFolder");
        PreCondition.assertNotNull(dependency, "dependency");

        return getDependencyFolder(qubFolder, dependency).getFile("project.json").await();
    }

    static String getDependencyRelativePathString(Dependency dependency)
    {
        PreCondition.assertNotNull(dependency, "dependency");

        return getDependencyRelativeFolderPathString(dependency) + "/" +
            dependency.getProject() + ".jar";
    }

    static Path getDependencyRelativePath(Dependency dependency)
    {
        PreCondition.assertNotNull(dependency, "dependency");

        return Path.parse(getDependencyRelativePathString(dependency));
    }

    static File resolveDependencyReference(Folder qubFolder, Dependency dependency)
    {
        PreCondition.assertNotNull(qubFolder, "qubFolder");
        PreCondition.assertNotNull(dependency, "dependency");

        return qubFolder.getFile(getDependencyRelativePath(dependency)).await();
    }

    static Map<Dependency,Iterable<Dependency>> getAllDependencies(Folder qubFolder, Iterable<Dependency> dependencies)
    {
        PreCondition.assertNotNull(qubFolder, "qubFolder");

        final MutableMap<Dependency,Iterable<Dependency>> result = Map.create();
        if (!Iterable.isNullOrEmpty(dependencies))
        {
            getAllDependencies(qubFolder, dependencies, result, List.create());
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    static void getAllDependencies(Folder qubFolder, Iterable<Dependency> dependencies, MutableMap<Dependency,Iterable<Dependency>> resultMap, List<Dependency> currentPath)
    {
        PreCondition.assertNotNull(qubFolder, "qubFolder");
        PreCondition.assertNotNullAndNotEmpty(dependencies, "dependencies");
        PreCondition.assertNotNull(resultMap, "resultMap");
        PreCondition.assertNotNull(currentPath, "currentPath");

        for (final Dependency dependency : dependencies)
        {
            if (!resultMap.containsKey(dependency))
            {
                resultMap.set(dependency, currentPath.toArray());

                final File dependencyProjectJsonFile = getDependencyProjectJsonFile(qubFolder, dependency);
                final ProjectJSON dependencyProjectJson = ProjectJSON.parse(dependencyProjectJsonFile)
                    .catchError(NotFoundException.class)
                    .await();
                if (dependencyProjectJson != null)
                {
                    final ProjectJSONJava dependencyProjectJsonJava = dependencyProjectJson.getJava();
                    if (dependencyProjectJsonJava != null)
                    {
                        final Iterable<Dependency> nextDependencies = dependencyProjectJsonJava.getDependencies();
                        if (!Iterable.isNullOrEmpty(nextDependencies))
                        {
                            currentPath.add(dependency);
                            getAllDependencies(qubFolder, nextDependencies, resultMap, currentPath);
                            currentPath.removeLast();
                        }
                    }
                }
            }
        }
    }
}