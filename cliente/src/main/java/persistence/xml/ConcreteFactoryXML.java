package persistence.xml;

import persistence.AbstractFactoryPersistence;
import persistence.AbstractProductContacts;
import persistence.AbstractProductConversation;

public class ConcreteFactoryXML implements AbstractFactoryPersistence {
    @Override
    public AbstractProductContacts createProductContacts() {
        return new ConcreteProductContactsXML();
    }

    @Override
    public AbstractProductConversation createProductConversation() {
        return new ConcreteProductConversationXML();
    }
}
