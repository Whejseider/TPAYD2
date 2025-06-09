package encryption;

public interface EncryptionStrategy {
    String encrypt(String mensaje, String secretPhrase);
    String decrypt(String mensaje, String secretPhrase);
}
