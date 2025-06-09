package encryption.chacha20;

import encryption.EncryptionStrategy;
import encryption.factory.EncryptionCreator;

public class ChaCha20EncryptionCreator extends EncryptionCreator {

    @Override
    public EncryptionStrategy createEncryption() {
        System.out.println("Creando estrategia ChaCha20-Poly1305");
        return new EncryptionChaCha20();
    }

    @Override
    public String encryptMessage(String mensaje, String secretPhrase) {
        if (!validatePassphrase(secretPhrase)) {
            throw new IllegalArgumentException("ChaCha20 requiere passphrase de al menos 8 caracteres");
        }
        return super.encryptMessage(mensaje, secretPhrase);
    }
}
