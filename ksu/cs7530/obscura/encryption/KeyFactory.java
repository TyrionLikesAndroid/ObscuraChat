package ksu.cs7530.obscura.encryption;

public interface KeyFactory {

    public long[] createKeySchedule(String hexKey);
}
