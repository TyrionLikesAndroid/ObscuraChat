package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public class DESCryptosystem extends Cryptosystem {

    static private final byte[] INITIAL_PERMUTATION = { 58, 50, 42, 34, 26, 18, 10, 2,
                                                        60, 52, 44, 36, 28, 20, 12, 4,
                                                        62, 54, 46, 38, 30, 22, 14, 6,
                                                        64, 56, 48, 40, 32, 24, 16, 8,
                                                        57, 49, 41, 33, 25, 17, 9, 1,
                                                        59, 51, 43, 35, 27, 19, 11, 3,
                                                        61, 53, 45, 37, 29, 21, 13, 5,
                                                        63, 55, 47, 39, 31, 23, 15, 7};

    static private final byte[] FINAL_PERMUTATION = { 40, 8, 48, 16, 56, 24, 64, 32,
                                                      39, 7, 47, 15, 55, 23, 63, 31,
                                                      38, 6, 46, 14, 54, 22, 62, 30,
                                                      37, 5, 45, 13, 53, 21, 61, 29,
                                                      36, 4, 44, 12, 52, 20, 60, 28,
                                                      35, 3, 43, 11, 51, 19, 59, 27,
                                                      34, 2, 42, 10, 50, 18, 58, 26,
                                                      33, 1, 41, 9, 49, 17, 57, 25};

    private final FeistelCipher cipher;

    public DESCryptosystem(String hexKey)
    {
        this.cipher = new FeistelCipher(new DESKeyFactory(), new DESFFunction(), hexKey, this);
        System.out.println("DESCryptosystem constructed");
    }

    public String encrypt(String message, boolean nested)
    {
        String out = "";
        if(! nested)
        {
            String cipherText = cipher.encrypt(stringToHex(message));
            out = super.encrypt(cipherText, false);
        }
        else
            out = cipher.encrypt(message);

        return out;
    }
    public String decrypt(String message, boolean nested)
    {
        String out = "";
        if(! nested)
        {
            String cipherMsg = super.decrypt(message, false);
            String plainStr = DESCryptosystem.hexToString(cipher.decrypt(cipherMsg));

            // Trim off any zeros we padded at the end of a partial block
            while (plainStr.charAt(plainStr.length() - 1) == 0)
                plainStr = plainStr.substring(0, plainStr.length() - 1);

            out = plainStr;
        }
        else
            out = cipher.decrypt(message);

        return out;
    }

    public BigInteger performInitialPermutation(BigInteger input)
    {
        //return input;

        //System.out.println("DES initial permutation input = " + input);
        String binaryInput = padLeadingZerosToFit(input.toString(2), 64);
        //System.out.println("Binary Conversion = " + binaryInput);

        // Perform the initial permutation on the input that is already in binary
        char[] afterPermutation = new char[64];
        for(int i = 0; i < 64; i++)
            afterPermutation[i] = binaryInput.charAt(INITIAL_PERMUTATION[i] - 1);

        String output = new String(afterPermutation);
        //System.out.println("DES initial permutation output = " + output);

        return new BigInteger(output, 2);
    }

    public BigInteger performFinalPermutation(BigInteger input)
    {
        //return input;

        //System.out.println("DES final permutation input = " + input);
        String binaryInput = padLeadingZerosToFit(input.toString(2), 64);
        //System.out.println("Binary Conversion = " + binaryInput);

        // Perform the initial permutation on the input that is already in binary
        char[] afterPermutation = new char[64];
        for(int i = 0; i < 64; i++)
            afterPermutation[i] = binaryInput.charAt(FINAL_PERMUTATION[i] - 1);

        String output = new String(afterPermutation);
        //System.out.println("DES final permutation output = " + output);
        //System.out.println();

        return new BigInteger(output, 2);
    }

    public static String padLeadingZerosToFit(String binaryString, int size)
    {
        StringBuilder output = new StringBuilder(binaryString);

        int padNeeded = size - output.length();
        for(int i = 0; i < padNeeded; i++)
            output.insert(0, "0");

        return output.toString();
    }

    public static String stringToHex(String input) {
        StringBuilder hexString = new StringBuilder();
        for (char c : input.toCharArray()) {
            hexString.append(String.format("%02x", (int) c));
        }
        return hexString.toString();
    }

    public static String hexToString(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public static String hexToBinary(String hex)
    {
        StringBuilder binary = new StringBuilder();
        hex = hex.replace(" ", ""); // Remove spaces if any

        for (char hexChar : hex.toCharArray()) {
            int decimal = Integer.parseInt(String.valueOf(hexChar), 16);
            String binaryChunk = String.format("%4s", Integer.toBinaryString(decimal)).replace(' ', '0');
            binary.append(binaryChunk);
        }

        return binary.toString();
    }

    public static void main(String[] args)
    {
        DESCryptosystem crypto = new DESCryptosystem("F33457789BBCDFF1");
        //DESCryptosystem crypto = new DESCryptosystem("FFFFFFFF00000000");

        //String plainText = "0123456789ABCDEF";
        String plainText = "I pledge allegiance to the flag of the United States of your momma";
        System.out.println("Original string = " + plainText);
        String encrypted = crypto.encrypt(plainText, false);
        System.out.println("Encrypted string = " + encrypted);
        String decrypted = crypto.decrypt(encrypted, false);
        System.out.println("Decrypted string = " + decrypted);
    }
}