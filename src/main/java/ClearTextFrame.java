public class ClearTextFrame {

    public final byte[] frameHeader;
    public final byte packageNumber;
    public final byte[] data;

    public ClearTextFrame(byte[] frameHeader, byte packageNumber, byte[] data) {
        this.frameHeader = frameHeader;
        this.packageNumber = packageNumber;
        this.data = data;
    }
}
