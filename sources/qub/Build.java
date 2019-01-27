package qub;

public class Build
{
    private JavaCompiler javaCompiler;

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

        final CommandLine commandLine = console.getCommandLine();
        if (commandLine.contains(Build::showUsage))
        {
            console.writeLine("Usage: qub-build");
            console.writeLine("  Used to compile and package source code projects.");
        }
        else
        {
            final Stopwatch stopwatch = console.getStopwatch();
            stopwatch.start();

            console.write("Compiling...");

            final Folder rootFolder = console.getCurrentFolder().throwErrorOrGetValue();
            final File projectJsonFile = rootFolder.getFile("project.json").throwErrorOrGetValue();
            final ProjectJSON projectJson = ProjectJSON.parse(projectJsonFile).throwErrorOrGetValue();
            final ProjectJSONJava projectJsonJava = projectJson.getJava();
            if (projectJsonJava == null)
            {
                console.writeLine();
                console.writeLine(" No language specified in project.json. Nothing to compile.");
            }
            else
            {
                final JavaCompiler javaCompiler = getJavaCompiler(JavacJavaCompiler::new);
                javaCompiler.checkJavaVersion(projectJsonJava.getVersion(), console)
                    .then(() ->
                    {
                        final Iterable<File> javaSourceFiles = getJavaSourceFiles(rootFolder).throwErrorOrGetValue();

                        String outputFolderName = "outputs";
                        if (!Strings.isNullOrEmpty(projectJsonJava.getOutputFolder()))
                        {
                            outputFolderName = projectJsonJava.getOutputFolder();
                        }
                        final Folder outputsFolder = rootFolder.getFolder(outputFolderName).throwErrorOrGetValue();
                        final File parseJsonFile = outputsFolder.getFile("parse.json").throwErrorOrGetValue();
                        final List<File> newJavaSourceFiles = List.create();
                        final List<File> deletedJavaSourceFiles = List.create();
                        final List<File> modifiedJavaSourceFiles = List.create();
                        final List<File> nonModifiedJavaSourceFiles = List.create();
                        final List<ParseJSONSourceFile> parseJsonSourceFiles = List.create();
                        outputsFolder.create()
                            .then(() ->
                            {
                                newJavaSourceFiles.addAll(javaSourceFiles);
                                parseJsonSourceFiles.addAll(ParseJSONSourceFile.create(javaSourceFiles, rootFolder));
                            })
                            .catchError(FolderAlreadyExistsException.class, () ->
                            {
                                ParseJSON.parse(parseJsonFile).then((ParseJSON parseJson) ->
                                {
                                    for (final File javaSourceFile : javaSourceFiles)
                                    {
                                        final Path javaSourceFileRelativePath = javaSourceFile.relativeTo(rootFolder);

                                        parseJson.getSourceFile(javaSourceFileRelativePath).then((ParseJSONSourceFile parseJsonSource) ->
                                        {
                                            if (!javaSourceFile.getLastModified().throwErrorOrGetValue().equals(parseJsonSource.getLastModified()))
                                            {
                                                modifiedJavaSourceFiles.add(javaSourceFile);
                                                parseJsonSourceFiles.add(ParseJSONSourceFile.create(javaSourceFile, rootFolder, javaSourceFiles));
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
                                                parseJsonSourceFiles.add(ParseJSONSourceFile.create(javaSourceFile, rootFolder, javaSourceFiles));
                                            });
                                    }

                                    for (final ParseJSONSourceFile parseJsonSource : parseJson.getSourceFiles())
                                    {
                                        final Path parseJsonSourceFilePath = parseJsonSource.getRelativePath();
                                        final File parseJsonSourceFile = rootFolder.getFile(parseJsonSourceFilePath).throwErrorOrGetValue();
                                        if (!javaSourceFiles.contains(parseJsonSourceFile))
                                        {
                                            deletedJavaSourceFiles.add(parseJsonSourceFile);
                                        }
                                    }
                                })
                                    .catchError(FileNotFoundException.class, () ->
                                    {
                                        newJavaSourceFiles.addAll(javaSourceFiles);
                                        parseJsonSourceFiles.addAll(ParseJSONSourceFile.create(javaSourceFiles, rootFolder));
                                    });
                            })
                            .throwError();

                        final ParseJSON updatedParseJson = new ParseJSON();
                        updatedParseJson.setSourceFiles(parseJsonSourceFiles);


                        try (final CharacterWriteStream parseJsonWriteStream = parseJsonFile.getContentCharacterWriteStream().throwErrorOrGetValue())
                        {
                            JSON.object(parseJsonWriteStream, (JSONObjectBuilder parseJsonBuilder) ->
                            {
                                for (final ParseJSONSourceFile parseJSONSourceFile : parseJsonSourceFiles)
                                {
                                    parseJSONSourceFile.writeJson(parseJsonBuilder);
                                }
                            });
                        }

                        for (final File deletedSourceFile : deletedJavaSourceFiles)
                        {
                            final File classFile = getClassFile(deletedSourceFile, rootFolder, outputsFolder);
                            classFile.delete().catchError(java.io.FileNotFoundException.class, () -> {});
                        }

                        final Set<File> javaSourceFilesToCompile = Set.create();
                        javaSourceFilesToCompile.addAll(newJavaSourceFiles);
                        javaSourceFilesToCompile.addAll(modifiedJavaSourceFiles);
                        javaSourceFilesToCompile.addAll(nonModifiedJavaSourceFiles.where((File javaSourceFile) ->
                        {
                            final Path javaSourceFileRelativePath = javaSourceFile.relativeTo(rootFolder);
                            final ParseJSONSourceFile parseJsonSourceFile = updatedParseJson.getSourceFile(javaSourceFileRelativePath).throwErrorOrGetValue();
                            final Iterable<Path> dependencyRelativeFilePaths = parseJsonSourceFile.getDependencies();
                            final Iterable<File> dependencyFiles = dependencyRelativeFilePaths.map((Path dependencyRelativeFilePath) -> rootFolder.getFile(dependencyRelativeFilePath).throwErrorOrGetValue());
                            boolean shouldCompile = false;
                            for (final File dependencyFile : dependencyFiles)
                            {
                                shouldCompile = modifiedJavaSourceFiles.contains(dependencyFile) ||
                                    deletedJavaSourceFiles.contains(dependencyFile);
                                if (shouldCompile)
                                {
                                    break;
                                }
                            }
                            return shouldCompile;
                        }));

                        if (javaSourceFilesToCompile.any())
                        {
                            javaCompiler
                                .compile(javaSourceFilesToCompile, rootFolder, outputsFolder, console)
                                .throwError();
                        }
                    })
                    .catchError((Throwable error) ->
                    {
                        console.writeLine();
                        console.writeLine(" " + error.getMessage());
                    });
            }

            final Duration compilationDuration = stopwatch.stop().toSeconds();
            console.writeLine(" Done (" + compilationDuration.toString("0.0") + ")");
        }
    }

