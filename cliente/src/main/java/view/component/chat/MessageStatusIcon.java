package view.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import model.MessageStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class MessageStatusIcon extends JComponent {

    private MessageStatus status;
    private static final int ICON_SIZE = 18;
    private static final int TICK_SIZE = 24;

    public MessageStatusIcon(MessageStatus status) {
        this.status = status;
        setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        setOpaque(false);
        putClientProperty(FlatClientProperties.STYLE, "foreground:$Text.Foreground.secondary");
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Color iconColor = getIconColor();
        g2.setColor(iconColor);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        switch (status) {
            case PENDING:
                drawClockIcon(g2, centerX, centerY);
                break;
            case SENT:
                drawSingleTick(g2, centerX, centerY);
                break;
            case DELIVERED:
                drawDoubleTick(g2, centerX, centerY);
                break;
            case READ:
                drawDoubleTickBlue(g2, centerX, centerY);
                break;
            case FAILED:
                drawErrorIcon(g2, centerX, centerY);
                break;
            default:
                break;
        }

        g2.dispose();
    }

    private Color getIconColor() {
        return switch (status) {
            case PENDING -> UIManager.getColor("Label.disabledForeground"); // Gris
            case SENT -> UIManager.getColor("Label.disabledForeground"); // Gris
            case DELIVERED -> UIManager.getColor("Label.disabledForeground"); // Gris
            case READ -> UIManager.getColor("Actions.Yellow"); // amarillo para leído
            case FAILED -> UIManager.getColor("Actions.Red"); // ROJo
        };
    }

    private void drawClockIcon(Graphics2D g2, int centerX, int centerY) {
        int radius = TICK_SIZE / 2 - 1;

        g2.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        g2.drawLine(centerX, centerY, centerX, centerY - radius / 2);
        g2.drawLine(centerX, centerY, centerX + radius / 3, centerY + radius / 3);
    }

    private void drawSingleTick(Graphics2D g2, int centerX, int centerY) {
        Path2D tick = new Path2D.Double();

        int size = TICK_SIZE / 2;
        tick.moveTo(centerX - size / 2, centerY);
        tick.lineTo(centerX - size / 4, centerY + size / 3);
        tick.lineTo(centerX + size / 2, centerY - size / 3);

        g2.draw(tick);
    }

    private void drawDoubleTick(Graphics2D g2, int centerX, int centerY) {
        Path2D tick1 = new Path2D.Double();
        Path2D tick2 = new Path2D.Double();

        int size = TICK_SIZE / 2;
        int offset = 3;

        tick1.moveTo(centerX - size / 2 - offset, centerY);
        tick1.lineTo(centerX - size / 4 - offset, centerY + size / 3);
        tick1.lineTo(centerX + size / 4 - offset, centerY - size / 3);

        tick2.moveTo(centerX - size / 4, centerY);
        tick2.lineTo(centerX, centerY + size / 3);
        tick2.lineTo(centerX + size / 2, centerY - size / 3);

        g2.draw(tick1);
        g2.draw(tick2);
    }

    private void drawDoubleTickBlue(Graphics2D g2, int centerX, int centerY) {
        g2.setColor(getIconColor());
        drawDoubleTick(g2, centerX, centerY);
    }

    private void drawErrorIcon(Graphics2D g2, int centerX, int centerY) {
        g2.setColor(getIconColor());

        int size = TICK_SIZE / 3;

        g2.drawLine(centerX - size, centerY - size, centerX + size, centerY + size);
        g2.drawLine(centerX + size, centerY - size, centerX - size, centerY + size);
    }
}
