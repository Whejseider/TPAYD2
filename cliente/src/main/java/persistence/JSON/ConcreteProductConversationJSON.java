package persistence.JSON;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import connection.Sesion;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.MessageStatus;
import persistence.AbstractProductConversation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConcreteProductConversationJSON implements AbstractProductConversation {
    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Messenger" + File.separator;

    @Override
    public void save() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Map<String, Conversacion> conversacionesOriginales = Sesion.getInstance().getUsuarioActual().getConversaciones();
        Map<String, Conversacion> conversacionesFiltradas = new HashMap<>();

        for (Map.Entry<String, Conversacion> entry : conversacionesOriginales.entrySet()) {
            String contacto = entry.getKey();
            Conversacion original = entry.getValue();

            Conversacion copia = new Conversacion();
            copia.setContacto(original.getContacto());

            List<Mensaje> mensajesFiltrados = original.getMensajes().stream()
                    .filter(m -> m.getStatus() != MessageStatus.FAILED)
                    .toList();
            copia.setMensajes(mensajesFiltrados);

            if (!mensajesFiltrados.isEmpty()) {
                copia.setUltimoMensaje(mensajesFiltrados.get(mensajesFiltrados.size() - 1));
            } else {
                copia.setUltimoMensaje(null);
            }

            conversacionesFiltradas.put(contacto, copia);
        }

        File userDir = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario());
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        try {
            File conversationsFile = new File(userDir, "conversaciones.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(conversationsFile, conversacionesFiltradas);
            System.out.println("Conversaciones guardadas exitosamente (sin mensajes FAILED).");

        } catch (IOException e) {
            System.err.println("Error al guardar las conversaciones al archivo JSON.");
            e.printStackTrace();
        }
    }


    @Override
    public void load() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        File conversationsFile = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario() + File.separator + "conversaciones.json");

        if (!conversationsFile.exists()) {
            System.out.println("El archivo de conversaciones no existe. Se creará uno nuevo al guardar.");
            Sesion.getInstance().getUsuarioActual().setConversaciones(new HashMap<>());
            return;
        }

        try {
            Map<String, Conversacion> conversaciones = mapper.readValue(conversationsFile, new TypeReference<Map<String, Conversacion>>() {
            });

            for (Map.Entry<String, Conversacion> entry : conversaciones.entrySet()) {
                String contactoNombre = entry.getKey();
                Conversacion conversacion = entry.getValue();

                Contacto contacto = Sesion.getInstance().getUsuarioActual().getAgenda().getContactoPorNombre(contactoNombre);

                if (contacto != null) {
                    conversacion.setContacto(contacto);
                } else {
                    System.err.println("Advertencia TODO: Se encontró una conversación para un contacto que ya no existe en la agenda: " + contactoNombre);
                }
                conversacion.setUltimoMensaje(conversacion.getUltimoMensaje());
            }

            Sesion.getInstance().getUsuarioActual().setConversaciones(conversaciones);

            System.out.println("Conversaciones cargadas exitosamente: " + conversaciones.size() + " conversaciones.");

        } catch (IOException e) {
            System.err.println("Error al cargar las conversaciones desde el archivo JSON.");
            e.printStackTrace();
        }
    }
}