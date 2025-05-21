package controller;

import connection.Cliente;
import interfaces.ClientListener;
import interfaces.ConnectionCallBack;
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
                case CONEXION_PERDIDA: //TODO
                    System.out.println("ClientManager: CONEXION_PERDIDA detectada. Mensaje: " + comando.getContenido());
                    eventManager.notifyConnectionLost(
                            comando.getContenido() != null ? (String) comando.getContenido() : "Se perdió la conexión."
                    );
                    break;

                case CONECTARSE_SERVIDOR://TODO
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK) {
                        System.out.println("ClientManager: CONECTARSE_SERVIDOR OK.");
                        eventManager.notifyConnectionSuccess();
                    } else {
                        System.out.println("ClientManager: Error al CONECTARSE_SERVIDOR. Mensaje: " + comando.getContenido());
                        eventManager.notifyConnectionAttemptFailure(
                                comando.getContenido() != null ? (String) comando.getContenido() : "Fallo al conectarse al servidor."
                        );
                    }
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
                    if (comando.getTipoRespuesta() == TipoRespuesta.OK && comando.getContenido() instanceof Contacto) {
                        eventManager.notifyAddContactSuccess((Contacto) comando.getContenido());
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
