package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensaje {
    private String contenido;
    private LocalDateTime tiempo;
    private String IPOrigen;

    public Mensaje(String contenido, String IPOrigen) {
        this.contenido = contenido;
        this.IPOrigen = IPOrigen;
        this.tiempo = LocalDateTime.now();
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getIPOrigen() {
        return IPOrigen;
    }

    public void setIPOrigen(String IPOrigen) {
        this.IPOrigen = IPOrigen;
    }

    public LocalDateTime getTiempo() {
        return tiempo;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "[" + tiempo.format(formatter) + "] " + IPOrigen + ": " + contenido;
    }
}
