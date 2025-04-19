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

    public NuevoChatController(NuevoChat vista, User user) {
        this.vista = vista;
        this.user = user;
        this.messengerController = messengerController;
        this.vista.getList1().addListSelectionListener(this);

        for (Contacto c: this.user.getAgenda().getContactos()){
            this.vista.agregarContacto(c);
        }
    }

    public MessengerController getMessengerController() {
        return messengerController;
    }

    public void setMessengerController(MessengerController messengerController) {
        this.messengerController = messengerController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Contacto seleccionado = this.vista.getList1().getSelectedValue();
            if (seleccionado != null) {
                this.messengerController.mostrarChat(seleccionado);
                this.messengerController.getVista().getMessengerPanel().getListChat().setSelectedValue(seleccionado, true);
                this.vista.dispose();
            }
        }
    }
}
