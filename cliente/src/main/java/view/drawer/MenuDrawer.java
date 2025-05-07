package view.drawer;

import model.User;
import raven.modal.Drawer;
import raven.modal.drawer.simple.header.SimpleHeader;
import raven.modal.drawer.simple.header.SimpleHeaderData;
import view.system.FormManager;

import javax.swing.*;

public class MenuDrawer {

    private static MenuDrawer instance;
    private MyDrawerBuilder drawerBuilder;

    public static MenuDrawer getInstance() {
        if (instance == null) {
            instance = new MenuDrawer();
        }
        return instance;
    }

    public static void clearInstance() {
        if (instance != null)
            instance = null;
    }

    private MenuDrawer() {
        drawerBuilder = new MyDrawerBuilder();
        Drawer.installDrawer(FormManager.getFrame(), drawerBuilder);
    }

    public void showDrawer() {
        SwingUtilities.invokeLater(Drawer::showDrawer);
    }

    public void closeDrawer() {
        Drawer.closeDrawer();
    }

    public void setVisible(boolean v) {
        Drawer.setVisible(v);
    }

    public void setDrawerHeader(User user) {
        SimpleHeader header = (SimpleHeader) drawerBuilder.getHeader();
        SimpleHeaderData data = header.getSimpleHeaderData();
        if (user != null) {
//            data.setIcon(NetworkDataUtil.getNetworkIcon(profile.getProfile(), profile.getName().getProfileString(), 50, 50, 999));
            data.setTitle(user.getNombreUsuario());
            data.setDescription(user.getIP() + ":" + user.getPuerto());
        } else {
            data.setTitle("-");
            data.setDescription("-");
            data.setIcon(null);
        }
        header.setSimpleHeaderData(data);
    }
}
