package ksu.cs7530.obscura.encryption;

public class Cryptosystem {

    public String encrypt(String message)
    {
        System.out.println("super encrypt called = " + message);
        return message;
    }

    public String decrypt(String message)
    {
        System.out.println("super decrypt called = " + message);
        return message;
    }
}