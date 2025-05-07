package view.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import model.Contacto;

import javax.swing.*;
import java.awt.Color;

public class Chat_Title extends JPanel {

    public Chat_Title() {
        initComponents();
        ocultar();
    }

    public void setUserName(String userName) {
        lbName.setText(userName);
    }

    public void setStatusText(String text) {
        lbStatus.setText(text);
        lbStatus.setForeground(new Color(160, 160, 160));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        layer = new JLayeredPane();
        lbName = new JLabel();
        lbStatus = new JLabel();

        lbName.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:0,5,0,5;" +
                "foreground:$Component.accentColor;" +
                "font:bold");
        lbStatus.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,30%);" +
                "[dark]background:lighten(@background,30%)");

//        setBackground(new Color(229, 229, 229));

        layer.setLayout(new java.awt.GridLayout(0, 1));

//        lbName.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
//        lbName.setForeground(new Color(66, 66, 66));
        lbName.setText("Name");
        layer.add(lbName);

//        lbStatus.setForeground(new Color(40, 147, 59));
//        lbStatus.setText("Active now");
        layer.add(lbStatus);
        layer.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,30%);" +
                "[dark]background:lighten(@background,30%)");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(layer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(406, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(layer, GroupLayout.PREFERRED_SIZE, 34, Short.MAX_VALUE)
                                .addGap(3, 3, 3))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane layer;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbStatus;

    public void mostrar(Contacto contactoSeleccionado) {
        setVisible(true);
        setUserName(contactoSeleccionado.getNombreUsuario());
        setStatusText(contactoSeleccionado.getIP() + " : " + contactoSeleccionado.getPuerto());
        revalidate();
        repaint();
    }

    public void ocultar(){
        setVisible(false);
        revalidate();
        repaint();
    }
    // End of variables declaration//GEN-END:variables
}
