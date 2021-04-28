package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.SvnLogDto;
import com.hoomoomoo.im.utils.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.collections.CollectionUtils;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.STR_SPACE;

/**
 * @author humm23693
 * @description svn提交记录
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
            setProgress(0);
            OutputUtils.clearLog(svnLog);
            OutputUtils.clearLog(fileLog);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfig();
            appConfigDto.setSvnUsername(svnName.getText());
            appConfigDto.setSvnRecentTime(svnTimes.getText());
            updateProgress();
            getSvnLog(Integer.valueOf(svnTimes.getText()));
        } catch (Exception e) {
            OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + STR_SPACE + e.getMessage());
        }
    }

    private void getSvnLog(Integer svnTimes) {
        new Thread(() -> {
            try {
                Date date = new Date();
                svnSubmit.setDisable(true);
                List<SvnLogDto> logDtoList = SvnUtils.getSvnLog(svnTimes);
                for (SvnLogDto svnLogDto : logDtoList) {
                    OutputUtils.info(svnLog, svnLogDto);
                    OutputUtils.info(fileLog, svnLogDto.getFile());
                }
                if (CollectionUtils.isEmpty(logDtoList)) {
                    OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + " 未获取到相关提交记录\n");
                }
                setProgress(1);
                LoggerUtils.writeSvnLogInfo(date, logDtoList);
            } catch (Exception e) {
                e.printStackTrace();
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
                    setProgress(progress + 0.05);
                } else if (progress < 0.9) {
                    setProgress(progress + 0.01);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
            OutputUtils.clearLog(svnName);
            OutputUtils.clearLog(svnTimes);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfig();
            OutputUtils.info(svnName, appConfigDto.getSvnUsername());
            OutputUtils.info(svnTimes, appConfigDto.getSvnRecentTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
