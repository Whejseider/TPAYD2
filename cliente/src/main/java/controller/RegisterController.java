package controller;

import com.formdev.flatlaf.FlatClientProperties;
import connection.Cliente;
import interfaces.AuthenticationListener;
import interfaces.IController;
import model.User;
import raven.modal.Toast;
import view.forms.FormRegister;
import view.manager.ToastManager;
import view.system.Form;
import view.system.FormManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class RegisterController implements IController, ActionListener, AuthenticationListener {
    private FormRegister vista;
    private EventManager eventManager;
    private User user;

    public RegisterController(FormRegister form) {
        this.vista = form;
    }

    public FormRegister getVista() {
        return vista;
    }

    public EventManager getMainController() {
        return eventManager;
    }

    @Override
    public void init() {
        this.eventManager = EventManager.getInstance();

        this.eventManager.addAuthenticationListener(this);
    }

    @Override
    public Form getForm() {
        return vista;
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
                user = new User(userName, puerto);
                System.out.println(user);

                Cliente.getInstance().registrarse(user);
            }
        }

        if (e.getSource() == vista.getBtnLogin()) {
            FormManager.showLogin();
        }
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
    public void onRegistrationSuccess() {
        ToastManager.getInstance().showToast(Toast.Type.SUCCESS, "Usuario registrado correctamente.");
        FormManager.showLogin();
    }

    @Override
    public void onRegistrationFailure(String s) {
        ToastManager.getInstance().showToast(Toast.Type.ERROR, s);
    }


}
