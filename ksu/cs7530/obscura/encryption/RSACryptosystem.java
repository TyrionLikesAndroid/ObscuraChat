package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class RSACryptosystem {

    RSAKeyFactory.RSAKeyTriad keys;

    public RSACryptosystem(RSAKeyFactory.RSAKeyTriad keys)
    {
        this.keys = keys;
        System.out.println("RSACryptosystem constructed");
    }

    public String encrypt(String message)
    {
        return "";
    }

    public String decrypt(String message)
    {
       return "";
    }

    private static BigInteger generatePrimeCandidate()
    {
        Random random = new SecureRandom();
        BigInteger primeCandidate;

        do { primeCandidate = new BigInteger(512, 100, random); }
        while (! primeCandidate.isProbablePrime(100));

        return primeCandidate;
    }

    public static void main(String[] args)
    {
        BigInteger primeCandidate1 = BigInteger.ZERO;
        BigInteger primeCandidate2 = BigInteger.ZERO;
        RSAKeyFactory.RSAKeyTriad keys = null;

        do
        {
            primeCandidate1 = RSACryptosystem.generatePrimeCandidate();
            primeCandidate2 = RSACryptosystem.generatePrimeCandidate();

            try { keys = new RSAKeyFactory().createRsaKeys(primeCandidate1, primeCandidate2); }
            catch(ArithmeticException e)
            {
                System.out.println("Got a math exception, phi and e are likely not relatively prime, so retry\n");
            }

        } while(keys == null);

        System.out.println(keys.toString());

        RSACryptosystem crypto = new RSACryptosystem(keys);

        //String plainText = "0123456789ABCDEF";
        String plainText = "I pledge allegiance to the flag of the United States of your momma";
        System.out.println("Original string = " + plainText);
        String encrypted = crypto.encrypt(plainText);
        System.out.println("Encrypted string = " + encrypted);
        String decrypted = crypto.decrypt(encrypted);
        System.out.println("Decrypted string = " + decrypted);
    }
}