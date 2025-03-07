package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public interface FeistelFFunction {

    public BigInteger transform(BigInteger input, long key);
}
