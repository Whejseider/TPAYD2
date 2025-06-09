package persistence.text;

import connection.Sesion;
import encryption.EncryptionType;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.MessageStatus;
import persistence.AbstractProductConversation;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
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
                    String id = mensaje.getId();
                    String emisor = mensaje.getNombreEmisor();
                    String receptor = mensaje.getNombreReceptor();
                    String fechaHora = mensaje.getTiempo().toString();
                    String contenido = mensaje.getContenido();
                    String encryptType = mensaje.getEncryption().toString();
                    String status = mensaje.getStatus().toString();

                    contenido = contenido.replace("|", "/");

                    writer.println(id + "|" + encryptType + "|" + emisor + "|" + receptor + "|" + fechaHora + "|" + contenido + "|" + status);
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
                    String[] partes = linea.split("\\|", 7);
                    if (partes.length == 7) {
                        String id = partes[0];
                        EncryptionType encryptType = EncryptionType.valueOf(partes[1]);
                        String nombreEmisor = partes[2];
                        String nombreReceptor = partes[3];
                        LocalDateTime fechaHora = LocalDateTime.parse(partes[4]);
                        String texto = partes[5];
                        MessageStatus status = MessageStatus.valueOf(partes[6]);

                        Mensaje mensaje = new Mensaje(texto, nombreEmisor, nombreReceptor, encryptType);
                        mensaje.setTiempo(fechaHora);
                        mensaje.setId(id);
                        mensaje.setStatus(status);
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

