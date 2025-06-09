package config;

import connection.Sesion;
import encryption.EncryptionType;

import java.io.*;
import java.util.Properties;

public class Config {
    private static Config instance;
    private static final String CONFIG_FILE_PATH =
            System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Messenger" +
                    File.separator + Sesion.getInstance().getUsuarioActual().getNombreUsuario() +
                    File.separator + "config.properties";

    public String LOCAL_PASSPHRASE = "Messenger2025";

    public enum PersistenceType {
        JSON,
        XML,
        TXT
    }

    private PersistenceType persistenceType = PersistenceType.JSON; // DEFAULT
    private EncryptionType encryptionType = EncryptionType.AES_GCM; // DEFAULT

    private Config() {
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public String getLocalPassphrase() {
        return this.LOCAL_PASSPHRASE;
    }

    public void setLocalPassphrase(String localPassphrase) {
        if (localPassphrase != null && localPassphrase.length() >= 8) {
            this.LOCAL_PASSPHRASE = localPassphrase;
        }
    }

    public PersistenceType getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(PersistenceType persistenceType) {
        this.persistenceType = persistenceType;
    }

    public EncryptionType getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }

    public void saveConfiguration() {
        File configFile = new File(CONFIG_FILE_PATH);
        configFile.getParentFile().mkdirs();

        Properties props = new Properties();

        if (persistenceType != null) {
            props.setProperty("persistence.type", persistenceType.name());
        }
        if (encryptionType != null) {
            props.setProperty("encryption.type", encryptionType.name());
        }

        if (LOCAL_PASSPHRASE != null) {
            props.setProperty("local.passphrase", LOCAL_PASSPHRASE);
        }

        try (OutputStream output = new FileOutputStream(configFile)) {
            props.store(output, "Messenger App Configuration");
            System.out.println("Configuración guardada.");
        } catch (IOException e) {
            System.err.println("Error al guardar la configuración.");
            e.printStackTrace();
        }
    }

    public boolean loadConfiguration() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            return false;
        }

        Properties props = new Properties();
        try (InputStream input = new FileInputStream(configFile)) {
            props.load(input);

            String persistenceValue = props.getProperty("persistence.type");
            String encryptionValue = props.getProperty("encryption.type");
            String localPassphrase = props.getProperty("local.passphrase");

            if (persistenceValue == null || encryptionValue == null) {
                System.err.println("Archivo de configuración incompleto.");
                return false;
            }

            this.persistenceType = PersistenceType.valueOf(persistenceValue);
            this.encryptionType = EncryptionType.valueOf(encryptionValue);
            LOCAL_PASSPHRASE = localPassphrase;

            System.out.println("Configuración cargada: Persistencia=" + this.persistenceType + ", Cifrado=" + this.encryptionType + ", PASSPHRASE");
            return true;

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error al cargar configuración o valor inválido.");
            e.printStackTrace();
        }
        return false;
    }
}
