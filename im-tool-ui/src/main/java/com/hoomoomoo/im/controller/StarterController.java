package com.hoomoomoo.im.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author humm23693
 * @description 界面初始化
 * @package com.hoomoomoo.im.start
 * @date 2021/04/18
 */
public class StarterController implements Initializable {

    @FXML
    private Menu menuFunction;

    @FXML
    private MenuItem menuItemSvnlog;

    @FXML
    private Menu menuHelp;

    @FXML
    private MenuItem menuItemAbout;

    @FXML
    private TabPane functionTab;

    @FXML
    void openAbout(ActionEvent event) {

    }

    @FXML
    void openSvnLog(ActionEvent event) throws IOException {
        Tab tab = getFunctionTab("fxml/svnLog.fxml", "获取svn提交文件记录");
        if (!isOpen(tab)) {
            functionTab.getTabs().add(tab);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Tab tab = getFunctionTab("fxml/svnLog.fxml", "获取svn提交文件记录");
            functionTab.getTabs().add(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取功能TAB
     *
     * @param tabPath
     * @param tabName
     * @author: humm23693
     * @date: 2021/04/18
     * @return:
     */
    private Tab getFunctionTab(String tabPath, String tabName) throws IOException {
        Parent svnLog = FXMLLoader.load(getClass().getClassLoader().getResource(tabPath));
        return new Tab(tabName, svnLog);
    }

    /**
     * TAB是否已打开
     *
     * @param tab
     * @author: humm23693
     * @date: 2021/04/18
     * @return:
     */
    private boolean isOpen(Tab tab) {
        ObservableList<Tab> tabList = functionTab.getTabs();
        if (tabList != null) {
            for (Tab item : tabList) {
                if (item.getText().equals(tab.getText())) {
                    return true;
                }
            }
        }
        return false;
    }
}
