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

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1.全量新版UED缺少菜单
        // 2.全量老版UED缺少菜单
        // 3.新版UED菜单全量开通不一致
        // 4.老板UED菜单全量开通不一致
        // 5.存在菜单缺少路由
        // 6.缺少日志信息.sql
        // 7.日志错误信息.sql
        // 8.所有非弹窗菜单.sql
        // 10.全量新版UED升级.sql
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String pageType = appConfigDto.getPageType();
        if (PAGE_TYPE_SYSTEM_TOOL_CHECK_RESULT.equals(pageType)) {
            Tab tab1 = new Tab("全量新版UED缺少菜单");
            outputContent(tab1, getContent(appConfigDto, "1.全量新版UED缺少菜单.sql"));
            functionTab.getTabs().add(tab1);

            Tab tab2 = new Tab("全量老版UED缺少菜单");
            outputContent(tab2, getContent(appConfigDto, "2.全量老版UED缺少菜单.sql"));
            functionTab.getTabs().add(tab2);

            Tab tab3 = new Tab("新版UED菜单全量开通不一致");
            outputContent(tab3, getContent(appConfigDto, "3.新版UED菜单全量开通不一致.sql"));
            functionTab.getTabs().add(tab3);

            Tab tab4 = new Tab("老板UED菜单全量开通不一致");
            outputContent(tab4, getContent(appConfigDto, "4.老板UED菜单全量开通不一致.sql"));
            functionTab.getTabs().add(tab4);

            Tab tab5 = new Tab("存在菜单缺少路由");
            outputContent(tab5, getContent(appConfigDto, "5.存在菜单缺少路由.sql"));
            functionTab.getTabs().add(tab5);

            Tab tab6 = new Tab("缺少日志信息");
            outputContent(tab6, getContent(appConfigDto, "6.缺少日志信息.sql"));
            functionTab.getTabs().add(tab6);

            Tab tab7 = new Tab("日志错误信息");
            outputContent(tab7, getContent(appConfigDto, "7.日志错误信息.sql"));
            functionTab.getTabs().add(tab7);

            Tab tab8 = new Tab("所有非弹窗菜单");
            outputContent(tab8, getContent(appConfigDto, "8.所有非弹窗菜单.sql"));
            functionTab.getTabs().add(tab8);
        } else if (PAGE_TYPE_SYSTEM_TOOL_UPDATE_RESULT.equals(pageType)) {
            Tab tab1 = new Tab("全量新版UED升级");
            outputContent(tab1, getContent(appConfigDto, "10.全量新版UED升级.sql"));
            functionTab.getTabs().add(tab1);
        }
    }

    private List<String> getContent(AppConfigDto appConfigDto, String path) throws IOException {
        String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
        return FileUtils.readNormalFile(resultPath + "\\" + path, false);
    }

    private void outputContent(Tab tab, List<String> content) {
        StringBuilder text = new StringBuilder();
        for (String item : content) {
            text.append(item.replaceAll("--", "")).append(STR_NEXT_LINE);
        }
        tab.setContent(new TextArea(text.toString()));
    }
}
