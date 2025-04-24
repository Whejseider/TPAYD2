package view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import controller.LoginController;
import controller.MessengerController;
import interfaces.IVista;
import model.Contacto;
import model.User;
import raven.modal.Drawer;
import utils.ChatListRenderer;
import view.menu.MyDrawerBuilder;
import view.system.FormManager;

import javax.swing.*;
import java.awt.*;

public class Messenger extends JFrame implements IVista<MessengerController> {
    private MessengerController controlador;
    private MessengerPanel messengerPanel;

    public Messenger() throws HeadlessException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        Drawer.installDrawer(this, new MyDrawerBuilder());
        FormManager.install(this);
        setSize(new Dimension(1366, 768));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void setControlador(MessengerController controlador) {
        this.controlador = controlador;
    }

    public MessengerPanel getMessengerPanel() {
        return messengerPanel;
    }

    public MessengerController getControlador() {
        return controlador;
    }
}
