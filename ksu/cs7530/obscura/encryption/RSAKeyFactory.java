package ksu.cs7530.obscura.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Random;

public class RSAKeyFactory {

    static public class RSAKeyTriad
    {
        public BigInteger publicKey;
        public BigInteger privateKey;
        public BigInteger n;

        @Override
        public String toString() {
            return "RSAKeyTriad{" +
                    "publicKey=" + publicKey +
                    ", privateKey=" + privateKey +
                    ", n=" + n +
                    '}';
        }

        public RSAKeyTriad(BigInteger pubKey, BigInteger privKey, BigInteger n)
        {
            this.publicKey = pubKey;
            this.privateKey = privKey;
            this.n = n;
        }
    }

    public static RSAKeyFactory.RSAKeyTriad generateRSAKeyset()
    {
        BigInteger primeCandidate1;
        BigInteger primeCandidate2;
        RSAKeyFactory.RSAKeyTriad keys = null;

        do
        {
            primeCandidate1 = RSAKeyFactory.generatePrimeCandidate();
            primeCandidate2 = RSAKeyFactory.generatePrimeCandidate();

            try { keys = createRsaKeys(primeCandidate1, primeCandidate2); }
            catch(ArithmeticException e)
            {
                System.out.println("Got a math exception, phi and e are likely not relatively prime, so retry\n");
            }

        } while(keys == null);

        System.out.println(keys.toString());
        return keys;
    }

    private static RSAKeyTriad createRsaKeys(BigInteger p, BigInteger q)
    {
        RSAKeyTriad out = new RSAKeyTriad(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);

        if(p.isProbablePrime(100) && q.isProbablePrime(100))
        {
            //System.out.println("p(" + p + ") and q(" + q + ") are highly likely to be prime.");

            BigInteger n = p.multiply(q);
            //System.out.println("Calculated n(" + n + ")");

            BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
            //System.out.println("Calculated phi(" + phi + ")");

            // Choose 17 for our public key since it's a popular choice
            BigInteger e = new BigInteger("17");

            // Calculate e by using Euclid's algorithms
            LinkedList<BigInteger> quotientList = new LinkedList<>();
            BigInteger gcd = euclideanAlgorithm(e, phi, quotientList);
            //System.out.println("GCD(e,phi) = " + gcd);

            BigInteger invModulus = e.modInverse(phi);
            //System.out.println("Inv Modulus GCD(e,phi) = " + invModulus);

            out.publicKey = e;
            out.privateKey = invModulus;
            out.n = n;
        }

        return out;
    }

    private static BigInteger generatePrimeCandidate()
    {
        Random random = new SecureRandom();
        BigInteger primeCandidate;

        do { primeCandidate = new BigInteger(512, 100, random); }
        while (! primeCandidate.isProbablePrime(100));

        return primeCandidate;
    }

    private static BigInteger euclideanAlgorithm(BigInteger original_a, BigInteger original_b, LinkedList<BigInteger> quotientList)
    {
        System.out.println();
        quotientList.clear();

        // Special rule for zero
        if(original_a.equals(BigInteger.ZERO))
            return original_b.abs();
        else if(original_b.equals(BigInteger.ZERO))
            return original_a.abs();

        // Initialize variables for the non-zero cases
        BigInteger a = original_a.abs(); BigInteger b = original_b.abs(); int step = 1;
        BigInteger remainder = new BigInteger("99");
        BigInteger quotient = new BigInteger("99");

        // Flip the values if b is larger than a
        if(a.compareTo(b) < 0) { a = original_b.abs(); b = original_a.abs(); }

        // Run the Euclidean algorithm loop until we have a remainder of zero.  The last non-zero remainder
        // is the final answer
        while(! remainder.equals(BigInteger.ZERO)) {

            BigInteger[] values = a.divideAndRemainder(b);
            quotient = values[0];
            remainder = values[1];
            quotientList.add(quotient);

            //System.out.println("Step(" + step + "): quotient=" + quotient + " remainder=" + remainder);

            a = b;
            b = remainder;
            step++;
        }
        return a;
    }
}