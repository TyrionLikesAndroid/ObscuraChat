package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public class DESCryptosystem implements PrivateKeyCryptosystem {

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

    public String encrypt(String message)
    {
        return cipher.encrypt(message);
    }
    public String decrypt(String message)
    {
        return cipher.decrypt(message);
    }

    public BigInteger performInitialPermutation(BigInteger input)
    {
        //return input;

        System.out.println("DES initial permutation input = " + input);
        String binaryInput = padLeadingZerosToFit64(input.toString(2));
        System.out.println("Binary Conversion = " + binaryInput);

        // Perform the initial permutation on the input that is already in binary
        char[] afterPermutation = new char[64];
        for(int i = 0; i < 64; i++)
            afterPermutation[i] = binaryInput.charAt(INITIAL_PERMUTATION[i] - 1);

        String output = new String(afterPermutation);
        System.out.println("DES initial permutation output = " + output);

        return new BigInteger(output, 2);
    }

    public BigInteger performFinalPermutation(BigInteger input)
    {
        //return input;

        System.out.println("DES final permutation input = " + input);
        String binaryInput = padLeadingZerosToFit64(input.toString(2));
        System.out.println("Binary Conversion = " + binaryInput);

        // Perform the initial permutation on the input that is already in binary
        char[] afterPermutation = new char[64];
        for(int i = 0; i < 64; i++)
            afterPermutation[i] = binaryInput.charAt(FINAL_PERMUTATION[i] - 1);

        String output = new String(afterPermutation);
        System.out.println("DES final permutation output = " + output);
        System.out.println();

        return new BigInteger(output, 2);
    }

    private String padLeadingZerosToFit64(String binaryString)
    {
        StringBuilder output = new StringBuilder(binaryString);

        int padNeeded = 64 - output.length();
        for(int i = 0; i < padNeeded; i++)
            output.insert(0, "0");

        return output.toString();
    }

    public static void main(String[] args)
    {
        DESCryptosystem crypto = new DESCryptosystem("0123456789ABCDEF");
        //DESCryptosystem crypto = new DESCryptosystem("FFFFFFFF00000000");

        String plainText = "0123456789ABCDEF";
        //String plainText = "I pledge allegiance to the flag of the United States of America";
        System.out.println("Original string = " + plainText);
        String encrypted = crypto.encrypt(plainText);
        System.out.println("Encrypted string = " + encrypted);
        String decrypted = crypto.decrypt(encrypted);
        System.out.println("Decrypted string = " + decrypted);
    }
}