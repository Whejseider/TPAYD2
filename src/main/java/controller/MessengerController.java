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

    public void mostrarChat(Contacto contacto) {
        contactoActual = contacto;
        vista.getLblNombreMensaje().setText(contacto.getNombreUsuario());
        vista.getLblIP().setText("IP: " + contacto.getIP());
        vista.getLblPuerto().setText("Puerto: " + contacto.getPuerto());

        // Mostrar el historial de conversación
        Conversacion conversacion = user.getConversacionCon(contacto);
        StringBuilder historial = new StringBuilder();

        for (Mensaje mensaje : conversacion.getMensajes()) {
            historial.append(mensaje.getContenido()).append("\n");
        }

        vista.getTxtAreaConversacion().setText(historial.toString());
    }

    public Contacto getContactoActual() {
        return contactoActual;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnEnviar()) {
            String texto = vista.getTxtMensaje().getText().trim();
            if (texto.isEmpty() || contactoActual == null) return;

            // Crear mensaje
            Mensaje mensaje = new Mensaje(user.getNombreUsuario(), texto);

            // Obtener o crear conversación
            Conversacion conversacion = user.getConversacionCon(contactoActual);
            conversacion.agregarMensaje(mensaje);

            // Enviar por red
            cliente.enviarMensaje(texto, contactoActual);

            // Limpiar campo y actualizar vista
            vista.getTxtMensaje().setText("");
            mostrarChat(contactoActual);
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
    }

}
