package service;

import controller.MessengerController;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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
                    ObjectInputStream in = new ObjectInputStream(soc.getInputStream());

                    Mensaje mensajeRecibido = (Mensaje) in.readObject();
                    if (mensajeRecibido == null) continue;


                    User usuarioRemitente = mensajeRecibido.getRemitente();
                    String contenido = mensajeRecibido.getContenido();

                    Contacto contactoRemitente = this.messengerController.getUser().getAgenda().getContactoPorUsuario(usuarioRemitente);

                    if (contactoRemitente == null) {
                        // Crear nuevo contacto con la información del user recibido
                        contactoRemitente = new Contacto(usuarioRemitente.getNombreUsuario(), usuarioRemitente.getIP(), usuarioRemitente.getPuerto());
                        contactoRemitente.setNombreUsuario(usuarioRemitente.getNombreUsuario());
                        contactoRemitente.setIP(soc.getInetAddress().getHostAddress());
                        contactoRemitente.setPuerto(usuarioRemitente.getPuerto());

                        // Agregar el nuevo contacto
                        this.messengerController.getUser().getAgenda().agregarContacto(usuarioRemitente, contactoRemitente);
                        this.messengerController.getVista().agregarContacto(contactoRemitente);
                        this.messengerController.getVista().repaint();
                    }

                    // Obtener o crear la conversación
                    Conversacion conversacion = this.messengerController.getUser().getConversacionCon(contactoRemitente);
                    conversacion.agregarMensaje(mensajeRecibido);

                    if (contactoRemitente.equals(this.messengerController.getContactoActual())) {
                        this.messengerController.mostrarChat(contactoRemitente);
                    } else {
                        contactoRemitente.setTieneMensajesNuevos(true);
                        this.messengerController.getVista().getListChat().repaint();
                    }

                    in.close();
                    soc.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
                this.messengerController.getVista().getTxtAreaConversacion().append(e.getMessage() + "\n");
            }
        }).start();
    }

    public MessengerController getMessengerController() {
        return messengerController;
    }
}
