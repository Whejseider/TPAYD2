package persistence;

public interface AbstractFactoryPersistence {
    AbstractProductContacts createProductContacts();
    AbstractProductConversation createProductConversation();
}