package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE_2;
import static com.hoomoomoo.im.consts.BaseConst.STR_SPACE_4;
import static com.hoomoomoo.im.consts.VersionConst.VERSION_DATE;
import static com.hoomoomoo.im.consts.VersionConst.VERSION_NUM;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/09
 */
public class AboutInfoController implements Initializable {

    @FXML
    private TextArea about;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            OutputUtils.clearLog(about);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            OutputUtils.info(about, appConfigDto.getAppName() + STR_NEXT_LINE_2);
            OutputUtils.info(about, STR_SPACE_4 + "版本号: " + VERSION_NUM + STR_NEXT_LINE_2);
            OutputUtils.info(about, STR_SPACE_4 + "发布时间: " + VERSION_DATE + STR_NEXT_LINE_2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
