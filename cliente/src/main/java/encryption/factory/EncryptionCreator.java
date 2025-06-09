package encryption.factory;

import encryption.EncryptionStrategy;

public abstract class EncryptionCreator {

    public abstract EncryptionStrategy createEncryption();

    public String encryptMessage(String mensaje, String secretPhrase) {
        EncryptionStrategy strategy = createEncryption();
        return strategy.encrypt(mensaje, secretPhrase);
    }

    public String decryptMessage(String mensaje, String secretPhrase) {
        EncryptionStrategy strategy = createEncryption();
        return strategy.decrypt(mensaje, secretPhrase);
    }

    protected boolean validatePassphrase(String passphrase) {
        return passphrase != null && passphrase.length() >= 8;
    }

}
