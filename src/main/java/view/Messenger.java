package view;

import controller.MessengerController;
import interfaces.IVista;
import model.Contacto;
import utils.ChatListRenderer;

import javax.swing.*;
import java.awt.*;

public class Messenger extends JFrame implements IVista<MessengerController> {
    private MessengerController controlador;
    private MessengerPanel messengerPanel;

    public Messenger() throws HeadlessException {
        super("Messenger");
        messengerPanel = new MessengerPanel();
        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.requestFocus();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void setControlador(MessengerController controlador) {
        this.controlador = controlador;
    }


    public MessengerPanel getMessengerPanel() {
        return messengerPanel;
    }
}
