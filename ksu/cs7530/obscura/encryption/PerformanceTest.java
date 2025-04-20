package ksu.cs7530.obscura.encryption;

import java.util.*;

public class PerformanceTest {
    public static void main(String[] args)
    {
        LinkedList<String> testList = new LinkedList<>();
        testList.add(Cryptosystem.PERFORMANCE_TEST_MSG);

        // Build our test string list
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i <= 5; i++)
        {
            for(int j = 1; j <= 8; j++)
                sb.append(Cryptosystem.PERFORMANCE_TEST_MSG);

            testList.add(sb.toString());
        }

        // Setup our cryptosystems
        HashMap<String, Cryptosystem> cryptoMap = new HashMap<String,Cryptosystem>();
        cryptoMap.put("DES", new DESCryptosystem("DEADBEEFDEADBEEF"));
        cryptoMap.put("3DES", new TripleDESCryptosystem("DEADBEEFDEADBEEF0123456789ABCDEFDEAD01234567BEEF"));
        RSACryptosystem rsa = new RSACryptosystem();
        rsa.remoteKeys = rsa.localKeys;
        cryptoMap.put("RSA", rsa);

        // Setup or operational modes
        String[] modes = {"ECB", "CBC", "CTR"};
        LinkedList<String> modeList = new LinkedList<>(Arrays.asList(modes));

        // Main performance test loop
        for (Map.Entry<String, Cryptosystem> entry : cryptoMap.entrySet())
        {
            String cryptoName = entry.getKey();
            Cryptosystem crypto = entry.getValue();

            // Loop the operational modes
            for (String testMode : modeList)
            {
                crypto.setOperationalMode(testMode);

                // Loop the test messages
                for (String test : testList)
                {
                    int numIterations = 20;
                    long startEncrypt = System.currentTimeMillis();

                    // Loop the iterations so we can take an average
                    for (int i = 1; i <= numIterations; i++)
                    {
                        String encrypted = crypto.encrypt(test, false);
                        String decrypted = crypto.decrypt(encrypted, false);

                        // Fall out if we aren't getting valid results
//                        if(! test.equals(decrypted))
//                        {
//                            System.out.println("Existing test on error \ne=" + encrypted + "\nd=" + decrypted);
//                            return;
//                        }
                    }
                    long decryptTime = System.currentTimeMillis() - startEncrypt;

                    System.out.println(cryptoName + "[" + testMode + "]," + test.getBytes().length +
                            "," + decryptTime / numIterations);
                }
            }
        }
    }
}