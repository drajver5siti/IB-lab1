import exceptions.InvalidMIC;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class CCMP {

    private SecretKey key;

    private byte[] originalIV;

    public CCMP(SecretKey key) {

        this.key = key;
        this.originalIV = AES.generateIV();
    }

    private void printBytes(byte[] toPrint) {
            StringBuilder sb = new StringBuilder();
        for (byte b : toPrint) {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());
    }

    private void printBinary(byte[] toPrint) {
        for (byte b : toPrint) {
            System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1) + " ");
        }
        System.out.println();
    }

    private byte[] XOR(byte[] first, byte[] second) {
        byte[] result = new byte[first.length];
        for(int i = 0; i < result.length; i++) {
            result[i] = (byte) (first[i] ^ second[i]);
        }

        return result;
    }

    private byte[] trimTrailingZeroes(byte[] toTrim) {
        int trimToIndex = 0;
        for(int i = 0; i < toTrim.length; i++) {
            if(toTrim[i] == 0) break;
            trimToIndex++;
        }

        return Arrays.copyOfRange(toTrim, 0, trimToIndex);
    }

    private byte[] MICEncode(byte[] data, byte[] initialIV) {
        int blocks = data.length / 16;
        boolean hasBlockToPad = (data.length % 16) > 0;

        int from = 0;
        int to = 1;

        byte[] dataBlock;
        byte[] IV = AES.encrypt(initialIV, this.key);

        for(int i = 0; i <= blocks; i++) {
            if (i == blocks) {
                if(hasBlockToPad) {
                    // last block to pad
                    int lastBlockSize = data.length % 16;
                    byte[] blockWithoutPad = Arrays.copyOfRange(data, from * 16, ((to - 1) * 16 ) + lastBlockSize);
                    int bytesToPad = 16 - lastBlockSize;
                    byte[] padding = new byte[bytesToPad];
                    for (int j = 0; j < bytesToPad; j++) {
                        padding[j] = 0;
                    }
                    dataBlock = new byte[16];
                    System.arraycopy(blockWithoutPad, 0, dataBlock, 0, blockWithoutPad.length);
                    System.arraycopy(padding, 0, dataBlock, blockWithoutPad.length, padding.length);
                } else {
                    continue;
                }
            } else {
                dataBlock = Arrays.copyOfRange(data, from * 16, to * 16);
            }

            IV = this.XOR(IV, dataBlock);
            IV = AES.encrypt(IV, this.key);

            from++;
            to++;
        }

        return IV;
    }

    private byte[] calculateMIC(ClearTextFrame frame) {
        //
        byte[] IV = MICEncode(frame.frameHeader, this.originalIV);
        IV = MICEncode(frame.data, IV);

        // This is the MIC
        return Arrays.copyOfRange(IV, 0, 8);
    }

    private byte[] encrypt(byte[] toEncrypt) {
        int blocks = toEncrypt.length / 16;
        boolean hasBlockToPad = (toEncrypt.length % 16) > 0;
        BigInteger counter = BigInteger.valueOf(0L);
        int from = 0;
        int to = 1;

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        byte[] dataBlock;
        byte[] IV = this.originalIV;
        for (int i = 0; i <= blocks; i++) {

            if (i == blocks) {
                if(hasBlockToPad) {
                    // last block to pad
                    int lastBlockSize = toEncrypt.length % 16;
                    byte[] blockWithoutPad = Arrays.copyOfRange(toEncrypt, from * 16, ((to - 1) * 16 ) + lastBlockSize);
                    int bytesToPad = 16 - lastBlockSize;
                    byte[] padding = new byte[bytesToPad];
                    for (int j = 0; j < bytesToPad; j++) {
                        padding[j] = 0;
                    }
                    dataBlock = new byte[16];
                    System.arraycopy(blockWithoutPad, 0, dataBlock, 0, blockWithoutPad.length);
                    System.arraycopy(padding, 0, dataBlock, blockWithoutPad.length, padding.length);
                } else {
                    continue;
                }
            } else {
                dataBlock = Arrays.copyOfRange(toEncrypt, from * 16, to * 16);
            }

            byte[] XORed = this.XOR(IV, dataBlock);
            byte[] encrypted = AES.encrypt(XORed, this.key);


            // IV is now the XORed array to use in the next iteration
            IV = XORed;

            from++;
            to++;

            try {
                result.write(encrypted);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result.toByteArray();
    }

    private byte[] decrypt(byte[] toDecrypt) {
        int blocks = toDecrypt.length / 16;
        int from = 0;
        int to = 1;

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        byte[] dataBlock;
        byte[] IV = this.originalIV;
        for (int i = 0; i < blocks; i++) {

            dataBlock = Arrays.copyOfRange(toDecrypt, from * 16, to * 16);

            byte[] decrypted = AES.decrypt(
                    dataBlock,
                    this.key
            );

            byte[] decryptedFinal = this.XOR(IV, decrypted);
            IV = decrypted;
            from++;
            to++;

            try {
                if(i == blocks - 1) {
                    // last block, trim zeroes if any
                    result.write(this.trimTrailingZeroes(decryptedFinal));
                } else {
                    result.write(decryptedFinal);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result.toByteArray();
    }

    public EncryptedFrame sendMessage(ClearTextFrame toSend) {

        System.out.println("Sending Message...");

        byte[] encryptedData = this.encrypt(toSend.data);
        byte[] MIC = this.calculateMIC(toSend);

        System.out.println("Original Data: ");
        printBytes(toSend.data);
        System.out.println("Encrypted Data: ");
        printBytes(encryptedData);
        System.out.println("MIC: ");
        printBytes(MIC);
        System.out.println();

        return new EncryptedFrame(
                toSend.frameHeader,
                toSend.packageNumber,
                this.encrypt(toSend.data),
                this.calculateMIC(toSend)
        );
    }

    public ClearTextFrame receiveMessage(EncryptedFrame toReceive) throws InvalidMIC {

        System.out.println("Receiving message...");

        ClearTextFrame decrypted = new ClearTextFrame(
            toReceive.frameHeader,
            toReceive.packageNumber,
            this.decrypt(toReceive.data)
        );

        byte[] MIC = this.calculateMIC(decrypted);

        System.out.println("Encrypted Data: ");
        printBytes(toReceive.data);
        System.out.println("Decrypted Data: ");
        printBytes(decrypted.data);
        System.out.println("MIC: ");
        printBytes(MIC);
        System.out.println();


        if(!Arrays.equals(toReceive.MIC, this.calculateMIC(decrypted))) {
            throw new InvalidMIC();
        }

        System.out.println("Message is OK !");
        System.out.println();

        return decrypted;
    }

}
