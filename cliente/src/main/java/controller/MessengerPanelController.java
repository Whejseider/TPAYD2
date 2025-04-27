package controller;

import connection.Cliente;
import connection.Sesion;
import interfaces.AppStateListener;
import model.*;
import raven.modal.Toast;
import view.NuevoChat;
import view.forms.MessengerPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessengerPanelController implements ActionListener, ListSelectionListener, AppStateListener {
    private MessengerPanel vista;
    private Cliente cliente = Cliente.getInstance();
    private User user = Sesion.getInstance().getUsuarioActual();
    private Contacto contactoActual;
    private MainController mainController = MainController.getInstance();

    public MessengerPanelController(MessengerPanel vista) {
        this.vista = vista;

        this.vista.getBtnEnviar().addActionListener(this);
        this.vista.getBtnNuevoChat().addActionListener(this);
        this.vista.getListChat().addListSelectionListener(this);

        this.mainController.addAppStateListener(this);
    }


    public MessengerPanel getVista() {
        return vista;
    }


    public void setVista(MessengerPanel vista) {
        this.vista = vista;
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

    public void procesaMensajeEntrante(Mensaje mensaje){

        User receptor = mensaje.getReceptor().getUser();
        Sesion.getInstance().setUsuarioActual(receptor); // Actualizo el usuario?
        this.user = Sesion.getInstance().getUsuarioActual(); // Y la variable local

        User emisor = mensaje.getEmisor();

        Contacto contacto = receptor.getAgenda().getContactoPorUsuario(emisor);

        //Agrega la conversacion a la vista
        Conversacion conversacion = receptor.getConversacionCon(contacto);
        this.getVista().agregarConversacion(conversacion);

        if (contacto.equals(this.getContactoActual())) {
            this.recibirMensaje(mensaje);
        } else {
            conversacion.getNotificacion().setTieneMensajesNuevos(true);
            this.getVista().getListChat().repaint();
            this.getVista().getListChat().revalidate();
        }
    }

    public void mostrarChat(Contacto contacto) {
        SwingUtilities.invokeLater(() -> {
            contactoActual = contacto;

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

            vista.getTxtAreaConversacion().setText(historial.toString());
            vista.getTxtAreaConversacion().revalidate();
            vista.getTxtAreaConversacion().repaint();
        });

    }

    public Contacto getContactoActual() {
        return contactoActual;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Conversacion conversacion = this.vista.getListChat().getSelectedValue();
            if (conversacion != null) {

                conversacion.getNotificacion().setTieneMensajesNuevos(false);

                vista.getListChat().revalidate();
                this.vista.getListChat().repaint();

                vista.mostrarContactoInfo(conversacion.getContacto());

                setContactoActual(conversacion.getContacto());
                mostrarChat(conversacion.getContacto());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnEnviar()) {
            String contenido = vista.getTxtMensaje().getText().trim();
            if (contenido.isEmpty() || contactoActual == null) return;

            Mensaje mensaje = new Mensaje(contenido, this.user, contactoActual);

            cliente.enviarMensaje(mensaje);

            Conversacion conversacion = mensaje.getEmisor().getConversacionCon(mensaje.getReceptor());

            DefaultListModel<Conversacion> listModel = this.vista.getListModel();
            if (!listModel.contains(conversacion)) {
                listModel.addElement(conversacion);
                vista.getListChat().setSelectedValue(conversacion, true);
            }

            vista.getTxtMensaje().setText("");

        }

        if (e.getSource() == this.vista.getBtnNuevoChat()) {
            NuevoChat nuevoChat = new NuevoChat();
            NuevoChatController nuevoChatController = new NuevoChatController(nuevoChat);
            nuevoChatController.setMessengerController(this);
            nuevoChat.setControlador(nuevoChatController);
            nuevoChat.display();
        }

    }

    public void enviarMensaje(Mensaje mensaje) {
        SwingUtilities.invokeLater(() -> {
            //TODO puede que haya otra forma mejor
            Sesion.getInstance().setUsuarioActual(mensaje.getEmisor());
            this.user = Sesion.getInstance().getUsuarioActual();
            vista
                    .getTxtAreaConversacion()
                    .append("\t\t\tYo: " + mensaje.getContenido() + "\n" + "\t\t\t" + mensaje.getTiempoFormateado() + "\n");
        });

    }

    public void recibirMensaje(Mensaje mensaje) {
        String alias = mensaje.getReceptor().getUser().getAgenda().getContactoPorUsuario(mensaje.getEmisor()).getAlias();
        SwingUtilities.invokeLater(() -> {
            vista
                    .getTxtAreaConversacion()
                    .append(alias + ": " + mensaje.getContenido() + "\n" + mensaje.getTiempoFormateado() + "\n");
        });

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
    public void onMessageReceivedSuccess(Mensaje mensaje) {
        procesaMensajeEntrante(mensaje);
    }

    @Override
    public void onMessageReceivedFailure(String s) {
        Toast.show(vista, Toast.Type.ERROR, s);
    }

    @Override
    public void onRegistrationSuccess() {

    }

    @Override
    public void onRegistrationFailure(String s) {

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
    public void onSendMessageSuccess(Mensaje mensaje) {
        enviarMensaje(mensaje);
    }

    @Override
    public void onSendMessageFailure(String s) {
        Toast.show(vista, Toast.Type.ERROR, s);
    }
}
