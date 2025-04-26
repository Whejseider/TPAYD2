package view.forms;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import connection.ConnectionManager;
import model.TipoRespuesta;
import net.miginfocom.swing.MigLayout;
import raven.modal.Toast;
import connection.Cliente;
import view.system.Form;
import view.system.FormManager;

import javax.swing.*;
import java.io.IOException;

public class FormError extends Form {

    public FormError() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        JLabel labelMessage = new JLabel("Error de conexión con el servidor");
        JLabel labelDescription = new JLabel("Perdón, algo salio mal.");
        labelMessage.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");
        FlatSVGIcon icon = new FlatSVGIcon("demo/icons/server_error.svg", 2f);
        add(new JLabel(icon));
        panel = new JPanel(new MigLayout("wrap", "", "[]3[]10[]"));
        panel.add(labelMessage);
        panel.add(labelDescription);
        add(panel);
    }

    private JButton getReconnectButton() {
        if (reconnectButton == null) {
            reconnectButton = new JButton("Reconectar");
            reconnectButton.addActionListener(e -> reconnect());
            reconnectButton.putClientProperty(FlatClientProperties.STYLE, "" +
                    "[light]background:darken(@background,5%);" +
                    "[dark]background:lighten(@background,5%);" +
                    "font:-1;" +
                    "arc:999;" +
                    "margin:4,10,4,10;" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;");
        }
        return reconnectButton;
    }

    private void reconnect() {
        TipoRespuesta tipo = ConnectionManager.getInstance().checkConnection();
        if (tipo == TipoRespuesta.OK) {
            ConnectionManager.getInstance().checkOnReconnection();
        } else {
            Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Error de conexión");
        }
    }


    public void showReconnectButton(boolean show) {
        if (reconnectButton != null) {
            panel.remove(reconnectButton);
        }
        if (show) {
            panel.add(getReconnectButton());
        }
    }

    private JPanel panel;
    private JButton reconnectButton;
}
