package controller;

import model.Contacto;
import model.User;
import view.NuevoChat;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class NuevoChatController implements ActionListener, ListSelectionListener {
    private NuevoChat vista;
    private User user;
    private Contacto contacto;

    public NuevoChatController(NuevoChat vista, User user) {
        this.vista = vista;
        this.user = user;
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

    }
}
