package com.hoomoomoo.im;

import javax.swing.*;
import java.awt.*;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2020/11/21
 */
public class WindowMain {

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("小布局...");
//        borderLayout(jFrame);
//        flowLayout(jFrame);
//        gridLayout(jFrame);
        nullLayout(jFrame);
        jFrame.setSize(500, 300);
        jFrame.setLocation(300, 200);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.setResizable(true);
    }

    /**
     * 边框布局
     *
     * @param jFrame
     * @author: humm23693
     * @date: 2020/11/21
     * @return:
     */
    private static void borderLayout(JFrame jFrame) {
        jFrame.setLayout(new BorderLayout(1, 1));
        jFrame.add(new JButton("北"), BorderLayout.NORTH);
        jFrame.add(new JButton("东"), BorderLayout.EAST);
        jFrame.add(new JButton("南"), BorderLayout.SOUTH);
        jFrame.add(new JButton("西"), BorderLayout.WEST);
        jFrame.add(new JButton("中"));
    }

    /**
     * 流式布局
     *
     * @param jFrame
     * @author: humm23693
     * @date: 2020/11/21
     * @return:
     */
    private static void flowLayout(JFrame jFrame) {
        jFrame.setLayout(new FlowLayout());
        for (int i = 0; i < 6; i++) {
            jFrame.add(new JButton("按钮" + i));
        }
    }

    /**
     * 网格布局
     *
     * @param jFrame
     * @author: humm23693
     * @date: 2020/11/21
     * @return:
     */
    private static void gridLayout(JFrame jFrame) {
        jFrame.setLayout(new GridLayout(3, 3, 10, 10));
        for (int i = 0; i < 9; i++) {
            jFrame.add(new JButton("按钮" + i));
        }
    }

    /**
     * 绝对位置布局
     *
     * @param jFrame
     * @author: humm23693
     * @date: 2020/11/21
     * @return:
     */
    private static void nullLayout(JFrame jFrame) {
        jFrame.setLayout(null);
        JButton button1 = new JButton("按钮1");
        button1.setBounds(10, 10, 70, 25);
        jFrame.add(button1);
        JButton button2 = new JButton("按钮2");
        button2.setBounds(50, 50, 70, 25);
        jFrame.add(button2);
        JButton button3 = new JButton("按钮3");
        button3.setBounds(100, 100, 70, 25);
        jFrame.add(button3);
    }
}
