package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;


/**
 * @author humm23693
 * @description 检查结果页面
 * @package com.hoomoomoo.im.start
 * @date 2021/04/18
 */
public class CheckResultController implements Initializable {

    @FXML
    private TabPane functionTab;

    private int index = -1;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String pageType = appConfigDto.getPageType();
        if (PAGE_TYPE_SYSTEM_TOOL_CHECK_RESULT.equals(pageType)) {
            initTab(appConfigDto, FILE_SQL_NAME_LACK_NEW_MENU_ALL);
            initTab(appConfigDto, FILE_SQL_NAME_DIFF_NEW_ALL_EXT);
            initTab(appConfigDto, FILE_SQL_NAME_LEGAL_NEW_MENU);
            initTab(appConfigDto, FILE_SQL_NAME_LACK_OLD_NEW_ALL);
            initTab(appConfigDto, FILE_SQL_NAME_DIFF_OLD_ALL_EXT);
            initTab(appConfigDto, FILE_SQL_NAME_LACK_ROUTER);
            initTab(appConfigDto, FILE_SQL_NAME_LACK_LOG);
            initTab(appConfigDto, FILE_SQL_NAME_ERROR_LOG);
            initTab(appConfigDto, FILE_SQL_NAME_ALL_MENU);
        } else if (PAGE_TYPE_SYSTEM_TOOL_UPDATE_RESULT.equals(pageType)) {
            initTab(appConfigDto, FILE_SQL_NAME_NEW_MENU_UPDATE);
        }
    }

    private void initTab(AppConfigDto appConfigDto, String fileName) throws IOException {
        index = -1;
        Tab tab = new Tab();
        outputContent(tab, getContent(appConfigDto, fileName), fileName);
        String tabName = fileName.split("\\.")[1];
        if (index == 0) {
            return;
        }
        if (index != -1) {
            tabName += "【" + index + "】";
        }
        tab.setText(tabName);
        functionTab.getTabs().add(tab);
    }

    private List<String> getContent(AppConfigDto appConfigDto, String fileName) throws IOException {
        String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
        return FileUtils.readNormalFile(resultPath + "\\" + fileName, false);
    }

    private void outputContent(Tab tab, List<String> content, String fileName) {
        StringBuilder text = new StringBuilder();
        for (String item : content) {
            if (index == -1) {
                if ((item.contains("待处理") || item.contains("菜单总数")) && item.contains("【") && item.contains("】")) {
                    index = Integer.valueOf(item.split("【")[1].split("】")[0]);
                }
            }
            if (!FILE_SQL_NAME_NEW_MENU_UPDATE.equals(fileName)) {
                item = item.replaceAll("--", "");
            }
            text.append(item).append(STR_NEXT_LINE);
        }
        tab.setContent(new TextArea(text.toString()));
    }
}
