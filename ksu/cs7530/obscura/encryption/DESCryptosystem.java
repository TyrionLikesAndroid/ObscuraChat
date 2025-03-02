package ksu.cs7530.obscura.encryption;

public class DESCryptosystem extends PrivateKeyCryptosystem {

    public DESCryptosystem(String hexKey)
    {
        super(new FeistelCipher(new DESKeyFactory(), new SimpleFFunction(), hexKey), hexKey);
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

    public static void main(String[] args)
    {
        DESCryptosystem crypto = new DESCryptosystem("0123456789ABCDEF");
        //DESCryptosystem crypto = new DESCryptosystem("FFFFFFFF00000000");

        String plainText = "I pledge allegiance to the flag of the United States of America";
        System.out.println("Original string = " + plainText);
        String encrypted = crypto.encrypt(plainText);
        System.out.println("Encrypted string = " + encrypted);
        String decrypted = crypto.decrypt(encrypted);
        System.out.println("Decrypted string = " + decrypted);
    }
}