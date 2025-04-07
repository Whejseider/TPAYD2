package service;

import controller.MessengerController;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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

            Mensaje mensaje = new Mensaje(contenido, this.messengerController.getUser());

            out.writeObject(mensaje);
            out.flush();

            Conversacion conversacion = this.messengerController.getUser().getConversacionCon(destino);
            conversacion.agregarMensaje(mensaje);

            if (destino.equals(this.messengerController.getContactoActual())) {
                this.messengerController.mostrarChat(destino);
            }

//            this.messengerController.getVista().getTxtAreaConversacion().append("YO:" + contenido);

            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
