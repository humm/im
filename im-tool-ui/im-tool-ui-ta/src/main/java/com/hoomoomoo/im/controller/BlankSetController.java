package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.extend.ScriptRepairSql;
import com.hoomoomoo.im.task.BlankSetTask;
import com.hoomoomoo.im.task.BlankSetTaskParam;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import com.hoomoomoo.im.utils.TaskUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.ERROR_LOG;

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
            SQL_CHECK_TYPE[] checkType = SQL_CHECK_TYPE.values();
            if (PAGE_TYPE_SYSTEM_TOOL_REPAIR_ERROR_LOG.equals(appConfigDto.getPageType())) {
                String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
                confPath = resultPath + "\\" + ERROR_LOG.getFileName();
            } else {
                for (SQL_CHECK_TYPE item : checkType) {
                    String index = String.valueOf(item.getIndex());
                    if (appConfigDto.getPageType().equals(index)) {
                        String pathConf = item.getPathConf();
                        confPath = FileUtils.getFilePath(pathConf);
                        break;
                    }
                }
                if (StringUtils.isBlank(confPath)) {
                    SQL_CHECK_TYPE_EXTEND[] checkTypeExtend = SQL_CHECK_TYPE_EXTEND.values();
                    for (SQL_CHECK_TYPE_EXTEND item : checkTypeExtend) {
                        String index = String.valueOf(item.getIndex());
                        if (appConfigDto.getPageType().equals(index)) {
                            String pathConf = item.getPathConf();
                            confPath = FileUtils.getFilePath(pathConf);
                            break;
                        }
                    }
                }
            }
            FileUtils.writeFile(confPath, content, false);
        }
        if (PAGE_TYPE_SYSTEM_TOOL_REPAIR_ERROR_LOG.equals(appConfigDto.getPageType())) {
            TaskUtils.execute(new BlankSetTask(new BlankSetTaskParam(this)));
        }
        appConfigDto.getChildStage().close();
        appConfigDto.setChildStage(null);
    }

    public void repairErrorLog() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        try {
            appConfigDto.setExecute(true);
            ScriptRepairSql.repairErrorLog();
        } catch (Exception e) {
            LoggerUtils.info(e);
        } finally {
            appConfigDto.setExecute(false);
        }
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
                if ("缺陷".equals(hepTaskDto.getTaskMark())) {
                    info = hepTaskDto.getName();
                }
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
            SQL_CHECK_TYPE[] checkType = SQL_CHECK_TYPE.values();
            if (PAGE_TYPE_SYSTEM_TOOL_REPAIR_ERROR_LOG.equals(appConfigDto.getPageType())) {
                String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
                confPath = resultPath + "\\" + ERROR_LOG.getFileName();
                submit.setText("修复");
            } else {
                for (SQL_CHECK_TYPE item : checkType) {
                    String index = String.valueOf(item.getIndex());
                    if (appConfigDto.getPageType().equals(index)) {
                        String pathConf = item.getPathConf();
                        confPath = FileUtils.getFilePath(pathConf);
                        break;
                    }
                }
                if (StringUtils.isBlank(confPath)) {
                    SQL_CHECK_TYPE_EXTEND[] checkTypeExtend = SQL_CHECK_TYPE_EXTEND.values();
                    for (SQL_CHECK_TYPE_EXTEND item : checkTypeExtend) {
                        String index = String.valueOf(item.getIndex());
                        if (appConfigDto.getPageType().equals(index)) {
                            String pathConf = item.getPathConf();
                            confPath = FileUtils.getFilePath(pathConf);
                            break;
                        }
                    }
                }
            }
            if (!new File(confPath).exists()) {
                LoggerUtils.info("文件不存在: " + confPath);
                return;
            }
            List<String> content = FileUtils.readNormalFile(confPath, false);
            for (String item : content) {
                if (StringUtils.isBlank(item) && !PAGE_TYPE_SYSTEM_TOOL_REPAIR_ERROR_LOG.equals(appConfigDto.getPageType())) {
                    continue;
                }
                if (PAGE_TYPE_SYSTEM_TOOL_REPAIR_ERROR_LOG.equals(appConfigDto.getPageType())) {
                    item = item.replace(ANNOTATION_NORMAL, STR_BLANK);
                }
                OutputUtils.info(config, item + STR_NEXT_LINE);
            }
        }
    }
}
