package controller;

import connection.Cliente;
import view.Messenger;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ControllerManager {

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
