package encryption.chacha20;

import encryption.EncryptionStrategy;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionChaCha20 implements EncryptionStrategy {

    private final SecureRandom secureRandom = new SecureRandom();

    private SecretKeySpec deriveKey(String passphrase, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, LocalChaCha20.PBKDF2_ITERATIONS, LocalChaCha20.KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(LocalChaCha20.DERIVATION_ALGORITHM);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, LocalChaCha20.KEY_ALGO);
    }

    @Override
    public String encrypt(String mensaje, String secretPhrase) {
        try {
            byte[] salt = new byte[LocalChaCha20.SALT_LENGTH];
            byte[] nonce = new byte[LocalChaCha20.NONCE_LENGTH];
            secureRandom.nextBytes(salt);
            secureRandom.nextBytes(nonce);

            SecretKeySpec key = deriveKey(secretPhrase, salt);

            Cipher cipher = Cipher.getInstance(LocalChaCha20.CHACHA20_POLY1305);
            ChaCha20ParameterSpec paramSpec = new ChaCha20ParameterSpec(nonce, 0);
            cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

            byte[] plaintextBytes = mensaje.getBytes(StandardCharsets.UTF_8);
            byte[] cipherText = cipher.doFinal(plaintextBytes);

            ByteBuffer buffer = ByteBuffer.allocate(LocalChaCha20.SALT_LENGTH + LocalChaCha20.NONCE_LENGTH + cipherText.length);
            buffer.put(salt);
            buffer.put(nonce);
            buffer.put(cipherText);

            return Base64.getEncoder().encodeToString(buffer.array());

        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar con ChaCha20", e);
        }
    }

    @Override
    public String decrypt(String mensaje, String secretPhrase) {
        try {
            byte[] decoded = Base64.getDecoder().decode(mensaje);

            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] salt = new byte[LocalChaCha20.SALT_LENGTH];
            byte[] nonce = new byte[LocalChaCha20.NONCE_LENGTH];
            buffer.get(salt);
            buffer.get(nonce);

            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);

            SecretKeySpec key = deriveKey(secretPhrase, salt);

            Cipher cipher = Cipher.getInstance(LocalChaCha20.CHACHA20_POLY1305);
            ChaCha20ParameterSpec paramSpec = new ChaCha20ParameterSpec(nonce, 0);
            cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

            byte[] plainTextBytes = cipher.doFinal(cipherText);
            return new String(plainTextBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error al desencriptar con ChaCha20", e);
        }
    }
}
