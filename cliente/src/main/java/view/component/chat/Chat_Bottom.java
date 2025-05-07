package view.component.chat;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import interfaces.IController;
import net.miginfocom.swing.MigLayout;
import utils.JIMSendTextPane;
import utils.ScrollBar;
import view.system.Form;

public class Chat_Bottom extends Form {

    private JButton cmd;
    private JIMSendTextPane txt;

    public Chat_Bottom() {
        initComponents();
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx, filly", "0[fill]0[]0[]2", "2[fill]2"));
        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(null);
        txt = new JIMSendTextPane();
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (ke.isShiftDown()) {
                        try {
                            txt.getDocument().insertString(txt.getCaretPosition(), "\n", null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ke.consume();
                    } else {
                        ke.consume();
                        cmd.doClick();
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent ke) {
                refresh();
            }
        });

        txt.setHintText("Escribe un mensaje");
        scroll.setViewportView(txt);
        ScrollBar sb = new ScrollBar();
        sb.setPreferredSize(new Dimension(2, 10));
        scroll.setVerticalScrollBar(sb);
        add(sb);
        add(scroll, "w 100%");
        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout("filly", "0[]0", "0[bottom]0"));
        panel.setPreferredSize(new Dimension(30, 28));
//        panel.setBackground(Color.WHITE);
        cmd = new JButton();
        cmd.setBorder(null);
        cmd.setContentAreaFilled(false);
        cmd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmd.setIcon(new FlatSVGIcon("fv/icons/send.svg", 0.04f));
        panel.add(cmd);
        add(panel);
    }

    public JButton getCmd() {
        return cmd;
    }

    public JIMSendTextPane getTxt() {
        return txt;
    }

    private void refresh() {
        revalidate();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

//        setBackground(new Color(229, 229, 229));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
