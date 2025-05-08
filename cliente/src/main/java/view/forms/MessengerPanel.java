package view.forms;

import controller.MessengerPanelController;
import interfaces.IController;
import model.Contacto;
import model.Conversacion;
import utils.ChatListRenderer;
import utils.AutoWrapText;
import utils.SystemForm;
import view.component.chat.Chat_Body;
import view.component.chat.TextPaneCustom;
import view.system.Form;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;

@SystemForm(name = "Chats", description = "Muestra los chats activos y el crear un nuevo chat", tags = {"chat"})
public class MessengerPanel extends Form {
    private JPanel pane;
    private JPanel panelOpciones;
    private JPanel panelPrincipal;
    private JButton btnNuevoContacto;
    private JPanel panelChats;
    private FormChat panelConversacion;
    private JPanel panelEnviarMensaje;
    private JPanel panelSupChats;
    private JLabel lblChats;
    private JButton btnNuevoChat;
    private JPanel panelBuscarChat;
    private JLabel lblBuscarChat;
    private JTextField txtBuscarChat;
    private JPanel panelChatLista;
    private JPanel panelContactoInfo;
    private JPanel panelNombreContacto;
    private JPanel panelNombreContactoAux;
    private JLabel lblNombreMensaje;
    private JPanel panelPuertoIpChat;
    private JLabel lblIP;
    private JLabel lblPuerto;
    private JTextField txtMensaje;
    private JButton btnEnviar;
    private Chat_Body panelMensajes;
    private JList<Conversacion> listChat;
    private DefaultListModel<Conversacion> listModel;
    private JScrollPane scrollPane;
    private MessengerPanelController controlador;

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

//        panelBuscarChat = new JPanel(new BorderLayout());
//        panelSupChats.add(panelBuscarChat, BorderLayout.SOUTH);
//
//        lblBuscarChat = new JLabel("Buscar chat");
//        panelBuscarChat.add(lblBuscarChat, BorderLayout.NORTH);
//
//        txtBuscarChat = new JTextField();
//        panelBuscarChat.add(txtBuscarChat, BorderLayout.SOUTH);

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

    public MessengerPanelController getControlador() {
        return controlador;
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

    public JButton getBtnNuevoContacto() {
        return btnNuevoContacto;
    }

    public JLabel getLblChats() {
        return lblChats;
    }

    public JButton getBtnNuevoChat() {
        return btnNuevoChat;
    }

    public JLabel getLblBuscarChat() {
        return lblBuscarChat;
    }

    public JTextField getTxtBuscarChat() {
        return txtBuscarChat;
    }

    public JLabel getLblNombreMensaje() {
        return lblNombreMensaje;
    }

    public JLabel getLblIP() {
        return lblIP;
    }

    public JLabel getLblPuerto() {
        return lblPuerto;
    }

    public TextPaneCustom getTxtMensaje() {
        return panelConversacion.getChatBottom().getTxtInput();
    }

    public JButton getBtnEnviar() {
        return panelConversacion.getChatBottom().getBtnSend();
    }

    public Chat_Body getPanelMensajes() {
        return panelMensajes;
    }

    public Chat_Body getChat() {
        return panelConversacion.getChatBody();
    }

    public JList<Conversacion> getListChat() {
        return listChat;
    }

    public JPanel getPanelContactoInfo() {
        return panelContactoInfo;
    }

}
