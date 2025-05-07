package view.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import model.Mensaje;
import net.miginfocom.swing.MigLayout;
import utils.ScrollBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Chat_Body extends JPanel {

    private JPanel body;
    private JScrollPane sp;
    private JPanel messageWrapperPanel;

    public Chat_Body() {
        initComponents();
        init();
        addDate("Today");
        addDate("13/05/1998");
    }

    private void initComponents() {
        // Crear panel del cuerpo con MigLayout directamente
        body = new JPanel(new MigLayout("fillx, wrap", "[fill]", "20[]20"));
        body.setOpaque(true); // Asegura que el fondo sea pintado

        messageWrapperPanel = new JPanel(new BorderLayout());
        messageWrapperPanel.setOpaque(false); // Para que se vea el fondo del JScrollPane o Chat_Body
        messageWrapperPanel.add(body, BorderLayout.SOUTH); // <-- ¡LA CLAVE! Empuja 'body' hacia abajo

        // Crear scroll con custom scrollbar
        sp = new JScrollPane(messageWrapperPanel);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBar(new ScrollBar());
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);

        // Establecer layout principal
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
    }

    private void init() {


    }

    public void addItemLeft(Mensaje mensaje) {
        Chat_Left item = new Chat_Left();
        item.setText(mensaje.getContenido());
        item.setTime(mensaje.getTiempoFormateado());
        // Añadir "growx 0" para evitar que crezca horizontalmente
        // "shrinkx 0" (opcional pero bueno) para evitar que se encoja si el espacio es muy poco
        // "wmax 80%" para limitar el ancho máximo al 80% del contenedor
        body.add(item, "growx 0, shrinkx 0, wmax 80%");
        refreshMessages();
    }

    public void addItemRight(Mensaje mensaje) {
        Chat_Right item = new Chat_Right();
        item.setText(mensaje.getContenido());
        item.setTime(mensaje.getTiempoFormateado());
        // Añadir "align right" para alinear a la derecha de la celda
        // "growx 0" y "shrinkx 0" como en addItemLeft
        body.add(item, "align right, growx 0, shrinkx 0, wmax 80%");
        refreshMessages();
        scrollToBottom();
    }

    public void addDate(String date) {
        Chat_Date item = new Chat_Date();
        item.setText(date);
        // "align center" para centrar
        // "growx 0" y "shrinkx 0" para que tome su ancho preferido
        body.add(item, "align center, growx 0, shrinkx 0");
        refreshMessages();
    }

    private void scrollToBottom() {
        // Tu método scrollToBottom parece un poco complejo con el AdjustmentListener
        // que se auto-remueve. Una forma más simple y robusta suele ser:
        SwingUtilities.invokeLater(() -> {
            if (sp != null) {
                JScrollBar verticalBar = sp.getVerticalScrollBar();
                if (verticalBar != null) {
                    verticalBar.setValue(verticalBar.getMaximum());
                }
            }
        });
    }

    private void refreshMessages() {
        body.revalidate();
        body.repaint();
        // Es posible que también necesites revalidar/repintar el wrapper o el scrollpane
        // si encuentras problemas de renderizado, aunque usualmente body.revalidate() es suficiente
        // para que el JScrollPane actualice sus barras de scroll.
        // messageWrapperPanel.revalidate();
        // sp.revalidate();
    }

    public void clearMessages() {
        body.removeAll();
        refreshMessages();
    }
}
