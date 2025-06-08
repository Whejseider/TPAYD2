package persistence;

import model.User;

public interface AbstractProductConversation {
    void save(User user) ;
    void load(User user);
}
