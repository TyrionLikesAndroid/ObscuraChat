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

    BigInteger transform(BigInteger input, int iter, boolean encryptFlag)
    {
        //System.out.println("Encode transform input = " + input.toString(2));
        String direction = encryptFlag ? "E" : "D";

        BigInteger rightChunk = input.and(BigInteger.valueOf(4294967295L));
        BigInteger leftChunk = input.shiftRight(32);

        if(iter == 0)
        {
            //System.out.println("L" + direction + iter + ": " + leftChunk.toString(2) +
                //    " R" + direction + iter + ": " + rightChunk.toString(2));
        }

        BigInteger rightChunkAfterFcn = function.transform(rightChunk, roundKey);
        BigInteger rightChunkAfterXor = leftChunk.xor(rightChunkAfterFcn);

        BigInteger output = rightChunk.shiftLeft( 32);
        output = output.or(rightChunkAfterXor);

       // System.out.println("L" + direction + (iter + 1) + ": " + rightChunk.toString(2) +
            //    " R" + direction + (iter + 1) +": " + rightChunkAfterXor.toString(2) +
            //    " K" + (iter + 1) + ": " + roundKey);

        //System.out.println("Encode transform output: " + output.toString(2));

        return output;
    }
}