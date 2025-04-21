package service;

import controller.MessengerController;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;
import raven.toast.Notifications;

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

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeObject(mensaje);
                out.flush();

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
//                socket.close();
            } catch (Exception e) {
                Notifications
                        .getInstance()
                        .show(
                                Notifications.Type.ERROR,
                                Notifications.Location.TOP_RIGHT,
                                "Error al enviar el enviar el mensaje.\n" +
                                        "El destinatario [" + mensaje.getReceptor().getAlias() + "]"
                                        + " no se encuentra en línea.");
            }
        }).start();

    }


}
