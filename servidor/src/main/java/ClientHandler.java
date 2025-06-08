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
    private volatile boolean running = true;

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

    private void registrarse() throws IOException {
        User user = (User) comando.getContenido();
        Comando c;
        boolean exito = false;

        if (!estaEnDirectorio(user.getNombreUsuario())) {
            Servidor.directorio.add(user);
            serverLog("Servidor: Usuario registrado: " + user.getNombreUsuario(), Servidor.COLOR_INFO);
            c = new Comando(TipoSolicitud.REGISTRARSE, TipoRespuesta.OK);
            exito = true;
        } else {
            c = new Comando(TipoSolicitud.REGISTRARSE, TipoRespuesta.ERROR, "Ya existe un usuario con el mismo nombre");
        }

        enviarComando(c);
        if (exito) {
            replicar("registro de usuario");
        }
    }

    public void enviarMensaje() throws IOException {
        Mensaje mensajeRecibido = (Mensaje) comando.getContenido();
        String receptor = mensajeRecibido.getNombreReceptor();
        String emisor = mensajeRecibido.getNombreEmisor();
        Comando c;

        if (estaEnDirectorio(receptor)) {

            User emisorDirectorio = Servidor.directorio.getUsuarioPorNombre(emisor);
            User receptorDirectorio = Servidor.directorio.getUsuarioPorNombre(receptor);

            if (emisorDirectorio == null) {
                serverLog("ERROR CRÍTICO: Emisor '" + emisor + "' no encontrado en directorio.", Servidor.COLOR_ERROR);
                c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, TipoRespuesta.ERROR, "Error interno: tu usuario no fue encontrado.");

            } else if (receptorDirectorio == null) {
                serverLog("ERROR CRÍTICO: Receptor '" + receptor + "' no encontrado en directorio.", Servidor.COLOR_ERROR);
                c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, TipoRespuesta.ERROR, "Error interno: el destinatario no fue encontrado.");
            } else {

                Mensaje mensajeCopia = new Mensaje(mensajeRecibido);

                Conversacion conversacion = emisorDirectorio.getConversacionCon(receptorDirectorio.getNombreUsuario());
                conversacion.agregarMensaje(mensajeCopia);
                conversacion.setUltimoMensaje(mensajeCopia);

                c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, TipoRespuesta.OK, mensajeRecibido);
                enviarComando(c);
                replicar("enviar mensaje");

                ClientHandler receptorConectado = getClienteConectado(receptor);

                if (receptorConectado != null) {
                    receptorConectado.recibirMensaje(mensajeCopia);
                } else {
                    Servidor.mensajesPendientes.computeIfAbsent(receptor, k -> new ArrayList<>()).add(mensajeCopia);
                    serverLog("Mensaje pendiente para " + receptor + " de " + emisorDirectorio.getNombreUsuario(), Servidor.COLOR_INFO);
                    replicar("mensaje pendiente");
                }

            }
        } else {
            c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, TipoRespuesta.ERROR, "El usuario " + receptor + " no existe");
        }

        enviarComando(c);

    }

    //Verificar si hace falta actualizar el directorio en estos casos
    // pero creo que no
    public void recibirMensaje(Mensaje mensaje) throws IOException {
        String emisorOriginal = mensaje.getNombreEmisor();
        boolean existeContacto;

        if (this.userActual != null) {
            existeContacto = this.userActual.getAgenda().existeContacto(emisorOriginal);
            if (!existeContacto) {
                User u = getUserDirectorio(emisorOriginal);
                Contacto c = Agenda.crearContacto(u);
                this.userActual.getAgenda().agregarContacto(c);
            }
            this.userActual.getConversacionCon(emisorOriginal).agregarMensaje(mensaje);
        }


        Comando c = new Comando(TipoSolicitud.RECIBIR_MENSAJE, TipoRespuesta.OK, mensaje);
        enviarComando(c);


        serverLog("Servidor: Mensaje entregado a " + this.userActual.getNombreUsuario() + " de " + emisorOriginal, Servidor.COLOR_INFO);
    }


    public void enviarMensajesPendientes() throws IOException {
        if (userActual != null) {
            String nombreUsuario = userActual.getNombreUsuario();

            List<Mensaje> mensajesARecibir;

            mensajesARecibir = Servidor.mensajesPendientes.remove(nombreUsuario);
            replicar("mensajes pendientes (remover)");


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
        if (Servidor.clientesConectados != null && userActual != null) {
            boolean removed = Servidor.clientesConectados.remove(this);
            if (removed) {
                serverLog("Servidor: El usuario " + this.userActual.getNombreUsuario() + " se desconectó. Clientes restantes: " + Servidor.clientesConectados.size(), Servidor.COLOR_INFO);
            }
        }
    }

    private void cerrarTodo(Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        if (!running && this.socket != null && this.socket.isClosed()) {
            // Si ya se llamó a shutdown() y el socket está cerrado, puede que ya se haya hecho parte de la limpieza.
            // Sin embargo, es más seguro intentar cerrar los streams y remover de la lista.
        }
        running = false; // Asegurarse de que esté en false

        // Remover el cliente de la lista de conectados
        // Es importante que esto se haga ANTES de que Servidor.instanciaServidorActivo pueda ser null
        // si la remoción depende de ello para el log, o que el log maneje el caso null.
        removeClienteConectado(); // Ya tienes la lógica para esto y parece correcta.

        try {
            if (ois != null) ois.close();
        } catch (IOException e) { /* ignora o loguea */ }
        try {
            if (oos != null) oos.close();
        } catch (IOException e) { /* ignora o loguea */ }
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) { /* ignora o loguea */ }

        // userActual se pone a null aquí, lo cual es correcto para este handler.
        // El log ya está en removeClienteConectado.
        // serverLog("Servidor: Recursos cerrados para un cliente.", Servidor.COLOR_WARNING); // Puedes mantenerlo si quieres
        this.userActual = null; // Asegurar que la referencia se limpie
    }

    private void enviarDirectorio() throws IOException {
        Comando c;

        c = new Comando(TipoSolicitud.OBTENER_DIRECTORIO, TipoRespuesta.OK, Servidor.directorio);

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
        Comando c;
        if (this.userActual == null) {
            c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.ERROR, "Debe iniciar sesión primero.");
        } else {
            if (!estaEnDirectorio(nombreUsuario)) {
                c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.ERROR, "El usuario a agregar no existe en el directorio.");
                serverLog("Servidor: ERROR al agregar contacto: " + nombreUsuario + " no existe.", Servidor.COLOR_ERROR);
            } else {
                User miUsuarioActualizado = Servidor.directorio.getUsuarioPorNombre(this.userActual.getNombreUsuario());
                if (miUsuarioActualizado == null) {
                    c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.ERROR, "Error interno: tu usuario no se encontró.");
                } else {
                    if (!miUsuarioActualizado.getAgenda().existeContacto(nombreUsuario)) {

                        User usuarioDirectorio = Servidor.directorio.getUsuarioPorNombre(nombreUsuario);
                        Contacto contacto = Agenda.crearContacto(usuarioDirectorio);

                        miUsuarioActualizado.getAgenda().agregarContacto(contacto);
                        this.userActual = miUsuarioActualizado;

                        c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.OK, contacto);
                        serverLog("Servidor: Contacto " + nombreUsuario + " agregado a " + this.userActual.getNombreUsuario(), Servidor.COLOR_INFO);

                        replicar("agregar contacto");

                    } else {
                        c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, TipoRespuesta.ERROR, "Ya existe ese contacto en la agenda");
                    }
                }
            }
        }
        enviarComando(c);

    }


    //UTIL

    public void enviarComando(Comando comando)  {
        try {
            if (objectOutputStream != null) {
                objectOutputStream.reset();
                objectOutputStream.writeObject(comando);
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            serverLog("Servidor: ¡ERROR! al enviar comando al cliente", Servidor.COLOR_ERROR);
        }
    }

    private boolean verificaCredenciales(User user) {
        User userDirectorio = Servidor.directorio.getUsuarioPorNombre(user.getNombreUsuario());
        return user.getNombreUsuario().equalsIgnoreCase(userDirectorio.getNombreUsuario()) &&
                Objects.equals(user.getPuerto(), userDirectorio.getPuerto());
    }

    private static boolean estaConectado(String nombreUsuario) {
            return Servidor.clientesConectados.stream().anyMatch(
                    c -> c.userActual != null && c.userActual.getNombreUsuario().equalsIgnoreCase(nombreUsuario));

    }

    private static boolean estaEnDirectorio(String nombreUsuario) {

        return Servidor.directorio.getDirectorio().stream().anyMatch(
                u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario));

    }

    private static ClientHandler getClienteConectado(String nombreUsuario) {
            return Servidor.clientesConectados.stream().filter(
                    c -> c.userActual != null && c.userActual.getNombreUsuario().equalsIgnoreCase(nombreUsuario)).findFirst().orElse(null);

    }

    private static User getUserDirectorio(String nombreUsuario) {
            return Servidor.directorio.getDirectorio().stream().filter(
                    u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)).findFirst().orElse(null);

    }

    private void serverLog(String message, Color color) {
        if (Servidor.instanciaServidorActivo != null) {
            Servidor.instanciaServidorActivo.logMessage(message, color);
        } else {
            System.out.println("LOG: " + message);
        }
    }

    //No me gusto como manejo el mensaje, despues lo tengo que cambiar
    private void replicar(String msg) {
        Servidor servidorActivo = Servidor.instanciaServidorActivo; // Buena práctica: copia local
        if (servidorActivo != null && servidorActivo.isServerRunning() && servidorActivo.getCurrentRole() == ServerRole.PRIMARIO) {
            serverLog("Forzando replicación tras " + msg + ".", Servidor.COLOR_REPLICATION);
            servidorActivo.forceStateReplication(); // Esta es la línea problemática
        } else {
            // Loguea si el servidor no está en el estado correcto
            String motivo = "Servidor no disponible o no es primario";
            if (servidorActivo == null) motivo = "Instancia del servidor es null";
            else if (!servidorActivo.isServerRunning()) motivo = "Servidor no está corriendo";
            else if (servidorActivo.getCurrentRole() != ServerRole.PRIMARIO) motivo = "Servidor no es primario";
            serverLog("ClientHandler: No se puede forzar la replicación (" + msg + "). Motivo: " + motivo, Servidor.COLOR_WARNING);
        }
    }

    //Entrada de datos
