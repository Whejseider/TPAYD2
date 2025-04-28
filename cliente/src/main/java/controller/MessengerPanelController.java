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
    private Contacto contactoActual;
    private MainController mainController = MainController.getInstance();

    public MessengerPanelController(MessengerPanel vista) {
        this.vista = vista;

        this.vista.getBtnEnviar().addActionListener(this);
        this.vista.getBtnNuevoChat().addActionListener(this);
        this.vista.getListChat().addListSelectionListener(this);

        this.mainController.addAppStateListener(this);
        cargarConversaciones();
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

    public void setContactoActual(Contacto contactoActual) {
        this.contactoActual = contactoActual;
    }

    public void procesaMensajeEntrante(Mensaje mensaje) {

        User receptor = mensaje.getReceptor().getUser();
        User emisor = mensaje.getEmisor();
        Contacto contacto = receptor.getAgenda().getContactoPorUsuario(emisor);
        Conversacion conversacion = receptor.getConversacionCon(emisor);

        SwingUtilities.invokeLater(() -> {
            DefaultListModel<Conversacion> listModel = this.vista.getListModel();
            if (!listModel.contains(conversacion)) {
                listModel.addElement(conversacion);
            }

            if (contacto.equals(this.getContactoActual())) {
                this.recibirMensaje(mensaje);
            } else {
                revalidarListChat();
//                System.out.println("DEBUG - Antes de set: " + conversacion.getNotificacion().tieneMensajesNuevos());
                conversacion.getNotificacion().setTieneMensajesNuevos(true);
                listModel.set(listModel.indexOf(conversacion), conversacion);
//                System.out.println("DEBUG - Después de set: " + conversacion.getNotificacion().tieneMensajesNuevos());
            }

        });
    }

    public void mostrarChat(Contacto contacto) {
        SwingUtilities.invokeLater(() -> {
            contactoActual = contacto;

            Conversacion conversacion = Sesion.getInstance().getUsuarioActual().getConversacionCon(contacto);
            StringBuilder historial = new StringBuilder();
            if (!vista.getListModel().contains(conversacion)) {
                vista.getListModel().addElement(conversacion);
            }

            for (Mensaje mensaje : conversacion.getMensajes()) {
                if (mensaje.getEmisor().getNombreUsuario().equals(Sesion.getInstance().getUsuarioActual().getNombreUsuario())) {
                    historial.append("\t\t\t").append("Yo: ").append(mensaje.getContenido()).append("\n").append("\t\t\t").append(mensaje.getTiempoFormateado()).append("\n");
                } else {
                    historial.append(contacto.getNombreUsuario()).append(": ")
                            .append(mensaje.getContenido()).append("\n").append(mensaje.getTiempoFormateado()).append("\n");
                }
            }

            revalidarTxtConversacion();
            vista.getTxtAreaConversacion().setText(historial.toString());
        });

    }

    public void revalidarTxtConversacion() {
        vista.getTxtAreaConversacion().revalidate();
        vista.getTxtAreaConversacion().repaint();
    }

    public void revalidarListChat() {

        vista.getListChat().revalidate();
        vista.getListChat().repaint();

    }

    public void cargarConversaciones() {
        SwingUtilities.invokeLater(() -> {
            DefaultListModel<Conversacion> model = vista.getListModel();
            model.clear();

            for (Conversacion conversacion : Sesion.getInstance().getUsuarioActual().getConversacion().values()) {
                model.addElement(conversacion);
            }

            revalidarListChat();
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
                SwingUtilities.invokeLater(() -> {
                    setContactoActual(conversacion.getContacto());
                    mostrarChat(conversacion.getContacto());


                    revalidarListChat();
                    conversacion.getNotificacion().setTieneMensajesNuevos(false);

                    vista.mostrarContactoInfo(conversacion.getContacto());
                });
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnEnviar()) {
            String contenido = vista.getTxtMensaje().getText().trim();
            if (contenido.isEmpty() || contactoActual == null) return;

            Mensaje mensaje = new Mensaje(contenido, Sesion.getInstance().getUsuarioActual(), contactoActual);

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
        Sesion.getInstance().setUsuarioActual(mensaje.getReceptor().getUser()); //A Futuro actualizar los datos del usuario, y no el usuario en si
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
        Sesion.getInstance().setUsuarioActual(mensaje.getEmisor());
        enviarMensaje(mensaje);
    }

    @Override
    public void onSendMessageFailure(String s) {
        Toast.show(vista, Toast.Type.ERROR, s);
    }
}
