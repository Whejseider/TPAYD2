import controller.ConfigurationController;
import view.Configuracion;

public class Main {

    public static void main(String[] args) {
        Configuracion configuration = new Configuracion();
        ConfigurationController configurationController = new ConfigurationController(configuration);
    }
}
