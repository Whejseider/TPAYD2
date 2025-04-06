package service;

import controller.MessengerController;
import model.Contacto;

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
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Mandás algo como "TuNombre:Hola!"
            out.println(messengerController.getUser().getNombreUsuario() + ":" + contenido);

            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
