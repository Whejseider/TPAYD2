package utils;

import javax.swing.*;
import java.awt.*;

public class ScrollBar extends JScrollBar {

    public ScrollBar() {
        setUI(new ModernScrollBarUI());
        setPreferredSize(new Dimension(10, 10));
        setUnitIncrement(20);
    }
}
