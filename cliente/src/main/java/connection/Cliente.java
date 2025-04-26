package connection;

import controller.MainController;
import interfaces.ClientListener;
import model.Comando;
import model.Mensaje;
import model.TipoSolicitud;
import model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente {

    public static final int PUERTO = 1234; //default
    public static final String IP = "127.0.0.1"; //default

    private Socket socket;
    private ObjectInputStream objectInputStream; //Entrada
    private ObjectOutputStream objectOutputStream; //Salida
    private Comando comando;
    private ClientListener clientListener = MainController.getInstance();
    public static Cliente cliente;

    private Cliente() {
    }

    public static Cliente getInstance() {
        if (cliente == null) {
            cliente = new Cliente();
        }
        return cliente;
    }

    public void init(Socket socket) {
        this.socket = socket;
        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
//            Comando c = new Comando(TipoSolicitud.CONECTARSE_SERVIDOR);
//            objectOutputStream.writeObject(c);
//            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo(socket, objectInputStream, objectOutputStream);
        }
    }


    //TODO: Hacerlo pasandole COMANDO oconvertirlo aca, ver que onda capaz mas facil hacerlo aca
    public void enviarMensaje(Mensaje mensaje) {
        try {
            Comando c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, mensaje);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo(socket, objectInputStream, objectOutputStream);
        }
    }

    public void registrarse(User user) {
        try {
            Comando c = new Comando(TipoSolicitud.REGISTRARSE, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo(socket, objectInputStream, objectOutputStream);

        }
    }

    public void iniciarSesion(User user){
        try {
            Comando c = new Comando(TipoSolicitud.INICIAR_SESION, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo(socket, objectInputStream, objectOutputStream);
        }
    }

    public void cerrarSesion(User user){
        try {
            Comando c = new Comando(TipoSolicitud.CERRAR_SESION, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {}
    }


    /**
     * Starts a thread to continuously listen for responses from the server.
     *
     * This method reads objects from the input stream connected to the server,
     * casting them to the expected type, and passes them to the client listener
     * for handling. If an exception occurs during this process, such as when the
     * connection is lost, the relevant resources (socket, input and output streams)
     * are closed to release resources and avoid potential leaks.
     *
     * Behavior:
     * - Continuously listens for incoming serialized objects from the server.
     * - Parses the received data into a {@code Comando} object and notifies the
     *   {@code ClientListener} via the {@code onResponse} method.
     * - Automatically terminates on encountering a disconnection or error,
     *   ensuring proper resource cleanup by calling {@code cerrarTodo}.
     */
    public void escuchar() {
        new Thread(() -> {
            Comando respuesta;

            while (socket.isConnected()) {
                try {
                    respuesta = (Comando) objectInputStream.readObject();
                    clientListener.onResponse(respuesta);
                } catch (Exception e) {
                    cerrarTodo(socket, objectInputStream, objectOutputStream);
                }
            }
        }).start();
    }

    private void cerrarTodo(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ClientListener getClientListener() {
        return clientListener;
    }

    public void setClientListener(ClientListener clientListener) {
        this.clientListener = clientListener;
    }

    public Comando getComando() {
        return comando;
    }

    public void setComando(Comando comando) {
        this.comando = comando;
    }
}
