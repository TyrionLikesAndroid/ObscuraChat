package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public class SimpleFFunction implements FeistelFFunction {

    public BigInteger transform(BigInteger input, long key)
    {
        int result = input.intValue() / ((int) ((key % 32L) + 1));
        return BigInteger.valueOf(result);
    }
}