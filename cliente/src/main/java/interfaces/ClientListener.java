package interfaces;

import model.Comando;

/**
 * Interfaz que delega las respuestas del servidor
 */
public interface ClientListener {
    void onResponse(Comando comando);
}
