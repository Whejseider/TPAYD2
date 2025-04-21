package service;

import controller.MessengerController;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;

import javax.swing.*;
import java.io.IOException;
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
                SwingUtilities.invokeLater(() -> this.messengerController.getVista().getMessengerPanel()
                        .getTxtAreaConversacion().append("Esperando conexiones en puerto: " + puerto + "\n"));

                while (true) {
                    try  {
                        Socket soc = s.accept();
                        new Thread(() -> {
                            handleClientConnection(soc);

                        }).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            this.messengerController.getVista().getMessengerPanel().getTxtAreaConversacion().append("Error al aceptar la conexión: " + e.getMessage() + "\n");
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.messengerController.getVista().getMessengerPanel().getTxtAreaConversacion().append("Error al establecer la conexión" + "\n");
            }
        }).start();
    }

    private void handleClientConnection(Socket soc) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(soc.getInputStream());


            Mensaje mensaje = (Mensaje) in.readObject();
            User receptor = this.messengerController.getUser();
            mensaje.getReceptor().setUser(receptor);

            out.writeObject(receptor);
            out.flush();

            User emisor = mensaje.getEmisor();
            receptor.setNombreUsuario(this.messengerController.getUser().getNombreUsuario()); //PORQUE?

            Contacto contacto = receptor.getAgenda().getContactoPorUsuario(emisor);

            if (contacto == null) {
                contacto = new Contacto();
                contacto.setUser(emisor);
                contacto.setNombreUsuario(emisor.getNombreUsuario());
                contacto.setAlias(emisor.getNombreUsuario());
                contacto.setIP(emisor.getIP());
                contacto.setPuerto(emisor.getPuerto());
                receptor.getAgenda().agregarContacto(contacto);
                this.messengerController.getVista().getMessengerPanel().agregarContacto(contacto);
                this.messengerController.getVista().getMessengerPanel().revalidate();
                this.messengerController.getVista().getMessengerPanel().repaint();
            }

            Conversacion conversacion = receptor.getConversacionCon(contacto);
            conversacion.agregarMensaje(mensaje);

            if (contacto.equals(this.messengerController.getContactoActual())) {
                this.messengerController.recibirMensaje(mensaje);
            } else {
                contacto.getNotificacion().setTieneMensajesNuevos(true);
                this.messengerController.getVista().getMessengerPanel().getListChat().revalidate();
                this.messengerController.getVista().getMessengerPanel().getListChat().repaint();
            }

//            in.close();
//            out.close();
            soc.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                this.messengerController
                        .getVista()
                        .getMessengerPanel()
                        .getTxtAreaConversacion()
                        .append("Error: Se recibió un tipo de objeto desconocido.\n");
            });
        } catch (IOException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                this.messengerController
                        .getVista()
                        .getMessengerPanel()
                        .getTxtAreaConversacion()
                        .append("Error de I/O con el cliente: " + e.getMessage() + "\n");
            });
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                this.messengerController
                        .getVista()
                        .getMessengerPanel()
                        .getTxtAreaConversacion()
                        .append("Error inesperado al procesar conexión: " + e.getMessage() + "\n");
            });
        }
    }


    public MessengerController getMessengerController() {
        return messengerController;
    }
}
