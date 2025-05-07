package interfaces;

import model.User;

public interface ContactsListener {

    void onAddContactSuccess(User user);
    void onAddContactFailure(String reason);
}
