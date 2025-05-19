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

    /**
     * Si hay tiempo, me gustaria mover esto a la primera carga, y despues verificar si cambio
     * el directorio del server y agregar los paneles 1x1, y no redibujar todo
     */
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

                    boolean estaAgregado = Sesion.getInstance().getUsuarioActual().getAgenda().existeContacto(u.getNombreUsuario());

                    if (estaAgregado) {
                        c.getBtnAgregar().setEnabled(false);
                        c.getBtnAgregar().setText("Contacto agregado");
                    }
                    vista.getPanelCard().add(c);
                }
            }
            actualizarPanelCard();
        });
    }

    private void actualizarPanelCard() {
        vista.getPanelCard().repaint();
        vista.getPanelCard().revalidate();
    }

    private Contacto crearContacto(User userSeleccionado) {
        Contacto c = new Contacto();
        c.setNombreUsuario(userSeleccionado.getNombreUsuario());
        c.setAlias(userSeleccionado.getNombreUsuario());
        c.setIP(userSeleccionado.getIP());
        c.setPuerto(userSeleccionado.getPuerto());
        return c;
    }

    @Override
    public void init() {
        this.directorio = new Directorio();
        initListeners();
    }

    private void initListeners() {
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

                Cliente.getInstance().agregarContacto(userSeleccionado.getNombreUsuario());
            }
        }
    }


    @Override
    public void onDirectoryInfoReceived(Directorio directorio) {
        this.directorio.getDirectorio().clear();
        this.directorio.getDirectorio().addAll(directorio.getDirectorio());
        cargarDirectorio();
    }

    @Override
    public void onAddContactSuccess(Contacto contacto) {
        if (this.vista.isVisible()) {
            Sesion.getInstance().getUsuarioActual().getAgenda().agregarContacto(contacto);
            Toast.show(vista, Toast.Type.SUCCESS, "Contacto agregado correctamente.");
            cargarDirectorio();
            System.out.println("DIRECTORIO: CONTACTO AGREGADO");
        }
    }

    @Override
    public void onAddContactFailure(String reason) {

    }

}
