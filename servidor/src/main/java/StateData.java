import model.Directorio;
import model.Mensaje;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class StateData implements Serializable {
    private static final long serialVersionUID = 1L;
    Directorio directorio;
    Map<String, List<Mensaje>> mensajesPendientes;

    public StateData(Directorio directorio, Map<String, List<Mensaje>> mensajesPendientes) {
        this.directorio = directorio;
        this.mensajesPendientes = mensajesPendientes;
    }

    public Directorio getDirectorio() {
        return directorio;
    }

    public Map<String, List<Mensaje>> getMensajesPendientes() {
        return mensajesPendientes;
    }
}
