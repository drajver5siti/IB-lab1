import exceptions.InvalidMIC;

import javax.crypto.SecretKey;

public class CCMPTest {
    public static void main(String[] args) {
        SecretKey key = AES.generateKey();
        CCMP alice = new CCMP(key);
        CCMP bob   = new CCMP(key);

        byte[] frameHeader = "TestHeader".getBytes();
        byte packageNumber = 0;
        byte[] data = "HelloWorldHello1244".getBytes();

        ClearTextFrame toSend = new ClearTextFrame(frameHeader, packageNumber, data);
        EncryptedFrame toReceive = alice.sendMessage(toSend);


        try {
            ClearTextFrame receivedMessage = bob.receiveMessage(toReceive);
        } catch (InvalidMIC e) {
            System.out.println("Invalid Message !");
            e.printStackTrace();
        }

    }
}
