package model;

import java.io.Serializable;
import java.util.Objects;

public class Contacto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String alias;
    private String nombreUsuario;
    private String IP;
    private Integer puerto;

    public Contacto() {
    }

    @Override
    public String toString() {
        String alias = getAlias();
        String nombreUsuario = getNombreUsuario();

        String mostrar = "";

        if ((alias != null && !alias.isBlank())) {
            mostrar = alias;
        } else if (nombreUsuario != null && !nombreUsuario.isBlank()) {
            mostrar = nombreUsuario;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Contacto contacto = (Contacto) o;
        return Objects.equals(nombreUsuario, contacto.nombreUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nombreUsuario);
    }
}
