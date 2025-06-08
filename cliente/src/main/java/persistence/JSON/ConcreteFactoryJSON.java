package persistence.JSON;

import persistence.AbstractFactoryPersistence;
import persistence.AbstractProductContacts;
import persistence.AbstractProductConversation;

public class ConcreteFactoryJSON implements AbstractFactoryPersistence {
    @Override
    public AbstractProductContacts createProductContacts() {
        return new ConcreteProductContactsJSON();
    }

    @Override
    public AbstractProductConversation createProductConversation() {
        return new ConcreteProductConversationJSON();
    }
}
