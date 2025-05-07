package view.component.chat;

import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;


public class Chat_Left extends JLayeredPane {

    private Chat_Item txt;

    public Chat_Left() {
        initComponents();
        txt.setBackground(Color.DARK_GRAY);
    }

    public void setText(String text) {
        txt.setText(text);
    }

    public void setTime(String time) {
        txt.setTime(time);
    }

    private void initComponents() {
        txt = new Chat_Item();

        setLayout(new BorderLayout());
        add(txt, BorderLayout.CENTER);
    }
}

