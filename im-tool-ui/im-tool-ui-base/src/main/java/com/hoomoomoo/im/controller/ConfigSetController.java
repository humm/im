package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.AppCache;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
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
    private TextArea config;

    @FXML
    private Button submit;

    @FXML
    private Label tips;

    @FXML
    void onSave(ActionEvent event) throws Exception {
        LoggerUtils.info(String.format(MSG_USE, CONFIG_SET.getName()));
        submit.setDisable(true);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String activatePrevFunction = appConfigDto.getActivatePrevFunction();
        String content = config.getText();
        String confPath = FileUtils.getFilePath(PATH_APP);
        FileUtils.writeFile(confPath, content);
        ConfigCache.initConfigCache();
        LoggerUtils.writeConfigSetInfo(CONFIG_SET.getCode());
        ConfigCache.getAppConfigDtoCache().setActivateFunction(activatePrevFunction);
        CommonUtils.showTipsByInfo(NAME_SAVE_SUCCESS);
        if (AppCache.FUNCTION_TAB_CACHE != null) {
            CommonUtils.closeTab(CONFIG_SET, true);
        }
        submit.setDisable(false);
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String confPath = FileUtils.getFilePath(PATH_APP);
        List<String> content = FileUtils.readNormalFile(confPath);
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
