package network;

public final class NetworkConstants {

    private NetworkConstants() {}

    // PUERTOS SERVIDOR
    public static final int PUERTO_CLIENTES_DEFAULT = 1234;
    public static final int PUERTO_REPLICACION_DEFAULT = 1235; // Secundario para replicar estado
    public static final int PUERTO_COMANDO_MONITOR_A_STANDBY_DEFAULT = 1235; // Secundario para comando de promocion a primario
    public static final int PUERTO_CLIENTES_SECUNDARIO = 1238;

    // MONITOR
    public static final int PUERTO_HEARTBEAT_A_MONITOR_DEFAULT = 1236;
    public static final String IP_MONITOR_DEFAULT = "127.0.0.1";

    // IPS SERVIDORES
    public static final String IP_DEFAULT = "127.0.0.1";

    // PURTO DE CONSULTA DE SERVIDOR PRIMARIO
    public static final int PUERTO_CONSULTA_CLIENTE_A_MONITOR_DEFAULT = 1237;

    // En network.NetworkConstants.java (o donde tengas tus constantes)
    public static final int PUERTO_NOTIFICACION_SECUNDARIO_LISTO_DEFAULT = 1239; // Elige un puerto no usado
    public static String IP_PRIMARIO_CONOCIDO_POR_SECUNDARIO = IP_DEFAULT; // Variable para almacenar la IP del primario si se conoce
}
