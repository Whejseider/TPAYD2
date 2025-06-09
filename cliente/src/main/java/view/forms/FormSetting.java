package view.forms;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.LoggingFacade;
import config.Config;
import encryption.EncryptionType;
import net.miginfocom.swing.MigLayout;
import utils.SystemForm;
import view.component.AccentColorIcon;
import view.system.Form;
import view.themes.PanelThemes;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

@SystemForm(name = "Configuración", description = "Configuración de la aplicación", tags = {"temas", "opciones"})
public class FormSetting extends Form {

    public FormSetting() {
        init();
    }

    private void init() {
        setName("FormSetting");
        setLayout(new MigLayout("fill", "[fill][fill,grow 0,250:250]", "[fill]"));
        tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "" +
                "tabType:card");

        tabbedPane.addTab("Encriptado", createEncryptOption());
        tabbedPane.addTab("Estilo", createStyleOption());
        add(tabbedPane, "gapy 1 0");
        add(createThemes());
    }

    private JPanel createEncryptOption() {
        JPanel panel = new JPanel(new MigLayout("wrap,fillx", "[fill]"));
        panel.add(createEncryptionLayout());
        return panel;
    }

    private Component createEncryptionLayout() {
        JPanel panel = new JPanel(new MigLayout("", "[grow]", "[]10[]10[]"));
        panel.setBorder(new TitledBorder("Configuración de la Encriptación de Mensajes"));

        JRadioButton jrAES = new JRadioButton("AES_GCM");
        JRadioButton jrChaCha20 = new JRadioButton("ChaCha20-poly1305");
        JRadioButton jrBlowfish = new JRadioButton("Blowfish");

        ButtonGroup group = new ButtonGroup();
        group.add(jrAES);
        group.add(jrChaCha20);
        group.add(jrBlowfish);

        switch (Config.getInstance().getEncryptionType()) {
            case AES_GCM -> jrAES.setSelected(true);
            case BLOWFISH -> jrBlowfish.setSelected(true);
            case CHACHA20 -> jrChaCha20.setSelected(true);
        }

        jrAES.addActionListener(e -> {
            Config.getInstance().setEncryptionType(EncryptionType.AES_GCM);
            Config.getInstance().saveConfiguration();
            System.out.println(Config.getInstance().getEncryptionType());
        });

        jrChaCha20.addActionListener(e -> {
            Config.getInstance().setEncryptionType(EncryptionType.CHACHA20);
            Config.getInstance().saveConfiguration();
            System.out.println(Config.getInstance().getEncryptionType());
        });

        jrBlowfish.addActionListener(e -> {
            Config.getInstance().setEncryptionType(EncryptionType.BLOWFISH);
            Config.getInstance().saveConfiguration();
            System.out.println(Config.getInstance().getEncryptionType());
        });

        JLabel lblClave = new JLabel("Clave secreta:");
        JPasswordField txtClave = new JPasswordField(2);
        txtClave.setText(Config.getInstance().getLocalPassphrase());
        txtClave.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Escriba su clave secreta para la comunicación.");
        txtClave.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:4,10,4,10;" +
                "arc:12;" +
                "showRevealButton:true;");

        JButton btnGuardarClave = getBtnGuardarClave(txtClave, panel);

        panel.add(jrAES, "wrap");
        panel.add(jrChaCha20, "wrap");
        panel.add(jrBlowfish, "wrap");
        panel.add(lblClave, "split 2");
        panel.add(txtClave, "w 75%");
        panel.add(btnGuardarClave);

        return panel;
    }

    private static JButton getBtnGuardarClave(JTextField txtClave, JPanel panel) {
        JButton btnGuardarClave = new JButton("Guardar clave") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };

        btnGuardarClave.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:4,10,4,10;" +
                "arc:12;");

        btnGuardarClave.addActionListener(e -> {
            String nuevaClave = txtClave.getText().trim();
            if (!nuevaClave.isEmpty()) {
                Config.getInstance().setLocalPassphrase(nuevaClave);
                Config.getInstance().saveConfiguration();
                JOptionPane.showMessageDialog(panel, "Clave secreta actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, "La clave no puede estar vacía o contener menos de 8 caractéres.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return btnGuardarClave;
    }


    private JPanel createStyleOption() {
        JPanel panel = new JPanel(new MigLayout("wrap,fillx", "[fill]"));
        panel.add(createAccentColor());
        return panel;
    }

    private static String[] accentColorKeys = {
            "fv.accent.default", "fv.accent.blue", "fv.accent.purple", "fv.accent.red",
            "fv.accent.orange", "fv.accent.yellow", "fv.accent.green",
    };
    private static String[] accentColorNames = {
            "Predeterminada", "Azul", "Violeta", "Rojo", "Naranja", "Amarillo", "Verde",
    };
    private final JToggleButton[] accentColorButtons = new JToggleButton[accentColorKeys.length];
    private Color accentColor;

    private Component createAccentColor() {
        JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(new TitledBorder("Color de acento"));
        ButtonGroup group = new ButtonGroup();
        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty(FlatClientProperties.STYLE, "" +
                "hoverButtonGroupBackground:null;");
        for (int i = 0; i < accentColorButtons.length; i++) {
            accentColorButtons[i] = new JToggleButton(new AccentColorIcon(accentColorKeys[i]));
            accentColorButtons[i].setToolTipText(accentColorNames[i]);
            accentColorButtons[i].addActionListener(this::accentColorChanged);
            toolBar.add(accentColorButtons[i]);
            group.add(accentColorButtons[i]);
        }
        accentColorButtons[0].setSelected(true);

        FlatLaf.setSystemColorGetter(name -> name.equals("accent") ? accentColor : null);
        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName())) {
                updateAccentColorButtons();
            }
        });
        updateAccentColorButtons();
        panel.add(toolBar);
        return panel;
    }

    private void accentColorChanged(ActionEvent e) {
        String accentColorKey = null;
        for (int i = 0; i < accentColorButtons.length; i++) {
            if (accentColorButtons[i].isSelected()) {
                accentColorKey = accentColorKeys[i];
                break;
            }
        }
        accentColor = (accentColorKey != null && accentColorKey != accentColorKeys[0])
                ? UIManager.getColor(accentColorKey)
                : null;
        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        try {
            FlatLaf.setup(lafClass.getDeclaredConstructor().newInstance());
            FlatLaf.updateUI();
        } catch (Exception ex) {
            LoggingFacade.INSTANCE.logSevere(null, ex);
        }
    }

    private void updateAccentColorButtons() {
        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        boolean isAccentColorSupported =
                lafClass == FlatLightLaf.class ||
                        lafClass == FlatDarkLaf.class ||
                        lafClass == FlatIntelliJLaf.class ||
                        lafClass == FlatDarculaLaf.class ||
                        lafClass == FlatMacLightLaf.class ||
                        lafClass == FlatMacDarkLaf.class;
        for (int i = 0; i < accentColorButtons.length; i++) {
            accentColorButtons[i].setEnabled(isAccentColorSupported);
        }
    }

    private JPanel createThemes() {
        JPanel panel = new JPanel(new MigLayout("wrap,fill,insets 0", "[fill]", "[grow 0,fill]0[fill]"));
        final PanelThemes panelThemes = new PanelThemes();
        JPanel panelHeader = new JPanel(new MigLayout("fillx,insets 3", "[grow 0]push[]"));
        panelHeader.add(new JLabel("Temas"));
        JComboBox combo = new JComboBox(new Object[]{"Todos", "Claros", "Oscuros"});
        combo.addActionListener(e -> {
            panelThemes.updateThemesList(combo.getSelectedIndex());
        });
        panelHeader.add(combo);
        panel.add(panelHeader);
        panel.add(panelThemes);
        return panel;
    }

    private JTabbedPane tabbedPane;
}
