package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Agenda {
    private HashMap<User, Contacto> contactos;
    private User user;
    private Contacto contacto;

    public Agenda(User user, Contacto contacto) {
        this.user = user;
        this.contacto = contacto;
        this.contactos = new LinkedHashMap<>();
    }

    public List<Contacto> getContactos() {
        return new ArrayList<>(contactos.values());
    }

    public void agregarContacto(Contacto contacto) {
        this.contactos.putIfAbsent(user, contacto);
    }

    public Contacto getContactoPorUsuario(User user) {
        if (user == null) return null; //TODO
        return this.contactos.get(user);
    }

    public Contacto getContacto(Contacto contacto) {
        for (Contacto c : this.getContactos()) {
            if (c.getIP().equals(contacto.getIP())) {
                return c;
            }
        }
        return null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }
}
