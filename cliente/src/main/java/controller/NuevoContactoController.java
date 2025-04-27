package controller;

import com.formdev.flatlaf.FlatClientProperties;
import connection.Sesion;
import model.Contacto;
import model.User;
import view.forms.NuevoContacto;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class NuevoContactoController implements ActionListener {
    private NuevoContacto vista;
    private User user = Sesion.getInstance().getUsuarioActual();
    private Contacto contacto;

    public NuevoContactoController(NuevoContacto vista) {
        this.vista = vista;

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
                    vista.getTxtPuerto().putClientProperty(
                            FlatClientProperties.OUTLINE, "error");
                    vista.getLblErrorPuerto().setText("*El puerto debe estar entre 1000 y 65535");
                    puertoValido = false;
                } else {
                    vista.getTxtPuerto().putClientProperty(
                            FlatClientProperties.OUTLINE, null);
                    vista.getLblErrorPuerto().setText("");
                }
            } catch (NumberFormatException err) {
                vista.getTxtPuerto().putClientProperty(
                        FlatClientProperties.OUTLINE, "error");
                vista.getLblErrorPuerto().setText("*El puerto debe ser un número y no estar vacío");
                puertoValido = false;
            }

            // Validar la IP
            if (ip.isEmpty()) {
                vista.getTxtIP().putClientProperty(
                        FlatClientProperties.OUTLINE, "error");
                vista.getLblErrorIP().setText("*La dirección IP no puede estar vacía");
                ipValida = false;
            } else {
                if (ip.equalsIgnoreCase("localhost")) {
                    ip = "127.0.0.1"; //TODO
                }
                String regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
                Pattern pattern = Pattern.compile(regex);
                if (!pattern.matcher(ip).matches()) {
                    vista.getTxtIP().putClientProperty(
                            FlatClientProperties.OUTLINE, "error");
                    vista.getLblErrorIP().setText("*La dirección IP no es válida");
                    ipValida = false;
                } else {
                    vista.getTxtIP().putClientProperty(
                            FlatClientProperties.OUTLINE, null);
                    vista.getLblErrorIP().setText("");
                }
            }

            if (puertoValido && ipValida) {
                int puerto = Integer.parseInt(puertoStr);
                contacto = new Contacto();
                contacto.setAlias(alias);
                contacto.setIP(ip);
                contacto.setPuerto(puerto);

                if (this.user.getAgenda().getContactos().contains(contacto)) {
//                    Notifications
//                            .getInstance()
//                            .show(
//                                    Notifications.Type.ERROR,
//                                    Notifications.Location.TOP_RIGHT,
//                                    "Ya hay otro contacto registrado igual"
//                            );
                } else {
                    this.user.getAgenda().agregarContacto(contacto);
//                    Notifications
//                            .getInstance()
//                            .show(
//                                    Notifications.Type.SUCCESS,
//                                    Notifications.Location.TOP_RIGHT,
//                                    "Contacto agregado correctamente");
                }
                this.vista.dispose();
            }
        }
    }
}