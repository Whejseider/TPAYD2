package view.system;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import raven.modal.Drawer;
import view.component.FormSearchButton;
import view.component.MemoryBar;
import view.component.RefreshLine;
import view.icons.SVGIconUIColor;

import javax.swing.*;
import java.awt.*;

public class MainForm extends Form {

    public MainForm() {
        init();
    }

    private void init() {
        setName("MainForm");
        setLayout(new MigLayout("fillx,wrap,insets 0,gap 0", "[fill]", "[][][fill,grow][]"));
        add(createHeader());
        add(createRefreshLine(), "height 3!");
        add(createMain());
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 3", "[]push[]push", "[fill]"));
        JToolBar toolBar = new JToolBar();
        JButton buttonDrawer = new JButton(new FlatSVGIcon("fv/icons/menu.svg", 0.5f));
        buttonUndo = new JButton(new FlatSVGIcon("fv/icons/undo.svg", 0.5f));
        buttonRedo = new JButton(new FlatSVGIcon("fv/icons/redo.svg", 0.5f));
        buttonRefresh = new JButton(new FlatSVGIcon("fv/icons/refresh.svg", 0.5f));

        buttonDrawer.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:10;");
        buttonUndo.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:10;");
        buttonRedo.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:10;");
        buttonRefresh.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:10;");

        buttonDrawer.addActionListener(e -> {
            if (Drawer.isOpen()) {
                Drawer.showDrawer();
            } else {
                Drawer.toggleMenuOpenMode();
            }
        });
        buttonUndo.addActionListener(e -> FormManager.undo());
        buttonRedo.addActionListener(e -> FormManager.redo());
        buttonRefresh.addActionListener(e -> FormManager.refresh());

        toolBar.add(buttonDrawer);
        toolBar.add(buttonUndo);
        toolBar.add(buttonRedo);
        toolBar.add(buttonRefresh);
        panel.add(toolBar);
        panel.add(createSearchBox(), "gapx n 135");
        return panel;
    }

    private JPanel createSearchBox() {
        JPanel panel = new JPanel(new MigLayout("fill", "[fill,center,200:250:]", "[fill]"));
        FormSearchButton button = new FormSearchButton();
        button.addActionListener(e -> FormSearch.getInstance().showSearch());
        panel.add(button);
        return panel;
    }

    private JPanel createRefreshLine() {
        refreshLine = new RefreshLine();
        return refreshLine;
    }

    private Component createMain() {
        mainPanel = new JPanel(new BorderLayout());
        return mainPanel;
    }

    public void setForm(Form form) {
        mainPanel.removeAll();
        mainPanel.add(form);
        mainPanel.repaint();
        mainPanel.revalidate();

        // verificar bton
        buttonUndo.setEnabled(FormManager.FORMS.isUndoAble());
        buttonRedo.setEnabled(FormManager.FORMS.isRedoAble());
        // verificar la orientacion del componente y actualizar
        if (mainPanel.getComponentOrientation().isLeftToRight() != form.getComponentOrientation().isLeftToRight()) {
            applyComponentOrientation(mainPanel.getComponentOrientation());
        }
    }

    public void refresh() {
        refreshLine.refresh();
    }

    private JPanel mainPanel;
    private RefreshLine refreshLine;

    private JButton buttonUndo;
    private JButton buttonRedo;
    private JButton buttonRefresh;
}
