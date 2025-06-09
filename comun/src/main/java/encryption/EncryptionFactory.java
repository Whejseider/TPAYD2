package encryption;

import encryption.aes.EncryptionAES;
import encryption.blowfish.EncryptionBlowfish;
import encryption.chacha20.EncryptionChaCha20;

public class EncryptionFactory {

    public static EncryptionStrategy getEncryptation(EncryptionType type) {

        return switch (type) {
            case AES_GCM -> {
                System.out.println("Encriptación AES_GCM.");
                yield new EncryptionAES();
            }
            case CHACHA20 -> {
                System.out.println("Encriptación ChaCha20-poly1305");
                yield new EncryptionChaCha20();
            }
            case BLOWFISH -> {
                System.out.println("Encriptación Blowfish CBC.");
                yield new EncryptionBlowfish();
            }

        };
    }
}
