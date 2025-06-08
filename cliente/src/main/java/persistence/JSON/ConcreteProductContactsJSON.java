package persistence.JSON;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import connection.Sesion;
import model.Contacto;
import persistence.AbstractProductContacts;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConcreteProductContactsJSON implements AbstractProductContacts {
    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Messenger" + File.separator;

    @Override
    public void save() {
        ObjectMapper mapper = new ObjectMapper();

        List<Contacto> contactos = Sesion.getInstance().getUsuarioActual().getAgenda().getContactos();

        File userDir = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario());
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        try {
            File contactsFile = new File(userDir, "contactos.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(contactsFile, contactos);
            System.out.println("Contactos guardados guardados correctamente");

        } catch (IOException e) {
            System.out.println("Error al guardar contactos al archivo JSON.");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        ObjectMapper mapper = new ObjectMapper();

        File contactsFile = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario() + File.separator + "contactos.json");

        if (!contactsFile.exists()) {
            System.out.println("El archivo de contactos no existe. No se ha cargado nada.");
            return;
        }

        try {
            List<Contacto> contactos = mapper.readValue(contactsFile, new TypeReference<>() {
            });

            Sesion.getInstance().getUsuarioActual().getAgenda().setContactos(contactos);

            System.out.println("Contactos cargados exitosamente: " + contactos.size() + " contactos.");

        } catch (IOException e) {
            System.err.println("Error al cargar los contactos desde el archivo JSON.");
            e.printStackTrace();
        }
    }
}
