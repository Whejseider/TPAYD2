package controller;

import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;
import service.Cliente;
import service.Servidor;
import view.Configuracion;
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

    public MessengerController(Messenger vista) {
        this.vista = vista;
        this.vista.getBtnEnviar().addActionListener(this);
        this.vista.getBtnNuevoChat().addActionListener(this);
        this.vista.getBtnNuevoContacto().addActionListener(this);
        this.vista.getListChat().addListSelectionListener(this);
        this.vista.getBtnLogin().addActionListener(this);
    }

    public Messenger getVista() {
        return vista;
    }

    public void configurarServidor(){
        this.servidor = new Servidor(this);
    }

    public void configurarCliente(){
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

        vista.getPanelContactoInfo().setVisible(true);
        vista.getLblNombreMensaje().setText(contacto.getNombreUsuario());
        vista.getLblIP().setText("IP: " + contacto.getIP());
        vista.getLblPuerto().setText("Puerto: " + contacto.getPuerto());

        Conversacion conversacion = user.getConversacionCon(contacto);
        StringBuilder historial = new StringBuilder();

        for (Mensaje mensaje : conversacion.getMensajes()) {
            if (mensaje.getRemitente().getNombreUsuario().equals(user.getNombreUsuario())) {
                // izquierda <<<
                historial.append("\t\t\t").append("Yo: ").append(mensaje.getContenido()).append("\n").append("\t\t\t").append(mensaje.toString()).append("\n");
            } else {
                // derecha >>>
                historial.append(contacto.getNombreUsuario()).append(": ")
                        .append(mensaje.getContenido()).append("\n").append(mensaje.toString()).append("\n");
            }
        }

        vista.getTxtAreaConversacion().setText(historial.toString());
    }

    public Contacto getContactoActual() {
        return contactoActual;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Contacto contactoSeleccionado = this.vista.getListChat().getSelectedValue();
            if (contactoSeleccionado != null) {

                contactoSeleccionado.setTieneMensajesNuevos(false);

                this.vista.getListChat().repaint();

                setContactoActual(contactoSeleccionado);
                mostrarChat(contactoSeleccionado);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnEnviar()) {
            String texto = vista.getTxtMensaje().getText().trim();
            if (texto.isEmpty() || contactoActual == null) return;

            this.configurarCliente();

            String contenido = this.vista.getTxtMensaje().getText().trim();

            DefaultListModel<Contacto> listModel = vista.getListModel();
            if (!listModel.contains(contactoActual)) {
                listModel.addElement(contactoActual);
                vista.getListChat().setSelectedValue(contactoActual, true);
            }

            vista.getTxtMensaje().setText("");
            mostrarChat(contactoActual);

            cliente.enviarMensaje(contenido, contactoActual);
        }

        if (e.getSource() == this.vista.getBtnNuevoContacto()) {
            NuevoContacto nuevoContacto = new NuevoContacto();
            NuevoContactoController nuevoContactoController = new NuevoContactoController(nuevoContacto, this.user);
            nuevoContacto.display();
        }

        if (e.getSource() == this.vista.getBtnNuevoChat()) {
            NuevoChat nuevoChat = new NuevoChat();
            NuevoChatController nuevoChatController = new NuevoChatController(nuevoChat, this.user, this);
            nuevoChat.display();
        }

        if (e.getSource() == this.vista.getBtnLogin()){
            Configuracion configuracion = new Configuracion();
            ConfigurationController configurationController = new ConfigurationController(configuracion, this);
            configuracion.display();
        }
    }

}
