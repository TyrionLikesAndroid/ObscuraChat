package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public class TripleDESCryptosystem extends Cryptosystem {

    private final DESCryptosystem[] desCrypto = new DESCryptosystem[3];;

    public TripleDESCryptosystem(String hexKey)
    {
        StringBuilder tripleDESHexKey = new StringBuilder(hexKey);

        // If the string is too short, fill out the rest of the length with zero
        for(int i = hexKey.length(); i < (64 * 3); i++)
            tripleDESHexKey.append("0");

        desCrypto[0] = new DESCryptosystem(tripleDESHexKey.substring(0, 63));
        desCrypto[1] = new DESCryptosystem(tripleDESHexKey.substring(64, 127));
        desCrypto[2] = new DESCryptosystem(tripleDESHexKey.substring(128,191));

        System.out.println("TripleDESCryptosystem constructed");
    }

    public String encrypt(String message, boolean nested)
    {
        String plainToHex = DESCryptosystem.stringToHex(message);
        String cipherText = desCrypto[2].encrypt(desCrypto[1].encrypt(desCrypto[0].encrypt(plainToHex, true), true), true);
        return super.encrypt(cipherText, false);
    }
    public String decrypt(String message, boolean nested)
    {
        String cipherMsg = super.decrypt(message, false);
        cipherMsg = desCrypto[2].decrypt(desCrypto[1].decrypt(desCrypto[0].decrypt(cipherMsg, true), true), true);
        String plainStr = DESCryptosystem.hexToString(cipherMsg);

        // Trim off any zeros we padded at the end of a partial block
        while(plainStr.charAt(plainStr.length() - 1) == 0)
            plainStr = plainStr.substring(0, plainStr.length() - 1);

        return plainStr;
    }

    public static void main(String[] args)
    {
        TripleDESCryptosystem crypto = new TripleDESCryptosystem("DEADBEEFDEADBEEF0123456789ABCDEFDEAD01234567BEEF");

        String plainText = "I pledge allegiance to the flag of the United States of your momma";
        System.out.println("Original string = " + plainText);
        String encrypted = crypto.encrypt(plainText, false);
        System.out.println("Encrypted string = " + encrypted);
        String decrypted = crypto.decrypt(encrypted, false);
        System.out.println("Decrypted string = " + decrypted);
    }
}