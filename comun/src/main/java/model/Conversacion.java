package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Conversacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private Contacto contacto;
    private List<Mensaje> mensajes;
    private Notificacion notificacion = new Notificacion();
    private Mensaje ultimoMensaje;

    public Conversacion(Contacto contacto) {
        this.contacto = contacto;
        this.mensajes = new ArrayList<>();
    }

    public Mensaje getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(Mensaje ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public void agregarMensaje(Mensaje mensaje) {
        this.mensajes.add(mensaje);
    }

    public void setMensajes(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Conversacion that = (Conversacion) o;
        return Objects.equals(contacto, that.contacto);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(contacto);
    }
}