package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.stream.IntStream;

public class FeistelCipher {

    private final FeistelEncodeRound[] encryptChain;
    private final FeistelEncodeRound[] decryptChain;
    private final DESCryptosystem cryptosystem;

    public FeistelCipher(KeyFactory factory, FeistelFFunction fcn, String hexKey, DESCryptosystem cryptosystem)
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

    public String encrypt(String hexMsg)
    {
        return encode(hexMsg, true);
    }

    public String decrypt(String hexMsg)
    {
        return encode(hexMsg, false);
    }

    private String encode(String hexMsg, boolean encryptFlag)
    {
        StringBuilder encodeOut = new StringBuilder();

        if (hexMsg == null || hexMsg.isEmpty())
            return encodeOut.toString();

        BigInteger nextBlock = BigInteger.ZERO;
        String binaryMsg = DESCryptosystem.hexToBinary(hexMsg);

        String chunk = null;
        int chunkSize = 64;
        for (int i = 0; i < binaryMsg.length(); i += chunkSize)
        {
            chunk = binaryMsg.substring(i, Math.min(i + chunkSize, binaryMsg.length()));
            //System.out.println("Chunk: " + chunk);

            // Check to see if we have a fragment.  Pad right with zeros to keep the block size constant
            String paddedChunk = chunk;
            if(chunk.length() != chunkSize)
            {
                StringBuilder paddedString = new StringBuilder(chunk);
                for (int j = 0; j < chunkSize - chunk.length(); j++)
                    paddedString.append('0');
                paddedChunk = paddedString.toString();
            }

            // CBC mode hook
            String cbcChunk = paddedChunk;
            if(encryptFlag)
                cbcChunk = cryptosystem.cbcTransform(paddedChunk, false, 2);

            // Next payload block to be encrypted
            nextBlock = new BigInteger(cbcChunk,2);

            // CTR mode logic
            String encodeChunk;
            if(cryptosystem.operationalMode.equals(Cryptosystem.CHAT_OPERATIONAL_MODE_CTR))
            {
                String ctr = cryptosystem.ctrNextCounter(chunkSize, 2, ! encryptFlag);

                BigInteger ctrBlock = new BigInteger(ctr,2);
                String ctrEncoded = encryptBlock(ctrBlock);

                encodeChunk = (nextBlock.xor(new BigInteger(ctrEncoded, 16))).toString(16);
                encodeChunk = DESCryptosystem.padLeadingZerosToFit(encodeChunk,16);
            }
            else
            {
                encodeChunk = encryptFlag ? encryptBlock(nextBlock) : decryptBlock(nextBlock);

                // CBC mode hook
                if(encryptFlag)
                    cryptosystem.setPreviousOut(new BigInteger(encodeChunk, 16).toString(2));
                else
                {
                    encodeChunk = cryptosystem.cbcTransform(encodeChunk, true, 16);
                    cryptosystem.setPreviousIn(new BigInteger(paddedChunk, 2).toString(16));
                }
            }

            encodeOut.append(encodeChunk);
        }

        return encodeOut.toString();
    }

    private String encryptBlock(BigInteger aBlock)
    {
        // Perform the initial permutation before handing it to the encryption chain
        BigInteger encrypted = cryptosystem.performInitialPermutation(aBlock);

        int iteration = 0;
        for (FeistelEncodeRound feistelEncodeRound : encryptChain)
        {
            encrypted = feistelEncodeRound.transform(encrypted, iteration, true);
            iteration++;
        }

        // Swap the upper 32 with the lower 32 bits since we are after the last encrypt block
        BigInteger encryptedAndSwapped = (encrypted.and(BigInteger.valueOf(4294967295L)).shiftLeft(32));
        encryptedAndSwapped = encryptedAndSwapped.or(encrypted.shiftRight(32));

        //System.out.println("LE17: " + encrypted.and(BigInteger.valueOf(4294967295L)).toString(2) +
                //" RE17: " + encrypted.shiftRight(32).toString(2));

        // Perform the final permutation to finish the encryption process
        encryptedAndSwapped = cryptosystem.performFinalPermutation(encryptedAndSwapped);

        // Convert the encrypted big integer back to hex
        return DESCryptosystem.padLeadingZerosToFit(encryptedAndSwapped.toString(16), 16);
    }

    private String decryptBlock(BigInteger aBlock)
    {
        // Perform the initial permutation before handing it to the decryption chain
        BigInteger decrypted = cryptosystem.performInitialPermutation(aBlock);

        int iteration = 0;
        for (FeistelEncodeRound feistelEncodeRound : decryptChain)
        {
            decrypted = feistelEncodeRound.transform(decrypted, iteration, false);
            iteration++;
        }

        // Swap the upper 32 with the lower 32 bits since we are after the last decrypt block
        BigInteger decryptedAndSwapped = (decrypted.and(BigInteger.valueOf(4294967295L)).shiftLeft(32));
        decryptedAndSwapped = decryptedAndSwapped.or(decrypted.shiftRight(32));

        //System.out.println("LD17: " + decrypted.and(BigInteger.valueOf(4294967295L)).toString(2) +
              //  " RD17: " + decrypted.shiftRight(32).toString(2));

        // Perform the final permutation to finish the encryption process
        decryptedAndSwapped = cryptosystem.performFinalPermutation(decryptedAndSwapped);

        // Convert the decrypted big integer back to hex
        String hexStr = DESCryptosystem.padLeadingZerosToFit(decryptedAndSwapped.toString(16), 16);

        return hexStr;
    }
}