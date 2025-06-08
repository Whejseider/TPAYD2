package persistence.xml;


import model.Contacto;
import model.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import persistence.AbstractProductContacts;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class ConcreteProductContactsXML implements AbstractProductContacts {
    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Messenger" + File.separator;

    @Override
    public void save(User user) {
        try {
            File directory = new File(FILE_PATH + user.getNombreUsuario());
            if (!directory.exists()) {
                directory.mkdirs();
            }

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            Element rootElement = document.createElement("contactos");
            document.appendChild(rootElement);

            for (Contacto c : user.getAgenda().getContactos()) {
                Element contactoElement = document.createElement("contacto");

                contactoElement.appendChild(document.createElement("alias"))
                        .appendChild(document.createTextNode(c.getAlias()));

                contactoElement.appendChild(document.createElement("nombre"))
                        .appendChild(document.createTextNode(c.getNombreUsuario()));

                contactoElement.appendChild(document.createElement("ip"))
                        .appendChild(document.createTextNode(c.getIP()));

                contactoElement.appendChild(document.createElement("puerto"))
                        .appendChild(document.createTextNode(Integer.toString(c.getPuerto())));

                rootElement.appendChild(contactoElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(FILE_PATH + user.getNombreUsuario() + File.separator + "contactos.xml");
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void load(User user) {
        try {
            File directory = new File(FILE_PATH + user.getNombreUsuario());
            if (directory.exists()) {
                File xmlFile = new File(FILE_PATH + user.getNombreUsuario() + File.separator + "contactos.xml");

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document document = builder.parse(xmlFile);

                NodeList nodeList = document.getElementsByTagName("contactos");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    System.out.println("Element Content: " + node.getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
