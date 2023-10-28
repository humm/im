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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.CONFIG_SET;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class ConfigSetController implements Initializable {

    @FXML
    private AnchorPane configSet;

    @FXML
    private TextArea config;

    @FXML
    private Button submit;

    @FXML
    private Label tips;

    @FXML
    void onSave(ActionEvent event) throws Exception {
        LoggerUtils.info(String.format(MSG_USE, CONFIG_SET.getName()));
        submit.setDisable(true);
        String content = config.getText();
        String confPath = FileUtils.getFilePath(PATH_APP);
        FileUtils.writeFile(confPath, content, false);
        ConfigCache.initConfigCache();
        OutputUtils.info(tips, NAME_SAVE_SUCCESS + CommonUtils.getCurrentDateTime8(new Date()));
        LoggerUtils.writeConfigSetInfo(CONFIG_SET.getCode());
        if (AppCache.FUNCTION_TAB_CACHE != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ObservableList<Tab> tabs = AppCache.FUNCTION_TAB_CACHE.getTabs();
                    Iterator<Tab> iterator = tabs.listIterator();
                    while (iterator.hasNext()) {
                        Tab tab = iterator.next();
                        if (tab.getText().equals(CommonUtils.getMenuName(CONFIG_SET.getCode(), CONFIG_SET.getName()))) {
                            try {
                                Thread.sleep(1000);
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
        String confPath = FileUtils.getFilePath(PATH_APP);
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
