package model;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String nombreUsuario;
    private String IP = "";
    private Integer puerto;
    private List<Contacto> contactos;
    private List<Mensaje> mensajes;

    public User(String nombreUsuario, Integer puerto) {
        this.nombreUsuario = nombreUsuario;
        this.setPuerto(puerto);
        this.contactos = new ArrayList<>();
        this.mensajes = new ArrayList<>();
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

    public void agregarMensaje(Mensaje mensaje){
        this.mensajes.add(mensaje);
    }

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public List<Mensaje> getMensajesDe(Contacto contacto) {
        List<Mensaje> resultado = new ArrayList<>();
        for (Mensaje m : mensajes) {
            if (m.getIPOrigen().equals(contacto.getIP()) || contacto.getIP().equals(this.getIP())) {
                resultado.add(m);
            }
        }
        return resultado;
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
