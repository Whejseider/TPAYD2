package connection;

import controller.ClientManager;
import interfaces.ClientListener;
import interfaces.ConnectionCallBack;
import model.*;
import network.HeartbeatData;
import network.NetworkConstants;
import view.system.FormManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Cliente {

    private Socket socket;
    private ObjectInputStream objectInputStream; //Entrada
    private ObjectOutputStream objectOutputStream; //Salida
    private Comando comando;
    private ClientListener clientListener = ClientManager.getInstance();
    public static Cliente cliente;

    private volatile boolean activo = true;

    private volatile boolean reconectando = false;

    private static String lastKnownPrimaryIp = null;
    private static int lastKnownPrimaryPort = -1;
    private static final int CONNECTION_TIMEOUT_MS = 5000; // 5 segundos para timeouts de conexión
    private static final int SERVER_CHECK_INTERVAL_SECONDS = 15; // 15 segundos para chequear la conexión con el servidor (el monitor nos da el servidor primario)
    private static final int RECONNECT_DELAY_MS = 3000; // 3 segundos de espera antes de intentar reconectar
    private ScheduledExecutorService scheduler;

    private Cliente() {
    }

    public static Cliente getInstance() {
        if (cliente == null ) {
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

    public void startPeriodicServerCheck() {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("Cliente: La verificación periódica ya está en ejecución.");
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            t.setName("PrimaryServerChecker");
            return t;
        });

        System.out.println("Cliente: Iniciando verificación periódica del servidor primario...");

        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Verificar si hay una reconexión en curso
                if (reconectando) {
                    System.out.println("Cliente: Verificación periódica - Reconexión en curso, saltando verificación");
                    return;
                }

                if (!isConectadoYActivo()) {
                    System.out.println("Cliente: Verificación periódica - Detectó conexión inactiva, intentando reconectar...");
                    intentarReconectar();
                    return;
                }

                HeartbeatData primaryInfo = queryMonitorForPrimaryServer();

                if (primaryInfo != null) {
                    String monitorIp = primaryInfo.getPrimaryClientIp();
                    int monitorPort = primaryInfo.getPrimaryClientPort();

                    boolean needReconnect = false;

                    if (socket != null &&
                            ((!socket.getInetAddress().getHostAddress().equals(monitorIp)) ||
                                    (socket.getPort() != monitorPort))) {

                        System.out.println("Cliente: Detectado cambio de servidor primario. " +
                                "Actual: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() +
                                ", Nuevo: " + monitorIp + ":" + monitorPort);
                        needReconnect = true;
                    }

                    if (needReconnect) {
                        System.out.println("Cliente: Reconectando al nuevo servidor primario...");
                        lastKnownPrimaryIp = monitorIp;
                        lastKnownPrimaryPort = monitorPort;

                        cerrarTodo(false);

                        if (clientListener != null) {
                            Comando informacionCmd = new Comando(
                                    TipoSolicitud.CONECTARSE_SERVIDOR,
                                    TipoRespuesta.INFORMATION,
                                    "Reconectando al nuevo servidor primario...");
                            clientListener.onResponse(informacionCmd);
                        }

                        intentarReconectar();
                    } else {
                        System.out.println("Cliente: Verificación periódica - Conectado al servidor primario correcto.");
                    }
                } else {
                    System.out.println("Cliente: Verificación periódica - No se pudo obtener información del servidor primario.");
                    Comando c = new Comando(TipoSolicitud.CONECTARSE_SERVIDOR, TipoRespuesta.ERROR, "No se pudo establecer la conexión con el servidor.");
                    clientListener.onResponse(c);
                }
            } catch (Exception e) {
                System.err.println("Cliente: Error en verificación periódica: " + e.getMessage());
            }
        }, SERVER_CHECK_INTERVAL_SECONDS, SERVER_CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public void stopPeriodicServerCheck() {
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
            System.out.println("Cliente: Verificación periódica detenida.");
        }
        scheduler = null;
    }

    private HeartbeatData queryMonitorForPrimaryServer() {
        System.out.println("Cliente: Consultando al Monitor ("+ NetworkConstants.IP_MONITOR_DEFAULT +":"+ NetworkConstants.PUERTO_CONSULTA_CLIENTE_A_MONITOR_DEFAULT + ") por información del servidor primario...");
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
                        System.out.println("Cliente: Monitor respondió. Primario en: " + info.getPrimaryClientIp() + ":" + info.getPrimaryClientPort());
                        return info;
                    } else {
                        System.out.println("Cliente: Monitor informó que no hay primario disponible (info recibida: " +
                                info.getPrimaryClientIp() + ":" + info.getPrimaryClientPort() + ")");
                        return null;
                    }
                } else {
                    System.out.println("Cliente: Respuesta inesperada del monitor (esperaba HeartbeatData): " +
                            (response != null ? response.getClass().getName() : "null"));
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Cliente: Timeout al consultar al Monitor: " + e.getMessage());
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("Cliente: Error al consultar al Monitor: " + e.getMessage());
        }
        return null;
    }

    public Socket attemptConnection() {
        Socket newSocket;

        if (lastKnownPrimaryIp != null && lastKnownPrimaryPort != -1 &&
                ! (lastKnownPrimaryIp.equals(NetworkConstants.IP_DEFAULT) && lastKnownPrimaryPort == NetworkConstants.PUERTO_CLIENTES_DEFAULT) ) {
            System.out.println("Cliente: Intentando conectar al último primario conocido: " + lastKnownPrimaryIp + ":" + lastKnownPrimaryPort);
            try {
                newSocket = new Socket();
                newSocket.connect(new java.net.InetSocketAddress(lastKnownPrimaryIp, lastKnownPrimaryPort), CONNECTION_TIMEOUT_MS);
                System.out.println("Cliente: Conectado exitosamente al último primario conocido.");
                return newSocket;
            } catch (IOException e) {
                System.err.println("Cliente: Fallo al conectar al último primario conocido: " + e.getMessage());
                // No limpiamos lastKnownPrimaryIp/Port aquí para mantener la información
            }
        }

        HeartbeatData primaryInfoFromMonitor = queryMonitorForPrimaryServer(); // Ahora devuelve HeartbeatData

        if (primaryInfoFromMonitor != null && primaryInfoFromMonitor.getPrimaryClientIp() != null && primaryInfoFromMonitor.getPrimaryClientPort() != -1) {
            String monitorIp = primaryInfoFromMonitor.getPrimaryClientIp();
            int monitorPort = primaryInfoFromMonitor.getPrimaryClientPort();

            System.out.println("Cliente: Intentando conectar al primario (info del Monitor): " + monitorIp + ":" + monitorPort);
            try {
                newSocket = new Socket();
                newSocket.connect(new java.net.InetSocketAddress(monitorIp, monitorPort), CONNECTION_TIMEOUT_MS);
                System.out.println("Cliente: Conectado exitosamente a " + monitorIp + ":" + monitorPort);
                lastKnownPrimaryIp = monitorIp;
                lastKnownPrimaryPort = monitorPort;
                return newSocket;
            } catch (IOException e) {
                System.err.println("Cliente: Fallo al conectar a " + monitorIp + ":" + monitorPort + " (info del Monitor) - " + e.getMessage());
            }
        } else {
            System.out.println("Cliente: No se obtuvo información válida del Monitor o no hay primario según Monitor.");
        }

        System.out.println("Cliente: Intentando conectar a la dirección por defecto: " + NetworkConstants.IP_DEFAULT + ":" + NetworkConstants.PUERTO_CLIENTES_DEFAULT);
        try {
            newSocket = new Socket();
            newSocket.connect(new java.net.InetSocketAddress(NetworkConstants.IP_DEFAULT, NetworkConstants.PUERTO_CLIENTES_DEFAULT), CONNECTION_TIMEOUT_MS);
            System.out.println("Cliente: Conectado exitosamente a la dirección por defecto.");
            lastKnownPrimaryIp = NetworkConstants.IP_DEFAULT;
            lastKnownPrimaryPort = NetworkConstants.PUERTO_CLIENTES_DEFAULT;
            return newSocket;
        } catch (IOException e) {
            System.err.println("Cliente: Fallo al conectar a la dirección por defecto - " + e.getMessage());
        }

        System.err.println("Cliente: No se pudo conectar a ningún servidor después de todos los intentos.");
        return null;
    }

    // Método para intentar reconectar después de un error
    public void intentarReconectar() {
        if (reconectando) {
            System.out.println("Cliente: Reconexión ya en curso, ignorando solicitud redundante");
            return;
        }
        reconectando = true;
        System.out.println("Cliente: Programando intento de reconexión en " + RECONNECT_DELAY_MS + "ms");

        new Thread(() -> {
            try {
                // Pequeña espera para no saturar con intentos
                Thread.sleep(RECONNECT_DELAY_MS);

                System.out.println("Cliente: Ejecutando intento de reconexión programado");
                if (conectarAlServidor()) {
                    System.out.println("Cliente: Reconexión exitosa");
                    if (clientListener != null) {
                        Comando informacionCmd = new Comando(
                                TipoSolicitud.CONECTARSE_SERVIDOR,
                                TipoRespuesta.OK,
                                "Reconexión exitosa al servidor");
                        clientListener.onResponse(informacionCmd);
                    }
                } else {
                    System.err.println("Cliente: Intento de reconexión falló");

                    if (clientListener != null) {
                        Comando errorCmd = new Comando(
                                TipoSolicitud.CONECTARSE_SERVIDOR,
                                TipoRespuesta.ERROR,
                                "No se pudo reconectar. Se seguirá intentando...");
                        clientListener.onResponse(errorCmd);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Cliente: Intento de reconexión interrumpido: " + e.getMessage());
            } finally {
                reconectando = false;
            }
        }).start();
    }

    public boolean conectarAlServidor() {
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            System.out.println("Cliente: Ya conectado.");
            return true;
        }

        System.out.println("Cliente: No conectado o conexión cerrada. Intentando (re)conectar...");
        cerrarTodo(false);
        activo = true;

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
                return true;
            } else {
                System.err.println("Cliente: Socket obtenido pero falló la inicialización de streams.");
                return false;
            }
        } else {
            System.err.println("Cliente: Fallo en attemptConnection().");
            if (clientListener != null) {
                Comando errorCmd = new Comando(TipoSolicitud.CONECTARSE_SERVIDOR, TipoRespuesta.ERROR, "No se pudo conectar a ningún servidor.");
                clientListener.onResponse(errorCmd);
            }
            return false;
        }
    }

    public void enviarMensaje(Mensaje mensaje) {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            intentarReconectar(); // Intentar reconectar automáticamente
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, mensaje);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje: " + e.getMessage());
            notificarErrorConexionPerdida("Error al enviar mensaje: " + e.getClass().getSimpleName());
            cerrarTodo(false);
            intentarReconectar();
        }
    }

    public void registrarse(User user) {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            intentarReconectar();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.REGISTRARSE, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje de registro: " + e.getMessage());
            notificarErrorConexionPerdida("Error al registrarse: " + e.getClass().getSimpleName());
            cerrarTodo(false);
            intentarReconectar();
        }
    }

    public void iniciarSesion(User user){
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            intentarReconectar();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.INICIAR_SESION, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje de inicio de sesión: " + e.getMessage());
            notificarErrorConexionPerdida("Error al iniciar sesión: " + e.getClass().getSimpleName());
            cerrarTodo(false);
            intentarReconectar();
        }
    }

    public void cerrarSesion(User user){
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            intentarReconectar();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.CERRAR_SESION, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje de cierre de sesión: " + e.getMessage());
            notificarErrorConexionPerdida("Error al cerrar sesión: " + e.getClass().getSimpleName());
            cerrarTodo(false);
            intentarReconectar();
        }
    }

    public void obtenerDirectorio(){
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            intentarReconectar();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.OBTENER_DIRECTORIO);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje de obtener directorio: " + e.getMessage());
            notificarErrorConexionPerdida("Error al obtener el directorio: " + e.getClass().getSimpleName());
            cerrarTodo(false);
            intentarReconectar();
        }
    }

    public void agregarContacto(String nombreUsuario) {
        if (!isConectadoYActivo()) {
            notificarErrorConexionPerdida("No conectado. Intente reconectar.");
            intentarReconectar();
            return;
        }
        try {
            Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, nombreUsuario);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Cliente: IOException al enviar mensaje de agregar contacto: " + e.getMessage());
            notificarErrorConexionPerdida("Error al agregar un contacto: " + e.getClass().getSimpleName());
            cerrarTodo(false);
            intentarReconectar();
        }
    }

    /**
     * Escucha conexiones del servidor
     * luego las gestiona con el patrón listener/observer
     */
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
                    } else {
                        System.err.println("Cliente: clientListener es nulo. No se puede procesar respuesta.");
                    }
                } catch (SocketTimeoutException e) {

                }
                catch (java.io.EOFException | java.net.SocketException e) {

                    System.err.println("Cliente: Conexión cerrada por el servidor o error de socket: " + e.getMessage());
                    if (activo) {
                        notificarErrorConexionPerdida("Se perdió la conexión con el servidor: " + e.getClass().getSimpleName());
                        cerrarTodo(false);
                        intentarReconectar();
                    }
                    break;
                }
                catch (IOException | ClassNotFoundException e) {
                    System.err.println("Cliente: Error en hilo de escucha: " + e.getMessage());
                    if (activo) {
                        notificarErrorConexionPerdida("Error de comunicación: " + e.getClass().getSimpleName());
                        cerrarTodo(false);
                        intentarReconectar();
                    }
                    break;
                } catch (Exception e) {
                    System.err.println("Cliente: Error inesperado en hilo de escucha: " + e.getMessage());
                    e.printStackTrace();
                    if (activo) {
                        notificarErrorConexionPerdida("Error inesperado: " + e.getClass().getSimpleName());
                        cerrarTodo(false);
                        intentarReconectar();
                    }
                    break;
                }
            }
            System.out.println("Cliente: Hilo de escucha terminado. Activo: " + activo);
        }).start();
    }

    private void notificarErrorConexionPerdida(String mensaje) {
        if (clientListener != null) {
            Comando errorComando = new Comando(TipoSolicitud.CONEXION_PERDIDA, TipoRespuesta.ERROR, mensaje);
            clientListener.onResponse(errorComando);
        }
    }

    public void cerrarTodo(boolean fuePorErrorInesperado) {

        if (!fuePorErrorInesperado) {
            stopPeriodicServerCheck();
        }

        if (!activo && !fuePorErrorInesperado) {
            return;
        }
        System.out.println("Cliente: cerrando todo. Activo era: " + activo + ", fuePorError: " + fuePorErrorInesperado);
        activo = false;

        try {
            if (objectInputStream != null) objectInputStream.close();
        } catch (IOException ignored) { }
        try {
            if (objectOutputStream != null) objectOutputStream.close();
        } catch (IOException ignored) {  }
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {  }

        objectInputStream = null;
        objectOutputStream = null;
        socket = null;

        if (fuePorErrorInesperado) {
            System.out.println("Cliente: Recursos cerrados después de un error inesperado.");
        } else {
            System.out.println("Cliente: Recursos cerrados.");
        }
    }

    public static void clearInstance() {
        if (cliente != null) {
            cliente.stopPeriodicServerCheck();
            cliente.cerrarTodo(false);
            cliente = null;
        }
        lastKnownPrimaryIp = null;
        lastKnownPrimaryPort = -1;
    }

    private boolean isConectadoYActivo() {
        return activo && socket != null && socket.isConnected() && !socket.isClosed() && objectOutputStream != null;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ClientListener getClientListener() {
        return clientListener;
    }

    public void setClientListener(ClientListener clientListener) {
        this.clientListener = clientListener;
    }

    public Comando getComando() {
        return comando;
    }

    public void setComando(Comando comando) {
        this.comando = comando;
    }
}
