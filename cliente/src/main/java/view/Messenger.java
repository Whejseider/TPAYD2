package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.ControllerManager;
import interfaces.IVista;
import view.drawer.MyDrawerBuilder;
import view.forms.MessengerPanel;
import view.system.FormManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Messenger extends JFrame implements IVista<ControllerManager> {
    private ControllerManager controlador;
    private MessengerPanel messengerPanel;
    public static MyDrawerBuilder myDrawerBuilder;

    public Messenger(ControllerManager controlador) throws HeadlessException {
        this.controlador = controlador;
        ControllerManager.getInstance().setVista(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                FormManager.install(Messenger.this);
            }
        });

        setSize(new Dimension(1366, 768));
        setLocationRelativeTo(null);
        setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                controlador.cerrarTodo();
            }
        });
    }

    @Override
    public void setControlador(ControllerManager controlador) {
        this.controlador = controlador;
    }

    public MessengerPanel getMessengerPanel() {
        return messengerPanel;
    }

    public ControllerManager getControlador() {
        return controlador;
    }


}
