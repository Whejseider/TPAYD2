package persistence.xml;

import connection.Sesion;
import encryption.EncryptionType;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.MessageStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.util.Map;

public class ConcreteProductConversationXML implements AbstractProductConversation {

    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Messenger" + File.separator;

    @Override
    public void save() {
        Map<String, Conversacion> conversacion = Sesion.getInstance().getUsuarioActual().getConversaciones();
        try {
            File directory = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario());
            if (!directory.exists()) {
                directory.mkdirs();
            }

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            Element rootElement = document.createElement("conversaciones");
            document.appendChild(rootElement);

            for (Conversacion c : conversacion.values()) {
                Element conversationElement = document.createElement("conversacion");
                conversationElement.setAttribute("con", c.getContacto().getNombreUsuario());

                Element mensajesElement = document.createElement("mensajes");
                for (Mensaje m : c.getMensajes()) {
                    if (m.getStatus() != MessageStatus.FAILED) {

                        Element mensajeElement = document.createElement("mensaje");

                        Element contenido = document.createElement("contenido");
                        contenido.appendChild(document.createTextNode(m.getContenido()));
                        mensajeElement.appendChild(contenido);

                        Element emisor = document.createElement("emisor");
                        emisor.appendChild(document.createTextNode(c.getUltimoMensaje().getNombreEmisor()));
                        mensajeElement.appendChild(emisor);

                        Element receptor = document.createElement("receptor");
                        receptor.appendChild(document.createTextNode(m.getNombreReceptor()));
                        mensajeElement.appendChild(receptor);

                        Element hora = document.createElement("hora");
                        hora.appendChild(document.createTextNode(m.getTiempo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
                        mensajeElement.appendChild(hora);

                        Element encryptType = document.createElement("encrypt");
                        encryptType.appendChild(document.createTextNode(m.getEncryption().toString()));
                        mensajeElement.appendChild(encryptType);

                        Element id = document.createElement("id");
                        id.appendChild(document.createTextNode(m.getId()));
                        mensajeElement.appendChild(id);

                        Element status = document.createElement("status");
                        status.appendChild(document.createTextNode(m.getStatus().toString()));
                        mensajeElement.appendChild(status);

                        mensajesElement.appendChild(mensajeElement);
                    }
                }
                conversationElement.appendChild(mensajesElement);

                rootElement.appendChild(conversationElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario() + File.separator + "conversaciones.xml");
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void load() {
        try {
            File xmlFile = new File(FILE_PATH + Sesion.getInstance().getUsuarioActual().getNombreUsuario() + File.separator + "conversaciones.xml");

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

                Contacto contacto = Sesion.getInstance().getUsuarioActual().getAgenda().getContactoPorNombre(contactoNombre);
                Conversacion conv = new Conversacion(contacto);

                Element mensajesElement = (Element) conversacionElement.getElementsByTagName("mensajes").item(0);
                NodeList mensajeNodes = mensajesElement.getElementsByTagName("mensaje");

                for (int j = 0; j < mensajeNodes.getLength(); j++) {
                    Element mensajeElement = (Element) mensajeNodes.item(j);

                    String contenido = mensajeElement.getElementsByTagName("contenido").item(0).getTextContent();
                    String emisor = mensajeElement.getElementsByTagName("emisor").item(0).getTextContent();
                    String receptor = mensajeElement.getElementsByTagName("receptor").item(0).getTextContent();
                    String hora = mensajeElement.getElementsByTagName("hora").item(0).getTextContent();
                    String encryptType = mensajeElement.getElementsByTagName("encrypt").item(0).getTextContent();
                    String id = mensajeElement.getElementsByTagName("id").item(0).getTextContent();
                    String status = mensajeElement.getElementsByTagName("status").item(0).getTextContent();


                    EncryptionType type = EncryptionType.valueOf(encryptType);

                    Mensaje mensaje = new Mensaje(contenido, emisor, receptor, type);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    mensaje.setTiempo(LocalDateTime.parse(hora, formatter));
                    mensaje.setId(id);
                    mensaje.setStatus(MessageStatus.valueOf(status));
                    conv.agregarMensaje(mensaje);
                }

                if (!conv.getMensajes().isEmpty()) {
                    int lastMessageIndex = conv.getMensajes().size() - 1;
                    Mensaje ultimoMensajeReal = conv.getMensajes().get(lastMessageIndex);
                    conv.setUltimoMensaje(ultimoMensajeReal);
                }

                Sesion.getInstance().getUsuarioActual().agregarConversacion(conv);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
