package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;
import java.util.Random;

public class Cryptosystem {

    static public String CHAT_OPERATIONAL_MODE_CBC = "CBC";
    static public String CHAT_OPERATIONAL_MODE_ECB = "ECB";
    static public String CHAT_OPERATIONAL_MODE_CTR = "CTR";

    // 2K character test string for performance testing
    static public String PERFORMANCE_TEST_MSG =
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789";

    private String operationalMode = CHAT_OPERATIONAL_MODE_ECB;
    protected String previousOut;
    protected String previousIn;

    public void setOperationalMode(String mode)
    {
        System.out.println("Setting operational mode to " + mode);
        operationalMode = mode;

        // Need to clear the cached blocks on any transition to CBC.  If we were using ECB before
        // the transition, we will get garbage for the first word because we don't use our nonce.
        if(mode.equals(CHAT_OPERATIONAL_MODE_CBC))
        {
            previousIn = null;
            previousOut = null;
        }
    }

    public String encrypt(String message, boolean nested)
    {
        int size = message.length();
        System.out.println("super encrypt called[" + size + "] = " + message);

        return message;
    }

    public String decrypt(String message, boolean nested)
    {
        int size = message.length();
        System.out.println("super decrypt called[" + size + "] = " + message);

        return message;
    }

    protected String plainChunkOperation(String hexChunk, boolean inFlag)
    {
        if(operationalMode.equals(CHAT_OPERATIONAL_MODE_ECB))
            return hexChunk;
        else
        {
            String cbcChunk = inFlag ? previousIn : previousOut;
            BigInteger numChunk = new BigInteger(hexChunk,16);
            BigInteger cbcNum = getCBCNumber(hexChunk.length(), cbcChunk);
            return (numChunk.xor(cbcNum)).toString(16);
        }
    }

    private BigInteger getCBCNumber(int length, String cbcChunk)
    {
        if(cbcChunk == null)
        {
            Random random = new Random(23444342L);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++)
            {
                int randomByte = random.nextInt(16); // Get a random value between 0 and 15
                sb.append(Integer.toHexString(randomByte));
            }
            System.out.println("nonce=" + sb);
            return new BigInteger(sb.toString(), 16);
        }
        else if(cbcChunk.length() == length)
            return new BigInteger(cbcChunk, 16);
        else if(cbcChunk.length() > length)
            return new BigInteger(cbcChunk.substring(1, length), 16);
        else
        {
            StringBuilder builder = new StringBuilder();
            builder.append(cbcChunk);
            int i = 0;
            while(builder.length() < length)
            {
                builder.append(cbcChunk.charAt(i % length));
                i++;
            }
            return new BigInteger(builder.toString(), 16);
        }
    }
}