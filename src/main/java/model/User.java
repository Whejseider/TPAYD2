package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private String nombreUsuario;
    private String IP = "127.0.0.1"; //DEFAULT
    private Integer puerto;
    private List<Contacto> contactos;
    private Map<Contacto, Conversacion> conversaciones;

    public User(String nombreUsuario, Integer puerto) {
        this.nombreUsuario = nombreUsuario;
        this.setPuerto(puerto);
        this.contactos = new ArrayList<>();
        this.conversaciones = new HashMap<>();
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public Integer getPuerto() {
        return puerto;
    }

    public void setPuerto(Integer puerto) {
        this.puerto = puerto;
    }

    public void agregarContacto(Contacto contacto) {
        if (!this.contactos.contains(contacto)) {
            this.contactos.add(contacto);
        }
    }

    public List<Contacto> getContactos() {
        return contactos;
    }

    public Map<Contacto, Conversacion> getConversaciones() {
        return conversaciones;
    }

    public Conversacion getConversacionCon(Contacto contacto) {
        return this.conversaciones.computeIfAbsent(contacto, k -> new Conversacion(contacto));
    }

    public Contacto getContacto(Contacto contacto) {
        for (Contacto c : this.getContactos()) {
            if (c.getIP().equals(contacto.getIP())) {
                return c;
            }
        }
        return null;
    }

    public Contacto getContactoPorNombre(String nombre) {
        for (Contacto c : contactos) {
            if (c.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "User{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                ", IP='" + IP + '\'' +
                ", puerto=" + puerto +
                '}';
    }


}
