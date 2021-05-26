package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LicenseDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.ABOUT_INFO;

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
        LoggerUtils.info(String.format(STR_MSG_USE, ABOUT_INFO.getName()));
        try {
            OutputUtils.clearLog(about);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            OutputUtils.info(about, appConfigDto.getAppName() + STR_SYMBOL_NEXT_LINE_2);
            List<String> version = new ArrayList<>(16);
            try {
                version = FileUtils.readNormalFile(FileUtils.getFilePath("/conf/init/version.init"), false);
            } catch (IOException e) {
                LoggerUtils.info(e);
            }
            if (CollectionUtils.isNotEmpty(version)) {
                for (String item : version) {
                    OutputUtils.info(about, STR_SPACE_4 + item + STR_SYMBOL_NEXT_LINE_2);
                }
            }

            if (appConfigDto.getAppLicenseShow()) {
                OutputUtils.info(about, STR_SYMBOL_NEXT_LINE);
                LicenseDto licenseDto = appConfigDto.getLicense();
                String effectiveDate = CommonUtils.getCurrentDateTime5(licenseDto.getEffectiveDate());
                OutputUtils.info(about, STR_SPACE_4 + "授权截止: " + effectiveDate + STR_SYMBOL_NEXT_LINE_2);

                String authStatus = "已过期";
                if (CommonUtils.checkLicense(null)) {
                    authStatus = "生效中";
                }
                OutputUtils.info(about, STR_SPACE_4 + "授权状态: " + authStatus + STR_SYMBOL_NEXT_LINE_2);
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
