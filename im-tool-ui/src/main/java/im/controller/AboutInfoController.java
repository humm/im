package im.controller;

import im.cache.ConfigCache;
import im.dto.AppConfigDto;
import im.dto.LicenseDto;
import im.utils.CommonUtils;
import im.utils.FileUtils;
import im.utils.LoggerUtils;
import im.utils.OutputUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static im.consts.BaseConst.*;
import static im.consts.FunctionConfig.ABOUT_INFO;

/**
 * @author humm23693
 * @description TODO
 * @package im.controller
 * @date 2021/05/09
 */
public class AboutInfoController implements Initializable {

    @FXML
    private TextArea about;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.info(String.format(MSG_USE, ABOUT_INFO.getName()));
        try {
            OutputUtils.clearLog(about);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            OutputUtils.info(about, appConfigDto.getAppName() + SYMBOL_NEXT_LINE_2);
            List<String> version = new ArrayList<>(16);
            try {
                version = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION), false);
            } catch (IOException e) {
                LoggerUtils.info(e);
            }
            if (CollectionUtils.isNotEmpty(version)) {
                for (String item : version) {
                    if (StringUtils.isBlank(item)) {
                        OutputUtils.info(about, SYMBOL_NEXT_LINE);
                        continue;
                    }
                    OutputUtils.info(about, SYMBOL_SPACE_4 + item + SYMBOL_NEXT_LINE_2);
                }
            }

            if (appConfigDto.getAppLicenseShow()) {
                OutputUtils.info(about, SYMBOL_NEXT_LINE);
                LicenseDto licenseDto = appConfigDto.getLicense();
                String effectiveDate = CommonUtils.getCurrentDateTime5(licenseDto.getEffectiveDate());
                OutputUtils.info(about, SYMBOL_SPACE_4 + "授权截止: " + effectiveDate + SYMBOL_NEXT_LINE_2);

                String authStatus = "已过期";
                if (CommonUtils.checkLicense(null)) {
                    authStatus = "生效中";
                }
                OutputUtils.info(about, SYMBOL_SPACE_4 + "授权状态: " + authStatus + SYMBOL_NEXT_LINE_2);
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
