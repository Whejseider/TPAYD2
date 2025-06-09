package view.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.LoggingFacade;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class About extends JPanel {

    public About() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx,wrap,insets 5 30 5 30,width 400", "[fill,330::]", ""));

        JTextPane title = createText("Proyecto Servicio de Mensajería Instantánea");
        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +5");

        JTextPane description = createText("");
        description.setContentType("text/html");
        description.setText(getDescriptionText());
        description.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                showUrl(e.getURL());
            }
        });

        add(title);
        add(description);
        add(createSystemInformation());
    }

    private JTextPane createText(String text) {
        JTextPane textPane = new JTextPane();
        textPane.setBorder(BorderFactory.createEmptyBorder());
        textPane.setText(text);
        textPane.setEditable(false);
        textPane.setCaret(new DefaultCaret() {
            @Override
            public void paint(Graphics g) {
            }
        });
        return textPane;
    }

    private String getDescriptionText() {
        String text = "Este es un proyecto para la materia Análisis y Diseño de Sistemas II, " +
                "hecho con las librerías FlatLaf Look and Feel y MigLayout library.<br><br>" +
                "Se basa en una conexión por socket cliente-servidor, con el servidor centralizado.<br><br>" +
                "Para el código de fuente, visitar <a href=\"https://github.com/DJ-Raven/swing-modal-dialog/\">GitHub TPAYD2.</a><br><br>" +
                "<a href=\"https://github.com/AgusC02\">Agustín Cerdá</a> - <a href=\"https://github.com/Whejseider\">Franco Vallone</a><br>";

        return text;
    }

    private String getSystemInformationText() {
        String text = "<b>Versión: </b>%s<br/>" +
                "<b>Java: </b>%s<br/>" +
                "<b>Sistema: </b>%s<br/>";

        return text;
    }

    private JComponent createSystemInformation() {
        JPanel panel = new JPanel(new MigLayout("wrap"));
        panel.setBorder(new TitledBorder("Información del Sistema"));
        JTextPane textPane = createText("");
        textPane.setContentType("text/html");
        String version = "1";//Demo.DEMO_VERSION;
        String java = System.getProperty("java.vendor") + " - v" + System.getProperty("java.version");
        String system = System.getProperty("os.name") + " " + System.getProperty("os.arch") + " - v" + System.getProperty("os.version");
        String text = String.format(getSystemInformationText(),
                version,
                java,
                system);
        textPane.setText(text);
        panel.add(textPane);
        return panel;
    }

    private void showUrl(URL url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(url.toURI());
                } catch (IOException | URISyntaxException e) {
                    LoggingFacade.INSTANCE.logSevere("Error de URL", e);
                }
            }
        }
    }
}