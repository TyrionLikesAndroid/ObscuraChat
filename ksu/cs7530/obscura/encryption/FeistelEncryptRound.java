package ksu.cs7530.obscura.encryption;

public class FeistelEncryptRound {

    private long roundKey;
    private FeistelFFunction function;

    FeistelEncryptRound(long key, FeistelFFunction function)
    {
        this.roundKey = key;
        this.function = function;
    }

    long transform(long input)
    {
        int chunkRE0 = (int) (input & 4294967295L);
        int chunkLE0 = (int) (input >> 32L);

        //System.out.println("LE0: " + Long.toBinaryString(chunkLE0) + " RE0: " + Long.toBinaryString(chunkRE0));

        int chunkRE0AfterF = function.transform(chunkRE0, roundKey);
        int chunkRE0AfterFAfterXor = chunkLE0 ^ chunkRE0AfterF;

        long output = ((long) chunkRE0) << 32L;
        output = output | chunkRE0AfterFAfterXor;

        //System.out.println("Encrypt transform output: " + Long.toBinaryString(output));

        return output;
    }
}
