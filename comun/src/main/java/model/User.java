package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User implements Serializable {
    private String nombreUsuario;
    private String IP = "127.0.0.1"; //TODO
    private Integer puerto;
    private Agenda agenda;
    private Map<Contacto, Conversacion> conversacion;

    public User() {
        this.conversacion = new HashMap<>();
        this.agenda = new Agenda();
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

    public Map<Contacto, Conversacion> getConversacion() {
        return conversacion;
    }

    public Conversacion getConversacionCon(Contacto contacto) {
        return this.conversacion.computeIfAbsent(contacto, k -> new Conversacion(contacto));
    }

    public Conversacion getConversacionCon(User user) {
        Contacto c = new Contacto(); //Atado con alambre
        c.setUser(user);
        c.setIP(user.getIP());
        c.setPuerto(user.getPuerto());
        c.setNombreUsuario(user.getNombreUsuario());
        c.setAlias(user.getNombreUsuario());
        return this.conversacion.computeIfAbsent(c, k -> new Conversacion(c));
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
        return Objects.equals(nombreUsuario, user.nombreUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nombreUsuario);
    }
}
