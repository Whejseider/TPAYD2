package model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreUsuario;
    private String IP = "127.0.0.1";
    private Integer puerto;
    private Agenda agenda;
    private Map<String, Conversacion> conversaciones;

    public User() {
    }

    public User(String userName, int puerto) {
        this.nombreUsuario = userName;
        this.puerto = puerto;
        this.conversaciones = new ConcurrentHashMap<>();
        this.agenda = new Agenda();
    }

    public User(User userOriginal) {
        this.nombreUsuario = userOriginal.getNombreUsuario();
        this.IP = userOriginal.getIP();
        this.puerto = userOriginal.getPuerto();
        this.agenda = new Agenda(userOriginal.getAgenda());
        this.conversaciones = new ConcurrentHashMap<>();
        if (userOriginal.getConversaciones() != null) {
            for (Conversacion c : userOriginal.getConversaciones().values()) {
                this.conversaciones.computeIfAbsent(c.getContacto().getNombreUsuario(), k -> new Conversacion(c));
            }
        }
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

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public void setConversaciones(Map<String, Conversacion> conversaciones) {
        this.conversaciones = conversaciones;
    }

    public Map<String, Conversacion> getConversaciones() {
        return conversaciones;
    }

    public Conversacion getConversacionCon(String nombre) {
        Contacto contacto = this.agenda.getContactoPorNombre(nombre);
        return this.conversaciones.computeIfAbsent(contacto.getNombreUsuario(), k -> new Conversacion(contacto));
    }

    public void agregarConversacion(Conversacion conversacion) {
        this.conversaciones.putIfAbsent(conversacion.getContacto().getNombreUsuario(), conversacion);
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
        return Objects.equals(nombreUsuario.toLowerCase(), user.nombreUsuario.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nombreUsuario);
    }
}
