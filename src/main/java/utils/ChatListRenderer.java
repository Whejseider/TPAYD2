package utils;

import model.Contacto;
import model.Conversacion;

import javax.swing.*;
import java.awt.*;

public class ChatListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        if (value instanceof Conversacion conversacion) {

            if (conversacion.getNotificacion().tieneMensajesNuevos()) {
                label.setText(conversacion.getContacto().getAlias() + " - IP: " + conversacion.getContacto().getIP() + "  Puerto: " + conversacion.getContacto().getPuerto() + "  *");
                label.setForeground(isSelected ? Color.WHITE : Color.GREEN.darker());
            } else {
                label.setText(conversacion.getContacto().getAlias() + "  IP: "+ conversacion.getContacto().getIP() + "  Puerto: " + conversacion.getContacto().getPuerto());
            }
        }

        return label;
    }
}
