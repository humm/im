package com.hoomoomoo.im.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author hoomoomoo
 * @description 文件工具类
 * @package com.hoomoomoo.im.ui
 * @date 2020/09/28
 */
public class ImFileUtils {

    public static void main(String[] args) {
        formatUi();
        JFrame main = new JFrame("边界布局");
        main.setResizable(true);
        main.setLocationRelativeTo(null);
        main.setLayout(new BoxLayout(main.getContentPane(), BoxLayout.Y_AXIS));
        main.add(initMode(), BorderLayout.EAST);
        main.getContentPane().add(Box.createVerticalStrut(10));
        //设置大小
        main.setSize(600, 300);
        //自动调整大小
//        main.pack();
        //关闭
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //初始可见
        main.setVisible(true);
        // 居中显示
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int) (toolkit.getScreenSize().getWidth() - main.getWidth()) / 2;
        int y = (int) (toolkit.getScreenSize().getHeight() - main.getHeight()) / 2;
        main.setLocation(x, y);
    }

    private static JPanel initMode() {
        JPanel mode = new JPanel();
        mode.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton btn1 = new JButton("one");
        JButton btn2 = new JButton("two");
        JButton btn3 = new JButton("three");
        JButton btn4 = new JButton("four");
        JButton btn5 = new JButton("five");
        mode.add(btn1);
        mode.add(btn2);
        mode.add(btn3);
        mode.add(btn4);
        mode.add(btn5);
        return mode;
    }

    private static JPanel initVersion() {
        return null;
    }

    private static JPanel initWorkspace() {
        return null;
    }

    private static void formatUi() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Font font = new Font("Dialog", Font.BOLD, 12);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }
}
