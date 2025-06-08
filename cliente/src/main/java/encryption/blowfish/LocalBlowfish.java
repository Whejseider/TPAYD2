package encryption.blowfish;

public class LocalBlowfish {
    public static final String ALGORITHM = "Blowfish";
    public static final String CIPHER_MODE = "Blowfish/CBC/PKCS5Padding";
    public static final int IV_LENGTH = 8; // Blowfish uses 64-bit block = 8 bytes
}
