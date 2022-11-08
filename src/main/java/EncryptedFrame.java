public class EncryptedFrame {

    public final byte[] frameHeader;
    public final byte packageNumber;
    public final byte[] data;
    public final byte[] MIC;

    public EncryptedFrame(byte[] frameHeader, byte packageNumber, byte[] data, byte[] MIC) {
        this.frameHeader = frameHeader;
        this.packageNumber = packageNumber;
        this.data = data;
        this.MIC = MIC;
    }
}
