package view;

import model.Contacto;

import javax.swing.*;
import java.awt.*;

public class Messenger extends JFrame {
    private JPanel pane;
    private JPanel panelOpciones;
    private JPanel panelPrincipal;
    private JButton btnNuevoContacto;
    private JPanel panelChats;
    private JPanel panelConversación;
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
    private JScrollPane scrollPane;
    private JButton btnLogin;

    public Messenger(String titulo) throws HeadlessException {
        super(titulo);
        this.setContentPane(this.pane);
        this.setSize(1100,600);
        this.setLocationRelativeTo(null);
        this.requestFocus();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }


    public JButton getBtnNuevoContacto() {
        return btnNuevoContacto;
    }

    public void setBtnNuevoContacto(JButton btnNuevoContacto) {
        this.btnNuevoContacto = btnNuevoContacto;
    }

    public JLabel getLblChats() {
        return lblChats;
    }

    public void setLblChats(JLabel lblChats) {
        this.lblChats = lblChats;
    }

    public JButton getBtnNuevoChat() {
        return btnNuevoChat;
    }

    public void setBtnNuevoChat(JButton btnNuevoChat) {
        this.btnNuevoChat = btnNuevoChat;
    }

    public JLabel getLblBuscarChat() {
        return lblBuscarChat;
    }

    public void setLblBuscarChat(JLabel lblBuscarChat) {
        this.lblBuscarChat = lblBuscarChat;
    }

    public JTextField getTextField1() {
        return txtBuscarChat;
    }

    public void setTextField1(JTextField textField1) {
        this.txtBuscarChat = textField1;
    }

    public JLabel getLblNombreMensaje() {
        return lblNombreMensaje;
    }

    public void setLblNombreMensaje(JLabel lblNombreMensaje) {
        this.lblNombreMensaje = lblNombreMensaje;
    }

    public JLabel getLblIP() {
        return lblIP;
    }

    public void setLblIP(JLabel lblIP) {
        this.lblIP = lblIP;
    }

    public JLabel getLblPuerto() {
        return lblPuerto;
    }

    public void setLblPuerto(JLabel lblPuerto) {
        this.lblPuerto = lblPuerto;
    }

    public JTextField getTextField2() {
        return txtMensaje;
    }

    public void setTextField2(JTextField textField2) {
        this.txtMensaje = textField2;
    }

    public JButton getBtnEnviar() {
        return btnEnviar;
    }

    public void setBtnEnviar(JButton btnEnviar) {
        this.btnEnviar = btnEnviar;
    }

    public JTextArea getTxtAreaConversacion() {
        return txtAreaConversacion;
    }

    public void setTxtAreaConversacion(JTextArea txtAreaConversacion) {
        this.txtAreaConversacion = txtAreaConversacion;
    }

    public JList<Contacto> getListChat() {
        return listChat;
    }

    public void setListChat(JList<Contacto> listChat) {
        this.listChat = listChat;
    }

    public JPanel getPanelContactoInfo() {
        return panelContactoInfo;
    }

    public void setPanelContactoInfo(JPanel panelContactoInfo) {
        this.panelContactoInfo = panelContactoInfo;
    }

    public JTextField getTxtBuscarChat() {
        return txtBuscarChat;
    }

    public void setTxtBuscarChat(JTextField txtBuscarChat) {
        this.txtBuscarChat = txtBuscarChat;
    }

    public JTextField getTxtMensaje() {
        return txtMensaje;
    }

    public void setTxtMensaje(JTextField txtMensaje) {
        this.txtMensaje = txtMensaje;
    }
}
