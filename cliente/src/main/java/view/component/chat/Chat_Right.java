package view.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;

public class Chat_Right extends JLayeredPane {

    public Chat_Right() {
        initComponents();
        boolean isDark = FlatLaf.isLafDark();
        Color bg = isDark
                ? UIManager.getColor("Component.accentColor").brighter()
                : UIManager.getColor("Component.accentColor").darker();
        txt.setBackground(bg);
    }

    public void setText(String text) {
        txt.setText(text);
    }

    public void setTime(String time) {
        txt.setTime(time);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txt = new Chat_Item();

        setLayer(txt, JLayeredPane.DEFAULT_LAYER);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(txt, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(txt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Chat_Item txt;
    // End of variables declaration//GEN-END:variables
}
