package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.utils.CommonUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author humm23693
 * @description 界面初始化
 * @package com.hoomoomoo.im.start
 * @date 2021/04/18
 */
public class TaStarterController implements Initializable {

    @FXML
    private MenuBar ta;

    @FXML
    private TabPane functionTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CommonUtils.initialize(location, resources, functionTab, ta);
    }

}
