package model;

import java.io.Serializable;

public class Contacto extends User implements Serializable {
    private Notificacion notificacion;
    private String alias;

    public Contacto() {
        super();
        this.notificacion = new Notificacion();
    }

    @Override
    public String toString() {
        String alias = getAlias();
        String nombre = getNombreUsuario();

        String mostrar = "";

        if ((alias != null && !alias.isBlank())) {
            mostrar = alias;
        } else if (nombre != null && !nombre.isBlank()) {
            mostrar = nombre;
        }

        return mostrar + "\n  IP: " + getIP() + "  Puerto: " + getPuerto();
    }


    public Notificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
