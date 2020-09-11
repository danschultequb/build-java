package qub;

public interface QubBuildTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubBuild.class, () ->
        {
            runner.testGroup("main(String[])", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubBuild.main((String[])null),
                        new PreConditionFailure("args cannot be null."));
                });
            });

            runner.testGroup("run(QubProcess)", () ->
            {
                runner.test("with null process", (Test test) ->
                {
                    test.assertThrows(() -> QubBuild.run((QubProcess)null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with foo -?", (Test test) ->
                {
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                    try (final QubProcess process = QubProcess.create("foo", "-?"))
                    {
                        process.setOutputWriteStream(output);

                        QubBuild.run(process);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Unrecognized action: \"foo\"",
                            "",
                            "Usage: qub-build [--action=]<action-name> [--help]",
                            "  Used to compile source code projects.",
                            "  --action(a): The name of the action to invoke.",
                            "  --help(?):   Show the help message for this application.",
                            "",
                            "Actions:",
                            "  compile (default): Compile source code files.",
                            "  logs:              Show the logs folder."),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with -?", (Test test) ->
                {
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                    try (final QubProcess process = QubProcess.create("-?"))
                    {
                        process.setOutputWriteStream(output);

                        QubBuild.run(process);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Usage: qub-build [--action=]<action-name> [--help]",
                            "  Used to compile source code projects.",
                            "  --action(a): The name of the action to invoke.",
                            "  --help(?):   Show the help message for this application.",
                            "",
                            "Actions:",
                            "  compile (default): Compile source code files.",
                            "  logs:              Show the logs folder."),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with compile -?", (Test test) ->
                {
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                    try (final QubProcess process = QubProcess.create("compile", "-?"))
                    {
                        process.setOutputWriteStream(output);

                        QubBuild.run(process);
                    }

                    test.assertEqual(
                        Iterable.create(
                            "Usage: qub-build compile [[--folder=]<folder-path-to-build>] [--warnings=<show|error|hide>] [--buildjson] [--verbose] [--profiler] [--help]",
                            "  Compile source code files.",
                            "  --folder:     The folder to build. The current folder will be used if this isn't defined.",
                            "  --warnings:   How to handle build warnings. Can be either \"show\", \"error\", or \"hide\". Defaults to \"show\".",
                            "  --buildjson:  Whether or not to read and write a build.json file. Defaults to true.",
                            "  --verbose(v): Whether or not to show verbose logs.",
                            "  --profiler:   Whether or not this application should pause before it is run to allow a profiler to be attached.",
                            "  --help(?):    Show the help message for this application."),
                        Strings.getLines(output.getText().await()));
                });
            });
        });
    }
}
