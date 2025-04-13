package ksu.cs7530.obscura.encryption;

public interface Cryptosystem {

    public String encrypt(String message);
    public String decrypt(String message);
}