package ksu.cs7530.obscura.encryption;

import java.util.Iterator;
import java.util.stream.IntStream;

public class FeistelCipher {

    private final FeistelEncodeRound[] encryptChain;
    private final FeistelEncodeRound[] decryptChain;

    public FeistelCipher(KeyFactory factory, FeistelFFunction fcn, String hexKey)
    {
        long[] keySchedule = factory.createKeySchedule(hexKey);

        int chainSize = keySchedule.length;
        encryptChain = new FeistelEncodeRound[chainSize];
        decryptChain = new FeistelEncodeRound[chainSize];

        // Stuff the key schedules in opposite order for encrypt / decrypt chains
        for(int i = 0; i < chainSize; i++)
        {
            encryptChain[i] = new FeistelEncodeRound(keySchedule[i], fcn);
            decryptChain[i] = new FeistelEncodeRound(keySchedule[chainSize - i - 1], fcn);
        }
    }

    public String encrypt(String message)
    {
        return encode(message, true);
    }

    public String decrypt(String message)
    {
        return encode(message, false);
    }

    private String encode(String message, boolean encryptFlag)
    {
        StringBuilder encodeOut = new StringBuilder();

        if (message == null || message.isEmpty())
            return encodeOut.toString();

        IntStream encodeStream =  message.chars();
        //encodeStream.forEach(ascii -> System.out.println(ascii + " = " + Long.toBinaryString(ascii)));

        int counter = 0;
        long nextBlock = 0L;
        Iterator<Integer> iter = encodeStream.iterator();
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
                    encodeOut.append(encryptFlag ? encryptBlock(nextBlock) : decryptBlock(nextBlock));
                    counter = 0;
                    nextBlock = 0L;
                }
                else
                    counter++;
            }
        }

        // Bit shift logic to check if we have a fragment.  We don't need to pad it because its already zero
        if(nextBlock != 0L)
            encodeOut.append(encryptFlag ? encryptBlock(nextBlock) : decryptBlock(nextBlock));

        return encodeOut.toString();
    }

    private String encryptBlock(long aBlock)
    {
        StringBuilder encryptOut = new StringBuilder();

        long encrypted = aBlock;
        for (FeistelEncodeRound feistelEncodeRound : encryptChain)
            encrypted = feistelEncodeRound.transform(encrypted);

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
            encryptOut.append(cryptoChunk);
        }

        return encryptOut.toString();
    }

    private String decryptBlock(long aBlock)
    {
        StringBuilder decryptOut = new StringBuilder();

        // We have a whole block at this point, so send it through the decrypt chain
        long decrypted = aBlock;
        for (FeistelEncodeRound feistelDecryptRound : decryptChain)
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
                decryptOut.append(cryptoChunk);
        }

        return decryptOut.toString();
    }
}