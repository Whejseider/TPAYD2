package controller;

import model.Contacto;
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

    public MessengerController(Messenger vista, User user) {
        this.vista = vista;
        this.user = user;
        this.servidor = new Servidor(this);

        this.vista.getBtnEnviar().addActionListener(this);
        this.vista.getBtnNuevoChat().addActionListener(this);
        this.vista.getBtnNuevoContacto().addActionListener(this);
        this.vista.getListChat().addListSelectionListener(this);
    }

    public Messenger getVista() {
        return vista;
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

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnEnviar()) {
        }

        if (e.getSource() == this.vista.getBtnNuevoContacto()) {
            NuevoContacto nuevoContacto = new NuevoContacto();
            NuevoContactoController nuevoContactoController = new NuevoContactoController(nuevoContacto, this.user);
            nuevoContacto.display();
        }

        if (e.getSource() == this.vista.getBtnNuevoChat()) {
            NuevoChat nuevoChat = new NuevoChat();
            NuevoChatController nuevoChatController = new NuevoChatController(nuevoChat, this.user);
            nuevoChat.display();
        }
    }

}
