package connection;

import interfaces.ConnectionCallBack;
import model.Comando;
import model.TipoRespuesta;
import model.TipoSolicitud;
import raven.modal.Toast;
import view.forms.FormError;
import view.system.FormManager;

import java.io.IOException;
import java.net.Socket;

public class ConnectionManager {

    private ConnectionCallBack callBack;
    private static ConnectionManager instance;
    private FormError formError;

    private ConnectionManager() {}

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public TipoRespuesta checkConnection() {
        try {
            Socket socket = new Socket(Cliente.IP, Cliente.PUERTO);
            Cliente cliente = Cliente.getInstance();
            cliente.init(socket);
//            Esto deberia de cambiarlo
            //Para que el server mande la respuesta de si hay conexion o no
            return TipoRespuesta.OK;
        } catch (IOException e) {
            return TipoRespuesta.ERROR;
        }
    }

    public void showError(ConnectionCallBack callBack, boolean showReconnectButton) {
        getInstance().callBack = callBack;
        Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Connection error");
        FormManager.showError(getFormError(showReconnectButton));
    }

    private FormError getFormError(boolean showReconnectButton) {
        if (formError == null) {
            formError = new FormError();
        }
        formError.showReconnectButton(showReconnectButton);
        return formError;
    }

    public void checkOnReconnection() {
        if (callBack != null) {
            callBack.onConnected();
            callBack = null;
        }
    }
}

