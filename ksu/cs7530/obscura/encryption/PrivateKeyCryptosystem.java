package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;

public interface PrivateKeyCryptosystem {

    public String encrypt(String message);
    public String decrypt(String message);

    public BigInteger performInitialPermutation(BigInteger input);
    public BigInteger performFinalPermutation(BigInteger input);
}