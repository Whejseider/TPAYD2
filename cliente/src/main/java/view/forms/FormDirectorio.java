package view.forms;

import com.formdev.flatlaf.FlatClientProperties;
import interfaces.IController;
import layout.ResponsiveLayout;
import net.miginfocom.swing.MigLayout;
import utils.SystemForm;
import view.system.Form;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import java.awt.*;

@SystemForm(name = "Directorio", description = "Directorio de contactos", tags = {"directorio"})
public class FormDirectorio extends Form  {
    private JPanel panelCard;
    private ResponsiveLayout responsiveLayout;
    private JTextField txtSearch;

    public FormDirectorio() {
        init();
    }

    private void init() {
        setName("FormDirectorio");
        setLayout(new MigLayout("wrap,fill", "[fill]", "[grow 0][fill]"));
        add(createInfo());
        add(createOptions());
    }

    public JPanel getPanelCard() {
        return panelCard;
    }

    private JPanel createInfo() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap", "[fill]"));
        JLabel title = new JLabel("Directorio de contactos");
        JTextPane text = new JTextPane();
        text.setText("Desde aquí se listan todos los usuarios registrados en el servidor.");
        text.setEditable(false);
        text.setBorder(BorderFactory.createEmptyBorder());
        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +3");

        panel.add(title);
        panel.add(text, "width 500");
        return panel;
    }

    private Component createOptions() {
        JPanel panel = new JPanel(new MigLayout("wrap 1,fill", "[fill]", "[fill][fill]"));
        panel.add(createSearchUser(), "gapx 0 2");
        panel.add(createDirectoryComponent(), "gapx 0 2");
        return panel;
    }

    private Component createSearchUser() {
        JPanel panel = new JPanel(new MigLayout("fill", "[center]","[]"));
        panel.setBorder(new TitledBorder("Busca un usuario"));

        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Escriba algo para comenzar a buscar");

        panel.add(txtSearch, "width 500");

        return panel;
    }

    private Component createDirectoryComponent() {
        responsiveLayout = new ResponsiveLayout(ResponsiveLayout.JustifyContent.START, new Dimension(-1, -1), 10, 10);
        panelCard = new JPanel(responsiveLayout);
        panelCard.setPreferredSize(new Dimension(400, 550));
        panelCard.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:10,10,10,10;");
        JScrollPane scrollPane = new JScrollPane(panelCard);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "trackArc:$ScrollBar.thumbArc;" +
                "thumbInsets:0,0,0,0;" +
                "width:5;");
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "trackArc:$ScrollBar.thumbArc;" +
                "thumbInsets:0,0,0,0;" +
                "width:5;");
        scrollPane.setBorder(new TitledBorder("Directorio"));
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(scrollPane);
        splitPane.setRightComponent(Box.createGlue());
        splitPane.setResizeWeight(1);
        splitPane.setDividerSize(0); // Oculta el boton
        return splitPane;
    }

    @Override
    public void setControlador(IController controlador) {
        this.txtSearch.getDocument().addDocumentListener((DocumentListener) controlador);
    }

    public JTextField getTxtSearch() {
        return txtSearch;
    }
}
