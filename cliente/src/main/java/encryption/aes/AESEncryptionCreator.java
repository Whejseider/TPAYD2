package encryption.aes;

import encryption.EncryptionStrategy;
import encryption.factory.EncryptionCreator;

public class AESEncryptionCreator extends EncryptionCreator {

    @Override
    public EncryptionStrategy createEncryption() {
        System.out.println("Creando estrategia AES-GCM");
        return new EncryptionAES();
    }

    @Override
    public String encryptMessage(String mensaje, String secretPhrase) {
        if (!validatePassphrase(secretPhrase)) {
            throw new IllegalArgumentException("AES requiere passphrase de al menos 8 caracteres");
        }
        return super.encryptMessage(mensaje, secretPhrase);
    }
}