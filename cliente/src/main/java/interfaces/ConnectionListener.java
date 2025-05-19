package interfaces;

import model.TipoRespuesta;

/**
 * Verificar
 */
public interface ConnectionListener {

    void onConnectionAttempt(TipoRespuesta tipoRespuesta);
    void onConnectionEstablished();
    void onConnectionLost(String reason);
    void onConnectionAttemptFailure(String reason);
}
