import controller.ConfigurationController;
import controller.MessengerController;
import model.User;
import view.Configuracion;
import view.Messenger;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        Messenger messenger = new Messenger("Messenger");
        MessengerController messengerController = new MessengerController(messenger);
    }
}
