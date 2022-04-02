package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.CONFIG_SET;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class BlankSetController implements Initializable {

    @FXML
    private TextArea config;

    @FXML
    private Button submit;

    private String pageType;

    @FXML
    void onSave(ActionEvent event) throws Exception {
        String content = config.getText();
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        if (STR_1.equals(pageType)) {
            appConfigDto.getGenerateCodeDto().setTable(content);
            appConfigDto.getTableStage().close();
            appConfigDto.setTableStage(null);
        } else {
            appConfigDto.getGenerateCodeDto().setAsyTable(content);
            appConfigDto.getAsyTableStage().close();
            appConfigDto.setAsyTableStage(null);
        }
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        GenerateCodeDto generateCodeDto = appConfigDto.getGenerateCodeDto();
        pageType = appConfigDto.getPageType();
        String table = null;
        if (STR_1.equals(pageType)) {
            if (generateCodeDto != null) {
                table = generateCodeDto.getTable();
            }
            table = StringUtils.isBlank(table) ? SYMBOL_EMPTY : table;
        } else {
            if (generateCodeDto != null) {
                table = generateCodeDto.getAsyTable();
            }
            table = StringUtils.isBlank(table) ? SYMBOL_EMPTY : table;
        }
        OutputUtils.info(config, table);
    }
}
