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
        System.out.println("Encrypt transform: " + Long.toBinaryString(input));

        int chunkRE0 = (int) (input & 4294967295L);
        int chunkLE0 = (int) (input >> 32L);

        System.out.println("LE0: " + Long.toBinaryString(chunkLE0) + " RE0: " + Long.toBinaryString(chunkRE0));

        return input;
    }
}
