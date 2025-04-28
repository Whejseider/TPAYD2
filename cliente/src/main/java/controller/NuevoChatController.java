package controller;

import connection.Sesion;
import model.Contacto;
import model.Conversacion;
import view.NuevoChat;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NuevoChatController implements ActionListener, ListSelectionListener {
    private NuevoChat vista;
    private Contacto contacto;
    private MessengerPanelController messengerPanelController;

    public NuevoChatController(NuevoChat vista) {
        this.vista = vista;
        this.vista.getList1().addListSelectionListener(this);

        for (Contacto c : Sesion.getInstance().getUsuarioActual().getAgenda().getContactos()) {
            this.vista.agregarContacto(c);
        }
    }

    public MessengerPanelController getMessengerController() {
        return messengerPanelController;
    }

    public void setMessengerController(MessengerPanelController messengerPanelController) {
        this.messengerPanelController = messengerPanelController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Contacto seleccionado = this.vista.getList1().getSelectedValue();
            if (seleccionado != null) {
                Conversacion conversacion = Sesion.getInstance().getUsuarioActual().getConversacionCon(seleccionado);
                SwingUtilities.invokeLater(() -> {
                    this.messengerPanelController.getVista().mostrarContactoInfo(seleccionado);
                    this.messengerPanelController.setContactoActual(seleccionado);

                    /**
                     * Si existe la conversacion entonces la muestro y selecciono, sino borro todo
                     */
                    if (conversacion.getMensajes() != null) {
                        this.messengerPanelController.mostrarChat(seleccionado);

                            this.messengerPanelController.getVista().getListChat().setSelectedValue(seleccionado, true);
                            this.messengerPanelController.revalidarListChat();

                    } else {
                        this.messengerPanelController.revalidarTxtConversacion();
                        this.messengerPanelController.getVista().getTxtAreaConversacion().setText("");
                    }


                    this.vista.dispose();
                });

            }
        }
    }
}
