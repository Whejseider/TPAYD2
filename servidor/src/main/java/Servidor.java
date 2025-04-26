import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static final int PUERTO = 1234;
    public static final String IP = "127.0.0.1";

    private ServerSocket serverSocket;

    public Servidor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void iniciarServidor() {
        try {
            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cerrarSocketServidor() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PUERTO, 50, InetAddress.getByName(IP));
        Servidor servidor = new Servidor(serverSocket);
        System.out.println("Servidor iniciado en IP:PUERTO = " + IP + ":" + PUERTO);
        servidor.iniciarServidor();
    }

}
