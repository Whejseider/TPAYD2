package service;

import controller.MessengerController;
import model.Contacto;

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
                    BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

                    String msg = in.readLine(); // Por ejemplo: "Remitente:Mensaje"
                    if (msg == null || msg.isEmpty()) continue;

                    // Desglosar el mensaje si viene como "usuario:mensaje"
                    String[] partes = msg.split(":", 2);
                    if (partes.length != 2) continue;

                    String remitenteNombre = partes[0].trim();
                    String contenido = partes[1].trim();

                    // Buscar contacto con ese nombre (esto depende de cómo identificás contactos)
                    Contacto remitente = messengerController.getUser().getContactoPorNombre(remitenteNombre);

                    // Si no existe, lo podés ignorar o agregarlo temporalmente
                    if (remitente == null) {
                        // Omitimos por ahora
                        continue;
                    }

                    // Crear el mensaje
                    model.Mensaje mensaje = new model.Mensaje(remitenteNombre, contenido);

                    // Obtener conversación y agregar mensaje
                    model.Conversacion conversacion = messengerController.getUser().getConversacionCon(remitente);
                    conversacion.agregarMensaje(mensaje);

                    // Si el contacto está abierto en el chat, actualizar vista
                    if (remitente.equals(messengerController.getContactoActual())) {
                        messengerController.mostrarChat(remitente);
                    }

                    // (opcional) imprimir log
                    System.out.println("Mensaje recibido de " + remitenteNombre + ": " + contenido);
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
