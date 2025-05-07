package connection;

import controller.ClientManager;
import interfaces.ClientListener;
import model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Debo separar responsabilidades de acceso y luego de uso del usuario, pero
 * no me queda mucho tiempo
 *
 */
public class Cliente {

    public static final int PUERTO = 1234; //default
    public static final String IP = "127.0.0.1"; //default

    private Socket socket;
    private ObjectInputStream objectInputStream; //Entrada
    private ObjectOutputStream objectOutputStream; //Salida
    private Comando comando;
    private ClientListener clientListener = ClientManager.getInstance();
    public static Cliente cliente;

    private volatile boolean activo = true;


    private Cliente() {
    }

    public static Cliente getInstance() {
        if (cliente == null ) {
            cliente = new Cliente();
        }
        return cliente;
    }

    public void init(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
//            Comando c = new Comando(TipoSolicitud.CONECTARSE_SERVIDOR);
//            objectOutputStream.writeObject(c);
//            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo();
        }
    }



    public void enviarMensaje(Mensaje mensaje) {
        try {
            Comando c = new Comando(TipoSolicitud.ENVIAR_MENSAJE, mensaje);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo();
        }
    }

    public void registrarse(User user) {
        try {
            Comando c = new Comando(TipoSolicitud.REGISTRARSE, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo();

        }
    }

    public void iniciarSesion(User user){
        try {
            Comando c = new Comando(TipoSolicitud.INICIAR_SESION, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo();
        }
    }

    public void cerrarSesion(User user){
        try {
            Comando c = new Comando(TipoSolicitud.CERRAR_SESION, user);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo();
        }
    }

    public void obtenerDirectorio(){
        try {
            Comando c = new Comando(TipoSolicitud.OBTENER_DIRECTORIO);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo();
        }
    }

    public void agregarContacto(Contacto contacto) {
        try {
            Comando c = new Comando(TipoSolicitud.AGREGAR_CONTACTO, contacto);
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
        } catch (IOException e) {
            cerrarTodo();
        }
    }

    /**
     * Escucha conexiones del servidor
     * luego las gestiona con el patrón listener
     */
    public void escuchar() {
        activo = true;

        new Thread(() -> {
            Comando respuesta;

            while (activo && socket != null && socket.isConnected()) {
                try {
                    respuesta = (Comando) objectInputStream.readObject();
                    clientListener.onResponse(respuesta);
                } catch (Exception e) {
                    cerrarTodo();
                }
            }
        }).start();
    }

    public void cerrarTodo() {
        activo = false;

        try {

            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            objectInputStream = null;
            objectOutputStream = null;
            socket = null;
            comando = null;
        }
    }

    public static void clearInstance() {
        if (cliente != null) {
            cliente.cerrarTodo();
        }
        cliente = null;
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
