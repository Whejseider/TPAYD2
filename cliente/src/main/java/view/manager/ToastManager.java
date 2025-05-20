package view.manager;

import model.Contacto;
import raven.modal.Toast;
import raven.modal.option.Location;
import raven.modal.toast.option.ToastBorderStyle;
import raven.modal.toast.option.ToastLocation;
import raven.modal.toast.option.ToastOption;
import raven.modal.toast.option.ToastStyle;
import view.simple.SimpleCustomToast;
import view.system.FormManager;

public class ToastManager {
    private static ToastManager instance;

    public static ToastManager getInstance() {
        if (instance == null) {
            instance = new ToastManager();
        }
        return instance;
    }

    private static ToastOption getToastOption() {
        ToastOption option = Toast.createOption();

        option.setAnimationEnabled(true)
                .setPauseDelayOnHover(true)
                .setAutoClose(true)
                .setCloseOnClick(false)
                .setHeavyWeight(false);

        option.getLayoutOption()
                .setLocation(ToastLocation.from(Location.CENTER, Location.TOP))
                .setRelativeToOwner(true);

        option.getStyle().setBackgroundType(ToastStyle.BackgroundType.DEFAULT)
                .setShowCloseButton(true)
                .setShowLabel(false)
                .setIconSeparateLine(false)
                .setShowCloseButton(true)
                .setPaintTextColor(false)
                .setPromiseLabel("...")
                .getBorderStyle()
                .setBorderType(ToastBorderStyle.BorderType.NONE);
        return option;
    }

    public void showToast(Toast.Type type, String text) {
        ToastOption option = getToastOption();

        Toast.show(FormManager.getFrame(), type, text, option);
    }

    //Por si quiero mostrar notificaciones de mensajes
    //Cuando no estoy en la pantalla de mensajeria
    public void showNotifyMessage(Contacto contacto) {
        ToastOption option = getToastOption();

        Toast.showCustom(FormManager.getMainForm(), new SimpleCustomToast(contacto), option);
    }


}
