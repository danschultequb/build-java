package qub;

public interface QubBuild
{
    static void main(String[] args)
    {
        Process.run(args, QubBuild::getParameters, QubBuild::run);
    }

    static void main(Process process)
    {
        PreCondition.assertNotNull(process, "process");

        final QubBuildParameters parameters = QubBuild.getParameters(process);
        if (parameters != null)
        {
            process.showDuration(() ->
            {
                process.setExitCode(QubBuild.run(parameters));
            });
        }
    }

    static CommandLineParameter<Folder> addFolderToBuildParameter(CommandLineParameters parameters, Process process)
    {
        PreCondition.assertNotNull(parameters, "parameters");
        PreCondition.assertNotNull(process, "process");

        return parameters.addPositionalFolder("folder", process)
            .setValueName("<folder-path-to-build>")
            .setDescription("The folder to build. The current folder will be used if this isn't defined.");
    }

    static CommandLineParameter<Warnings> addWarningsParameter(CommandLineParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        return parameters.addEnum("warnings", QubBuildParameters.getWarningsDefault())
            .setValueName("<show|error|hide>")
            .setDescription("How to handle build warnings. Can be either \"show\", \"error\", or \"hide\". Defaults to \"show\".");
    }

    static CommandLineParameterBoolean addBuildJsonParameter(CommandLineParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        return parameters.addBoolean("buildjson", QubBuildParameters.getBuildJsonDefault())
            .setDescription("Whether or not to read and write a build.json file. Defaults to true.");
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
        final CommandLineParameter<Folder> folderToBuildParameter = QubBuild.addFolderToBuildParameter(parameters, process);
        final CommandLineParameter<Warnings> warningsParameter = QubBuild.addWarningsParameter(parameters);
        final CommandLineParameterBoolean buildJsonParameter = QubBuild.addBuildJsonParameter(parameters);
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
            final Warnings warnings = warningsParameter.getValue().await();
            final Boolean buildJson = buildJsonParameter.getValue().await();
            final VerboseCharacterWriteStream verbose = verboseParameter.getVerboseCharacterWriteStream().await();
            result = new QubBuildParameters(output, folderToBuild, environmentVariables, processFactory)
                .setWarnings(warnings)
                .setBuildJson(buildJson)
                .setVerbose(verbose);
        }

