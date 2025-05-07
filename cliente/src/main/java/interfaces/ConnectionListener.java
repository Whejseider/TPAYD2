package interfaces;

import model.TipoRespuesta;

public interface ConnectionListener {

    void onConnectionAttempt(TipoRespuesta tipoRespuesta);

}
