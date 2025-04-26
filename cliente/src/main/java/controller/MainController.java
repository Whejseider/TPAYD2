package controller;

import interfaces.AppStateListener;
import interfaces.ClientListener;
import model.Comando;
import model.Mensaje;
import model.TipoRespuesta;
import model.User;
import connection.Sesion;
import view.system.FormManager;
import view.system.MainForm;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainController implements ClientListener {
    private MainForm vista;
    private User user;
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
            for (AppStateListener listener : listeners) {
                listener.onConnectionAttempt(tipoRespuesta);
            }
        });
    }

    private void notifyLoginAttempt(User user) {
        SwingUtilities.invokeLater(() -> {
            List<AppStateListener> copiaListeners = new ArrayList<>(listeners);
            for (AppStateListener listener : copiaListeners) {
                listener.onRegistrationFailure();
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

    private void notifyLoginFailure(Exception e) {
        SwingUtilities.invokeLater(() -> {
            for (AppStateListener listener : listeners) {
                listener.onLoginFailure(e);
            }
        });
    }

    private void notifyLogoutSuccess() {
        SwingUtilities.invokeLater(() -> {
            for (AppStateListener listener : listeners) {
                listener.onLogoutSuccess();
            }
        });
    }

    private void notifyNewMessageReceived(Mensaje mensaje) {
        SwingUtilities.invokeLater(() -> {
            for (AppStateListener listener : listeners) {
                listener.onNewMessageReceived(mensaje);
            }
        });
    }

    private void notifyUserListUpdated(List<User> userList) {
        SwingUtilities.invokeLater(() -> {
            for (AppStateListener listener : listeners) {
                listener.onUserListUpdated(userList);
            }
        });
    }

    private void notifyRegistrationSuccess() {
        SwingUtilities.invokeLater(() -> {
            for (AppStateListener listener : listeners) {
                listener.onRegistrationSuccess();
            }
        });
    }

    private void notifyRegistrationFailure(){
        SwingUtilities.invokeLater(() -> {
            for (AppStateListener listener : listeners) {
                listener.onRegistrationFailure();
            }
        });
    }

    private void notifyDirectoryInfoReceived(Object directoryData) {
        SwingUtilities.invokeLater(() -> {
            for (AppStateListener listener : listeners) {
                listener.onDirectoryInfoReceived(directoryData);
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

                case INICIAR_SESION:
                    try {
                        if (comando.getContenido() instanceof User) {
                            Sesion.getInstance().setUsuarioActual((User) comando.getContenido());
                            user = Sesion.getInstance().getUsuarioActual();
                            System.out.println("DEBUG: Login exitoso para: " + user.getNombreUsuario());
                            notifyLoginAttempt(this.user);
                        }
                    } catch (Exception ex) {
                        notifyLoginFailure(ex);
                    }

                    break;

                case CERRAR_SESION:
                    this.user = null;
                    System.out.println("DEBUG: Logout procesado.");
                    notifyLogoutSuccess();
                    break;

                case ENVIAR_MENSAJE:
                    if (comando.getContenido() instanceof Mensaje) {
                        Mensaje msg = (Mensaje) comando.getContenido();
                        System.out.println("DEBUG: Mensaje recibido: " + msg.getContenido());
                        notifyNewMessageReceived(msg);
                    } else {
                        System.err.println("ERROR: Contenido inesperado para ENVIAR_MENSAJE: " + comando.getContenido());
                    }
                    break;

                case LISTA_USUARIOS:
                    if (comando.getContenido() instanceof List) {
                        try {
                            @SuppressWarnings("unchecked")
                            List<User> userList = (List<User>) comando.getContenido();

                            if (!userList.isEmpty() && !(userList.get(0) instanceof User)) {
                                System.err.println("ERROR: La lista recibida no contiene objetos User.");

                            } else {
                                System.out.println("DEBUG: Lista de usuarios actualizada recibida.");
                                notifyUserListUpdated(userList);
                            }
                        } catch (ClassCastException e) {
                            System.err.println("ERROR: No se pudo castear el contenido de LISTA_USUARIOS a List<User>.");

                        }
                    } else {
                        System.err.println("ERROR: Contenido inesperado para LISTA_USUARIOS: " + comando.getContenido());
                    }
                    break;

                case REGISTRARSE:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK) {
                        notifyRegistrationSuccess();
                    } else {
                        notifyRegistrationFailure();
                    }
                    break;

                case OBTENER_DIRECTORIO:

                    Object dirData = comando.getContenido();
                    System.out.println("DEBUG: Información de directorio recibida.");
                    notifyDirectoryInfoReceived(dirData);
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

    public User getUser() {
        return user;
    }

}