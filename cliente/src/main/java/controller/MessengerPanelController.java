package controller;

import connection.Cliente;
import connection.Sesion;
import interfaces.IController;
import interfaces.MessageListener;
import model.*;
import raven.modal.Toast;
import view.NuevoChat;
import view.forms.MessengerPanel;
import view.manager.ToastManager;
import view.system.Form;
import view.system.FormManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class MessengerPanelController implements IController, ActionListener, ListSelectionListener, MessageListener {
    private MessengerPanel vista;
    private Cliente cliente = Cliente.getInstance();
    private Contacto contactoActual;
    private EventManager eventManager = EventManager.getInstance();

    private LocalDate ultimaFechaMostradaEnChat = null;

    public MessengerPanelController(MessengerPanel form) {
        this.vista = form;
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

    private void actualizarVistaMensaje(Mensaje mensaje, boolean esMio) {
        SwingUtilities.invokeLater(() -> {
            if (vista.getChat() == null) return;

            LocalDate fechaMensaje = mensaje.getTiempo().toLocalDate();
            LocalDate hoy = LocalDate.now();
            LocalDate ayer = hoy.minusDays(1);

            if (ultimaFechaMostradaEnChat == null || !fechaMensaje.isEqual(ultimaFechaMostradaEnChat)) {
                String textoFecha;
                if (fechaMensaje.isEqual(hoy)) {
                    textoFecha = "Hoy";
                } else if (fechaMensaje.isEqual(ayer)) {
                    textoFecha = "Ayer";
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    textoFecha = fechaMensaje.format(formatter);
                }
                vista.getChat().addDate(textoFecha);
                ultimaFechaMostradaEnChat = fechaMensaje;
            }

            if (esMio) {
                vista.getChat().addItemRight(mensaje);
            } else {
                vista.getChat().addItemLeft(mensaje);
            }

            vista.getChat().scrollToBottom();
        });
    }

    public void procesaMensajeEntrante(Mensaje mensaje) {

        User usuarioActual = Sesion.getInstance().getUsuarioActual();
        if (usuarioActual == null) return;

        Contacto contactoEmisor = usuarioActual.getAgenda().getContactoPorNombre(mensaje.getEmisor().getNombreUsuario());

        Conversacion conversacion = usuarioActual.getConversacionCon(mensaje.getEmisor().getNombreUsuario());

        SwingUtilities.invokeLater(() -> {
            DefaultListModel<Conversacion> listModel = this.vista.getListModel();

            if (!listModel.contains(conversacion)) {
                listModel.addElement(conversacion);
            } else {
                listModel.setElementAt(conversacion, listModel.indexOf(conversacion));
            }

            if (contactoActual != null && contactoEmisor != null &&
                    contactoActual.getNombreUsuario().equals(contactoEmisor.getNombreUsuario())) {
                actualizarVistaMensaje(mensaje, false);
            } else {
                if (conversacion != null && conversacion.getNotificacion() != null) {
                    conversacion.getNotificacion().setTieneMensajesNuevos(true);
//                    ToastManager.getInstance().showNotifyMessage(contactoEmisor);
                }
                revalidarListChat();
            }
        });
    }

    public void mostrarChat(Contacto contacto) {
        SwingUtilities.invokeLater(() -> {

            vista.getChat().clearMessages();
            
            ultimaFechaMostradaEnChat = null;

            contactoActual = contacto;

            Conversacion conversacion = Sesion.getInstance().getUsuarioActual().getConversacionCon(contacto.getNombreUsuario());
            if (!vista.getListModel().contains(conversacion)) {
                vista.getListModel().addElement(conversacion);
            }

            List<Mensaje> mensajes = conversacion.getMensajes();

            mensajes.sort(Comparator.comparing(Mensaje::getTiempo));

            for (Mensaje mensaje : conversacion.getMensajes()) {
                boolean esMio = mensaje.getEmisor().getNombreUsuario().equalsIgnoreCase(Sesion.getInstance().getUsuarioActual().getNombreUsuario());
                actualizarVistaMensaje(mensaje, esMio);
            }

        });

    }

    public void revalidarPanelMensajes() {
        vista.getChat().clearMessages();
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
                if (conversacion.getMensajes() != null && !conversacion.getMensajes().isEmpty()) {
                    model.addElement(conversacion);
                }
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

                    vista.getTxtMensaje().setText("");
                    vista.getTxtMensaje().grabFocus();
                });
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnEnviar()) {
            String contenido = vista.getTxtMensaje().getText().trim();
            if (contenido.isEmpty() || contactoActual == null) {
                vista.getTxtMensaje().grabFocus();
            } else {

                Mensaje mensaje = new Mensaje(contenido, Sesion.getInstance().getUsuarioActual(), contactoActual.getNombreUsuario());

                Conversacion conversacion = Sesion.getInstance().getUsuarioActual().getConversacionCon(contactoActual.getNombreUsuario());

                DefaultListModel<Conversacion> listModel = this.vista.getListModel();
                if (!listModel.contains(conversacion)) {
                    listModel.addElement(conversacion);
                } else {
                    listModel.setElementAt(conversacion, listModel.indexOf(conversacion));
                }

                if (vista.getListChat().getSelectedValue() != conversacion) {
                    vista.getListChat().setSelectedValue(conversacion, true);
                }

                actualizarVistaMensaje(mensaje, true);

                vista.getTxtMensaje().setText("");
                vista.getTxtMensaje().grabFocus();

                Cliente.getInstance().enviarMensaje(mensaje);

            }
        }

        if (e.getSource() == this.vista.getBtnNuevoChat()) {
            NuevoChat nuevoChat = new NuevoChat();
            NuevoChatController nuevoChatController = new NuevoChatController(nuevoChat);
            nuevoChatController.setMessengerController(this);
            nuevoChat.setControlador(nuevoChatController);
            nuevoChat.display();
        }

    }

    @Override
    public void onMessageReceivedSuccess(Mensaje mensajeRecibido) {
        String receptor = mensajeRecibido.getNombreReceptor();

        User usuarioActual = Sesion.getInstance().getUsuarioActual();

        if (usuarioActual == null) {
            System.err.println("MessengerPanelController: usuarioActualEnSesion es null en onMessageReceivedSuccess");
            return;
        }

        if (!usuarioActual.getNombreUsuario().equals(receptor)) {
            System.err.println("MessengerPanelController: Mensaje recibido para un usuario diferente al de la sesión actual. Ignorando.");
            return;
        }

        Contacto c = Agenda.crearContacto(mensajeRecibido.getEmisor());
        usuarioActual.getAgenda().agregarContacto(c);
        usuarioActual.getConversacionCon(c.getNombreUsuario()).agregarMensaje(mensajeRecibido);
        usuarioActual.getConversacionCon(c.getNombreUsuario()).setUltimoMensaje(mensajeRecibido);

        procesaMensajeEntrante(mensajeRecibido);

    }

    @Override
    public void onMessageReceivedFailure(String s) {
//        Toast.show(vista, Toast.Type.ERROR, s);
    }

    @Override
    public void onSendMessageSuccess(Mensaje mensajeEnviadoConfirmado) {
        String emisor = mensajeEnviadoConfirmado.getEmisor().getNombreUsuario();
        String receptor = mensajeEnviadoConfirmado.getNombreReceptor();
        User usuarioActual = Sesion.getInstance().getUsuarioActual();

        if (usuarioActual == null || !usuarioActual.getNombreUsuario().equals(emisor)) {
            System.err.println("MessengerPanelController: Confirmación de mensaje para un emisor/sesión incorrecto.");
            return;
        }

        Conversacion conversacion = usuarioActual.getConversacionCon(receptor);

        conversacion.agregarMensaje(mensajeEnviadoConfirmado);
        conversacion.setUltimoMensaje(mensajeEnviadoConfirmado);

        if (getContactoActual() != null && getContactoActual().getNombreUsuario().equals(receptor)) {
            SwingUtilities.invokeLater(() -> {
                DefaultListModel<Conversacion> listModel = vista.getListModel();
                Conversacion conversacionEnSesion = usuarioActual.getConversacionCon(receptor);
                int index = listModel.indexOf(conversacionEnSesion);
                if (index != -1) {
                    listModel.setElementAt(conversacionEnSesion, index);
                }
            });
        } else {
            SwingUtilities.invokeLater(this::cargarConversaciones);
        }
    }

    /**
     * TODO
     *
     * @param s
     */
    @Override
    public void onSendMessageFailure(String s) {
        ToastManager.getInstance().showToast(Toast.Type.ERROR, s);
    }

    @Override
    public void init() {

        this.eventManager.addMessageListener(this);

        cargarConversaciones();
    }

    @Override
    public Form getForm() {
        return vista;
    }
}
