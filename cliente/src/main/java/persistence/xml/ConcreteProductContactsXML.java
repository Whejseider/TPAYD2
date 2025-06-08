package persistence.xml;


import connection.Sesion;
import model.Contacto;
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
    public void save() {
        try {
            File directory = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario());
            if (!directory.exists()) {
                directory.mkdirs();
            }

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            Element rootElement = document.createElement("contactos");
            document.appendChild(rootElement);

            for (Contacto c : Sesion.getInstance().getUsuarioActual().getAgenda().getContactos()) {
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

            StreamResult result = new StreamResult(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario() + File.separator + "contactos.xml");
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void load() {
        File directory = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File xmlFile = new File(directory, "contactos.xml");

        if (!xmlFile.exists()) {
            System.out.println("Archivo de contactos no encontrado. No se cargarán contactos.");
            Sesion.getInstance().getUsuarioActual().getAgenda().setContactos(null); // Me olvide de hacer esto asi que a testear
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("contacto");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element contactoElement = (Element) node;

                    String alias = contactoElement.getElementsByTagName("alias").item(0).getTextContent();
                    String nombre = contactoElement.getElementsByTagName("nombre").item(0).getTextContent();
                    String ip = contactoElement.getElementsByTagName("ip").item(0).getTextContent();
                    int puerto = Integer.parseInt(contactoElement.getElementsByTagName("puerto").item(0).getTextContent());

                    Contacto contactoCargado = new Contacto(alias, nombre, ip, puerto);

                    Sesion.getInstance().getUsuarioActual().getAgenda().agregarContacto(contactoCargado);

                    System.out.println("Contacto cargado: " + contactoCargado.getAlias());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
