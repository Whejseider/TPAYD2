package controller;

import model.User;
import view.Configuracion;
import view.Messenger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ConfigurationController implements ActionListener {
    private Configuracion vista;
    private User user;

    public ConfigurationController(Configuracion vista) {
        this.vista = vista;
        this.vista.getBtnAceptar().addActionListener(this);
        this.vista.getBtnCancelar().addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnCancelar()) {
            System.out.println("cancelar");
            System.exit(0);
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
                user = new User(userName, puerto);
                System.out.println(user.toString());

                Messenger messengerVista = new Messenger("Messenger");
                MessengerController controller = new MessengerController(messengerVista);
                controller.setUser(this.user);

                this.vista.dispose();
            }
        }
    }

}
