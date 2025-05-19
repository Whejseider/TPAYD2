package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;
    private String contenido;
    private LocalDateTime tiempo;
    private User emisor;
    private String nombreReceptor;

    public Mensaje(String contenido, User emisor, String nombreReceptor) {
        this.contenido = contenido;
        this.emisor = emisor;
        this.nombreReceptor = nombreReceptor;
        this.tiempo = LocalDateTime.now();
    }

    public Mensaje(Mensaje original) {
        this.contenido = original.contenido;
        this.tiempo = original.tiempo;
        this.nombreReceptor = original.nombreReceptor;

        this.emisor = original.emisor;

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

    public String getTiempoFormateado(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return tiempo.format(formatter);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mensaje mensaje = (Mensaje) o;
        return Objects.equals(emisor.getNombreUsuario(), mensaje.emisor.getNombreUsuario()) && Objects.equals(nombreReceptor, mensaje.nombreReceptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emisor.getNombreUsuario(), nombreReceptor);
    }
}
