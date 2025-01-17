package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.utils.LoggerUtils;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.FILE_SYNC;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class FileTaskSyncController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, FILE_SYNC.getName()));
    }


}
