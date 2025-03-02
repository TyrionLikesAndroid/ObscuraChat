package ksu.cs7530.obscura.encryption;

public class PrivateKeyCryptosystem {

    protected FeistelCipher cipher;
    protected String hexKey;

    public PrivateKeyCryptosystem(FeistelCipher cipher, String hexKey)
    {
        this.cipher = cipher;
        this.hexKey = hexKey;
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

    public static String hexStringToBinary(String hexString)
    {
        if (hexString == null || hexString.isEmpty())
        {
            return "Invalid hexadecimal string";
        }

        try {
            // Remove prefix if present
            if (hexString.startsWith("0x") || hexString.startsWith("0X"))
            {
                hexString = hexString.substring(2);
            }

            StringBuilder binaryString = new StringBuilder();
            for (int i = 0; i < hexString.length(); i++)
            {
                char hexChar = hexString.charAt(i);
                int decimal = Integer.parseInt(String.valueOf(hexChar), 16);
                String binary = Integer.toBinaryString(decimal);
                // Pad with leading zeros to 4 bits
                binaryString.append(String.format("%4s", binary).replace(' ', '0'));
            }
            return binaryString.toString();

        }
        catch (NumberFormatException e)
        {
            return "Invalid hexadecimal string";
        }
    }
}