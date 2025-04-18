package controller;

import model.Contacto;
import model.User;
import view.NuevoContacto;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class NuevoContactoController implements ActionListener {
    private NuevoContacto vista;
    private User user;
    private Contacto contacto;

    public NuevoContactoController(NuevoContacto nuevoContacto, User user) {
        this.vista = nuevoContacto;
        this.user = user;

        this.vista.getBtnAceptar().addActionListener(this);
        this.vista.getBtnCancelar().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.getBtnCancelar()) {
            System.out.println("NuevoContacto - Cancelar");
            this.vista.dispose();
        }

        if (e.getSource() == this.vista.getBtnAceptar()) {
            System.out.println("NuevoContacto - Aceptar");

            this.vista.limpiarErrores();

            String alias = this.vista.getTxtUsuario().getText().trim();
            String puertoStr = this.vista.getTxtPuerto().getText().trim();
            String ip = this.vista.getTxtIP().getText().trim();

            //TODO:Primera vez carga el nombre de usuario como desconocido, luego del primer mensaje, le cambia el nombre
            if (alias.isEmpty()) {
                alias = "Usuario Desconocido";
            }

            boolean puertoValido = true;
            boolean ipValida = true;

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

            // Validar la IP
            if (ip.isEmpty()) {
                vista.mostrarErrorIP("La dirección IP no puede estar vacía");
                ipValida = false;
            } else {
                if (ip.equalsIgnoreCase("localhost")){
                    ip = "127.0.0.1"; //TODO
                }
                String regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
                Pattern pattern = Pattern.compile(regex);
                if (!pattern.matcher(ip).matches()) {
                    vista.mostrarErrorIP("La dirección IP no es válida");
                    ipValida = false;
                }
            }

            if (puertoValido && ipValida) {
                int puerto = Integer.parseInt(puertoStr);
                contacto = new Contacto();
                contacto.setAlias(alias);
                contacto.setIP(ip);
                contacto.setPuerto(puerto);
                System.out.println(contacto);

                this.user.getAgenda().agregarContacto(contacto);

                this.vista.dispose();
            }
        }
    }
}