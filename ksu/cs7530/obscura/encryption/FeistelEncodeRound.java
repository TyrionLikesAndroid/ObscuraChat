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

    BigInteger transform(BigInteger input, int iter)
    {
        System.out.println("Encode transform input = " + input.toString(2));

        BigInteger rightChunk = input.and(BigInteger.valueOf(4294967295L));
        BigInteger leftChunk = input.shiftRight(32);

        System.out.println("LE" + iter + ": " + leftChunk.toString(2) +
                " RE" + iter +": " + rightChunk.toString(2));

        BigInteger rightChunkAfterFcn = function.transform(rightChunk, roundKey);
        BigInteger rightChunkAfterXor = leftChunk.xor(rightChunkAfterFcn);

        BigInteger output = rightChunk.shiftLeft( 32);
        output = output.or(rightChunkAfterXor);

        System.out.println("Encode transform output: " + output.toString(2));

        return output;
    }
}