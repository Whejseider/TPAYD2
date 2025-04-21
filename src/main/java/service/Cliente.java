package service;

import controller.MessengerController;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente {
    private MessengerController messengerController;

    public Cliente(MessengerController messengerController) {
        this.messengerController = messengerController;
    }

    public void enviarMensaje(Mensaje mensaje) {
        new Thread(() -> {
            try (Socket socket = new Socket(mensaje.getReceptor().getIP(), mensaje.getReceptor().getPuerto())) {


                // Primero crear el OutputStream
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush(); // Importante flush antes de crear el InputStream

                // Después crear el InputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                // Ahora enviar el mensaje
                out.writeObject(mensaje);
                out.flush();

                // Y recibir la respuesta
                User input = (User) in.readObject();
                Contacto receptor = mensaje.getReceptor();

                if (receptor.getAlias().equalsIgnoreCase("Usuario Desconocido")) {

                    Contacto contactoModificado = new Contacto();
                    contactoModificado.setUser(input);
                    contactoModificado.setNombreUsuario(input.getNombreUsuario());
                    contactoModificado.setAlias(input.getNombreUsuario());
                    contactoModificado.setIP(input.getIP());
                    contactoModificado.setPuerto(input.getPuerto());

                    this.messengerController.getUser().getAgenda().modificarContactoPorIP(receptor, contactoModificado);
                    receptor.setAlias(contactoModificado.getAlias());
                }

                receptor.setUser(input);

                Conversacion conversacion = this.messengerController.getUser().getConversacionCon(receptor);
                conversacion.agregarMensaje(mensaje);

                if (receptor.equals(this.messengerController.getContactoActual())) {
                    this.messengerController.enviarMensaje(mensaje);
                }

                in.close();
                out.close();
                socket.close();
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    this.messengerController.getVista().getMessengerPanel().getTxtAreaConversacion().append(
                            "Error al enviar el enviar el mensaje. El destinatario " +
                                    "[" + mensaje.getReceptor().toString() + "]"
                                    + " no se encuentra en línea.");
                });

            }
        }).start();

    }


}
