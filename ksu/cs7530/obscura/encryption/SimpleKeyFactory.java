package ksu.cs7530.obscura.encryption;

public class SimpleKeyFactory implements KeyFactory{

    public long[] createKeySchedule(String hexKey)
    {
        long[] keySchedule = new long[16];
        long root = Long.parseLong(hexKey, 16);

        for(int i = 0; i < 16; i++)
        {
            System.out.println("Schedule loop[" + i + "] input = " + Long.toBinaryString(root));
            long newKey = root << 1;
            keySchedule[i] = newKey;
            root = newKey;
            System.out.println("Schedule loop[" + i + "] output = " + Long.toBinaryString(newKey));
        }

        return keySchedule;
    }
}