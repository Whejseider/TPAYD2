package model;

import java.io.Serializable;

public class Notificacion implements Serializable {
    private boolean tieneMensajesNuevos = false;

    public Notificacion() {
    }

    public boolean tieneMensajesNuevos() {
        return tieneMensajesNuevos;
    }

    public void setTieneMensajesNuevos(boolean tieneMensajesNuevos) {
        this.tieneMensajesNuevos = tieneMensajesNuevos;
    }
}
