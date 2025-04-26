import model.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientesConectados = new ArrayList<>();
    public static ArrayList<User> directorio = new ArrayList<>();
    private Socket socket;
    private ObjectInputStream objectInputStream; //Entrada
    private ObjectOutputStream objectOutputStream; //Salida
    private Comando comando;
    private User user;

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

    private void iniciarSesion() throws IOException {
        user = (User) comando.getContenido();
        if (directorio.stream().noneMatch(u -> u.getNombreUsuario().equalsIgnoreCase(user.getNombreUsuario()))) {
            System.out.println("Servidor: Error al iniciar sesión: " + user.getNombreUsuario());
            Comando c = new Comando(TipoSolicitud.INICIAR_SESION, TipoRespuesta.ERROR, "El usuario es inexistente");
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } else {
            if (clientesConectados.stream().noneMatch(
                    c -> c.user.getNombreUsuario().equalsIgnoreCase(user.getNombreUsuario()))) {
                clientesConectados.add(this);
                System.out.println("Servidor: Usuario conectado: " + user.getNombreUsuario());
                Comando c = new Comando(TipoSolicitud.INICIAR_SESION, TipoRespuesta.OK, user);
                objectOutputStream.writeObject(c);
                objectOutputStream.flush();
            } else {
                System.out.println("Servidor: Error al iniciar sesión: " + user.getNombreUsuario());
                Comando c = new Comando(TipoSolicitud.INICIAR_SESION, TipoRespuesta.ERROR, "Ya hay un usuario conectado con ese nombre");
                objectOutputStream.writeObject(c);
                objectOutputStream.flush();
            }
        }
    }

    private void registrarse() throws IOException {
        user = (User) comando.getContenido();
        if (directorio.stream().noneMatch(u -> u.getNombreUsuario().equalsIgnoreCase(user.getNombreUsuario()))) {
            directorio.add(user);
            System.out.println("Servidor: Usuario registrado: " + user.getNombreUsuario());
            Comando c = new Comando(TipoSolicitud.REGISTRARSE, TipoRespuesta.OK);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } else {
            System.out.println("Servidor: El usuario ya existe.");
            Comando c = new Comando(TipoSolicitud.REGISTRARSE, TipoRespuesta.ERROR);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        }
    }

    @Override
    public void run() {
        // Lógica del hilo para recibir y reenviar mensajes
        Object objetoRecibido;

        while (socket.isConnected()) {
            try {
                objetoRecibido = objectInputStream.readObject();
                if (objetoRecibido instanceof Comando) {
                    comando = (Comando) objetoRecibido;
                    switch (comando.getTipoSolicitud()) {
                        case ENVIAR_MENSAJE -> {
                            Mensaje msg = (Mensaje) comando.getContenido();
                            enviarMensaje(msg);
                        }
                        case LISTA_USUARIOS -> {
//                            enviarListaUsuarios();
                        }
                        case OBTENER_DIRECTORIO -> {
                        }
                        case REGISTRARSE -> { // no va

                            registrarse();
                        }
                        case INICIAR_SESION -> { // nno va
                            iniciarSesion();
                        }
//                        case AGREGAR_CONTACTO {
//
//                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                cerrarTodo(socket, objectInputStream, objectOutputStream);
                break;
            }
        }
    }

    public void enviarMensaje(Mensaje mensaje) {
        for (ClientHandler clienteConectado : clientesConectados) {
            try {
                /**
                 * Hacer que verifique si existe el usuario registrado
                 * despues verifico si esta en linea y se lo mando, y si no
                 * guardo el mensaje en un array
                 * crear metodos, despues veo
                 */
                User receptor = mensaje.getReceptor().getUser();
                if (clienteConectado.user.getNombreUsuario().equalsIgnoreCase(receptor.getNombreUsuario())) {
                    clienteConectado.objectOutputStream.writeObject(mensaje);
                    clienteConectado.objectOutputStream.flush();
                }
            } catch (Exception e) {
                cerrarTodo(socket, objectInputStream, objectOutputStream);
            }
        }
    }

    public void removeClienteConectado() {
        clientesConectados.remove(this);
        System.out.println("SERVIDOR: El usuario " + this.user.getNombreUsuario() + " se desconectó");
    }

    private void cerrarTodo(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        removeClienteConectado();

        try {

            if (objectInputStream != null) {
                objectInputStream.close();
            }

            if (objectOutputStream != null) {
                objectOutputStream.close();
            }

            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}
