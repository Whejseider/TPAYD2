package interfaces;

import model.User;

public interface AuthenticationListener {

    //LOGIN
    void onLoginSuccess(User user);
    void onLoginFailure(String s);

    //LOGOUT
    void onLogoutSuccess();
    void onLogoutFailure(String s);

    //REGISTER
    void onRegistrationSuccess();
    void onRegistrationFailure(String s);
}
