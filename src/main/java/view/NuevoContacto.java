package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.ConfigurationController;
import controller.NuevoContactoController;
import interfaces.IVista;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class NuevoContacto extends JDialog implements IVista<NuevoContactoController> {
    private JPanel pane;
    private JTextField txtUsuario;
    private JTextField txtPuerto;
    private JTextField txtIP;
    private JLabel lblTitulo;
    private JLabel lblDescripcion;
    private JLabel lblUsuario;
    private JLabel lblPuerto;
    private JLabel lblIP;
    private JButton btnCancelar;
    private JButton btnAceptar;
    private JPanel paneBotones;
    private JLabel lblErrorIP;
    private JLabel lblErrorPuerto;
    private NuevoContactoController controlador;
    private Frame main;

    public NuevoContacto(Frame main) throws HeadlessException {
        super(main);
        this.main = main;
        setTitle("Nuevo Contacto");
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));

        txtUsuario = new JTextField();
        txtUsuario.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Ingrese su nombre de usuario");

        txtPuerto = new JTextField();
        txtPuerto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Ingrese el número de puerto");

        txtIP = new JTextField();
        txtIP.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Ingrese la dirección IP");

        btnAceptar = new JButton("Aceptar"){
            @Override
            public boolean isDefaultButton(){
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

        lblTitulo = new JLabel("Nuevo Contacto");
        lblTitulo.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");

        lblDescripcion = new JLabel("Por favor ingrese los datos del contacto");
        lblDescripcion.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,30%);" +
                "[dark]background:lighten(@background,30%)");

        lblUsuario = new JLabel("Usuario");

        lblPuerto = new JLabel("Puerto");

        lblIP = new JLabel("IP");

        lblErrorIP = new JLabel("");
        lblErrorIP.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:plain;" +
                "foreground:#DC3545;");

        lblErrorPuerto = new JLabel("");
        lblErrorPuerto.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:plain;"+
                "foreground:#DC3545;");

        pane.add(lblTitulo);
        pane.add(lblDescripcion);
        pane.add(new JSeparator(), "gapy 10 10");
        pane.add(lblUsuario, "gapy 8");
        pane.add(txtUsuario);
        pane.add(lblPuerto, "gapy 8");
        pane.add(txtPuerto);
        pane.add(lblErrorPuerto);
        pane.add(lblIP, "gapy 8");
        pane.add(txtIP);
        pane.add(lblErrorIP);
        pane.add(btnCancelar, "gapy 10, split 2, sizegroup btn");
        pane.add(btnAceptar, "sizegroup btn, wrap");

        add(pane);
    }

    public JLabel getLblErrorIP() {
        return lblErrorIP;
    }

    public JLabel getLblErrorPuerto() {
        return lblErrorPuerto;
    }

    public NuevoContactoController getControlador() {
        return controlador;
    }

    public JTextField getTxtIP() {
        return txtIP;
    }

    public void setTxtIP(JTextField txtIP) {
        this.txtIP = txtIP;
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


    public void display() {
        this.pack();
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(main);
        this.setVisible(true);
    }


    @Override
    public void setControlador(NuevoContactoController controlador) {
        this.controlador = controlador;
    }
}