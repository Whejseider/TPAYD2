package controller;

import interfaces.*;
import model.Directorio;
import model.Mensaje;
import model.TipoRespuesta;
import model.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final List<AppStateListener> listeners = new ArrayList<>();
    private final List<ConnectionListener> connectionListeners = new ArrayList<>();
    private final List<AuthenticationListener> authenticationListeners = new ArrayList<>();
    private final List<MessageListener> messageListeners = new ArrayList<>();
    private final List<ContactsListener> contactsListeners = new ArrayList<>();
    private final List<DirectoryListener> directoryListeners = new ArrayList<>();

    private static EventManager instance;

    public EventManager() {
    }

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public static void clearInstance() {

        if (instance != null) {
            instance.clearListeners();
            instance = null;
        }
    }

    private void clearListeners() {
        if (listeners != null)
            listeners.clear();

        if (connectionListeners != null)
            connectionListeners.clear();

        if (authenticationListeners != null)
            authenticationListeners.clear();

        if (messageListeners != null)
            messageListeners.clear();

        if (contactsListeners != null)
            contactsListeners.clear();

        if (directoryListeners != null)
            directoryListeners.clear();
    }

    public void addAppStateListener(AppStateListener listener) {
        listeners.add(listener);
    }

    public void removeAppStateListener(AppStateListener listener) {
        listeners.remove(listener);
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    public void addAuthenticationListener(AuthenticationListener listener) {
        authenticationListeners.add(listener);
    }

    public void removeAuthenticationListener(AuthenticationListener listener) {
        authenticationListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    public void addContactsListener(ContactsListener listener) {
        contactsListeners.add(listener);
    }

    public void removeContactsListener(ContactsListener listener) {
        contactsListeners.remove(listener);
    }

    public void addDirectoryListener(DirectoryListener listener) {
        directoryListeners.add(listener);
    }

    public void removeDirectoryListener(DirectoryListener listener) {
        directoryListeners.remove(listener);
    }

    public void notifyConnectionAttempt(TipoRespuesta tipoRespuesta) {
        SwingUtilities.invokeLater(() -> {
            List<ConnectionListener> copiaListeners = new ArrayList<>(connectionListeners);
            for (ConnectionListener listener : copiaListeners) {
                listener.onConnectionAttempt(tipoRespuesta);
            }
        });
    }

    public void notifyLoginSuccess(User user) {
        SwingUtilities.invokeLater(() -> {
            List<AuthenticationListener> copiaListeners = new ArrayList<>(authenticationListeners);
            for (AuthenticationListener listener : copiaListeners) {
                listener.onLoginSuccess(user);
            }
        });
    }

    public void notifyLoginFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<AuthenticationListener> copiaListeners = new ArrayList<>(authenticationListeners);
            for (AuthenticationListener listener : copiaListeners) {
                listener.onLoginFailure(s);
            }
        });
    }

    public void notifyLogoutSuccess() {
        SwingUtilities.invokeLater(() -> {
            List<AuthenticationListener> copiaListeners = new ArrayList<>(authenticationListeners);
            for (AuthenticationListener listener : copiaListeners) {
                listener.onLogoutSuccess();
            }
        });
    }

    public void notifyLogoutFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<AuthenticationListener> copiaListeners = new ArrayList<>(authenticationListeners);
            for (AuthenticationListener listener : copiaListeners) {
                listener.onLogoutSuccess();
            }
        });
    }

    public void notifyRegistrationSuccess() {
        SwingUtilities.invokeLater(() -> {
            List<AuthenticationListener> copiaListeners = new ArrayList<>(authenticationListeners);
            for (AuthenticationListener listener : copiaListeners) {
                listener.onRegistrationSuccess();
            }
        });
    }

    public void notifyRegistrationFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<AuthenticationListener> copiaListeners = new ArrayList<>(authenticationListeners);
            for (AuthenticationListener listener : copiaListeners) {
                listener.onRegistrationFailure(s);
            }
        });
    }

    public void notifyDirectoryInfoReceived(Directorio directorio) {
        SwingUtilities.invokeLater(() -> {
            List<DirectoryListener> copiaListeners = new ArrayList<>(directoryListeners);
            for (DirectoryListener listener : copiaListeners) {
                listener.onDirectoryInfoReceived(directorio);
            }
        });
    }

    public void notifyAddContactSuccess(User user) {
        SwingUtilities.invokeLater(() -> {
            List<ContactsListener> copiaListeners = new ArrayList<>(contactsListeners);
            for (ContactsListener listener : copiaListeners) {
                listener.onAddContactSuccess(user);
            }
        });
    }

    public void notifyAddContactFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<ContactsListener> copiaListeners = new ArrayList<>(contactsListeners);
            for (ContactsListener listener : copiaListeners) {
                listener.onAddContactFailure(s);
            }
        });
    }

    public void notifySendMessageSuccess(Mensaje contenido) {
        SwingUtilities.invokeLater(() -> {
            List<MessageListener> copiaListeners = new ArrayList<>(messageListeners);
            for (MessageListener listener : copiaListeners) {
                listener.onSendMessageSuccess(contenido);
            }
        });
    }

    public void notifySendMessageFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<MessageListener> copiaListeners = new ArrayList<>(messageListeners);
            for (MessageListener listener : copiaListeners) {
                listener.onSendMessageFailure(s);
            }
        });
    }

    public void notifyMessageReceivedSuccess(Mensaje mensaje) {
        SwingUtilities.invokeLater(() -> {
            List<MessageListener> copiaListeners = new ArrayList<>(messageListeners);
            for (MessageListener listener : copiaListeners) {
                listener.onMessageReceivedSuccess(mensaje);
            }
        });
    }

    public void notifyMessageReceivedFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<MessageListener> copiaListeners = new ArrayList<>(messageListeners);
            for (MessageListener listener : copiaListeners) {
                listener.onMessageReceivedFailure(s);
            }
        });
    }

}