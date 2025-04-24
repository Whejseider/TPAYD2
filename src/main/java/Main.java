import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import controller.LoginController;
import raven.toast.Notifications;
import utils.DemoPreferences;
import view.Login;
import view.Messenger;

import javax.swing.*;
import java.awt.*;


public class Main {
    private static Main main;
    private Messenger mainForm;
    private Login loginForm;

    public static void main(String[] args) {
        DemoPreferences.init();
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

        DemoPreferences.setupLaf();
        SwingUtilities.invokeLater(() -> {
            try {
                Messenger messenger = new Messenger();
//                Notifications.getInstance().setJFrame(messenger);

//                Login login = new Login();
//                LoginController loginController = new LoginController(login);
//                loginController.setMessenger(messenger);
//                login.setControlador(loginController);

//                messenger.setContentPane(login);

            } catch (Exception e) {
                System.err.println("Error al inicializar la vista");
            }
        });
    }

}
