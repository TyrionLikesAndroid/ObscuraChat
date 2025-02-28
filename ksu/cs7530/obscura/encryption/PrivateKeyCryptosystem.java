package ksu.cs7530.obscura.encryption;

public class PrivateKeyCryptosystem {

    protected FeistelCipher cipher;

    public PrivateKeyCryptosystem(FeistelCipher cipher)
    {
        this.cipher = cipher;
    }

    public String encrypt(String message)
    {
        System.out.println("encrypt - NOT IMPLEMENTED");
        return "";
    }
    public String decrypt(String message)
    {
        System.out.println("decrypt - NOT IMPLEMENTED");
        return "";
    }

}
