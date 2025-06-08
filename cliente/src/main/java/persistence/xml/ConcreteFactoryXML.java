package persistence.xml;

import persistence.AbstractFactoryPersistence;

public class ConcreteFactoryXML implements AbstractFactoryPersistence {
    @Override
    public ConcreteProductContactsXML createProductContacts() {
        return new ConcreteProductContactsXML();
    }

    @Override
    public ConcreteProductConversationXML createProductConversation() {
        return new ConcreteProductConversationXML();
    }
}
