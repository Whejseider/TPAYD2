package controller;

import connection.Cliente;
import connection.Sesion;
import interfaces.ContactsListener;
import interfaces.DirectoryListener;
import interfaces.IController;
import model.Contacto;
import model.Directorio;
import model.User;
import raven.modal.Toast;
import view.forms.FormDirectorio;
import view.forms.other.Card;
import view.system.Form;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DirectorioController implements IController, DirectoryListener, ContactsListener, ActionListener {

    private FormDirectorio vista;
    private Directorio directorio;
    private EventManager eventManager = EventManager.getInstance();

    public DirectorioController(FormDirectorio form) {
        this.vista = form;

    }

    private void cargarDirectorio() {
        SwingUtilities.invokeLater(() -> {
            vista.getPanelCard().removeAll();
            for (User u : directorio.getDirectorio()) {
                if (!u.getNombreUsuario().equalsIgnoreCase(Sesion.getInstance().getUsuarioActual().getNombreUsuario())) {

                    Card c = new Card();
                    c.getTitle().setText(u.getNombreUsuario());
                    c.getDescription().setText(u.getIP() + " : " + u.getPuerto());
                    c.addAgregarListener(this);
                    c.getBtnAgregar().setActionCommand("agregar_" + u.getNombreUsuario());

                    boolean estaAgregado = Sesion.getInstance().getUsuarioActual().getAgenda().existeContacto(u);

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

    @Override
    public void init() {
        this.directorio = new Directorio();
        this.eventManager.addDirectoryListener(this);
        this.eventManager.addContactsListener(this);
    }

    @Override
    public Form getForm() {
        return vista;
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
        cargarDirectorio();
    }

    @Override
    public void onAddContactFailure(String reason) {

    }

}
