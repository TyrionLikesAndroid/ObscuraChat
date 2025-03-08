package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public class DESFFunction implements FeistelFFunction {

    static private final byte[][] EXPANSION = { {2, 48}, {3}, {4}, {5, 7},
                                                {6, 8}, {9}, {10}, {11, 13},
                                                {12, 14}, {15}, {16}, {17, 19},
                                                {18, 20}, {21}, {22}, {23, 25},
                                                {24, 26}, {27}, {28}, {29, 31},
                                                {30, 32}, {33}, {34}, {35, 37},
                                                {36, 38}, {39}, {40}, {41, 43},
                                                {42, 44}, {45}, {46}, {47, 1}};

    static private final byte[][] SUB_1 =   {   {14, 0, 4, 15}, {4, 15, 1, 12}, {13, 7, 14, 8}, {1, 4, 8, 2},
                                                {2, 14, 13, 4}, {15, 2, 6, 9}, {11, 13, 2, 1}, {8, 1, 11, 7},
                                                {3, 10, 15, 5}, {10, 6, 12, 11}, {6, 12, 9, 3}, {12, 11, 7, 14},
                                                {5, 9, 3, 10}, {9, 5, 10, 0}, {0, 3, 5, 6}, {7, 8, 0, 13} };
    static private final byte[][] SUB_2 =   {   {15, 3, 0, 13}, {1, 13, 14, 8}, {8, 4, 7, 10}, {14, 7, 11, 1},
                                                {6, 15, 10, 3}, {11, 2, 4, 15}, {3, 8, 13, 4}, {4, 14, 1, 2},
                                                {9, 12, 5, 11}, {7, 0, 8, 6}, {2, 1, 12, 7}, {13, 10, 6, 12},
                                                {12, 6, 9, 0}, {0, 9, 3, 5}, {5, 11, 2, 14}, {10, 5, 15, 9} };
    static private final byte[][] SUB_3 =   {   {10, 13, 13, 1}, {0, 7, 6, 10}, {9, 0, 4, 13}, {14, 9, 9, 0},
                                                {6, 3, 8, 6}, {3, 4, 15, 9}, {15, 6, 3, 8}, {5, 10, 0, 7},
                                                {1, 2, 11, 4}, {13, 8, 1, 15}, {12, 5, 2, 14}, {7, 14, 12, 3},
                                                {11, 12, 5, 11}, {4, 11, 10, 5}, {2, 15, 14, 2}, {8, 1, 7, 12} };
    static private final byte[][] SUB_4 =   {   {7, 13, 10, 3}, {13, 8, 6, 15}, {14, 11, 9, 0}, {3, 5, 0, 6},
                                                {0, 6, 12, 10}, {6, 15, 11, 1}, {9, 0, 7, 13}, {10, 3, 13, 8},
                                                {1, 4, 15, 9}, {2, 7, 1, 4}, {8, 2, 3, 5}, {5, 12, 14, 11},
                                                {11, 1, 5, 12}, {12, 10, 2, 7}, {4, 14, 8, 2}, {15, 9, 4, 14} };
    static private final byte[][] SUB_5 =   {   {2, 14, 4, 11}, {12, 11, 2, 8}, {4, 2, 1, 12}, {1, 12, 11, 7},
                                                {7, 4, 10, 1}, {10, 7, 13, 14}, {11, 13, 7, 2}, {6, 1, 8, 13},
                                                {8, 5, 15, 6}, {5, 0, 9, 15}, {3, 15, 12, 0}, {15, 10, 5, 9},
                                                {13, 3, 6, 10}, {0, 9, 3, 4}, {14, 8, 0, 5}, {9, 6, 14, 3} };
    static private final byte[][] SUB_6 =   {   {12, 10, 9, 4}, {1, 15, 14, 3}, {10, 4, 15, 2}, {15, 2, 5, 12},
                                                {9, 7, 2, 9}, {2, 12, 8, 5}, {6, 9, 12, 15}, {8, 5, 3, 10},
                                                {0, 6, 7, 11}, {13, 1, 0, 14}, {3, 13, 4, 1}, {4, 14, 10, 7},
                                                {14, 0, 1, 6}, {7, 11, 13, 0}, {5, 3, 11, 8}, {11, 8, 6, 13} };
    static private final byte[][] SUB_7 =   {   {4, 13, 1, 6}, {11, 0, 4, 11}, {2, 11, 11, 13}, {14, 7, 13, 8},
                                                {15, 4, 12, 1}, {0, 9, 3, 4}, {8, 1, 7, 10}, {13, 10, 14, 7},
                                                {3, 14, 10, 9}, {12, 3, 15, 5}, {9, 5, 6, 0}, {7, 12, 8, 15},
                                                {5, 2, 0, 14}, {10, 15, 5, 2}, {6, 8, 9, 3}, {1, 6, 2, 12} };
    static private final byte[][] SUB_8 =   {   {13, 1, 7, 2}, {2, 15, 11, 1}, {8, 13, 4, 14}, {4, 8, 1, 7},
                                                {6, 10, 9, 4}, {15, 3, 12, 10}, {11, 7, 14, 8}, {1, 4, 2, 13},
                                                {10, 12, 0, 15}, {9, 5, 6, 12}, {3, 6, 10, 9}, {14, 11, 13, 0},
                                                {5, 0, 15, 3}, {0, 14, 3, 5}, {12, 9, 5, 6}, {7, 2, 8, 11} };

    static private final byte[][][] SUB_TABLE = { SUB_1, SUB_2, SUB_3, SUB_4, SUB_5, SUB_6, SUB_7, SUB_8 };

    static private final byte[] PERMUTATION = { 16, 7, 20, 21, 29, 12, 28, 17,
                                                1, 15, 23, 26, 5, 18, 31, 10,
                                                2, 8, 24, 14, 32, 27, 3, 9,
                                                19, 13, 30, 6, 22, 11, 4, 25 };

    public BigInteger transform(BigInteger input, long key)
    {
        //System.out.println("DES F-function input: " + input.toString(2));

        BigInteger expansionResult = BigInteger.ZERO;
        BigInteger biKey = BigInteger.valueOf(key);

        for(int i = 0; i < EXPANSION.length; i++)
        {
            BigInteger maskResult = input.and(BigInteger.ONE.shiftLeft(31 - i));
            //System.out.println("DES F-function mask result[" + i + "] = " + maskResult);

            if(maskResult.compareTo(BigInteger.ZERO) > 0)
            {
                byte[] innerArray = EXPANSION[i];
                for(int j = 0; j < innerArray.length; j++)
                {
                    expansionResult = expansionResult.or(BigInteger.ONE.shiftLeft(48 - innerArray[j]));
                    //System.out.println("DES F-function expansion step: " + expansionResult.toString(2));
                }
            }
        }

        //System.out.println("DES F-function after expansion: " + expansionResult.toString(2));

        // XOR the key and the expansion
        BigInteger xorComplete = expansionResult.xor(biKey);
        //System.out.println("DES F-function after XOR: " + xorComplete.toString(2));

        // Build a byte array to hold all the S chunks as we chop up.  We will chop from the
        // right, that way if we are missing any zeros to the left the default is already zero
        byte[] sBytes = new byte[8];
        BigInteger subResult = BigInteger.ZERO;
        for(int i = 0; i < sBytes.length; i++)
        {
            int index = sBytes.length - i - 1;
            BigInteger maskResult = xorComplete.and(BigInteger.valueOf(63L << i * 6));
            sBytes[index] = maskResult.shiftRight(i * 6).byteValue();
            //System.out.println("DES F-function sByte[" + index + "] = " + Long.toBinaryString(sBytes[index]));

            // Calculate the column and the row
            int row = ((sBytes[index] & (1 << 5)) >> 4) | (sBytes[index] & 1);
            int column = (sBytes[index] & 30) >> 1;
            //System.out.println("DES F-function sByte row = " + row + " column = " + column);

            // Index the substitution map for the correct transformation for each chunk and
            // build the output number
            byte subByte = SUB_TABLE[index][column][row];
            BigInteger subChunk = BigInteger.valueOf(subByte);
            //System.out.println("DES F-function subChunk[" + index + "] = " + subChunk.toString(2));

            subResult = subResult.or(subChunk.shiftLeft(i * 4));
            //System.out.println("DES F-function substitution step: " + subResult.toString(2));
        }

        //System.out.println("DES F-function after substitution = " + subResult.toString(2));

        // Do the final permutation and return back to cipher
        BigInteger permutationResult = BigInteger.ZERO;
        for(int i = 0; i < PERMUTATION.length; i++)
        {
            if(subResult.testBit(32 - PERMUTATION[i]))
            {
                //System.out.println("DES F-function mask result[" + i + "] = 1");
                permutationResult = permutationResult.setBit(31 - i);
                //System.out.println("DES F-function permutation step: " + permutationResult.toString(2));
            }
        }

        //System.out.println("DES F-function after permutation = " + permutationResult.toString(2));

        return permutationResult;
    }
}