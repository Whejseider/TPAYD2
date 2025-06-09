package view.system;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class MainForm extends Form {

    public MainForm() {
        init();
    }

    private void init() {
        setName("MainForm");
        setLayout(new MigLayout("fillx,wrap,insets 0,gap 0", "[fill]", "[fill,grow][]"));
        add(createMain());
    }


    private Component createMain() {
        mainPanel = new JPanel(new BorderLayout());
        return mainPanel;
    }

    public void setForm(Form form) {
        mainPanel.removeAll();
        mainPanel.add(form);
        mainPanel.repaint();
        mainPanel.revalidate();


    }

    private JPanel mainPanel;

}
