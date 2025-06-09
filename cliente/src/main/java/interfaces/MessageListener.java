package interfaces;

import model.Mensaje;
import model.User;

public interface MessageListener {

    void onMessageReceivedSuccess(Mensaje mensaje, User user);
    void onMessageReceivedFailure(Mensaje mensaje);

    void onSendMessageSuccess(Mensaje mensaje);
    void onSendMessageFailure(Mensaje mensaje);

    void onMessageDelivered(Mensaje mensaje);
    void onMessageRead(Mensaje mensaje);
}
