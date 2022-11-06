public class ClearTextFrame {

    private final byte[] data;

    public ClearTextFrame(byte[] data) {
        this.data = data;
    }

    public byte[] getData()
    {
        return this.data;
    }
}
