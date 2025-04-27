import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.util.FontUtils;
import controller.ControllerManager;
import utils.Preferences;
import view.Messenger;
import view.forms.Login;

import javax.swing.*;
import java.awt.*;


public class Main {
    private static Main main;
    private Messenger mainForm;
    private Login loginForm;

    public static void main(String[] args) {
        Preferences.init();
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("fv.themes");
        UIManager.put("defaultFont", FontUtils.getCompositeFont(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        Preferences.setupLaf();
        SwingUtilities.invokeLater(() -> {
            try {
                Messenger messenger = new Messenger(ControllerManager.getInstance());
            } catch (Exception e) {
                System.err.println("Error al inicializar la vista");
            }
        });
    }

}
