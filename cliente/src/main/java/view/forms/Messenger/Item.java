package view.forms.Messenger;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import connection.Sesion;
import model.Conversacion;
import model.Mensaje;
import net.miginfocom.swing.MigLayout;
import raven.extras.AvatarIcon;

import javax.swing.*;
import java.awt.*;

public class Item extends JButton {

    private Conversacion data;

    public Item(Conversacion data) {
        this.data = data;
        init();
        actualizarApariencia();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
        setLayout(new MigLayout("wrap,fill,insets 3", "[fill]"));
        panelLabel = new PanelLabel();
        profile = new ProfileStatus(new AvatarIcon(new FlatSVGIcon("fv/drawer/image/avatar_male.svg", 100, 100), 50, 50, 3.5f));
//        profile.setActiveStatus(data.isActiveStatus());
        add(profile, "dock west,width 50,height 50,gap 6 10");
        add(panelLabel);
    }

    public Conversacion getData() {
        return data;
    }

    public boolean isActiveStatus() {
        return profile.isActiveStatus();
    }

    public void setActiveStatus(boolean activeStatus) {
//        data.setActiveStatus(activeStatus);
        profile.setActiveStatus(activeStatus);
    }

    public void setLastMessage(Mensaje lastMessage) {
        panelLabel.setLastMessage(lastMessage);
    }

    public void actualizarApariencia() {
        panelLabel.actualizarApariencia();
    }

    private ProfileStatus profile;
    private PanelLabel panelLabel;

    public class PanelLabel extends JPanel {

        public PanelLabel() {
            init();
        }

        private void init() {
            setOpaque(false);
            setLayout(new LabelLayout());
            lbName = new JLabel(data.getContacto().getNombreUsuario());
            lbEmisorName = new JLabel();
            lbMensaje = new JLabel();
            lbMensaje.setHorizontalTextPosition(SwingConstants.LEADING);
            lbStatus = new JLabel();

            lbName.putClientProperty(FlatClientProperties.STYLE, "" +
                    "font:bold");
            lbStatus.putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground:$Text.lowForeground");

            lbMensaje.putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground:$Text.middleForeground");

            add(lbName);
            add(lbEmisorName);
            add(lbMensaje);
            add(lbStatus);
            setLastMessage(data.getUltimoMensaje());
        }

        private void setLastMessage(Mensaje mensaje) {
            if (mensaje != null) {
                lbEmisorName.setText(mensaje.esMio(Sesion.getInstance().getUsuarioActual()) ? "Tú: " : "");
                lbMensaje.setText(mensaje.getContenido());
                lbMensaje.setIcon(null);
            }
        }

        public void actualizarApariencia() {
            if (data.getNotificacion().tieneMensajesNuevos()) {

                panelLabel.lbEmisorName.setForeground(new Color(0, 150, 0));
                panelLabel.lbMensaje.setForeground(new Color(0, 150, 0)); // Un verde oscuro

            } else {
                panelLabel.lbEmisorName.setForeground(null);
                panelLabel.lbMensaje.setForeground(UIManager.getColor("Text.middleForeground"));
            }
            panelLabel.revalidate();
            panelLabel.repaint();
        }

        private JLabel lbName;
        private JLabel lbEmisorName;
        private JLabel lbMensaje;
        private JLabel lbStatus;

        private class LabelLayout implements LayoutManager {

            private final int labelGap = 3;
            private final int gap = 6;

            @Override
            public void addLayoutComponent(String name, Component comp) {
            }

            @Override
            public void removeLayoutComponent(Component comp) {
            }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                synchronized (parent.getTreeLock()) {
                    Insets insets = parent.getInsets();
                    int g = UIScale.scale(gap);
                    int width = insets.left + insets.right;
                    int height = insets.top + insets.bottom + g;
                    height += lbName.getPreferredSize().height;
                    height += Math.max(lbMensaje.getPreferredSize().height, lbStatus.getPreferredSize().height);
                    return new Dimension(width, height);
                }
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                synchronized (parent.getTreeLock()) {
                    return new Dimension(0, 0);
                }
            }

            @Override
            public void layoutContainer(Container parent) {
                synchronized (parent.getTreeLock()) {
                    Insets insets = parent.getInsets();
                    int g = UIScale.scale(gap);
                    int lbg = UIScale.scale(labelGap);
                    int x = insets.left;
                    int y = insets.top;
                    int width = parent.getWidth() - (insets.left + insets.right);
                    int height = parent.getHeight() - (insets.top + insets.bottom);
                    Dimension statusSize = lbStatus.getPreferredSize();
                    lbName.setBounds(x, y, width, lbName.getPreferredSize().height);

                    int descriptionLabelWidth = lbEmisorName.getPreferredSize().width;
                    if (descriptionLabelWidth == 0) {
                        lbg = 0;
                    }
                    int descriptionWidth = width - statusSize.width - descriptionLabelWidth - g - lbg;

                    lbEmisorName.setBounds(x, y + height - lbEmisorName.getPreferredSize().height, descriptionLabelWidth, lbEmisorName.getPreferredSize().height);
                    lbMensaje.setBounds(x + descriptionLabelWidth + lbg, y + height - lbMensaje.getPreferredSize().height, descriptionWidth, lbMensaje.getPreferredSize().height);

                    lbStatus.setBounds(x + width - statusSize.width, y + height - statusSize.height, statusSize.width, statusSize.height);
                }
            }
        }
    }
}
