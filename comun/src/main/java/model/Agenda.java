package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Agenda implements Serializable {
    private List<Contacto> contactos;

    public Agenda() {
        this.contactos = new ArrayList<>();
    }

    public List<Contacto> getContactos() {
        return contactos;
    }

    public void agregarContacto(Contacto contacto) {
        this.contactos.add(contacto);
    }

    public void agregarContacto(User user){
        Contacto c = new Contacto();
        c.setUser(user);
        c.setNombreUsuario(user.getNombreUsuario());
        c.setIP(user.getIP());
        c.setPuerto(user.getPuerto());
        c.setAlias(user.getNombreUsuario());
        this.contactos.add(c);
    }

    public Contacto getContactoPorUsuario(User user) {
        for (Contacto c : contactos) {
            if (c.getNombreUsuario().equals(user.getNombreUsuario())) {
                return c;
            }
        }
        return null;
    }

    public boolean existeContacto(User user) {
        for (Contacto c : contactos) {
            if (c.getNombreUsuario().equalsIgnoreCase(user.getNombreUsuario())) {
                return true;
            }
        }
        return false;
    }

    /**
     * EN desUSO
     * @param contactoOriginal
     * @param contactoActualizado
     * @return
     */
    public boolean modificarContactoPorIP(Contacto contactoOriginal, Contacto contactoActualizado) {
        for (int i = 0; i < contactos.size(); i++) {
            Contacto c = contactos.get(i);
            if (c.getIP().equals(contactoOriginal.getIP())) {
                contactos.set(i, contactoActualizado);
                return true;
            }
        }
        return false;
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
