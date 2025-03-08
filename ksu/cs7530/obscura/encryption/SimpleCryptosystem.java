package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public class SimpleCryptosystem implements PrivateKeyCryptosystem{

    private final FeistelCipher cipher;

    public SimpleCryptosystem(String hexKey)
    {
        this.cipher = new FeistelCipher(new SimpleKeyFactory(), new SimpleFFunction(), hexKey, this);
        System.out.println("SimpleCryptosystem constructed");
    }

    public String encrypt(String message) { return cipher.encrypt(DESCryptosystem.stringToHex(message)); }
    public String decrypt(String message)
    {
        String plainStr = DESCryptosystem.hexToString(cipher.decrypt(message));

        // Trim off any zeros we padded at the end of a partial block
        while(plainStr.charAt(plainStr.length() - 1) == 0)
            plainStr = plainStr.substring(0, plainStr.length() - 1);

        return plainStr;
    }

    public BigInteger performInitialPermutation(BigInteger input) { return input; }
    public BigInteger performFinalPermutation(BigInteger input) { return input; }

    public static void main(String[] args)
    {
        //SimpleCryptosystem crypto = new SimpleCryptosystem("B5308BE6267FF00");
        //SimpleCryptosystem crypto = new SimpleCryptosystem("D5308BE6267FFF0");
        SimpleCryptosystem crypto = new SimpleCryptosystem("0123456789ABCDEF");

        String plainText = "I want to be a cowboy, and you can be my cowgirl";
        //String plainText = "01234567";
        System.out.println("Original string = " + plainText);
        String encrypted = crypto.encrypt(plainText);
        System.out.println("Encrypted string = " + encrypted);
        String decrypted = crypto.decrypt(encrypted);
        System.out.println("Decrypted string = " + decrypted);
    }
}