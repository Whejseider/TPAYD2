package encryption.chacha20;

import encryption.EncryptionStrategy;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import static encryption.chacha20.LocalChaCha20.ENCRYPT_ALGO;
import static encryption.chacha20.LocalChaCha20.NONCE_LEN;

public class EncryptionChaCha20 implements EncryptionStrategy {

    private final SecureRandom secureRandom = new SecureRandom();

    private SecretKey stringToKey(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, "ChaCha20");
    }

    private byte[] generateNonce() {
        byte[] nonce = new byte[NONCE_LEN];
        secureRandom.nextBytes(nonce);
        return nonce;
    }

    @Override
    public String encrypt(String mensaje, String secretPhrase) {
        try {
            SecretKey key = stringToKey(secretPhrase);
            byte[] nonce = generateNonce();

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            IvParameterSpec iv = new IvParameterSpec(nonce);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            byte[] plaintextBytes = mensaje.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedText = cipher.doFinal(plaintextBytes);

            ByteBuffer buffer = ByteBuffer.allocate(encryptedText.length + NONCE_LEN);
            buffer.put(encryptedText);
            buffer.put(nonce);

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

            byte[] encryptedText = new byte[decoded.length - NONCE_LEN];
            byte[] nonce = new byte[NONCE_LEN];
            buffer.get(encryptedText);
            buffer.get(nonce);

            SecretKey key = stringToKey(secretPhrase);

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            IvParameterSpec iv = new IvParameterSpec(nonce);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            byte[] plaintextBytes = cipher.doFinal(encryptedText);
            return new String(plaintextBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error al desencriptar con ChaCha20", e);
        }
    }

    public static SecretKey generateRandomKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("ChaCha20");
        keyGen.init(256, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }
}