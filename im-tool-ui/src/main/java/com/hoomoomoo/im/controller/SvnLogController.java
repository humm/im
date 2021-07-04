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

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.SVN_LOG;

/**
 * @author humm23693
 * @description svn提交记录
 * @package com.hoomoomoo.im.controller
 * @date 2021/04/18
 */
public class SvnLogController implements Initializable {

    @FXML
    private Label svnName;

    @FXML
    private TextField svnTimes;

    @FXML
    private Button svnSubmit;

    @FXML
    private TableView<?> svnLog;

    @FXML
    private TextArea fileLog;

    @FXML
    private ProgressIndicator svnSchedule;

    @FXML
    private Label scheduleText;

    private double progress = 0;

    @FXML
    void executeSubmit(ActionEvent event) {
        LoggerUtils.info(String.format(MSG_USE, SVN_LOG.getName()));
        try {
            if (!CommonUtils.checkConfig(fileLog, FunctionConfig.SVN_LOG.getCode())) {
                return;
            }
            setProgress(0);
            OutputUtils.clearLog(svnLog);
            OutputUtils.clearLog(fileLog);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            appConfigDto.setSvnRecentTime(svnTimes.getText());
            updateProgress();
            getSvnLog(Integer.valueOf(svnTimes.getText()));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + SYMBOL_SPACE + e.toString());
        }
    }

    private void getSvnLog(Integer svnTimes) {
        new Thread(() -> {
            try {
                svnSubmit.setDisable(true);
                Date date = new Date();
                List<LogDto> logDtoList = SvnUtils.getSvnLog(svnTimes);
                List<String> fileList = new ArrayList<>();
                for (LogDto svnLogDto : logDtoList) {
                    OutputUtils.info(svnLog, svnLogDto);
                    OutputUtils.info(fileLog, svnLogDto.getFile());
                    fileList.addAll(svnLogDto.getFile());
                }
                if (CollectionUtils.isEmpty(logDtoList)) {
                    OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + " 未获取到相关提交记录\n");
                } else {
                    AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                    if (appConfigDto.getSvnDefaultAppendBiz() && StringUtils.isNotBlank(appConfigDto.getSvnDefaultAppendPath())) {
                        for (String file : fileList) {
                            if (file.trim().endsWith(FILE_TYPE_VUE)) {
                                OutputUtils.info(fileLog, appConfigDto.getSvnDefaultAppendPath());
                                break;
                            }
                        }
                    }
                }
                setProgress(1);
                LoggerUtils.writeSvnLogInfo(date, logDtoList);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + SYMBOL_SPACE + ExceptionMsgUtils.getMsg(e));
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
                    setProgress(progress + 0.05);
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
                svnSchedule.setProgress(progress);
                scheduleText.setText(String.valueOf(value * 100).split(SYMBOL_POINT_SLASH)[0] + SYMBOL_PERCENT);
                svnSchedule.requestFocus();
            });
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            OutputUtils.clearLog(svnName);
            OutputUtils.clearLog(svnTimes);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (StringUtils.isNotBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(svnName, appConfigDto.getSvnUsername());
            }
            if (StringUtils.isNotBlank(appConfigDto.getSvnRecentTime())) {
                OutputUtils.info(svnTimes, appConfigDto.getSvnRecentTime());
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
