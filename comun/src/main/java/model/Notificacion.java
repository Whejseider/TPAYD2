package model;

import java.io.Serializable;

public class Notificacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private volatile boolean tieneMensajesNuevos;

    public Notificacion() {
    }

    public boolean isTieneMensajesNuevos() {
        return this.tieneMensajesNuevos;
    }

    public void setTieneMensajesNuevos(boolean value) {
        this.tieneMensajesNuevos = value;
    }
}
