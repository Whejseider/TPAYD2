package view;

import model.Contacto;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class NuevoChat extends JDialog {
    private JPanel pane;
    private JList<Contacto> list1;
    private JTextField txtBuscar;
    private JPanel paneBuscar;
    private JLabel lblBuscar;
    private DefaultListModel<Contacto> listModel;

    public NuevoChat() throws HeadlessException {
        super();
        this.setTitle("Nuevo Chat");
        this.setLocationRelativeTo(null);
        this.requestFocus();
        this.setContentPane(this.pane);
        this.setVisible(true);

        this.listModel = new DefaultListModel<>();
        this.list1.setModel(this.listModel);
    }

    public JList<Contacto> getList1() {
        return list1;
    }

    public void setList1(JList<Contacto> list1) {
        this.list1 = list1;
    }

    public JTextField getTxtBuscar() {
        return txtBuscar;
    }

    public void setTxtBuscar(JTextField txtBuscar) {
        this.txtBuscar = txtBuscar;
    }

    public JLabel getLblBuscar() {
        return lblBuscar;
    }

    public void setLblBuscar(JLabel lblBuscar) {
        this.lblBuscar = lblBuscar;
    }

    public void display() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void agregarContacto(Contacto c) {
        listModel.addElement(c);
    }

}
