package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;

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
        if (PAGE_TYPE_GENERATE_CODE_TABLE.equals(pageType)) {
            appConfigDto.getGenerateCodeDto().setTable(content);
            appConfigDto.getTableStage().close();
            appConfigDto.setTableStage(null);
        } else if (PAGE_TYPE_GENERATE_CODE_TABLE.equals(pageType)) {
            appConfigDto.getGenerateCodeDto().setAsyTable(content);
            appConfigDto.getAsyTableStage().close();
            appConfigDto.setAsyTableStage(null);
        } else if (PAGE_TYPE_HEP_DETAIL.equals(pageType)) {
            appConfigDto.getTaskStage().close();
            appConfigDto.setTaskStage(null);
        }
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        GenerateCodeDto generateCodeDto = appConfigDto.getGenerateCodeDto();
        HepTaskDto hepTaskDto = appConfigDto.getHepTaskDto();
        pageType = appConfigDto.getPageType();
        String info = null;
        if (PAGE_TYPE_GENERATE_CODE_TABLE.equals(pageType)) {
            if (generateCodeDto != null) {
                info = generateCodeDto.getTable();
            }
        } else if (PAGE_TYPE_GENERATE_CODE_ASY_TABLE.equals(pageType)) {
            if (generateCodeDto != null) {
                info = generateCodeDto.getAsyTable();
            }
        } else if (PAGE_TYPE_HEP_DETAIL.equals(pageType)) {
            if (hepTaskDto != null) {
                info = hepTaskDto.getDescription();
                String hepTaskTodoDetailSymbol = appConfigDto.getHepTaskTodoDetailSymbol();
                if (StringUtils.isNotBlank(hepTaskTodoDetailSymbol)) {
                    String[] symbol = hepTaskTodoDetailSymbol.split(STR_$_SLASH);
                    for (String item : symbol) {
                        String[] ele = item.split(STR_COLON);
                        if (ele.length == 1) {
                            info = info.replaceAll(ele[0], STR_BLANK);
                        } else if (ele.length == 2) {
                            if (KEY_NEXT.equals(ele[1])) {
                                ele[1] = STR_NEXT_LINE;
                            }
                            info = info.replaceAll(ele[0], ele[1]);
                        }
                    }
                }
            }
            submit.setText("关闭");
        }
        info = StringUtils.isBlank(info) ? STR_BLANK : info;
        OutputUtils.info(config, info);
    }
}
