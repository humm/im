package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.*;


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
    private MenuItem menuItemScriptUpdate;

    @FXML
    private MenuItem menuStat;

    @FXML
    private MenuItem menuAbout;

    @FXML
    private TabPane functionTab;

    @FXML
    void openSvnLog(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(STR_MSG_OPEN, SVN_LOG.getName()));
            Tab tab = isOpen(SVN_LOG.getName());
            if (tab == null) {
                tab = getFunctionTab(SVN_LOG.getPath(), SVN_LOG.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void openSvnUpdate(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(STR_MSG_OPEN, SVN_UPDATE.getName()));
            Tab tab = isOpen(SVN_UPDATE.getName());
            if (tab == null) {
                tab = getFunctionTab(SVN_UPDATE.getPath(), SVN_UPDATE.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void openFundInfo(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(STR_MSG_OPEN, FUND_INFO.getName()));
            Tab tab = isOpen(FUND_INFO.getName());
            if (tab == null) {
                tab = getFunctionTab(FUND_INFO.getPath(), FUND_INFO.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void openProcessInfo(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(STR_MSG_OPEN, PROCESS_INFO.getName()));
            Tab tab = isOpen(PROCESS_INFO.getName());
            if (tab == null) {
                tab = getFunctionTab(PROCESS_INFO.getPath(), PROCESS_INFO.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void openScriptUpdate(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(STR_MSG_OPEN, SCRIPT_UPDATE.getName()));
            Tab tab = isOpen(SCRIPT_UPDATE.getName());
            if (tab == null) {
                tab = getFunctionTab(SCRIPT_UPDATE.getPath(), SCRIPT_UPDATE.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void openStatInfo(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(STR_MSG_OPEN, STAT_INFO.getName()));
            Tab tab = isOpen(STAT_INFO.getName());
            if (tab == null) {
                tab = getFunctionTab(STAT_INFO.getPath(), STAT_INFO.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void openAboutInfo(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(STR_MSG_OPEN, ABOUT_INFO.getName()));
            Tab tab = isOpen(ABOUT_INFO.getName());
            if (tab == null) {
                tab = getFunctionTab(ABOUT_INFO.getPath(), ABOUT_INFO.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();

            // 校验证书是否过期
            if (!CommonUtils.checkLicense(null)) {
                menuFunction.getItems().clear();
                menuHelp.getItems().remove(0);
                return;
            }

            LoggerUtils.info(String.format(STR_MSG_CHECK, "证书有效日期"));

            // 控制菜单功能
            CommonUtils.showAuthFunction(menuFunction);

            String showTab = appConfigDto.getShowTab();
            if (StringUtils.isNotBlank(showTab)) {
                String[] tabs = showTab.split(STR_SYMBOL_COMMA);
                for (String tab : tabs) {
                    if (StringUtils.isBlank(FunctionConfig.getName(tab))) {
                        LoggerUtils.info(String.format(STR_MSG_FUNCTION_NOT_EXIST, tab));
                        continue;
                    }
                    // 校验功能是否有权限
                    if (!CommonUtils.checkLicense(tab)) {
                        continue;
                    }
                    functionTab.getTabs().add(getFunctionTab(FunctionConfig.getPath(tab), FunctionConfig.getName(tab)));
                }
            } else {
                // 默认打开有权限的第一个功能
                List<FunctionDto> functionDtoList = CommonUtils.getAuthFunction();
                if (CollectionUtils.isNotEmpty(functionDtoList)) {
                    FunctionDto functionDto = functionDtoList.get(0);
                    Tab tab = getFunctionTab(FunctionConfig.getPath(functionDto.getFunctionCode()), FunctionConfig.getName(functionDto.getFunctionCode()));
                    functionTab.getTabs().add(tab);
                }
            }
            LoggerUtils.info(String.format(STR_MSG_INIT, "功能界面"));
        } catch (Exception e) {
            LoggerUtils.info(e);
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
        Parent tab = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(tabPath)));
        return new Tab(tabName, tab);
    }

    /**
     * TAB是否已打开
     *
     * @param tabName
     * @author: humm23693
     * @date: 2021/04/18
     * @return:
     */
    private Tab isOpen(String tabName) {
        ObservableList<Tab> tabList = functionTab.getTabs();
        if (tabList != null) {
            for (Tab item : tabList) {
                if (item.getText().equals(tabName)) {
                    return item;
                }
            }
        }
        return null;
    }
}
