package ksu.cs7530.obscura.encryption;

public class FeistelDecryptRound {

    private long roundKey;
    private FeistelFFunction function;

    FeistelDecryptRound(long key, FeistelFFunction function)
    {
        this.roundKey = key;
        this.function = function;
    }

    long transform(long input)
    {
        return input;
    }
}
