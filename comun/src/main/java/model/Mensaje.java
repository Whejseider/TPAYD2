package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Mensaje implements Serializable {
    private String contenido;
    private LocalDateTime tiempo;
    private User emisor;
    private Contacto receptor;
    private boolean esMio;

    public Mensaje(String contenido, User emisor, Contacto receptor, boolean esMio) {
        this.contenido = contenido;
        this.emisor = emisor;
        this.receptor = receptor;
        this.esMio = esMio;
        this.tiempo = LocalDateTime.now();
    }

    public boolean EsMio() {
        return esMio;
    }

    public void setEsMio(boolean esMio) {
        this.esMio = esMio;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public User getEmisor() {
        return emisor;
    }

    public void setEmisor(User emisor) {
        this.emisor = emisor;
    }

    public Contacto getReceptor() {
        return receptor;
    }

    public void setReceptor(Contacto receptor) {
        this.receptor = receptor;
    }

    public LocalDateTime getTiempo() {
        return tiempo;
    }

    public String getTiempoFormateado(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return tiempo.format(formatter);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mensaje mensaje = (Mensaje) o;
        return Objects.equals(emisor, mensaje.emisor) && Objects.equals(receptor, mensaje.receptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emisor, receptor);
    }
}
