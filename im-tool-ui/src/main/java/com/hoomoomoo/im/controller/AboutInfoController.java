package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE_2;
import static com.hoomoomoo.im.consts.BaseConst.STR_SPACE_4;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/09
 */
public class AboutInfoController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(AboutInfoController.class);

    @FXML
    private TextArea about;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            OutputUtils.clearLog(about);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            OutputUtils.info(about, appConfigDto.getAppName() + STR_NEXT_LINE_2);
            List<String> version = new ArrayList<>(16);
            try {
                version = FileUtils.readNormalFile(FileUtils.getFilePath("/conf/version.conf").getPath(), false);
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
            if (CollectionUtils.isNotEmpty(version)) {
                for (String item : version) {
                    OutputUtils.info(about, STR_SPACE_4 + item + STR_NEXT_LINE_2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
