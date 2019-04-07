package qub;

public class QubBuild
{
    private Boolean showTotalDuration;
    private JavaCompiler javaCompiler;
    private JarCreator jarCreator;

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

    /**
     * Get the JarCreator that should be used to package compiled Java source (class) files. If no
     * JarCreator has been set, then the provided creator function will be used to initialize the
     * JarCreator and then the initialized JarCreator will be returned.
     * @param creator The creator function that will initialize the JarCreator if it hasn't been set
     *                yet.
     * @return The JarCreator to use to package compiled java source (class) files.
     */
    private JarCreator getJarCreator(Function0<JarCreator> creator)
    {
        PreCondition.assertNotNull(creator, "creator");

        if (jarCreator == null)
        {
            jarCreator = creator.run();
        }
        final JarCreator result = jarCreator;

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Set the JarCreator that will be used to package compiled Java source (class) files.
     * @param jarCreator The JarCreator that will be used to package compiled Java source (class)
     *                   files.
     */
    public void setJarCreator(JarCreator jarCreator)
    {
        PreCondition.assertNotNull(jarCreator, "jarCreator");

        this.jarCreator = jarCreator;
    }

    public void main(Console console)
    {
        PreCondition.assertNotNull(console, "console");

        final CommandLine commandLine = console.getCommandLine();
        if (commandLine.contains(QubBuild::showUsage))
        {
            console.writeLine("Usage: qub-build [[-folder=]<folder-path-to-build>] [-verbose]");
            console.writeLine("  Used to compile and package source code projects.");
            console.writeLine("  -folder: The folder to build. This can be specified either with the -folder");
            console.writeLine("           argument name or without it. The current folder will be used if this");
            console.writeLine("           isn't defined.");
            console.writeLine("  -createjar: Whether or not to create a jar file from the compiled source code");
            console.writeLine("              files.");
            console.writeLine("  -verbose: Whether or not to show verbose logs.");
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

            try
            {
                console.writeLine("Compiling...").await();

                final boolean useParseJson = shouldUseParseJson(console);
                final boolean createJar = shouldCreateJar(console);
                final Warnings warnings = getWarnings(console);
                final Folder folderToBuild = getFolderToBuild(console).await();
                final File projectJsonFile = folderToBuild.getFile("project.json").await();

                verboseLog(console, "Parsing " + projectJsonFile.relativeTo(folderToBuild).toString() + "...", false).await();
                final ProjectJSON projectJson = ProjectJSON.parse(projectJsonFile)
                    .catchError((Throwable error) ->
                    {
                        error(console, error.getMessage()).await();
                        return null;
                    }).await();
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
                        javaCompiler.checkJavaVersion(projectJsonJava.getVersion(), console)
                            .catchError((Throwable error) ->
                            {
                                error(console, error.getMessage());
                            })
                            .await();
                    }

                    if (console.getExitCode() == 0)
                    {
                        javaCompiler.setMaximumErrors(projectJsonJava.getMaximumErrors());
                        javaCompiler.setMaximumWarnings(projectJsonJava.getMaximumWarnings());

                        final Iterable<Dependency> dependencies = projectJsonJava.getDependencies();
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
                                                error(console, "No version folder named " + Strings.escapeAndQuote(dependency.getVersion()) + " found in the " + Strings.escapeAndQuote(dependency.getPublisher()) + " project folder (" + projectFolder + ").").await();
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
                        Iterable<PathPattern> sourceFilePatterns = projectJsonJava.getSourceFilePatterns();
                        if (Iterable.isNullOrEmpty(sourceFilePatterns))
                        {
                            sourceFilePatterns = Iterable.create(PathPattern.parse("**/*.java"));
                        }
                        final Iterable<File> javaSourceFiles = getJavaSourceFiles(folderToBuild, sourceFilePatterns)
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
                            final File parseJsonFile = outputsFolder.getFile("parse.json").await();
                            final List<File> newJavaSourceFiles = List.create();
                            final List<File> deletedJavaSourceFiles = List.create();
                            final List<File> modifiedJavaSourceFiles = List.create();
                            final List<File> nonModifiedJavaSourceFiles = List.create();
                            final List<ParseJSONSourceFile> parseJsonSourceFiles = List.create();
                            final Value<Boolean> compileEverything = Value.create(false);
                            final ParseJSON updatedParseJson = new ParseJSON();
                            if (!useParseJson)
                            {
                                compileEverything.set(true);
                                outputsFolder.delete()
                                    .catchError(FolderNotFoundException.class)
                                    .thenResult(outputsFolder::create)
                                    .await();
                            }
                            else
                            {
                                outputsFolder.create()
                                    .then(() ->
                                    {
                                        compileEverything.set(true);
                                        newJavaSourceFiles.addAll(javaSourceFiles);
                                        parseJsonSourceFiles.addAll(ParseJSONSourceFile.create(javaSourceFiles, folderToBuild));
                                    })
                                    .catchErrorResult(FolderAlreadyExistsException.class, () ->
                                    {
                                        verboseLog(console, "Parsing " + parseJsonFile.relativeTo(folderToBuild).toString() + "...").await();
                                        return ParseJSON.parse(parseJsonFile)
                                            .then((ParseJSON parseJson) ->
                                            {
                                                compileEverything.set(shouldCompileEverything(parseJson.getProjectJson(), projectJson));

                                                for (final File javaSourceFile : javaSourceFiles)
                                                {
                                                    final Path javaSourceFileRelativePath = javaSourceFile.relativeTo(folderToBuild);

                                                    parseJson.getSourceFile(javaSourceFileRelativePath)
                                                        .then((ParseJSONSourceFile parseJsonSource) ->
                                                        {
                                                            if (!javaSourceFile.getLastModified().await().equals(parseJsonSource.getLastModified()))
                                                            {
                                                                modifiedJavaSourceFiles.add(javaSourceFile);
                                                                parseJsonSourceFiles.add(ParseJSONSourceFile.create(javaSourceFile, folderToBuild, javaSourceFiles));
                                                            }
                                                            else
                                                            {
                                                                nonModifiedJavaSourceFiles.add(javaSourceFile);
                                                                parseJsonSourceFiles.add(parseJsonSource);
                                                            }
                                                        })
                                                        .catchError(NotFoundException.class, () ->
                                                        {
                                                            newJavaSourceFiles.add(javaSourceFile);
                                                            parseJsonSourceFiles.add(ParseJSONSourceFile.create(javaSourceFile, folderToBuild, javaSourceFiles));
                                                        });
                                                }

                                                for (final ParseJSONSourceFile parseJsonSource : parseJson.getSourceFiles())
                                                {
                                                    final Path parseJsonSourceFilePath = parseJsonSource.getRelativePath();
                                                    final File parseJsonSourceFile = folderToBuild.getFile(parseJsonSourceFilePath).await();
                                                    if (!javaSourceFiles.contains(parseJsonSourceFile))
                                                    {
                                                        deletedJavaSourceFiles.add(parseJsonSourceFile);
                                                    }
                                                }
                                            })
                                            .catchError(FileNotFoundException.class, () ->
                                            {
                                                compileEverything.set(true);
                                                newJavaSourceFiles.addAll(javaSourceFiles);
                                                parseJsonSourceFiles.addAll(ParseJSONSourceFile.create(javaSourceFiles, folderToBuild));
                                            });
                                    })
                                    .await();

                                verboseLog(console, "Updating " + parseJsonFile.relativeTo(folderToBuild).toString() + "...").await();
                                verboseLog(console, "Setting project.json...").await();
                                updatedParseJson.setProjectJson(projectJson);
                                verboseLog(console, "Setting source files...").await();
                                updatedParseJson.setSourceFiles(parseJsonSourceFiles);
                                verboseLog(console, "Writing parse.json file...").await();
                                updatedParseJson.write(parseJsonFile).await();
                                verboseLog(console, "Done writing parse.json file...").await();
                            }

                            if (!Iterable.isNullOrEmpty(deletedJavaSourceFiles))
                            {
                                verboseLog(console, "Deleting class files for deleted source files...").await();
                                for (final File deletedSourceFile : deletedJavaSourceFiles)
                                {
                                    getClassFile(deletedSourceFile, folderToBuild, outputsFolder)
                                        .delete()
                                        .catchError(FileNotFoundException.class, () -> {
                                        })
                                        .await();
                                }
                            }

                            verboseLog(console, "Detecting java source files to compile...").await();
                            final Set<File> javaSourceFilesToCompile = Set.create();
                            if (compileEverything.get())
                            {
                                verboseLog(console, "Compiling all source files.").await();
                                javaSourceFilesToCompile.addAll(javaSourceFiles);
                            }
                            else
                            {
                                javaSourceFilesToCompile.addAll(newJavaSourceFiles);
                                javaSourceFilesToCompile.addAll(modifiedJavaSourceFiles);
                                javaSourceFilesToCompile.addAll(nonModifiedJavaSourceFiles.where((File javaSourceFile) ->
                                {
                                    final Path javaSourceFileRelativePath = javaSourceFile.relativeTo(folderToBuild);
                                    final ParseJSONSourceFile parseJsonSourceFile = updatedParseJson.getSourceFile(javaSourceFileRelativePath).await();
                                    final Iterable<Path> dependencyRelativeFilePaths = parseJsonSourceFile.getDependencies();
                                    boolean shouldCompile = false;
                                    if (!Iterable.isNullOrEmpty(dependencyRelativeFilePaths))
                                    {
                                        final Iterable<File> dependencyFiles = dependencyRelativeFilePaths
                                            .map((Path dependencyRelativeFilePath) ->
                                                folderToBuild.getFile(dependencyRelativeFilePath).await());
                                        for (final File dependencyFile : dependencyFiles)
                                        {
                                            if (modifiedJavaSourceFiles.contains(dependencyFile) ||
                                                deletedJavaSourceFiles.contains(dependencyFile))
                                            {
                                                shouldCompile = true;
                                                break;
                                            }
                                        }
                                    }
                                    verboseLog(console, "Compile " + javaSourceFile + "?: " + shouldCompile);
                                    return shouldCompile;
                                }));
                            }

                            if (Iterable.isNullOrEmpty(javaSourceFilesToCompile))
                            {
                                verboseLog(console, "No source files need compilation.").await();
                            }
                            else
                            {
                                verboseLog(console, "Starting compilation...");
                                final JavaCompilationResult compilationResult = javaCompiler
                                    .compile(javaSourceFilesToCompile, folderToBuild, outputsFolder, console)
                                    .await();
                                console.setExitCode(compilationResult.exitCode);

                                verboseLog(console, "Compilation finished.").await();
                                if (!Iterable.isNullOrEmpty(compilationResult.issues))
                                {
                                    final Iterable<JavaCompilerIssue> sortedIssues = compilationResult.issues
                                        .order((JavaCompilerIssue lhs, JavaCompilerIssue rhs) -> lhs.sourceFilePath.compareTo(rhs.sourceFilePath) < 0);

                                    final Iterable<JavaCompilerIssue> warningIssues = sortedIssues.where((JavaCompilerIssue issue) -> issue.type == Issue.Type.Warning);
                                    final int warningCount = warningIssues.getCount();
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

                                if (createJar && console.getExitCode() == 0)
                                {
                                    console.writeLine("Creating jar file...").await();
                                    final JarCreator jarCreator = getJarCreator(JavaJarCreator::new);
                                    jarCreator.setOutputsFolder(outputsFolder);

                                    String jarName = projectJson.getProject();
                                    if (Strings.isNullOrEmpty(jarName) && javaSourceFiles.getCount() == 1)
                                    {
                                        jarName = javaSourceFiles.first().getNameWithoutFileExtension();
                                    }
                                    if (Strings.isNullOrEmpty(jarName) && !folderToBuild.getPath().equals(folderToBuild.getRoot().getPath()))
                                    {
                                        jarName = folderToBuild.getName();
                                    }
                                    if (Strings.isNullOrEmpty(jarName))
                                    {
                                        jarName = "project";
                                    }
                                    jarCreator.setJarName(jarName);

                                    File manifestFile = null;
                                    final String mainClass = projectJsonJava.getMainClass();
                                    if (!Strings.isNullOrEmpty(mainClass))
                                    {
                                        manifestFile = outputsFolder.getFile("META-INF/MANIFEST.MF").await();
                                        final String manifestFileContents =
                                            "Manifest-Version: 1.0\n" +
                                                "Main-Class: " + mainClass + "\n";
                                        manifestFile.setContentsAsString(manifestFileContents).await();
                                        jarCreator.setManifestFile(manifestFile);
                                    }

                                    jarCreator.setClassFiles(outputsFolder.getFilesRecursively().await()
                                        .where((File outputFile) -> Comparer.equal(outputFile.getFileExtension(), ".class")));

                                    jarCreator.setSourceFiles(javaSourceFiles);

                                    jarCreator.createJarFile(console, isVerbose(console).await()).await();
                                }
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

    public static Result<Folder> getFolderToBuild(Console console)
    {
        PreCondition.assertNotNull(console, "console");

        final CommandLine commandLine = console.getCommandLine();
        Path folderPathToBuild = null;

        CommandLineArgument folderArgument = commandLine.get("folder");
        if (folderArgument == null)
        {
            folderArgument = commandLine.getArguments()
                .first((CommandLineArgument argument) -> argument.getName() == null);
        }
        if (folderArgument != null)
        {
            folderPathToBuild = Path.parse(folderArgument.getValue());
        }

        if (folderPathToBuild == null)
        {
            folderPathToBuild = console.getCurrentFolderPath();
        }

        if (!folderPathToBuild.isRooted())
        {
            folderPathToBuild = console.getCurrentFolderPath().resolve(folderPathToBuild).await();
        }

        final FileSystem fileSystem = console.getFileSystem();
        final Result<Folder> result = fileSystem.getFolder(folderPathToBuild);

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    public static boolean shouldCreateJar(Console console)
    {
        PreCondition.assertNotNull(console, "console");

        final CommandLine commandLine = console.getCommandLine();

        boolean result = true;
        CommandLineArgument createJarArgument = commandLine.get("createjar");
        if (createJarArgument != null)
        {
            result = Strings.isNullOrEmpty(createJarArgument.getValue()) ||
                java.lang.Boolean.parseBoolean(createJarArgument.getValue());
        }

        return result;
    }

    public static Warnings getWarnings(Console console)
    {
        PreCondition.assertNotNull(console, "console");

        final CommandLine commandLine = console.getCommandLine();

        Warnings result = Warnings.Show;
        final CommandLineArgument warningsArgument = commandLine.get("warnings");
        if (warningsArgument != null)
        {
            final String warningsArgumentValue = warningsArgument.getValue();
            if (!Strings.isNullOrEmpty(warningsArgumentValue))
            {
                if (warningsArgumentValue.equalsIgnoreCase(Warnings.Error.toString()))
                {
                    result = Warnings.Error;
                }
                else if (warningsArgumentValue.equalsIgnoreCase(Warnings.Hide.toString()))
                {
                    result = Warnings.Hide;
                }
            }
        }

        return result;
    }

    public static boolean shouldUseParseJson(Console console)
    {
        PreCondition.assertNotNull(console, "console");

        final CommandLine commandLine = console.getCommandLine();

        boolean result = true;

        CommandLineArgument useParseJsonArgument = commandLine.get("parsejson");
        if (useParseJsonArgument != null)
        {
            result = Strings.isNullOrEmpty(useParseJsonArgument.getValue()) ||
                java.lang.Boolean.parseBoolean(useParseJsonArgument.getValue());
        }

        return result;
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
    public static Result<Iterable<File>> getJavaSourceFiles(Folder folder, Iterable<PathPattern> sourceFilePatterns)
    {
        PreCondition.assertNotNull(folder, "folder");
        PreCondition.assertNotNullAndNotEmpty(sourceFilePatterns, "sourceFilePatterns");

        return folder.getFilesRecursively()
            .thenResult((Iterable<File> files) ->
            {
                Result<Iterable<File>> result;
                final Iterable<File> javaSources = files
                    .where((File file) -> sourceFilePatterns.contains((PathPattern pattern) -> pattern.isMatch(file.getPath().relativeTo(folder))))
                    .toArray();
                if (!javaSources.any())
                {
                    result = Result.error(new NotFoundException("No java source files found in " + folder + "."));
                }
                else
                {
                    result = Result.success(javaSources);
                }
                return result;
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

    private static boolean showUsage(CommandLineArgument argument)
    {
        final String argumentString = argument.toString();
        return argumentString.equals("/?") || argumentString.equals("-?");
    }

    public static Result<Void> verboseLog(Process process, String message)
    {
        return process instanceof Console
            ? verboseLog((Console)process, message)
            : Result.success();
    }

    public static Result<Void> verboseLog(Console console, String message)
    {
        return verboseLog(console, message, false);
    }

    public static Result<Void> verboseLog(Console console, String message, boolean showTimestamp)
    {
        return isVerbose(console)
            .thenResult((Boolean verbose) ->
            {
                Result<Void> result;
                if (verbose)
                {
                    result = console.writeLine("VERBOSE" + (showTimestamp ? "(" + System.currentTimeMillis() + ")" : "") + ": " + message).then(() -> {});
                }
                else
                {
                    result = Result.success();
                }
                return result;
            });
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

    private static Result<Boolean> isVerbose(Console console)
    {
        PreCondition.assertNotNull(console, "console");

        return isVerbose(console.getCommandLine());
    }

    private static Result<Boolean> isVerbose(CommandLine commandLine)
    {
        PreCondition.assertNotNull(commandLine, "commandLine");

        return Result.create(() ->
        {
            boolean verbose = false;

            final CommandLineArgument verboseArgument = commandLine.get("verbose");
            if (verboseArgument != null)
            {
                final String verboseArgumentValue = verboseArgument.getValue();
                verbose = Strings.isNullOrEmpty(verboseArgumentValue) || Comparer.equal(verboseArgumentValue, "true");
            }

            return verbose;
        });
    }

    public static void main(String[] args)
    {
        final QubBuild qubBuild = new QubBuild();
        Console.run(args, qubBuild::main);
    }
}