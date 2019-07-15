package qub;

public class QubBuild
{
    private Boolean showTotalDuration;
    private JavaCompiler javaCompiler;

    /**
     * Get whether or not to show the total duration of this QubBuild command. This value will default
     * to true if it hasn't been set.
     * @return Whether or not to show the total duration of this QubBuild command.
     */
    public boolean getShowTotalDuration()
    {
        if (showTotalDuration == null)
        {
            showTotalDuration = true;
        }
        final boolean result = showTotalDuration;

        return result;
    }

    /**
     * Set whether or not to show the total duration of this QubBuild command.
     * @param showTotalDuration Whether or not to show the total duration of this QubBuild command.
     */
    public void setShowTotalDuration(boolean showTotalDuration)
    {
        this.showTotalDuration = showTotalDuration;
    }

    /**
     * Get the JavaCompiler that should be used to compile Java source files. If no JavaCompiler has
     * been set, then the provided creator function will be used to initialize the JavaCompiler and
     * then the initialized JavaCompiler will be returned.
     * @param creator The creator function that will initialize the JavaCompiler if it hasn't been
     *                set yet.
     * @return The JavaCompiler to use to compile Java source files.
     */
    private JavaCompiler getJavaCompiler(Function0<JavaCompiler> creator)
    {
        PreCondition.assertNotNull(creator, "creator");

        if (javaCompiler == null)
        {
            javaCompiler = creator.run();
        }
        final JavaCompiler result = javaCompiler;

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Set the JavaCompiler that will be used to compile Java source files.
     * @param javaCompiler The JavaCompiler that will be used to compile Java source files.
     */
    public void setJavaCompiler(JavaCompiler javaCompiler)
    {
        PreCondition.assertNotNull(javaCompiler, "javaCompiler");

        this.javaCompiler = javaCompiler;
    }

    public void main(Console console)
    {
        PreCondition.assertNotNull(console, "console");

        final CommandLineParameters parameters = console.createCommandLineParameters();

        final CommandLineParameter<Folder> folderToBuildParameter = parameters.addPositionalFolder("folder", console)
            .setValueName("<folder-path-to-build>")
            .setDescription("The folder to build. The current folder will be used if this isn't defined.");
        final CommandLineParameter<Warnings> warningsParameter = parameters.addEnum("warnings", Warnings.Show)
            .setValueName("<show|error|hide>")
            .setDescription("How to handle build warnings. Can be either \"show\", \"error\", or \"hide\". Defaults to \"show\".");
        final CommandLineParameterBoolean buildJsonParameter = parameters.addBoolean("buildjson", true)
            .setDescription("Whether or not to read and write a build.json file. Defaults to true.");
        final CommandLineParameterVerbose verbose = parameters.addVerbose(console);
        final CommandLineParameterProfiler profiler = parameters.addProfiler(console, QubBuild.class);
        final CommandLineParameter<Boolean> help = parameters.addHelp();

        if (help.getValue().await())
        {
            parameters.writeHelpLines(console, "qub-build", "Used to compile and package source code projects.").await();
            console.setExitCode(-1);
        }
        else
        {
            final boolean showTotalDuration = getShowTotalDuration();
            final Stopwatch stopwatch = console.getStopwatch();
            if (showTotalDuration)
            {
                stopwatch.start();
            }

            profiler.await();

            try
            {
                console.writeLine("Compiling...").await();

                final Folder folderToBuild = folderToBuildParameter.getValue().await();
                final File projectJsonFile = folderToBuild.getFile("project.json").await();

                verbose.writeLine("Parsing " + projectJsonFile.relativeTo(folderToBuild).toString() + "...").await();
                final ProjectJSON projectJson = ProjectJSON.parse(projectJsonFile)
                    .catchError((Throwable error) -> error(console, error.getMessage()).await())
                    .await();
                if (projectJson != null)
                {
                    final ProjectJSONJava projectJsonJava = projectJson.getJava();
                    if (projectJsonJava == null)
                    {
                        error(console, "No language specified in project.json. Nothing to compile.").await();
                    }

                    if (console.getExitCode() == 0)
                    {
                        javaCompiler = getJavaCompiler(JavacJavaCompiler::new);
                        javaCompiler.setVerbose(verbose);
                        javaCompiler.checkJavaVersion(projectJsonJava.getVersion(), console)
                            .catchError((Throwable error) -> error(console, error.getMessage()).await())
                            .await();
                    }

                    if (console.getExitCode() == 0)
                    {
                        javaCompiler.setMaximumErrors(projectJsonJava.getMaximumErrors());
                        javaCompiler.setMaximumWarnings(projectJsonJava.getMaximumWarnings());

                        Iterable<Dependency> dependencies = projectJsonJava.getDependencies();
                        if (!Iterable.isNullOrEmpty(dependencies))
                        {
                            final String qubHome = console.getEnvironmentVariable("QUB_HOME");
                            if (Strings.isNullOrEmpty(qubHome))
                            {
                                error(console, "Cannot resolve project dependencies without a QUB_HOME environment variable.").await();
                            }
                            else
                            {
                                final Folder qubFolder = console.getFileSystem().getFolder(qubHome).await();
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
                                            error(console, errorMessage.getText().await()).await();
                                        }
                                    }
                                }

                                for (final Dependency dependency : dependencies)
                                {
                                    final Folder publisherFolder = qubFolder.getFolder(dependency.getPublisher()).await();
                                    if (!publisherFolder.exists().await())
                                    {
                                        error(console, "No publisher folder named " + Strings.escapeAndQuote(dependency.getPublisher()) + " found in the Qub folder (" + qubFolder + ").").await();
                                    }
                                    else
                                    {
                                        final Folder projectFolder = publisherFolder.getFolder(dependency.getProject()).await();
                                        if (!projectFolder.exists().await())
                                        {
                                            error(console, "No project folder named " + Strings.escapeAndQuote(dependency.getProject()) + " found in the " + Strings.escapeAndQuote(dependency.getPublisher()) + " publisher folder (" + publisherFolder + ").").await();
                                        }
                                        else
                                        {
                                            final Folder versionFolder = projectFolder.getFolder(dependency.getVersion()).await();
                                            if (!versionFolder.exists().await())
                                            {
                                                error(console, "No version folder named " + Strings.escapeAndQuote(dependency.getVersion()) + " found in the " + Strings.escapeAndQuote(dependency.getProject()) + " project folder (" + projectFolder + ").").await();
                                            }
                                            else
                                            {
                                                final File dependencyFile = versionFolder.getFile(dependency.getProject() + ".jar").await();
                                                if (!dependencyFile.exists().await())
                                                {
                                                    error(console, "No dependency file named " + Strings.escapeAndQuote(dependencyFile.getName()) + " found in the " + Strings.escapeAndQuote(dependency.getVersion()) + " version folder (" + versionFolder + ").").await();
                                                }
                                            }
                                        }
                                    }
                                }
                                javaCompiler.setDependencies(dependencies);
                            }
                        }
                    }

                    if (console.getExitCode() == 0)
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
                                error(console, error.getMessage());
                                return Iterable.create();
                            })
                            .await();

