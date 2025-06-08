package persistence;

import model.User;

public interface AbstractProductContacts {
    void save(User user) ;
    void load(User user);
}
