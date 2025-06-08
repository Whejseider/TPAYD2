package encryption.blowfish;

import config.Config;
import encryption.EncryptionStrategy;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionBlowfish implements EncryptionStrategy {

    public EncryptionBlowfish() {}

    private SecretKeySpec generateKey() throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = md.digest(Config.LOCAL_PASSPHRASE.getBytes(StandardCharsets.UTF_8));
        // Blowfish allows keys from 32 bits up to 448 bits; use first 16 bytes (128 bits) for simplicity
        byte[] shortKey = new byte[16];
        System.arraycopy(keyBytes, 0, shortKey, 0, shortKey.length);
        return new SecretKeySpec(shortKey, LocalBlowfish.ALGORITHM);
    }

    @Override
    public String encrypt(String mensaje) {
        try {
            byte[] iv = new byte[LocalBlowfish.IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(LocalBlowfish.CIPHER_MODE);
            SecretKeySpec key = generateKey();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            byte[] encrypted = cipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));

            byte[] output = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, output, 0, iv.length);
            System.arraycopy(encrypted, 0, output, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(output);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String decrypt(String mensaje) {
        try {
            byte[] data = Base64.getDecoder().decode(mensaje);
            byte[] iv = new byte[LocalBlowfish.IV_LENGTH];
            byte[] encrypted = new byte[data.length - LocalBlowfish.IV_LENGTH];

            System.arraycopy(data, 0, iv, 0, LocalBlowfish.IV_LENGTH);
            System.arraycopy(data, LocalBlowfish.IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(LocalBlowfish.CIPHER_MODE);
            SecretKeySpec key = generateKey();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
