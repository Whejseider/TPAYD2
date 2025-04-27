package view.forms.other;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import model.User;
import net.miginfocom.swing.MigLayout;
import raven.extras.AvatarIcon;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class Card extends JPanel {
    private JLabel title;
    private JTextPane description;
    private JButton btnAgregar;

    public Card() {
        init();
    }

    private void init() {

        title = new JLabel();
        description = new JTextPane();
        btnAgregar = new JButton("Agregar");

        putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:30;" +
                "[light]background:darken($Panel.background,3%);" +
                "[dark]background:lighten($Panel.background,3%);");

        setLayout(new MigLayout("", "", "fill"));
        // create panel header
        panelHeader = createHeader();

        // create panel body
        panelBody = createBody();

        add(panelHeader);
        add(panelBody);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new MigLayout("fill,insets 0", "[fill]", "[top]"));
        header.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        JLabel label = new JLabel(new AvatarIcon(new FlatSVGIcon("demo/drawer/image/avatar_male.svg", 100, 100), 50, 50, 3.5f));
        header.add(label);
        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new MigLayout("wrap", "[150]", "[][]push[]"));
        body.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");

        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +1;");

        description.setEditable(false);
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:0,0,0,0;" +
                "background:null;" +
                "[light]foreground:tint($Label.foreground,30%);" +
                "[dark]foreground:shade($Label.foreground,30%)");

        btnAgregar = new JButton("Agregar contacto");

        btnAgregar.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:3,25,3,25;" +
                "borderWidth:1;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "background:null;");

        body.add(title);
        body.add(description);
        body.add(btnAgregar);
        return body;
    }

    public void addAgregarListener(ActionListener listener) {
        btnAgregar.addActionListener(listener);
    }

    public JLabel getTitle() {
        return title;
    }

    public JTextPane getDescription() {
        return description;
    }

    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    public JPanel getPanelHeader() {
        return panelHeader;
    }

    public JPanel getPanelBody() {
        return panelBody;
    }

    private JPanel panelHeader;
    private JPanel panelBody;
}
