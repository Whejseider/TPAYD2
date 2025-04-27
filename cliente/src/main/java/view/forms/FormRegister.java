package view.forms;

import com.formdev.flatlaf.FlatClientProperties;
import controller.RegisterController;
import interfaces.IVista;
import net.miginfocom.swing.MigLayout;
import view.system.Form;

import javax.swing.*;
import java.awt.*;

public class FormRegister extends Form implements IVista<RegisterController> {

    private final JLabel lblIP;
    private final JLabel lblErrorIP;
    private final JTextField txtIP;
    private final JButton btnLogin;
    private final JLabel lblLogin;
    private JLabel lblErrorUsuario;
    private JLabel lblErrorPuerto;
    private JPanel pane;
    private JTextField txtUsuario;
    private JTextField txtPuerto;
    private JLabel lblTitulo;
    private JLabel lblDescripcion;
    private JLabel lblUsuario;
    private JLabel lblPuerto;
    private JLabel lblPuertoError;
    private JButton btnCancelar;
    private JButton btnAceptar;
    private JPanel paneBotones;
    private RegisterController controlador;

    public FormRegister() throws HeadlessException {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));

        txtUsuario = new JTextField();
        txtUsuario.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese su nombre de usuario");

        txtIP = new JTextField();
        txtIP.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese su dirección IP");

        txtPuerto = new JTextField();
        txtPuerto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese el número de puerto");


        btnAceptar = new JButton("Crear una cuenta") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        btnAceptar.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:#FFFFFF;");

        btnCancelar = new JButton("Cancelar");

        pane = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "fill,250:280"));
        pane.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        lblTitulo = new JLabel("Registrarse");
        lblTitulo.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");

        lblDescripcion = new JLabel("Por favor rellene los campos con sus credenciales");
        lblDescripcion.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,30%);" +
                "[dark]background:lighten(@background,30%)");

        lblUsuario = new JLabel("Usuario");
        lblUsuario.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold;");

        lblIP = new JLabel("IP");
        lblIP.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold;");

        lblPuerto = new JLabel("Puerto");
        lblPuerto.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold;");

        lblErrorUsuario = new JLabel("");
        lblErrorUsuario.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:plain;" +
                "foreground:#DC3545;");

        lblErrorIP = new JLabel("");
        lblErrorIP.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:plain;" +
                "foreground:#DC3545;");

        lblErrorPuerto = new JLabel("");
        lblErrorPuerto.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:plain;" +
                "foreground:#DC3545;");

        btnLogin = new JButton("<html><a href=\"#\">Iniciar Sesión</a></html>");
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,3,3,3");
        btnLogin.setContentAreaFilled(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblLogin = new JLabel("Ya tienes una cuenta ?");
        lblLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.middleForeground");

        pane.add(lblTitulo);
        pane.add(lblDescripcion);
        pane.add(new JSeparator(), "gapy 10 10");
        pane.add(lblUsuario, "gapy 8");
        pane.add(txtUsuario);
        pane.add(lblErrorUsuario);
        pane.add(lblIP, "gapy 8");
        pane.add(txtIP);
        pane.add(lblErrorIP);
        pane.add(lblPuerto, "gapy 8");
        pane.add(txtPuerto);
        pane.add(lblErrorPuerto);
        pane.add(btnAceptar, "wrap");
        pane.add(new JSeparator(), "gapy 10 10");
        pane.add(lblLogin, "gapy 8, split 2, sizegroup btn");
        pane.add(btnLogin, "sizegroup btn, wrap, gapy 8");

        add(pane);
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JTextField getTxtIP() {
        return txtIP;
    }

    public JLabel getLblIP() {
        return lblIP;
    }

    public JLabel getLblErrorIP() {
        return lblErrorIP;
    }

    public RegisterController getControlador() {
        return controlador;
    }

    public JLabel getLblErrorUsuario() {
        return lblErrorUsuario;
    }

    public void setLblErrorUsuario(JLabel lblErrorUsuario) {
        this.lblErrorUsuario = lblErrorUsuario;
    }

    public JLabel getLblErrorPuerto() {
        return lblErrorPuerto;
    }

    public void setLblErrorPuerto(JLabel lblErrorPuerto) {
        this.lblErrorPuerto = lblErrorPuerto;
    }

    public JTextField getTxtUsuario() {
        return txtUsuario;
    }

    public void setTxtUsuario(JTextField txtUsuario) {
        this.txtUsuario = txtUsuario;
    }

    public JTextField getTxtPuerto() {
        return txtPuerto;
    }

    public void setTxtPuerto(JTextField txtPuerto) {
        this.txtPuerto = txtPuerto;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    public void setBtnCancelar(JButton btnCancelar) {
        this.btnCancelar = btnCancelar;
    }

    public JButton getBtnAceptar() {
        return btnAceptar;
    }

    public void setBtnAceptar(JButton btnAceptar) {
        this.btnAceptar = btnAceptar;
    }

    public JLabel getLblPuertoError() {
        return lblPuertoError;
    }

    public void setLblPuertoError(JLabel lblPuertoError) {
        this.lblPuertoError = lblPuertoError;
    }

    public void display() {
        this.setVisible(true);
    }

    @Override
    public void setControlador(RegisterController controlador) {
        this.controlador = controlador;
    }
}
