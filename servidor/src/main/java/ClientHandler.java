import model.*;
import network.NetworkConstants;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Comando comando;
    private User userActual;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());

            serverLog(NetworkConstants.IP_DEFAULT + ": Conectado a cliente " + socket.getRemoteSocketAddress(), Servidor.COLOR_INFO);
        } catch (IOException e) {
            cerrarTodo(socket, objectInputStream, objectOutputStream);
        }
    }

    private void iniciarSesion() throws IOException {
        User user = (User) comando.getContenido();
        synchronized (Servidor.class) {
            if (!estaEnDirectorio(user.getNombreUsuario())) {
                Comando c = new Comando(TipoSolicitud.INICIAR_SESION, TipoRespuesta.ERROR, "El usuario es inexistente");
                enviarComando(c);
            } else {
                if (!estaConectado(user.getNombreUsuario())) {
                    if (verificaCredenciales(user)) {
                        Servidor.clientesConectados.add(this);

                        this.userActual = Servidor.directorio.getUsuarioPorNombre(user.getNombreUsuario());
                        serverLog("Servidor: Usuario conectado: " + user.getNombreUsuario(), Servidor.COLOR_INFO);

                        Comando c = new Comando(TipoSolicitud.INICIAR_SESION, TipoRespuesta.OK, this.userActual);
                        enviarComando(c);
                        enviarMensajesPendientes();
                    } else {
                        Comando c = new Comando(TipoSolicitud.INICIAR_SESION, TipoRespuesta.ERROR, "Error, el usuario o el puerto son incorrectos");
                        enviarComando(c);
                    }
                } else {
                    Comando c = new Comando(TipoSolicitud.INICIAR_SESION, TipoRespuesta.ERROR, "Ya hay un usuario conectado con ese nombre");
                    enviarComando(c);
                }
            }
        }
    }

    private void registrarse() throws IOException {
        User user = (User) comando.getContenido();
        synchronized (Servidor.class) {
            if (!estaEnDirectorio(user.getNombreUsuario())) {
                Servidor.directorio.add(user);
                serverLog("Servidor: Usuario registrado: " + user.getNombreUsuario(), Servidor.COLOR_INFO);
                Comando c = new Comando(TipoSolicitud.REGISTRARSE, TipoRespuesta.OK);
                enviarComando(c);
                replicar("registro de usuario");
            } else {
                Comando c = new Comando(TipoSolicitud.REGISTRARSE, TipoRespuesta.ERROR, "Ya existe un usuario con el mismo nombre");
                enviarComando(c);
            }
        }
    }

    public void enviarMensaje() throws IOException {
        Mensaje mensajeRecibido = (Mensaje) comando.getContenido();
        String receptor = mensajeRecibido.getNombreReceptor();
        String emisor = mensajeRecibido.getEmisor().getNombreUsuario();

        synchronized (Servidor.class) {
            if (estaEnDirectorio(receptor)) {

                User emisorDirectorio = Servidor.directorio.getUsuarioPorNombre(emisor);
                User receptorDirectorio = Servidor.directorio.getUsuarioPorNombre(receptor);

                if (emisorDirectorio == null) {
                    serverLog("ERROR CRÍTICO: Emisor '" + emisor + "' no encontrado en directorio.", Servidor.COLOR_ERROR);
                    Comando cErr = new Comando(TipoSolicitud.ENVIAR_MENSAJE, TipoRespuesta.ERROR, "Error interno: tu usuario no fue encontrado.");
                    enviarComando(cErr);
                    return;
                }
                if (receptorDirectorio == null) {
                    serverLog("ERROR CRÍTICO: Receptor '" + receptor + "' no encontrado en directorio.", Servidor.COLOR_ERROR);
                    Comando cErr = new Comando(TipoSolicitud.ENVIAR_MENSAJE, TipoRespuesta.ERROR, "Error interno: el destinatario no fue encontrado.");
                    enviarComando(cErr);
                    return;
                }

                Mensaje mensajeCopia = new Mensaje(mensajeRecibido);

                Conversacion conversacion = emisorDirectorio.getConversacionCon(receptorDirectorio.getNombreUsuario());
                conversacion.agregarMensaje(mensajeCopia);
                conversacion.setUltimoMensaje(mensajeCopia);

                Comando c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, TipoRespuesta.OK, mensajeRecibido);
                enviarComando(c);

                ClientHandler receptorConectado = getClienteConectado(receptor);

                if (receptorConectado != null) {
                    receptorConectado.recibirMensaje(mensajeCopia);
                } else {
                    Servidor.mensajesPendientes.computeIfAbsent(receptor, k -> new ArrayList<>()).add(mensajeCopia);
                    serverLog("Mensaje pendiente para " + receptor + " de " + emisorDirectorio.getNombreUsuario(), Servidor.COLOR_INFO);
                }

                if (receptorConectado == null) {
                    replicar("mensaje pendiente");
                }

            } else {
                Comando c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, TipoRespuesta.ERROR, "El usuario " + receptor + " no existe");
                enviarComando(c);
            }
        }
    }

    //Verificar si hace falta actualizar el directorio en estos casos
    public void recibirMensaje(Mensaje mensaje) throws IOException {
        String emisorOriginal = mensaje.getEmisor().getNombreUsuario();
        boolean existeContacto = false;

        synchronized (Servidor.class) {
            if (this.userActual != null) {
                existeContacto = this.userActual.getAgenda().existeContacto(emisorOriginal);
                if (!existeContacto) {
                    User u = getUserDirectorio(emisorOriginal);
                    Contacto c = Agenda.crearContacto(u);
                    this.userActual.getAgenda().agregarContacto(c);
                }
                this.userActual.getConversacionCon(emisorOriginal).agregarMensaje(mensaje);
            }
        }

        if (existeContacto) {
            Comando c = new Comando(TipoSolicitud.RECIBIR_MENSAJE, TipoRespuesta.OK, mensaje);
            enviarComando(c);
        }

        serverLog("Servidor: Mensaje entregado a " + this.userActual.getNombreUsuario() + " de " + emisorOriginal, Servidor.COLOR_INFO);
    }


    public void enviarMensajesPendientes() throws IOException {
        if (userActual != null) {
            String nombreUsuario = userActual.getNombreUsuario();

            List<Mensaje> mensajesARecibir;
            synchronized (Servidor.class) {
                mensajesARecibir = Servidor.mensajesPendientes.remove(nombreUsuario);
                replicar("mensajes pendientes (remover)");
            }

            if (mensajesARecibir != null && !mensajesARecibir.isEmpty()) {
                serverLog("Servidor: Enviando " + mensajesARecibir.size() + " mensajes pendientes a " + nombreUsuario, Servidor.COLOR_INFO);
                for (Mensaje mensaje : mensajesARecibir) {
                    recibirMensaje(mensaje);
                }
            } else {
                serverLog("Servidor: No hay mensajes pendientes para el usuario: " + nombreUsuario, Servidor.COLOR_INFO);
            }
        }
    }

    public void removeClienteConectado() {
        synchronized (Servidor.class) {
            if (Servidor.clientesConectados != null && userActual != null) {
                boolean removed = Servidor.clientesConectados.remove(this);
                if (removed) {
                    serverLog("Servidor: El usuario " + this.userActual.getNombreUsuario() + " se desconectó. Clientes restantes: " + Servidor.clientesConectados.size(), Servidor.COLOR_INFO);
                }
            }
        }
    }

    private void cerrarTodo(Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        removeClienteConectado();
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        //VER BIEN ESTO!! TODO!!
        serverLog("Servidor: Recursos cerrados para un cliente.", Servidor.COLOR_WARNING);
        this.userActual = null;
    }

    private void enviarDirectorio() throws IOException {
        Comando c;
        synchronized (Servidor.class) {
            c = new Comando(TipoSolicitud.OBTENER_DIRECTORIO, TipoRespuesta.OK, Servidor.directorio);
        }
        enviarComando(c);
        serverLog("Servidor: directorio enviado", Servidor.COLOR_INFO);
    }

    //Mandar usuario aca desde el cliente
    private void cerrarSesion() throws IOException {
        if (this.userActual == null) {
            Comando c = new Comando(TipoSolicitud.CERRAR_SESION, TipoRespuesta.ERROR, "No hay sesión activa para cerrar.");
            enviarComando(c);
            cerrarTodo(socket, objectInputStream, objectOutputStream);
        } else {
            serverLog("Servidor: Cerrar sesión de usuario: " + this.userActual.getNombreUsuario(), Servidor.COLOR_INFO);
            Comando c = new Comando(TipoSolicitud.CERRAR_SESION, TipoRespuesta.OK, null);
            enviarComando(c);

            cerrarTodo(socket, objectInputStream, objectOutputStream);
        }
    }

    private void agregarContacto() throws IOException {
        String nombreUsuario = (String) comando.getContenido();
        if (this.userActual == null) {
            Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.ERROR, "Debe iniciar sesión primero.");
            enviarComando(c);
        } else {
            synchronized (Servidor.class) {
                if (!estaEnDirectorio(nombreUsuario)) {
                    Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.ERROR, "El usuario a agregar no existe en el directorio.");
                    enviarComando(c);
                    serverLog("Servidor: ERROR al agregar contacto: " + nombreUsuario + " no existe.", Servidor.COLOR_ERROR);
                } else {
                    User miUsuarioActualizado = Servidor.directorio.getUsuarioPorNombre(this.userActual.getNombreUsuario());
                    if (miUsuarioActualizado == null) {
                        Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.ERROR, "Error interno: tu usuario no se encontró.");
                        enviarComando(c);
                    } else {
                        if (!miUsuarioActualizado.getAgenda().existeContacto(nombreUsuario)) {

                            User usuarioDirectorio = Servidor.directorio.getUsuarioPorNombre(nombreUsuario);
                            Contacto contacto = Agenda.crearContacto(usuarioDirectorio);

                            miUsuarioActualizado.getAgenda().agregarContacto(contacto);
                            this.userActual = miUsuarioActualizado;

                            Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.OK, contacto);
                            enviarComando(c);
                            serverLog("Servidor: Contacto " + nombreUsuario + " agregado a " + this.userActual.getNombreUsuario(), Servidor.COLOR_INFO);

                            replicar("agregar contacto");

                        } else {
                            Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.ERROR, "Ya existe ese contacto en la agenda");
                            enviarComando(c);
                        }
                    }
                }
            }
        }
    }


    //UTIL

    public void enviarComando(Comando comando) throws IOException {
        if (objectOutputStream != null) {
            objectOutputStream.reset();
            objectOutputStream.writeObject(comando);
            objectOutputStream.flush();
        }
    }

    private boolean verificaCredenciales(User user) {
        User userDirectorio = Servidor.directorio.getUsuarioPorNombre(user.getNombreUsuario());
        return user.getNombreUsuario().equalsIgnoreCase(userDirectorio.getNombreUsuario()) &&
                Objects.equals(user.getPuerto(), userDirectorio.getPuerto());
    }

    private static boolean estaConectado(String nombreUsuario) {
        synchronized (Servidor.class) {
            return Servidor.clientesConectados.stream().anyMatch(
                    c -> c.userActual != null && c.userActual.getNombreUsuario().equalsIgnoreCase(nombreUsuario));
        }
    }

    private static boolean estaEnDirectorio(String nombreUsuario) {
        synchronized (Servidor.class) {
            return Servidor.directorio.getDirectorio().stream().anyMatch(
                    u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario));
        }
    }

    private static ClientHandler getClienteConectado(String nombreUsuario) {
        synchronized (Servidor.class) {
            return Servidor.clientesConectados.stream().filter(
                    c -> c.userActual != null && c.userActual.getNombreUsuario().equalsIgnoreCase(nombreUsuario)).findFirst().orElse(null);
        }
    }

    private static User getUserDirectorio(String nombreUsuario) {
        synchronized (Servidor.class) {
            return Servidor.directorio.getDirectorio().stream().filter(
                    u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)).findFirst().orElse(null);
        }
    }

    private void serverLog(String message, Color color) {
        if (Servidor.instanciaServidorActivo != null) {
            Servidor.instanciaServidorActivo.logMessage(message, color);
        } else {
            System.out.println("LOG: " + message);
        }
    }

    private void replicar(String msg) {
        serverLog("Forzando replicación tras " + msg +".", Servidor.COLOR_REPLICATION);
        Servidor.instanciaServidorActivo.forceStateReplicationNow();
    }

    //Entrada de datos
    @Override
    public void run() {
        Object objetoRecibido;

        while (socket.isConnected() && !socket.isClosed()) {
            try {
                objetoRecibido = objectInputStream.readObject();
                if (objetoRecibido instanceof Comando) {
                    comando = (Comando) objetoRecibido;
                    serverLog("Servidor: Comando recibido de " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()) + ": " + comando.getTipoSolicitud(), Servidor.COLOR_INFO);
                    switch (comando.getTipoSolicitud()) {
                        case ENVIAR_MENSAJE:
                            enviarMensaje();
                            break;
                        case OBTENER_DIRECTORIO:
                            enviarDirectorio();
                            break;
                        case REGISTRARSE:
                            registrarse();
                            break;
                        case INICIAR_SESION:
                            iniciarSesion();
                            break;
                        case CERRAR_SESION:
                            cerrarSesion();
                            break;
                        case AGREGAR_CONTACTO:
                            agregarContacto();
                            break;
                        // case OBTENER_CONVERSACIONES: obtenerConversaciones(); break;
                        default:
                            serverLog("Servidor: Comando no reconocido o no manejado: " + comando.getTipoSolicitud(), Servidor.COLOR_ERROR);
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                serverLog("Servidor: Error de conexión con " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()) + ": " + e.getMessage(), Servidor.COLOR_ERROR);
                cerrarTodo(socket, objectInputStream, objectOutputStream);
                break;
            } catch (Exception e) {
                serverLog("Servidor: Excepción inesperada en el hilo del cliente " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()) + ": " + e.getMessage(), Servidor.COLOR_ERROR);
                e.printStackTrace();
                cerrarTodo(socket, objectInputStream, objectOutputStream);
                break;
            }
        }
        serverLog("Servidor: Hilo de cliente terminado para " + (userActual != null ? userActual.getNombreUsuario() : "cliente desconocido"), Servidor.COLOR_INFO);
    }
}