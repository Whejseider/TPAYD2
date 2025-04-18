import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import controller.MessengerController;
import view.Messenger;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel( new FlatMacLightLaf() );
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
                MessengerController messengerController = new MessengerController(messenger);
            } catch (Exception e) {
                System.err.println("Error al inicializar la vista");
            }
        });
    }
}
