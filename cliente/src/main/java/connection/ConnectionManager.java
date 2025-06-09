package connection;

import interfaces.ConnectionCallBack;
import raven.modal.Toast;
import view.forms.FormError;
import view.manager.ToastManager;
import view.system.FormManager;

public class ConnectionManager {

    private ConnectionCallBack callBack;
    private static ConnectionManager instance;
    private FormError formError;

    private ConnectionManager() {
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public void showError(ConnectionCallBack callBack, boolean showReconnectOptions, String s) {
        getInstance().callBack = callBack;
        ToastManager.getInstance().showToast(Toast.Type.ERROR, s);
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

    public FormError getFormError() {
        return formError;
    }

    public void setFormError(FormError formError) {
        this.formError = formError;
    }

    public ConnectionCallBack getCallBack() {
        return callBack;
    }
}