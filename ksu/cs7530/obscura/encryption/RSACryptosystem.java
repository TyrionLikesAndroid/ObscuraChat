package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public class RSACryptosystem implements Cryptosystem{

    public RSAKeyFactory.RSAKeyTriad localKeys;
    public RSAKeyFactory.RSAKeyTriad remoteKeys;

    public RSACryptosystem()
    {
        this.localKeys = generateRSAKeyset();
        System.out.println("RSACryptosystem constructed");
    }

    public void setRemoteKeyset(BigInteger publicKey, BigInteger nValue)
    {
        this.remoteKeys = new RSAKeyFactory.RSAKeyTriad(publicKey, BigInteger.ZERO, nValue);
    }

    public String encrypt(String message)
    {
        String hexMsg = DESCryptosystem.stringToHex(message);
        return encodeMessage(hexMsg);
    }

    public String decrypt(String message)
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

            if(chunk.length() == chunkSize)
            {
                nextBlock = new BigInteger(chunk,16);
                encryptedChunk = encryptBlock(nextBlock);
                //System.out.println("Encrypted Chunk(" + encryptedChunk.length() + "): " + encryptedChunk);

                encodeOut.append(padEncryptionIfNeeded(encryptedChunk));
            }
        }

        // Check to see if we have a fragment.  We need to pad right with zeros if we
        // hit this case to keep the block size constant
        if(chunk.length() != chunkSize)
        {
            StringBuilder paddedString = new StringBuilder(chunk);
            for (int i = 0; i < chunkSize - chunk.length(); i++)
                paddedString.append('0');

            nextBlock = new BigInteger(paddedString.toString(), 16);
            encryptedChunk = encryptBlock(nextBlock);
            //System.out.println("Encrypted Chunk(" + encryptedChunk.length() + "): " + encryptedChunk);

            encodeOut.append(padEncryptionIfNeeded(encryptedChunk));
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
            //System.out.println("Chunk: " + chunk);

            if(chunk.length() == chunkSize)
            {
                nextBlock = new BigInteger(chunk,16);
                String tmp1 = decryptBlock(nextBlock);
                //System.out.println("Decrypted Chunk(" + tmp1.length() + "): " + tmp1);
                decodeOut.append(tmp1);
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

    private static RSAKeyFactory.RSAKeyTriad generateRSAKeyset()
    {
        BigInteger primeCandidate1;
        BigInteger primeCandidate2;
        RSAKeyFactory.RSAKeyTriad keys = null;

        do
        {
            primeCandidate1 = RSAKeyFactory.generatePrimeCandidate();
            primeCandidate2 = RSAKeyFactory.generatePrimeCandidate();

            try { keys = new RSAKeyFactory().createRsaKeys(primeCandidate1, primeCandidate2); }
            catch(ArithmeticException e)
            {
                System.out.println("Got a math exception, phi and e are likely not relatively prime, so retry\n");
            }

        } while(keys == null);

        System.out.println(keys.toString());
        return keys;
    }

    public static void main(String[] args)
    {
        // Create an RSA cryptosystem.  This step will make the local keyset on construction
        RSACryptosystem crypto = new RSACryptosystem();

        // For our self test, make sure both sets are the same since there is no remote end
        crypto.remoteKeys = crypto.localKeys;

        String plainText = "I pledge allegiance to the flag of the United States of your momma";
        System.out.println("Original string = " + plainText);
        String encrypted = crypto.encrypt(plainText);
        System.out.println("Encrypted string = " + encrypted);
        String decrypted = crypto.decrypt(encrypted);
        System.out.println("Decrypted string = " + decrypted);
    }
}