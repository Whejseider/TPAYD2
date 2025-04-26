package interfaces;

import model.Comando;

public interface ClientListener {
    void onResponse(Comando comando);
}
