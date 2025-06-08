package encryption.aes;

public class LocalAES {
    public static final String AES_ALGORITHM = "AES";
    public static final String AES_ALGORITHM_GCM = "AES/GCM/NoPadding";
    public static final int IV_LENGTH = 12; // 96 bits
    public static final int TAG_LENGTH = 128; // bits
    public static final int SALT_LENGTH = 16; // 128 bits
    public static final int PBKDF2_ITERATIONS = 65536;
    public static final int KEY_LENGTH = 256; // bits

}
