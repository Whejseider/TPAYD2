import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import controller.ConfigurationController;
import model.Notificacion;
import raven.toast.Notifications;
import view.Configuracion;
import view.Messenger;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        try {
            FlatLaf.registerCustomDefaultsSource("fv.themes");
            UIManager.setLookAndFeel( new FlatIntelliJLaf() );
        } catch( Exception ex ) {
            System.err.println( "Error al inicializar LAF" );
        }

        try{
            FlatRobotoFont.install();
            UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        }catch (Exception e){
            System.err.println("Error al instalar la fuente FlatRobotoFont");
        }

        SwingUtilities.invokeLater(() -> {
            try {
                Messenger messenger = new Messenger();
                Notifications.getInstance().setJFrame(messenger);

                Configuracion configuracion = new Configuracion();
                ConfigurationController configurationController = new ConfigurationController(configuracion);
                configurationController.setMessenger(messenger);
                configuracion.setControlador(configurationController);

                messenger.setContentPane(configuracion);
            } catch (Exception e) {
                System.err.println("Error al inicializar la vista");
            }
        });
    }
}
