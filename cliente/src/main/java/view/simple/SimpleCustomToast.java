package view.simple;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import connection.Sesion;
import model.Contacto;
import net.miginfocom.swing.MigLayout;
import raven.extras.AvatarIcon;
import raven.modal.toast.ToastCustomPanel;

import javax.swing.*;

public class SimpleCustomToast extends ToastCustomPanel {

    public SimpleCustomToast(Contacto contacto) {
        init(contacto);
    }

    private void init(Contacto contacto) {
        setOpaque(false);
        setLayout(new MigLayout("ay center,insets 0", "", "[sg h,bottom][sg h,top]"));
        JLabel labelProfile = new JLabel(new AvatarIcon(new FlatSVGIcon("fv/drawer/image/avatar_male.svg", 100, 100), 50, 50, 3.5f));
        JLabel labelName = new JLabel(contacto.getNombreUsuario());
        JLabel labelDescription = new JLabel(Sesion.getInstance().getUsuarioActual().getConversacionCon(contacto.getNombreUsuario()).getUltimoMensaje().getContenido());

        JButton buttonClose = new JButton(new FlatSVGIcon("fv/icons/close.svg", 0.3f));
        buttonClose.setFocusable(false);

        labelName.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +1;" +
                "foreground:$Component.accentColor;");

        buttonClose.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:5,5,5,5;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "background:null;");

        buttonClose.addActionListener(e -> toastAction.close());

        add(labelProfile, "span 1 2,w 55::,h 55::,grow 0");
        add(labelName, "cell 1 0");
        add(labelDescription, "cell 1 1");
        add(buttonClose, "cell 2 0,span 1 2,ay top,gap 3 3 3 3");
    }
}
