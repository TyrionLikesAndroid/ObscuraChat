package ksu.cs7530.obscura.encryption;

public class SimpleFFunction implements FeistelFFunction {
    public int transform(int input, int key)
    {
        return Math.abs(input - key);
    }
}