                        if (console.getExitCode() == 0)
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
                            final List<BuildJSONSourceFile> parseJsonSourceFiles = List.create();
                            boolean compileEverything = false;
                            final BuildJSON updatedBuildJson = new BuildJSON();
                            if (!buildJsonParameter.getValue().await())
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
                                    parseJsonSourceFiles.addAll(BuildJSONSourceFile.create(javaSourceFiles, folderToBuild));
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
                                        parseJsonSourceFiles.addAll(BuildJSONSourceFile.create(javaSourceFiles, folderToBuild));
                                    }
                                    else
                                    {
                                        compileEverything = shouldCompileEverything(buildJson.getProjectJson(), projectJson);

                                        for (final File javaSourceFile : javaSourceFiles)
                                        {
                                            final Path javaSourceFileRelativePath = javaSourceFile.relativeTo(folderToBuild);

                                            final BuildJSONSourceFile parseJsonSource = buildJson.getSourceFile(javaSourceFileRelativePath)
                                                .catchError(NotFoundException.class)
                                                .await();
                                            if (parseJsonSource == null)
                                            {
                                                newJavaSourceFiles.add(javaSourceFile);
                                                parseJsonSourceFiles.add(BuildJSONSourceFile.create(javaSourceFile, folderToBuild, javaSourceFiles));
                                            }
                                            else if (!javaSourceFile.getLastModified().await().equals(parseJsonSource.getLastModified()))
                                            {
                                                modifiedJavaSourceFiles.add(javaSourceFile);
                                                parseJsonSourceFiles.add(BuildJSONSourceFile.create(javaSourceFile, folderToBuild, javaSourceFiles));
                                            }
                                            else
                                            {
                                                nonModifiedJavaSourceFiles.add(javaSourceFile);
                                                parseJsonSourceFiles.add(parseJsonSource);
                                            }
                                        }

                                        for (final BuildJSONSourceFile parseJsonSource : buildJson.getSourceFiles())
                                        {
                                            final Path parseJsonSourceFilePath = parseJsonSource.getRelativePath();
                                            final File parseJsonSourceFile = folderToBuild.getFile(parseJsonSourceFilePath).await();
                                            if (!javaSourceFiles.contains(parseJsonSourceFile))
                                            {
                                                deletedJavaSourceFiles.add(parseJsonSourceFile);
                                            }
                                        }

                                        if (!Iterable.isNullOrEmpty(deletedJavaSourceFiles))
                                        {
                                            verbose.writeLine("Deleting class files for deleted source files...").await();
                                            for (final File deletedSourceFile : deletedJavaSourceFiles)
                                            {
                                                getClassFile(deletedSourceFile, folderToBuild, outputsFolder)
                                                    .delete()
                                                    .catchError(FileNotFoundException.class)
                                                    .await();
                                            }
                                        }
                                    }
                                }

                                verbose.writeLine("Updating " + buildJsonFile.relativeTo(folderToBuild).toString() + "...").await();
                                verbose.writeLine("Setting project.json...").await();
                                updatedBuildJson.setProjectJson(projectJson);
                                verbose.writeLine("Setting source files...").await();
                                updatedBuildJson.setSourceFiles(parseJsonSourceFiles);
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
                                javaSourceFilesToCompile.addAll(newJavaSourceFiles);
                                javaSourceFilesToCompile.addAll(modifiedJavaSourceFiles);

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
                                                javaSourceFilesToCompile.add(nonModifiedJavaSourceFile);
                                                break;
                                            }
                                        }
                                    }
                                }

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
                                                    javaSourceFilesToCompile.add(fileToNotCompile);
                                                    filesToNotCompile.remove(fileToNotCompile);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                                for (final File nonModifiedJavaSourceFile : nonModifiedJavaSourceFiles)
                                {
                                    final File classFile = getClassFile(nonModifiedJavaSourceFile, folderToBuild, outputsFolder);
                                    if (!classFile.exists().await() && !javaSourceFilesToCompile.contains(nonModifiedJavaSourceFile))
                                    {
                                        javaSourceFilesToCompile.add(nonModifiedJavaSourceFile);
                                    }
                                }
                            }

                            if (Iterable.isNullOrEmpty(javaSourceFilesToCompile))
                            {
                                verbose.writeLine("No source files need compilation.").await();
                            }
                            else
                            {
                                verbose.writeLine("Starting compilation...").await();
                                final JavaCompilationResult compilationResult = javaCompiler
                                    .compile(javaSourceFilesToCompile, folderToBuild, outputsFolder, console)
                                    .await();
                                console.setExitCode(compilationResult.exitCode);

                                verbose.writeLine("Compilation finished.").await();
                                if (!Iterable.isNullOrEmpty(compilationResult.issues))
                                {
                                    final Iterable<JavaCompilerIssue> sortedIssues = compilationResult.issues
                                        .order((JavaCompilerIssue lhs, JavaCompilerIssue rhs) -> lhs.sourceFilePath.compareTo(rhs.sourceFilePath) < 0);

                                    final Iterable<JavaCompilerIssue> warningIssues = sortedIssues.where((JavaCompilerIssue issue) -> issue.type == Issue.Type.Warning);
                                    final int warningCount = warningIssues.getCount();
                                    final Warnings warnings = warningsParameter.getValue().await();
                                    if (warningCount > 0 && warnings == Warnings.Show)
                                    {
                                        console.writeLine(warningCount + " Warning" + (warningCount == 1 ? "" : "s") + ":").await();
                                        for (final JavaCompilerIssue warning : warningIssues)
                                        {
                                            console.writeLine(warning.sourceFilePath + " (Line " + warning.lineNumber + "): " + warning.message).await();
                                        }
                                    }

                                    final Iterable<JavaCompilerIssue> errors = warnings == Warnings.Error ? sortedIssues : sortedIssues.where((JavaCompilerIssue issue) -> issue.type == Issue.Type.Error);
                                    final int errorCount = errors.getCount();
                                    if (errorCount > 0)
                                    {
                                        console.writeLine(errorCount + " Error" + (errorCount == 1 ? "" : "s") + ":").await();
                                        for (final JavaCompilerIssue error : errors)
                                        {
                                            console.writeLine(error.sourceFilePath + " (Line " + error.lineNumber + "): " + error.message).await();
                                        }
                                    }
                                }
                            }

                            if (buildJsonParameter.getValue().await())
                            {
                                verbose.writeLine("Writing build.json file...").await();
                                updatedBuildJson.write(buildJsonFile).await();
                                verbose.writeLine("Done writing build.json file...").await();
                            }
                        }
                    }
                }
            }
            finally
            {
                if (showTotalDuration)
                {
                    final Duration compilationDuration = stopwatch.stop().toSeconds();
                    console.writeLine("Done (" + compilationDuration.toString("0.0") + ")");
                }
            }
        }
    }

    public static boolean shouldCompileEverything(ProjectJSON oldProjectJson, ProjectJSON newProjectJson)
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

    public static boolean isJava8(String javaVersion)
    {
        return Strings.isOneOf(javaVersion, Iterable.create("8", "1.8", "8.0"));
    }

    public static boolean isJava11(String javaVersion)
    {
        return Strings.isOneOf(javaVersion, Iterable.create("11", "11.0"));
    }

    /**
     * Get all of the Java source files found in the provided folder.
     * @param folder The folder to look for Java source files in.
     * @return All of the Java source files found in the provided folder.
     */
    public static Result<Iterable<File>> getJavaSourceFiles(Folder folder, Function1<File,Boolean> sourceFileMatcher)
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

    public static File getClassFile(File sourceFile, Folder rootFolder, Folder outputFolder)
    {
        final Path sourceFileRelativeToRootPath = sourceFile.relativeTo(rootFolder);
        final String sourceFileRelativePathFirstSegment = sourceFileRelativeToRootPath.getSegments().first();
        final Folder sourceFolder = rootFolder.getFolder(sourceFileRelativePathFirstSegment).await();
        final Path sourceFileRelativeToSourcePath = sourceFile.relativeTo(sourceFolder);
        return outputFolder.getFile(sourceFileRelativeToSourcePath.changeFileExtension(".class")).await();
    }

    public static Result<Void> error(Console console, String message)
    {
        return error(console, message, false);
    }

    public static Result<Void> error(Console console, String message, boolean showTimestamp)
    {
        final Result<Void> result = console.writeLine("ERROR" + (showTimestamp ? "(" + System.currentTimeMillis() + ")" : "") + ": " + message).then(() -> {});
        console.incrementExitCode();
        return result;
    }

    public static String getDependencyRelativeFolderPathString(Dependency dependency)
    {
        PreCondition.assertNotNull(dependency, "dependency");

        return dependency.getPublisher() + "/" +
            dependency.getProject() + "/" +
            dependency.getVersion();
    }

    public static Path getDependencyRelativeFolderPath(Dependency dependency)
    {
        PreCondition.assertNotNull(dependency, "dependency");

        return Path.parse(getDependencyRelativeFolderPathString(dependency));
    }

    public static Folder getDependencyFolder(Folder qubFolder, Dependency dependency)
    {
        PreCondition.assertNotNull(qubFolder, "qubFolder");
        PreCondition.assertNotNull(dependency, "dependency");

        return qubFolder.getFolder(getDependencyRelativeFolderPath(dependency)).await();
    }

    public static File getDependencyProjectJsonFile(Folder qubFolder, Dependency dependency)
    {
        PreCondition.assertNotNull(qubFolder, "qubFolder");
        PreCondition.assertNotNull(dependency, "dependency");

        return getDependencyFolder(qubFolder, dependency).getFile("project.json").await();
    }

    public static String getDependencyRelativePathString(Dependency dependency)
    {
        PreCondition.assertNotNull(dependency, "dependency");

        return getDependencyRelativeFolderPathString(dependency) + "/" +
            dependency.getProject() + ".jar";
    }

    public static Path getDependencyRelativePath(Dependency dependency)
    {
        PreCondition.assertNotNull(dependency, "dependency");

        return Path.parse(getDependencyRelativePathString(dependency));
    }

    public static File resolveDependencyReference(Folder qubFolder, Dependency dependency)
    {
        PreCondition.assertNotNull(qubFolder, "qubFolder");
        PreCondition.assertNotNull(dependency, "dependency");

        return qubFolder.getFile(getDependencyRelativePath(dependency)).await();
    }

    public static Map<Dependency,Iterable<Dependency>> getAllDependencies(Folder qubFolder, Iterable<Dependency> dependencies)
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

    public static void getAllDependencies(Folder qubFolder, Iterable<Dependency> dependencies, MutableMap<Dependency,Iterable<Dependency>> resultMap, List<Dependency> currentPath)
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

    public static void main(String[] args)
    {
        final QubBuild qubBuild = new QubBuild();
        Console.run(args, qubBuild::main);
    }
}