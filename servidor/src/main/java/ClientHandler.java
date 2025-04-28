import model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Emprolijar, quizas mover tema de conexiones a otro modulo, y manejo de usuario a otro y asi<br>
 * tambien crear funciones si un usuario esta en linea, o en el directorio, etc<br>
 * para no andar copiando y pegando
 */
public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientesConectados = new ArrayList<>();
    public static Directorio directorio = new Directorio();
    private static Map<String, List<Mensaje>> mensajesPendientes = new HashMap<>();

    private Socket socket;
    private ObjectInputStream objectInputStream; //Entrada
    private ObjectOutputStream objectOutputStream; //Salida
    private Comando comando;
    private User userActual;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
//            this.comando = (Comando) objectInputStream.readObject();
//
            System.out.println("Servidor: Conectado a cliente");
//            Comando c = new Comando(TipoSolicitud.CONECTARSE_SERVIDOR, TipoRespuesta.OK, true);
//            objectOutputStream.writeObject(c);
//            objectOutputStream.flush();

        } catch (IOException e) {
            cerrarTodo(socket, objectInputStream, objectOutputStream);
        }
    }

    private static boolean estaConectado(String nombreUsuario) {
        return clientesConectados.stream().anyMatch(
                c -> c.userActual.getNombreUsuario().equalsIgnoreCase(nombreUsuario));
    }

    private static boolean estaEnDirectorio(String nombreUsuario) {
        return directorio.getDirectorio().stream().anyMatch(
                u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario));
    }

    private static ClientHandler getClienteConectado(String nombreUsuario) {
        return clientesConectados.stream().filter(
                c -> c.userActual.getNombreUsuario().equalsIgnoreCase(nombreUsuario)).findFirst().orElse(null);
    }

    private void iniciarSesion() throws IOException {
        User user = (User) comando.getContenido();
        if (!estaEnDirectorio(user.getNombreUsuario())) {
            System.out.println("Servidor: Error al iniciar sesión: " + user.getNombreUsuario());
            Comando c = new Comando(TipoSolicitud.INICIAR_SESION, TipoRespuesta.ERROR, "El usuario es inexistente");
            enviarComando(c);
        } else {
            if (!estaConectado(user.getNombreUsuario())) {
                clientesConectados.add(this);
                // o asignarle user
                this.userActual = directorio.getUsuarioPorNombre(user.getNombreUsuario());
                System.out.println("Servidor: Usuario conectado: " + user.getNombreUsuario());
                Comando c = new Comando(TipoSolicitud.INICIAR_SESION, TipoRespuesta.OK, userActual);
                enviarComando(c);
                enviarMensajesPendientes();
            } else {
                System.out.println("Servidor: Error al iniciar sesión: " + user.getNombreUsuario());
                Comando c = new Comando(TipoSolicitud.INICIAR_SESION, TipoRespuesta.ERROR, "Ya hay un usuario conectado con ese nombre");
                enviarComando(c);
            }
        }
    }

    private void registrarse() throws IOException {
        User user = (User) comando.getContenido();
        if (!estaEnDirectorio(user.getNombreUsuario())) {
            directorio.add(user);
            System.out.println("Servidor: Usuario registrado: " + user.getNombreUsuario());
            Comando c = new Comando(TipoSolicitud.REGISTRARSE, TipoRespuesta.OK);
            enviarComando(c);
        } else {
            System.out.println("Servidor: El usuario ya existe.");
            Comando c = new Comando(TipoSolicitud.REGISTRARSE, TipoRespuesta.ERROR, "Ya existe un usuario con el mismo nombre");
            enviarComando(c);
        }
    }

    private void cerrarSesion() throws IOException {
        if (!estaConectado(userActual.getNombreUsuario())) {
            System.out.println("Servidor: Error el usuario no está conectado: " + userActual.getNombreUsuario());
            Comando c = new Comando(TipoSolicitud.CERRAR_SESION, TipoRespuesta.ERROR, "Hubo un error al cerrar la sesión");
            enviarComando(c);
        } else {
            System.out.println("Servidor: Cerrar sesión de usuario: " + userActual.getNombreUsuario());
            Comando c = new Comando(TipoSolicitud.CERRAR_SESION, TipoRespuesta.OK, null);
            enviarComando(c);
            cerrarTodo(socket, objectInputStream, objectOutputStream);
        }
    }

    private void enviarDirectorio() throws IOException {
        Comando c = new Comando(TipoSolicitud.OBTENER_DIRECTORIO, TipoRespuesta.OK, directorio);
        enviarComando(c);
        System.out.println("Servidor: directorio enviado");
    }

    private void agregarContacto() throws IOException {
        Contacto contacto = (Contacto) comando.getContenido();
        if (!estaEnDirectorio(contacto.getNombreUsuario())) {
            Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.ERROR);
            enviarComando(c);
            System.out.println("Servidor: ERROR al agregar contacto: " + contacto.getNombreUsuario());
        } else {

            if (!userActual.getAgenda().existeContacto(contacto.getUser())) {
                userActual.getAgenda().agregarContacto(contacto);
                actualizarUsuario(userActual);

                Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.OK, userActual);
                enviarComando(c);
                System.out.println("Servidor: Contacto agregado: " + contacto.getNombreUsuario());
            } else {
                Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.ERROR, "Ya existe ese contacto en la agenda");
                enviarComando(c);
            }
        }
    }

    private Contacto crearContacto(User user) {
        Contacto userContacto = new Contacto();
        userContacto.setUser(user);
        userContacto.setNombreUsuario(user.getNombreUsuario());
        userContacto.setAlias(user.getNombreUsuario());
        userContacto.setIP(socket.getInetAddress().getHostAddress());
        userContacto.setPuerto(socket.getPort());
        return userContacto;
    }

    public void actualizarUsuario(User userActualizado) {
        if (estaEnDirectorio(userActualizado.getNombreUsuario())) {
            directorio.updateUser(userActualizado);

            if (estaConectado(userActualizado.getNombreUsuario())) {
                for (ClientHandler clienteConectado : clientesConectados) {
                    if (clienteConectado.userActual.getNombreUsuario().equalsIgnoreCase(userActualizado.getNombreUsuario())) {
                        clienteConectado.userActual = userActualizado;
                        break;
                    }
                }
            }

            System.out.println("Servidor: Usuario actualizado: " + userActualizado.getNombreUsuario());
        }
    }

    public void enviarMensaje() throws IOException {
        Mensaje mensaje = (Mensaje) comando.getContenido();
        Contacto receptor = mensaje.getReceptor();
        User emisor = mensaje.getEmisor();
        String nombreReceptor = receptor.getNombreUsuario();

        if (estaEnDirectorio(nombreReceptor)) {
            emisor.getConversacionCon(receptor).agregarMensaje(mensaje);
            actualizarUsuario(emisor);
            Comando c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, TipoRespuesta.OK, mensaje);
            enviarComando(c);

            if (estaConectado(nombreReceptor)) {
                ClientHandler receptorConectado = getClienteConectado(nombreReceptor);
                receptorConectado.recibirMensaje(mensaje);
            } else {
                mensajesPendientes.computeIfAbsent(nombreReceptor, k -> new ArrayList<>()).add(mensaje);

                System.out.println(
                        "Servidor: Mensaje pendiente - Emisor: "
                                + mensaje.getEmisor().getNombreUsuario() +
                                " - Receptor: " + nombreReceptor);
            }
        } else {
            Comando c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, TipoRespuesta.ERROR, "El usuario" + nombreReceptor + " no existe");
            enviarComando(c);
        }
    }

    /**
     * Al recibir un mensaje, verifico
     *
     * @param mensaje
     * @throws IOException
     */
    public void recibirMensaje(Mensaje mensaje) throws IOException {
        User emisor = mensaje.getEmisor();
        User receptor = mensaje.getReceptor().getUser();
        String nombreEmisor = emisor.getNombreUsuario();

        if (!userActual.getAgenda().existeContacto(emisor)) {
            userActual.getAgenda().agregarContacto(emisor);
        }


        userActual.getConversacionCon(emisor).agregarMensaje(mensaje);
        Contacto contacto = crearContacto(userActual);
        mensaje.setReceptor(contacto);
        Comando c = new Comando(TipoSolicitud.RECIBIR_MENSAJE, TipoRespuesta.OK, mensaje);
        enviarComando(c);
    }

    /**
     * Método invocado al iniciar sesión para recibir los mensajes pendientes si tuviera alguno
     *
     * @throws IOException
     */
    public void enviarMensajesPendientes() throws IOException {
        String nombreUsuario = userActual.getNombreUsuario();
        // Si existe la clave en el MAP, la sacamos del map y el contenido se almacena en mensajes!!
        List<Mensaje> mensajes = mensajesPendientes.remove(nombreUsuario);

        if (mensajes != null && !mensajes.isEmpty()) {
            for (Mensaje mensaje : mensajes) {
                recibirMensaje(mensaje);
            }
        } else {
            System.out.println("Servidor: No hay mensajes pendientes para el usuario: " + nombreUsuario);
        }
    }

    public void obtenerConversaciones() throws IOException {
        if (estaEnDirectorio(userActual.getNombreUsuario())) {
            if (estaConectado(userActual.getNombreUsuario())) {

            } else {
                Comando c = new Comando(TipoSolicitud.OBTENER_CONVERSACIONES, TipoRespuesta.ERROR);
                enviarComando(c);
                System.out.println("Servidor: ERROR al obtener conversaciones: " + userActual.getNombreUsuario());
            }
        } else {
            Comando c = new Comando(TipoSolicitud.OBTENER_CONVERSACIONES, TipoRespuesta.ERROR, "El usuario no existe");
            enviarComando(c);
            System.out.println("Servidor: ERROR al obtener conversaciones: " + userActual.getNombreUsuario());
        }
    }

    public void enviarComando(Comando comando) throws IOException {
        objectOutputStream.reset();
        objectOutputStream.writeObject(comando);
        objectOutputStream.flush();
    }

    /**
     * Ir agregando los casos que falta, e ir sacando los que no van (del enum tambien)
     */
    @Override
    public void run() {
        Object objetoRecibido;

        while (socket.isConnected()) {
            try {
                objetoRecibido = objectInputStream.readObject();
                if (objetoRecibido instanceof Comando) {
                    comando = (Comando) objetoRecibido;
                    switch (comando.getTipoSolicitud()) {

                        case ENVIAR_MENSAJE -> {
                            enviarMensaje();
                        }

                        case RECIBIR_MENSAJE -> {

                        }

                        case OBTENER_DIRECTORIO -> {
                            enviarDirectorio();
                        }

                        case REGISTRARSE -> {

                            registrarse();
                        }

                        case INICIAR_SESION -> {
                            iniciarSesion();
                        }

                        case CERRAR_SESION -> {
                            cerrarSesion();
                        }

                        case AGREGAR_CONTACTO -> {
                            agregarContacto();
                        }

                        case OBTENER_CONVERSACIONES -> {
                            obtenerConversaciones();
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                cerrarTodo(socket, objectInputStream, objectOutputStream);
                break;
            }
        }
    }

    public void removeClienteConectado() {
        if (clientesConectados != null && clientesConectados.contains(this)) {
            System.out.println("SERVIDOR: El usuario " + this.userActual.getNombreUsuario() + " se desconectó");
            clientesConectados.remove(this);
            //TODO deberia de guarda lo ultimo del usuario en el directorio creo, mucho texto
            userActual = null;
        }
    }

    private void cerrarTodo(Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        removeClienteConectado();
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            if (ois != null) ois.close();
            if (oos != null) oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
