package model;

import java.io.Serializable;
import java.util.*;

public class User implements Serializable {
    private String nombreUsuario;
    private String IP = "127.0.0.1"; //TODO
    private Integer puerto;
    private Map<String, Contacto> contactos;
    private Map<Contacto, Conversacion> conversaciones;

    public User(String nombreUsuario, Integer puerto) {
        this.nombreUsuario = nombreUsuario;
        this.setPuerto(puerto);
        this.contactos = new LinkedHashMap();
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
        this.contactos.putIfAbsent(contacto.getNombreUsuario(), contacto);
    }

    public List<Contacto> getContactos() {
        return new ArrayList<>(contactos.values());
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
        if (nombre == null) return null;
        return this.contactos.get(nombre);
    }


    @Override
    public String toString() {
        return "User{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                ", IP='" + IP + '\'' +
                ", puerto=" + puerto +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(nombreUsuario, user.nombreUsuario) && Objects.equals(IP, user.IP) && Objects.equals(puerto, user.puerto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreUsuario, IP, puerto);
    }
}
