package view.component.chat;

import javax.swing.*;
import java.awt.*;

public class Chat_Date extends JLayeredPane {

    public Chat_Date() {
        initComponents();
        txt.setBackground(new Color(221, 221, 221, 110));
    }

    public void setText(String text) {
        txt.setText(text);
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