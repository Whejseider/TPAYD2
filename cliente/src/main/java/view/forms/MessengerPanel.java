package view.forms;

import com.formdev.flatlaf.FlatClientProperties;
import interfaces.IController;
import model.Contacto;
import model.Conversacion;
import plugin.ScrollRefreshModel;
import utils.ChatListRenderer;
import utils.SystemForm;
import view.component.chat.Chat_Panel;
import view.component.chat.TextPaneCustom;
import view.system.Form;
import view.system.FormManager;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;

@SystemForm(name = "Chats", description = "Muestra los chats activos y el crear un nuevo chat", tags = {"chat"})
public class MessengerPanel extends Form {
    private JPanel pane;
    private JPanel panelPrincipal;
    private JPanel panelChats;
    private FormChat panelConversacion;
    private JPanel panelSupChats;
    private JLabel lblChats;
    private JButton btnNuevoChat;
    private JPanel panelChatLista;
    private JList<Conversacion> listChat;
    private DefaultListModel<Conversacion> listModel;
    private JScrollPane scrollPane;

    public MessengerPanel() {
        init();
    }

    private void init() {
        setName("MessengerPanel");
        pane = new JPanel(new BorderLayout());
        panelPrincipal = new JPanel(new BorderLayout());
        pane.add(panelPrincipal, BorderLayout.CENTER);

        panelChats = new JPanel(new BorderLayout());
        panelPrincipal.add(panelChats, BorderLayout.WEST);

        panelSupChats = new JPanel(new BorderLayout());
        panelChats.add(panelSupChats, BorderLayout.NORTH);

        lblChats = new JLabel("Chats");
        panelSupChats.add(lblChats, BorderLayout.WEST);

        btnNuevoChat = new JButton("Nuevo Chat");
        panelSupChats.add(btnNuevoChat, BorderLayout.EAST);

        panelChatLista = new JPanel(new BorderLayout());
        panelChatLista.setPreferredSize(new Dimension(220, 515));
        panelChats.add(panelChatLista, BorderLayout.CENTER);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black)));
        panelChatLista.add(scrollPane, BorderLayout.CENTER);

        listChat = new JList<>();
        listChat.setFixedCellHeight(60);
        scrollPane.setViewportView(listChat);

        panelConversacion = new FormChat();
        panelPrincipal.add(panelConversacion, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        listChat.setModel(listModel);
        listChat.setCellRenderer(new ChatListRenderer());
    }

    @Override
    public void setControlador(IController controlador) {
        this.panelConversacion.getChatBottom().getBtnSend().addActionListener((ActionListener) controlador);
        this.btnNuevoChat.addActionListener((ActionListener) controlador);
        this.listChat.addListSelectionListener((ListSelectionListener) controlador);
    }

    public FormChat getPanelConversacion() {
        return panelConversacion;
    }

    public void agregarConversacion(Conversacion c) {
        if (!listModel.contains(c)) {
            listModel.addElement(c);
        }
    }

    public void mostrarContactoInfo(Contacto contactoSeleccionado) {
        panelConversacion.getChatTitle().mostrar(contactoSeleccionado);
    }

    public void setListChat(JList<Conversacion> listChat) {
        this.listChat = listChat;
    }

    public DefaultListModel<Conversacion> getListModel() {
        return listModel;
    }

    public void setListModel(DefaultListModel<Conversacion> listModel) {
        this.listModel = listModel;
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
