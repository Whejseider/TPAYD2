package interfaces;

import model.Mensaje;

public interface MessageListener {

    void onMessageReceivedSuccess(Mensaje mensaje);
    void onMessageReceivedFailure(String reason);

    void onSendMessageSuccess(Mensaje mensaje);
    void onSendMessageFailure(String reason);
}
