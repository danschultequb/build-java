package qub;

public class NewLineBeforeFirstWriteByteWriteStreamTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(NewLineBeforeFirstWriteByteWriteStream.class, () ->
        {
            runner.testGroup("constructor()", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    test.assertThrows(() -> new NewLineBeforeFirstWriteByteWriteStream(null, Value.create()),
                        new PreConditionFailure("innerStream cannot be null."));
                });

                runner.test("with null hasWritten", (Test test) ->
                {
                    final InMemoryByteStream innerStream = new InMemoryByteStream();
                    test.assertThrows(() -> new NewLineBeforeFirstWriteByteWriteStream(innerStream, null),
                        new PreConditionFailure("hasWritten cannot be null."));
                });

                runner.test("with valid arguments", (Test test) ->
                {
                    final InMemoryByteStream innerStream = new InMemoryByteStream();
                    final Value<Boolean> hasWritten = Value.create();
                    final NewLineBeforeFirstWriteByteWriteStream decorator = new NewLineBeforeFirstWriteByteWriteStream(innerStream, hasWritten);
                    test.assertEqual(new byte[0], innerStream.getBytes());
                    test.assertFalse(innerStream.isDisposed());
                    test.assertFalse(decorator.isDisposed());
                    test.assertFalse(hasWritten.hasValue());
                });
            });

            runner.test("writeBytes()", (Test test) ->
            {
                final InMemoryByteStream innerStream = new InMemoryByteStream();
                final Value<Boolean> hasWritten = Value.create();
                final NewLineBeforeFirstWriteByteWriteStream decorator = new NewLineBeforeFirstWriteByteWriteStream(innerStream, hasWritten);

                decorator.writeByte((byte)7);
                test.assertTrue(hasWritten.hasValue());
                test.assertTrue(hasWritten.get());
                test.assertEqual(new byte[] { 10, 7 }, innerStream.getBytes());

                decorator.writeByte((byte)8);
                test.assertTrue(hasWritten.hasValue());
                test.assertTrue(hasWritten.get());
                test.assertEqual(new byte[] { 10, 7, 8 }, innerStream.getBytes());
            });
        });
    }
}
