package view.system;


import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import connection.Cliente;
import connection.ConnectionManager;
import connection.Sesion;
import controller.LoginController;
import controller.MainFormController;
import controller.RegisterController;
import model.TipoRespuesta;
import raven.modal.Drawer;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import utils.UndoRedo;
import view.component.About;
import view.drawer.MenuDrawer;
import view.forms.FormError;
import view.forms.FormRegister;
import view.forms.Login;
import view.forms.MessengerPanel;

import javax.swing.*;

public class FormManager {

    protected static final UndoRedo<Form> FORMS = new UndoRedo<>();
    private static JFrame frame;
    private static MainForm mainForm;
    private static Login login;
    private static FormRegister register;

    public static void install(JFrame f) {
        frame = f;
        install();
        init();
    }

    public static void init() {
        boolean logged = Sesion.getInstance().getUsuarioActual() != null;
        if (logged) {
            showHome();
        } else {
            TipoRespuesta tipoRespuesta = ConnectionManager.getInstance().checkConnection();
            if (tipoRespuesta == TipoRespuesta.OK) {
                showLogin();
                Cliente.getInstance().escuchar();
            } else {
                ConnectionManager.getInstance().showError(() -> showLogin(), true);
            }
        }
    }

    private static void install() {
        FormSearch.getInstance().installKeyMap(getMainForm());
        MainFormController mainFormController = new MainFormController();
    }

    public static void showForm(Form form) {
        SwingUtilities.invokeLater(() -> {
            if (form != FORMS.getCurrent()) {
                FlatAnimatedLafChange.showSnapshot();
                FORMS.add(form);
                form.formCheck();
                form.formOpen();
                mainForm.setForm(form);
                mainForm.refresh();
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
            }
        });
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

    //TODO CAMbiar nombres porque me equivoque este y logout, estan al revés
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
            RegisterController registerController = new RegisterController(register);
        }
        return register;
    }

    public static void showError(FormError formError) {
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
        }
        return mainForm;
    }

    private static Login getLogin() {
        if (login == null) {
            login = new Login();
            LoginController loginController = new LoginController(login);
        }
        return login;
    }

    public static void showAbout() {
        ModalDialog.showModal(frame, new SimpleModalBorder(new About(), "Acerca de"),
                ModalDialog.createOption().setAnimationEnabled(false)
        );
    }
}
