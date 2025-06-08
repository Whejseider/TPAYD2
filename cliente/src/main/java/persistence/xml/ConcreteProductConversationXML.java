package persistence.xml;

import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import persistence.AbstractProductConversation;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConcreteProductConversationXML implements AbstractProductConversation {

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

            Element rootElement = document.createElement("conversaciones");
            document.appendChild(rootElement);

            for (Conversacion c : user.getConversaciones().values()) {
                Element conversationElement = document.createElement("conversacion");
                conversationElement.setAttribute("con", c.getContacto().getNombreUsuario());

                Element ultimoMensaje = document.createElement("ultimoMensaje");

                Element contenidoUltimoMensaje = document.createElement("contenido");
                contenidoUltimoMensaje.appendChild(document.createTextNode(c.getUltimoMensaje().getContenido()));
                ultimoMensaje.appendChild(contenidoUltimoMensaje);

                Element emisorUltimoMensaje = document.createElement("emisor");
                emisorUltimoMensaje.appendChild(document.createTextNode(c.getUltimoMensaje().getEmisor()));
                ultimoMensaje.appendChild(emisorUltimoMensaje);

                Element receptorUltimoMensaje = document.createElement("receptor");
                receptorUltimoMensaje.appendChild(document.createTextNode(c.getUltimoMensaje().getNombreReceptor()));
                ultimoMensaje.appendChild(receptorUltimoMensaje);

                Element horaUltimoMensaje = document.createElement("hora");
                horaUltimoMensaje.appendChild(document.createTextNode(c.getUltimoMensaje().getTiempo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
                ultimoMensaje.appendChild(horaUltimoMensaje);

                conversationElement.appendChild(ultimoMensaje);

                Element mensajesElement = document.createElement("mensajes");
                for (Mensaje m : c.getMensajes()) {
                    Element mensajeElement = document.createElement("mensaje");

                    Element contenido = document.createElement("contenido");
                    contenido.appendChild(document.createTextNode(m.getContenido()));
                    mensajeElement.appendChild(contenido);

                    Element emisor = document.createElement("emisor");
                    emisor.appendChild(document.createTextNode(c.getUltimoMensaje().getEmisor()));
                    mensajeElement.appendChild(emisor);

                    Element receptor = document.createElement("receptor");
                    receptor.appendChild(document.createTextNode(m.getNombreReceptor()));
                    mensajeElement.appendChild(receptor);

                    Element hora = document.createElement("hora");
                    hora.appendChild(document.createTextNode(m.getTiempo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
                    mensajeElement.appendChild(hora);

                    mensajesElement.appendChild(mensajeElement);
                }
                conversationElement.appendChild(mensajesElement);

                rootElement.appendChild(conversationElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(FILE_PATH + user.getNombreUsuario() + File.separator + "conversaciones.xml");
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void load(User user) {
        try {
            File xmlFile = new File(FILE_PATH + user.getNombreUsuario() + File.separator + "conversaciones.xml");

            if (!xmlFile.exists()) {
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList conversacionNodes = document.getElementsByTagName("conversacion");

            for (int i = 0; i < conversacionNodes.getLength(); i++) {
                Element conversacionElement = (Element) conversacionNodes.item(i);
                String contactoNombre = conversacionElement.getAttribute("con");

                Contacto contacto = user.getAgenda().getContactoPorNombre(contactoNombre);
                Conversacion conversacion = new Conversacion(contacto);

                String ultimoMensajeTexto = conversacionElement
                        .getElementsByTagName("ultimoMensaje")
                        .item(0)
                        .getTextContent();

                Element mensajesElement = (Element) conversacionElement.getElementsByTagName("mensajes").item(0);
                NodeList mensajeNodes = mensajesElement.getElementsByTagName("mensaje");

                for (int j = 0; j < mensajeNodes.getLength(); j++) {
                    Element mensajeElement = (Element) mensajeNodes.item(j);

                    String contenido = mensajeElement.getElementsByTagName("contenido").item(0).getTextContent();
                    String emisor = mensajeElement.getElementsByTagName("emisor").item(0).getTextContent();
                    String receptor = mensajeElement.getElementsByTagName("receptor").item(0).getTextContent();
                    String hora = mensajeElement.getElementsByTagName("hora").item(0).getTextContent();

                    Mensaje mensaje = new Mensaje(contenido, emisor, receptor);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    mensaje.setTiempo(LocalDateTime.parse(hora, formatter));
                    conversacion.agregarMensaje(mensaje);
                }

                if (!conversacion.getMensajes().isEmpty()) {
                    conversacion.setUltimoMensaje(conversacion.getUltimoMensaje());
                } else {
                    conversacion.setUltimoMensaje(new Mensaje(contactoNombre, user.getNombreUsuario(), ultimoMensajeTexto));
                }

                user.agregarConversacion(conversacion);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
