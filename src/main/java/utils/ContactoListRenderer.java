package utils;

import model.Contacto;

import javax.swing.*;
import java.awt.*;

public class ContactoListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        if (value instanceof Contacto) {
            Contacto contacto = (Contacto) value;

            if (contacto.tieneMensajesNuevos()) {
                label.setText("● " + contacto.getNombreUsuario());
                label.setForeground(isSelected ? Color.WHITE : Color.GREEN);
            } else {
                label.setText(contacto.getNombreUsuario());
            }
        }

        return label;
    }
}
