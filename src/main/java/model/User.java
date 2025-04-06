package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String nombreUsuario;
    private String IP = "";
    private Integer puerto;
    private List<Contacto> contactos;
    private List<Conversacion> conversaciones;

    public User(String nombreUsuario, Integer puerto) {
        this.nombreUsuario = nombreUsuario;
        this.setPuerto(puerto);
        this.contactos = new ArrayList<>();
        this.conversaciones = new ArrayList<>();
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
        this.contactos.add(contacto);
    }

    public List<Contacto> getContactos() {
        return contactos;
    }

    public void agregarConversacion(Conversacion conversacion){
        this.conversaciones.add(conversacion);
    }

    public List<Conversacion> getConversaciones() {
        return this.conversaciones;
    }

    public Conversacion getConversacionCon(Contacto contacto) {
        for (Conversacion c : conversaciones) {
            if (c.getContacto().getIP().equals(contacto.getIP())) {
                return c;
            }
        }
        // Si no existe, la creamos
        Conversacion nuevaConversacion = new Conversacion(contacto);
        agregarConversacion(nuevaConversacion);
        return nuevaConversacion;
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
    public String toString() {
        return "User{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                ", IP='" + IP + '\'' +
                ", puerto=" + puerto +
                '}';
    }


}
