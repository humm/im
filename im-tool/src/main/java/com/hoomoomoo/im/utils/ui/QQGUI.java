package com.hoomoomoo.im.utils.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.utils.ui
 * @date 2020/09/13
 */
public class QQGUI extends JFrame implements ActionListener {

    private JLabel userLa;
    private JLabel pwdLa;
    private JLabel verCodeLa;//验证码
    private JTextField userTxt;
    private JPasswordField pwdTxt;
    private JTextField verCodeTxt;//验证码
    private JButton sureBt;
    private JButton quitBt;
    private Mypanel mp;

    //构造方法
    public QQGUI() {
        Init();
    }

    public void Init() {
      /*  try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/

        Font font = new Font("Dialog", Font.BOLD, 12);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }

        //用户文本
        userLa = new JLabel();
        userLa.setText("用户名：");
        userLa.setSize(60, 50);
        userLa.setLocation(100, 80);

        //密码文本
        pwdLa = new JLabel();
        pwdLa.setText("密码：");
        pwdLa.setSize(50, 50);
        pwdLa.setLocation(100, 120);

        //用户输入框
        userTxt = new JTextField();
        userTxt.setSize(100, 20);
        userTxt.setLocation(170, 95);

        //密码输入框
        pwdTxt = new JPasswordField();
        pwdTxt.setSize(100, 20);
        pwdTxt.setLocation(170, 135);

        //确认按钮
        sureBt = new JButton("登录");
        sureBt.setSize(60, 25);
        sureBt.setLocation(135, 260);

        //退出按钮
        quitBt = new JButton("退出");
        quitBt.setSize(60, 25);
        quitBt.setLocation(240, 260);

       /* //验证码文本
        verCodeLa = new JLabel();
        verCodeLa.setText("验证码：");
        verCodeLa.setSize(60, 50);
        verCodeLa.setLocation(100, 165);*/

        //验证码文本框
       /* verCodeTxt = new JTextField();
        verCodeTxt.setSize(100, 20);
        verCodeTxt.setLocation(170, 180);
*/
       /* //验证码
        mp = new Mypanel();
        mp.setSize(100, 30);
        mp.setLocation(280, 175);*/

        //登录方式选择框
        JComboBox xlk = new JComboBox();
        xlk.setSize(60, 20);
        xlk.setLocation(250, 220);
        xlk.addItem("在线");
        xlk.addItem("隐身");
        xlk.addItem("离开");

        //登录方式选择框
        JTextArea textArea = new JTextArea();
//        textArea.setSize(300, 200);
        textArea.setLocation(400, 220);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        scroll.setVerticalScrollBarPolicy(
//                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        this.setLayout(null);
        this.setSize(500, 400);
        this.add(userLa);
        this.add(pwdLa);
        this.add(userTxt);
        this.add(pwdTxt);
        this.add(sureBt);
        this.add(quitBt);
//        this.add(verCodeLa);
//        this.add(verCodeTxt);
//        this.add(mp);
        this.add(xlk);
        this.add(scroll);
        sureBt.addActionListener(this);
        quitBt.addActionListener(this);
        this.setVisible(true);
        this.setSize(1000, 500);
        this.setVisible(true);

        Toolkit toolkit = Toolkit.getDefaultToolkit();

        int x = (int) (toolkit.getScreenSize().getWidth() - this.getWidth()) / 2;

        int y = (int) (toolkit.getScreenSize().getHeight() - this.getHeight()) / 2;

        this.setLocation(x, y);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    //具体事件的处理
    @Override
    public void actionPerformed(ActionEvent e) {
        //获取产生事件的事件源强制转换
        JButton bt = (JButton) e.getSource();
        //获取按钮上显示的文本
        String str = bt.getText();
        if (str.equals("登录")) {
            if (!CheckIsNull()) {
                //获取用户所输入的用户名
                String user = userTxt.getText().trim();
                //获取用户所输入的密码
                String pwd = pwdTxt.getText().trim();
                if (checkUserAndPwd(user, pwd)) {

                    //隐藏当前登录窗口
                    this.setVisible(false);
                    //验证成功创建一个主窗口
                    MainFrame frame = new MainFrame();
                } else {
                    //如果错误则弹出一个显示框
                    JOptionPane pane = new JOptionPane("用户或密码错误");
                    JDialog dialog = pane.createDialog(this, "警告");
                    dialog.show();
                }
            }
        } else {
            //调用系统类中的一个正常退出
            System.exit(0);
        }
    }

    private boolean CheckIsNull() {
        boolean flag = false;
        if (userTxt.getText().trim().equals(" ")) {
            flag = true;
        } else {
            if (pwdTxt.getText().trim().equals(" ")) {
                flag = true;
            }
        }
        return flag;
    }

    private boolean checkUserAndPwd(String user, String pwd) {
        boolean result = false;
        try {
            FileReader file = new FileReader("D:\\Workspaces\\MyEclipse 8.5\\testGUI.txt");
            BufferedReader bre = new BufferedReader(file);
            String str = bre.readLine();

            while (str != null) {
                String[] strs = str.split(",");
                if (strs[0].equals(user)) {
                    if (strs[1].equals(pwd)) {
                        result = true;
                    }
                }
                str = bre.readLine();
            }
            file.close();
        } catch (Exception ex) {
            System.out.print("");
        }
        return result;
    }
}
