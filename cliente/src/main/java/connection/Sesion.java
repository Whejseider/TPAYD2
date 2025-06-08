package connection;

import model.User;
import persistence.AbstractFactoryPersistence;
import persistence.AbstractProductContacts;
import persistence.AbstractProductConversation;
import persistence.PersistenceFactory;

/**
 * De aca controlo la sesión del usuario para que las demás clases puedan acceder siempre
 */
public class Sesion {
    private User usuarioActual;
    private static Sesion instance;

    public Sesion() {
    }

    public static Sesion getInstance() {
        if (instance == null) {
            instance = new Sesion();
        }
        return instance;
    }

    public void setUsuarioActual(User usuario) {
        this.usuarioActual = usuario;
    }

    public User getUsuarioActual() {
        return this.usuarioActual;
    }

    public void loadUserData() {
        System.out.println("--- Iniciando carga de todos los datos del usuario ---");
        try {
            AbstractFactoryPersistence factory = PersistenceFactory.getFactory();

            System.out.println("Cargando contactos...");
            AbstractProductContacts contactsManager = factory.createProductContacts();
            contactsManager.load();

            System.out.println("Cargando conversaciones...");
            AbstractProductConversation conversationManager = factory.createProductConversation();
            conversationManager.load();

        } catch (Exception e) {
            System.err.println("¡ERROR CRÍTICO! Falló la carga de datos del usuario.");
            e.printStackTrace();
        }
        System.out.println("--- Carga de datos del usuario completada ---");
    }

    public void saveUserData() {
        System.out.println("--- Iniciando guardado de todos los datos del usuario ---");
        try {
            AbstractFactoryPersistence factory = PersistenceFactory.getFactory();

            System.out.println("Guardando contactos...");
            AbstractProductContacts contactsManager = factory.createProductContacts();
            contactsManager.save();
            System.out.println("Contactos guardados.");

            System.out.println("Guardando conversaciones...");
            AbstractProductConversation conversationManager = factory.createProductConversation();
            conversationManager.save();
            System.out.println("Conversaciones guardadas.");


        } catch (Exception e) {
            System.err.println("¡ERROR CRÍTICO! Falló el guardado de datos del usuario.");
            e.printStackTrace();
        }
        System.out.println("--- Guardado de datos del usuario completado ---");
    }

}

