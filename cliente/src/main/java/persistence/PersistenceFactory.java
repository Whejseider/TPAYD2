package persistence;

import config.Config;
import persistence.JSON.ConcreteFactoryJSON;
import persistence.text.ConcreteFactoryText;
import persistence.xml.ConcreteFactoryXML;

public class PersistenceFactory {

    public static AbstractFactoryPersistence getFactory() {

        Config config = Config.getInstance();
        Config.PersistenceType type = config.getPersistenceType();

        return switch (type) {
            case JSON -> {
                System.out.println("Usando la fábrica de persistencia JSON.");
                yield new ConcreteFactoryJSON();
            }
            case XML -> {
                System.out.println("Usando la fábrica de persistencia de XML.");
                yield new ConcreteFactoryXML();
            }
            case TXT -> {
                System.out.println("Usando la fábrica de persistencia de Texto Plano.");
                yield new ConcreteFactoryText();
            }
        };
    }

}