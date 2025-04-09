package model;

import java.io.Serializable;
import java.util.Objects;

public class Contacto extends User implements Serializable {
    private Notificacion notificacion;

    public Contacto(String nombreUsuario, String IP, Integer puerto) {
        super(nombreUsuario, puerto);
        this.setIP(IP);
        this.notificacion = new Notificacion();
    }

    @Override
    public String toString() {
        return getNombreUsuario() + "\n" + "IP:" + getIP() + "  " + "Puerto:" + getPuerto();
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }
}
