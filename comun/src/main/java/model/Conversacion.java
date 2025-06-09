package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Conversacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    private Contacto contacto;
    private List<Mensaje> mensajes = new CopyOnWriteArrayList<>();;
    private Notificacion notificacion = new Notificacion();
    @JsonIgnore
    private Mensaje ultimoMensaje;

    public Conversacion(Contacto contacto) {
        this.contacto = contacto;
    }

    public Conversacion(Conversacion conversacionOriginal) {
        this.notificacion = conversacionOriginal.getNotificacion();
        this.ultimoMensaje = new Mensaje(conversacionOriginal.getUltimoMensaje());
        this.mensajes = new CopyOnWriteArrayList<>();
        if (conversacionOriginal.getMensajes() != null){
            for (Mensaje m : conversacionOriginal.getMensajes()){
                this.mensajes.add(new Mensaje(m));
            }
        }
        this.contacto = new Contacto(conversacionOriginal.getContacto());
    }

    public Conversacion() {
    }

    public Mensaje getUltimoMensaje() {
        if (mensajes == null || mensajes.isEmpty()) {
            return null;
        }
        return mensajes.get(mensajes.size() - 1);
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
        if (mensaje != null && !mensajes.contains(mensaje)) {
            mensajes.add(mensaje);
        }
    }

    public Mensaje getMensajePorId(Mensaje mensaje){
        if (mensaje != null){
            for (Mensaje m : mensajes){
                if (m.getId().equals(mensaje.getId())){
                    return m;
                }
            }
        }
        return null;
    }


    public void setMensajes(List<Mensaje> mensajes) {
        if (mensajes != null) {
            this.mensajes.clear();
            this.mensajes.addAll(mensajes);
        } else {
            this.mensajes.clear();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversacion that = (Conversacion) o;
        return Objects.equals(contacto, that.contacto);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(contacto);
    }
}