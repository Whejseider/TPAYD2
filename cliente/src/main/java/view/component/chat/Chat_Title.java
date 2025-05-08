package view.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import model.Contacto;

import javax.swing.*;
import java.awt.*;

public class Chat_Title extends JPanel {

    private JLayeredPane layer;
    private JLabel lbName;
    private JLabel lbStatus;

    public Chat_Title() {
        init();
        ocultar();
    }

    public void setUserName(String userName) {
        lbName.setText(userName);
    }

    public void setStatusText(String text) {
        lbStatus.setText(text);
        lbStatus.setForeground(new Color(160, 160, 160));
    }

    private void init() {

        layer = new JLayeredPane();
        lbName = new JLabel();
        lbStatus = new JLabel();

        lbName.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:0,5,0,5;" +
                "font:bold +2");
        lbStatus.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,30%);" +
                "[dark]background:lighten(@background,30%)");

        layer.setLayout(new GridLayout(0, 1));

        lbName.setText("Name");
        layer.add(lbName);

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
    }

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

}
