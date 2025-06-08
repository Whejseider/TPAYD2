package persistence.text;

import persistence.AbstractProductConversation;

import java.io.File;

public class ConcreteProductConversationText implements AbstractProductConversation {
    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Messenger" + File.separator;

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }
}
