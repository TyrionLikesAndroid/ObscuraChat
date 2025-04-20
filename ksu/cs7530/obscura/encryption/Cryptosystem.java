package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;
import java.util.Random;

public class Cryptosystem {

    static public String CHAT_OPERATIONAL_MODE_CBC = "CBC";
    static public String CHAT_OPERATIONAL_MODE_ECB = "ECB";
    static public String CHAT_OPERATIONAL_MODE_CTR = "CTR";

    // 2K character test string for performance testing
    static protected String PERFORMANCE_TEST_MSG =
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

    protected String operationalMode = CHAT_OPERATIONAL_MODE_ECB;
    private String previousOut = null;
    private String previousIn = null;
    private final Random ctrInRandom = new Random(7867787L);
    private final Random ctrOutRandom = new Random(7867787L);

    public void setPreviousOut(String str)
    {
        this.previousOut = str;
        //boolean isBinary = str.matches("[01]+");
        //String hexStr = isBinary ? new BigInteger(str,2).toString(16) : str;
        //System.out.println("Setting previousOut = " + hexStr);
    }

    public void setPreviousIn(String str)
    {
        this.previousIn = str;
        //boolean isBinary = str.matches("[01]+");
        //String hexStr = isBinary ? new BigInteger(str,2).toString(16) : str;
        //System.out.println("Setting previousIn = " + hexStr);
    }

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

        postOpModeChange();
    }

    protected void postOpModeChange()
    {
        // Do nothing
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

    public String cbcTransform(String chunk, boolean inFlag, int radix)
    {
        if(! operationalMode.equals(CHAT_OPERATIONAL_MODE_CBC))
            return chunk;
        else
        {
            String cbcChunk = inFlag ? previousIn : previousOut;

            BigInteger numChunk = new BigInteger(chunk, radix);
            //System.out.println("chunk = " + numChunk.toString(16));

            BigInteger cbcNum = getCBCNumber(chunk.length(), cbcChunk, radix);
            //System.out.println("cbcChunk = " + cbcNum.toString(16));

            BigInteger xform = numChunk.xor(cbcNum);
            //System.out.println("xform = " + xform.toString(16));

            //if(inFlag)
                //System.out.println("plain = " + DESCryptosystem.hexToString(xform.toString(16)));

            return xform.toString(radix);
        }
    }

    private BigInteger getCBCNumber(int length, String cbcChunk, int radix)
    {
        String paddedChunk = cbcChunk;

        // Just for debugging
        if(paddedChunk != null)
        {
            //boolean isBinary = cbcChunk.matches("[01]+");
            //String hexStr = isBinary ? new BigInteger(cbcChunk, 2).toString(16) : cbcChunk;
            //System.out.println("len=" + length + ", cbcChunk=" + hexStr + ", radix=" + radix);

            paddedChunk = DESCryptosystem.padLeadingZerosToFit(cbcChunk,length);
        }

        if(paddedChunk == null)
        {
            Random random = new Random(23444342L);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++)
            {
                int randomByte = random.nextInt(16); // Get a random value between 0 and 15
                sb.append(Integer.toHexString(randomByte));
            }
            //System.out.println("nonce=" + sb);
            return new BigInteger(sb.toString(), 16);
        }
        else if(paddedChunk.length() == length)
        {
            //System.out.println("got here 1");
            return new BigInteger(paddedChunk, radix);
        }
        else if(paddedChunk.length() > length)
        {
            //System.out.println("got here 2");
            return new BigInteger(paddedChunk.substring(1, length), radix);
        }
        else
        {
            //System.out.println("got here 3");

            StringBuilder builder = new StringBuilder();
            builder.append(paddedChunk);
            int i = 0;
            while(builder.length() < length)
            {
                builder.append(paddedChunk.charAt(i % length));
                i++;
            }
            return new BigInteger(builder.toString(), radix);
        }
    }

    public String ctrNextCounter(int length, int radix, boolean inFlag)
    {
        Random random = inFlag ? ctrInRandom : ctrOutRandom;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            int randomByte = random.nextInt(radix); // Get a random value between 0 and radix
            String randomStr = (radix == 2) ? Integer.toString(randomByte) : Integer.toHexString(randomByte);
            sb.append(randomStr);
        }
        //System.out.println("ctr[" + inFlag + "] =" + sb);
        return sb.toString();
    }
}