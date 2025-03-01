package ksu.cs7530.obscura.encryption;

public class SimpleFFunction implements FeistelFFunction {
    public int transform(int input, long key)
    {
        return input / ((int) ((key % 32L) + 1));
    }
}
