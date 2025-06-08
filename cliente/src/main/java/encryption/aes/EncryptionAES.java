package encryption.aes;

import config.Config;
import encryption.EncryptionStrategy;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionAES implements EncryptionStrategy {

    private final SecureRandom secureRandom = new SecureRandom();

    private SecretKeySpec deriveKey(String passphrase, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, LocalAES.PBKDF2_ITERATIONS, LocalAES.KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, LocalAES.AES_ALGORITHM);
    }

    @Override
    public String encrypt(String mensaje) {
        try {
            byte[] iv = new byte[LocalAES.IV_LENGTH];
            byte[] salt = new byte[LocalAES.SALT_LENGTH];
            secureRandom.nextBytes(iv);
            secureRandom.nextBytes(salt);

            SecretKeySpec key = deriveKey(Config.LOCAL_PASSPHRASE, salt);

            Cipher cipher = Cipher.getInstance(LocalAES.AES_ALGORITHM_GCM);
            GCMParameterSpec spec = new GCMParameterSpec(LocalAES.TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] cipherText = cipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));

            // Concatenar: salt + IV + ciphertext
            byte[] result = new byte[LocalAES.SALT_LENGTH + LocalAES.IV_LENGTH + cipherText.length];
            System.arraycopy(salt, 0, result, 0, LocalAES.SALT_LENGTH);
            System.arraycopy(iv, 0, result, LocalAES.SALT_LENGTH, LocalAES.IV_LENGTH);
            System.arraycopy(cipherText, 0, result, LocalAES.SALT_LENGTH + LocalAES.IV_LENGTH, cipherText.length);

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    @Override
    public String decrypt(String mensaje) {
        try {
            byte[] decoded = Base64.getDecoder().decode(mensaje);

            byte[] salt = new byte[LocalAES.SALT_LENGTH];
            byte[] iv = new byte[LocalAES.IV_LENGTH];
            byte[] cipherText = new byte[decoded.length - LocalAES.SALT_LENGTH - LocalAES.IV_LENGTH];

            System.arraycopy(decoded, 0, salt, 0, LocalAES.SALT_LENGTH);
            System.arraycopy(decoded, LocalAES.SALT_LENGTH, iv, 0, LocalAES.IV_LENGTH);
            System.arraycopy(decoded, LocalAES.SALT_LENGTH + LocalAES.IV_LENGTH, cipherText, 0, cipherText.length);

            SecretKeySpec key = deriveKey(Config.LOCAL_PASSPHRASE, salt);

            Cipher cipher = Cipher.getInstance(LocalAES.AES_ALGORITHM_GCM);
            GCMParameterSpec spec = new GCMParameterSpec(LocalAES.TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error al desencriptar", e);
        }
    }
}
