package interfaces;

import model.Mensaje;
import model.User;

public interface MessageListener {

    void onMessageReceivedSuccess(Mensaje mensaje, User user);
    void onMessageReceivedFailure(String reason);

    void onSendMessageSuccess(Mensaje mensaje);
    void onSendMessageFailure(String reason);
}
