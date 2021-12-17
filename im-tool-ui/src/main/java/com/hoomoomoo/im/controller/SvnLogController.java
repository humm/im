package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.utils.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.*;
import java.util.List;

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
    private Button copy;

    @FXML
    private TableView<?> svnLog;

    @FXML
    private TextArea fileLog;

    @FXML
    private ComboBox<?> svnRep;

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

    @FXML
    void executeCopy(ActionEvent event) {
        schedule.requestFocus();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(fileLog.getText()), null);
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
            appConfigDto.setSvnRep((String)svnRep.getSelectionModel().getSelectedItem());
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
                                String prefix = file.substring(0, file.indexOf(appConfigDto.getSvnStartPrefix()));
                                OutputUtils.info(fileLog, prefix + appConfigDto.getSvnDefaultAppendPath());
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

    @FXML
    void selectSvnRep(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            String version = (String)svnRep.getSelectionModel().getSelectedItem();
            appConfigDto.setSvnRep(version);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(fileLog, e.toString());
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
            ObservableList svnRepItems = svnRep.getItems();
            Map<String, String> svnRepVersion = appConfigDto.getSvnUrl();
            if (MapUtils.isNotEmpty(svnRepVersion)) {
                Iterator<String> version = svnRepVersion.keySet().iterator();
                while (version.hasNext()) {
                    String ver = version.next();
                    svnRepItems.add(ver);
                }
            }
            if (MapUtils.isNotEmpty(svnRepVersion)) {
                svnRep.getSelectionModel().select(0);
                appConfigDto.setSvnRep((String)svnRep.getSelectionModel().getSelectedItem());
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
