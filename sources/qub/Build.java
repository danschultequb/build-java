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

            final Folder currentFolder = console.getCurrentFolder().throwErrorOrGetValue();
            final File projectJsonFile = currentFolder.getFile("project.json").throwErrorOrGetValue();
            final ProjectJSON projectJson = ProjectJSON.parse(projectJsonFile).throwErrorOrGetValue();
            final Folder sourceFolder = currentFolder.getFolder("sources").throwErrorOrGetValue();
            final Iterable<File> javaSourceFiles = getJavaSourceFiles(sourceFolder).throwErrorOrGetValue();

            final Folder outputsFolder = currentFolder.getFolder("outputs").throwErrorOrGetValue();
            final File parseJsonFile = outputsFolder.getFile("parse.json").throwErrorOrGetValue();
            final List<File> newJavaSourceFiles = List.create();
            final List<File> deletedJavaSourceFiles = List.create();
            final List<File> modifiedJavaSourceFiles = List.create();
            final List<File> nonModifiedJavaSourceFiles = List.create();
            outputsFolder.create().then(() ->
            {
                newJavaSourceFiles.addAll(javaSourceFiles);
            })
            .catchError(FolderAlreadyExistsException.class, () ->
            {
                ParseJSON.parse(parseJsonFile).then((ParseJSON parseJson) ->
                {
                    for (final File javaSourceFile : javaSourceFiles)
                    {
                        final Path javaSourceFileRelativePath = javaSourceFile.relativeTo(currentFolder);

                        parseJson.getSourceFile(javaSourceFileRelativePath)
                            .then((ParseJSONSourceFile parseJsonSource) ->
                            {
                                if (!javaSourceFile.getLastModified().throwErrorOrGetValue().equals(parseJsonSource.getLastModified()))
                                {
                                    modifiedJavaSourceFiles.add(javaSourceFile);
                                }
                                else
                                {
                                    nonModifiedJavaSourceFiles.add(javaSourceFile);
                                }
                            })
                            .catchError(NotFoundException.class, () ->
                            {
                                newJavaSourceFiles.add(javaSourceFile);
                            });
                    }

                    for (final ParseJSONSourceFile parseJsonSource : parseJson.getSourceFiles())
                    {
                        final Path parseJsonSourceFilePath = parseJsonSource.getRelativePath();
                        if (parseJsonSourceFilePath != null)
                        {
                            final File parseJsonSourceFile = currentFolder.getFile(parseJsonSourceFilePath).throwErrorOrGetValue();
                            if (!javaSourceFiles.contains(parseJsonSourceFile))
                            {
                                deletedJavaSourceFiles.add(parseJsonSourceFile);
                            }
                        }
                    }
                })
                .catchError(FileNotFoundException.class, () ->
                {
                    newJavaSourceFiles.addAll(javaSourceFiles);
                });
            })
            .throwError();

            final String javaVersion = projectJson.getJava().getVersion();

            final JavaCompiler javaCompiler = getJavaCompiler(JavacJavaCompiler::new);
            final JavaCompilationResult compilationResult = javaCompiler
                .compile(javaSourceFiles, sourceFolder, outputsFolder, javaVersion, console)
                .throwErrorOrGetValue();

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