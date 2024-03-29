package qub;

public interface QubBuildCompile
{
    static void addAction(CommandLineActions actions)
    {
        PreCondition.assertNotNull(actions, "actions");

        actions.addAction("compile", QubBuildCompile::getParameters, QubBuildCompile::run)
            .setDescription("Compile source code files.")
            .setDefaultAction();
    }

    static CommandLineParameter<Folder> addFolderToBuildParameter(CommandLineParameters parameters, DesktopProcess process)
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

        return parameters.addEnum("warnings", QubBuildCompileParameters.getWarningsDefault())
            .setValueName("<show|error|hide>")
            .setDescription("How to handle build warnings. Can be either \"show\", \"error\", or \"hide\". Defaults to \"show\".");
    }

    static CommandLineParameterBoolean addBuildJsonParameter(CommandLineParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        return parameters.addBoolean("buildjson", QubBuildCompileParameters.getBuildJsonDefault())
            .setDescription("Whether or not to read and write a build.json file. Defaults to true.");
    }

    static QubBuildCompileParameters getParameters(DesktopProcess process, CommandLineAction action)
    {
        PreCondition.assertNotNull(process, "process");
        PreCondition.assertNotNull(action, "action");

        final CommandLineParameters parameters = action.createCommandLineParameters(process);
        final CommandLineParameter<Folder> folderToBuildParameter = QubBuildCompile.addFolderToBuildParameter(parameters, process);
        final CommandLineParameter<Warnings> warningsParameter = QubBuildCompile.addWarningsParameter(parameters);
        final CommandLineParameterBoolean buildJsonParameter = QubBuildCompile.addBuildJsonParameter(parameters);
        final CommandLineParameterVerbose verboseParameter = parameters.addVerbose(process);
        final CommandLineParameterProfiler profiler = parameters.addProfiler(process, QubBuild.class);
        final CommandLineParameterHelp help = parameters.addHelp();

        QubBuildCompileParameters result = null;
        if (!help.showApplicationHelpLines(process).await())
        {
            profiler.await();

            final CharacterToByteWriteStream output = process.getOutputWriteStream();
            final Folder folderToBuild = folderToBuildParameter.getValue().await();
            final EnvironmentVariables environmentVariables = process.getEnvironmentVariables();
            final ProcessFactory processFactory = process.getProcessFactory();
            final Warnings warnings = warningsParameter.getValue().await();
            final Boolean buildJson = buildJsonParameter.getValue().await();
            final VerboseCharacterToByteWriteStream verbose = verboseParameter.getVerboseCharacterToByteWriteStream().await();
            final QubFolder qubFolder = process.getQubFolder().await();
            final Folder projectDataFolder = process.getQubProjectDataFolder().await();
            result = new QubBuildCompileParameters(output, folderToBuild, environmentVariables, processFactory, qubFolder, projectDataFolder)
                .setWarnings(warnings)
                .setBuildJson(buildJson)
                .setVerbose(verbose);
        }

        return result;
    }

    static int run(QubBuildCompileParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final Folder folderToBuild = parameters.getFolderToBuild();
        final EnvironmentVariables environmentVariables = parameters.getEnvironmentVariables();
        final Warnings warnings = parameters.getWarnings();
        final boolean useBuildJson = parameters.getBuildJson();
        final QubFolder qubFolder = parameters.getQubFolder();
        final Folder qubBuildDataFolder = parameters.getQubBuildDataFolder();

        int exitCode = 0;
        try (final LogStreams logStreams = CommandLineLogsAction.getLogStreamsFromDataFolder(qubBuildDataFolder, parameters.getOutputWriteStream(), parameters.getVerbose()))
        {
            final CharacterWriteStream output = logStreams.getOutput();
            final VerboseCharacterToByteWriteStream verbose = logStreams.getVerbose();

            try
            {
                final JavacProcessBuilder javac = JavacProcessBuilder.get(parameters.getProcessFactory()).await();

                final File projectJsonFile = folderToBuild.getFile("project.json").await();
                final FileSystem fileSystem = folderToBuild.getFileSystem();

                verbose.writeLine("Parsing " + projectJsonFile.relativeTo(folderToBuild).toString() + "...").await();
                final ProjectJSON projectJson = ProjectJSON.parse(projectJsonFile).await();

                final ProjectJSONJava projectJsonJava = projectJson.getJava();
                if (projectJsonJava == null)
                {
                    throw new NotFoundException("No language specified in project.json. Nothing to compile.");
                }

                final Folder outputsFolder = QubBuild.getJavaOutputsFolder(folderToBuild, projectJsonJava).await();
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
                    final Map<ProjectSignature, Iterable<ProjectSignature>> dependencyMap = projectJsonJava.getTransitiveDependencyPaths(qubFolder);
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
                                final InMemoryCharacterToByteStream errorMessage = InMemoryCharacterToByteStream.create();
                                final IndentedCharacterWriteStream indentedErrorMessage = IndentedCharacterWriteStream.create(errorMessage)
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
                                QubProjectVersionFolder versionFolder = projectFolder.getProjectVersionFolder(dependency.getVersion()).await();
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

                final Iterable<File> javaSourceFiles = QubBuild.iterateJavaSourceFiles(folderToBuild, projectJsonJava).toList();
                if (!javaSourceFiles.any())
                {
                    throw new NotFoundException("No java source files found in " + folderToBuild + ".");
                }

                final File buildJsonFile = outputsFolder.getFile("build.json").await();
                final List<File> newJavaSourceFiles = List.create();
                final List<File> deletedJavaSourceFiles = List.create();
                final List<File> modifiedJavaSourceFiles = List.create();
                final List<File> nonModifiedJavaSourceFiles = List.create();
                final List<File> javaSourceFilesWithErrors = List.create();
                final List<JavaCompilerIssue> nonModifiedJavaSourceFileWarnings = List.create();
                final List<BuildJSONSourceFile> buildJsonSourceFiles = List.create();
                boolean compileEverything;
                final BuildJSON updatedBuildJson = BuildJSON.create();
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
                    verbose.writeLine("Getting javac version...").await();

                    final JavacProcessBuilder javacVersionProcessBuilder = JavacProcessBuilder.get(parameters.getProcessFactory()).await();
                    final VersionNumber javacVersion = javacVersionProcessBuilder.getVersion(verbose).await();
                    updatedBuildJson.setJavacVersion(javacVersion);

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

                        if (buildJson == null || !javacVersion.equals(buildJson.getJavacVersion()))
                        {
                            compileEverything = true;
                            newJavaSourceFiles.addAll(javaSourceFiles);
                            buildJsonSourceFiles.addAll(BuildJSONSourceFile.create(javaSourceFiles, folderToBuild));
                        }
                        else
                        {
                            final ProjectJSON buildJsonProjectJson = buildJson.getProjectJson();
                            updateBuildJsonFile = !Comparer.equal(buildJsonProjectJson, projectJson);
                            compileEverything = QubBuildCompile.shouldCompileEverything(buildJson.getProjectJson(), projectJson);

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
                                    final Iterable<JavaCompilerIssue> sourceErrors = buildJsonSource.getIssues().where((JavaCompilerIssue issue) -> issue.type == Issue.Type.Error).toList();
                                    if (!Iterable.isNullOrEmpty(sourceErrors))
                                    {
                                        verbose.writeLine(javaSourceFile + " - Has errors").await();
                                        javaSourceFilesWithErrors.add(javaSourceFile);
                                    }

                                    final Iterable<JavaCompilerIssue> sourceWarnings = buildJsonSource.getIssues().where((JavaCompilerIssue issue) -> issue.type == Issue.Type.Warning).toList();
                                    if (!Iterable.isNullOrEmpty(sourceWarnings))
                                    {
                                        verbose.writeLine(javaSourceFile + " - Has warnings").await();
                                        nonModifiedJavaSourceFileWarnings.addAll(sourceWarnings);
                                    }

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
                                final Iterable<File> classFilesToDelete = QubBuildCompile.getExistingClassFiles(deletedSourceFile, folderToBuild, outputsFolder);
                                for (final File classFileToDelete : classFilesToDelete)
                                {
                                    classFileToDelete.delete().await();
                                }
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
                    QubBuildCompile.writeFileList(verbose, newJavaSourceFiles, "Added source files").await();
                    javaSourceFilesToCompile.addAll(newJavaSourceFiles);

                    QubBuildCompile.writeFileList(verbose, modifiedJavaSourceFiles, "Modified source files").await();
                    javaSourceFilesToCompile.addAll(modifiedJavaSourceFiles);

                    QubBuildCompile.writeFileList(verbose, javaSourceFilesWithErrors, "Source files that previously contained errors").await();
                    javaSourceFilesToCompile.addAll(javaSourceFilesWithErrors);

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
                        final File classFile = QubBuildCompile.getClassFile(nonModifiedJavaSourceFile, folderToBuild, outputsFolder);
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

                    if (nonModifiedJavaSourceFileWarnings.any())
                    {
                        nonModifiedJavaSourceFileWarnings.sort((JavaCompilerIssue lhs, JavaCompilerIssue rhs) -> lhs.sourceFilePath.compareTo(rhs.sourceFilePath) < 0);

                        final int unmodifiedWarningCount = nonModifiedJavaSourceFileWarnings.getCount();
                        output.writeLine(unmodifiedWarningCount + " Unmodified Warning" + (unmodifiedWarningCount == 1 ? "" : "s") + ":").await();
                        for (final JavaCompilerIssue warning : nonModifiedJavaSourceFileWarnings)
                        {
                            output.writeLine(warning.sourceFilePath + " (Line " + warning.lineNumber + "): " + warning.message).await();
                            final BuildJSONSourceFile sourceFile = updatedBuildJson.getSourceFile(Path.parse(warning.sourceFilePath)).await();
                            sourceFile.addIssue(warning);
                        }
                    }
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

                    final List<JavaCompilerIssue> nonModifiedNonCompiledJavaSourceFileWarnings = nonModifiedJavaSourceFileWarnings
                        .where((JavaCompilerIssue warning) -> !javaSourceFilesToCompile.contains((File fileToCompile) -> fileToCompile.relativeTo(folderToBuild).equals(Path.parse(warning.sourceFilePath))))
                        .toList();
                    if (nonModifiedNonCompiledJavaSourceFileWarnings.any())
                    {
                        nonModifiedNonCompiledJavaSourceFileWarnings.sort((JavaCompilerIssue lhs, JavaCompilerIssue rhs) -> lhs.sourceFilePath.compareTo(rhs.sourceFilePath) < 0);

                        final int unmodifiedWarningCount = nonModifiedNonCompiledJavaSourceFileWarnings.getCount();
                        output.writeLine(unmodifiedWarningCount + " Unmodified Warning" + (unmodifiedWarningCount == 1 ? "" : "s") + ":").await();
                        for (final JavaCompilerIssue warning : nonModifiedNonCompiledJavaSourceFileWarnings)
                        {
                            output.writeLine(warning.sourceFilePath + " (Line " + warning.lineNumber + "): " + warning.message).await();
                            final BuildJSONSourceFile sourceFile = updatedBuildJson.getSourceFile(Path.parse(warning.sourceFilePath)).await();
                            sourceFile.addIssue(warning);
                        }
                    }

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
                    try (final CharacterWriteStream writeStream = CharacterWriteStream.create(ByteWriteStream.buffer(buildJsonFile.getContentsByteWriteStream().await())))
                    {
                        writeStream.write(updatedBuildJson.toString(JSONFormat.pretty)).await();
                    }
                    verbose.writeLine("Done writing build.json file.").await();
                }
            }
            catch (Throwable error)
            {
                final Throwable unwrappedError = Exceptions.unwrap(error);
                if (Types.instanceOf(unwrappedError, Iterable.create(PreConditionFailure.class, PostConditionFailure.class, NullPointerException.class)))
                {
                    throw error;
                }
                final String message = unwrappedError.getMessage();
                output.writeLine("ERROR: " + message).await();
                ++exitCode;
            }
        }

        return exitCode;
    }

    static Result<Void> writeFileList(CharacterWriteStream verbose, Iterable<File> files, String description)
    {
        return Result.create(() ->
        {
            if (!Iterable.isNullOrEmpty(files))
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
     * Get the class file that will be created when the provided source file is compiled. The returned class file may
     * or may not already exist.
     * @param sourceFile The source file that will create the returned class file when it is compiled.
     * @param rootFolder The root project folder that contains the source folder and the output folder.
     * @param outputFolder The output folder where compiled class files will be created in.
     * @return The class file that will be created when the provided source file is compiled.
     */
    static File getClassFile(File sourceFile, Folder rootFolder, Folder outputFolder)
    {
        final Path sourceFileRelativeToRootPath = sourceFile.relativeTo(rootFolder);
        final String sourceFileRelativePathFirstSegment = sourceFileRelativeToRootPath.getSegments().first();
        final Folder sourceFolder = rootFolder.getFolder(sourceFileRelativePathFirstSegment).await();
        final Path sourceFileRelativeToSourcePath = sourceFile.relativeTo(sourceFolder);
        return outputFolder.getFile(sourceFileRelativeToSourcePath.changeFileExtension(".class")).await();
    }

    /**
     * Find the existing class files that are related to the provided source file.
     * @param sourceFile The source file that was compiled into the resulting class files.
     * @param rootFolder The root project folder that contains the source folder and the output folder.
     * @param outputFolder The output folder where compiled class files will be created in.
     * @return The existing class files that were created from the provided source file.
     */
    static Iterable<File> getExistingClassFiles(File sourceFile, Folder rootFolder, Folder outputFolder)
    {
        final File classFile = QubBuildCompile.getClassFile(sourceFile, rootFolder, outputFolder);
        final List<File> result = classFile.getParentFolder().await()
            .iterateFiles()
            .where((File file) -> file.getName().startsWith(classFile.getNameWithoutFileExtension() + "$"))
            .toList();
        if (classFile.exists().await())
        {
            result.add(classFile);
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
