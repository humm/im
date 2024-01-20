package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.List;
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
        String pageType = appConfigDto.getPageType();
        if (!PAGE_TYPE_HEP_DETAIL.equals(pageType)) {
            String content = config.getText();
            String confPath = STR_BLANK;
            if (PAGE_TYPE_SYSTEM_TOOL_SKIP_NEW_MENU.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_NEW_MENU);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_OLD_MENU.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_OLD_MENU);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_NEW_DIFF_MENU.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_NEW_DIFF_MENU);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_OLD_DIFF_MENU.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_OLD_DIFF_MENU);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_ROUTER.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_ROUTER);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_LOG.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_LOG);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_ERROR_LOG.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_ERROR_LOG);
            }
            FileUtils.writeFile(confPath, content, false);
        }
        appConfigDto.getChildStage().close();
        appConfigDto.setChildStage(null);
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
            info = StringUtils.isBlank(info) ? STR_BLANK : info;
            OutputUtils.info(config, info);
            submit.setText("关闭");
        } else  {
            String confPath = STR_BLANK;
            if (PAGE_TYPE_SYSTEM_TOOL_SKIP_NEW_MENU.equals(pageType)){
                confPath = FileUtils.getFilePath(PATH_SKIP_NEW_MENU);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_OLD_MENU.equals(pageType)){
                confPath = FileUtils.getFilePath(PATH_SKIP_OLD_MENU);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_NEW_DIFF_MENU.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_NEW_DIFF_MENU);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_OLD_DIFF_MENU.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_OLD_DIFF_MENU);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_ROUTER.equals(pageType)) {
                confPath = FileUtils.getFilePath(PATH_SKIP_ROUTER);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_LOG.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_LOG);
            } else if (PAGE_TYPE_SYSTEM_TOOL_SKIP_ERROR_LOG.equals(appConfigDto.getPageType())) {
                confPath = FileUtils.getFilePath(PATH_SKIP_ERROR_LOG);
            }
            List<String> content = FileUtils.readNormalFile(confPath, false);
            for (String item : content) {
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                OutputUtils.info(config, item + STR_NEXT_LINE);
            }
        }
    }
}
