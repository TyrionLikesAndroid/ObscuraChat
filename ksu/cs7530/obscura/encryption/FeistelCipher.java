package ksu.cs7530.obscura.encryption;

import java.util.stream.IntStream;

public class FeistelCipher {

    private long key;
    private long[] keySchedule;
    private FeistelEncryptRound[] encryptChain;
    private FeistelDecryptRound[] decryptChain;

    public FeistelCipher(KeyFactory factory, FeistelFFunction fcn, long key)
    {
        this.key = key;
        this.keySchedule = factory.createKeySchedule(key);

        int chainSize = keySchedule.length;
        encryptChain = new FeistelEncryptRound[chainSize];
        decryptChain = new FeistelDecryptRound[chainSize];

        // Stuff the key schedules in opposite order for encrypt / decrypt chains
        for(int i = 0; i < chainSize; i++)
        {
            encryptChain[i] = new FeistelEncryptRound(keySchedule[i], fcn);
            decryptChain[i] = new FeistelDecryptRound(keySchedule[chainSize - i - 1], fcn);
        }
    }

    public String encrypt(String message)
    {
        if (message == null || message.isEmpty())
        {
           return "";
        }

        StringBuilder asciiOnly = new StringBuilder();
        for (char c : message.toCharArray()) {
            if (c <= 127) { // ASCII range is 0 to 127
                asciiOnly.append(c);
            }
        }

        IntStream encryptStream =  asciiOnly.chars();
        encryptStream.forEach(ascii -> System.out.println(ascii + " = " + Long.toBinaryString(ascii)));

        return "";
    }

    public String decrypt(String message)
    {
        return "";
    }

}
