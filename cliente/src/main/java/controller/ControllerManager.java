package controller;

import view.Messenger;

public class ControllerManager  {

    private static ControllerManager instance;
    private Messenger vista;

    private ControllerManager() {
    }

    public static ControllerManager getInstance() {
        if (instance == null) {
            instance = new ControllerManager();
        }
        return instance;
    }

    public Messenger getVista() {
        return vista;
    }

    public void setVista(Messenger vista) {
        this.vista = vista;
    }
}
