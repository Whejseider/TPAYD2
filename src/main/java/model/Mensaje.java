package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Mensaje implements Serializable {
    private String contenido;
    private LocalDateTime tiempo;
    private User emisor;
    private User receptor;

    public Mensaje(String contenido, User emisor, User receptor) {
        this.contenido = contenido;
        this.emisor = emisor;
        this.receptor = receptor;
        this.tiempo = LocalDateTime.now();
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

    public User getReceptor() {
        return receptor;
    }

    public void setReceptor(User receptor) {
        this.receptor = receptor;
    }

    public LocalDateTime getTiempo() {
        return tiempo;
    }

    public String getTiempoFormateado(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return "[" + tiempo.format(formatter) + "]";
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
