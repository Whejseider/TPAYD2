package view.component.chat;

import model.Mensaje;
import net.miginfocom.swing.MigLayout;
import utils.ScrollBar;

import javax.swing.*;
import java.awt.*;


public class Chat_Body extends JPanel {

    private JPanel body;
    private JScrollPane sp;
    private JPanel messageWrapperPanel;

    public Chat_Body() {
        initComponents();
        init();
        addDate("Today");
        addDate("13/05/1998");
    }

    private void initComponents() {

        body = new JPanel(new MigLayout("fillx, wrap", "[fill]", "20[]20"));
        body.setOpaque(true);

        messageWrapperPanel = new JPanel(new BorderLayout());
        messageWrapperPanel.setOpaque(false);
        messageWrapperPanel.add(body, BorderLayout.SOUTH);


        sp = new JScrollPane(messageWrapperPanel);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBar(new ScrollBar());
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);


        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
    }

    private void init() {


    }

    public void addItemLeft(Mensaje mensaje) {
        Chat_Left item = new Chat_Left();
        item.setText(mensaje.getContenido());
        item.setTime(mensaje.getTiempoFormateado());
        body.add(item, "growx 0, shrinkx 0, wmax 80%");
        refreshMessages();
    }

    public void addItemRight(Mensaje mensaje) {
        Chat_Right item = new Chat_Right();
        item.setText(mensaje.getContenido());
        item.setTime(mensaje.getTiempoFormateado());
        body.add(item, "align right, growx 0, shrinkx 0, wmax 80%");
        refreshMessages();
        scrollToBottom();
    }

    public void addDate(String date) {
        Chat_Date item = new Chat_Date();
        item.setText(date);
        body.add(item, "align center, growx 0, shrinkx 0");
        refreshMessages();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            if (sp != null) {
                JScrollBar verticalBar = sp.getVerticalScrollBar();
                if (verticalBar != null) {
                    verticalBar.setValue(verticalBar.getMaximum());
                }
            }
        });
    }

    private void refreshMessages() {
        body.revalidate();
        body.repaint();
    }

    public void clearMessages() {
        body.removeAll();
        refreshMessages();
    }
}
