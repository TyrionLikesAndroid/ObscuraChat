package ksu.cs7530.obscura.encryption;

import ksu.cs7530.obscura.view.ChatLoginView;

import javax.swing.*;

public class SimpleCryptosystem extends PrivateKeyCryptosystem{

    public SimpleCryptosystem(long key)
    {
        super(new FeistelCipher(new SimpleKeyFactory(), new SimpleFFunction(), key));
        System.out.println("SimpleCryptosystem constructed");
    }

    public String encrypt(String message)
    {
        return cipher.encrypt(message);
    }
    public String decrypt(String message)
    {
        return cipher.decrypt(message);
    }

    public static void main(String[] args)
    {
        SimpleCryptosystem crypto = new SimpleCryptosystem(23372036854775807L);

        String plainText = "I pledge allegiance to the flag of the United States of America";
        System.out.println("Original string = " + plainText);
        String encrypted = crypto.encrypt(plainText);
        System.out.println("Encrypted string = " + encrypted);
        String decrypted = crypto.decrypt(encrypted);
        System.out.println("Decrypted string = " + decrypted);
    }
}
