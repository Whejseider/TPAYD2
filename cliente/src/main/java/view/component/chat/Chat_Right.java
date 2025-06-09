package view.component.chat;

import model.MessageStatus;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class Chat_Right extends Chat_Item {

    private JLayeredPane layeredPane;
    private JPanel timePanel;
    private MessageStatusIcon statusIcon;

    public Chat_Right(Component component) {
        super(5, 2);
        init();
        layeredPane.add(component);
    }

    public void addTimePanel(Component component) {
        if (timePanel == null) {
            timePanel = new JPanel(new MigLayout("insets 0"));
            timePanel.setOpaque(false);
            String lc = "pos 100%-pref 100%-pref";
            layeredPane.setLayer(timePanel, JLayeredPane.POPUP_LAYER);
            layeredPane.add(timePanel, lc, 0);
        }
        timePanel.add(component);
    }

    public void setMessageStatus(MessageStatus status) {
        if (statusIcon == null) {
            statusIcon = new MessageStatusIcon(status);
            if (timePanel != null) {
                timePanel.add(statusIcon);
            }
        } else {
            statusIcon.setStatus(status);
        }

        if (timePanel == null) {
            addTimePanel(new JLabel(""));
            timePanel.add(statusIcon);
        }

        revalidate();
        repaint();
    }

    public void updateMessageStatus(MessageStatus status) {
        setMessageStatus(status);
    }

    private void init() {
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new MigLayout(
                "insets 0", "[fill,150::]"));
        setLayout(new MigLayout("insets 0,fillx,wrap,gapy 3", "fill"));
        add(layeredPane);
    }
}
