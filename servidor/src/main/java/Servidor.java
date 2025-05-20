import model.Directorio;
import model.Mensaje;
import network.HeartbeatData;
import network.NetworkConstants;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Servidor extends JFrame {

    // GUI
    private JTextPane logArea;
    private JButton startButton, stopButton;
    private JLabel statusLabel, roleLabel, clientConnectionsLabel, directoryLabel;
    private JRadioButton primaryRadioButton, secondaryRadioButton;
    private ButtonGroup roleButtonGroup;
    private JTextField clientPortField, internalPortField, peerIpField, peerPortField;
    private JPanel configPanel;

    // Server
    private ServerSocket clientServerSocket;
    private ServerSocket internalServerSocket;
    private ServerRole currentRole;
    private String currentPeerIp;
    private int currentPeerReplicationPort;
    private int currentClientPort;
    private int currentInternalPort;
    private final int CHECK_INTERVAL_MS = 5000;
    // Heartneats
    private final int INIT_DELAY_HEARTBEATS_SECONDS = 0;
    private final int SEND_HEARTBEATS_SECONDS = 4;
    // Replication, lo tengo que cambiar a futuro, para que cuando el secundario se conecte, le replique el estado
    // Y luego solamente se hace el force en cambios criticos del servidor
    // No me dio el tiempo :D
    private final int INIT_DELAY_REPLICATION_SECONDS = 6;
    private final int SEND_REPLICATION_SECONDS = 12;

    private final AtomicBoolean promotedToPrimary = new AtomicBoolean(false);
    private ScheduledExecutorService scheduler;
    private volatile boolean serverRunning = false;
    private Thread clientAcceptThread;
    private Thread internalCommsThread;

    // Compartido entre servidores
    public static Directorio directorio = new Directorio();
    public static Map<String, List<Mensaje>> mensajesPendientes = new HashMap<>();
    public static ArrayList<ClientHandler> clientesConectados = new ArrayList<>();
    public static Servidor instanciaServidorActivo;

    // Colores para el log, mover esto a una clase aparte, igual que el monitor
    public static final Color COLOR_INFO = Color.BLACK;
    public static final Color COLOR_CLIENT = new Color(0, 0, 139); // Azul oscuro
    public static final Color COLOR_REPLICATION = new Color(0, 100, 0); // Verde oscuro
    public static final Color COLOR_PROMOTION = new Color(139, 0, 139); // Violeta oscuro
    public static final Color COLOR_ERROR = Color.RED;
    public static final Color COLOR_WARNING = new Color(255, 140, 0);

    //A Futuro pasarlo quizas a MVC
    // Pero no creo que haga falta
    public Servidor() {
        setTitle("Panel de Control del Servidor de Mensajería");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        setupActions();

        logMessage("Servidor listo. Configure y presione 'Iniciar'.", COLOR_INFO);
        updateStatusLabel();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopServer();
                dispose();
                System.exit(0);
            }
        });
    }

    private void initComponents() {
        logArea = new JTextPane();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        startButton = new JButton("Iniciar Servidor");
        stopButton = new JButton("Detener Servidor");
        stopButton.setEnabled(false);

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel = new JLabel("Rol: No definido");
        directoryLabel = new JLabel("Usuarios registrados: 0");
//        clientConnectionsLabel = new JLabel("Clientes conectados: 0");

        primaryRadioButton = new JRadioButton("Primario", true);
        secondaryRadioButton = new JRadioButton("Secundario");
        roleButtonGroup = new ButtonGroup();
        roleButtonGroup.add(primaryRadioButton);
        roleButtonGroup.add(secondaryRadioButton);

        clientPortField = new JTextField(Integer.toString(NetworkConstants.PUERTO_CLIENTES_DEFAULT), 5);
        internalPortField = new JTextField(Integer.toString(NetworkConstants.PUERTO_REPLICACION_DEFAULT), 5); // Usado por Secundario para escuchar
        peerIpField = new JTextField(NetworkConstants.IP_DEFAULT, 10); // IP del otro servidor
        peerPortField = new JTextField(Integer.toString(NetworkConstants.PUERTO_REPLICACION_DEFAULT), 5); // Puerto de replicación del otro servidor

        configPanel = new JPanel(new GridBagLayout());
    }

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        configPanel.add(new JLabel("Rol del Servidor:"), gbc);
        gbc.gridx = 1;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.add(primaryRadioButton);
        radioPanel.add(secondaryRadioButton);
        configPanel.add(radioPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        configPanel.add(new JLabel("Puerto para Primario:"), gbc);
        gbc.gridx = 1;
        configPanel.add(clientPortField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        configPanel.add(new JLabel("Puerto Interno (Secundario):"), gbc);
        gbc.gridx = 1;
        configPanel.add(internalPortField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        configPanel.add(new JLabel("IP del servidor secundario:"), gbc);
        gbc.gridx = 1;
        configPanel.add(peerIpField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        configPanel.add(new JLabel("Puerto Replicación:"), gbc);
        gbc.gridx = 1;
        configPanel.add(peerPortField, gbc);


        // Panel de Estado
        JPanel statusDisplayPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statusDisplayPanel.add(statusLabel);
        statusDisplayPanel.add(roleLabel);
//        statusDisplayPanel.add(clientConnectionsLabel);
        statusDisplayPanel.add(directoryLabel);

        // Panel Superior (Config + Estado)
        JPanel topOuterPanel = new JPanel(new BorderLayout(10, 10));
        topOuterPanel.add(configPanel, BorderLayout.CENTER);
        topOuterPanel.add(statusDisplayPanel, BorderLayout.EAST);
        topOuterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        // Panel de Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        JScrollPane scrollPane = new JScrollPane(logArea);

        setLayout(new BorderLayout(10, 10));
        add(topOuterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateStatusLabel() {
        if (serverRunning) {
            statusLabel.setText("Estado: Corriendo");
            statusLabel.setForeground(new Color(0, 128, 0)); // Verde
        } else {
            statusLabel.setText("Estado: Detenido");
            statusLabel.setForeground(Color.RED);
        }
        roleLabel.setText("Rol Actual: " + (currentRole != null ? currentRole.toString() : "No iniciado"));
        synchronized (Servidor.class) {
//            clientConnectionsLabel.setText("Clientes conectados: " + clientesConectados.size());
            directoryLabel.setText("Usuarios registrados: " + Servidor.directorio.getDirectorio().size());
        }
    }

    private void setConfigFieldsEnabled(boolean enabled) {
        primaryRadioButton.setEnabled(enabled);
        secondaryRadioButton.setEnabled(enabled);
        clientPortField.setEnabled(enabled);
        internalPortField.setEnabled(enabled);
        peerIpField.setEnabled(enabled);
        peerPortField.setEnabled(enabled);
    }

    private void setupActions() {
        startButton.addActionListener(this::startServerAction);
        stopButton.addActionListener(e -> stopServer());

        primaryRadioButton.addActionListener(e -> {
            peerIpField.setEnabled(true);
            peerPortField.setEnabled(true);
            internalPortField.setEnabled(false);
            clientPortField.setEnabled(true);
        });
        secondaryRadioButton.addActionListener(e -> {
            peerIpField.setEnabled(false);
            peerPortField.setEnabled(false);
            internalPortField.setEnabled(true);
            clientPortField.setEnabled(true);
        });

        peerIpField.setEnabled(primaryRadioButton.isSelected());
        peerPortField.setEnabled(primaryRadioButton.isSelected());
        internalPortField.setEnabled(secondaryRadioButton.isSelected());
        clientPortField.setEnabled(primaryRadioButton.isSelected());
    }

    // Podria usar en otra clase, para monitor y servidor
    public void logMessage(String message, Color color) {
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

    // Logica

    private boolean checkPort(int puerto) {
        try (ServerSocket socket = new ServerSocket(puerto)) {
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean checkNetworkPorts(int puerto) {
        return puerto != NetworkConstants.PUERTO_REPLICACION_DEFAULT &&
                puerto != NetworkConstants.PUERTO_HEARTBEAT_A_MONITOR_DEFAULT &&
                puerto != NetworkConstants.PUERTO_CONSULTA_CLIENTE_A_MONITOR_DEFAULT;
    }

    /**
     * Si no está el monitor funcionando, no se puede iniciar el servidor<br>
     * El monitor como conoce los servidores maneja los roles
     *
     * @param e
     */
    private void startServerAction(ActionEvent e) {
        String errorMsg;

        if (serverRunning) {
            logMessage("El servidor ya está en ejecución.", COLOR_WARNING);
            return;
        }

        ServerRole selectedRole = primaryRadioButton.isSelected() ? ServerRole.PRIMARIO : ServerRole.SECUNDARIO;
        int clientPortToUse;
        int internalPortToUse; // Para el secundario
        String peerIpToUse; // Para el primario, la IP del secundario
        int peerReplicationPortToUse; // Para el primario, el puerto de replicacion del secundario

        try {
            clientPortToUse = Integer.parseInt(clientPortField.getText());
            internalPortToUse = Integer.parseInt(internalPortField.getText());
            peerIpToUse = peerIpField.getText();
            peerReplicationPortToUse = Integer.parseInt(peerPortField.getText());

            if (selectedRole == ServerRole.PRIMARIO && (peerIpToUse.isEmpty())) {
                JOptionPane.showMessageDialog(this, "Para el rol Primario, la IP del Secundario no puede estar vacía.", "Error de Configuración", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (NumberFormatException nfe) {
            logMessage("Error: Puerto inválido. Ingrese un número.", COLOR_ERROR);
            JOptionPane.showMessageDialog(this, "Error en el formato de los puertos. Deben ser números.", "Error de Configuración", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Verifico que no esten en uso los puertos
        if (!checkNetworkPorts(clientPortToUse)) {
            errorMsg = "Error: El puerto (" + clientPortToUse + ") es parte de otro servicio del servidor." +
                    "\nIntenta cambiar el puerto de Puerto Clientes (Primario) por alguno que no esté en uso y que sea diferente." +
                    "\nPuertos no disponibles: " + NetworkConstants.PUERTO_REPLICACION_DEFAULT + " - " + NetworkConstants.PUERTO_HEARTBEAT_A_MONITOR_DEFAULT + " - " + NetworkConstants.PUERTO_CONSULTA_CLIENTE_A_MONITOR_DEFAULT;
            logMessage("Error al iniciar el servidor. El puerto (" + clientPortToUse + ") declarado en Puerto Clientes (Primario) es parte de otro servicio del servidor.", COLOR_ERROR);
            JOptionPane.showMessageDialog(this, errorMsg, "Error de inicialización del servidor.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!checkPort(clientPortToUse)) {
            errorMsg = "Error: El puerto (" + clientPortToUse + ") se encuentra en uso.\nIntenta cambiar el puerto de Puerto Clientes (Primario) por alguno que no esté en uso";
            logMessage("Error al iniciar el servidor. El puerto (" + clientPortToUse + ") declarado en Puerto Clientes (Primario) ya está en uso.", COLOR_ERROR);
            JOptionPane.showMessageDialog(this, errorMsg, "Error de inicialización del servidor.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // inicio verificación con el monitor
        if (selectedRole == ServerRole.PRIMARIO) {
            logMessage("Verificando puertos", COLOR_INFO);
            logMessage("Intentando iniciar como PRIMARIO. Verificando con el Monitor...", COLOR_INFO);
            try (Socket monitorSocket = new Socket(NetworkConstants.IP_MONITOR_DEFAULT, NetworkConstants.PUERTO_CONSULTA_CLIENTE_A_MONITOR_DEFAULT);
                 ObjectOutputStream oosMonitor = new ObjectOutputStream(monitorSocket.getOutputStream());
                 ObjectInputStream oisMonitor = new ObjectInputStream(monitorSocket.getInputStream())) {

                monitorSocket.setSoTimeout(CHECK_INTERVAL_MS);

                oosMonitor.writeObject("PUEDO_SER_PRIMARIO?");
                oosMonitor.flush();

                Object responseObj = oisMonitor.readObject();
                if (responseObj instanceof String response) {
                    if (response.startsWith("PRIMARIO_YA_EXISTE:")) {
                        String infoExistePrimario = response.substring("PRIMARIO_YA_EXISTE:".length());
                        errorMsg = "No se puede iniciar como Primario.\nEl Monitor informa que ya existe un Primario activo en: " + infoExistePrimario +
                                "\n\nPor favor, inicie como Secundario o verifique la configuración.";
                        logMessage("Monitor informó: Ya existe un primario activo en " + infoExistePrimario, COLOR_ERROR);
                        JOptionPane.showMessageDialog(this, errorMsg, "Error de Inicio - Primario Existente", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (response.equals("OK_SER_PRIMARIO")) {
                        logMessage("Monitor confirmó: OK para iniciar como Primario.", COLOR_INFO);
                    } else {
                        errorMsg = "Respuesta inesperada del Monitor: " + response + "\nNo se puede iniciar como Primario.";
                        logMessage(errorMsg, COLOR_ERROR);
                        JOptionPane.showMessageDialog(this, errorMsg, "Error de Comunicación con Monitor", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    errorMsg = "Respuesta inesperada (formato incorrecto) del Monitor.\nNo se puede iniciar como Primario.";
                    logMessage("Respuesta inesperada (no String) del Monitor.", COLOR_ERROR);
                    JOptionPane.showMessageDialog(this, errorMsg, "Error de Comunicación con Monitor", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            } catch (IOException | ClassNotFoundException ex) {
                errorMsg = "No se pudo contactar o comunicar con el Monitor para verificar el estado del Primario.\n" +
                        "Error: " + ex.getMessage() +
                        "\n\nNo se puede iniciar como Primario sin la confirmación del Monitor.";
                logMessage("Error al consultar al Monitor: " + ex.getMessage(), COLOR_ERROR);
                JOptionPane.showMessageDialog(this, errorMsg, "Error de Comunicación con Monitor", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        // fin verificación monitor

        currentRole = selectedRole;
        currentClientPort = clientPortToUse;
        currentInternalPort = internalPortToUse;
        currentPeerIp = peerIpToUse;
        currentPeerReplicationPort = peerReplicationPortToUse;

        resetServerState();
        Servidor.instanciaServidorActivo = this;

        logMessage("Iniciando servidor como " + currentRole + "...", COLOR_INFO);

        try {
            if (currentRole == ServerRole.PRIMARIO) {
                clientServerSocket = new ServerSocket(currentClientPort, 50, InetAddress.getByName(NetworkConstants.IP_DEFAULT));
                logMessage("Servidor PRIMARIO escuchando clientes en: " + NetworkConstants.IP_DEFAULT + ":" + currentClientPort, COLOR_INFO);
                scheduler = Executors.newScheduledThreadPool(2);
                startHeartbeatToMonitor();
                startPeriodicStateReplication(); //Lo reactive porque no pude hacer todavia lo de resincronizar
                //al iniciar el secundario
                clientAcceptThread = new Thread(this::acceptClientConnectionsLoop);
                clientAcceptThread.start();
            } else {
                internalServerSocket = new ServerSocket(currentInternalPort, 50, InetAddress.getByName(NetworkConstants.IP_DEFAULT));
                logMessage("Servidor SECUNDARIO escuchando internamente en: " + NetworkConstants.IP_DEFAULT + ":" + currentInternalPort, COLOR_INFO);
                scheduler = Executors.newScheduledThreadPool(1);
                internalCommsThread = new Thread(this::listenForInternalCommsLoop);
                internalCommsThread.start();
            }

            serverRunning = true;
            promotedToPrimary.set(currentRole == ServerRole.PRIMARIO);
            setConfigFieldsEnabled(false);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            updateStatusLabel();

        } catch (IOException ex) {
            logMessage("Error al iniciar servidor (socket principal): " + ex.getMessage(), COLOR_ERROR);
            JOptionPane.showMessageDialog(this, "Error al iniciar servidor (socket principal): " + ex.getMessage(), "Error de Inicio", JOptionPane.ERROR_MESSAGE);
            serverRunning = false;
            cleanupSocketsAndScheduler();

            setConfigFieldsEnabled(true);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            currentRole = null;
            Servidor.instanciaServidorActivo = null;
            updateStatusLabel();
        }
    }

    private void resetServerState() {

        synchronized (Servidor.class) {
            directorio = new Directorio();
            mensajesPendientes = new HashMap<>();
            clientesConectados = new ArrayList<>();
        }
        promotedToPrimary.set(false);
    }


    private synchronized void stopServer() {
        if (!serverRunning) {
            logMessage("El servidor ya está detenido.", COLOR_WARNING);
            return;
        }
        logMessage("Deteniendo servidor...", COLOR_INFO);
        serverRunning = false;
        Servidor.instanciaServidorActivo = null;


        if (clientAcceptThread != null && clientAcceptThread.isAlive()) clientAcceptThread.interrupt();
        if (internalCommsThread != null && internalCommsThread.isAlive()) internalCommsThread.interrupt();

        cleanupSocketsAndScheduler();

        setConfigFieldsEnabled(true);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        currentRole = null;
        updateStatusLabel();
        logMessage("Servidor detenido.", COLOR_INFO);
    }

    private void cleanupSocketsAndScheduler() {

        try {
            if (clientServerSocket != null && !clientServerSocket.isClosed()) clientServerSocket.close();
            if (internalServerSocket != null && !internalServerSocket.isClosed()) internalServerSocket.close();
        } catch (IOException e) {
            logMessage("Advertencia al cerrar sockets: " + e.getMessage(), COLOR_WARNING);
        } finally {
            clientServerSocket = null;
            internalServerSocket = null;
        }


        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            try {
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    logMessage("Scheduler no terminó a tiempo.", COLOR_WARNING);
                }
            } catch (InterruptedException e) {
                logMessage("Interrupción esperando al scheduler.", COLOR_WARNING);
                Thread.currentThread().interrupt();
            }
        }
        scheduler = null;
    }

    private void acceptClientConnectionsLoop() {
        logMessage("PRIMARIO: Aceptando conexiones de clientes...", COLOR_CLIENT);
        while (serverRunning && currentRole == ServerRole.PRIMARIO && clientServerSocket != null && !clientServerSocket.isClosed()) {
            try {
                Socket socket = clientServerSocket.accept();
                if (!serverRunning) break;
                logMessage("PRIMARIO: Nuevo cliente conectado: " + socket.getRemoteSocketAddress(), COLOR_CLIENT);
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
                SwingUtilities.invokeLater(this::updateStatusLabel);
            } catch (java.net.SocketException se) {
                if (serverRunning)
                    logMessage("PRIMARIO: SocketException aceptando clientes (normal al detener): " + se.getMessage(), COLOR_WARNING);
                break;
            } catch (IOException e) {
                if (serverRunning)
                    logMessage("PRIMARIO: IOException aceptando clientes: " + e.getMessage(), COLOR_ERROR);
            }
        }
        logMessage("PRIMARIO: Hilo de aceptación de clientes terminado.", COLOR_INFO);
    }

    private void listenForInternalCommsLoop() {
        logMessage("SECUNDARIO: Escuchando en puerto interno...", COLOR_REPLICATION);
        while (serverRunning && currentRole == ServerRole.SECUNDARIO && !promotedToPrimary.get() && internalServerSocket != null && !internalServerSocket.isClosed()) {
            try (Socket internalClientSocket = internalServerSocket.accept();
                 ObjectInputStream ois = new ObjectInputStream(internalClientSocket.getInputStream())) {
                if (!serverRunning) break;

                logMessage("SECUNDARIO: Conexión interna recibida de: " + internalClientSocket.getRemoteSocketAddress(), COLOR_REPLICATION);
                Object receivedObject = ois.readObject();

                if (receivedObject instanceof String command) {
                    if ("PROMOVER_A_PRIMARIO".equals(command)) {
                        logMessage("SECUNDARIO: Recibido comando PROMOVER_A_PRIMARIO del Monitor.", COLOR_PROMOTION);
                        handlePromoteToPrimary();
                        break;
                    }
                } else if (receivedObject instanceof StateData state) {
                    synchronized (Servidor.class) {
                        Servidor.directorio = state.getDirectorio();
                        Servidor.mensajesPendientes = state.getMensajesPendientes();
                    }
                    logMessage("SECUNDARIO: Estado replicado recibido y actualizado.", COLOR_REPLICATION);
                }
            } catch (java.net.SocketException se) {
                if (serverRunning)
                    logMessage("SECUNDARIO: SocketException en escucha interna (normal al detener): " + se.getMessage(), COLOR_WARNING);
                break;
            } catch (IOException | ClassNotFoundException e) {
                if (serverRunning)
                    logMessage("SECUNDARIO: Error en comunicación interna: " + e.getMessage(), COLOR_ERROR);
            }
        }
        if (promotedToPrimary.get() && currentRole == ServerRole.PRIMARIO) {
            logMessage("SECUNDARIO (AHORA PRIMARIO): Dejando de escuchar comandos internos.", COLOR_PROMOTION);
        } else {
            logMessage("SECUNDARIO: Hilo de escucha interna terminado.", COLOR_INFO);
        }
    }

    private void handlePromoteToPrimary() {
        if (!serverRunning || currentRole != ServerRole.SECUNDARIO || promotedToPrimary.get()) {
            return;
        }
        logMessage("SECUNDARIO: Promoviéndome a PRIMARIO...", COLOR_PROMOTION);

        currentRole = ServerRole.PRIMARIO;
        promotedToPrimary.set(true);
        primaryRadioButton.setSelected(true);
        Servidor.instanciaServidorActivo = this;

        try {
            if (internalCommsThread != null && internalCommsThread.isAlive())
                internalCommsThread.interrupt();
            if (internalServerSocket != null && !internalServerSocket.isClosed()) internalServerSocket.close();
        } catch (IOException e) {
            logMessage("Advertencia: Error al cerrar socket interno durante promoción: " + e.getMessage(), COLOR_WARNING);
        } finally {
            internalServerSocket = null;
            internalCommsThread = null;
        }


        try {
            clientServerSocket = new ServerSocket(currentClientPort, 50, InetAddress.getByName(NetworkConstants.IP_DEFAULT));
            logMessage("NUEVO PRIMARIO: Escuchando clientes en: " + NetworkConstants.IP_DEFAULT + ":" + currentClientPort, COLOR_PROMOTION);

            synchronized (Servidor.class) {
                Servidor.clientesConectados.clear();
            }
            SwingUtilities.invokeLater(this::updateStatusLabel);


            if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdownNow();
            scheduler = Executors.newScheduledThreadPool(2);

            startHeartbeatToMonitor();
            startPeriodicStateReplication();
            clientAcceptThread = new Thread(this::acceptClientConnectionsLoop);
            clientAcceptThread.start();
            logMessage("NUEVO PRIMARIO: Listo para aceptar clientes.", COLOR_PROMOTION);

        } catch (IOException e) {
            logMessage("NUEVO PRIMARIO: Error crítico al iniciar como primario tras promoción: " + e.getMessage(), COLOR_ERROR);
            stopServer();
        }
    }

    private void startHeartbeatToMonitor() {
        if (scheduler == null || scheduler.isShutdown()) {
            logMessage("Advertencia: Scheduler no disponible para iniciar heartbeats.", COLOR_WARNING);
            return;
        }
        scheduler.scheduleAtFixedRate(() -> {
            if (serverRunning && currentRole == ServerRole.PRIMARIO) {
                try (Socket socket = new Socket(NetworkConstants.IP_MONITOR_DEFAULT, NetworkConstants.PUERTO_HEARTBEAT_A_MONITOR_DEFAULT);
                     ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

                    HeartbeatData heartbeatPayload = getHeartbeatData();
                    oos.writeObject(heartbeatPayload);
                    oos.flush();
                    // logMessage("PRIMARIO: Heartbeat enviado al Monitor.", COLOR_INFO);
                } catch (IOException e) {
                    logMessage("PRIMARIO: Error al enviar heartbeat al Monitor: " + e.getMessage(), COLOR_ERROR);
                }
            }
        }, INIT_DELAY_HEARTBEATS_SECONDS, SEND_HEARTBEATS_SECONDS, TimeUnit.SECONDS);
    }

    private HeartbeatData getHeartbeatData() {
        String myClientListeningIp = NetworkConstants.IP_DEFAULT;

        if (clientServerSocket != null && clientServerSocket.getInetAddress() != null && !clientServerSocket.getInetAddress().isAnyLocalAddress()) {
            myClientListeningIp = clientServerSocket.getInetAddress().getHostAddress();
        }

        int myClientListeningPort = this.currentClientPort;

        return new HeartbeatData(myClientListeningIp, myClientListeningPort);
    }

    /**
     * PROVISORIO
     */
    private void startPeriodicStateReplication() {
        if (scheduler == null || scheduler.isShutdown()) {
            logMessage("Advertencia: Scheduler no disponible para iniciar replicación periódica.", COLOR_WARNING);
            return;
        }
        scheduler.scheduleAtFixedRate(this::replicateStateToPeer, INIT_DELAY_REPLICATION_SECONDS, SEND_REPLICATION_SECONDS, TimeUnit.SECONDS);
    }

    public void forceStateReplication() {
        if (serverRunning && currentRole == ServerRole.PRIMARIO) {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.submit(this::replicateStateToPeer);
            } else {
                new Thread(this::replicateStateToPeer).start();
            }
        }
    }

    private void replicateStateToPeer() {
        if (!serverRunning || currentRole != ServerRole.PRIMARIO || currentPeerIp == null || currentPeerIp.isEmpty()) {
            logMessage("Replicación: No es primario o no hay peer configurado.", COLOR_INFO);
            return;
        }
        logMessage("PRIMARIO: Iniciando replicación de estado a Peer: " + currentPeerIp + ":" + currentPeerReplicationPort, COLOR_REPLICATION);
        try (Socket socket = new Socket(currentPeerIp, currentPeerReplicationPort);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

            StateData currentState;
            synchronized (Servidor.class) {
                currentState = new StateData(directorio, mensajesPendientes);
            }

            oos.writeObject(currentState);
            oos.flush();
            logMessage("PRIMARIO: Estado replicado exitosamente a Peer.", COLOR_REPLICATION);

        } catch (java.net.ConnectException e) {
            logMessage("PRIMARIO: No se pudo conectar al Peer para replicar: " + e.getMessage(), COLOR_ERROR);
        } catch (IOException e) {
            logMessage("PRIMARIO: Error de IO al replicar estado a Peer: " + e.getMessage(), COLOR_ERROR);
        } catch (Exception e) {
            logMessage("PRIMARIO: Error general durante la replicación a Peer: " + e.getMessage(), COLOR_ERROR);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            Servidor gui = new Servidor();
            gui.setVisible(true);
        });
    }
}