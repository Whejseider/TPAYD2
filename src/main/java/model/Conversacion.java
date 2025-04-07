package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversacion implements Serializable {
    private Contacto contacto;
    private List<Mensaje> mensajes;

    public Conversacion(Contacto contacto) {
        this.contacto = contacto;
        this.mensajes = new ArrayList<>();
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
}