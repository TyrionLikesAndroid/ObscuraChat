package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public class RSACryptosystem extends Cryptosystem{

    public RSAKeyFactory.RSAKeyTriad localKeys;
    public RSAKeyFactory.RSAKeyTriad remoteKeys;

    public RSACryptosystem()
    {
        this.localKeys = RSAKeyFactory.generateRSAKeyset();
        System.out.println("RSACryptosystem constructed");
    }

    public void setRemoteKeyset(BigInteger publicKey, BigInteger nValue)
    {
        this.remoteKeys = new RSAKeyFactory.RSAKeyTriad(publicKey, BigInteger.ZERO, nValue);
    }

    public String encrypt(String message, boolean nested)
    {
        return encodeMessage(DESCryptosystem.stringToHex(message));
    }

    public String decrypt(String message, boolean nested)
    {
        String plainStr = DESCryptosystem.hexToString(decodeMessage(message));

        // Trim off any zeros we padded at the end of a partial block
        while(plainStr.charAt(plainStr.length() - 1) == 0)
            plainStr = plainStr.substring(0, plainStr.length() - 1);

        return plainStr;
    }

    private String encodeMessage(String hexMsg)
    {
        StringBuilder encodeOut = new StringBuilder();

        if (hexMsg == null || hexMsg.isEmpty())
            return encodeOut.toString();

        BigInteger nextBlock;
        String chunk = null;
        String encryptedChunk = null;
        int chunkSize = 60;     // 60 hex characters will take us to 960 bytes out of our 1024 length modulus
        for (int i = 0; i < hexMsg.length(); i += chunkSize)
        {
            chunk = hexMsg.substring(i, Math.min(i + chunkSize, hexMsg.length()));
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

            // operational mode hook
            String cbcChunk = cbcTransform(paddedChunk, false, 16);

            nextBlock = new BigInteger(cbcChunk,16);

            // CTR mode logic
            String encodeChunk;
            if(operationalMode.equals(Cryptosystem.CHAT_OPERATIONAL_MODE_CTR))
            {
                String ctr = ctrNextCounter(chunkSize, 16, false);

                BigInteger ctrBlock = new BigInteger(ctr,16);
                String ctrEncoded = encryptBlock(ctrBlock);

                encodeChunk = (nextBlock.xor(new BigInteger(ctrEncoded, 16))).toString(16);
                encodeChunk = padEncryptionIfNeeded(encodeChunk);
            }
            else
            {
                encryptedChunk = encryptBlock(nextBlock);
                //System.out.println("Encrypted Chunk(" + encryptedChunk.length() + "): " + encryptedChunk);

                encodeChunk = padEncryptionIfNeeded(encryptedChunk);
                setPreviousOut(encodeChunk);
            }

            encodeOut.append(encodeChunk);
        }

        return encodeOut.toString();
    }

    private String decodeMessage(String hexMsg)
    {
        StringBuilder decodeOut = new StringBuilder();

        if (hexMsg == null || hexMsg.isEmpty())
            return decodeOut.toString();

        BigInteger nextBlock;
        String chunk = null;
        int chunkSize = 256;     // Decode in chunks of 256 to keep the boundaries correct
        for (int i = 0; i < hexMsg.length(); i += chunkSize)
        {
            chunk = hexMsg.substring(i, Math.min(i + chunkSize, hexMsg.length()));

            if(chunk.length() == chunkSize)
            {
                nextBlock = new BigInteger(chunk,16);

                // CTR mode logic
                String encodeChunk;
                if(operationalMode.equals(Cryptosystem.CHAT_OPERATIONAL_MODE_CTR))
                {
                    String ctr = ctrNextCounter(60, 16, true);

                    BigInteger ctrBlock = new BigInteger(ctr,16);
                    String ctrEncoded = encryptBlock(ctrBlock);

                    encodeChunk = (nextBlock.xor(new BigInteger(ctrEncoded, 16))).toString(16);
                }
                else
                {
                    encodeChunk = decryptBlock(nextBlock);

                    // operational mode hook
                    encodeChunk = cbcTransform(encodeChunk, true, 16);
                    setPreviousIn(chunk);
                }

                decodeOut.append(encodeChunk);
            }
        }

       // We should never have a fragment on decryption if encryption is working
        if(chunk.length() != chunkSize)
            System.out.println("Decrypt error - unexpected chunk size");

        return decodeOut.toString();
    }

    private String encryptBlock(BigInteger msg)
    {
        BigInteger encrypted = msg.modPow(remoteKeys.publicKey, remoteKeys.n);
        return encrypted.toString(16);
    }

    private String padEncryptionIfNeeded(String chunk)
    {
        // This keeps us right on our 256 character boundary.  Sometimes the math comes up short, so we
        // need to add zeros or the decryption will get outside the boundaries
        String out = chunk;
        if(chunk.length() < 256)
        {
            out = DESCryptosystem.padLeadingZerosToFit(chunk, 256);
            //System.out.println("Padding for short chunk - " + out);
        }
        return out;
    }

    private String decryptBlock(BigInteger msg)
    {
        BigInteger decrypted = msg.modPow(localKeys.privateKey, localKeys.n);
        return decrypted.toString(16);
    }

    public static void main(String[] args)
    {
        // Create an RSA cryptosystem.  This step will make the local keyset on construction
        RSACryptosystem crypto = new RSACryptosystem();

        // For our self test, make sure both sets are the same since there is no remote end
        crypto.remoteKeys = crypto.localKeys;

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
        System.out.println("RSA Encrypt Time = " + encryptTime + ", Decrypt Time = " + decryptTime);
    }
}