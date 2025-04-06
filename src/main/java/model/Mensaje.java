package model;

import java.time.LocalDate;

public class Mensaje {
    private String contenido;
    private LocalDate tiempo;
    private String IPOrigen;

    public Mensaje(String contenido, String IPOrigen) {
        this.contenido = contenido;
        this.IPOrigen = IPOrigen;
        this.tiempo = LocalDate.now();
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

    public LocalDate getTiempo() {
        return tiempo;
    }
}
