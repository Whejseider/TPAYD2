package connection;

import model.User;

public class Sesion {
    private User usuarioActual;
    private static Sesion instance;

    public Sesion() {
    }

    public static Sesion getInstance() {
        if (instance == null) {
            instance = new Sesion();
        }
        return instance;
    }

    public void setUsuarioActual(User usuario) {
        this.usuarioActual = usuario;
    }

    public User getUsuarioActual() {
        return this.usuarioActual;
    }
}

