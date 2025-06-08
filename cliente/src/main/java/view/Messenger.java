package view;

import connection.Cliente;
import view.system.FormManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Messenger extends JFrame  {

    public Messenger() throws HeadlessException {
        setName("Messenger");
        setTitle("Messenger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
                Cliente.getInstance().cerrarTodo(true);
            }
        });
    }

}