//    @Override
//    public void run() {
//        Object objetoRecibido;
//
//        while (socket.isConnected() && !socket.isClosed()) {
//            try {
//                objetoRecibido = objectInputStream.readObject();
//                if (objetoRecibido instanceof Comando) {
//                    comando = (Comando) objetoRecibido;
//                    serverLog("Servidor: Comando recibido de " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()) + ": " + comando.getTipoSolicitud(), Servidor.COLOR_INFO);
//                    switch (comando.getTipoSolicitud()) {
//                        case ENVIAR_MENSAJE:
//                            enviarMensaje();
//                            break;
//                        case OBTENER_DIRECTORIO:
//                            enviarDirectorio();
//                            break;
//                        case REGISTRARSE:
//                            registrarse();
//                            break;
//                        case INICIAR_SESION:
//                            iniciarSesion();
//                            break;
//                        case CERRAR_SESION:
//                            cerrarSesion();
//                            break;
//                        case AGREGAR_CONTACTO:
//                            agregarContacto();
//                            break;
//                        // case OBTENER_CONVERSACIONES: obtenerConversaciones(); break;
//                        default:
//                            serverLog("Servidor: Comando no reconocido o no manejado: " + comando.getTipoSolicitud(), Servidor.COLOR_ERROR);
//                            break;
//                    }
//                }
//            } catch (IOException | ClassNotFoundException e) {
//                serverLog("Servidor: Error de conexión con " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()) + ": " + e.getMessage(), Servidor.COLOR_ERROR);
//                cerrarTodo(socket, objectInputStream, objectOutputStream);
//                break;
//            } catch (Exception e) {
//                serverLog("Servidor: Excepción inesperada en el hilo del cliente " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()) + ": " + e.getMessage(), Servidor.COLOR_ERROR);
//                e.printStackTrace();
//                cerrarTodo(socket, objectInputStream, objectOutputStream);
//                break;
//            }
//        }
//        serverLog("Servidor: Hilo de cliente terminado para " + (userActual != null ? userActual.getNombreUsuario() : "cliente desconocido"), Servidor.COLOR_INFO);
//    }

    public void shutdown() {
        this.running = false; // Indicar al bucle que se detenga
        serverLog("ClientHandler: Iniciando apagado para " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()), Servidor.COLOR_INFO);
        try {
            if (socket != null && !socket.isClosed()) {
                // Cerrar el socket puede ayudar a desbloquear objectInputStream.readObject()
                // Es importante manejar la SocketException que esto puede causar en run().
                socket.close();
            }
        } catch (IOException e) {
            serverLog("ClientHandler: Error al cerrar socket durante shutdown: " + e.getMessage(), Servidor.COLOR_WARNING);
        }
        // No llames a cerrarTodo() aquí directamente, deja que el bucle run() termine y lo haga.
    }


    @Override
    public void run() {
        Object objetoRecibido;

        // Usar la variable 'running' y verificar el estado del socket
        while (running && socket != null && socket.isConnected() && !socket.isClosed()) {
            try {
                objetoRecibido = objectInputStream.readObject(); // Puede bloquear
                if (!running) break; // Comprobar de nuevo después de desbloquear

                if (objetoRecibido instanceof Comando) {
                    comando = (Comando) objetoRecibido;
                    // ... (tu switch)
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
            } catch (java.net.SocketException se) {
                if (running) { // Solo loguear como error si no es un cierre esperado
                    serverLog("ClientHandler: SocketException con " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()) + ": " + se.getMessage(), Servidor.COLOR_ERROR);
                } else {
                    serverLog("ClientHandler: Socket cerrado (esperado durante shutdown) para " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()), Servidor.COLOR_INFO);
                }
                // Ya no es necesario llamar a cerrarTodo aquí, se hará en el finally
                break; // Salir del bucle
            } catch (java.io.EOFException eofe) {
                // El cliente cerró la conexión abruptamente
                if (running) {
                    serverLog("ClientHandler: Cliente " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()) + " cerró la conexión (EOF).", Servidor.COLOR_WARNING);
                }
                break; // Salir del bucle
            }
            catch (IOException | ClassNotFoundException e) {
                if (running) { // Solo loguear como error si no es un cierre esperado
                    serverLog("Servidor: Error de conexión con " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()) + ": " + e.getMessage(), Servidor.COLOR_ERROR);
                }
                // Ya no es necesario llamar a cerrarTodo aquí, se hará en el finally
                break; // Salir del bucle
            } catch (Exception e) { // Captura genérica, ten cuidado aquí
                if (running) {
                    serverLog("Servidor: Excepción inesperada en el hilo del cliente " + (userActual != null ? userActual.getNombreUsuario() : socket.getRemoteSocketAddress()) + ": " + e.getMessage(), Servidor.COLOR_ERROR);
                    e.printStackTrace(); // Importante para depurar
                }
                // Ya no es necesario llamar a cerrarTodo aquí, se hará en el finally
                break; // Salir del bucle
            }
        } // Fin del while

        // Este bloque finally asegura que cerrarTodo se llama una vez al salir del bucle
        // independientemente de cómo se salió (normal, break, excepción).
        // Pero solo si no fue un cierre ya iniciado por shutdown() que limpia.
        // Sin embargo, es más simple que cerrarTodo se llame siempre al final de run.
        // if (running) { // Si 'running' sigue true, fue una desconexión inesperada
        // }
        cerrarTodo(socket, objectInputStream, objectOutputStream); // Asegurar limpieza

        serverLog("Servidor: Hilo de cliente terminado para " + (userActual != null ? userActual.getNombreUsuario() : "cliente desconocido"), Servidor.COLOR_INFO);
    }
}