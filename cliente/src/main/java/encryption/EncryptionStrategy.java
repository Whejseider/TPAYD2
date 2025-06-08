package encryption;

public interface EncryptionStrategy {
    String encrypt(String mensaje);

    String decrypt(String mensaje);
}
