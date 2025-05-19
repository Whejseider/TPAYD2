package network;

import java.io.Serializable;

public class HeartbeatData implements Serializable {

    private static final long serialVersionUID = 2L;
    public final String type = "HEARTBEAT_PRIMARY";
    public final String primaryClientIp;
    public final int primaryClientPort;

    public HeartbeatData(String primaryClientIp, int primaryClientPort) {
        this.primaryClientIp = primaryClientIp;
        this.primaryClientPort = primaryClientPort;
    }

    public String getType() {
        return type;
    }

    public String getPrimaryClientIp() {
        return primaryClientIp;
    }

    public int getPrimaryClientPort() {
        return primaryClientPort;
    }
}