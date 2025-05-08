package view.component.chat;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;


public class Chat_Left extends Chat_Item {

    private JLayeredPane layeredPane;
    private JPanel timePanel;

    public Chat_Left(Component component) {
        super(5, 1);
        init();
        layeredPane.add(component);
    }

    public void addTimePanel(Component component) {
        if (timePanel == null) {
            timePanel = new JPanel(new MigLayout("insets 0"));
            timePanel.setOpaque(false);
            String lc = "pos 100%-pref-5 100%-pref";
            layeredPane.setLayer(timePanel, JLayeredPane.POPUP_LAYER);
            layeredPane.add(timePanel, lc, 0);
        }
        timePanel.add(component);
    }

    private void init() {
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new MigLayout(
                "insets 0", "[fill,150::]"));
        setLayout(new MigLayout("insets 0,fillx,wrap,gapy 3", "fill"));
        add(layeredPane);
    }

}

