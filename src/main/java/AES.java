import exceptions.HMACNotValidException;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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
//
//    private static byte[] calculateHMAC(byte[] encryptedData, SecretKey key) {
//        try {
//            Mac mac = Mac.getInstance("HmacSHA256");
//            mac.init(key);
//            return mac.doFinal(encryptedData);
//        } catch (Exception e) {
//            System.out.println("Error while calculating MAC: " + e);
//            return null;
//        }
//    }
//
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
//
//    public static EncryptedFrame encryptMessage(ClearTextFrame toEncrypt, SecretKey key)
//    {
//        byte[] encrypted = AES.encrypt(toEncrypt.getData(), key);
//        byte[] HMAC = AES.calculateHMAC(encrypted, key);
//
//        return new EncryptedFrame(
//                encrypted,
//                HMAC
//        );
//    }
//
//    public static ClearTextFrame decryptMessage(EncryptedFrame toDecrypt, SecretKey key) throws HMACNotValidException {
//        byte[] HMAC = AES.calculateHMAC(toDecrypt.getData(), key);
////        byte[] wrongHMAC = AES.calculateHMAC("Test".getBytes(StandardCharsets.UTF_8), key);
//        if(!Arrays.equals(HMAC, toDecrypt.getHMAC())) {
//            throw new HMACNotValidException();
//        }
//
//        return new ClearTextFrame(
//                AES.decrypt(toDecrypt.getData(), key)
//        );
//    }

}
