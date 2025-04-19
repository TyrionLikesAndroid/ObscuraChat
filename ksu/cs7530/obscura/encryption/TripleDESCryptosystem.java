package ksu.cs7530.obscura.encryption;

public class TripleDESCryptosystem extends Cryptosystem {

    private final DESCryptosystem[] desCrypto = new DESCryptosystem[3];;

    public TripleDESCryptosystem(String hexKey)
    {
        StringBuilder tripleDESHexKey = new StringBuilder(hexKey);

        // If the string is too short, fill out the rest of the length with zero
        for(int i = hexKey.length(); i < (16 * 3); i++)
            tripleDESHexKey.append("0");

        desCrypto[0] = new DESCryptosystem(tripleDESHexKey.substring(0, 16));
        desCrypto[1] = new DESCryptosystem(tripleDESHexKey.substring(16, 32));
        desCrypto[2] = new DESCryptosystem(tripleDESHexKey.substring(32, 48));

        System.out.println("TripleDESCryptosystem constructed");
    }

    protected void postOpModeChange()
    {
        // Set the operational mode on the first crypto instance
        desCrypto[0].setOperationalMode(operationalMode);
    }

    public String encrypt(String message, boolean nested)
    {
        String plainToHex = DESCryptosystem.stringToHex(message);
        return desCrypto[2].encrypt(desCrypto[1].encrypt(desCrypto[0].encrypt(plainToHex, true), true), true);
    }
    public String decrypt(String message, boolean nested)
    {
        String cipherMsg = desCrypto[0].decrypt(desCrypto[1].decrypt(desCrypto[2].decrypt(message, true), true), true);
        String plainStr = DESCryptosystem.hexToString(cipherMsg);

        // Trim off any zeros we padded at the end of a partial block
        while(plainStr.charAt(plainStr.length() - 1) == 0)
            plainStr = plainStr.substring(0, plainStr.length() - 1);

        return plainStr;
    }

    public static void main(String[] args)
    {
        TripleDESCryptosystem crypto = new TripleDESCryptosystem("DEADBEEFDEADBEEF0123456789ABCDEFDEAD01234567BEEF");

        //String plainText = Cryptosystem.PERFORMANCE_TEST_MSG;
        String plainText = "I pledge allegiance to the flag of the United States of your momma";
        System.out.println("Original string = " + plainText);

        long startEncrypt = System.currentTimeMillis();
        String encrypted = crypto.encrypt(plainText, false);
        long encryptTime = System.currentTimeMillis() - startEncrypt;

        System.out.println("Encrypted string = " + encrypted);

        long startDecrypt = System.currentTimeMillis();
        String decrypted = crypto.decrypt(encrypted, false);
        long decryptTime = System.currentTimeMillis() - startDecrypt;

        System.out.println("Decrypted string = " + decrypted);
        System.out.println("3DES Encrypt Time = " + encryptTime + ", Decrypt Time = " + decryptTime);
    }
}