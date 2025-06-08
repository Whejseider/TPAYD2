package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;
    private String contenido;
    private LocalDateTime tiempo;
    private String emisor;
    private String nombreReceptor;

    public Mensaje(String contenido, String emisor, String nombreReceptor) {
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

    public Mensaje() {
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
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

    @JsonIgnore
    public String getTiempoFormateado(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return tiempo.format(formatter);
    }

    @JsonIgnore
    public boolean esMio(User u) {
        return !emisor.isEmpty() && emisor.equalsIgnoreCase(u.getNombreUsuario());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mensaje mensaje = (Mensaje) o;
        return Objects.equals(contenido, mensaje.contenido) && Objects.equals(tiempo, mensaje.tiempo) && Objects.equals(emisor, mensaje.emisor) && Objects.equals(nombreReceptor, mensaje.nombreReceptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contenido, tiempo, emisor, nombreReceptor);
    }
}
