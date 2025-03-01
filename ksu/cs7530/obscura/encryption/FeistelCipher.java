package ksu.cs7530.obscura.encryption;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

public class FeistelCipher {

    private long key;
    private long[] keySchedule;
    private FeistelEncryptRound[] encryptChain;
    private FeistelDecryptRound[] decryptChain;
    private String encryptOut = "";
    private String decryptOut = "";

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
        //encryptStream.forEach(ascii -> System.out.println(ascii + " = " + Long.toBinaryString(ascii)));

        int counter = 0;
        long nextBlock = 0L;
//        int[] holdingArray = new int[4];
        Iterator<Integer> iter = encryptStream.iterator();
        while(iter.hasNext())
        {
            int nextInt = iter.next();
            if(counter < 4)
            {
                // Array logic
//                holdingArray[counter] = nextInt;
//                System.out.println(Long.toBinaryString(nextInt));

                // Bit shift logic
                nextBlock = nextBlock | ((long) nextInt) << ((3 - counter) * 16);
//                System.out.println(Long.toBinaryString(nextBlock));

                if(counter == 3)
                {
                    // Array logic
//                    System.out.println(Arrays.toString(holdingArray));
//                    Arrays.fill(holdingArray,0);

                    // Bit shift logic
//                    System.out.println(Long.toBinaryString(nextBlock));

                    // We have a whole block at this point, so send it through the encrypt chain
                    long encrypted = nextBlock;
                    for (int i = 0; i < encryptChain.length; i++)
                        encrypted = encryptChain[i].transform(encrypted);

                    // Convert the encrypted long back to ascii to put in the output string
                    String cryptoChunk = "";
                    long numberChunk = 0L;
                    for(int i = 0; i < 4; i++)
                    {
                        numberChunk = encrypted & (65535L << ((3 - i) * 16));
                        long numberChunkShift = numberChunk >>> ((3 - i) * 16);
                        if(numberChunkShift > 127)
                            System.out.println("ERROR - NON ASCII CHARACTER FOUND");

                        cryptoChunk = Character.toString((char)(numberChunk >>> ((3 - i) * 16)));

                        encryptOut = encryptOut + cryptoChunk;
                    }

                    counter = 0;
                    nextBlock = 0L;
                }
                else
                    counter++;
            }
        }

        // Array logic.  Check if we have a fragment.  If so we need to pad it and send it
//        if(holdingArray.length > 0)
//        {
//            for(int i = holdingArray.length; i < 4; i++)
//                holdingArray[i] = 0;
//            System.out.println(Arrays.toString(holdingArray));
//        }

        // Bit shift logic to check if we have a fragment.  We don't need to pad it because its already zero
        if(nextBlock != 0L)
        {
            long encrypted = nextBlock;
            for(int i = 0; i < encryptChain.length; i++)
                encrypted = encryptChain[i].transform(encrypted);

            // Convert the encrypted long back to ascii to put in the output string
            String cryptoChunk = "";
            long numberChunk = 0L;
            for(int i = 0; i < 4; i++)
            {
                numberChunk = encrypted & (65535L << ((3 - i) * 16));
                cryptoChunk = Character.toString((char)(numberChunk >>> ((3 - i) * 16)));

                encryptOut = encryptOut + cryptoChunk;
            }
        }

        return encryptOut;
    }

    public String decrypt(String message)
    {
        return "";
    }
}
