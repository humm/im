package com.hoomoomoo.im.utils.ui;

import javax.swing.*;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.utils.ui
 * @date 2020/09/13
 */
public class xialakuang extends JFrame {

    private JComboBox comboBox;//定义一个组合框

    public void xia() {

        //JPanel panel = new JPanel();//创建一个JPanel面板
        comboBox = new JComboBox();
        comboBox.addItem("在线");
        comboBox.addItem("隐身");
        comboBox.addItem("离开");

        this.add(comboBox);

        //this.add(panel);
        this.setSize(200, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
