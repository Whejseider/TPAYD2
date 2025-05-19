package interfaces;

import model.Contacto;

public interface ContactsListener {

    void onAddContactSuccess(Contacto contacto);
    void onAddContactFailure(String reason);
}
