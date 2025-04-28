package model;

import java.io.Serializable;

public class Notificacion implements Serializable {
    private volatile boolean tieneMensajesNuevos;

    public Notificacion() {
    }

    public synchronized boolean tieneMensajesNuevos() {
        return this.tieneMensajesNuevos;
    }

    public synchronized void setTieneMensajesNuevos(boolean value) {
        this.tieneMensajesNuevos = value;
    }
}
