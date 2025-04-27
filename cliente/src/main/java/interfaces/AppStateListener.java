package interfaces;

import model.*;

import java.util.List;

/**
 * Tengo que usar todo español o todo ingles, preferentemente español
 * Desde aca gestionamos las respuestas del servidor
 * Probablemente habria que mover los listener de conexiones a uno aparte
 */
public interface AppStateListener {

    //ESTE YA NI ME ACUERDO
    void onConnectionAttempt(TipoRespuesta tipoRespuesta);

    //LOGIN
    void onLoginSuccess(User user);

    void onLoginFailure(String s);

    //LOGOUT
    void onLogoutSuccess();

    void onLogoutFailure(String s);

    //Nuevo mensajeRecibido
    void onNewMessageReceived(Mensaje mensaje);

    //No se todavia
    void onUserListUpdated(List<User> userList);

    //SIGNUP
    //TODO AGREGAR STRING
    void onRegistrationSuccess();

    void onRegistrationFailure(String s);

    //CONSULTA DIRECTORIO
    void onDirectoryInfoReceived(Directorio directorio);

    //CONTACTO
    void onAddContactSuccess(User user);

    void onAddContactFailure(String s);
}
