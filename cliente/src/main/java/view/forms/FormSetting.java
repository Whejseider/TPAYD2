package view.forms;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.LoggingFacade;
import net.miginfocom.swing.MigLayout;
import raven.modal.Drawer;
import raven.modal.ModalDialog;
import raven.modal.drawer.DrawerBuilder;
import raven.modal.drawer.renderer.AbstractDrawerLineStyleRenderer;
import raven.modal.drawer.renderer.DrawerCurvedLineStyle;
import raven.modal.drawer.renderer.DrawerStraightDotLineStyle;
import raven.modal.drawer.simple.SimpleDrawerBuilder;
import raven.modal.option.LayoutOption;
import raven.modal.option.Location;
import utils.SystemForm;
import view.component.AccentColorIcon;
import view.system.Form;
import view.system.FormManager;
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

//        tabbedPane.addTab("Diseño", createLayoutOption());
        tabbedPane.addTab("Estilo", createStyleOption());
        add(tabbedPane, "gapy 1 0");
        add(createThemes());
    }

    private JPanel createLayoutOption() {
        JPanel panel = new JPanel(new MigLayout("wrap,fillx", "[fill]"));
        panel.add(createWindowsLayout());
        panel.add(createDrawerLayout());
        panel.add(createModalDefaultOption());
        return panel;
    }

    private Component createWindowsLayout() {
        JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(new TitledBorder("Diseño de ventana"));
        JCheckBox chRightToLeft = new JCheckBox("Derecha a Izquierda", !getComponentOrientation().isLeftToRight());
        JCheckBox chFullWindow = new JCheckBox("Contenido de Ventana Completa", FlatClientProperties.clientPropertyBoolean(FormManager.getFrame().getRootPane(), FlatClientProperties.FULL_WINDOW_CONTENT, false));
        chRightToLeft.addActionListener(e -> {
            if (chRightToLeft.isSelected()) {
                FormManager.getFrame().applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            } else {
                FormManager.getFrame().applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            }
            FormManager.getFrame().revalidate();
        });
        chFullWindow.addActionListener(e -> {
            FormManager.getFrame().getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, chFullWindow.isSelected());
        });
        panel.add(chRightToLeft);
        panel.add(chFullWindow);
        return panel;
    }

    private Component createDrawerLayout() {
        JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(new TitledBorder("Diseño del drawer"));

        JRadioButton jrLeft = new JRadioButton("Izquierda");
        JRadioButton jrLeading = new JRadioButton("Principal");
        JRadioButton jrTrailing = new JRadioButton("Final");
        JRadioButton jrRight = new JRadioButton("Derecha");
        JRadioButton jrTop = new JRadioButton("Superior");
        JRadioButton jrBottom = new JRadioButton("Inferior");

        ButtonGroup group = new ButtonGroup();
        group.add(jrLeft);
        group.add(jrLeading);
        group.add(jrTrailing);
        group.add(jrRight);
        group.add(jrTop);
        group.add(jrBottom);

        jrLeading.setSelected(true);

        jrLeft.addActionListener(e -> {
            DrawerBuilder drawerBuilder = Drawer.getDrawerBuilder();
            LayoutOption layoutOption = Drawer.getDrawerOption().getLayoutOption();
            layoutOption.setSize(drawerBuilder.getDrawerWidth(), 1f)
                    .setLocation(Location.LEFT, Location.TOP)
                    .setAnimateDistance(-0.7f, 0f);
            getRootPane().revalidate();
        });
        jrLeading.addActionListener(e -> {
            DrawerBuilder drawerBuilder = Drawer.getDrawerBuilder();
            LayoutOption layoutOption = Drawer.getDrawerOption().getLayoutOption();
            layoutOption.setSize(drawerBuilder.getDrawerWidth(), 1f)
                    .setLocation(Location.LEADING, Location.TOP)
                    .setAnimateDistance(-0.7f, 0f);
            getRootPane().revalidate();
        });
        jrTrailing.addActionListener(e -> {
            DrawerBuilder drawerBuilder = Drawer.getDrawerBuilder();
            LayoutOption layoutOption = Drawer.getDrawerOption().getLayoutOption();
            layoutOption.setSize(drawerBuilder.getDrawerWidth(), 1f)
                    .setLocation(Location.TRAILING, Location.TOP)
                    .setAnimateDistance(0.7f, 0f);
            getRootPane().revalidate();
        });
        jrRight.addActionListener(e -> {
            DrawerBuilder drawerBuilder = Drawer.getDrawerBuilder();
            LayoutOption layoutOption = Drawer.getDrawerOption().getLayoutOption();
            layoutOption.setSize(drawerBuilder.getDrawerWidth(), 1f)
                    .setLocation(Location.RIGHT, Location.TOP)
                    .setAnimateDistance(0.7f, 0f);
            getRootPane().revalidate();
        });
        jrTop.addActionListener(e -> {
            DrawerBuilder drawerBuilder = Drawer.getDrawerBuilder();
            LayoutOption layoutOption = Drawer.getDrawerOption().getLayoutOption();
            layoutOption.setSize(1f, drawerBuilder.getDrawerWidth())
                    .setLocation(Location.LEADING, Location.TOP)
                    .setAnimateDistance(0f, -0.7f);
            getRootPane().revalidate();
        });
        jrBottom.addActionListener(e -> {
            DrawerBuilder drawerBuilder = Drawer.getDrawerBuilder();
            LayoutOption layoutOption = Drawer.getDrawerOption().getLayoutOption();
            layoutOption.setSize(1f, drawerBuilder.getDrawerWidth())
                    .setLocation(Location.LEADING, Location.BOTTOM)
                    .setAnimateDistance(0f, 0.7f);
            getRootPane().revalidate();
        });

        panel.add(jrLeft);
        panel.add(jrLeading);
        panel.add(jrTrailing);
        panel.add(jrRight);
        panel.add(jrTop);
        panel.add(jrBottom);
        return panel;
    }

    private Component createModalDefaultOption() {
        JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(new TitledBorder("Opción Modal Predeterminada"));
        JCheckBox chAnimation = new JCheckBox("Animación activada");
        JCheckBox chCloseOnPressedEscape = new JCheckBox("Cerrar al presionar la tecla Escape");
        chAnimation.setSelected(ModalDialog.getDefaultOption().isAnimationEnabled());
        chCloseOnPressedEscape.setSelected(ModalDialog.getDefaultOption().isCloseOnPressedEscape());

        chAnimation.addActionListener(e -> ModalDialog.getDefaultOption().setAnimationEnabled(chAnimation.isSelected()));
        chCloseOnPressedEscape.addActionListener(e -> ModalDialog.getDefaultOption().setCloseOnPressedEscape(chCloseOnPressedEscape.isSelected()));

        panel.add(chAnimation);
        panel.add(chCloseOnPressedEscape);

        return panel;
    }

    private JPanel createStyleOption() {
        JPanel panel = new JPanel(new MigLayout("wrap,fillx", "[fill]"));
        panel.add(createAccentColor());
//        panel.add(createDrawerStyle());
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

    private Component createDrawerStyle() {
        JPanel panel = new JPanel(new MigLayout("insets 0,filly", "[][][grow,fill]", "[fill]"));
        JPanel lineStyle = new JPanel(new MigLayout("wrap", "[200]"));
        JPanel lineStyleOption = new JPanel(new MigLayout("wrap", "[200]"));
        JPanel lineColorOption = new JPanel(new MigLayout("wrap", "[200]"));

        lineStyle.setBorder(new TitledBorder("Estilo de línea del Drawer"));
        lineStyleOption.setBorder(new TitledBorder("Opción del estilo de línea"));
        lineColorOption.setBorder(new TitledBorder("Opción del color"));

        ButtonGroup groupStyle = new ButtonGroup();
        JRadioButton jrCurvedStyle = new JRadioButton("Estilo línea curvada");
        JRadioButton jrStraightDotStyle = new JRadioButton("Estilo línea recta con puntos", true);
        groupStyle.add(jrCurvedStyle);
        groupStyle.add(jrStraightDotStyle);

        ButtonGroup groupStyleOption = new ButtonGroup();
        JRadioButton jrStyleOption1 = new JRadioButton("Rectángulo");
        JRadioButton jrStyleOption2 = new JRadioButton("Elipse", true);
        groupStyleOption.add(jrStyleOption1);
        groupStyleOption.add(jrStyleOption2);

        JCheckBox chPaintLineColor = new JCheckBox("Pintar de color la línea seleccionada");

        jrCurvedStyle.addActionListener(e -> {
            if (jrCurvedStyle.isSelected()) {
                jrStyleOption1.setText("Línea");
                jrStyleOption2.setText("Curva");
                boolean round = jrStyleOption2.isSelected();
                boolean paintSelectedLine = chPaintLineColor.isSelected();
                setDrawerLineStyle(true, round, paintSelectedLine);
            }
        });
        jrStraightDotStyle.addActionListener(e -> {
            if (jrStraightDotStyle.isSelected()) {
                jrStyleOption1.setText("Rectangulo");
                jrStyleOption2.setText("Elipse");
                boolean round = jrStyleOption2.isSelected();
                boolean paintSelectedLine = chPaintLineColor.isSelected();
                setDrawerLineStyle(false, round, paintSelectedLine);
            }
        });

        jrStyleOption1.addActionListener(e -> {
            if (jrStyleOption1.isSelected()) {
                boolean curved = jrCurvedStyle.isSelected();
                boolean paintSelectedLine = chPaintLineColor.isSelected();
                setDrawerLineStyle(curved, false, paintSelectedLine);
            }
        });

        jrStyleOption2.addActionListener(e -> {
            if (jrStyleOption2.isSelected()) {
                boolean curved = jrCurvedStyle.isSelected();
                boolean paintSelectedLine = chPaintLineColor.isSelected();
                setDrawerLineStyle(curved, true, paintSelectedLine);
            }
        });

        chPaintLineColor.addActionListener(e -> {
            boolean curved = jrCurvedStyle.isSelected();
            boolean round = jrStyleOption2.isSelected();
            boolean paintSelectedLine = chPaintLineColor.isSelected();
            setDrawerLineStyle(curved, round, paintSelectedLine);
        });

        lineStyle.add(jrCurvedStyle);
        lineStyle.add(jrStraightDotStyle);

        lineStyleOption.add(jrStyleOption1);
        lineStyleOption.add(jrStyleOption2);

        lineColorOption.add(chPaintLineColor);

        panel.add(lineStyle);
        panel.add(lineStyleOption);
        panel.add(lineColorOption);
        return panel;
    }

    private void setDrawerLineStyle(boolean curved, boolean round, boolean color) {
        AbstractDrawerLineStyleRenderer style;
        if (curved) {
            style = new DrawerCurvedLineStyle(round, color);
        } else {
            style = new DrawerStraightDotLineStyle(round, color);
        }
        ((SimpleDrawerBuilder) Drawer.getDrawerBuilder()).getSimpleMenuOption().getMenuStyle().setDrawerLineStyleRenderer(style);
        ((SimpleDrawerBuilder) Drawer.getDrawerBuilder()).getDrawerMenu().repaint();
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
