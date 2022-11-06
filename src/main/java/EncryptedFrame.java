public class EncryptedFrame {

    private final byte[] data;
    private final byte[] HMAC;

    public EncryptedFrame(byte[] data, byte[] HMAC) {
        this.data = data;
        this.HMAC = HMAC;
    }

    public byte[] getData()
    {
        return this.data;
    }

    public byte[] getHMAC()
    {
        return this.HMAC;
    }
}
