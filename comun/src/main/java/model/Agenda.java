package model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Agenda implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Contacto> contactos;

    public Agenda() {
        this.contactos = new CopyOnWriteArrayList<Contacto>();
    }

    public Agenda(Agenda agendaOriginal){
        this.contactos = new CopyOnWriteArrayList<>();
        if (agendaOriginal != null) {
            for (Contacto contacto : agendaOriginal.getContactos()) {
                this.contactos.add(new Contacto(contacto));
            }
        }
    }

    public void setContactos(List<Contacto> contactos) {
        this.contactos = contactos;
    }

    public List<Contacto> getContactos() {
        return contactos;
    }

    public void agregarContacto(Contacto contacto) {
        if (!contactos.contains(contacto)) {
            this.contactos.add(contacto);
        }
    }

    public static Contacto crearContacto(User user) {
        Contacto c = new Contacto();
        c.setNombreUsuario(user.getNombreUsuario());
        c.setIP(user.getIP());
        c.setPuerto(user.getPuerto());
        c.setAlias(user.getNombreUsuario());
        return c;
    }

    public Contacto getContactoPorNombre(String nombre) {
        for (Contacto c : contactos) {
            if (c.getNombreUsuario().equals(nombre)) {
                return c;
            }
        }
        return null;
    }

    public boolean existeContacto(String nombre) {
        for (Contacto c : contactos) {
            if (c.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    /**
     * EN desUSO
     *
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agenda agenda = (Agenda) o;
        return Objects.equals(contactos, agenda.contactos);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(contactos);
    }
}
