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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.ABOUT_INFO;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/09
 */
public class AboutInfoController implements Initializable {

    @FXML
    private TextArea about;

    private static final String SHOW_CONTENT = "当前版本";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.info(String.format(MSG_USE, ABOUT_INFO.getName()));
        try {
            OutputUtils.clearLog(about);
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            OutputUtils.info(about, appConfigDto.getAppName() + STR_NEXT_LINE_2);
            List<String> version = new ArrayList<>(16);
            try {
                version = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION), false);
            } catch (IOException e) {
                LoggerUtils.info(e);
            }
            if (CollectionUtils.isNotEmpty(version)) {
                for (String item : version) {
                    if (StringUtils.isBlank(item) || !isShow(appConfigDto, item)) {
                        OutputUtils.info(about, STR_NEXT_LINE);
                        continue;
                    }
                    OutputUtils.info(about, STR_SPACE_4 + item + STR_NEXT_LINE_2);
                }
            }

            if (CommonUtils.checkUser(appConfigDto.getAppUser(), APP_USER_IM)) {
                OutputUtils.info(about, STR_NEXT_LINE);
                LicenseDto licenseDto = appConfigDto.getLicense();
                String effectiveDate = CommonUtils.getCurrentDateTime5(licenseDto.getEffectiveDate());
                OutputUtils.info(about, STR_SPACE_4 + "授权截止: " + effectiveDate + STR_NEXT_LINE_2);

                String authStatus = "已过期";
                if (CommonUtils.checkLicense(null)) {
                    authStatus = "生效中";
                }
                OutputUtils.info(about, STR_SPACE_4 + "授权状态: " + authStatus + STR_NEXT_LINE_2);
            }
            LoggerUtils.writeLogInfo(ABOUT_INFO.getCode(), new Date(), new ArrayList<>());
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private boolean isShow(AppConfigDto appConfigDto, String item) {
        if (CommonUtils.checkUser(appConfigDto.getAppUser(), APP_USER_IM)) {
            return true;
        }
        String[] showMsg = SHOW_CONTENT.split(STR_COMMA);
        for (String temp : showMsg) {
            if (item.startsWith(temp)) {
                return true;
            }
        }
        return false;
    }
}
