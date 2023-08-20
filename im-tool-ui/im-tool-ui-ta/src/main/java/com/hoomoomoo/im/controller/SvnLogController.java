package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.utils.*;
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
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.SVN_LOG;

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
    private ComboBox svnVersion;

    private int maxVersionConfig = 0;

    @FXML
    void showVersion(MouseEvent event) {
        String ver = ((LogDto)svnLog.getSelectionModel().getSelectedItem()).getVersion();
        String serialNo = ((LogDto)svnLog.getSelectionModel().getSelectedItem()).getSerialNo();
        String codeVersion = ((LogDto)svnLog.getSelectionModel().getSelectedItem()).getCodeVersion();
        svnVersion.getSelectionModel().select(codeVersion);
        OutputUtils.clearLog(version);
        OutputUtils.info(version, ver);
        OutputUtils.clearLog(modifyNo);
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
        OutputUtils.clearLog(svnTimes);
        OutputUtils.info(svnTimes, appConfigDto.getSvnRecentTime());
        svnVersion.getSelectionModel().select(KEY_TRUNK);
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
                if (!TaCommonUtils.checkConfig(fileLog, SVN_LOG.getCode())) {
                    return;
                }
                Thread.sleep(1000);
                setProgress(0);
                OutputUtils.clearLog(fileLog);
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
                OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + e.getMessage());
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
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                List<LogDto> logDtoList = new ArrayList<>(16);
                int maxTime = times;
                if (StringUtils.isNotBlank(modifyNo)) {
                    maxTime = Integer.valueOf(appConfigDto.getSvnTaskMaxRevision());
                    appConfigDto.setSvnMaxRevision(String.valueOf(appConfigDto.getSvnTaskMaxRevision()));
                } else {
                    appConfigDto.setSvnMaxRevision(String.valueOf(maxVersionConfig));
                }
                String versionValue = String.valueOf(svnVersion.getValue());
                if (KEY_TRUNK.equals(versionValue)) {
                    appConfigDto.setSvnRep(appConfigDto.getSvnUrl().get(KEY_TRUNK));
                } else {
                    String svnUrl = appConfigDto.getSvnUrl().get(KEY_BRANCHES);
                    if (versionValue.contains(KEY_FUND)) {
                        svnUrl = TaCommonUtils.getSvnUrl(versionValue, svnUrl);
                        versionValue += KEY_SOURCES_TA_FUND;
                    }
                    appConfigDto.setSvnRep(svnUrl + versionValue);
                }
                logDtoList.addAll(SvnUtils.getSvnLog(version, modifyNo));
                Collections.sort(logDtoList, new Comparator<LogDto>() {
                    @Override
                    public int compare(LogDto o1, LogDto o2) {
                        return o1.compareTo(o2);
                    }
                });
                if (CollectionUtils.isEmpty(logDtoList)) {
                    OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + " 未获取到相关提交记录\n");
                } else {
                    String codeVersion = (String)svnVersion.getSelectionModel().getSelectedItem();
                    if (StringUtils.isNotBlank(codeVersion)) {
                        List<LogDto> temp = new ArrayList<>(16);
                        for (LogDto item : logDtoList) {
                            if (item.getCodeVersion().equals(codeVersion) || (KEY_B + item.getCodeVersion()).equals(codeVersion)) {
                                temp.add(item);
                            }
                        }
                        logDtoList = temp;
                    }
                    if (CollectionUtils.isEmpty(logDtoList)) {
                        OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + " 未获取到相关提交记录\n");
                    } else {
                        if (logDtoList.size() > maxTime) {
                            logDtoList = logDtoList.subList(0, maxTime);
                        }
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
                                    if (!fileList.contains(item) && isFileLocation(item)) {
                                        fileList.add(item);
                                    }
                                }
                            } else {
                                String msg = svnLogDto.getMsg();
                                if (StringUtils.isBlank(msg)) {
                                    continue;
                                }
                                msg = msg.trim() + STR_NEXT_LINE;
                                if (!fileList.contains(msg)) {
                                    fileList.add(msg);
                                }
                            }
                        }
                        OutputUtils.info(fileLog, fileList);
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
                }
                setProgress(1);
                LoggerUtils.writeSvnLogInfo(date, logDtoList);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + STR_SPACE + ExceptionMsgUtils.getMsg(e));
            } finally {
                svnSubmit.setDisable(false);
                svnResetSubmit.setDisable(false);
                svnDescribe.setDisable(false);
                copy.setDisable(false);
            }
        }).start();
    }

    @FXML
    void selectSvnVersion(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            String version = (String)svnVersion.getSelectionModel().getSelectedItem();
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
            ObservableList svnItems = svnVersion.getItems();
            Map<String, String> svnVersionMap = appConfigDto.getCopyCodeVersion();
            String maxVer = "";
            if (MapUtils.isNotEmpty(svnVersionMap)) {
                Iterator<String> version = svnVersionMap.keySet().iterator();
                while (version.hasNext()) {
                    String ver = version.next();
                    if (maxVer.compareTo(ver) == -1) {

                    }
                    if (KEY_DESKTOP.equals(ver)) {
                        continue;
                    }
                    svnItems.add(ver);
                }
            }
            svnVersion.getSelectionModel().select(KEY_TRUNK);
            maxVersionConfig = Integer.valueOf(appConfigDto.getSvnMaxRevision());
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private boolean isFileLocation(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }
        int index = path.lastIndexOf(STR_SLASH);
        String fileName = path.substring(index + 1);
        if (fileName.contains(STR_POINT)) {
            return true;
        }
        return false;
    }
}
