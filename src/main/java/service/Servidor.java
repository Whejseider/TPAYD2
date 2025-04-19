package service;

import controller.MessengerController;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
                this.messengerController.getVista().getMessengerPanel().getTxtAreaConversacion().append("Esperando conexiones en puerto: " + puerto + "\n");

                while (true) {
                    Socket soc = s.accept();
                    User receptor = this.messengerController.getUser();
                    ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
                    out.writeObject(receptor);

                    Mensaje mensaje = (Mensaje) in.readObject();
                    if (mensaje == null) continue;

                    User emisor = mensaje.getEmisor();
                    receptor.setNombreUsuario(this.messengerController.getUser().getNombreUsuario());

                    Contacto contacto = receptor.getAgenda().getContactoPorUsuario(emisor);

                    if (contacto == null) {
                        contacto = new Contacto();
                        contacto.setNombreUsuario(emisor.getNombreUsuario());
                        contacto.setAlias(emisor.getNombreUsuario());
                        contacto.setIP(emisor.getIP());
                        contacto.setPuerto(emisor.getPuerto());
                        receptor.getAgenda().agregarContacto(contacto);
                        this.messengerController.getVista().getMessengerPanel().agregarContacto(contacto);
                        this.messengerController.getVista().getMessengerPanel().repaint();
                    }

                    Conversacion conversacion = receptor.getConversacionCon(contacto);
                    conversacion.agregarMensaje(mensaje);

                    if (contacto.equals(this.messengerController.getContactoActual())) {
                        this.messengerController.mostrarChat(contacto);
                    } else {
                        contacto.getNotificacion().setTieneMensajesNuevos(true);
                        this.messengerController.getVista().getMessengerPanel().getListChat().repaint();
                    }

                    in.close();
                    soc.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
                this.messengerController.getVista().getMessengerPanel().getTxtAreaConversacion().append("Error al establecer la conexión" + "\n");
            }
        }).start();
    }

    public MessengerController getMessengerController() {
        return messengerController;
    }
}
