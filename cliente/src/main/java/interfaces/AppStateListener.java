package interfaces;

/**
 * Interfaz que implementa todos los listener en caso de ser necesario
 */
public interface AppStateListener
        extends MessageListener, ContactsListener, AuthenticationListener, ConnectionListener, DirectoryListener {
}
