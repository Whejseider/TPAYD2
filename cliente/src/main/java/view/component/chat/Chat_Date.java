package view.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class Chat_Date extends JPanel {

    private JLabel labelDate;

    public Chat_Date() {
        init();
    }

    private void init() {

        setOpaque(false);

        setLayout(new MigLayout("al center", "[center]", "5[center]5"));

        labelDate = new JLabel();

        labelDate.putClientProperty(FlatClientProperties.STYLE, "" +
                "background: $Chat.date.background;" +
                "foreground: $Chat.date.foreground;" +
                "arc: $Chat.date.arc;"
        );
        add(labelDate);
    }

    public void setText(String text) {
        labelDate.setText(text);
    }

}