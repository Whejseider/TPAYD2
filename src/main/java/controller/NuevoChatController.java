package controller;

import model.Contacto;
import model.User;
import view.NuevoChat;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NuevoChatController implements ActionListener, ListSelectionListener {
    private NuevoChat vista;
    private User user;
    private Contacto contacto;
    private MessengerController messengerController;

    public NuevoChatController(NuevoChat vista, User user, MessengerController messengerController) {
        this.vista = vista;
        this.user = user;
        this.messengerController = messengerController;
        this.vista.getList1().addListSelectionListener(this);

        for (Contacto c: this.user.getContactos()){
            this.vista.agregarContacto(c);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Contacto seleccionado = this.vista.getList1().getSelectedValue();
            if (seleccionado != null) {
                this.messengerController.iniciarChat(seleccionado);
                this.messengerController.getVista().getLblNombreMensaje().setText(seleccionado.getNombreUsuario());
                this.messengerController.getVista().getLblIP().setText("IP: "+ seleccionado.getIP());
                this.messengerController.getVista().getLblPuerto().setText("Puerto: " + seleccionado.getPuerto());
                this.vista.dispose();
            }
        }
    }
}
