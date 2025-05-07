import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.util.FontUtils;
import utils.Preferences;
import view.Messenger;

import javax.swing.*;
import java.awt.*;


public class Main {

    public static void main(String[] args) {
        Preferences.init();
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("fv.themes");
        UIManager.put("defaultFont", FontUtils.getCompositeFont(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        Preferences.setupLaf();
        SwingUtilities.invokeLater(() -> {
            try {
                Messenger messenger = new Messenger();
            } catch (Exception e) {
                System.err.println("Error al inicializar la vista");
            }
        });
    }

}
