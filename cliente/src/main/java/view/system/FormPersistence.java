package view.system;

import com.formdev.flatlaf.FlatClientProperties;
import config.Config;
import encryption.EncryptionType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FormPersistence extends JDialog {
    private final JComboBox<Config.PersistenceType> comboPersistence;
    private final JComboBox<EncryptionType> comboEncryption;
    private final JButton btnAceptar;

    public FormPersistence(Frame owner) {
        super(owner, "Configuración de Persistencia y Cifrado", true);

        setLayout(new MigLayout("wrap 2, insets 20, gap 10", "[right][grow, fill]"));


        JLabel lblTitulo = new JLabel(
                "<html><div style='text-align: center;'><b>Elija el formato de guardado de los archivos<br>" +
                        "y el método de cifrado de los mensajes.</b><br><br>" +
                        "El método de cifrado se puede modificar más tarde<br>" +
                        "en la configuración de la aplicación.</div></html>");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.PLAIN, 13));
        add(lblTitulo, "span, align center, gapbottom 20");


        add(new JLabel("Método de guardado:"));
        comboPersistence = new JComboBox<>(Config.PersistenceType.values());
        add(comboPersistence);

        add(new JLabel("Método de cifrado:"));
        comboEncryption = new JComboBox<>(EncryptionType.values());
        add(comboEncryption);

        btnAceptar = new JButton("Guardar y Continuar"){
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        btnAceptar.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:4,10,4,10;" +
                "arc:12;");

        btnAceptar.setFocusPainted(false);
        btnAceptar.setPreferredSize(new Dimension(180, 30));
        btnAceptar.addActionListener(e -> {
            Config config = Config.getInstance();
            config.setPersistenceType((Config.PersistenceType) comboPersistence.getSelectedItem());
            config.setEncryptionType((EncryptionType) comboEncryption.getSelectedItem());
            config.saveConfiguration();
            dispose();
        });
        add(btnAceptar, "span, align center, gaptop 20");

        pack();
        setMinimumSize(new Dimension(400, getHeight()));
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(
                        FormPersistence.this,
                        "Debe presionar el botón 'Guardar y Continuar' una vez que haya elegido sus opciones para continuar.",
                        "Acción Requerida",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });
    }
}
