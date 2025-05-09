package view.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import utils.AutoWrapText;
import utils.MethodUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Chat_Bottom extends JPanel {

    private TextPaneCustom txtInput;
    private JButton btnSend;

    private String placeholderText = "Escribe un mensaje...";

    public Chat_Bottom() {
        init();
    }

    private void init() {

        setLayout(new MigLayout("fill", "[fill][grow 0]", "[::200,bottom]"));
        putClientProperty(FlatClientProperties.STYLE, "arc:10");

        txtInput = new TextPaneCustom();
        txtInput.setPlaceholderText(placeholderText);
        txtInput.setEditorKit(new AutoWrapText());

        JScrollPane scrollInput = new JScrollPane(txtInput);
        scrollInput.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        txtInput.putClientProperty(FlatClientProperties.STYLE,
                "background:null; " +
                        "margin:4,4,4,4;");

        scrollInput.putClientProperty(FlatClientProperties.STYLE,
                "border:0,0,0,0;");
        scrollInput.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:3");
        scrollInput.getVerticalScrollBar().setUnitIncrement(10);

        txtInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                revalidate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                revalidate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                revalidate();
            }
        });

        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (ke.isShiftDown() || ke.isControlDown()) {
                        try {
                            txtInput.getDocument().insertString(txtInput.getCaretPosition(), "\n", null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ke.consume();
                    } else {
                        ke.consume();
                        btnSend.doClick();
                    }
                }
            }

        });

        add(scrollInput);

        JPanel bottomRightPanel = new JPanel(new MigLayout("insets 0, gap 3"));
        bottomRightPanel.setOpaque(false);

        btnSend = createActionButton(MethodUtil.createIcon("fv/icons/chat/send.svg", 0.8f));

        bottomRightPanel.add(btnSend);
        add(bottomRightPanel);

    }

    private JButton createActionButton(Icon icon) {
        JButton button = new JButton(icon);
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "arc:15;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;");
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusable(false);
        return button;
    }

    public TextPaneCustom getTxtInput() {
        return txtInput;
    }

    public JButton getBtnSend() {
        return btnSend;
    }

}