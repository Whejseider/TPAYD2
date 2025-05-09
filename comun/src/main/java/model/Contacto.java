package model;

import java.io.Serializable;
import java.util.Objects;

public class Contacto implements Serializable {
    private String alias;
    private User user;
    private String IP;
    private Integer puerto;

    public Contacto() {
    }

    @Override
    public String toString() {
        String alias = getAlias();
        String nombre = (getUser() != null) ? getUser().getNombreUsuario() : null;

        String mostrar = "";

        if ((alias != null && !alias.isBlank())) {
            mostrar = alias;
        } else if (nombre != null && !nombre.isBlank()) {
            mostrar = nombre;
        }

        return mostrar + "\n  IP: " + getIP() + "  Puerto: " + getPuerto();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNombreUsuario() {
        return this.user.getNombreUsuario();
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.user.setNombreUsuario(nombreUsuario);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Contacto contacto = (Contacto) o;
        return Objects.equals(user, contacto.user);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user);
    }
}
