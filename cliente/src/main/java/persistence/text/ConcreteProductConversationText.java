package persistence.text;

import connection.Sesion;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import persistence.AbstractProductConversation;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConcreteProductConversationText implements AbstractProductConversation {
    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Messenger" + File.separator;

    @Override
    public void save() {
        Map<String, Conversacion> conversaciones = Sesion.getInstance().getUsuarioActual().getConversaciones();

        File contactsDir = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario());
        if (!contactsDir.exists()) {
            contactsDir.mkdirs();
        }
        File conversationsFile = new File(contactsDir, "conversaciones.txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(conversationsFile))) {
            writer.println("[CONVERSACIONES DE " + Sesion.getInstance().getUsuarioActual().getNombreUsuario() + "]");
            writer.println();

            int contador = 1;
            for (Map.Entry<String, Conversacion> entry : conversaciones.entrySet()) {
                String contactoNombre = entry.getKey();
                Conversacion conversacion = entry.getValue();

                writer.println("[CONVERSACION N° " + contador + "]");
                writer.println("Nombre_Contacto=" + contactoNombre);
                writer.println("[MENSAJES]");
                for (Mensaje mensaje : conversacion.getMensajes()) {
                    String emisor = mensaje.getNombreEmisor();
                    String receptor = mensaje.getNombreReceptor();
                    String fechaHora = mensaje.getTiempo().toString();
                    String contenido = mensaje.getContenido();

                    contenido = contenido.replace("|", "/");

                    writer.println(emisor + "|" + receptor + "|" + fechaHora + "|" + contenido);
                }
                writer.println("[FIN_MENSAJES]");
                writer.println("Notificacion=" + conversacion.getNotificacion().isTieneMensajesNuevos());
                writer.println();
                contador++;
            }

            System.out.println("Conversaciones guardadas exitosamente en: " + conversationsFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error al guardar las conversaciones en el archivo de texto.");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        Map<String, Conversacion> conversacionesCargadas = new HashMap<>();
        File conversationsDir = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario());
        if (!conversationsDir.exists()) {
            conversationsDir.mkdirs();
        }
        File conversationsFile = new File(conversationsDir, "conversaciones.txt");

        if (!conversationsFile.exists()) {
            System.out.println("Archivo de conversaciones no existe.");
            Sesion.getInstance().getUsuarioActual().setConversaciones(conversacionesCargadas);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(conversationsFile))) {
            String linea;
            Conversacion conversacionActual = null;
            String nombreContactoActual = null;
            boolean leyendoMensajes = false;

            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();

                if (linea.startsWith("[CONVERSACION N°")) {
                    conversacionActual = new Conversacion();
                    leyendoMensajes = false;
                } else if (linea.startsWith("Nombre_Contacto=")) {
                    if (conversacionActual != null) {
                        nombreContactoActual = linea.split("=", 2)[1].trim();
                    }
                } else if (linea.equals("[MENSAJES]")) {
                    leyendoMensajes = true;
                } else if (linea.equals("[FIN_MENSAJES]")) {
                    leyendoMensajes = false;
                } else if (linea.startsWith("Notificacion=")) {
                    if (conversacionActual != null) {
                        boolean tieneNotificacion = Boolean.parseBoolean(linea.split("=", 2)[1].trim());
                        conversacionActual.getNotificacion().setTieneMensajesNuevos(tieneNotificacion);

                        if (nombreContactoActual != null) {
                            Contacto contacto = Sesion.getInstance().getUsuarioActual().getAgenda().getContactoPorNombre(nombreContactoActual);
                            conversacionActual.setContacto(contacto);
                            conversacionesCargadas.put(nombreContactoActual, conversacionActual);
                        }
                        conversacionActual = null;
                        nombreContactoActual = null;
                    }
                } else if (leyendoMensajes && conversacionActual != null && !linea.isEmpty()) {
                    String[] partes = linea.split("\\|", 4);
                    if (partes.length == 4) {
                        String nombreEmisor = partes[0];
                        String nombreReceptor = partes[1];
                        LocalDateTime fechaHora = LocalDateTime.parse(partes[2]);
                        String texto = partes[3];


                        Mensaje mensaje = new Mensaje(texto, nombreEmisor, nombreReceptor);
                        mensaje.setTiempo(fechaHora);
                        conversacionActual.getMensajes().add(mensaje);
                        conversacionActual.setUltimoMensaje(conversacionActual.getUltimoMensaje());
                    }
                }
            }

            for (Map.Entry<String, Conversacion> entry : conversacionesCargadas.entrySet()) {
                Contacto contacto = Sesion.getInstance().getUsuarioActual().getAgenda().getContactoPorNombre(entry.getKey());
                if (contacto != null) {
                    entry.getValue().setContacto(contacto);
                }
            }

            Sesion.getInstance().getUsuarioActual().setConversaciones(conversacionesCargadas);
            System.out.println("Conversaciones cargadas desde texto: " + conversacionesCargadas.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

