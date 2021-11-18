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
import javafx.scene.input.MouseEvent;
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
 * @description 提交记录
 * @package com.hoomoomoo.im.controller
 * @date 2021/04/18
 */
public class SvnLogController extends BaseController implements Initializable {

    @FXML
    private Label svnName;

    @FXML
    private TextField svnTimes;

    @FXML
    private TextField version;

    @FXML
    private Button svnSubmit;

    @FXML
    private TableView<?> svnLog;

    @FXML
    private TextArea fileLog;

    @FXML
    void showVersion(MouseEvent event) {
        Long ver = ((LogDto)svnLog.getSelectionModel().getSelectedItem()).getVersion();
        OutputUtils.info(version, String.valueOf(ver));
        execute(false);
    }

    @FXML
    void executeSubmit(ActionEvent event) {
        execute(true);
    }

    private void execute(boolean updateLog) {
        LoggerUtils.info(String.format(MSG_USE, SVN_LOG.getName()));
        try {
            if (!CommonUtils.checkConfig(fileLog, FunctionConfig.SVN_LOG.getCode())) {
                return;
            }
            setProgress(0);
            if (updateLog) {
                OutputUtils.clearLog(svnLog);
            }
            OutputUtils.clearLog(fileLog);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            appConfigDto.setSvnRecentTime(svnTimes.getText());
            String times = svnTimes.getText().trim();
            String ver = version.getText().trim();
            if (StringUtils.isBlank(times) && StringUtils.isBlank(ver)) {
                return;
            }
            if (StringUtils.isBlank(times)) {
                times = STR_0;
            }
            if (StringUtils.isBlank(ver)) {
                ver = STR_0;
            }
            updateProgress();
            getSvnLog(Integer.valueOf(times), Integer.valueOf(ver), updateLog);
        } catch (NumberFormatException e) {
            LoggerUtils.info(e);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + SYMBOL_SPACE + e.toString());
        }
    }

    private void getSvnLog(int times, int version, boolean updateLog) {
        new Thread(() -> {
            try {
                svnSubmit.setDisable(true);
                Date date = new Date();
                List<LogDto> logDtoList = SvnUtils.getSvnLog(times, version);
                List<String> fileList = new ArrayList<>();
                int length = logDtoList.size();
                for (LogDto svnLogDto : logDtoList) {
                    svnLogDto.setGetNum(length);
                    svnLogDto.setMatch(times == length ? "匹配" : "未匹配");
                    if (updateLog) {
                        OutputUtils.info(svnLog, svnLogDto);
                    }
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
