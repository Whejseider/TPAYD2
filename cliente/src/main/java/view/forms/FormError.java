package view.forms;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import connection.Cliente;
import net.miginfocom.swing.MigLayout;
import view.system.Form;

import javax.swing.*;

public class FormError extends Form {

    private JPanel panel;
    private JButton reconnectButton;
    private JLabel countdownLabel;
    private Timer reconnectTimer;
    private int countdownSeconds;
    private static final int INITIAL_COUNTDOWN_SECONDS = 15;

    public FormError() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center, wrap 1", "[center]", "[][]"));

        JLabel labelMessage = new JLabel("Error de conexión con el servidor");
        JLabel labelDescription = new JLabel("Perdón, algo salió mal.");
        JLabel labelHint = new JLabel("Intente verificar su conexión de red o reintente conectarse de nuevo en unos segundos.");
        labelMessage.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");

        FlatSVGIcon icon = new FlatSVGIcon("fv/icons/server_error.svg", 2f);
        add(new JLabel(icon));

        panel = new JPanel(new MigLayout("wrap 1, fillx", "[grow, center]", "[]3[]5[]10[]10[][]"));
        panel.add(labelMessage);
        panel.add(labelDescription);
        panel.add(labelHint);
        panel.add(new JSeparator(), "growx, gapy 5 5");

        countdownLabel = new JLabel(" ");
        countdownLabel.putClientProperty(FlatClientProperties.STYLE, "font:-1");
        countdownLabel.setVisible(false);
        panel.add(countdownLabel);

        add(panel);
    }

    private JButton getReconnectButton() {
        if (reconnectButton == null) {
            reconnectButton = new JButton("Reconectar ahora");
            reconnectButton.addActionListener(e -> {
                stopAutomaticReconnectTimer();
                reconnect();
            });
            reconnectButton.putClientProperty(FlatClientProperties.STYLE, "" +
                    "[light]background:darken(@background,5%);" +
                    "[dark]background:lighten(@background,5%);" +
                    "font:-1;" +
                    "arc:999;" +
                    "margin:4,10,4,10;" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;");
        }
        return reconnectButton;
    }

    private void reconnect() {
        if (countdownLabel != null) {
            countdownLabel.setText("Intentando reconectar...");
            countdownLabel.setVisible(true);
        }
        if (reconnectButton != null) {
            reconnectButton.setEnabled(false);
        }
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                Cliente.getInstance().attemptReconnect();
                return null;
            }

            @Override
            protected void done() {
                if (isVisible() && reconnectButton != null) {
                    reconnectButton.setEnabled(true);
                }
            }
        }.execute();
    }

    public void showReconnectOptions(boolean show) {
        boolean buttonNeedsAddingOrRemoving = false;

        if (show) {
            if (reconnectButton == null || reconnectButton.getParent() == null) {
                JButton btn = getReconnectButton();
                panel.add(btn, "align center, gapy 10");
                buttonNeedsAddingOrRemoving = true;
            }
            reconnectButton.setVisible(true);
            reconnectButton.setEnabled(true);
            startAutomaticReconnectTimer();
        } else {
            if (reconnectButton != null && reconnectButton.getParent() != null) {
                reconnectButton.setVisible(false);
            }
            stopAutomaticReconnectTimer();
            if (countdownLabel != null) {
                countdownLabel.setVisible(false);
            }
        }

        if(buttonNeedsAddingOrRemoving){
            panel.revalidate();
            panel.repaint();
        }
    }

    private void startAutomaticReconnectTimer() {
        stopAutomaticReconnectTimer();

        countdownSeconds = INITIAL_COUNTDOWN_SECONDS;
        updateCountdownLabel();
        countdownLabel.setVisible(true);

        reconnectTimer = new Timer(1000, e -> {
            countdownSeconds--;
            if (countdownSeconds > 0) {
                updateCountdownLabel();
            } else {
                stopAutomaticReconnectTimer();
                countdownLabel.setText("Reintentando automáticamente...");
                reconnect();
            }
        });
        reconnectTimer.setInitialDelay(1000);
        reconnectTimer.start();
    }

    public void stopAutomaticReconnectTimer() {
        if (reconnectTimer != null && reconnectTimer.isRunning()) {
            reconnectTimer.stop();
        }
    }

    private void updateCountdownLabel() {
        if (countdownLabel != null) {
            countdownLabel.setText("Reintento automático en " + countdownSeconds + " segundos...");
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (!b) {
            stopAutomaticReconnectTimer();
            if (reconnectButton != null) {
                reconnectButton.setVisible(false);
            }
            if (countdownLabel != null) {
                countdownLabel.setVisible(false);
            }
        }
    }
}