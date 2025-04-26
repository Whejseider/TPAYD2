package controller;

import view.Messenger;

import java.awt.event.WindowListener;

/**
 * The ControllerManager class serves as the central point for managing and coordinating
 * multiple controllers within the application. It facilitates communication and interaction
 * between various controllers and ensures the consistency and synchronization of application states.
 * <p>
 * Responsibilities include:
 * - Instantiation and lifecycle management of controllers.
 * - Providing access to the main controllers and their functionalities.
 * - Acting as a mediator between controllers to handle shared processes or data.
 */
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
