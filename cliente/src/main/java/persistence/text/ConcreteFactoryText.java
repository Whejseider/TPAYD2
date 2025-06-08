package persistence.text;

import persistence.AbstractFactoryPersistence;
import persistence.AbstractProductContacts;
import persistence.AbstractProductConversation;

public class ConcreteFactoryText implements AbstractFactoryPersistence {
    @Override
    public AbstractProductContacts createProductContacts() {
        return new ConcreteProductContactsText();
    }

    @Override
    public AbstractProductConversation createProductConversation() {
        return new ConcreteProductConversationText();
    }
}
