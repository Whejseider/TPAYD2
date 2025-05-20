import network.HeartbeatData;
import network.NetworkConstants;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Monitor extends JFrame {

    // gui
    private JTextPane logArea;
    private JButton startButton, stopButton, restartButton;
    private JLabel statusLabel;
    private JLabel monitorInfoLabel;
    private JLabel standbyInfoLabel;
    private JLabel activePrimaryLabel; // Para mostrar el primario activo

    // logica
    private AtomicLong lastHeartbeatTime = new AtomicLong(0);
    private volatile boolean primaryConsideredDown = false;
    private final long heartbeatTimeoutMillis = 8000; // 4 segundos

    private String standbyIp;
    private int standbyCommandPort;
    private int monitorListenPort;

    private ScheduledExecutorService scheduler;
    private ServerSocket heartbeatServerSocket;
    private Thread heartbeatListenerThread;
    private volatile boolean monitorRunning = false;

    // informacion del primario
    private String activePrimaryClientIp = null;
    private int activePrimaryClientPort = -1;
    private final Object primaryInfoLock = new Object(); // Para sincronizar el acceso

    // consultas del cliente
    private ServerSocket clientQueryServerSocket;
    private Thread clientQueryListenerThread;
    private final int clientQueryPort = NetworkConstants.PUERTO_CONSULTA_CLIENTE_A_MONITOR_DEFAULT;

    // lo mismo que el server, pasarlo a una clase compartida
    private static final Color COLOR_INFO = Color.BLACK;
    private static final Color COLOR_HEARTBEAT = new Color(0, 100, 0); // Verde oscuro
    private static final Color COLOR_WARNING = new Color(255, 140, 0); // Naranja oscuro
    private static final Color COLOR_ERROR = Color.RED;
    private static final Color COLOR_ACTION = Color.BLUE;
    private static final Color COLOR_ROGUE = new Color(128, 0, 128); // Violeta para el primario fantasma o rogue

    /**
     * El monitor lo hice antes que el servidor, asi que no tiene los txtbox de los puertos
     * @param standbyIp
     * @param standbyCommandPort
     * @param monitorListenPort
     */
    public Monitor(String standbyIp, int standbyCommandPort, int monitorListenPort) {
        this.standbyIp = standbyIp;
        this.standbyCommandPort = standbyCommandPort;
        this.monitorListenPort = monitorListenPort;

        setTitle("Sistema de Monitoreo de Servidor");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        setupActions();

        updateMonitorInfoLabel();
        updateActivePrimaryLabel();
        logMessage("Monitor listo. Presione 'Iniciar' para comenzar.", COLOR_INFO);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopMonitor();
                dispose();
                System.exit(0);
            }
        });
    }

    private void initComponents() {
        logArea = new JTextPane();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        startButton = new JButton("Iniciar Monitor");
        stopButton = new JButton("Detener Monitor");
        stopButton.setEnabled(false);
        restartButton = new JButton("Reiniciar Monitor");
        restartButton.setEnabled(false);

        statusLabel = new JLabel("Estado: Detenido");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monitorInfoLabel = new JLabel();
        standbyInfoLabel = new JLabel("Secundario: " + standbyIp + ":" + standbyCommandPort);
        activePrimaryLabel = new JLabel("Primario Activo: Ninguno"); // Inicialmente ninguno
        activePrimaryLabel.setFont(new Font("Arial", Font.ITALIC, 12));
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(monitorInfoLabel);
        topPanel.add(standbyInfoLabel);
        topPanel.add(activePrimaryLabel);
        topPanel.add(statusLabel);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(restartButton);

        JScrollPane scrollPane = new JScrollPane(logArea);

        setLayout(new BorderLayout(10,10));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateMonitorInfoLabel() {
        try {
            String ip = NetworkConstants.IP_MONITOR_DEFAULT;
            monitorInfoLabel.setText("Monitor escuchando en: " + ip + ":" + monitorListenPort);
        } catch (Exception e) {
            monitorInfoLabel.setText("Monitor escuchando en: Desconocido:" + monitorListenPort);
            logMessage("Error obteniendo IP local: " + e.getMessage(), COLOR_ERROR);
        }
    }

    private void updateActivePrimaryLabel() {
        synchronized (primaryInfoLock) {
            if (activePrimaryClientIp != null && activePrimaryClientPort != -1) {
                activePrimaryLabel.setText("Primario Activo: " + activePrimaryClientIp + ":" + activePrimaryClientPort);
                activePrimaryLabel.setForeground(COLOR_HEARTBEAT);
            } else {
                activePrimaryLabel.setText("Primario Activo: Ninguno Detectado");
                activePrimaryLabel.setForeground(COLOR_ROGUE);
            }
        }
    }

    private void setupActions() {
        startButton.addActionListener(e -> startMonitor());
        stopButton.addActionListener(e -> stopMonitor());
        restartButton.addActionListener(e -> {
            stopMonitor();
            new Timer(1000, ae -> startMonitor()) {{ setRepeats(false); start(); }}.start();
        });
    }

    private void logMessage(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyleContext sc = StyleContext.getDefaultStyleContext();
                AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
                aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Monospaced");
                aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

                String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                int len = logArea.getDocument().getLength();
                logArea.getDocument().insertString(len, "[" + timeStamp + "] " + message + "\n", aset);
                logArea.setCaretPosition(len);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private synchronized void startMonitor() {
        if (monitorRunning) {
            logMessage("El monitor ya está en ejecución.", COLOR_WARNING);
            return;
        }
        logMessage("Iniciando monitor...", COLOR_ACTION);

        primaryConsideredDown = false;
        lastHeartbeatTime.set(System.currentTimeMillis());
        synchronized(primaryInfoLock) {
            activePrimaryClientIp = null;
            activePrimaryClientPort = -1;
        }
        updateActivePrimaryLabel();

        try {
            scheduler = Executors.newScheduledThreadPool(1);

            heartbeatServerSocket = new ServerSocket(monitorListenPort);
            monitorRunning = true;

            heartbeatListenerThread = new Thread(this::listenForHeartbeats);
            heartbeatListenerThread.start();

            clientQueryListenerThread = new Thread(this::startClientQueryListenerLoop);
            clientQueryListenerThread.start();

            scheduler.scheduleAtFixedRate(this::checkPrimaryStatusAndPromote,
                    heartbeatTimeoutMillis, heartbeatTimeoutMillis / 2, TimeUnit.MILLISECONDS);

            statusLabel.setText("Estado: Corriendo");
            statusLabel.setForeground(new Color(0,128,0)); // Verde
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            restartButton.setEnabled(true);
            logMessage("Monitor iniciado. Escuchando heartbeats en el puerto: " + monitorListenPort, COLOR_INFO);
            logMessage("Escuchando consultas de clientes en el puerto: " + clientQueryPort, COLOR_INFO);
            logMessage("Configurado para comandar al Secundario en: " + standbyIp + ":" + standbyCommandPort, COLOR_INFO);

        } catch (IOException e) {
            logMessage("Error al iniciar el monitor (socket de heartbeat): " + e.getMessage(), COLOR_ERROR);
            monitorRunning = false;
            cleanupMonitorResources();
        }
    }

    private synchronized void stopMonitor() {
        if (!monitorRunning) {
            logMessage("El monitor ya está detenido.", COLOR_WARNING);
            return;
        }
        logMessage("Deteniendo monitor...", COLOR_ACTION);
        monitorRunning = false;

        if (heartbeatListenerThread != null && heartbeatListenerThread.isAlive()) {
            heartbeatListenerThread.interrupt();
        }
        if (clientQueryListenerThread != null && clientQueryListenerThread.isAlive()) {
            clientQueryListenerThread.interrupt();
        }

        cleanupMonitorResources();

        statusLabel.setText("Estado: Detenido");
        statusLabel.setForeground(Color.RED);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        restartButton.setEnabled(false);
        logMessage("Monitor detenido.", COLOR_INFO);
    }

    private void cleanupMonitorResources() {
        try {
            if (heartbeatServerSocket != null && !heartbeatServerSocket.isClosed()) {
                heartbeatServerSocket.close();
            }
            if (clientQueryServerSocket != null && !clientQueryServerSocket.isClosed()) {
                clientQueryServerSocket.close();
            }
        } catch (IOException e) {
            logMessage("Error al cerrar sockets del monitor: " + e.getMessage(), COLOR_WARNING);
        } finally {
            heartbeatServerSocket = null;
            clientQueryServerSocket = null;
        }

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) { // Reducido tiempo
                    logMessage("Scheduler no terminó a tiempo.", COLOR_WARNING);
                }
            } catch (InterruptedException e) {
                logMessage("Interrupción mientras se esperaba la terminación del scheduler.", COLOR_WARNING);
                Thread.currentThread().interrupt();
            }
        }
        scheduler = null;
    }

    private void listenForHeartbeats() {
        logMessage("Hilo de escucha de heartbeats iniciado.", COLOR_INFO);
        while (monitorRunning && heartbeatServerSocket != null && !heartbeatServerSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
            try (Socket primaryConnectionSocket  = heartbeatServerSocket.accept();
                 ObjectInputStream ois = new ObjectInputStream(primaryConnectionSocket.getInputStream())) {

                if (!monitorRunning) break;

                Object received = ois.readObject();
                if (received instanceof HeartbeatData heartbeat) {
                    if (heartbeat.getType().equalsIgnoreCase("HEARTBEAT_PRIMARY")) {
                        String hbIp = heartbeat.primaryClientIp;
                        int hbPort = heartbeat.primaryClientPort;

                        synchronized (primaryInfoLock) {

                            if (activePrimaryClientIp == null ||
                                    (activePrimaryClientIp.equals(hbIp) && activePrimaryClientPort == hbPort)) {

                                if (activePrimaryClientIp == null) {
                                    logMessage("NUEVO PRIMARIO detectado (o recuperado): " + hbIp + ":" + hbPort, COLOR_HEARTBEAT);
                                    activePrimaryClientIp = hbIp;
                                    activePrimaryClientPort = hbPort;
                                } else {
                                    logMessage("Heartbeat recibido del Primario (" + hbIp + ":" + hbPort + ").", COLOR_HEARTBEAT);
                                }

                                lastHeartbeatTime.set(System.currentTimeMillis());
                                if (primaryConsideredDown) {
                                    logMessage("Primario (" + activePrimaryClientIp + ":" + activePrimaryClientPort +
                                            ") VUELVE A ESTAR ACTIVO.", COLOR_WARNING);
                                }
                                primaryConsideredDown = false; // El primario (nuevo o el mismo) está activo.
                                SwingUtilities.invokeLater(this::updateActivePrimaryLabel);

                            }

                            else if (!activePrimaryClientIp.equals(hbIp) || activePrimaryClientPort != hbPort) {
                                //O descomentar o ponerle un limite, maximo no se 10 cada 1 minuto, es molesto, se mandan demasiados
                                logMessage("HEARTBEAT FANTASMA: Recibido de " + hbIp + ":" + hbPort +
                                        ", pero el primario activo es " + activePrimaryClientIp + ":" + activePrimaryClientPort + ". IGNORANDO.", COLOR_ROGUE);
                            }
                        }
                    }
                }
            } catch (java.net.SocketException se) {
                if (monitorRunning) {
                    logMessage("SocketException en listener de heartbeats: " + se.getMessage(), COLOR_WARNING);
                }
                break;
            }
            catch (IOException | ClassNotFoundException e) {
                if (monitorRunning) {
                    logMessage("Error al recibir/procesar heartbeat: " + e.getMessage(), COLOR_ERROR);
                }
            }
        }
        logMessage("Hilo de escucha de heartbeats terminado.", COLOR_INFO);
    }

    private void checkPrimaryStatusAndPromote() {
        if (!monitorRunning) return;


        synchronized (primaryInfoLock) {
            if (primaryConsideredDown) {
                // Ya se detecto una caida y se esta (o se intento promover) promoviendo.
                // No hacer nada mas hasta que un nuevo primario se establezca
                // y sea validado en listenForHeartbeats.
                return;
            }

            if (activePrimaryClientIp == null) {
                if (System.currentTimeMillis() - lastHeartbeatTime.get() > heartbeatTimeoutMillis * 2 && monitorRunning) {
                    logMessage("Monitor activo pero ningún primario ha enviado heartbeats por un tiempo.", COLOR_INFO);
                }
                return;
            }
        }


        long timeSinceLastHeartbeat = System.currentTimeMillis() - lastHeartbeatTime.get();
        if (timeSinceLastHeartbeat > heartbeatTimeoutMillis) {
            synchronized (primaryInfoLock) {

                if (primaryConsideredDown) return;

                logMessage("¡TIMEOUT! No se ha recibido heartbeat del Primario (" + activePrimaryClientIp + ":" + activePrimaryClientPort +
                        ") en " + timeSinceLastHeartbeat + "ms.", COLOR_ERROR);
                logMessage("Considerando PRIMARIO (" + activePrimaryClientIp + ":" + activePrimaryClientPort + ") CAÍDO. Intentando promover Secundario...", COLOR_ERROR);

                activePrimaryClientIp = null;
                activePrimaryClientPort = -1;
                primaryConsideredDown = true;
                SwingUtilities.invokeLater(this::updateActivePrimaryLabel);
            }
            promoteStandby();
        }
    }

    private void promoteStandby() {
        if (!monitorRunning) return;


        logMessage("Intentando promover el servidor Secundario en " + standbyIp + ":" + standbyCommandPort, COLOR_ACTION);
        try (Socket socket = new Socket(standbyIp, standbyCommandPort);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.writeObject("PROMOVER_A_PRIMARIO");
            oos.flush();
            logMessage("Comando PROMOVER_A_PRIMARIO enviado al Secundario.", COLOR_ACTION);
        } catch (IOException e) {
            logMessage("Error al enviar comando de promoción al Secundario: " + e.getMessage(), COLOR_ERROR);
            logMessage("Promoción fallida. El sistema puede estar sin primario. Se esperará a que un servidor envíe heartbeats.", COLOR_ERROR);
        }
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String argStandbyIp = "127.0.0.1";
        int argStandbyPort = NetworkConstants.PUERTO_COMANDO_MONITOR_A_STANDBY_DEFAULT;
        int argMonitorPort = NetworkConstants.PUERTO_HEARTBEAT_A_MONITOR_DEFAULT;

        if (args.length >= 1) {
            argStandbyIp = args[0];
        }
        if (args.length >= 2) {
            try {
                argStandbyPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Puerto del Secundario inválido: " + args[1] + ". Usando default: " + argStandbyPort);
            }
        }
        if (args.length >= 3) {
            try {
                argMonitorPort = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Puerto de escucha del Monitor inválido: " + args[2] + ". Usando default: " + argMonitorPort);
            }
        }

        final String finalStandbyIp = argStandbyIp;
        final int finalStandbyPort = argStandbyPort;
        final int finalMonitorPort = argMonitorPort;

        SwingUtilities.invokeLater(() -> {
            Monitor gui = new Monitor(finalStandbyIp, finalStandbyPort, finalMonitorPort);
            gui.setVisible(true);
        });
    }

    private void startClientQueryListenerLoop() {
        logMessage("Hilo de escucha de consultas de clientes iniciado en puerto: " + clientQueryPort, COLOR_INFO);
        try {

            clientQueryServerSocket = new ServerSocket(clientQueryPort);

            while (monitorRunning && clientQueryServerSocket != null && !clientQueryServerSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
                try (Socket clientSocket = clientQueryServerSocket.accept();
                     ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                     ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())) {

                    if (!monitorRunning) break;

                    Object request = ois.readObject();
                    if ("OBTENER_INFO_PRIMARIO".equals(request)) {
                        String currentPrimaryIpReply;
                        int currentPrimaryPortReply;

                        synchronized (primaryInfoLock) {
                            currentPrimaryIpReply = this.activePrimaryClientIp;
                            currentPrimaryPortReply = this.activePrimaryClientPort;
                        }

                        if (currentPrimaryIpReply != null && currentPrimaryPortReply != -1) {
                            HeartbeatData infoToSend = new HeartbeatData(currentPrimaryIpReply, currentPrimaryPortReply);
                            oos.writeObject(infoToSend);
                            logMessage("Enviada info del primario (" + currentPrimaryIpReply + ":" + currentPrimaryPortReply +
                                    ") a cliente " + clientSocket.getRemoteSocketAddress(), COLOR_ACTION);
                        } else {
                            HeartbeatData noPrimaryInfo = new HeartbeatData("DESCONOCIDO_NINGUNO", -1);
                            oos.writeObject(noPrimaryInfo);
                            logMessage("Informado a cliente " + clientSocket.getRemoteSocketAddress() +
                                    " que no hay primario activo.", COLOR_WARNING);
                        }
                        oos.flush();
                    } else if ("PUEDO_SER_PRIMARIO?".equals(request)) {
                        String response;
                        synchronized (primaryInfoLock) {
                            if (activePrimaryClientIp == null) {
                                response = "OK_SER_PRIMARIO";
                                logMessage("Respondiendo OK_SER_PRIMARIO a " + clientSocket.getRemoteSocketAddress() +
                                        " (Servidor consultando)", COLOR_ACTION);
                            } else {
                                response = "PRIMARIO_YA_EXISTE:" + activePrimaryClientIp + ":" + activePrimaryClientPort;
                                logMessage("Respondiendo PRIMARIO_YA_EXISTE a " + clientSocket.getRemoteSocketAddress() +
                                        " (Servidor consultando). Actual: " + activePrimaryClientIp + ":" + activePrimaryClientPort, COLOR_WARNING);
                            }
                        }
                        oos.writeObject(response);
                        oos.flush();
                    }
                    else {
                        logMessage("Recibida consulta desconocida de cliente " + clientSocket.getRemoteSocketAddress() + ": " + request, COLOR_WARNING);
                    }

                } catch (java.net.SocketException se) {
                    if (monitorRunning) {
                        logMessage("SocketException en listener de consultas de clientes (normal al detener/interrumpir): " + se.getMessage(), COLOR_WARNING);
                    }
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    if (monitorRunning) {
                        logMessage("Error manejando consulta de cliente: " + e.getMessage(), COLOR_ERROR);
                    }
                }
            }
        } catch (IOException e) {
            if (monitorRunning) {
                logMessage("Error CRÍTICO al iniciar listener de consultas de clientes en puerto " + clientQueryPort + ": " + e.getMessage(), COLOR_ERROR);
            }
        } finally {

            if (clientQueryServerSocket != null && !clientQueryServerSocket.isClosed()) {
                try { clientQueryServerSocket.close(); } catch (IOException e) {  }
            }
            logMessage("Hilo de escucha de consultas de clientes terminado.", COLOR_INFO);
        }
    }
}