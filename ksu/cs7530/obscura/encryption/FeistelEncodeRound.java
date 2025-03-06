package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public class FeistelEncodeRound {

    private final long roundKey;
    private final FeistelFFunction function;

    FeistelEncodeRound(long key, FeistelFFunction function)
    {
        this.roundKey = key;
        this.function = function;
    }

    BigInteger transform(BigInteger input)
    {
        int chunkRE0 = input.and(BigInteger.valueOf(4294967295L)).intValue();
        int chunkLE0 = input.shiftRight(32).intValue();

        //System.out.println("LE0: " + Long.toBinaryString(chunkLE0) + " RE0: " + Long.toBinaryString(chunkRE0));

        int chunkRE0AfterF = function.transform(chunkRE0, roundKey);
        int chunkRE0AfterFAfterXor = chunkLE0 ^ chunkRE0AfterF;

        long output = ((long) chunkRE0) << 32L;
        output = output | chunkRE0AfterFAfterXor;

        //System.out.println("Encrypt transform output: " + Long.toBinaryString(output));

        return BigInteger.valueOf(output);
    }
}