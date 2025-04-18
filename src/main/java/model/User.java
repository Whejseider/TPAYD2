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
    private Map<Contacto, Conversacion> conversaciones;

    public User() {
        this.conversaciones = new HashMap<>();
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

    public Map<Contacto, Conversacion> getConversaciones() {
        return conversaciones;
    }

    public Conversacion getConversacionCon(Contacto contacto) {
        return this.conversaciones.computeIfAbsent(contacto, k -> new Conversacion(contacto));
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
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(nombreUsuario, user.nombreUsuario) &&
                Objects.equals(IP, user.IP) &&
                Objects.equals(puerto, user.puerto);
    }


    @Override
    public int hashCode() {
        return Objects.hash(nombreUsuario, IP, puerto);
    }
}
