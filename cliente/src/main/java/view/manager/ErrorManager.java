package view.manager;

import raven.modal.Toast;
import view.system.FormManager;

public class ErrorManager {

    private static ErrorManager instance;

    public static ErrorManager getInstance() {
        if (instance == null) {
            instance = new ErrorManager();
        }
        return instance;
    }

    public void showError(String s) {
        Toast.show(FormManager.getFrame(), Toast.Type.ERROR, s);
    }
}

