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
        return input;
    }
}
