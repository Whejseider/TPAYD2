package service;

import controller.MessengerController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private MessengerController messengerController;

    public Servidor(MessengerController messengerController) {
        this.messengerController = messengerController;
        this.iniciarServidor();
    }

    public void iniciarServidor() {
        new Thread(() -> {
            try {
                Integer puerto = this.messengerController.getUser().getPuerto();
                ServerSocket s = new ServerSocket(puerto);
                this.messengerController.getVista().getTxtAreaConversacion().append("Esperando conexiones en puerto: " + puerto + "\n");
                while (true) {
                    Socket soc = s.accept();
//                    PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

                    String msg = in.readLine();
                    this.messengerController.getVista().getTxtAreaConversacion().append(msg + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.messengerController.getVista().getTxtAreaConversacion().append(e.getMessage() + "\n");
            }
            this.messengerController.getVista().getTxtAreaConversacion().append("fin");
        }).start();
    }

    public MessengerController getMessengerController() {
        return messengerController;
    }
}
