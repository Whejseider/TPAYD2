package view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import controller.MessengerController;
import model.Contacto;
import net.miginfocom.swing.MigLayout;
import utils.ChatListRenderer;

import javax.swing.*;
import java.awt.*;

public class MessengerPanel extends JPanel {
    private JPanel pane;
    private JPanel panelOpciones;
    private JPanel panelPrincipal;
    private JButton btnNuevoContacto;
    private JPanel panelChats;
    private JPanel panelConversacion;
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
    private JTextArea txtAreaConversacion;
    private JList<Contacto> listChat;
    private DefaultListModel<Contacto> listModel;
    private JScrollPane scrollPane;
    private JButton btnDarkMode;
    private MessengerController controlador;
    private JButton btnLogOut;

    public MessengerPanel() {
        init();
    }

    private void init() {
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

        panelBuscarChat = new JPanel(new BorderLayout());
        panelSupChats.add(panelBuscarChat, BorderLayout.SOUTH);

        lblBuscarChat = new JLabel("Buscar chat");
        panelBuscarChat.add(lblBuscarChat, BorderLayout.NORTH);

        txtBuscarChat = new JTextField();
        panelBuscarChat.add(txtBuscarChat, BorderLayout.SOUTH);

        panelChatLista = new JPanel(new BorderLayout());
        panelChatLista.setPreferredSize(new Dimension(220, 515));
        panelChats.add(panelChatLista, BorderLayout.CENTER);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black)));
        panelChatLista.add(scrollPane, BorderLayout.CENTER);

        listChat = new JList<>();
        listChat.setFixedCellHeight(60);
        scrollPane.setViewportView(listChat);

        panelConversacion = new JPanel(new BorderLayout());
        panelPrincipal.add(panelConversacion, BorderLayout.CENTER);

        panelContactoInfo = new JPanel(new BorderLayout());
        panelContactoInfo.setPreferredSize(new Dimension(0, 85));
        panelContactoInfo.setVisible(false);
        panelConversacion.add(panelContactoInfo, BorderLayout.NORTH);

        panelNombreContacto = new JPanel(new BorderLayout());
        panelContactoInfo.add(panelNombreContacto, BorderLayout.CENTER);

        panelNombreContactoAux = new JPanel(new BorderLayout());
        panelNombreContacto.add(panelNombreContactoAux, BorderLayout.CENTER);

        lblNombreMensaje = new JLabel("Nombre del contacto");
        panelNombreContactoAux.add(lblNombreMensaje, BorderLayout.CENTER);

        panelPuertoIpChat = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNombreContactoAux.add(panelPuertoIpChat, BorderLayout.SOUTH);

        lblIP = new JLabel("IP:localhost");
        panelPuertoIpChat.add(lblIP);

        lblPuerto = new JLabel("Puerto:9999");
        panelPuertoIpChat.add(lblPuerto);

        JScrollPane scrollPaneConversacion = new JScrollPane();
        panelConversacion.add(scrollPaneConversacion, BorderLayout.CENTER);

        txtAreaConversacion = new JTextArea();
        txtAreaConversacion.setEditable(false);
        txtAreaConversacion.setFocusable(false);
        txtAreaConversacion.setLineWrap(true);
        txtAreaConversacion.setWrapStyleWord(true);
        scrollPaneConversacion.setViewportView(txtAreaConversacion);

        panelEnviarMensaje = new JPanel(new BorderLayout());
        panelConversacion.add(panelEnviarMensaje, BorderLayout.SOUTH);

        btnEnviar = new JButton("Enviar");
        panelEnviarMensaje.add(btnEnviar, BorderLayout.EAST);

        txtMensaje = new JTextField();
        panelEnviarMensaje.add(txtMensaje, BorderLayout.CENTER);

        panelOpciones = new JPanel(new MigLayout("wrap,fillx,insets 15 25 10 25", "fill,60:100"));
        panelOpciones.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:darken(@background,10%)");
        pane.add(panelOpciones, BorderLayout.WEST);

        btnDarkMode = new JButton("Tema Oscuro"){
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        panelOpciones.add(btnDarkMode, "gapy 10 10, sizegroup btn");
        panelOpciones.add(new JSeparator(),"gapy 10 10");
        btnNuevoContacto = new JButton("Nuevo Contacto");
        panelOpciones.add(btnNuevoContacto, "gapy 10 10, sizegroup btn");

        btnLogOut = new JButton("Cerrar Sesión");
        panelOpciones.add(btnLogOut, "gapy 10 10, sizegroup btn");

        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        listChat.setModel(listModel);
        listChat.setCellRenderer(new ChatListRenderer());
    }

    public void agregarContacto(Contacto c) {
        listModel.addElement(c);
    }

    public void cambiarTema(boolean dark){
        if (FlatLaf.isLafDark() != dark){
            if (!dark){
                SwingUtilities.invokeLater(()->{
                    try {
                        FlatIntelliJLaf.setup();
                        FlatLaf.updateUI();
                        btnDarkMode.setText("Tema Oscuro");
                    } catch( Exception ex ) {
                        System.err.println( "Error al inicializar LAF" );
                    }
                });
            } else {
                SwingUtilities.invokeLater(()->{
                    try {
                        FlatDarculaLaf.setup();
                        FlatLaf.updateUI();
                        btnDarkMode.setText("Tema Claro");
                    } catch( Exception ex ) {
                        System.err.println( "Error al inicializar LAF" );
                    }
                });
            }
        }
    }

    public void mostrarContactoInfo(Contacto contactoSeleccionado) {
        getPanelContactoInfo().setVisible(true);

        if (contactoSeleccionado.getAlias().isEmpty()) {
            getLblNombreMensaje().setText(contactoSeleccionado.getNombreUsuario());
        } else {
            getLblNombreMensaje().setText(contactoSeleccionado.getAlias());
        }

        getLblIP().setText("IP: " + contactoSeleccionado.getIP());
        getLblPuerto().setText("Puerto: " + contactoSeleccionado.getPuerto());

        getPanelContactoInfo().revalidate();
        getPanelContactoInfo().repaint();
    }

    public JButton getBtnLogOut() {
        return btnLogOut;
    }

    public void setBtnLogOut(JButton btnLogOut) {
        this.btnLogOut = btnLogOut;
    }

    public JButton getBtnDarkMode() {
        return btnDarkMode;
    }

    public void setBtnDarkMode(JButton btnDarkMode) {
        this.btnDarkMode = btnDarkMode;
    }

    public void setListChat(JList<Contacto> listChat) {
        this.listChat = listChat;
    }

    public DefaultListModel<Contacto> getListModel() {
        return listModel;
    }

    public void setListModel(DefaultListModel<Contacto> listModel) {
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

    public JTextField getTxtMensaje() {
        return txtMensaje;
    }

    public JButton getBtnEnviar() {
        return btnEnviar;
    }

    public JTextArea getTxtAreaConversacion() {
        return txtAreaConversacion;
    }

    public JList<Contacto> getListChat() {
        return listChat;
    }

    public JPanel getPanelContactoInfo() {
        return panelContactoInfo;
    }
}
