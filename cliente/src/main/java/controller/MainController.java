package controller;

import interfaces.AppStateListener;
import interfaces.ClientListener;
import model.*;
import view.system.FormManager;
import view.system.MainForm;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainController implements ClientListener {
    private MainForm vista;
    private List<AppStateListener> listeners = new ArrayList<>(); // Lista de observadores
    private static MainController instance;

    public MainController() {
    }

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
            instance.vista = FormManager.getMainForm();
        }
        return instance;
    }

    public void setVista(MainForm vista) {
        this.vista = vista;
    }

    public void addAppStateListener(AppStateListener listener) {
        listeners.add(listener);
    }

    public void removeAppStateListener(AppStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyConnectionAttempt(TipoRespuesta tipoRespuesta) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onConnectionAttempt(tipoRespuesta);
            }
        });
    }

    private void notifyLoginSuccess(User user) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onLoginSuccess(user);
            }
        });
    }

    private void notifyLoginFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onLoginFailure(s);
            }
        });
    }

    private void notifyLogoutSuccess() {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onLogoutSuccess();
            }
        });
    }

    private void notifyLogoutFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onLogoutSuccess();
            }
        });
    }

    private void notifyRegistrationSuccess() {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onRegistrationSuccess();
            }
        });
    }

    private void notifyRegistrationFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onRegistrationFailure(s);
            }
        });
    }

    private void notifyDirectoryInfoReceived(Directorio directorio) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onDirectoryInfoReceived(directorio);
            }
        });
    }

    private void notifyAddContactSuccess(User user) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onAddContactSuccess(user);
            }
        });
    }

    private void notifyAddContactFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onAddContactFailure(s);
            }
        });
    }

    private void notifySendMessageSuccess(Mensaje contenido) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onSendMessageSuccess(contenido);
            }
        });
    }

    private void notifySendMessageFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onSendMessageFailure(s);
            }
        });
    }

    private void notifyMessageReceivedSuccess(Mensaje mensaje) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onMessageReceivedSuccess(mensaje);
            }
        });
    }

    private void notifyMessageReceivedFailure(String s) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onMessageReceivedFailure(s);
            }
        });
    }

    /**
     * ClientListener
     *
     * @param comando
     */
    @Override
    public void onResponse(Comando comando) {
        System.out.println("DEBUG: Comando recibido en MainController: " + comando.getTipoSolicitud());

        try {
            switch (comando.getTipoSolicitud()) {
                case CONECTARSE_SERVIDOR:
                    notifyConnectionAttempt(comando.getTipoRespuesta());
                    break;


                case INICIAR_SESION:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK) {
                        notifyLoginSuccess((User) comando.getContenido());
                    } else {
                        notifyLoginFailure((String) comando.getContenido());
                    }
                    break;

                case REGISTRARSE:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK) {
                        notifyRegistrationSuccess();
                    } else {
                        notifyRegistrationFailure((String) comando.getContenido());
                    }
                    break;

                case CERRAR_SESION:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK) {
                        notifyLogoutSuccess();
                    } else {
                        notifyLogoutFailure((String) comando.getContenido());
                    }
                    break;

                case ENVIAR_MENSAJE:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK && comando.getContenido() instanceof Mensaje) {
                        notifySendMessageSuccess((Mensaje) comando.getContenido());
                    } else {
                        notifySendMessageFailure((String) comando.getContenido());
                    }
                    break;

                case RECIBIR_MENSAJE:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK && comando.getContenido() instanceof Mensaje) {
                        notifyMessageReceivedSuccess((Mensaje) comando.getContenido());
                    } else {
                        notifyMessageReceivedFailure((String) comando.getContenido());
                    }
                    break;

                case AGREGAR_CONTACTO:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK && comando.getContenido() instanceof User) {
                        notifyAddContactSuccess((User) comando.getContenido());
                    } else {
                        notifyAddContactFailure((String) comando.getContenido());
                    }
                    break;

                case OBTENER_DIRECTORIO:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK && comando.getContenido() instanceof Directorio) {
                        notifyDirectoryInfoReceived((Directorio) comando.getContenido());
                    }
                    break;

                default:
                    System.out.println("WARN: Tipo de acción no manejada: " + comando.getTipoSolicitud());
            }

        } catch (Exception e) {
            System.err.println("ERROR: Excepción al procesar comando " + comando.getTipoSolicitud() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public MainForm getVista() {
        return vista;
    }

}