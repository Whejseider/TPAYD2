import controller.ConfigurationController;
import view.Configuration;

public class Main {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        ConfigurationController configurationController = new ConfigurationController(configuration);
    }
}
