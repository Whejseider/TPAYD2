package controller;

import connection.Cliente;
import connection.ConnectionManager;
import connection.Sesion;
import interfaces.*;
import model.*;
import view.manager.ErrorManager;
import view.system.Form;
import view.system.FormManager;
import view.system.MainForm;

public class MainFormController implements IController, AuthenticationListener, ConnectionListener {
    private MainForm vista;
    private EventManager eventManager = EventManager.getInstance();

    public MainFormController(MainForm form) {
        this.vista = form;
    }

    @Override
    public void init() {
        this.eventManager.addAuthenticationListener(this);
        this.eventManager.addConnectionListener(this);
    }

    @Override
    public Form getForm() {
        return vista;
    }

    @Override
    public void onLoginSuccess(User user) {
    }

    @Override
    public void onLoginFailure(String s) {

    }

    /**
     * TODO!!
     */
    @Override
    public void onLogoutSuccess() {
        System.out.println("MainFormController: Logout exitoso.");
        EventManager.clearInstance();
        ClientManager.clearInstance();
        Cliente.clearInstance();
        Sesion.getInstance().setUsuarioActual(null);
        FormManager.showLogin();
    }

    @Override
    public void onLogoutFailure(String s) {
        Sesion.getInstance().setUsuarioActual(null);
        ErrorManager.getInstance().showError(s);
        FormManager.showLogin();
    }


    @Override
    public void onRegistrationSuccess() {

    }

    @Override
    public void onRegistrationFailure(String s) {

    }

    /**
     * TODO APARTIR DE ACA
     * @param tipoRespuesta
     */
    @Override
    public void onConnectionAttempt(TipoRespuesta tipoRespuesta) {
//        FormManager.init();
    }

    @Override
    public void onConnectionEstablished() {
        if (Sesion.getInstance().getUsuarioActual() != null) {
            Cliente.getInstance().iniciarSesion(Sesion.getInstance().getUsuarioActual());
        }
    }

    @Override
    public void onConnectionLost(String reason) {
//        FormManager.init();
    }

    @Override
    public void onConnectionAttemptFailure(String reason) {
        ConnectionManager.getInstance().showError(() -> {
        }, true);
    }

}
