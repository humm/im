package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.FunctionConfig.*;


/**
 * @author humm23693
 * @description 界面初始化
 * @package com.hoomoomoo.im.start
 * @date 2021/04/18
 */
public class ShoppingStarterController implements Initializable {

    @FXML
    private Menu menuJd;

    @FXML
    private Menu menuSet;

    @FXML
    private Menu menuHelp;

    @FXML
    private TabPane functionTab;

    @FXML
    void openWaitAppraise(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_OPEN, WAIT_APPRAISE.getName()));
            Tab tab = isOpen(WAIT_APPRAISE.getName());
            if (tab == null) {
                tab = getFunctionTab(WAIT_APPRAISE.getPath(), WAIT_APPRAISE.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void openShowOrder(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_OPEN, SHOW_ORDER.getName()));
            Tab tab = isOpen(SHOW_ORDER.getName());
            if (tab == null) {
                tab = getFunctionTab(SHOW_ORDER.getPath(), SHOW_ORDER.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void openAppendAppraise(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_OPEN, APPEND_APPRAISE.getName()));
            Tab tab = isOpen(APPEND_APPRAISE.getName());
            if (tab == null) {
                tab = getFunctionTab(APPEND_APPRAISE.getPath(), APPEND_APPRAISE.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void openServiceAppraise(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_OPEN, SERVICE_APPRAISE.getName()));
            Tab tab = isOpen(SERVICE_APPRAISE.getName());
            if (tab == null) {
                tab = getFunctionTab(SERVICE_APPRAISE.getPath(), SERVICE_APPRAISE.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }


    @FXML
    void openFunctionStatInfo(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_OPEN, FUNCTION_STAT_INFO.getName()));
            Tab tab = isOpen(FUNCTION_STAT_INFO.getName());
            if (tab == null) {
                tab = getFunctionTab(FUNCTION_STAT_INFO.getPath(), FUNCTION_STAT_INFO.getName());
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
            LoggerUtils.info(String.format(BaseConst.MSG_OPEN, ABOUT_INFO.getName()));
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

    @FXML
    void openConfigSet(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_OPEN, CONFIG_SET.getName()));
            Tab tab = isOpen(CONFIG_SET.getName());
            if (tab == null) {
                tab = getFunctionTab(CONFIG_SET.getPath(), CONFIG_SET.getName());
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void openJdCookieSet(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_OPEN, JD_COOKIE.getName()));
            Tab tab = isOpen(JD_COOKIE.getName());
            if (tab == null) {
                tab = getFunctionTab(JD_COOKIE.getPath(), JD_COOKIE.getName());
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
                menuJd.getItems().clear();
                menuSet.getItems().clear();
                return;
            }

            LoggerUtils.info(String.format(BaseConst.MSG_CHECK, "证书有效日期"));

            // 控制菜单功能
            CommonUtils.showAuthFunction(menuJd);
            CommonUtils.showAuthFunction(menuSet);

            String showTab = appConfigDto.getAppTabShow();
            if (StringUtils.isNotBlank(showTab)) {
                String[] tabs = showTab.split(BaseConst.SYMBOL_COMMA);
                for (String tab : tabs) {
                    if (StringUtils.isBlank(getName(tab))) {
                        LoggerUtils.info(String.format(BaseConst.MSG_FUNCTION_NOT_EXIST, tab));
                        continue;
                    }
                    // 校验功能是否有权限
                    if (!CommonUtils.checkLicense(tab)) {
                        continue;
                    }
                    functionTab.getTabs().add(getFunctionTab(getPath(tab), getName(tab)));
                }
            } else {
                // 默认打开有权限的第一个功能
                List<FunctionDto> functionDtoList = CommonUtils.getAuthFunction();
                if (CollectionUtils.isNotEmpty(functionDtoList)) {
                    FunctionDto functionDto = functionDtoList.get(0);
                    Tab tab = getFunctionTab(getPath(functionDto.getFunctionCode()), getName(functionDto.getFunctionCode()));
                    functionTab.getTabs().add(tab);
                }
            }
            LoggerUtils.info(String.format(BaseConst.MSG_INIT, "功能界面"));
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
