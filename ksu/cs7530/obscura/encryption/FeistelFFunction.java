package ksu.cs7530.obscura.encryption;

public interface FeistelFFunction {

    public int transform(int input, int key);
}
