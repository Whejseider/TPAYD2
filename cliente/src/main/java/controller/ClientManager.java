package controller;

import interfaces.ClientListener;
import model.*;

public class ClientManager implements ClientListener {
    private static ClientManager instance;
    private EventManager eventManager = EventManager.getInstance();

    public static ClientManager getInstance() {
        if (instance == null) {
            instance = new ClientManager();
        }
        return instance;
    }

    private ClientManager() {
    }

    public static void clearInstance() {
        instance = null;
    }


    @Override
    public void onResponse(Comando comando) {
        System.out.println("DEBUG: Comando recibido en MainController: " + comando.getTipoSolicitud());

        try {
            switch (comando.getTipoSolicitud()) {
                case CONECTARSE_SERVIDOR:
                    eventManager.notifyConnectionAttempt(comando.getTipoRespuesta());
                    break;


                case INICIAR_SESION:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK) {
                        eventManager.notifyLoginSuccess((User) comando.getContenido());
                    } else {
                        eventManager.notifyLoginFailure((String) comando.getContenido());
                    }
                    break;

                case REGISTRARSE:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK) {
                        eventManager.notifyRegistrationSuccess();
                    } else {
                        eventManager.notifyRegistrationFailure((String) comando.getContenido());
                    }
                    break;

                case CERRAR_SESION:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK) {
                        eventManager.notifyLogoutSuccess();
                    } else {
                        eventManager.notifyLogoutFailure((String) comando.getContenido());
                    }
                    break;

                case ENVIAR_MENSAJE:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK && comando.getContenido() instanceof Mensaje) {
                        eventManager.notifySendMessageSuccess((Mensaje) comando.getContenido());
                    } else {
                        eventManager.notifySendMessageFailure((String) comando.getContenido());
                    }
                    break;

                case RECIBIR_MENSAJE:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK && comando.getContenido() instanceof Mensaje) {
                        eventManager.notifyMessageReceivedSuccess((Mensaje) comando.getContenido());
                    } else {
                        eventManager.notifyMessageReceivedFailure((String) comando.getContenido());
                    }
                    break;

                case AGREGAR_CONTACTO:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK && comando.getContenido() instanceof User) {
                        eventManager.notifyAddContactSuccess((User) comando.getContenido());
                    } else {
                        eventManager.notifyAddContactFailure((String) comando.getContenido());
                    }
                    break;

                case OBTENER_DIRECTORIO:
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK && comando.getContenido() instanceof Directorio) {
                        eventManager.notifyDirectoryInfoReceived((Directorio) comando.getContenido());
                    }
                    break;

                default:
                    System.out.println("ADVERTENCIA: Tipo de acción no manejada: " + comando.getTipoSolicitud());
            }

        } catch (Exception e) {
            System.err.println("ERROR: Excepción al procesar comando " + comando.getTipoSolicitud() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