    /**
     * Get all of the Java source files found in the provided folder.
     * @param folder The folder to look for Java source files in.
     * @return All of the Java source files found in the provided folder.
     */
    public static Result<Iterable<File>> getJavaSourceFiles(Folder folder)
    {
        PreCondition.assertNotNull(folder, "folder");

        return folder.getFilesRecursively()
            .thenResult((Iterable<File> files) ->
            {
                Result<Iterable<File>> result;
                final Iterable<File> javaSources = files.where((File file) -> Comparer.equal(file.getFileExtension(), ".java"));
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
        final Folder sourceFolder = rootFolder.getFolder(sourceFileRelativePathFirstSegment).throwErrorOrGetValue();
        final Path sourceFileRelativeToSourcePath = sourceFile.relativeTo(sourceFolder);
        return outputFolder.getFile(sourceFileRelativeToSourcePath.changeFileExtension(".class")).throwErrorOrGetValue();
    }

    private static boolean showUsage(CommandLineArgument argument)
    {
        final String argumentString = argument.toString();
        return argumentString.equals("/?") || argumentString.equals("-?");
    }

    public static void main(String[] args)
    {
        PreCondition.assertNotNull(args, "args");

        try (final Console console = new Console(args))
        {
            new Build().main(console);
        }
    }
}