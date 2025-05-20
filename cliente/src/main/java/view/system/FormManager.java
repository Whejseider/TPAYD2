package view.system;


import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import connection.ConnectionManager;
import connection.Sesion;
import factory.ControllerFactory;
import interfaces.IController;
import model.TipoRespuesta;
import raven.modal.Drawer;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import utils.UndoRedo;
import view.component.About;
import view.drawer.MenuDrawer;
import view.forms.FormError;
import view.forms.FormRegister;
import view.forms.FormLogin;
import view.forms.MessengerPanel;

import javax.swing.*;

public class FormManager {

    protected static final UndoRedo<Form> FORMS = new UndoRedo<>();
    private static JFrame frame;
    private static MainForm mainForm;
    private static FormLogin formLogin;
    private static FormRegister register;
    private static Form lastFormBeforeError;

    public static void install(JFrame f) {
        frame = f;
        install();
        init();
    }

    public static void init() {
        boolean logged = Sesion.getInstance().getUsuarioActual() != null;
        if (logged) {
            System.out.println("FormManager: Usuario ya logueado, mostrando home.");
            showHome();
        } else {
            System.out.println("FormManager: No hay sesión activa, verificando conexión inicial...");
            TipoRespuesta respuestaConexion = ConnectionManager.getInstance().tryEstablishInitialConnection();

            if (respuestaConexion == TipoRespuesta.OK) {
                System.out.println("FormManager: Conexión OK, mostrando login.");
                showLogin();
            } else {
                System.out.println("FormManager: Falló la conexión inicial, mostrando error.");
//                ConnectionManager.getInstance().showError(() -> {
//                    System.out.println("FormManager: Reconexión exitosa desde pantalla de error, mostrando login.");
//                    showLogin();
//                }, true);
            }
        }
    }

    private static void install() {
        FormSearch.getInstance().installKeyMap(getMainForm());
    }

    public static void clearForms() {
        FORMS.clear();
        mainForm = null;
        register = null;
        formLogin = null;
        frame.repaint();
        frame.revalidate();
    }

    public static void showForm(Form form) {
            if (form != FORMS.getCurrent()) {
                FlatAnimatedLafChange.showSnapshot();
                FORMS.add(form);
                form.formCheck();
                form.formOpen();
                mainForm.setForm(form);
                mainForm.refresh();
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
            }
    }

    public static void undo() {
        if (FORMS.isUndoAble()) {
            Form form = FORMS.undo();
            form.formCheck();
            form.formOpen();
            mainForm.setForm(form);
            Drawer.setSelectedItemClass(form.getClass());
        }
    }

    public static void redo() {
        if (FORMS.isRedoAble()) {
            Form form = FORMS.redo();
            form.formCheck();
            form.formOpen();
            mainForm.setForm(form);
            Drawer.setSelectedItemClass(form.getClass());
        }
    }

    public static void refresh() {
        if (FORMS.getCurrent() != null) {
            FORMS.getCurrent().formRefresh();
            mainForm.refresh();
        }
    }

    public static void showHome() {
            MenuDrawer.getInstance().setVisible(true);
            MenuDrawer.getInstance().setDrawerHeader(Sesion.getInstance().getUsuarioActual());
            frame.getContentPane().removeAll();
            frame.getContentPane().add(getMainForm());
            Drawer.setSelectedItemClass(MessengerPanel.class);
            frame.repaint();
            frame.revalidate();
    }

    public static void showLogin() {
            MenuDrawer.getInstance().setVisible(false);
            frame.getContentPane().removeAll();
            Form login = getLogin();
            login.formCheck();
            frame.getContentPane().add(login);
            FORMS.clear();
            frame.repaint();
            frame.revalidate();
    }

    public static void showRegister() {
            MenuDrawer.getInstance().setVisible(false);
            frame.getContentPane().removeAll();
            Form register = getRegister();
            register.formCheck();
            frame.getContentPane().add(register);
            FORMS.clear();
            frame.repaint();
            frame.revalidate();
    }

    private static Form getRegister() {
        if (register == null) {
            register = new FormRegister();
            ControllerFactory controllerFactory = new ControllerFactory();
            IController controller = controllerFactory.getController(register.getName(), register);
            register.setControlador(controller);
            controller.init();
        }
        return register;
    }

    public static void showError(FormError formError) {
            lastFormBeforeError = FORMS.getCurrent();
            MenuDrawer.getInstance().setVisible(false);
            frame.getContentPane().removeAll();
            formError.formCheck();
            frame.getContentPane().add(formError);
            FORMS.clear();
            frame.repaint();
            frame.revalidate();
    }

    public static JFrame getFrame() {
        return frame;
    }

    public static MainForm getMainForm() {
        if (mainForm == null) {
            mainForm = new MainForm();
            ControllerFactory controllerFactory = new ControllerFactory();
            IController controller = controllerFactory.getController(mainForm.getName(), mainForm);
            mainForm.setControlador(controller);
            controller.init();
        }
        return mainForm;
    }

    private static FormLogin getLogin() {
        if (formLogin == null) {
            formLogin = new FormLogin();
            ControllerFactory controllerFactory = new ControllerFactory();
            IController controller = controllerFactory.getController(formLogin.getName(), formLogin);
            formLogin.setControlador(controller);
            controller.init();
        }
        return formLogin;
    }

    public static void showAbout() {
        ModalDialog.showModal(frame, new SimpleModalBorder(new About(), "Acerca de"),
                ModalDialog.createOption().setAnimationEnabled(false)
        );
    }

    public static void restorePreviousForm(){
        if (lastFormBeforeError != null) {
            showForm(lastFormBeforeError);
            lastFormBeforeError = null;
        } else {
            init();
        }
    }
}
