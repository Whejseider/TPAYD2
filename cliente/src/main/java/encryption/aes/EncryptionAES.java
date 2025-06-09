package encryption.aes;

import encryption.EncryptionStrategy;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import static encryption.aes.LocalAES.*;

public class EncryptionAES implements EncryptionStrategy {

    private SecretKeySpec generateAesKeyFromPassphrase(String secretPhrase) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance(SHA_CRYPT);
        byte[] keyBytes = sha256.digest(secretPhrase.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    @Override
    public String encrypt(String mensaje, String secretPhrase) {
        try {
            // Generate a random IV
            byte[] iv = new byte[IV_LENGTH_ENCRYPT];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            // Generate the AES key from the local passphrase
            SecretKeySpec aesKey = generateAesKeyFromPassphrase(secretPhrase);

            // Initialize cipher in AES-GCM mode
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);

            // Encrypt the plaintext
            byte[] encryptedBytes = cipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted text and encode them as Base64
            byte[] combinedIvAndCipherText = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combinedIvAndCipherText, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combinedIvAndCipherText, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combinedIvAndCipherText);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    @Override
    public String decrypt(String mensaje, String secretPhrase) {
        try {
            byte[] decodedCipherText = Base64.getDecoder().decode(mensaje);

            // Generate the AES key from the local passphrase
            SecretKeySpec aesKey = generateAesKeyFromPassphrase(secretPhrase);

            // Extract IV and encrypted text
            byte[] iv = new byte[IV_LENGTH_ENCRYPT];
            System.arraycopy(decodedCipherText, 0, iv, 0, iv.length);
            byte[] encryptedText = new byte[decodedCipherText.length - IV_LENGTH_ENCRYPT];
            System.arraycopy(decodedCipherText, IV_LENGTH_ENCRYPT, encryptedText, 0, encryptedText.length);

            // Initialize cipher in AES-GCM mode
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

            // Decrypt the ciphertext
            byte[] decryptedBytes = cipher.doFinal(encryptedText);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error al desencriptar", e);
        }
    }
}
