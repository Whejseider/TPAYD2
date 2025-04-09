package service;

import controller.MessengerController;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;

import java.io.*;
import java.net.Socket;

public class Cliente {
    private MessengerController messengerController;

    public Cliente(MessengerController messengerController) {
        this.messengerController = messengerController;
    }

    public void enviarMensaje(String contenido, Contacto destino) {
        try {
            Socket socket = new Socket(destino.getIP(), destino.getPuerto());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Mensaje mensaje = new Mensaje(contenido, this.messengerController.getUser());

            out.writeObject(mensaje);
            out.flush();

            User destinoRecibido = (User) in.readObject();

            if (destino.getNombreUsuario().equalsIgnoreCase("Usuario Desconocido")){
                Contacto contactoModificado = new Contacto(destinoRecibido.getNombreUsuario(),destinoRecibido.getIP(), destinoRecibido.getPuerto());
                this.messengerController.getUser().getAgenda().modificarContactoPorIP(destino, contactoModificado);
                destino.setNombreUsuario(contactoModificado.getNombreUsuario());
            }

            Conversacion conversacion = this.messengerController.getUser().getConversacionCon(destino);
            conversacion.agregarMensaje(mensaje);

            if (destino.equals(this.messengerController.getContactoActual())) {
                this.messengerController.mostrarChat(destino);
            }

            out.close();
            socket.close();
        } catch (Exception e) {
            this.messengerController.getVista().getTxtAreaConversacion().append(
                    "Error al enviar el enviar el mensaje. El destinatario " + "[" + destino.toString() + "]" + " no se encuentra en línea.");
        }
    }


}
