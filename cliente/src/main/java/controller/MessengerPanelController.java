package controller;

import connection.Cliente;
import connection.Sesion;
import interfaces.IController;
import interfaces.MessageListener;
import interfaces.SessionListener;
import model.*;
import raven.modal.Toast;
import view.NuevoChat;
import view.forms.Messenger.Item;
import view.forms.Messenger.LeftActionListener;
import view.forms.Messenger.MessengerPanel;
import view.manager.ToastManager;
import view.system.Form;
import view.system.FormManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class MessengerPanelController implements IController, LeftActionListener, ActionListener, MessageListener, SessionListener {
    private MessengerPanel vista;
    private Cliente cliente = Cliente.getInstance();
    private Conversacion conversacionActual;
    private EventManager eventManager = EventManager.getInstance();

    private LocalDate ultimaFechaMostradaEnChat = null;

    public MessengerPanelController(MessengerPanel form) {
        this.vista = form;
        this.vista.getLeftPanel().setEvent(this);
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

    public void setConversacionActual(Conversacion contactoActual) {
        this.conversacionActual = contactoActual;
    }

    private void actualizarVistaMensaje(Mensaje mensaje) {
        SwingUtilities.invokeLater(() -> {
            if (vista.getChat() == null) return;

            boolean esMio = mensaje.esMio(Sesion.getInstance().getUsuarioActual());

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

    public void procesaMensajeEntrante(Mensaje mensaje, User emisor) {

        User usuarioActual = Sesion.getInstance().getUsuarioActual();
        if (usuarioActual == null) return;

        if (mensaje.getNombreEmisor().equals(usuarioActual.getNombreUsuario())) {
            return;
        }

        Contacto contactoEmisor = Agenda.crearContacto(emisor);
        usuarioActual.getAgenda().agregarContacto(contactoEmisor);

        Conversacion conversacion = usuarioActual.getConversacionCon(mensaje.getNombreEmisor());
        conversacion.agregarMensaje(mensaje);
        conversacion.setUltimoMensaje(conversacion.getUltimoMensaje());

        SwingUtilities.invokeLater(() -> {

            vista.getLeftPanel().userMessage(conversacion, mensaje);

            if (conversacionActual != null && contactoEmisor != null &&
                    conversacionActual.getContacto().getNombreUsuario().equals(contactoEmisor.getNombreUsuario())) {
                actualizarVistaMensaje(mensaje);
                conversacion.getNotificacion().setTieneMensajesNuevos(false);
            } else {
                if (conversacion.getNotificacion() != null) {
                    conversacion.getNotificacion().setTieneMensajesNuevos(true);
                }
            }

            Item item = vista.getLeftPanel().getSelectedConversation(conversacion);
            if (item != null) {
                item.actualizarApariencia();
            }

            Sesion.getInstance().saveUserData();

            vista.getLeftPanel().getScroll().getScrollRefreshModel().stop();
            vista.getLeftPanel().getScroll().getScrollRefreshModel().resetPage();

        });
    }

    public void mostrarChat(Conversacion conversacion) {
        SwingUtilities.invokeLater(() -> {

            vista.getChat().clearMessages();

            ultimaFechaMostradaEnChat = null;

            conversacionActual = conversacion;

            List<Mensaje> mensajes = conversacion.getMensajes();

            mensajes.sort(Comparator.comparing(Mensaje::getTiempo));

            for (Mensaje mensaje : conversacion.getMensajes()) {
                actualizarVistaMensaje(mensaje);
            }

        });

    }

    public Conversacion getConversacionActual() {
        return conversacionActual;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnEnviar()) {
            String contenido = vista.getTxtMensaje().getText().trim();
            if (contenido.isEmpty() || conversacionActual == null) {
                vista.getTxtMensaje().grabFocus();
            } else {

                Mensaje mensaje = new Mensaje(contenido, Sesion.getInstance().getUsuarioActual().getNombreUsuario(), conversacionActual.getContacto().getNombreUsuario());

                Conversacion conversacion = Sesion.getInstance().getUsuarioActual().getConversacionCon(conversacionActual.getContacto().getNombreUsuario());
                conversacion.agregarMensaje(mensaje);
                conversacion.setUltimoMensaje(mensaje);

                vista.getLeftPanel().userMessage(conversacion, mensaje);

                actualizarVistaMensaje(mensaje);

                if (vista.getLeftPanel().getSelectedConversation(conversacion) == null) {
                    vista.getLeftPanel().initData();
                    vista.getLeftPanel().selectedConversation(conversacion);
                }

                messageInputFocus();

                Cliente.getInstance().enviarMensaje(mensaje);

            }
        }

        if (e.getSource() == this.vista.getLeftPanel().getBtnNuevoChat()) {
            NuevoChat nuevoChat = new NuevoChat();
            NuevoChatController nuevoChatController = new NuevoChatController(nuevoChat);
            nuevoChatController.setMessengerController(this);
            nuevoChat.setControlador(nuevoChatController);
            nuevoChat.display();
        }

    }

    private void messageInputFocus() {
        vista.getTxtMensaje().setText("");
        vista.getTxtMensaje().grabFocus();
    }

    @Override
    public void onMessageReceivedSuccess(Mensaje mensajeRecibido, User user) {
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

        procesaMensajeEntrante(mensajeRecibido, user);

    }

    @Override
    public void onMessageReceivedFailure(String s) {
//        Toast.show(vista, Toast.Type.ERROR, s);
    }

    @Override
    public void onSendMessageSuccess(Mensaje mensajeEnviadoConfirmado) {
        String emisor = mensajeEnviadoConfirmado.getNombreEmisor();
        String receptor = mensajeEnviadoConfirmado.getNombreReceptor();
        User usuarioActual = Sesion.getInstance().getUsuarioActual();

        if (usuarioActual == null || !usuarioActual.getNombreUsuario().equals(emisor)) {
            System.err.println("MessengerPanelController: Confirmación de mensaje para un emisor/sesión incorrecto.");
            return;
        }

        Conversacion conversacion = usuarioActual.getConversacionCon(receptor);

        conversacion.agregarMensaje(mensajeEnviadoConfirmado);
        conversacion.setUltimoMensaje(mensajeEnviadoConfirmado);

        Sesion.getInstance().saveUserData();

    }

    @Override
    public void onSendMessageFailure(String s) {
        ToastManager.getInstance().showToast(Toast.Type.ERROR, s);
    }

    @Override
    public void init() {

        this.eventManager.addMessageListener(this);
        this.eventManager.addSessionListener(this);
    }

    @Override
    public Form getForm() {
        return vista;
    }

    @Override
    public void onConversationSelected(Conversacion conversacion) {
        changeConversation(conversacion);
    }

    public void changeConversation(Conversacion conversacion) {
        if (conversacion != null) {
            if (!conversacion.getMensajes().isEmpty()) {

                conversacion.getNotificacion().setTieneMensajesNuevos(false);
                Item itemSeleccionado = vista.getLeftPanel().getSelectedConversation(conversacion);
                if (itemSeleccionado != null) {
                    itemSeleccionado.actualizarApariencia();
                }

                vista.getLeftPanel().selectedConversation(conversacion);
                vista.getLeftPanel().getScroll().getScrollRefreshModel().stop();
                vista.getChat().clearMessages();
                vista.getLeftPanel().getScroll().getScrollRefreshModel().resetPage();
                mostrarChat(conversacion);
                checkUser(conversacion.getContacto());
            } else {
                messageInputFocus();
                vista.getChat().clearMessages();
                checkUser(conversacion.getContacto());
            }
        } else {
            messageInputFocus();
            vista.getChat().scrollToBottom();
        }
    }

    private void checkUser(Contacto contacto) {
        try {
            vista.mostrarContactoInfo(contacto);
            messageInputFocus();
        } catch (Exception e) {
            messageInputFocus();
            System.out.println("CHECKUSER MSNPANEL: " + e.getMessage());
        }
    }

    @Override
    public void onSessionReloaded() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("MessengerPanelController: Sesión refrescada, actualizando UI.");
            User usuarioActualSesion = Sesion.getInstance().getUsuarioActual();
            if (usuarioActualSesion == null) {
                System.err.println("MessengerPanelController.onSessionRefreshed: Usuario actual es null. No se puede refrescar.");
                FormManager.showLogin();
                return;
            }


            String nombreContactoConversacionActual = null;
            if (this.conversacionActual != null && this.conversacionActual.getContacto() != null) {
                nombreContactoConversacionActual = this.conversacionActual.getContacto().getNombreUsuario();
            }
            this.conversacionActual = null;


            if (vista != null && vista.getLeftPanel() != null) {
                vista.getLeftPanel().initData();
                vista.getLeftPanel().getScroll().getScrollRefreshModel().stop();
                vista.getLeftPanel().getScroll().getScrollRefreshModel().resetPage();
            }


            if (nombreContactoConversacionActual != null) {
                Conversacion conversacionRestaurada = usuarioActualSesion.getConversacionCon(nombreContactoConversacionActual);
                if (conversacionRestaurada != null) {

                    changeConversation(conversacionRestaurada);
                } else {

                    vista.getChat().clearMessages();
                    ultimaFechaMostradaEnChat = null;
                    vista.mostrarContactoInfo(null);
                    if (vista.getLeftPanel() != null) {
                        vista.getLeftPanel().selectedConversation(null);
                    }
                }
            } else {

                vista.getChat().clearMessages();
                ultimaFechaMostradaEnChat = null;
                vista.mostrarContactoInfo(null);
            }
        });
    }
}
