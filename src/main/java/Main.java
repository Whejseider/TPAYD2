import controller.ConfigurationController;
import controller.MessengerController;
import model.User;
import view.Configuracion;
import view.Messenger;

public class Main {

    public static void main(String[] args) {
        Messenger messenger = new Messenger("Messenger");
        MessengerController messengerController = new MessengerController(messenger);
    }
}
