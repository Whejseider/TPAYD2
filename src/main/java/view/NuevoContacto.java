package view;

import javax.swing.*;
import java.awt.*;

public class NuevoContacto extends JDialog {
    private JPanel pane;
    private JTextField txtUsuario;
    private JTextField txtPuerto;
    private JLabel lblUsuario;
    private JLabel lblPuerto;
    private JLabel lblPuertoError;
    private JButton btnCancelar;
    private JButton btnAceptar;
    private JPanel paneContenido;
    private JPanel paneBotones;
    private JLabel lblIP;
    private JTextField txtIP;
    private JLabel lblIPError;

    public NuevoContacto() throws HeadlessException {
        super();
        this.setTitle("Nuevo Contacto");
        this.setSize(400, 350);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setModalityType(ModalityType.MODELESS);

        // Panel principal
        pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de contenido
        paneContenido = new JPanel();
        paneContenido.setLayout(new GridLayout(9, 1, 5, 5));

        // Nombre de usuario
        lblUsuario = new JLabel("Nombre de usuario:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        txtUsuario = new JTextField();
        txtUsuario.setMargin(new Insets(5, 5, 5, 5));

        // Puerto
        lblPuerto = new JLabel("Puerto:");
        lblPuerto.setFont(new Font("Arial", Font.BOLD, 14));
        txtPuerto = new JTextField();
        txtPuerto.setMargin(new Insets(5, 5, 5, 5));

        // Error del puerto
        lblPuertoError = new JLabel(" ");
        lblPuertoError.setForeground(Color.RED);
        lblPuertoError.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPuertoError.setVisible(false);

        // IP
        lblIP = new JLabel("Dirección IP:");
        lblIP.setFont(new Font("Arial", Font.BOLD, 14));
        txtIP = new JTextField();
        txtIP.setMargin(new Insets(5, 5, 5, 5));

        // Error de IP
        lblIPError = new JLabel(" ");
        lblIPError.setForeground(Color.RED);
        lblIPError.setFont(new Font("Arial", Font.PLAIN, 12));
        lblIPError.setVisible(false);

        // Panel de botones
        paneBotones = new JPanel();
        paneBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setPreferredSize(new Dimension(120, 40));

        btnAceptar = new JButton("Aceptar");
        btnAceptar.setBackground(new Color(33, 150, 243));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setPreferredSize(new Dimension(120, 40));

        // Agregar componentes a los paneles
        paneContenido.add(lblUsuario);
        paneContenido.add(txtUsuario);
        paneContenido.add(lblPuerto);
        paneContenido.add(txtPuerto);
        paneContenido.add(lblPuertoError);
        paneContenido.add(lblIP);
        paneContenido.add(txtIP);
        paneContenido.add(lblIPError);

        paneBotones.add(btnCancelar);
        paneBotones.add(btnAceptar);

        pane.add(paneContenido, BorderLayout.CENTER);
        pane.add(paneBotones, BorderLayout.SOUTH);

        this.setContentPane(pane);
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

    public JLabel getLblPuertoError() {
        return lblPuertoError;
    }

    public void setLblPuertoError(JLabel lblPuertoError) {
        this.lblPuertoError = lblPuertoError;
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

    public JLabel getLblIP() {
        return lblIP;
    }

    public void setLblIP(JLabel lblIP) {
        this.lblIP = lblIP;
    }

    public JTextField getTxtIP() {
        return txtIP;
    }

    public void setTxtIP(JTextField txtIP) {
        this.txtIP = txtIP;
    }

    public JLabel getLblIPError() {
        return lblIPError;
    }

    public void setLblIPError(JLabel lblIPError) {
        this.lblIPError = lblIPError;
    }

    public void display() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void mostrarErrorPuerto(String mensaje) {
        lblPuertoError.setText(mensaje);
        lblPuertoError.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    public void mostrarErrorIP(String mensaje) {
        lblIPError.setText(mensaje);
        lblIPError.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    public void limpiarErrores() {
        lblPuertoError.setVisible(false);
        lblIPError.setVisible(false);
        this.revalidate();
        this.repaint();
    }
}