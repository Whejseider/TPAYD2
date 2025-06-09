package encryption.blowfish;

import encryption.EncryptionStrategy;
import encryption.factory.EncryptionCreator;

public class BlowfishEncryptionCreator extends EncryptionCreator {

    @Override
    public EncryptionStrategy createEncryption() {
        System.out.println("Creando estrategia Blowfish CBC");
        return new EncryptionBlowfish();
    }

    @Override
    public String encryptMessage(String mensaje, String secretPhrase) {
        if (!validatePassphrase(secretPhrase)) {
            throw new IllegalArgumentException("Blowfish requiere passphrase de al menos 8 caracteres");
        }
        return super.encryptMessage(mensaje, secretPhrase);
    }
}
