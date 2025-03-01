package ksu.cs7530.obscura.encryption;

import java.util.Iterator;
import java.util.stream.IntStream;

public class FeistelCipher {

    private final FeistelEncryptRound[] encryptChain;
    private final FeistelDecryptRound[] decryptChain;

    public FeistelCipher(KeyFactory factory, FeistelFFunction fcn, long key)
    {
        long[] keySchedule = factory.createKeySchedule(key);

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
        String encryptOut = "";

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
        //encryptStream.forEach(ascii -> System.out.println(ascii + " = " + Long.toBinaryString(ascii)));

        int counter = 0;
        long nextBlock = 0L;

        Iterator<Integer> iter = encryptStream.iterator();
        while(iter.hasNext())
        {
            int nextInt = iter.next();
            if(counter < 4)
            {
                // Bit shift logic
                nextBlock = nextBlock | ((long) nextInt) << ((3 - counter) * 16);
                //System.out.println(Long.toBinaryString(nextBlock));

                if(counter == 3)
                {
                    encryptOut = encryptOut + encryptBlock(nextBlock);
                    counter = 0;
                    nextBlock = 0L;
                }
                else
                    counter++;
            }
        }

        // Bit shift logic to check if we have a fragment.  We don't need to pad it because its already zero
        if(nextBlock != 0L)
            encryptOut = encryptOut + encryptBlock(nextBlock);

        return encryptOut;
    }

    public String decrypt(String message)
    {
        String decryptOut = "";

        if (message == null || message.isEmpty())
        {
            return "";
        }

        IntStream decryptStream =  message.chars();
        //decryptStream.forEach(ascii -> System.out.println(ascii + " = " + Long.toBinaryString(ascii)));

        int counter = 0;
        long nextBlock = 0L;

        Iterator<Integer> iter = decryptStream.iterator();
        while(iter.hasNext())
        {
            int nextInt = iter.next();
            if(counter < 4)
            {
                // Bit shift logic
                nextBlock = nextBlock | ((long) nextInt) << ((3 - counter) * 16);
                // System.out.println(Long.toBinaryString(nextBlock));

                if(counter == 3)
                {
                    decryptOut = decryptOut + decryptBlock(nextBlock);
                    counter = 0;
                    nextBlock = 0L;
                }
                else
                    counter++;
            }
        }

        // Bit shift logic to check if we have a fragment.  We don't need to pad it because its already zero
        if(nextBlock != 0L)
            decryptOut = decryptOut + decryptBlock(nextBlock);

        return decryptOut;
    }

    private String encryptBlock(long aBlock)
    {
        String encryptOut = "";

        long encrypted = aBlock;
        for (FeistelEncryptRound feistelEncryptRound : encryptChain)
            encrypted = feistelEncryptRound.transform(encrypted);

        // Swap the upper 32 with the lower 32 bits since we are after the last encrypt block
        long encryptedAndSwapped = (encrypted & 4294967295L) << 32L;
        encryptedAndSwapped = encryptedAndSwapped | (encrypted >> 32L);

        // Convert the encrypted long back to ascii to put in the output string
        String cryptoChunk = "";
        long numberChunk = 0L;
        for(int i = 0; i < 4; i++)
        {
            numberChunk = encryptedAndSwapped & (65535L << ((3 - i) * 16));
            cryptoChunk = Character.toString((char)(numberChunk >>> ((3 - i) * 16)));
            encryptOut = encryptOut + cryptoChunk;
        }

        return encryptOut;
    }

    private String decryptBlock(long aBlock)
    {
        String decryptOut = "";

        // We have a whole block at this point, so send it through the decrypt chain
        long decrypted = aBlock;
        for (FeistelDecryptRound feistelDecryptRound : decryptChain)
            decrypted = feistelDecryptRound.transform(decrypted);

        // Swap the upper 32 with the lower 32 bits since we are after the last decrypt block
        long decryptedAndSwapped = (decrypted & 4294967295L) << 32L;
        decryptedAndSwapped = decryptedAndSwapped | (decrypted >> 32L);

        // Convert the decrypted long back to ascii to put in the output string
        String cryptoChunk = "";
        long numberChunk = 0L;
        for(int i = 0; i < 4; i++)
        {
            numberChunk = decryptedAndSwapped & (65535L << ((3 - i) * 16));
            cryptoChunk = Character.toString((char)(numberChunk >>> ((3 - i) * 16)));

            if(numberChunk != 0L)
                decryptOut = decryptOut + cryptoChunk;
        }

        return decryptOut;
    }
}