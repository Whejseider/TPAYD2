package controller;

import connection.Cliente;
import connection.Sesion;
import interfaces.AuthenticationListener;
import interfaces.IController;
import model.User;
import view.manager.ErrorManager;
import view.system.Form;
import view.system.FormManager;
import view.system.MainForm;

public class MainFormController implements IController, AuthenticationListener {
    private MainForm vista;
    private EventManager eventManager = EventManager.getInstance();

    public MainFormController(MainForm form) {
        this.vista = form;
    }

    @Override
    public void init() {
        this.eventManager.addAuthenticationListener(this);
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

    @Override
    public void onLogoutSuccess() {
        EventManager.clearInstance();
        ClientManager.clearInstance();
        Cliente.clearInstance();
        Sesion.getInstance().setUsuarioActual(null);
        FormManager.clearForms();
        FormManager.install(FormManager.getFrame());
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

}
