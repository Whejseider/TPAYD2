package encryption.chacha20;

public class LocalChaCha20 {
    public static final String CHACHA20_POLY1305 = "ChaCha20-Poly1305";
    public static final String DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    public static final String KEY_ALGO = "ChaCha20";
    public static final int NONCE_LENGTH = 12; // 96 bits
    public static final int SALT_LENGTH = 16;  // 128 bits
    public static final int KEY_LENGTH = 256;  // bits
    public static final int PBKDF2_ITERATIONS = 65536;
}
