package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.getName;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.getPath;


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
            initTab(appConfigDto, "1.全量新版UED缺少菜单.sql");
            initTab(appConfigDto, "3.新版UED菜单全量开通不一致.sql");
            initTab(appConfigDto, "9.新版UED菜单合法性.sql");
            initTab(appConfigDto, "2.全量老版UED缺少菜单.sql");
            initTab(appConfigDto, "4.老板UED菜单全量开通不一致.sql");
            initTab(appConfigDto, "5.存在菜单缺少路由.sql");
            initTab(appConfigDto, "6.缺少日志信息.sql");
            initTab(appConfigDto, "7.日志错误信息.sql");
            initTab(appConfigDto, "8.所有非弹窗菜单.sql");
        } else if (PAGE_TYPE_SYSTEM_TOOL_UPDATE_RESULT.equals(pageType)) {
            initTab(appConfigDto, "99.全量新版UED升级.sql");
        }
    }

    private void initTab(AppConfigDto appConfigDto, String fileName) throws IOException {
        index = -1;
        Tab tab = new Tab();
        outputContent(tab, getContent(appConfigDto, fileName));
        String tabName = fileName.split("\\.")[1];
        if (index != -1) {
            tabName += "【" + index + "】";
        }
        tab.setText(tabName);
        functionTab.getTabs().add(tab);
    }

    private List<String> getContent(AppConfigDto appConfigDto, String path) throws IOException {
        String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
        return FileUtils.readNormalFile(resultPath + "\\" + path, false);
    }

    private void outputContent(Tab tab, List<String> content) {
        StringBuilder text = new StringBuilder();
        for (String item : content) {
            if (index == -1 && item.contains("待处理") && item.contains("【") && item.contains("】")) {
                index = Integer.valueOf(item.split("【")[1].split("】")[0]);
            }
            text.append(item.replaceAll("--", "")).append(STR_NEXT_LINE);
        }
        tab.setContent(new TextArea(text.toString()));
    }
}
