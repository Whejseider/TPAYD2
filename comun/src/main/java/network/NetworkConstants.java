package network;

public final class NetworkConstants {
    private NetworkConstants() {}

    // PUERTOS SERVIDOR
    public static final int PUERTO_CLIENTES_DEFAULT = 1234;
    public static final int PUERTO_REPLICACION_DEFAULT = 1235; // Secundario para replicar estado
    public static final int PUERTO_COMANDO_MONITOR_A_STANDBY_DEFAULT = 1235; // Secundario para comando de promocion a primario
    public static final int PUERTO_SINCRONIZACION_DEFAULT = 1239;

    // MONITOR
    public static final int PUERTO_HEARTBEAT_A_MONITOR_DEFAULT = 1236;
    public static final String IP_MONITOR_DEFAULT = "127.0.0.1";

    // IPS SERVIDORES
    public static final String IP_DEFAULT = "127.0.0.1";

    // PURTO DE CONSULTA DE SERVIDOR PRIMARIO
    public static final int PUERTO_CONSULTA_CLIENTE_A_MONITOR_DEFAULT = 1237;
}
