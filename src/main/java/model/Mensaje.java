package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensaje implements Serializable {
    private String contenido;
    private LocalDateTime tiempo;
    private User remitente;

    public Mensaje(String contenido, User emisor) {
        this.contenido = contenido;
        this.remitente = emisor;
        this.tiempo = LocalDateTime.now();
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public User getRemitente() {
        return remitente;
    }

    public void setRemitente(User remitente) {
        this.remitente = remitente;
    }

    public LocalDateTime getTiempo() {
        return tiempo;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return "[" + tiempo.format(formatter) + "]";
//        + remitente.getIP() + ": " + contenido;
    }
}
