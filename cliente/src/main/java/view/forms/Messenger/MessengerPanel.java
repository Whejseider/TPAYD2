package view.forms.Messenger;

import interfaces.IController;
import model.Contacto;
import model.Conversacion;
import utils.ChatListRenderer;
import utils.SystemForm;
import view.component.chat.Chat_Panel;
import view.component.chat.TextPaneCustom;
import view.forms.FormChat;
import view.system.Form;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;


public class MessengerPanel extends Form {
    private JPanel pane;
    private JPanel panelPrincipal;
    private LeftPanel leftPanel;
    private FormChat panelConversacion;
    private JPanel panelSupChats;
    private JLabel lblChats;
    private JButton btnNuevoChat;
    private JPanel panelChatLista;
    private JList<Conversacion> listChat;
    private JScrollPane scrollPane;

    public MessengerPanel() {
        init();
    }

    private void init() {
        setName("MessengerPanel");
        pane = new JPanel(new BorderLayout());
        panelPrincipal = new JPanel(new BorderLayout());
        pane.add(panelPrincipal, BorderLayout.CENTER);

        leftPanel = new LeftPanel();
        panelPrincipal.add(leftPanel, BorderLayout.WEST);

        btnNuevoChat = new JButton("Nuevo");

        listChat = new JList<>();
        listChat.setFixedCellHeight(60);


        panelConversacion = new FormChat();
        panelPrincipal.add(panelConversacion, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);

    }

    @Override
    public void setControlador(IController controlador) {
        this.panelConversacion.getChatBottom().getBtnSend().addActionListener((ActionListener) controlador);
        this.leftPanel.getBtnNuevoChat().addActionListener((ActionListener) controlador);
    }

    public LeftPanel getLeftPanel() {
        return leftPanel;
    }

    public FormChat getPanelConversacion() {
        return panelConversacion;
    }

    public void mostrarContactoInfo(Contacto contactoSeleccionado) {
        try {
            panelConversacion.getChatTitle().mostrar(contactoSeleccionado);

            panelConversacion.getChatBottom().setVisible(true);
            panelConversacion.getChatBottom().getTxtInput().setText("");
            panelConversacion.getChatBottom().getTxtInput().grabFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListChat(JList<Conversacion> listChat) {
        this.listChat = listChat;
    }

    public JPanel getPane() {
        return pane;
    }

    public TextPaneCustom getTxtMensaje() {
        return panelConversacion.getChatBottom().getTxtInput();
    }

    public JButton getBtnEnviar() {
        return panelConversacion.getChatBottom().getBtnSend();
    }

    public Chat_Panel getChat() {
        return panelConversacion.getChatBody();
    }

    public JList<Conversacion> getListChat() {
        return listChat;
    }

    public JButton getBtnNuevoChat() {
        return btnNuevoChat;
    }
}
