package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public class SimpleCryptosystem implements PrivateKeyCryptosystem{

    private final FeistelCipher cipher;

    public SimpleCryptosystem(String hexKey)
    {
        this.cipher = new FeistelCipher(new SimpleKeyFactory(), new SimpleFFunction(), hexKey, this);
        System.out.println("SimpleCryptosystem constructed");
    }

    public String encrypt(String message) { return cipher.encrypt(message); }
    public String decrypt(String message) { return cipher.decrypt(message); }

    public BigInteger performInitialPermutation(BigInteger input) { return input; }
    public BigInteger performFinalPermutation(BigInteger input) { return input; }

    public static void main(String[] args)
    {
        SimpleCryptosystem crypto = new SimpleCryptosystem("5308BE6267FFFF");

        String plainText = "I pledge allegiance to the flag of the United States of America";
        System.out.println("Original string = " + plainText);
        String encrypted = crypto.encrypt(plainText);
        System.out.println("Encrypted string = " + encrypted);
        String decrypted = crypto.decrypt(encrypted);
        System.out.println("Decrypted string = " + decrypted);
    }
}