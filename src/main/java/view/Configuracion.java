package view;

import javax.swing.*;
import java.awt.*;

public class Configuracion extends JDialog {
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

    public Configuracion() throws HeadlessException {
        super();
        this.setTitle("Configuración");
        this.setSize(400,350);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setModalityType(ModalityType.MODELESS);

        // Panel principal
        pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de contenido
        paneContenido = new JPanel();
        paneContenido.setLayout(new GridLayout(6,1,10,10));

        // Nombre de usuario
        lblUsuario = new JLabel("Nombre de usuario:");
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsuario = new JTextField();
        txtUsuario.setMargin(new Insets(5,5,5,5));

        // Puerto
        lblPuerto = new JLabel("Puerto:");
        lblPuerto.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPuerto = new JTextField();
        txtPuerto.setMargin(new Insets(0,5,0,0));


        // Error del puerto
        lblPuertoError = new JLabel(" ");
        lblPuertoError.setForeground(Color.RED);
        lblPuertoError.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPuertoError.setVisible(false);

        // Panel de botones
        paneBotones = new JPanel();
        paneBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(255, 255, 255)); // Color azul similar al de la imagen
        btnCancelar.setForeground(Color.GRAY);
        btnCancelar.setPreferredSize(new Dimension(120, 40));

        btnAceptar = new JButton("Aceptar");
        btnAceptar.setBackground(new Color(33, 150, 243)); // Color azul similar al de la imagen
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setPreferredSize(new Dimension(120, 40));

        // Agregar componentes a los paneles
        paneContenido.add(lblUsuario);
        paneContenido.add(txtUsuario);
        paneContenido.add(lblPuerto);
        paneContenido.add(txtPuerto);
        paneContenido.add(lblPuertoError);

        paneBotones.add(btnCancelar);
        paneBotones.add(btnAceptar);


        pane.add(paneContenido, BorderLayout.CENTER);
        pane.add(paneBotones, BorderLayout.SOUTH);

        this.setContentPane(pane);
        this.setVisible(true);
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

    public void display(){
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void mostrarErrorPuerto(String mensaje) {
        lblPuertoError.setText(mensaje);
        lblPuertoError.setVisible(true);
        this.revalidate(); // Forzar actualización del layout
        this.repaint();    // Forzar repintado
    }

    public void limpiarErrores() {
        lblPuertoError.setVisible(false);
        this.revalidate();
        this.repaint();
    }

}
