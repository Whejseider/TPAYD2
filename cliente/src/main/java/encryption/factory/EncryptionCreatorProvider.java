package encryption.factory;

import encryption.EncryptionType;
import encryption.aes.AESEncryptionCreator;
import encryption.blowfish.BlowfishEncryptionCreator;
import encryption.chacha20.ChaCha20EncryptionCreator;

public class EncryptionCreatorProvider {

    public static EncryptionCreator getCreator(EncryptionType type) {
        return switch (type) {
            case AES_GCM -> new AESEncryptionCreator();
            case CHACHA20 -> new ChaCha20EncryptionCreator();
            case BLOWFISH -> new BlowfishEncryptionCreator();
        };
    }
}
