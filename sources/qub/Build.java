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

            final Folder currentFolder = console.getCurrentFolder().throwErrorOrGetValue();
            final Folder outputsFolder = currentFolder.getFolder("outputs").throwErrorOrGetValue();
            final File projectJsonFile = currentFolder.getFile("project.json").throwErrorOrGetValue();
            final Value<ProjectJSON> projectJson = Value.create();
            ProjectJSON.parse(projectJsonFile)
                .then(projectJson::set)
                .thenResult(() ->
                {
                    final Folder sourcesFolder = currentFolder.getFolder("sources").throwErrorOrGetValue();
                    final Folder sourcesOutputFolder = outputsFolder.getFolder(sourcesFolder.getName()).throwErrorOrGetValue();
                    final String javaSourcesVersion = projectJson.get().getJavaSourcesVersion();
                    final JavaCompiler javaCompiler = getJavaCompiler(JavacJavaCompiler::new);
                    return javaCompiler.compile(sourcesFolder, javaSourcesVersion, sourcesOutputFolder, console);
                })
                .then((JavaCompilationResult sourcesCompilationResult) ->
                {
                    if (sourcesCompilationResult.getExitCode() == 0)
                    {

                    }
                })
                .throwError();
        }
    }



    /**
     * Get all of the Java source files found in the provided folder.
     * @param folder The folder to look for Java source files in.
     * @return All of the Java source files found in the provided folder.
     */
    public static Result<Void> getJavaSourceFiles(Folder folder, Setable<Iterable<File>> javaSourceFiles)
    {
        PreCondition.assertNotNull(folder, "folder");

        return folder.getFilesRecursively()
            .thenResult((Iterable<File> files) ->
            {
                Result<Void> result;
                final Iterable<File> javaSources = files.where((File file) -> Comparer.equal(file.getFileExtension(), ".java"));
                if (!javaSources.any())
                {
                    result = Result.error(new NotFoundException("No java source files found in " + folder + "."));
                }
                else
                {
                    javaSourceFiles.set(javaSources);
                    result = Result.success();
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