package factory;

import controller.*;
import interfaces.IController;
import view.forms.FormDirectorio;
import view.forms.FormRegister;
import view.forms.Login;
import view.forms.MessengerPanel;
import view.system.Form;
import view.system.MainForm;

public class ControllerFactory {

    public IController getController(String className, Form form) {
        return switch (className) {
            case "Login" -> new LoginController((Login) form);
            case "FormDirectorio" -> new DirectorioController((FormDirectorio) form);
            case "MainForm" -> new MainFormController((MainForm) form);
            case "MessengerPanel" -> new MessengerPanelController((MessengerPanel) form);
            case "FormRegister" -> new RegisterController((FormRegister) form);
            default -> null;
        };
    }
}