        return result;
    }

    static int run(QubBuildParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final CharacterWriteStream output = parameters.getOutputCharacterWriteStream();
        final Folder folderToBuild = parameters.getFolderToBuild();
        final EnvironmentVariables environmentVariables = parameters.getEnvironmentVariables();
        final Warnings warnings = parameters.getWarnings();
        final boolean useBuildJson = parameters.getBuildJson();
        final VerboseCharacterWriteStream verbose = parameters.getVerbose();

        int exitCode = 0;
        try
        {
            final JavacProcessBuilder javac = JavacProcessBuilder.get(parameters.getProcessFactory()).await();

            final String qubHome = environmentVariables.get("QUB_HOME")
                .convertError(() -> new NotFoundException("A QUB_HOME folder path environment variable must be specified."))
                .await();

            final File projectJsonFile = folderToBuild.getFile("project.json").await();
            final FileSystem fileSystem = folderToBuild.getFileSystem();

            verbose.writeLine("Parsing " + projectJsonFile.relativeTo(folderToBuild).toString() + "...").await();
            final ProjectJSON projectJson = ProjectJSON.parse(projectJsonFile).await();
            final ProjectJSONJava projectJsonJava = projectJson.getJava();
            if (projectJsonJava == null)
            {
                throw new NotFoundException("No language specified in project.json. Nothing to compile.");
            }

            String outputFolderName = projectJsonJava.getOutputFolder();
            if (Strings.isNullOrEmpty(outputFolderName))
            {
                outputFolderName = "outputs";
            }
            final Folder outputsFolder = folderToBuild.getFolder(outputFolderName).await();
            javac.addOutputFolder(outputsFolder);
            javac.addXlintUnchecked();
            javac.addXlintDeprecation();

            final String javaVersion = projectJsonJava.getVersion();
            if (!Strings.isNullOrEmpty(javaVersion))
            {
                final String javaHomeFolderPathString = environmentVariables.get("JAVA_HOME")
                    .catchError(NotFoundException.class)
                    .await();
                if (Strings.isNullOrEmpty(javaHomeFolderPathString))
                {
                    throw new NotFoundException("Can't compile for a specific Java version if the JAVA_HOME environment variable is not specified.");
                }
                final Folder javaHomeFolder = fileSystem.getFolder(javaHomeFolderPathString).await();
                final File bootClasspath = JavacProcessBuilder.findBootClasspathFromJavaHomeFolder(javaVersion, javaHomeFolder).await();
                if (bootClasspath != null)
                {
                    javac.addJavaSourceVersion(javaVersion);
                    javac.addJavaTargetVersion(javaVersion);
                    javac.addBootClasspath(bootClasspath);
                }
            }

            final Integer maximumErrors = projectJsonJava.getMaximumErrors();
            if (maximumErrors != null)
            {
                javac.addMaximumErrors(maximumErrors);
            }

            final Integer maximumWarnings = projectJsonJava.getMaximumWarnings();
            if (maximumWarnings != null)
            {
                javac.addMaximumWarnings(maximumWarnings);
            }

            final List<String> classPaths = List.create();
            classPaths.add(outputsFolder.toString());
            Iterable<ProjectSignature> dependencies = projectJsonJava.getDependencies();
            if (!Iterable.isNullOrEmpty(dependencies))
            {
                final QubFolder qubFolder = QubFolder.create(fileSystem.getFolder(qubHome).await());

                final Map<ProjectSignature,Iterable<ProjectSignature>> dependencyMap = projectJsonJava.getTransitiveDependencyPaths(qubFolder);
                dependencies = dependencyMap.getKeys();

                final Set<ProjectSignature> errorDependencies = Set.create();
                for (final ProjectSignature dependency : dependencies)
                {
                    if (!errorDependencies.contains(dependency))
                    {
                        final Iterable<ProjectSignature> matchingDependencies = dependencies.where(dependency::equalsIgnoreVersion).toList();
                        if (matchingDependencies.getCount() > 1)
                        {
                            errorDependencies.addAll(matchingDependencies);
                            final InMemoryCharacterStream errorMessage = new InMemoryCharacterStream();
                            final IndentedCharacterWriteStream indentedErrorMessage = new IndentedCharacterWriteStream(errorMessage)
                                .setSingleIndent(" ");
                            indentedErrorMessage.writeLine("Found more than one required version for package " + dependency.toStringIgnoreVersion() + ":").await();
                            int number = 0;
                            for (final ProjectSignature matchingProjectSignature : matchingDependencies)
                            {
                                ++number;
                                final String numberString = number + ". ";
                                indentedErrorMessage.setCurrentIndent("");
                                errorMessage.writeLine(numberString + matchingProjectSignature).await();
                                indentedErrorMessage.setCurrentIndent(Strings.repeat(' ', numberString.length()));
                                final Iterable<ProjectSignature> path = dependencyMap.get(matchingProjectSignature).await();
                                for (final ProjectSignature pathProjectSignature : path)
                                {
                                    indentedErrorMessage.increaseIndent();
                                    indentedErrorMessage.writeLine("from " + pathProjectSignature).await();
                                }
                            }
                            throw new RuntimeException(errorMessage.getText().await());
                        }
                    }
                }

                for (final ProjectSignature dependency : dependencies)
                {
                    final QubPublisherFolder publisherFolder = qubFolder.getPublisherFolder(dependency.getPublisher()).await();
                    if (!publisherFolder.exists().await())
                    {
                        throw new NotFoundException("No publisher folder named " + Strings.escapeAndQuote(dependency.getPublisher()) + " found in the Qub folder (" + qubFolder + ").");
                    }
                    else
                    {
                        final QubProjectFolder projectFolder = publisherFolder.getProjectFolder(dependency.getProject()).await();
                        if (!projectFolder.exists().await())
                        {
                            throw new NotFoundException("No project folder named " + Strings.escapeAndQuote(dependency.getProject()) + " found in the " + Strings.escapeAndQuote(dependency.getPublisher()) + " publisher folder (" + publisherFolder + ").");
                        }
                        else
                        {
                            final QubProjectVersionFolder versionFolder = projectFolder.getProjectVersionFolder(dependency.getVersion()).await();
                            if (!versionFolder.exists().await())
                            {
                                throw new NotFoundException("No version folder named " + Strings.escapeAndQuote(dependency.getVersion()) + " found in the " + Strings.escapeAndQuote(dependency.getProject()) + " project folder (" + projectFolder + ").");
                            }
                            else
                            {
                                final File dependencyFile = versionFolder.getCompiledSourcesFile().await();
                                if (!dependencyFile.exists().await())
                                {
                                    throw new NotFoundException("No dependency file named " + Strings.escapeAndQuote(dependencyFile.getName()) + " found in the " + Strings.escapeAndQuote(dependency.getVersion()) + " version folder (" + versionFolder + ").");
                                }
                                else
                                {
                                    classPaths.add(dependencyFile.toString());
                                }
                            }
                        }
                    }
                }
            }
            javac.addClasspath(classPaths);

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

            final Iterable<File> javaSourceFiles = getJavaSourceFiles(folderToBuild, sourceFileMatcher).await();
            final File buildJsonFile = outputsFolder.getFile("build.json").await();
            final List<File> newJavaSourceFiles = List.create();
            final List<File> deletedJavaSourceFiles = List.create();
            final List<File> modifiedJavaSourceFiles = List.create();
            final List<File> nonModifiedJavaSourceFiles = List.create();
            final List<File> issuedJavaSourceFiles = List.create();
            final List<BuildJSONSourceFile> buildJsonSourceFiles = List.create();
            boolean compileEverything;
            final BuildJSON updatedBuildJson = new BuildJSON();
            boolean updateBuildJsonFile = false;
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
                        final ProjectJSON buildJsonProjectJson = buildJson.getProjectJson();
                        updateBuildJsonFile = !Comparer.equal(buildJsonProjectJson, projectJson);
                        compileEverything = shouldCompileEverything(buildJson.getProjectJson(), projectJson);

                        for (final File javaSourceFile : javaSourceFiles)
                        {
                            final Path javaSourceFileRelativePath = javaSourceFile.relativeTo(folderToBuild);

                            final BuildJSONSourceFile buildJsonSource = buildJson.getSourceFile(javaSourceFileRelativePath)
                                .catchError(NotFoundException.class)
                                .await();
                            if (buildJsonSource == null || buildJsonSource.getLastModified() == null)
                            {
                                verbose.writeLine(javaSourceFile + " - New file").await();
                                newJavaSourceFiles.add(javaSourceFile);
                                buildJsonSourceFiles.add(BuildJSONSourceFile.create(javaSourceFile, folderToBuild, javaSourceFiles));
                            }
                            else if (!javaSourceFile.getLastModified().await().equals(buildJsonSource.getLastModified()))
                            {
                                verbose.writeLine(javaSourceFile + " - Last modified: " + javaSourceFile.getLastModified().await()).await();
                                verbose.writeLine(Strings.repeat(' ', javaSourceFile.toString().length()) + " - Last built:    " + buildJsonSource.getLastModified()).await();

                                modifiedJavaSourceFiles.add(javaSourceFile);
                                buildJsonSourceFiles.add(BuildJSONSourceFile.create(javaSourceFile, folderToBuild, javaSourceFiles));
                            }
                            else if (!Iterable.isNullOrEmpty(buildJsonSource.getIssues()))
                            {
                                verbose.writeLine(javaSourceFile + " - Has issues").await();

                                issuedJavaSourceFiles.add(javaSourceFile);
                                buildJsonSourceFiles.add(BuildJSONSourceFile.create(javaSourceFile, folderToBuild, javaSourceFiles));
                            }
                            else
                            {
                                verbose.writeLine(javaSourceFile + " - No changes or issues").await();

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
                    final BuildJSONSourceFile sourceFile = updatedBuildJson.getSourceFile(relativePath).await();
                    final Iterable<Path> sourceFileProjectSignaturePaths = sourceFile.getDependencies();
                    if (!Iterable.isNullOrEmpty(sourceFileProjectSignaturePaths))
                    {
                        final Iterable<File> sourceFileProjectSignatureFiles = sourceFileProjectSignaturePaths
                            .map((Path sourceFileProjectSignaturePath) -> folderToBuild.getFile(sourceFileProjectSignaturePath).await());
                        for (final File sourceFileProjectSignatureFile : sourceFileProjectSignatureFiles)
                        {
                            if (deletedJavaSourceFiles.contains(sourceFileProjectSignatureFile))
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
                while (filesToNotCompileChanged && filesToNotCompile.any())
                {
                    filesToNotCompileChanged = false;
                    for (final File fileToNotCompile : List.create(filesToNotCompile))
                    {
                        final Path relativePath = fileToNotCompile.relativeTo(folderToBuild);
                        final BuildJSONSourceFile sourceFile = updatedBuildJson.getSourceFile(relativePath).await();
                        final Iterable<Path> sourceFileProjectSignaturePaths = sourceFile.getDependencies();
                        if (!Iterable.isNullOrEmpty(sourceFileProjectSignaturePaths))
                        {
                            final Iterable<File> sourceFileProjectSignatureFiles = sourceFileProjectSignaturePaths
                                .map((Path sourceFileProjectSignaturePath) -> folderToBuild.getFile(sourceFileProjectSignaturePath).await());
                            for (final File dependencyFile : sourceFileProjectSignatureFiles)
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

            if (!javaSourceFilesToCompile.any())
            {
                output.writeLine("No files need to be compiled.").await();
            }
            else
            {
                updateBuildJsonFile = true;

                javac.addSourceFilePaths(javaSourceFilesToCompile
                    .map((File javaSourceFile) -> javaSourceFile.getPath().relativeTo(folderToBuild)));

                final int filesToCompileCount = javaSourceFilesToCompile.getCount();
                output.writeLine("Compiling " + filesToCompileCount + " file" + (filesToCompileCount == 1 ? "" : "s") + "...").await();
                final JavaCompilationResult compilationResult = javac.compile(warnings, verbose).await();
                exitCode = compilationResult.exitCode;

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

            if (useBuildJson && updateBuildJsonFile)
            {
                verbose.writeLine("Writing build.json file...").await();
                try (final CharacterWriteStream writeStream = new BufferedByteWriteStream(buildJsonFile.getContentByteWriteStream().await()).asCharacterWriteStream())
                {
                    writeStream.write(updatedBuildJson.toString(JSONFormat.pretty)).await();
                }
                verbose.writeLine("Done writing build.json file.").await();
            }
        }
        catch (Throwable error)
        {
            final Throwable unwrappedError = Exceptions.unwrap(error);
            if (unwrappedError instanceof PreConditionFailure ||
                unwrappedError instanceof PostConditionFailure ||
                unwrappedError instanceof NullPointerException)
            {
                throw error;
            }
            final String message = unwrappedError.getMessage();
            output.writeLine("ERROR: " + message).await();
            ++exitCode;
        }

        return exitCode;
    }

    static Result<Void> writeFileList(VerboseCharacterWriteStream verbose, Iterable<File> files, String description)
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

            final String oldProjectJsonJavaVersion = oldProjectJsonJava.getVersion();
            final String newProjectJsonJavaVersion = newProjectJsonJava.getVersion();
            if (!Comparer.equal(oldProjectJsonJavaVersion, newProjectJsonJavaVersion))
            {
                result = !isJava8(oldProjectJsonJavaVersion) || !isJava8(newProjectJsonJavaVersion);
            }

            if (!result)
            {
                final Iterable<ProjectSignature> oldProjectJsonJavaDependencies = oldProjectJsonJava.getDependencies();
                final Iterable<ProjectSignature> newProjectJsonJavaDependencies = newProjectJsonJava.getDependencies();
                if (!Iterable.isNullOrEmpty(oldProjectJsonJavaDependencies))
                {
                    result = Iterable.isNullOrEmpty(newProjectJsonJavaDependencies) ||
                        oldProjectJsonJavaDependencies.contains((ProjectSignature oldProjectSignature) ->
                            !newProjectJsonJavaDependencies.contains(oldProjectSignature));
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
}