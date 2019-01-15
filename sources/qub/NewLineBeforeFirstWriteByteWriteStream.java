package qub;

public class NewLineBeforeFirstWriteByteWriteStream extends ByteWriteStreamDecorator
{
    private final Value<Boolean> hasWritten;

    public NewLineBeforeFirstWriteByteWriteStream(ByteWriteStream innerStream, Value<Boolean> hasWritten)
    {
        super(innerStream);

        PreCondition.assertNotNull(hasWritten, "hasWritten");

        this.hasWritten = hasWritten;
    }

    @Override
    public Result<Integer> writeBytes(byte[] bytes, int startIndex, int length)
    {
        if (!hasWritten.hasValue())
        {
            hasWritten.set(true);
            innerStream.asLineWriteStream().writeLine();
        }
        return innerStream.writeBytes(bytes, startIndex, length);
    }
}
