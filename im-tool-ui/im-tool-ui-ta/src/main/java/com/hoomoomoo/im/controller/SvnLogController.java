package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
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
    private TextField modifyNo;

    @FXML
    private Button svnSubmit;

    @FXML
    private Button svnResetSubmit;

    @FXML
    private Button svnDescribe;

    @FXML
    private Button copy;

    @FXML
    private TableView svnLog;

    @FXML
    private TextArea fileLog;

    @FXML
    private ComboBox svnRep;

    @FXML
    void showVersion(MouseEvent event) {
        String ver = ((LogDto)svnLog.getSelectionModel().getSelectedItem()).getVersion();
        String serialNo = ((LogDto)svnLog.getSelectionModel().getSelectedItem()).getSerialNo();
        OutputUtils.info(version, ver);
        OutputUtils.info(modifyNo, serialNo);
        execute(false, STR_1);
    }

    @FXML
    void executeSubmit(ActionEvent event) {
        execute(true, STR_1);
    }

    @FXML
    void executeDescribe(ActionEvent event) {
        execute(true, STR_2);
    }

    @FXML
    void svnResetSubmit(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        OutputUtils.clearLog(modifyNo);
        OutputUtils.info(svnTimes, appConfigDto.getSvnRecentTime());
        execute(true, STR_1);
    }

    @FXML
    void executeCopy(ActionEvent event) {
        schedule.requestFocus();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(fileLog.getText()), null);
    }

    private void execute(boolean updateLog, String type) {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, SVN_LOG.getName()));
        if (updateLog) {
            OutputUtils.clearLog(version);
            OutputUtils.clearLog(svnLog);
        }
        new Thread(() -> {
            try {
                if (!TaCommonUtil.checkConfig(fileLog, SVN_LOG.getCode())) {
                    return;
                }
                Thread.sleep(1000);
                setProgress(0);
                OutputUtils.clearLog(fileLog);
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                appConfigDto.setSvnRep((String)svnRep.getSelectionModel().getSelectedItem());
                String times = svnTimes.getText().trim();
                String ver = version.getText().trim();
                String modify = modifyNo.getText().trim();
                if (StringUtils.isBlank(times) && StringUtils.isBlank(ver)) {
                    return;
                }
                if (StringUtils.isBlank(times)) {
                    times = BaseConst.STR_0;
                }
                if (StringUtils.isBlank(ver)) {
                    ver = BaseConst.STR_0;
                }
                updateProgress();
                getSvnLog(Integer.valueOf(times), Integer.valueOf(ver), modify, updateLog, type);
            } catch (NumberFormatException e) {
                LoggerUtils.info(e);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + e.getMessage());
            }
        }).start();
    }

    private void getSvnLog(int times, int version, String modifyNo, boolean updateLog, String type) {
        new Thread(() -> {
            try {
                svnSubmit.setDisable(true);
                svnResetSubmit.setDisable(true);
                svnDescribe.setDisable(true);
                copy.setDisable(true);
                Date date = new Date();
                List<LogDto> logDtoList = SvnUtils.getSvnLog(times, version, modifyNo);
                if (CollectionUtils.isEmpty(logDtoList)) {
                    OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + " 未获取到相关提交记录\n");
                } else {
                    List<String> fileList = new ArrayList<>();
                    int length = logDtoList.size();
                    for (LogDto svnLogDto : logDtoList) {
                        svnLogDto.setGetNum(String.valueOf(length));
                        svnLogDto.setMatch((times == length || StringUtils.isNotBlank(modifyNo)) ? "匹配" : "未匹配");
                        if (updateLog) {
                            OutputUtils.info(svnLog, svnLogDto);
                        }
                        if (STR_1.equals(type)) {
                            for (String item : svnLogDto.getFile()) {
                                if (!fileList.contains(item)) {
                                    fileList.add(item);
                                }
                            }
                        } else {
                            if (!fileList.contains(svnLogDto.getMsg() + SYMBOL_NEXT_LINE)) {
                                fileList.add(svnLogDto.getMsg() + SYMBOL_NEXT_LINE);
                            }
                        }
                    }
                    OutputUtils.info(fileLog, fileList);
                    AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                    if (appConfigDto.getSvnDefaultAppendBiz() && StringUtils.isNotBlank(appConfigDto.getSvnDefaultAppendPath())) {
                        for (String file : fileList) {
                            if (file.trim().endsWith(BaseConst.FILE_TYPE_VUE)) {
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
                OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + ExceptionMsgUtils.getMsg(e));
            } finally {
                svnSubmit.setDisable(false);
                svnResetSubmit.setDisable(false);
                svnDescribe.setDisable(false);
                copy.setDisable(false);
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
            OutputUtils.info(fileLog, e.getMessage());
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
