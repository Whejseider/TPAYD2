package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import encryption.EncryptionType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String contenido;
    private LocalDateTime tiempo;
    private String nombreEmisor;
    private String nombreReceptor;
    private EncryptionType encryption;
    private MessageStatus status;

    public Mensaje(String contenido, String nombreEmisor, String nombreReceptor, EncryptionType encryption) {
        this.id = generateUniqueId();
        this.contenido = contenido;
        this.nombreEmisor = nombreEmisor;
        this.nombreReceptor = nombreReceptor;
        this.tiempo = LocalDateTime.now();
        this.encryption = encryption;
        this.status = MessageStatus.PENDING;
    }

    public Mensaje(Mensaje original) {
        this.id = original.id;
        this.contenido = original.contenido;
        this.tiempo = original.tiempo;
        this.nombreReceptor = original.nombreReceptor;
        this.nombreEmisor = original.nombreEmisor;
        this.encryption = original.encryption;
        this.status = original.status;
    }

    public Mensaje() {
        this.id = generateUniqueId();
        this.status = MessageStatus.PENDING;
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getNombreEmisor() {
        return nombreEmisor;
    }

    public EncryptionType getEncryption() {
        return encryption;
    }

    public void setEncryption(EncryptionType encryption) {
        this.encryption = encryption;
    }

    public void setNombreEmisor(String nombreEmisor) {
        this.nombreEmisor = nombreEmisor;
    }

    public String getNombreReceptor() {
        return nombreReceptor;
    }

    public void setNombreReceptor(String nombreReceptor) {
        this.nombreReceptor = nombreReceptor;
    }

    public void setTiempo(LocalDateTime tiempo) {
        this.tiempo = tiempo;
    }

    public LocalDateTime getTiempo() {
        return tiempo;
    }

    @JsonIgnore
    public String getTiempoFormateado(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return tiempo.format(formatter);
    }

    @JsonIgnore
    public boolean esMio(User u) {
        return !nombreEmisor.isEmpty() && nombreEmisor.equalsIgnoreCase(u.getNombreUsuario());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mensaje mensaje = (Mensaje) o;
        return Objects.equals(id, mensaje.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "id='" + id + '\'' +
                ", contenido='" + contenido + '\'' +
                ", emisor='" + nombreEmisor + '\'' +
                ", receptor='" + nombreReceptor + '\'' +
                ", tiempo=" + tiempo +
                ", status=" + status +
                '}';
    }
}