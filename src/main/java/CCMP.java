import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class CCMP {

    private SecretKey key;

    public CCMP(SecretKey key) {
        this.key = key;
    }

    private void printBytes(byte[] toPrint) {
            StringBuilder sb = new StringBuilder();
        for (byte b : toPrint) {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());
    }

    private void MICCalc()
    {
        // FH se deli na po 16Byte, for za site blokovi -> AES.encrypt(IV, data, key)
        // IV = izlez od prviot block
        // poslednoto IV od FH ke vlez vo sifriranje na data, poslednoto IV od data e potpisot
        // od celiot potpis se oteskuvaat 8 levi byte i toa e ustvari rezultot (spored sema)

        // Ova e samo za MICcalc, za encrypt na data e istoto samo IV ke e nekoj counter koj ke se
        // ++ za sekoja iteracija
        // kaj encrypt data sekoj rezultat od encrypt se pravi append
    }

    private void encrypt(byte[] toEncrypt) {
        int blocks = toEncrypt.length / 16;
        boolean hasBlockToPad = (toEncrypt.length % 16) > 0;
        BigInteger counter = new BigInteger("1");
        int from = 0;
        int to = 1;

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        byte[] blockToEncrypt = new byte[0];
        for (int i = 0; i <= blocks; i++) {
            blockToEncrypt = null;

            if (i == blocks) {
                if(hasBlockToPad) {
                    // last block to pad
                    int lastBlockSize = toEncrypt.length % 16;
                    byte[] blockWithoutPad = Arrays.copyOfRange(toEncrypt, from * 16, ((to - 1) * 16 ) + lastBlockSize);
                    int bytesToPad = 16 - lastBlockSize;
                    byte[] padding = new byte[bytesToPad];
                    for (int j = 0; j < bytesToPad; j++) {
                        padding[j] = new Integer(bytesToPad).byteValue();
                    }
                    blockToEncrypt = new byte[16];
                    System.arraycopy(blockWithoutPad, 0, blockToEncrypt, 0, blockWithoutPad.length);
                    System.arraycopy(padding, 0, blockToEncrypt, blockWithoutPad.length, padding.length);
                } else {
                    continue;
                }
            } else {
                blockToEncrypt = Arrays.copyOfRange(toEncrypt, from * 16, to * 16);
            }

            byte[] encrypted = AES.encrypt(
                    counter.toByteArray(),
                    blockToEncrypt,
                    this.key
            );
            // counter se pusta vo encrypt pa potoa se pravi XOR so datata
            from++;
            to++;
            try {
                result.write(encrypted);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Bytes: ");
            printBytes(blockToEncrypt);
//            System.out.println("Encrypted: ");
//            printBytes(encrypted);
        }


        // pom = for i to n, AES.encrypt(ctr, block);
        // Di = pom XOR Bi;
    }

    public void sendMessage(ClearTextFrame toSend)
    {
        this.encrypt(toSend.data);
    }

}
