package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.stream.IntStream;

public class FeistelCipher {

    private final FeistelEncodeRound[] encryptChain;
    private final FeistelEncodeRound[] decryptChain;
    private final PrivateKeyCryptosystem cryptosystem;

    public FeistelCipher(KeyFactory factory, FeistelFFunction fcn, String hexKey, PrivateKeyCryptosystem cryptosystem)
    {
        long[] keySchedule = factory.createKeySchedule(hexKey);
        this.cryptosystem = cryptosystem;

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

        int counter = 0;
        BigInteger nextBlock = BigInteger.ZERO;

        IntStream encodeStream =  message.chars();
        Iterator<Integer> iter = encodeStream.iterator();
        while(iter.hasNext())
        {
            int nextByte = iter.next();
            //System.out.println(nextByte + " = " + Long.toBinaryString(nextByte));

            if(counter < 8)
            {
                // Bit shift logic
                nextBlock = nextBlock.or(new BigInteger(String.valueOf(nextByte)).shiftLeft((7 - counter) * 8));
                //System.out.println(nextBlock.toString(2));

                if(counter == 7)
                {
                    BigInteger testBlock = new BigInteger("0123456789ABCDEF", 16);
                    encodeOut.append(encryptFlag ? encryptBlock(testBlock) : decryptBlock(testBlock));
                    //encodeOut.append(encryptFlag ? encryptBlock(nextBlock) : decryptBlock(nextBlock));
                    counter = 0;
                    nextBlock = BigInteger.ZERO;
                }
                else
                    counter++;
            }
        }

        // Bit shift logic to check if we have a fragment.  We don't need to pad it because its already zero
        if(! nextBlock.equals(BigInteger.ZERO))
            encodeOut.append(encryptFlag ? encryptBlock(nextBlock) : decryptBlock(nextBlock));

        return encodeOut.toString();
    }

    private String encryptBlock(BigInteger aBlock)
    {
        StringBuilder encryptOut = new StringBuilder();

        // Perform the initial permutation before handing it to the encryption chain
        BigInteger encrypted = cryptosystem.performInitialPermutation(aBlock);

        for (FeistelEncodeRound feistelEncodeRound : encryptChain)
            encrypted = feistelEncodeRound.transform(encrypted);

        // Swap the upper 32 with the lower 32 bits since we are after the last encrypt block
        BigInteger encryptedAndSwapped = (encrypted.and(BigInteger.valueOf(4294967295L)).shiftLeft(32));
        encryptedAndSwapped = encryptedAndSwapped.or(encrypted.shiftRight(32));

        // Perform the final permutation to finish the encryption process
        encryptedAndSwapped = cryptosystem.performFinalPermutation(encryptedAndSwapped);

        // Convert the encrypted big integer back to ascii to put in the output string
        String cryptoChunk = "";
        BigInteger numberChunk = BigInteger.ZERO;
        for(int i = 0; i < 8; i++)
        {
            numberChunk = encryptedAndSwapped.and(BigInteger.valueOf(255L << ((7 - i) * 8)));
            cryptoChunk = Character.toString((char)(numberChunk.shiftRight((7 - i) * 8)).intValue());
            encryptOut.append(cryptoChunk);
        }

        //System.out.println(encryptOut.toString());
        return encryptOut.toString();
    }

    private String decryptBlock(BigInteger aBlock)
    {
        StringBuilder decryptOut = new StringBuilder();

        // We have a whole block at this point, so send it through the decrypt chain
        BigInteger decrypted = aBlock;
        for (FeistelEncodeRound feistelDecryptRound : decryptChain)
            decrypted = feistelDecryptRound.transform(decrypted);

        // Swap the upper 32 with the lower 32 bits since we are after the last decrypt block
        BigInteger decryptedAndSwapped = (decrypted.and(BigInteger.valueOf(4294967295L)).shiftLeft(32));
        decryptedAndSwapped = decryptedAndSwapped.or(decrypted.shiftRight(32));

        // Convert the decrypted big integer back to ascii to put in the output string
        String cryptoChunk = "";
        BigInteger numberChunk = BigInteger.ZERO;
        for(int i = 0; i < 8; i++)
        {
            numberChunk = decryptedAndSwapped.and(BigInteger.valueOf(255L << ((7 - i) * 8)));
            cryptoChunk = Character.toString((char)(numberChunk.shiftRight((7 - i) * 8)).intValue());

            if(! numberChunk.equals(BigInteger.ZERO))
                decryptOut.append(cryptoChunk);
        }

        //System.out.println(decryptOut.toString());
        return decryptOut.toString();
    }
}