package controller;

import connection.Sesion;
import interfaces.AppStateListener;
import model.Directorio;
import model.Mensaje;
import model.TipoRespuesta;
import model.User;
import view.manager.ErrorManager;
import view.system.FormManager;
import view.system.MainForm;

import java.util.List;

public class MainFormController implements AppStateListener {
    private MainForm vista;
    private User user = Sesion.getInstance().getUsuarioActual();
    private MainController mainController = MainController.getInstance();

    public MainFormController() {
        this.vista = FormManager.getMainForm();
        this.mainController.addAppStateListener(this);
    }

    @Override
    public void onConnectionAttempt(TipoRespuesta tipoRespuesta) {

    }

    @Override
    public void onLoginSuccess(User user) {

    }

    @Override
    public void onLoginFailure(String s) {

    }

    @Override
    public void onLogoutSuccess() {
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
    public void onNewMessageReceived(Mensaje mensaje) {

    }

    @Override
    public void onUserListUpdated(List<User> userList) {

    }

    @Override
    public void onRegistrationSuccess() {

    }

    @Override
    public void onRegistrationFailure(String s) {

    }

    @Override
    public void onDirectoryInfoReceived(Directorio directorio) {

    }

    @Override
    public void onAddContactSuccess(User user) {

    }

    @Override
    public void onAddContactFailure(String s) {

    }
}
