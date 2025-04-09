package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Agenda implements Serializable {
    private HashMap<User, Contacto> contactos;

    public Agenda() {
        this.contactos = new LinkedHashMap<>();
    }

    public List<Contacto> getContactos() {
        return new ArrayList<>(contactos.values());
    }

    public void agregarContacto(User user, Contacto contacto) {
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

}
