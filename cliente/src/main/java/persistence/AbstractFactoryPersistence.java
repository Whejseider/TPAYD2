package persistence;

import persistence.xml.ConcreteProductContactsXML;
import persistence.xml.ConcreteProductConversationXML;

public interface AbstractFactoryPersistence {
    AbstractProductContacts createProductContacts();
    AbstractProductConversation createProductConversation();
}