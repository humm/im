package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
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

    @FXML
    void onSave(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
         if (PAGE_TYPE_HEP_DETAIL.equals(appConfigDto.getPageType())) {
            appConfigDto.getChildStage().close();
            appConfigDto.setChildStage(null);
        }
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        HepTaskDto hepTaskDto = appConfigDto.getHepTaskDto();
        String pageType = appConfigDto.getPageType();
        String info = null;
        if (PAGE_TYPE_HEP_DETAIL.equals(pageType)) {
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
