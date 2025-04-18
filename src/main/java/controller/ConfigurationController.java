package controller;

import model.User;
import view.Configuracion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigurationController implements ActionListener {
    private Configuracion vista;
    private User user;
    private MessengerController messengerController;

    public ConfigurationController(Configuracion vista, MessengerController messengerController) {
        this.vista = vista;
        this.messengerController = messengerController;
        this.vista.getBtnAceptar().addActionListener(this);
        this.vista.getBtnCancelar().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnCancelar()) {
            System.out.println("cancelar");
            this.vista.dispose();
        }

        if (e.getSource() == this.vista.getBtnAceptar()) {
            System.out.println("aceptar");

            this.vista.limpiarErrores();

            String userName = this.vista.getTxtUsuario().getText().trim();
            String puertoStr = this.vista.getTxtPuerto().getText().trim();

            boolean puertoValido = true;

            // Validar el puerto
            try {
                int puerto = Integer.parseInt(puertoStr);
                if (puerto < 1000 || puerto > 65535) {
                    vista.mostrarErrorPuerto("El puerto debe estar entre 1000 y 65535");
                    puertoValido = false;
                }
            } catch (NumberFormatException err) {
                vista.mostrarErrorPuerto("El puerto debe ser un número y no estar vacío");
                puertoValido = false;
            }

            if (puertoValido){
                int puerto = Integer.parseInt(puertoStr);
                user = new User();
                user.setNombreUsuario(userName);
                user.setPuerto(puerto);
                System.out.println(user);

                this.messengerController.setUser(this.user);

                JOptionPane.showMessageDialog(
                        this.vista,
                        "Sesión iniciada correctamente.",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE
                );
                this.messengerController.getVista().getBtnLogin().setVisible(false);
                this.messengerController.getVista().getBtnLogin().setEnabled(false);
                this.messengerController.getVista().getBtnNuevoChat().setEnabled(true);
                this.messengerController.getVista().getBtnNuevoContacto().setEnabled(true);
                this.messengerController.getVista().getBtnEnviar().setEnabled(true);

                this.messengerController.configurarServidor();

                this.messengerController.getVista().setTitle(this.messengerController.getVista().getTitle() +
                        " - Usuario: " + this.user.getNombreUsuario() +
                        "  IP: " + this.user.getIP() +
                        "  Puerto: " + this.user.getPuerto());
                this.vista.dispose();
            }
        }
    }

}
