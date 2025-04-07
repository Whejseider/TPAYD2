package model;

import java.io.Serializable;
import java.util.Objects;

public class Contacto extends User implements Serializable {
    private boolean tieneMensajesNuevos = false;

    public Contacto(String nombreUsuario, String IP, Integer puerto) {
        super(nombreUsuario, puerto);
        this.setIP(IP);
    }

    public boolean tieneMensajesNuevos() {
        return tieneMensajesNuevos;
    }

    public void setTieneMensajesNuevos(boolean tieneMensajesNuevos) {
        this.tieneMensajesNuevos = tieneMensajesNuevos;
    }

    @Override
    public String toString() {
        return getNombreUsuario() + "\n" + "IP:" + getIP() + "  " + "Puerto:" + getPuerto();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Contacto contacto = (Contacto) o;
        return Objects.equals(getIP(), contacto.getIP()) && Objects.equals(getPuerto(), contacto.getPuerto());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIP());
    }
}
