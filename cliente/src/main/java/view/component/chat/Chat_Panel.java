package view.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import model.Mensaje;
import net.miginfocom.swing.MigLayout;
import utils.AutoWrapText;
import utils.ScrollBar;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class Chat_Panel extends JPanel {

    private JPanel body;
    private JScrollPane sp;
    private JPanel messageWrapperPanel;

    public Chat_Panel() {
        init();
    }

    private void init() {
        body = new JPanel(new MigLayout("fillx", "", "15[bottom]15"));
        body.setOpaque(false);
        body.putClientProperty(FlatClientProperties.STYLE, "background:$Chat.background");


        messageWrapperPanel = new JPanel(new BorderLayout());
        messageWrapperPanel.setOpaque(false);
        messageWrapperPanel.add(body, BorderLayout.NORTH);


        sp = new JScrollPane(messageWrapperPanel);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBar(new ScrollBar());
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
        sp.putClientProperty(FlatClientProperties.STYLE, "background:$Chat.background");


        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
    }

    private JComponent createMessageComponent(String text, boolean esMio) {
        JTextPane textPane = new JTextPane();
        ((DefaultCaret) textPane.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        textPane.setEditorKit(new AutoWrapText(!esMio ? 60 : 100));
        textPane.setEditable(false);

        textPane.setText(text);

        String backgroundKey = !esMio ? "$Chat.item.background" : "$Chat.item.myselfBackground";
        String foregroundKey = !esMio ? "@foreground" : "$Chat.item.myselfForeground";

        textPane.putClientProperty(FlatClientProperties.STYLE, "" +
                "background: " + backgroundKey + ";" +
                "foreground: " + foregroundKey + ";");

        return textPane;
    }

    private JLabel createTimeLabel(String time) {
        JLabel timeLabel = new JLabel(time);
        timeLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:-2;" +
                "foreground:$Text.Foreground");
        timeLabel.setOpaque(false);
        return timeLabel;
    }

    public void addItemLeft(Mensaje mensaje) {
        JComponent contentComponent = createMessageComponent(mensaje.getContenido(), false);
        Chat_Left item = new Chat_Left(contentComponent);

        item.addTimePanel(createTimeLabel(mensaje.getTiempoFormateado()));
//        if (mensaje.getEmisor() != null && !mensaje.getEmisor().getNombreUsuario().isEmpty()) {
//            item.addUserName(mensaje.getEmisor().getNombreUsuario());
//        }
        item.setLevel(0);

        body.add(item, "wrap, w 100::80%");
        refreshMessages();
        scrollToBottom();
    }

    public void addItemRight(Mensaje mensaje) {
        JComponent contentComponent = createMessageComponent(mensaje.getContenido(), true);
        Chat_Right item = new Chat_Right(contentComponent);

        item.addTimePanel(createTimeLabel(mensaje.getTiempoFormateado()));

        item.setLevel(0);

        body.add(item, "wrap, al right, w 100::80%");
        refreshMessages();
        scrollToBottom();
    }

    public void addDate(String date) {
        Chat_Date item = new Chat_Date();
        item.setText(date);
        body.add(item, "align center, wrap");
        refreshMessages();
    }

    public void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            if (sp != null) {
                JScrollBar verticalBar = sp.getVerticalScrollBar();
                if (verticalBar != null) {
                    refreshMessages();
                    SwingUtilities.invokeLater(() -> {
                        verticalBar.setValue(verticalBar.getMaximum());
                    });
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