package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.SvnLogDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.SvnUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.collections.CollectionUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author humm23693
 * @description svn提交文件记录
 * @package com.hoomoomoo.im.controller
 * @date 2021/04/18
 */
public class SvnLogController implements Initializable {

    @FXML
    private TextField svnName;

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

    private double progress = 0;

    @FXML
    void executeSubmit(ActionEvent event) {
        try {
            svnSubmit.setDisable(true);
            setProgress(0);
            LoggerUtils.clearLog(svnLog);
            LoggerUtils.clearLog(fileLog);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfig();
            appConfigDto.setSvnUsername(svnName.getText());
            appConfigDto.setSvnRecentTime(svnTimes.getText());
            new Thread(() -> {
                updateProgress();
            }).start();
            new Thread(() -> {
                getSvnLog(Integer.valueOf(svnTimes.getText()));
            }).start();
        } catch (Exception e) {
            LoggerUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + " " + e.getMessage());
        }
    }

    private void getSvnLog(Integer svnTimes) {
        try {
            List<SvnLogDto> logDtoList = SvnUtils.getSvnLog(svnTimes);
            for (SvnLogDto svnLogDto : logDtoList) {
                LoggerUtils.info(svnLog, svnLogDto);
                LoggerUtils.info(fileLog, svnLogDto.getFile());
            }
            if (CollectionUtils.isEmpty(logDtoList)) {
                LoggerUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + " 未获取到相关提交记录");
            }
            setProgress(1);
            svnSubmit.setDisable(false);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + " " + e.getMessage());
        }
    }

    private void updateProgress() {
        while (true) {
            if (progress >= 0.9) {
                break;
            }
            if (progress <= 0.6) {
                setProgress(progress + 0.08);
            } else if (progress < 0.9) {
                setProgress(progress + 0.03);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized private void setProgress(double value) {
        progress = value;
        Platform.runLater(() -> {
            svnSchedule.setProgress(progress);
        });
        svnSchedule.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LoggerUtils.clearLog(svnName);
            LoggerUtils.clearLog(svnTimes);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfig();
            LoggerUtils.info(svnName, appConfigDto.getSvnUsername());
            LoggerUtils.info(svnTimes, appConfigDto.getSvnRecentTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
