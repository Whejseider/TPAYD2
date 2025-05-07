package view.system;

import interfaces.IController;
import interfaces.IVista;

import javax.swing.*;

public class Form extends JPanel implements IVista {

    @Override
    public void setControlador(IController controlador) {

    }

    private LookAndFeel oldTheme = UIManager.getLookAndFeel();

    public Form() {
        init();
    }

    private void init() {
    }

    public void formInit() {
    }

    public void formOpen() {
    }

    public void formRefresh() {
    }

    protected final void formCheck() {
        if (oldTheme != UIManager.getLookAndFeel()) {
            oldTheme = UIManager.getLookAndFeel();
            SwingUtilities.updateComponentTreeUI(this);
        }
    }

}
