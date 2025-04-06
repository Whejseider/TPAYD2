package controller;

import model.User;
import view.Configuration;
import view.Messenger;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ConfigurationController implements MouseListener {
    private Configuration vista;
    private User user;

    public ConfigurationController(Configuration configuration) {
        this.vista = configuration;
        this.vista.getBtnAceptar().addMouseListener(this);
        this.vista.getBtnCancelar().addMouseListener(this);
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == this.vista.getBtnCancelar()) {
            System.out.println("cancelar");
            System.exit(0);
        }
        if (e.getSource() == this.vista.getBtnAceptar()) {
            System.out.println("aceptar");
            String userName = this.vista.getTxtUsuario().getText();
            String puertoStr = this.vista.getTxtPuerto().getText();

            int puerto = -1;
            try {
                puerto = Integer.parseInt(puertoStr);
                if (puerto > 999 && puerto <= 65535) {
                    user = new User(this.vista.getTxtUsuario().getText(), Integer.parseInt(this.vista.getTxtPuerto().getText()));
                    System.out.println(user.toString());
                    this.vista.dispose();

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        Messenger messengerView = new Messenger("Messenger");
                        MessengerController messengerController = new MessengerController(messengerView, user);
                    });

                } else {
                    this.vista.getLblPuertoError().setVisible(true);
                    this.vista.getLblPuertoError().setText("El puerto debe estar entre 1000 y 65535");
                    this.vista.display();
                }
            } catch (NumberFormatException err) {
                this.vista.getLblPuertoError().setVisible(true);
                this.vista.getLblPuertoError().setText("El puerto debe ser un número y no estar vacío");
                this.vista.display();
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
