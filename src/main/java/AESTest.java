import exceptions.HMACNotValidException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class AESTest {
    public static void main(String[] args) {
        SecretKey key = AES.generateKey();
        String messageToSend = "Hello World!";

        ClearTextFrame frameToEncrypt = new ClearTextFrame(
                messageToSend.getBytes(StandardCharsets.UTF_8)
        );

        System.out.println("Original string: " + messageToSend);
        EncryptedFrame encryptedFrame = AES.encryptMessage(frameToEncrypt, key);

        StringBuilder encryptedString = new StringBuilder();
        for(byte b : encryptedFrame.getData()) {
            encryptedString.append(String.format("%02X ", b));
        }
        System.out.println("Encrypted string: " + encryptedString.toString());

        try {
            ClearTextFrame decryptedFrame = AES.decryptMessage(encryptedFrame, key);
            System.out.println("Decrypted string: " + new String(decryptedFrame.getData()));
        } catch (HMACNotValidException e) {
            System.out.println("Invalid HMAC !");
        }
    }
}
