package utils;

import model.Contacto;

import javax.swing.*;
import java.awt.*;

public class ChatListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        if (value instanceof Contacto contacto) {

            if (contacto.getNotificacion().tieneMensajesNuevos()) {
                label.setText(contacto.getAlias() + " - IP: " + contacto.getIP() + "  Puerto: " + contacto.getPuerto() + "  *");
                label.setForeground(isSelected ? Color.WHITE : Color.GREEN.darker());
            } else {
                label.setText(contacto.getAlias() + "  IP: "+ contacto.getIP() + "  Puerto: " + contacto.getPuerto());
            }
        }

        return label;
    }
}
