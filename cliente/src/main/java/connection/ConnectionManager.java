package connection;

import interfaces.ConnectionCallBack;
import model.TipoRespuesta;
import raven.modal.Toast;
import view.forms.FormError;
import view.manager.ErrorManager;
import view.system.FormManager;

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

    public TipoRespuesta tryEstablishInitialConnection() {
        Cliente cliente = Cliente.getInstance();
        boolean conectado = cliente.conectarAlServidor();

        if (conectado) {
            return TipoRespuesta.OK;
        } else {
            return TipoRespuesta.ERROR;
        }
    }

    public void attemptReconnectionAndNotify() {
        if (tryEstablishInitialConnection() == TipoRespuesta.OK) {
            checkOnReconnection();

            if (formError != null && formError.isVisible()) {
                formError.setVisible(false);
            }
            Toast.show(FormManager.getFrame(), Toast.Type.SUCCESS, "Reconectado exitosamente");
            FormManager.init();
        } else {
            Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Fallo al reconectar");
            if (formError == null || !formError.isVisible()) {
                showError(this.callBack, true);
            } else {
                if (formError.isVisible()) {
                    formError.showReconnectOptions(true);
                }
            }
        }
    }

    public void showError(ConnectionCallBack callBack, boolean showReconnectOptions) {
        getInstance().callBack = callBack;
        if (formError == null || !formError.isVisible()) {
            ErrorManager.getInstance().showError("Error de conexión");
        }
        FormManager.showError(getFormError(showReconnectOptions));
    }

    private FormError getFormError(boolean showReconnectOptions) {
        if (formError == null) {
            formError = new FormError();
        }
        formError.showReconnectOptions(showReconnectOptions);
        return formError;
    }

    public void checkOnReconnection() {
        if (callBack != null) {
            callBack.onConnected();
            callBack = null;
        }
    }
}