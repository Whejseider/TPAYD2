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
import view.manager.ToastManager;
import view.system.Form;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DirectorioController implements IController, DocumentListener, DirectoryListener, ContactsListener, ActionListener {

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
                    createCard(u);
                }
            }
            revalidarPanelCard();
        });
    }

    private void revalidarPanelCard() {
        vista.getPanelCard().repaint();
        vista.getPanelCard().revalidate();
    }

    private void filtrarUsuarios(String texto) {
        SwingUtilities.invokeLater(() -> {
            vista.getPanelCard().removeAll();

            for (User u : directorio.getDirectorio()) {
                if (!u.getNombreUsuario().equalsIgnoreCase(Sesion.getInstance().getUsuarioActual().getNombreUsuario())
                        && u.getNombreUsuario().toLowerCase().contains(texto.toLowerCase())) {
                    createCard(u);
                }
            }

            revalidarPanelCard();
        });
    }

    private void actualizarPanel(){
        filtrarUsuarios(this.vista.getTxtSearch().getText().trim());
    }

    private void createCard(User u) {
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
            ToastManager.getInstance().showToast(Toast.Type.SUCCESS, "Contacto agregado correctamente.");

            cargarDirectorio();
            System.out.println("DIRECTORIO: CONTACTO AGREGADO");
        }
    }

    @Override
    public void onAddContactFailure(String reason) {

    }


    // Busqueda
    @Override
    public void insertUpdate(DocumentEvent e) {
        actualizarPanel();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        actualizarPanel();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        actualizarPanel();
    }
}
