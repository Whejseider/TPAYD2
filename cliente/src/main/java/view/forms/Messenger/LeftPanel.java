package view.forms.Messenger;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import connection.Sesion;
import model.Contacto;
import model.Conversacion;
import model.Mensaje;
import model.User;
import net.miginfocom.swing.MigLayout;
import plugin.ScrollRefresh;
import plugin.ScrollRefreshModel;
import utils.Debounce;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LeftPanel extends JPanel {
    private String textSearch;
    private JButton btnNuevoChat;
    private ScrollRefresh scroll;
    private JPanel header;
    private JPanel panel;
    private LeftActionListener event;

    public LeftPanel() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fill,insets 0 0 3 0", "[fill,270::]", "[grow 0]0[fill]"));
        panel = new JPanel(new MigLayout("wrap,fillx,gapy 3", "[fill]"));
        scroll = new ScrollRefresh(createScrollRefreshModel(), panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:4");
        createHeader();
        add(scroll);
    }

    public void initData() {
        panel.removeAll();
        panel.repaint();
        panel.revalidate();
        scroll.getScrollRefreshModel().resetPage();
    }

    public void setEvent(LeftActionListener event) {
        this.event = event;
    }

    private ScrollRefreshModel createScrollRefreshModel() {
        return new ScrollRefreshModel(1, SwingConstants.BOTTOM) {
            @Override
            public boolean onRefreshNext() {
                return loadData();
            }

            @Override
            public void onFinishRefresh() {
                repaint();
                revalidate();
            }

            @Override
            public void onFinishData() {

            }

            @Override
            public void onError(Exception e) {

            }
        };
    }

    private void createHeader() {
        header = new JPanel(new MigLayout("fillx, wrap 2", "[grow][right]", "[]10[]"));

        JLabel lblTitle = new JLabel("CHATS");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));

        btnNuevoChat = new JButton(new FlatSVGIcon("fv/icons/copy.svg", 0.5f));
        btnNuevoChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevoChat.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "background:null");

        JTextField text = new JTextField();
        text.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar un chat");
        text.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:5,10,5,10;" +
                "borderWidth:0;" +
                "background:darken($Panel.background,2%);");
        SwingUtilities.invokeLater(text::requestFocusInWindow);

        Debounce.add(text, (ke, search) -> {
            search = search.trim();
            textSearch = search.isEmpty() ? null : search;
            initData();
        }, 300);

        header.add(lblTitle, "growx");
        header.add(btnNuevoChat);

        header.add(text, "span 2, growx");

        add(header);
    }

    public void userMessage(Conversacion conversacion, Mensaje lastMessage) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item item) {
                if (item.getData().equals(conversacion)  ) {
                    item.setLastMessage(lastMessage);
                    panel.setComponentZOrder(component, 0);
                    panel.revalidate();
                    break;
                }
            }
        }
    }

    public void selectedConversation(Conversacion data) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item item) {
                item.setSelected(item.getData().equals(data));
            }
        }
    }

    public Item getSelectedConversation(Conversacion data) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item item) {
                if (item.getData().equals(data)) {
                    return item;
                }
            }
        }
        return null;
    }

    public boolean loadData() {
        try {
            Map<String, Conversacion> response = Sesion.getInstance().getUsuarioActual().getConversaciones();
            String filter = textSearch != null ? textSearch.toLowerCase() : null;

            for (Conversacion d : response.values()) {
                if (filter != null) {
                    boolean coincidePorNombre = d.getContacto().getNombreUsuario().toLowerCase().contains(filter);
                    boolean coincidePorMensaje = false;

                    for (Mensaje m : d.getMensajes()) {
                        if (m.getContenido().toLowerCase().contains(filter)) {
                            coincidePorMensaje = true;
                            break;
                        }
                    }

                    if (!coincidePorNombre && !coincidePorMensaje) {
                        continue;
                    }
                }

                if (isNotExist(d)) {
                    Item item = new Item(d);
                    item.addActionListener(e -> event.onConversationSelected(d));
                    panel.add(item);
                }
            }

            return !response.isEmpty();
        } catch (Exception e) {
            System.out.println("LeftPanel (LOADDATA): " + e);
            return false;
        } finally {
            panel.repaint();
            panel.revalidate();
        }
    }

    private boolean isNotExist(Conversacion data) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item item) {
                if (item.getData().equals(data) ) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getTextSearch() {
        return textSearch;
    }

    public ScrollRefresh getScroll() {
        return scroll;
    }

    public JPanel getHeader() {
        return header;
    }

    public JPanel getPanel() {
        return panel;
    }

    public JButton getBtnNuevoChat() {
        return btnNuevoChat;
    }
}

