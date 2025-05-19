package controller;

import com.formdev.flatlaf.FlatClientProperties;
import connection.Cliente;
import connection.Sesion;
import interfaces.AuthenticationListener;
import interfaces.IController;
import model.User;
import network.NetworkConstants;
import raven.modal.Toast;
import view.forms.Login;
import view.manager.ErrorManager;
import view.system.Form;
import view.system.FormManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class LoginController implements IController, ActionListener, AuthenticationListener {
    private Login vista;
    private EventManager eventManager;

    public LoginController(Login vista) {
        this.vista = vista;
    }

    private void initMessenger() {
        try {
            Socket socket = new Socket(NetworkConstants.IP_DEFAULT, NetworkConstants.PUERTO_CLIENTES_DEFAULT);
            Cliente cliente = Cliente.getInstance();
            cliente.init(socket);
            cliente.escuchar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {

        this.eventManager = EventManager.getInstance();
        this.eventManager.addAuthenticationListener(this);

//        this.initMessenger();
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

            // Validar el puerto,se puede refactorizar
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
                User user = new User(userName, puerto);
                System.out.println(user);

                Cliente.getInstance().iniciarSesion(user);
            }
        }

        if (e.getSource() == vista.getBtnSignUp()) {
            FormManager.showRegister();
        }
    }

    public Login getVista() {
        return vista;
    }

    public void setVista(Login vista) {
        this.vista = vista;
    }


    @Override
    public void onLoginSuccess(User user) {
        removeListener();
        Sesion.getInstance().setUsuarioActual(user);
        Toast.show(vista, Toast.Type.SUCCESS, "Bienvenido " + user.getNombreUsuario());
        FormManager.showHome();
    }

    @Override
    public void onLoginFailure(String s) {
        ErrorManager.getInstance().showError(s);
    }

    @Override
    public void onLogoutSuccess() {
        addListener();
    }

    @Override
    public void onLogoutFailure(String s) {
        addListener();
    }

    @Override
    public void onRegistrationSuccess() {

    }

    @Override
    public void onRegistrationFailure(String s) {

    }


    public void removeListener() {
        this.eventManager = EventManager.getInstance();

        this.eventManager.removeAuthenticationListener(this);
        System.out.println("LoginController desregistrado como listener.");

    }

    public void addListener() {
        this.eventManager = EventManager.getInstance();

        this.eventManager.addAuthenticationListener(this);
        System.out.println("LoginController registrado como listener.");
    }

}
