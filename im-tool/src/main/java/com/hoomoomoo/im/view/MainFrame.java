package com.hoomoomoo.im.view;

import javax.swing.*;
import java.awt.*;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.utils.ui
 * @date 2020/09/13
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        this.setSize(300, 300);
        this.setVisible(true);
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        int x = (int) (toolkit.getScreenSize().getWidth() - this.getWidth()) / 2;

        int y = (int) (toolkit.getScreenSize().getHeight() - this.getHeight()) / 2;

        this.setLocation(x, y);
    }
}
