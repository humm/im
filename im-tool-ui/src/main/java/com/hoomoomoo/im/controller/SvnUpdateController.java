package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.utils.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.SVN_UPDATE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/09
 */
public class SvnUpdateController implements Initializable {

    @FXML
    private Label workspaceNum;

    @FXML
    private Button svnSubmit;

    @FXML
    private ProgressIndicator schedule;

    @FXML
    private Label svnName;

    @FXML
    private TableView<?> svnLog;

    @FXML
    private TextArea fileLog;

    private double progress = 0;

    @FXML
    void executeSubmit(ActionEvent event) {
        LoggerUtils.info(String.format(STR_MSG_USE, SVN_UPDATE.getName()));
        try {
            if (!CommonUtils.checkConfig(fileLog, FunctionConfig.SVN_UPDATE.getCode())) {
                return;
            }
            setProgress(0);
            OutputUtils.clearLog(svnLog);
            OutputUtils.clearLog(fileLog);
            OutputUtils.clearLog(workspaceNum);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            OutputUtils.info(workspaceNum, String.valueOf(appConfigDto.getSvnUpdatePath().size()));
            updateProgress();
            getSvnUpdate();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + STR_SPACE + e.toString());
        }
    }

    private void getSvnUpdate() {
        new Thread(() -> {
            try {
                svnSubmit.setDisable(true);
                Date date = new Date();
                List<String> updatePath = new ArrayList<>();
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                List<LinkedHashMap<String, String>> pathList = appConfigDto.getSvnUpdatePath();
                if (CollectionUtils.isNotEmpty(pathList)) {
                    for (LinkedHashMap<String, String> item : pathList) {
                        Iterator<String> iterator = item.keySet().iterator();
                        while (iterator.hasNext()) {
                            String name = iterator.next();
                            String path = item.get(name);
                            updatePath.add(path);
                            OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + STR_SPACE + "更新[ " + name + " ]开始\n");
                            OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + STR_SPACE_4 + path + "\n");
                            if (FileUtils.isSuffixDirectory(new File(path), FILE_TYPE_SVN)) {
                                Long version = SvnUtils.updateSvn(path);
                                infoMsg(name, version, "更新完成");
                            } else {
                                OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + STR_SPACE + "[ " + name + " ]非svn目录,无需更新\n");
                                infoMsg(name, -1L, "无需更新");
                            }
                            OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + STR_SPACE + "更新[ " + name + " ]结束\n");
                        }
                        OutputUtils.info(workspaceNum, String.valueOf(Integer.valueOf(workspaceNum.getText()) - 1));
                    }
                }
                setProgress(1);
                LoggerUtils.writeSvnUpdateInfo(date, updatePath);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + STR_SPACE + ExceptionMsgUtils.getMsg(e));
            } finally {
                svnSubmit.setDisable(false);
            }
        }).start();
    }

    private void updateProgress() {
        new Thread(() -> {
            while (true) {
                if (progress >= 0.95) {
                    break;
                }
                if (progress <= 0.6) {
                    setProgress(progress + 0.02);
                } else if (progress < 0.9) {
                    setProgress(progress + 0.01);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LoggerUtils.info(e);
                }
            }
        }).start();
    }

    synchronized private void setProgress(double value) {
        try {
            progress = value;
            Platform.runLater(() -> {
                schedule.setProgress(progress);
            });
            schedule.requestFocus();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            OutputUtils.clearLog(svnName);
            OutputUtils.clearLog(workspaceNum);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (StringUtils.isNotBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(svnName, appConfigDto.getSvnUsername());
            }
            OutputUtils.info(workspaceNum, String.valueOf(appConfigDto.getSvnUpdatePath().size()));
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private void infoMsg(String name, Long version, String msg) {
        LogDto logDto = new LogDto();
        logDto.setTime(CommonUtils.getCurrentDateTime1());
        logDto.setName(name);
        logDto.setVersion(version);
        logDto.setMsg(msg);
        OutputUtils.info(svnLog, logDto);
    }
}
