import exceptions.HMACNotValidException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class AESTest {
    public static void main(String[] args) {
        SecretKey key = AES.generateKey();
        CCMP alice = new CCMP(key);
        CCMP bob   = new CCMP(key);

        byte[] frameHeader = new byte[0];
        byte packageNumber = 0;
        byte[] data = "HelloWorldHello123".getBytes();
        // Hello1234 = 18 bytes
        ClearTextFrame ctFrame = new ClearTextFrame(frameHeader, packageNumber, data);

        alice.sendMessage(ctFrame);

    }
}
