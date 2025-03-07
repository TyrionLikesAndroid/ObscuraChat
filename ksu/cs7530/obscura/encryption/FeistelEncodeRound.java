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
        System.out.println("Encode transform input = " + input.toString(2));
        BigInteger chunkRE0 = input.and(BigInteger.valueOf(4294967295L));
        BigInteger chunkLE0 = input.shiftRight(32);

        System.out.println("LE0: " + chunkLE0.toString(2) + " RE0: " + chunkRE0.toString(2));

        BigInteger chunkRE0AfterF = function.transform(chunkRE0, roundKey);
        BigInteger chunkRE0AfterFAfterXor = chunkLE0.xor(chunkRE0AfterF);

        BigInteger output = chunkRE0.shiftLeft( 32);
        output = output.or(chunkRE0AfterFAfterXor);

        System.out.println("Encode transform output: " + output.toString(2));

        return output;
    }
}