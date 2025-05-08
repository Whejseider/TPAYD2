package view.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class Chat_Date extends JPanel {

    private JLabel labelDate;

    public Chat_Date() {
        init();
    }

    private void init() {
        setOpaque(false);
        setLayout(new MigLayout("fill, insets 10", "[center]", "[center]"));

        labelDate = new JLabel();
        labelDate.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold small;" +
                "background:$Chat.date.background;" +
                "foreground:$Chat.date.foreground;" +
                "arc:50;" +
                "border:5,10,5,10"
        );

        add(labelDate, "center");
    }

    public void setText(String text) {
        labelDate.setText(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}