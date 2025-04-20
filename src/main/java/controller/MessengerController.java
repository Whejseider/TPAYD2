package controller;

import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;
import service.Cliente;
import service.Servidor;
import view.Messenger;
import view.NuevoChat;
import view.NuevoContacto;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessengerController implements ActionListener, ListSelectionListener {
    private Messenger vista;
    private Servidor servidor;
    private Cliente cliente;
    private User user;
    private Contacto contactoActual;
    private boolean darkMode = false;

    public MessengerController(Messenger vista) {
        this.vista = vista;

        this.vista.getMessengerPanel().getBtnEnviar().addActionListener(this);
        this.vista.getMessengerPanel().getBtnNuevoChat().addActionListener(this);
        this.vista.getMessengerPanel().getBtnNuevoContacto().addActionListener(this);
        this.vista.getMessengerPanel().getListChat().addListSelectionListener(this);
        this.vista.getMessengerPanel().getBtnDarkMode().addActionListener(this);
    }

    public void setTituloVentana() {
        this.vista.setTitle(this.vista.getTitle() +
                " - Usuario: " + this.user.getNombreUsuario() +
                "  IP: " + this.user.getIP() +
                "  Puerto: " + this.user.getPuerto());
    }

    public Messenger getVista() {
        return vista;
    }

    public void configurarServidor() {
        this.servidor = new Servidor(this);
    }

    public void configurarCliente() {
        this.cliente = new Cliente(this);
    }

    public void setVista(Messenger vista) {
        this.vista = vista;
    }

    public Servidor getServidor() {
        return servidor;
    }

    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setContactoActual(Contacto contactoActual) {
        this.contactoActual = contactoActual;
    }

    public void mostrarChat(Contacto contacto) {
        contactoActual = contacto;

        vista.getMessengerPanel().getPanelContactoInfo().setVisible(true);

        if (contacto.getAlias().isEmpty()) {
            vista.getMessengerPanel().getLblNombreMensaje().setText(contacto.getNombreUsuario());
        } else {
            vista.getMessengerPanel().getLblNombreMensaje().setText(contacto.getAlias());
        }

        vista.getMessengerPanel().getLblIP().setText("IP: " + contacto.getIP());
        vista.getMessengerPanel().getLblPuerto().setText("Puerto: " + contacto.getPuerto());

        Conversacion conversacion = user.getConversacionCon(contacto);
        StringBuilder historial = new StringBuilder();

        for (Mensaje mensaje : conversacion.getMensajes()) {
            if (mensaje.getEmisor().getNombreUsuario().equals(user.getNombreUsuario())) {
                historial.append("\t\t\t").append("Yo: ").append(mensaje.getContenido()).append("\n").append("\t\t\t").append(mensaje.getTiempoFormateado()).append("\n");
            } else {
                historial.append(contacto.getNombreUsuario()).append(": ")
                        .append(mensaje.getContenido()).append("\n").append(mensaje.getTiempoFormateado()).append("\n");
            }
        }

        vista.getMessengerPanel().getTxtAreaConversacion().setText(historial.toString());
    }

    public Contacto getContactoActual() {
        return contactoActual;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Contacto contactoSeleccionado = this.vista.getMessengerPanel().getListChat().getSelectedValue();
            if (contactoSeleccionado != null) {

                contactoSeleccionado.getNotificacion().setTieneMensajesNuevos(false);

                this.vista.getMessengerPanel().getListChat().repaint();

                setContactoActual(contactoSeleccionado);
                mostrarChat(contactoSeleccionado);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getMessengerPanel().getBtnEnviar()) {
            String mensaje = vista.getMessengerPanel().getTxtMensaje().getText().trim();
            if (mensaje.isEmpty() || contactoActual == null) return;

            this.configurarCliente();

            DefaultListModel<Contacto> listModel = this.vista.getMessengerPanel().getListModel();
            if (!listModel.contains(contactoActual)) {
                listModel.addElement(contactoActual);
                vista.getMessengerPanel().getListChat().setSelectedValue(contactoActual, true);
            }

            vista.getMessengerPanel().getTxtMensaje().setText("");
            mostrarChat(contactoActual);

            cliente.enviarMensaje(mensaje, contactoActual);
        }

        if (e.getSource() == this.vista.getMessengerPanel().getBtnNuevoContacto()) {
            NuevoContacto nuevoContacto = new NuevoContacto(this.vista);
            NuevoContactoController nuevoContactoController = new NuevoContactoController(nuevoContacto, this.user);
            nuevoContacto.setControlador(nuevoContactoController);
            nuevoContacto.display();
        }

        if (e.getSource() == this.vista.getMessengerPanel().getBtnNuevoChat()) {
            NuevoChat nuevoChat = new NuevoChat(this.vista);
            NuevoChatController nuevoChatController = new NuevoChatController(nuevoChat, this.user);
            nuevoChatController.setMessengerController(this);
            nuevoChat.setControlador(nuevoChatController);
            nuevoChat.display();
        }

        if (e.getSource() == this.vista.getMessengerPanel().getBtnDarkMode()) {
            darkMode = !darkMode;
            this.vista.getMessengerPanel().cambiarTema(darkMode);
        }

    }

}
