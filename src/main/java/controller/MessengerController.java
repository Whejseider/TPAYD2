package controller;

import model.Contacto;
import model.Mensaje;
import model.User;
import service.Cliente;
import service.Servidor;
import view.Messenger;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MessengerController implements MouseListener {
    private Messenger vista;
    private Servidor servidor;
    private Cliente cliente;
    private User user;
    private Contacto contactoActual;

    public MessengerController(Messenger vista, User user) {
        this.vista = vista;
        this.user = user;
        this.servidor = new Servidor(this);

        this.vista.getBtnEnviar().addMouseListener(this);
        this.vista.getBtnNuevoChat().addMouseListener(this);
        this.vista.getBtnNuevoContacto().addMouseListener(this);
        this.vista.getListChat().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Contacto contactoSeleccionado = (Contacto) vista.getListChat().getSelectedValue();
                    if (contactoSeleccionado != null) {
                        mostrarChat(contactoSeleccionado);
                    }
                }
            }



        });
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
    public void mouseClicked(MouseEvent e) {

    }
    private void mostrarChat(Contacto contacto) {
        contactoActual = contacto;
        vista.getLblNombreMensaje().setText(contacto.getNombreUsuario());
        vista.getLblIP().setText("IP: " + contacto.getIP());
        vista.getLblPuerto().setText("Puerto: " + contacto.getPuerto());

        StringBuilder historial = new StringBuilder();
        for (Mensaje m : user.getMensajesDe(contacto)) {
            historial.append(m.getContenido()).append("\n");
        }

        vista.getTxtAreaConversacion().setText(historial.toString());
    }
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == this.vista.getBtnEnviar()){
            if (contactoActual != null){
                String contenido = vista.getTextField2().getText().trim();
                if (!contenido.isEmpty()){
                    Mensaje mensaje = new Mensaje(contenido, user.getIP());
                    user.agregarMensaje(mensaje);
                    mostrarChat(contactoActual);
                    vista.getTextField2().setText("");
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
