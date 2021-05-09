package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionType;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;


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
    private Menu menuHelp;

    @FXML
    private MenuItem menuItemSvnlog;

    @FXML
    private MenuItem menuItemFundInfo;

    @FXML
    private MenuItem menuItemProcessInfo;

    @FXML
    private MenuItem menuStat;

    @FXML
    private MenuItem menuAbout;

    @FXML
    private TabPane functionTab;

    @FXML
    void openAboutInfo(ActionEvent event) throws IOException {
        Tab tab = getFunctionTab(FunctionType.getPath(STR_6), FunctionType.getName(STR_6));
        if (!isOpen(tab)) {
            functionTab.getTabs().add(tab);
        }
        functionTab.getSelectionModel().select(tab);
    }

    @FXML
    void openStatInfo(ActionEvent event) throws IOException {
        Tab tab = getFunctionTab(FunctionType.getPath(STR_5), FunctionType.getName(STR_5));
        if (!isOpen(tab)) {
            functionTab.getTabs().add(tab);
        }
        functionTab.getSelectionModel().select(tab);
    }

    @FXML
    void openSvnLog(ActionEvent event) throws IOException {
        Tab tab = getFunctionTab(FunctionType.getPath(STR_1), FunctionType.getName(STR_1));
        if (!isOpen(tab)) {
            functionTab.getTabs().add(tab);
            functionTab.getSelectionModel().select(tab);
        }
        functionTab.getSelectionModel().select(tab);
    }

    @FXML
    void openSvnUpdate(ActionEvent event) throws IOException {
        Tab tab = getFunctionTab(FunctionType.getPath(STR_2), FunctionType.getName(STR_2));
        if (!isOpen(tab)) {
            functionTab.getTabs().add(tab);
        }
        functionTab.getSelectionModel().select(tab);
    }

    @FXML
    void openFundInfo(ActionEvent event) throws IOException {
        Tab tab = getFunctionTab(FunctionType.getPath(STR_3), FunctionType.getName(STR_3));
        if (!isOpen(tab)) {
            functionTab.getTabs().add(tab);
        }
        functionTab.getSelectionModel().select(tab);
    }

    @FXML
    void openProcessInfo(ActionEvent event) throws IOException {
        Tab tab = getFunctionTab(FunctionType.getPath(STR_4), FunctionType.getName(STR_4));
        if (!isOpen(tab)) {
            functionTab.getTabs().add(tab);
        }
        functionTab.getSelectionModel().select(tab);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            String showTab = appConfigDto.getShowTab();
            if (StringUtils.isNotBlank(showTab)) {
                String[] tabs = showTab.split(STR_COMMA);
                for (String tab : tabs) {
                    functionTab.getTabs().add(getFunctionTab(FunctionType.getPath(tab), FunctionType.getName(tab)));
                }
            } else {
                Tab tab = getFunctionTab(FunctionType.getPath(STR_1), FunctionType.getName(STR_1));
                functionTab.getTabs().add(tab);
            }
        } catch (Exception e) {
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
        Parent svnLog = FXMLLoader.load(FileUtils.getFilePath(tabPath));
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
