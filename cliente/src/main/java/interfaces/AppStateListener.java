package interfaces;

import model.Mensaje;
import model.TipoRespuesta;
import model.User;

import java.util.List;

public interface AppStateListener {

    void onConnectionAttempt(TipoRespuesta tipoRespuesta);

    void onLoginSuccess(User user);

    void onLoginFailure(Exception e);

    void onLogoutSuccess();

    void onNewMessageReceived(Mensaje mensaje);

    void onUserListUpdated(List<User> userList);

    void onRegistrationSuccess();

    void onRegistrationFailure();

    void onDirectoryInfoReceived(Object directoryData);
}
