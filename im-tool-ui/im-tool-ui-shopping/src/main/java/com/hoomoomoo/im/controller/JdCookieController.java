package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.AppCache;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.JD_COOKIE;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class JdCookieController implements Initializable {

    @FXML
    private TextArea config;

    @FXML
    private Button submit;

    @FXML
    private Label tips;

    @FXML
    void onSave(ActionEvent event) throws Exception {
        LoggerUtils.info(String.format(MSG_USE, JD_COOKIE.getName()));
        submit.setDisable(true);
        String content = config.getText();
        String confPath = FileUtils.getFilePath(PATH_JD_COOKIE);
        FileUtils.writeFile(confPath, content, false);
        ConfigCache.initCache();
        OutputUtils.info(tips, NAME_SAVE_SUCCESS + CommonUtils.getCurrentDateTime8(new Date()));
        LoggerUtils.writeConfigSetInfo(JD_COOKIE.getCode());
        if (AppCache.FUNCTION_TAB_CACHE != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ObservableList<Tab> tabs = AppCache.FUNCTION_TAB_CACHE.getTabs();
                    Iterator<Tab> iterator = tabs.listIterator();
                    while (iterator.hasNext()) {
                        Tab tab = iterator.next();
                        if (tab.getText().equals(CommonUtils.getMenuName(JD_COOKIE.getCode(), JD_COOKIE.getName()))) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                LoggerUtils.info(e);
                            }
                            iterator.remove();
                            break;
                        }
                    }
                }
            }).start();
        }
        submit.setDisable(false);
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String confPath = FileUtils.getFilePath(PATH_JD_COOKIE);
        List<String> content = FileUtils.readNormalFile(confPath, false);
        for (String item : content) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            OutputUtils.info(config, item + STR_NEXT_LINE);
            if (item.startsWith(CONFIG_PREFIX) && item.contains(NAME_END)) {
                OutputUtils.info(config, STR_NEXT_LINE);
            }
        }
    }
}
