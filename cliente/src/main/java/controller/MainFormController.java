package controller;

import connection.Cliente;
import connection.ConnectionManager;
import connection.Sesion;
import interfaces.*;
import model.*;
import raven.modal.Toast;
import view.manager.ToastManager;
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
        FormManager.showLogin();
    }

    /**
     * TODO!!
     */
    @Override
    public void onLogoutSuccess() {
        System.out.println("MainFormController: Logout exitoso.");
        Sesion.getInstance().setUsuarioActual(null);
//        Cliente.getInstance().cerrarTodo(false);
        FormManager.showLogin();
    }

    @Override
    public void onLogoutFailure(String s) {
        System.out.println("MainFormController: Logout fallo.");
        Sesion.getInstance().setUsuarioActual(null);
        ToastManager.getInstance().showToast(Toast.Type.ERROR, s);
        Cliente.getInstance().cerrarTodo(true);
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
     *
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
        } else {
            FormManager.showLogin();
        }
        ConnectionManager.getInstance().checkOnReconnection();

        if (ConnectionManager.getInstance().getFormError() != null && ConnectionManager.getInstance().getFormError().isVisible()) {
            ConnectionManager.getInstance().getFormError().setVisible(false);
        }
        ToastManager.getInstance().showToast(Toast.Type.SUCCESS, "Reconectado exitosamente");
        ConnectionManager.getInstance().setFormError(null);

//        Cliente.getInstance().startPeriodicServerCheck();
    }

    @Override
    public void onConnectionLost(String s) {
//        Sesion.getInstance().setUsuarioActual(null);
        ConnectionManager.getInstance().showError(this::callBackConnection, true, s);
        if (ConnectionManager.getInstance().getFormError() == null || !ConnectionManager.getInstance().getFormError().isVisible()) {
            ConnectionManager.getInstance().showError(this::callBackConnection, true, "Fallo al reconectar con el servidor");
        } else {
            if (ConnectionManager.getInstance().getFormError().isVisible()) {
                ConnectionManager.getInstance().getFormError().showReconnectOptions(true);
            }
        }
        Cliente.getInstance().stopPeriodicServerCheck();
    }

    @Override
    public void onConnectionAttemptFailure(String s) {
//        Sesion.getInstance().setUsuarioActual(null);
        ConnectionManager.getInstance().showError(this::callBackConnection, true, s);
        Cliente.getInstance().stopPeriodicServerCheck();
    }

    private void callBackConnection() {
        try {
            FormManager.restorePreviousForm();
        } catch (Exception e) {
            ToastManager.getInstance().showToast(Toast.Type.ERROR, e.getMessage());
        }
    }

}
