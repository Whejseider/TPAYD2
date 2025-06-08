package persistence.text;

import connection.Sesion;
import model.Contacto;
import persistence.AbstractProductContacts;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConcreteProductContactsText implements AbstractProductContacts {
    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Messenger" + File.separator;

    @Override
    public void save() {
        List<Contacto> contactos = Sesion.getInstance().getUsuarioActual().getAgenda().getContactos();

        File contactsDir = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario());
        if (!contactsDir.exists()) {
            contactsDir.mkdirs();
        }
        File contactsFile = new File(contactsDir, "contactos.txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(contactsFile))) {
            writer.println("[CONTACTOS DE " + Sesion.getInstance().getUsuarioActual().getNombreUsuario() + "]");
            writer.println();

            int contador = 1;
            for (Contacto contacto : contactos) {
                writer.println("[CONTACTO N° " + contador + "]");
                writer.println("Nombre=" + contacto.getNombreUsuario());
                writer.println("Alias=" + contacto.getAlias());
                writer.println("IP=" + contacto.getIP());
                writer.println("Puerto=" + contacto.getPuerto());
                writer.println();
                contador++;
            }

            System.out.println("Contactos guardados exitosamente en: " + contactsFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error al guardar los contactos en el archivo de texto.");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        List<Contacto> contactosCargados = new ArrayList<>();
        File contactsDir = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario());
        if (!contactsDir.exists()) {
            contactsDir.mkdirs();
        }
        File contactsFile = new File(contactsDir, "contactos.txt");


        if (!contactsFile.exists()) {
            System.out.println("El archivo de contactos de texto no existe. No se ha cargado nada.");
            Sesion.getInstance().getUsuarioActual().getAgenda().setContactos(contactosCargados);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(contactsFile))) {
            String linea;
            Contacto contactoActual = null;

            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();

                if (linea.startsWith("[CONTACTO")) {
                    if (contactoActual != null && contactoActual.getPuerto() != null) {
                        contactosCargados.add(contactoActual);
                    }
                    contactoActual = new Contacto();
                } else if (linea.contains("=")) {
                    if (contactoActual != null) {
                        String[] partes = linea.split("=", 2);
                        String clave = partes[0].trim();
                        String valor = partes[1].trim();

                        switch (clave) {
                            case "Nombre":
                                contactoActual.setNombreUsuario(valor);
                                break;
                            case "Alias":
                                contactoActual.setAlias(valor);
                                break;
                            case "IP":
                                contactoActual.setIP(valor);
                                break;
                            case "Puerto":
                                contactoActual.setPuerto(Integer.parseInt(valor));
                                break;
                        }
                    }
                }
            }

            if (contactoActual != null) {
                contactosCargados.add(contactoActual);
            }

            Sesion.getInstance().getUsuarioActual().getAgenda().setContactos(contactosCargados);
            System.out.println("Contactos cargados desde archivo de texto: " + contactosCargados.size());

        } catch (IOException e) {
            System.err.println("Error al cargar los contactos desde el archivo de texto.");
            e.printStackTrace();
        }
    }
}
