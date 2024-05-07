package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.extend.ScriptSqlUtils;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.NEW_MENU_UPDATE;


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
            SQL_CHECK_TYPE[] fileList = SQL_CHECK_TYPE.values();
            for (SQL_CHECK_TYPE item : fileList) {
                if (NEW_MENU_UPDATE.getName().equals(item.getName())) {
                    continue;
                }
                initTab(appConfigDto, item.getFileName());
            }
        } else if (PAGE_TYPE_SYSTEM_TOOL_UPDATE_RESULT.equals(pageType)) {
            initTab(appConfigDto, NEW_MENU_UPDATE.getFileName());
        } else if (PAGE_TYPE_SYSTEM_TOOL_SYSTEM_LOG.equals(pageType)) {
            initTab("appLog", "系统日志");
            MenuFunctionConfig.FunctionConfig[] functionConfigs = MenuFunctionConfig.FunctionConfig.values();
            for (MenuFunctionConfig.FunctionConfig functionConfig : functionConfigs) {
                initTab(functionConfig.getLogFolder(), functionConfig.getName());
            }
        } else if (PAGE_TYPE_SYSTEM_TOOL_REPAIR_OLD_MENU_LOG.equals(pageType)) {
            String basePath = appConfigDto.getSystemToolCheckMenuBasePath();
            String check = basePath + ScriptSqlUtils.baseMenu.replace(".sql", ".check.sql");
            String res = basePath + ScriptSqlUtils.baseMenu.replace(".sql", ".res.sql");
            initTabByFile(check, "错误信息");
            initTabByFile(res, "脚本详情");
        } else if (PAGE_TYPE_SYSTEM_TOOL_REPAIR_NEW_MENU_LOG.equals(pageType)) {
            String basePath = appConfigDto.getSystemToolCheckMenuBasePath();
            String check = basePath + ScriptSqlUtils.newUedPage.replace(".sql", ".check.sql");
            String res = basePath + ScriptSqlUtils.newUedPage.replace(".sql", ".res.sql");
            initTabByFile(check, "错误信息");
            initTabByFile(res, "脚本详情");
        }
    }

    private void initTab(String logFolder, String tabName) throws IOException {
        Tab tab = new Tab();
        File logFile = new File(FileUtils.getFilePath(String.format(SUB_PATH_LOG, logFolder)));
        File[] fileList = logFile.listFiles();
        if (fileList == null || fileList.length == 0) {
            return;
        }
        File lastModifiedFile = fileList[0];
        for (File file : logFile.listFiles()) {
            if (lastModifiedFile.lastModified() < file.lastModified()) {
                lastModifiedFile = file;
            }
        }
        List<String> content = FileUtils.readNormalFile(lastModifiedFile.getPath(), false);
        StringBuilder text = new StringBuilder();
        for (String item : content) {
            text.append(item).append(STR_NEXT_LINE);
        }
        tab.setContent(new TextArea(text.toString()));
        tab.setText(tabName);
        functionTab.getTabs().add(tab);
    }

    private void initTabByFile(String filePath, String tabName) throws IOException {
        Tab tab = new Tab();
        File logFile = new File(filePath);
        if (!logFile.exists()) {
            return;
        }
        List<String> content = FileUtils.readNormalFile(filePath, false);
        StringBuilder text = new StringBuilder();
        for (String item : content) {
            text.append(item).append(STR_NEXT_LINE);
        }
        tab.setContent(new TextArea(text.toString()));
        tab.setText(tabName);
        functionTab.getTabs().add(tab);
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
            if (!NEW_MENU_UPDATE.getFileName().equals(fileName)) {
                item = item.replaceAll("--", "");
            }
            text.append(item).append(STR_NEXT_LINE);
        }
        tab.setContent(new TextArea(text.toString()));
    }
}
