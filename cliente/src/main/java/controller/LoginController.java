package controller;

import com.formdev.flatlaf.FlatClientProperties;
import interfaces.AppStateListener;
import model.*;
import connection.Cliente;
import view.forms.Login;
import view.Messenger;
import view.system.FormManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LoginController implements ActionListener, AppStateListener {
    private Login vista;
    private User user;
    private Cliente cliente;
    private Messenger messenger;
    private MainController mainController;

    public LoginController(Login vista) {
        this.vista = vista;
        this.mainController = MainController.getInstance();
        this.vista.getBtnAceptar().addActionListener(this);
        this.vista.getBtnSignUp().addActionListener(this);

        this.mainController.addAppStateListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == this.vista.getBtnAceptar()) {
            System.out.println("aceptar");

            String userName = this.vista.getTxtUsuario().getText().trim();
            String puertoStr = this.vista.getTxtPuerto().getText().trim();

            boolean puertoValido = true;
            boolean nombreValido = true;

            // Validar el nombre
            if (userName.isEmpty()) {
                vista.getTxtUsuario().putClientProperty(
                        FlatClientProperties.OUTLINE, "error");
                vista.getLblErrorUsuario().setText("*El nombre de usuario no debe estar vacío");
                nombreValido = false;
            } else {
                vista.getTxtUsuario().putClientProperty(
                        FlatClientProperties.OUTLINE, null);
                vista.getLblErrorUsuario().setText("");
            }

            // Validar el puerto
            try {
                int puerto = Integer.parseInt(puertoStr);
                if (puerto < 1000 || puerto > 65535) {
                    vista.getTxtPuerto().putClientProperty(
                            FlatClientProperties.OUTLINE, "error");
                    vista.getLblErrorPuerto().setText("*El puerto debe estar entre 1000 y 65535");
                    puertoValido = false;
                } else {
                    vista.getTxtPuerto().putClientProperty(
                            FlatClientProperties.OUTLINE, null);
                    vista.getLblErrorPuerto().setText("");
                }
            } catch (NumberFormatException err) {
                vista.getTxtPuerto().putClientProperty(
                        FlatClientProperties.OUTLINE, "error");
                vista.getLblErrorPuerto().setText("*El puerto debe ser un número y no estar vacío");
                puertoValido = false;
            }

            if (puertoValido && nombreValido) {
                int puerto = Integer.parseInt(puertoStr);
                user = new User();
                user.setNombreUsuario(userName);
                user.setPuerto(puerto);
                System.out.println(user);

                Comando c = new Comando(TipoSolicitud.INICIAR_SESION, user);

                mainController.onResponse(c);
            }
        }

        if (e.getSource() == vista.getBtnSignUp()) {
//            mainController.showFormRegister();
            FormManager.register();

        }
    }

    public Login getVista() {
        return vista;
    }

    public void setVista(Login vista) {
        this.vista = vista;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

    @Override
    public void onConnectionAttempt(TipoRespuesta tipoRespuesta) {

    }

    @Override
    public void onRegistrationFailure(){

    }

    @Override
    public void onLoginSuccess(User user) {
        cleanup();
        FormManager.login();
    }

    @Override
    public void onLoginFailure(Exception e) {
        ErrorManager.getInstance().showError(e);
    }

    @Override
    public void onLogoutSuccess() {
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
    public void onDirectoryInfoReceived(Object directoryData) {

    }

    public void cleanup() {
        if (this.mainController != null) {
            this.mainController.removeAppStateListener(this);
            System.out.println("LoginController desregistrado como listener.");
        }
    }
}
