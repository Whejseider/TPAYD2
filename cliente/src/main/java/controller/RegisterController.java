package controller;

import com.formdev.flatlaf.FlatClientProperties;
import connection.Cliente;
import interfaces.AppStateListener;
import model.*;
import raven.modal.Toast;
import view.forms.FormRegister;
import view.system.FormManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterController implements ActionListener, AppStateListener {
    private FormRegister vista;
    private MainController mainController;
    private User user;

    public RegisterController(FormRegister vista) {
        this.vista = vista;
        this.mainController = MainController.getInstance();
        this.vista.getBtnAceptar().addActionListener(this);
        this.vista.getBtnLogin().addActionListener(this);

        this.mainController.addAppStateListener(this);
    }

    public FormRegister getVista() {
        return vista;
    }

    public MainController getMainController() {
        return mainController;
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

    }

    @Override
    public void onLogoutFailure(String s) {

    }

    @Override
    public void onNewMessageReceived(Mensaje mensaje) {

    }

    @Override
    public void onUserListUpdated(List<User> userList) {

    }

    @Override
    public void onRegistrationSuccess() {
        Toast.show(vista, Toast.Type.SUCCESS, "Usuario registrado correctamente.");
        FormManager.showLogin();
    }

    @Override
    public void onRegistrationFailure(String s) {
        ErrorManager.getInstance().showError(s);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnAceptar()) {
            System.out.println("aceptar");

            String userName = this.vista.getTxtUsuario().getText().trim();
            String puertoStr = this.vista.getTxtPuerto().getText().trim();
            String ip = this.vista.getTxtIP().getText().trim();

            boolean puertoValido = true;
            boolean nombreValido = true;
            boolean ipValida = true;

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

            if (ip.isEmpty()) {
                vista.getTxtIP().putClientProperty(
                        FlatClientProperties.OUTLINE, "error");
                vista.getLblErrorIP().setText("*La dirección IP no puede estar vacía");
                ipValida = false;
            } else {
                if (ip.equalsIgnoreCase("localhost")) {
                    ip = "127.0.0.1"; //TODO
                }
                String regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
                Pattern pattern = Pattern.compile(regex);
                if (!pattern.matcher(ip).matches()) {
                    vista.getTxtIP().putClientProperty(
                            FlatClientProperties.OUTLINE, "error");
                    vista.getLblErrorIP().setText("*La dirección IP no es válida");
                    ipValida = false;
                } else {
                    vista.getTxtIP().putClientProperty(
                            FlatClientProperties.OUTLINE, null);
                    vista.getLblErrorIP().setText("");
                }
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

            if (puertoValido && nombreValido && ipValida) {
                int puerto = Integer.parseInt(puertoStr);
                user = new User();
                user.setNombreUsuario(userName);
                user.setPuerto(puerto);
                System.out.println(user);

                Cliente.getInstance().registrarse(user);
            }
        }

        if (e.getSource() == vista.getBtnLogin()) {
            FormManager.showLogin();
        }
    }
}
