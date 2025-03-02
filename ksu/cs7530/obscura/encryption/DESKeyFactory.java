package ksu.cs7530.obscura.encryption;

public class DESKeyFactory implements KeyFactory {

    static private final byte[] PC1 = { 57, 49, 41, 33, 25, 17, 9, 1,
                                        58, 50, 42, 34, 26, 18, 10, 2,
                                        59, 51, 43, 35, 27, 19, 11, 3,
                                        60, 52, 44, 36, 63, 55, 47, 39,
                                        31, 23, 15, 7, 62, 54, 46, 38,
                                        30, 22, 14, 6, 61, 53, 45, 37,
                                        29, 21, 13, 5, 28, 20, 12, 4};

    static private final byte[] PC2 = { 14, 17, 11, 24, 1, 5, 3, 28,
                                        15, 6, 21, 10, 23, 19, 12, 4,
                                        26, 8, 16, 7, 27, 20, 13, 2,
                                        41, 52, 31, 37, 47, 55, 30, 40,
                                        51, 45, 33, 48, 44, 49, 39, 56,
                                        34, 53, 46, 42, 50, 36, 29, 32};

    static private final byte[] SHIFT_TABLE = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

    public long[] createKeySchedule(String hexKey)
    {
        //System.out.println("DES Key Input = " + hexKey);

        String binaryKey = PrivateKeyCryptosystem.hexStringToBinary(hexKey);
        //System.out.println("Binary Conversion = " + binaryKey);

        // Perform the PC1 permutation on the original string form.
        char[] binaryAfterPC1Str = new char[56];
        for(int i = 0; i < 56; i++)
            binaryAfterPC1Str[i] = binaryKey.charAt(PC1[i] - 1);

        // Convert it back to a long now that we know it will fit in a Java primitive
        long binaryAfterPC1Num = Long.parseLong(new String(binaryAfterPC1Str), 2);
        //System.out.println("Binary Key After PC1 = " + Long.toBinaryString(binaryAfterPC1Num));

        // Initialize C0 and D0
        int cValue = (int) (binaryAfterPC1Num >> 28);
        int dValue = (int) (binaryAfterPC1Num & 268435455L);

        //System.out.println("C0 = " + Long.toBinaryString(cValue) +
        //        " D0 = " + Long.toBinaryString(dValue));

        // Loop through our shift array and make the C and D arrays
        int[] cArray = new int[SHIFT_TABLE.length];
        int[] dArray = new int[SHIFT_TABLE.length];
        for(int i = 0; i < SHIFT_TABLE.length; i++)
        {
            // Use our wrapping shift function to keep everything aligned
            cArray[i] = wrapShiftFit28(cValue, SHIFT_TABLE[i]);
            dArray[i] = wrapShiftFit28(dValue, SHIFT_TABLE[i]);

            cValue = cArray[i];
            dValue = dArray[i];
            //System.out.println("C" + (i+1) +" = " + Long.toBinaryString(cValue) +
            //        " D" + (i+1) + " = " + Long.toBinaryString(dValue));
        }

        long[] keySchedule = new long[16];
        String joinCWithDRaw = "";

        // Loop through our C and D arrays to construct the final key schedule
        for(int i = 0; i <  SHIFT_TABLE.length; i++)
        {
            // Pad any zeros that might be needed to get us to 28 bits per side
            joinCWithDRaw = padZeroToFit28(Long.toBinaryString(cArray[i])) +
                    padZeroToFit28(Long.toBinaryString(dArray[i]));
            System.out.println("Joined C+D = " + joinCWithDRaw);

            // Perform the PC2 permutation on the joined string
            char[] binaryAfterPC2Str = new char[48];
            for(int j = 0; j < 48; j++)
                binaryAfterPC2Str[j] = joinCWithDRaw.charAt(PC2[j] - 1);

            // Convert it back to a long
            long binaryAfterPC2Num = Long.parseLong(new String(binaryAfterPC2Str), 2);
            keySchedule[i] = binaryAfterPC2Num;
            System.out.println("K[" + i + "] = " + Long.toBinaryString(binaryAfterPC2Num));
        }

        return keySchedule;
    }

    private int wrapShiftFit28(int input, byte shiftValue)
    {
        int output = input;
        byte numToShift = shiftValue;

        // This is a wrapping shift loop.  if we can shift without needed to wrap, proceed
        // as normal but if we need to wrap to shift add the values on the right side
        while(numToShift > 0)
        {
            boolean isBit28Active = (output & 1 << 27) > 0;
            if(isBit28Active)
            {
                output = output ^ (output & 1 << 27);
                output = output << 1;
                output = output | 1;
            }
            else
                output = output << 1;

            numToShift--;
        }

        return output;
    }

    private String padZeroToFit28(String input)
    {
        StringBuilder output = new StringBuilder(input);

        int padNeeded = 28 - output.length();
        for(int i = 0; i < padNeeded; i++)
            output.insert(0, "0");

        return output.toString();
    }
}