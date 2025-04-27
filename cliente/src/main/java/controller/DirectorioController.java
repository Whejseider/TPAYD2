package controller;

import connection.Cliente;
import connection.Sesion;
import interfaces.AppStateListener;
import model.*;
import raven.modal.Toast;
import view.forms.FormDirectorio;
import view.forms.other.Card;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DirectorioController implements AppStateListener, ActionListener {

    private FormDirectorio vista;
    private Directorio directorio; //TODO crear clase aparte?
    private MainController mainController = MainController.getInstance();
    private User usuarioSesion = Sesion.getInstance().getUsuarioActual();

    public DirectorioController(FormDirectorio vista) {
        this.vista = vista;
        this.directorio = new Directorio();
        this.mainController.addAppStateListener(this);
    }

    private void cargarDirectorio() {
        SwingUtilities.invokeLater(() -> {
            vista.getPanelCard().removeAll();
            for (User u : directorio.getDirectorio()) {
                if (!u.getNombreUsuario().equalsIgnoreCase(usuarioSesion.getNombreUsuario())) {

                    Card c = new Card();
                    c.getTitle().setText(u.getNombreUsuario());
                    c.getDescription().setText(u.getIP() + " : " + u.getPuerto());
                    c.addAgregarListener(this);
                    c.getBtnAgregar().setActionCommand("agregar_" + u.getNombreUsuario());

                    boolean estaAgregado = usuarioSesion.getAgenda().existeContacto(u);

                    if (estaAgregado) {
                        c.getBtnAgregar().setEnabled(false);
                        c.getBtnAgregar().setText("Contacto agregado");
                    }
                    vista.getPanelCard().add(c);
                }
            }
            vista.getPanelCard().repaint();
            vista.getPanelCard().revalidate();
        });
    }

    private Contacto crearContacto(User userSeleccionado) {
        Contacto c = new Contacto();
        c.setUser(userSeleccionado);
        c.setNombreUsuario(userSeleccionado.getNombreUsuario());
        c.setAlias(userSeleccionado.getNombreUsuario());
        c.setIP(userSeleccionado.getIP());
        c.setPuerto(userSeleccionado.getPuerto());
        return c;
    }

    //ACTION LISTENER
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (comando.startsWith("agregar_")) {
            String nombreUsuario = comando.substring("agregar_".length());
            System.out.println("agregar a " + nombreUsuario);

            User userSeleccionado = null;
            for (User u : directorio.getDirectorio()) {
                if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                    userSeleccionado = u;
                    break;
                }
            }

            if (userSeleccionado != null) {
                Contacto c = crearContacto(userSeleccionado);
                Cliente.getInstance().agregarContacto(c);
            }
        }
    }


    //APPSTATELISTENER

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
    public void onMessageReceivedSuccess(Mensaje mensaje) {

    }

    @Override
    public void onMessageReceivedFailure(String s) {

    }

    @Override
    public void onRegistrationSuccess() {

    }

    @Override
    public void onRegistrationFailure(String s) {
    }

    @Override
    public void onDirectoryInfoReceived(Directorio directorio) {
        this.directorio.getDirectorio().clear();  // Limpia el ArrayList actual
        this.directorio.getDirectorio().addAll(directorio.getDirectorio());  // Agrega todos los elementos del nuevo
        cargarDirectorio();
    }

    @Override
    public void onAddContactSuccess(User user) {
        Toast.show(vista, Toast.Type.SUCCESS, "Contacto agregado correctamente.");
        Sesion.getInstance().setUsuarioActual(user);
        this.usuarioSesion = Sesion.getInstance().getUsuarioActual();
        cargarDirectorio();
    }

    @Override
    public void onAddContactFailure(String s) {
        Toast.show(vista, Toast.Type.ERROR, s);
    }

    @Override
    public void onSendMessageSuccess(Mensaje contenido) {

    }

    @Override
    public void onSendMessageFailure(String s) {

    }

}
