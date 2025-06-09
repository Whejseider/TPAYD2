package view.system;

import config.Config;
import encryption.EncryptionType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FormPersistence extends JDialog {
    private JComboBox<Config.PersistenceType> comboPersistence;
    private JComboBox<EncryptionType> comboEncryption;
    private JButton btnAceptar;

    public FormPersistence(Form owner) {
        super();
        setTitle("Configuración de persistencia");

        setLayout(new MigLayout("wrap 2, fillx", "[right][grow,fill]"));

        JLabel lblTitulo = new JLabel("<html><b>Bienvenido/a a Messenger</b></html>");
        add(lblTitulo, "span, align center, gaptop 10, gapbottom 15");

        JLabel lblPersistence = new JLabel("Método de guardado:");
        comboPersistence = new JComboBox<>(Config.PersistenceType.values());
        add(lblPersistence);
        add(comboPersistence);

        JLabel lblEncryption = new JLabel("Método de cifrado:");
        comboEncryption = new JComboBox<>(EncryptionType.values());
        add(lblEncryption);
        add(comboEncryption);

        btnAceptar = new JButton("Guardar y Continuar");
        btnAceptar.addActionListener(e -> {
            Config config = Config.getInstance();

            config.setPersistenceType((Config.PersistenceType) comboPersistence.getSelectedItem());
            config.setEncryptionType((EncryptionType) comboEncryption.getSelectedItem());

            config.saveConfiguration();
            dispose();
        });

        add(btnAceptar, "span, align center, gaptop 20, gapbottom 10");

        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(
                        FormPersistence.this,
                        "Debes seleccionar ambas opciones para continuar.",
                        "Acción Requerida",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
