package view.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import utils.JIMSendTextPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class Chat_Item extends JLayeredPane {
    private JLabel lblTime;

    public Chat_Item() {
        initComponents();
        txt.setEditable(false);
        txt.setBackground(new Color(0, 0, 0, 0));
        txt.setOpaque(false);
    }

    public void setText(String text) {
        txt.setText(text);
    }

    public void setTime(String time) {
        JLayeredPane layer = new JLayeredPane();
        layer.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        layer.setBorder(new EmptyBorder(0, 5, 10, 5));
        lblTime = new JLabel(time);
//        label.setForeground(new Color(110, 110, 110));
        lblTime.setHorizontalTextPosition(JLabel.LEFT);
        lblTime.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,30%);" +
                "[dark]background:lighten(@background,30%)");
        layer.add(lblTime);
        add(layer);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txt = new JIMSendTextPane();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        txt.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        txt.setSelectionColor(new Color(92, 188, 255));
        add(txt);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(grphcs);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JIMSendTextPane txt;
    // End of variables declaration//GEN-END:variables
}
