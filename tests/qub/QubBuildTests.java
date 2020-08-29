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
                            "  --help(?): Show the help message for this application.",
                            "",
                            "Actions:",
                            "  compile (default): Compile source code files.",
                            "  logs: Show the logs folder."),
                        Strings.getLines(output.getText().await()));
                });
            });
        });
    }
}
