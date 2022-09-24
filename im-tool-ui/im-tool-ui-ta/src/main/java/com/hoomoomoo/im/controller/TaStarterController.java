package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
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

import static com.hoomoomoo.im.consts.FunctionConfig.*;


/**
 * @author humm23693
 * @description 界面初始化
 * @package com.hoomoomoo.im.start
 * @date 2021/04/18
 */
public class TaStarterController implements Initializable {

    @FXML
    private Menu menuScript;

    @FXML
    private Menu menuSvn;

    @FXML
    private Menu menuSet;

    @FXML
    private Menu menuHelp;

    @FXML
    private Menu menuCode;

    @FXML
    private TabPane functionTab;

    @FXML
    void openMenu(ActionEvent event) {
        CommonUtils.openMenu(event, functionTab);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CommonUtils.initialize(location, resources, functionTab, menuSvn, menuScript, menuCode, menuSet, menuHelp);
    }

}
