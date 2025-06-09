package connection;

import controller.ClientManager;
import interfaces.ClientListener;
import model.*;
import network.HeartbeatData;
import network.NetworkConstants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Cliente {

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Comando comando;
    private ClientListener clientListener = ClientManager.getInstance();
    public static Cliente cliente;

    private volatile boolean activo = true;
    private volatile boolean reconectando = false;
    private final AtomicBoolean monitorConnectionActive = new AtomicBoolean(false);

    private static String lastKnownPrimaryIp = null;
    private static int lastKnownPrimaryPort = -1;

    // Timeouts y delays
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final int SERVER_CHECK_INTERVAL_SECONDS = 10;
    private static final int MONITOR_CHECK_INTERVAL_SECONDS = 5; // Chequear monitor más seguido
    private static final int RECONNECT_DELAY_MS = 3000;
    private static final int MONITOR_RECONNECT_DELAY_MS = 2000; // Sin suso todaiva
    private static final int MAX_RECONNECT_ATTEMPTS = 3;
    private static final int EXTENDED_RECONNECT_DELAY_MS = 10000; // Si falla mucho, esperar más

    private ScheduledExecutorService serverCheckScheduler;
    private ScheduledExecutorService monitorCheckScheduler;
    private ScheduledExecutorService reconnectScheduler;

    private Cliente() {
    }

    public static Cliente getInstance() {
        if (cliente == null) {
            cliente = new Cliente();
        }
        return cliente;
    }

    public void init(Socket socket) {
        try {
            this.socket = socket;
            this.socket.setSoTimeout(CONNECTION_TIMEOUT_MS * 2);
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Cliente: Streams inicializados para socket: " + socket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.err.println("Cliente: Error al inicializar streams: " + e.getMessage());
            cerrarTodo(true);
        }
    }

    // Chequea el monitor constantemente para saber cuál es el servidor primario
    public void startMonitorConnectionCheck() {
        if (monitorCheckScheduler != null && !monitorCheckScheduler.isShutdown()) {
            System.out.println("Cliente: El monitoreo del monitor ya está en ejecución.");
            return;
        }

        monitorCheckScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            t.setName("MonitorConnectionChecker");
            return t;
        });

        System.out.println("Cliente: Iniciando verificación constante del monitor...");

        monitorCheckScheduler.scheduleAtFixedRate(() -> {
            try {
                HeartbeatData primaryInfo = queryMonitorForPrimaryServer();

                if (primaryInfo != null) {
                    monitorConnectionActive.set(true);

                    String monitorIp = primaryInfo.getPrimaryClientIp();
                    int monitorPort = primaryInfo.getPrimaryClientPort();

                    if (monitorIp != null && !monitorIp.equals("DESCONOCIDO_NINGUNO") && monitorPort != -1) {
                        // Si cambió el servidor primario, actualizar
                        if (!monitorIp.equals(lastKnownPrimaryIp) || monitorPort != lastKnownPrimaryPort) {
                            System.out.println("Cliente: Monitor informó nuevo servidor primario: " + monitorIp + ":" + monitorPort);
                            lastKnownPrimaryIp = monitorIp;
                            lastKnownPrimaryPort = monitorPort;

                            // Si estamos conectados a otro servidor, reconectar
                            if (socket != null && socket.isConnected() &&
                                    (!socket.getInetAddress().getHostAddress().equals(monitorIp) ||
                                            socket.getPort() != monitorPort)) {
                                System.out.println("Cliente: Reconectando al nuevo servidor primario informado por monitor...");
                                triggerServerReconnect();
                            }
                        }
                    }
                } else {
                    if (monitorConnectionActive.get()) {
                        System.out.println("Cliente: Se perdió la conexión con el monitor.");
                        monitorConnectionActive.set(false);
                    }
                }
            } catch (Exception e) {
                System.err.println("Cliente: Error en verificación del monitor: " + e.getMessage());
                monitorConnectionActive.set(false);
            }
        }, 0, MONITOR_CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    // Chequea que el servidor esté vivo cada tanto
    public void startPeriodicServerCheck() {
        if (serverCheckScheduler != null && !serverCheckScheduler.isShutdown()) {
            System.out.println("Cliente: La verificación periódica del servidor ya está en ejecución.");
            return;
        }

        serverCheckScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            t.setName("ServerConnectionChecker");
            return t;
        });

        System.out.println("Cliente: Iniciando verificación periódica del servidor...");

        serverCheckScheduler.scheduleAtFixedRate(() -> {
            try {
                if (reconectando) {
                    System.out.println("Cliente: Verificación servidor - Reconexión en curso, saltando verificación");
                    return;
                }

                if (!isConectadoYActivo()) {
                    System.out.println("Cliente: Verificación servidor - Detectó conexión inactiva, intentando reconectar...");
                    triggerServerReconnect();
                    return;
                }

                // Verificar que estemos conectados al servidor correcto
                if (monitorConnectionActive.get() && lastKnownPrimaryIp != null && lastKnownPrimaryPort != -1) {
                    if (socket != null && socket.isConnected() &&
                            (!socket.getInetAddress().getHostAddress().equals(lastKnownPrimaryIp) ||
                                    socket.getPort() != lastKnownPrimaryPort)) {

                        System.out.println("Cliente: Detectado que no estamos conectados al servidor primario correcto. Reconectando...");
                        triggerServerReconnect();
                        return;
                    }
                }

                // Mandar un ping para ver si el servidor responde
                if (!sendPingToServer()) {
                    System.out.println("Cliente: Ping al servidor falló. Iniciando reconexión...");
                    triggerServerReconnect();
                }

            } catch (Exception e) {
                System.err.println("Cliente: Error en verificación periódica del servidor: " + e.getMessage());
            }
        }, SERVER_CHECK_INTERVAL_SECONDS, SERVER_CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    // Proceso que se fija si hay que reconectar
    public void startReconnectProcess() {
        if (reconnectScheduler != null && !reconnectScheduler.isShutdown()) {
            return;
        }

        reconnectScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            t.setName("ReconnectProcess");
            return t;
        });

        System.out.println("Cliente: Iniciando proceso de reconexión constante...");

        reconnectScheduler.scheduleAtFixedRate(() -> {
            try {
                // Si no hay conexión al servidor, reconectar
                if (!isConectadoYActivo() && !reconectando) {
                    System.out.println("Cliente: Proceso de reconexión - Detectó falta de conexión al servidor");
                    attemptReconnectWithRetries();
                }

                // Info del estado del monitor
                if (!monitorConnectionActive.get()) {
                    System.out.println("Cliente: Proceso de reconexión - Sin conexión al monitor");
                }

            } catch (Exception e) {
                System.err.println("Cliente: Error en proceso de reconexión: " + e.getMessage());
            }
        }, RECONNECT_DELAY_MS / 1000, RECONNECT_DELAY_MS / 1000, TimeUnit.SECONDS);
    }

    // Ping simple al servidor para ver si está vivo
    private boolean sendPingToServer() {
        if (!isConectadoYActivo()) {
            return false;
        }

        try {
            // Usar un comando liviano que ya existe
            Comando pingCmd = new Comando(TipoSolicitud.PING);
            objectOutputStream.writeObject(pingCmd);
            objectOutputStream.flush();

            // La respuesta se maneja en el hilo de escucha
            return true;
        } catch (IOException e) {
            System.err.println("Cliente: Error al enviar ping al servidor: " + e.getMessage());
            return false;
        }
    }

    // Dispara reconexión en otro hilo
    private void triggerServerReconnect() {
        if (!reconectando) {
            new Thread(this::attemptReconnectWithRetries, "ServerReconnectTrigger").start();
        }
    }

    // Reconectar con reintentos
    private void attemptReconnectWithRetries() {
        if (reconectando) {
            return;
        }

        reconectando = true;
        int attempts = 0;

        try {
            while (activo && attempts < MAX_RECONNECT_ATTEMPTS) {
                attempts++;
                System.out.println("Cliente: Intento de reconexión " + attempts + "/" + MAX_RECONNECT_ATTEMPTS);

                Socket nuevoSocket = attemptConnection();

                if (nuevoSocket != null) {
                    System.out.println("Cliente: Reconexión exitosa en intento " + attempts);
                    if (ConnectSuccess(nuevoSocket)) return;
                }

                // Esperar antes del siguiente intento
                Thread.sleep(RECONNECT_DELAY_MS);
            }

            // Si fallan los intentos rápidos, seguir con intentos más espaciados
            System.out.println("Cliente: Agotados los intentos rápidos. Continuando con intentos espaciados...");

            while (activo) {
                Thread.sleep(EXTENDED_RECONNECT_DELAY_MS);

                Socket nuevoSocket = attemptConnection();
                if (nuevoSocket != null) {
                    System.out.println("Cliente: Reconexión exitosa después de intentos espaciados");
                    if (ConnectSuccess(nuevoSocket)) return;
                }
            }

        } catch (InterruptedException e) {
            System.err.println("Cliente: Proceso de reconexión interrumpido");
            Thread.currentThread().interrupt();
        } finally {
            reconectando = false;
        }
    }

    private boolean ConnectSuccess(Socket nuevoSocket) {
        cerrarConexionSolamente(); // Solo cerrar socket y streams, NO los schedulers
        init(nuevoSocket);

        if (isConectadoYActivo()) {
            escuchar();

            // Reiniciar solo el scheduler de verificación del servidor
            stopScheduler(serverCheckScheduler, "ServerCheck");
            startPeriodicServerCheck();

            if (clientListener != null) {
                Comando informacionCmd = new Comando(
                        TipoSolicitud.CONECTARSE_SERVIDOR,
                        TipoRespuesta.INFORMATION,
                        "Reconectado exitosamente al servidor.");
                clientListener.onResponse(informacionCmd);
            }

            reconectando = false;
            return true;
        }
        return false;
    }

    public void stopAllSchedulers() {
        stopScheduler(serverCheckScheduler, "ServerCheck");
        stopScheduler(monitorCheckScheduler, "MonitorCheck");
        stopScheduler(reconnectScheduler, "Reconnect");

        serverCheckScheduler = null;
        monitorCheckScheduler = null;
        reconnectScheduler = null;
    }

    private void stopScheduler(ScheduledExecutorService scheduler, String name) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            System.out.println("Cliente: " + name + " scheduler detenido.");
        }
    }

    private HeartbeatData queryMonitorForPrimaryServer() {
        try (Socket monitorSocket = new Socket()) {
            monitorSocket.connect(new java.net.InetSocketAddress(NetworkConstants.IP_MONITOR_DEFAULT, NetworkConstants.PUERTO_CONSULTA_CLIENTE_A_MONITOR_DEFAULT), CONNECTION_TIMEOUT_MS);
            monitorSocket.setSoTimeout(CONNECTION_TIMEOUT_MS);

            try (ObjectOutputStream oos = new ObjectOutputStream(monitorSocket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(monitorSocket.getInputStream())) {

                oos.writeObject("OBTENER_INFO_PRIMARIO");
                oos.flush();

                Object response = ois.readObject();
                if (response instanceof HeartbeatData info) {
                    if (info.getPrimaryClientIp() != null &&
                            !info.getPrimaryClientIp().equals("DESCONOCIDO_NINGUNO") &&
                            info.getPrimaryClientPort() != -1) {
                        return info;
                    }
                }
            }
        } catch (Exception e) {
            // No imprimir error para evitar spam
        }
        return null;
    }

    public Socket attemptConnection() {
        Socket newSocket;

        // 1. Intentar con último primario conocido (si no es el default)
        if (lastKnownPrimaryIp != null && lastKnownPrimaryPort != -1 &&
                !(lastKnownPrimaryIp.equals(NetworkConstants.IP_DEFAULT) && lastKnownPrimaryPort == NetworkConstants.PUERTO_CLIENTES_DEFAULT)) {
            try {
                newSocket = new Socket();
                newSocket.connect(new java.net.InetSocketAddress(lastKnownPrimaryIp, lastKnownPrimaryPort), CONNECTION_TIMEOUT_MS);
                return newSocket;
            } catch (IOException e) {
                // Fallar silenciosamente y continuar
            }
        }

        // 2. Preguntar al monitor por info fresca
        HeartbeatData primaryInfoFromMonitor = queryMonitorForPrimaryServer();
        if (primaryInfoFromMonitor != null && primaryInfoFromMonitor.getPrimaryClientIp() != null && primaryInfoFromMonitor.getPrimaryClientPort() != -1) {
            String monitorIp = primaryInfoFromMonitor.getPrimaryClientIp();
            int monitorPort = primaryInfoFromMonitor.getPrimaryClientPort();

            try {
                newSocket = new Socket();
                newSocket.connect(new java.net.InetSocketAddress(monitorIp, monitorPort), CONNECTION_TIMEOUT_MS);
                lastKnownPrimaryIp = monitorIp;
                lastKnownPrimaryPort = monitorPort;
                return newSocket;
            } catch (IOException e) {
                // Fallar silenciosamente y continuar
            }
        }

        // 3. Intentar dirección por defecto
        try {
            newSocket = new Socket();
            newSocket.connect(new java.net.InetSocketAddress(NetworkConstants.IP_DEFAULT, NetworkConstants.PUERTO_CLIENTES_DEFAULT), CONNECTION_TIMEOUT_MS);
            lastKnownPrimaryIp = NetworkConstants.IP_DEFAULT;
            lastKnownPrimaryPort = NetworkConstants.PUERTO_CLIENTES_DEFAULT;
            return newSocket;
        } catch (IOException e) {
            // Fallar silenciosamente
        }

        return null;
    }

    public void connectToServer() {
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            System.out.println("Cliente: Ya conectado.");
            return;
        }

        System.out.println("Cliente: Iniciando conexión al servidor...");
        cerrarConexionSolamente(); // Solo cerrar conexión anterior, no los schedulers
        activo = true;

        // Iniciar monitoreo solo si no están activos
        startMonitorConnectionCheck();
        startReconnectProcess();

        Socket newSocket = attemptConnection();

        if (newSocket != null) {
            this.init(newSocket);
            if (this.socket != null && this.socket.isConnected()) {
                this.escuchar();
                startPeriodicServerCheck();

                System.out.println("Cliente: Conexión establecida y escuchando.");
                Comando c = new Comando(TipoSolicitud.CONECTARSE_SERVIDOR, TipoRespuesta.OK);
                if (clientListener != null) {
                    clientListener.onResponse(c);
                }
            }
        } else {
            System.err.println("Cliente: Fallo en conexión inicial. El proceso de reconexión continuará automáticamente.");
        }
    }

    public void confirmarEntregaMensaje(Mensaje mensaje) {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            triggerServerReconnect();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.CONFIRMAR_ENTREGA_MENSAJE, mensaje);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al confirmar entrega mensaje: " + e.getMessage());
            notificarErrorConexionPerdida("Error al confirmar entrega mensaje: " + e.getClass().getSimpleName());
            cerrarConexionSolamente();
            triggerServerReconnect();
        }
    }

    public void confirmarLecturaMensaje(Mensaje mensaje) {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            triggerServerReconnect();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.CONFIRMAR_LECTURA_MENSAJE, mensaje);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al confirmar lectura mensaje: " + e.getMessage());
            notificarErrorConexionPerdida("Error al confirmar lectura mensaje: " + e.getClass().getSimpleName());
            cerrarConexionSolamente();
            triggerServerReconnect();
        }
    }

    public void registrarse(User user) {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            triggerServerReconnect();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.REGISTRARSE, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje de registro: " + e.getMessage());
            notificarErrorConexionPerdida("Error al registrarse: " + e.getClass().getSimpleName());
            cerrarConexionSolamente();
            triggerServerReconnect();
        }
    }

    public void iniciarSesion(User user) {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            triggerServerReconnect();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.INICIAR_SESION, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje de inicio de sesión: " + e.getMessage());
            notificarErrorConexionPerdida("Error al iniciar sesión: " + e.getClass().getSimpleName());
            cerrarConexionSolamente();
            triggerServerReconnect();
        }
    }

    public void cerrarSesion(User user) {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            triggerServerReconnect();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.CERRAR_SESION, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje de cierre de sesión: " + e.getMessage());
            notificarErrorConexionPerdida("Error al cerrar sesión: " + e.getClass().getSimpleName());
            cerrarConexionSolamente();
            triggerServerReconnect();
        }
    }

    public void obtenerDirectorio() {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            triggerServerReconnect();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.OBTENER_DIRECTORIO);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje de obtener directorio: " + e.getMessage());
            notificarErrorConexionPerdida("Error al obtener el directorio: " + e.getClass().getSimpleName());
            cerrarConexionSolamente();
            triggerServerReconnect();
        }
    }

    public void agregarContacto(String nombreUsuario) {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            triggerServerReconnect();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, nombreUsuario);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje de agregar contacto: " + e.getMessage());
            notificarErrorConexionPerdida("Error al agregar un contacto: " + e.getClass().getSimpleName());
            cerrarConexionSolamente();
            triggerServerReconnect();
        }
    }

    public void enviarMensaje(Mensaje mensaje) {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            if (Sesion.getInstance().getUsuarioActual() != null) {
                Sesion.getInstance().agregarMensaje(mensaje);
            }
            triggerServerReconnect();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, mensaje);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje: " + e.getMessage());
            notificarErrorConexionPerdida("Error al enviar mensaje: " + e.getClass().getSimpleName());
            cerrarConexionSolamente(); // Solo cerrar conexión, mantener schedulers
            triggerServerReconnect();
        }
    }

    public void escuchar() {
        activo = true;

        new Thread(() -> {
            Comando respuesta;
            System.out.println("Cliente: Hilo de escucha iniciado para " + (socket != null ? socket.getRemoteSocketAddress() : "socket nulo"));

            while (activo && socket != null && !socket.isClosed() && socket.isConnected()) {
                try {
                    if (objectInputStream == null) {
                        System.err.println("Cliente: objectInputStream es nulo en el hilo de escucha. Terminando hilo.");
                        break;
                    }
                    respuesta = (Comando) objectInputStream.readObject();
                    if (clientListener != null) {
                        clientListener.onResponse(respuesta);
                    }
                } catch (SocketTimeoutException e) {
                    // Timeout normal, continuar
                } catch (java.io.EOFException | java.net.SocketException e) {
                    System.err.println("Cliente: Conexión cerrada por el servidor: " + e.getMessage());
                    if (activo) {
                        notificarErrorConexionPerdida("Se perdió la conexión con el servidor");
                        cerrarConexionSolamente(); // Solo cerrar conexión, mantener schedulers
                    }
                    break;
                } catch (Exception e) {
                    System.err.println("Cliente: Error en hilo de escucha: " + e.getMessage());
                    if (activo) {
                        notificarErrorConexionPerdida("Error de comunicación");
                        cerrarConexionSolamente(); // Solo cerrar conexión, mantener schedulers
                    }
                    break;
                }
            }
            System.out.println("Cliente: Hilo de escucha terminado.");
        }, "ClienteListenerThread").start();
    }

    private void notificarErrorConexionPerdida(String mensaje) {
        if (clientListener != null) {
            Comando errorComando = new Comando(TipoSolicitud.CONEXION_PERDIDA, TipoRespuesta.ERROR, mensaje);
            clientListener.onResponse(errorComando);
        }
    }

    // Cierra solo la conexión del servidor pero mantiene los schedulers vivos
    private void cerrarConexionSolamente() {
        System.out.println("Cliente: Cerrando solo la conexión del servidor...");

        try {
            if (objectInputStream != null) objectInputStream.close();
        } catch (IOException ignored) {}
        try {
            if (objectOutputStream != null) objectOutputStream.close();
        } catch (IOException ignored) {}
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}

        objectInputStream = null;
        objectOutputStream = null;
        socket = null;

        System.out.println("Cliente: Conexión del servidor cerrada, schedulers mantenidos activos.");
    }

    public void cerrarTodo(boolean fuePorErrorInesperado) {
        if (!fuePorErrorInesperado) {
            stopAllSchedulers();
        }

        if (!activo && !fuePorErrorInesperado) {
            return;
        }

        System.out.println("Cliente: cerrando todo. Activo era: " + activo + ", fuePorError: " + fuePorErrorInesperado);
        activo = false;

        cerrarConexionSolamente();

        System.out.println("Cliente: Recursos cerrados.");
    }

    public static void clearInstance() {
        if (cliente != null) {
            cliente.stopAllSchedulers();
            cliente.cerrarTodo(false);
            cliente = null;
        }
        lastKnownPrimaryIp = null;
        lastKnownPrimaryPort = -1;
    }

    private boolean isConectadoYActivo() {
        return activo && socket != null && socket.isConnected() && !socket.isClosed() && objectOutputStream != null;
    }

    public boolean isMonitorConnected() {
        return monitorConnectionActive.get();
    }

    // Getters y setters
    public Socket getSocket() { return socket; }
    public void setSocket(Socket socket) { this.socket = socket; }
    public ClientListener getClientListener() { return clientListener; }
    public void setClientListener(ClientListener clientListener) { this.clientListener = clientListener; }
    public Comando getComando() { return comando; }
    public void setComando(Comando comando) { this.comando = comando; }
}