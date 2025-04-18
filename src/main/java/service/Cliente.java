package service;

import controller.MessengerController;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente {
    private MessengerController messengerController;

    public Cliente(MessengerController messengerController) {
        this.messengerController = messengerController;
    }

    public void enviarMensaje(String contenido, Contacto contacto) {
        try {
            Socket socket = new Socket(contacto.getIP(), contacto.getPuerto());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Mensaje mensaje = new Mensaje(contenido, this.messengerController.getUser(), contacto);

            out.writeObject(mensaje);
            out.flush();

            User receptor = (User) in.readObject();

            if (contacto.getAlias().equalsIgnoreCase("Usuario Desconocido")){

                Contacto contactoModificado = new Contacto();
                contactoModificado.setAlias(receptor.getNombreUsuario());
                contactoModificado.setIP(receptor.getIP());
                contactoModificado.setPuerto(receptor.getPuerto());

                this.messengerController.getUser().getAgenda().modificarContactoPorIP(contacto, contactoModificado);
                contacto.setAlias(contactoModificado.getAlias());
            }

            Conversacion conversacion = this.messengerController.getUser().getConversacionCon(contacto);
            conversacion.agregarMensaje(mensaje);

            if (contacto.equals(this.messengerController.getContactoActual())) {
                this.messengerController.mostrarChat(contacto);
            }

            out.close();
            socket.close();
        } catch (Exception e) {
            this.messengerController.getVista().getTxtAreaConversacion().append(
                    "Error al enviar el enviar el mensaje. El destinatario " + "[" + contacto.toString() + "]" + " no se encuentra en línea.");
        }
    }


}
