import javax.crypto.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {
    public static SecretKey generateKey()  {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static byte[] generateIV()
    {
        return new byte[] { 0x66, 0x14, 0x4F, 0x01, 0x2C, 0x10, 0x51, 0x43, 0x55, 0x29, 0x15, 0x3A, 0x66, 0x68, 0x0B, 0x05 };

//        SecureRandom random = null;
//        byte[] iv = new byte[0];
//        try {
//            random = SecureRandom.getInstanceStrong();
//            iv = new byte[Cipher.getInstance("AES/ECB/NoPadding").getBlockSize()];
//            random.nextBytes(iv);
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
//            e.printStackTrace();
//        }
//
//        return iv;
    }

    public static byte[] encrypt(byte[] toEncrypt, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return cipher.doFinal(toEncrypt);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e);
            return null;
        }
    }

    public static byte[] decrypt(byte[] toDecrypt, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            return cipher.doFinal(toDecrypt);
        }
        catch(Exception e) {
            System.out.println("Error while decrypting: " + e);
            return null;
        }
    }

}
