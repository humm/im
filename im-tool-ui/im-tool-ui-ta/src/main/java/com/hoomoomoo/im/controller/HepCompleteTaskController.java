package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSONArray;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.HepTaskComponent;
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.utils.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.controller.HepTaskTodoController.*;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2022/07/30
 */
public class HepCompleteTaskController extends BaseController implements Initializable {

    @FXML
    private Label id;

    @FXML
    private Label taskNumber;

    @FXML
    private TextField realRorkload;

    @FXML
    private DatePicker realFinishTime;

    @FXML
    private TextArea modifiedFile;

    @FXML
    private TextArea editDescription;

    @FXML
    private TextArea suggestion;

    @FXML
    private Button sync;

    @FXML
    private Button execute;

    @FXML
    private Label notice;

    @FXML
    void changeRealFinishTime(ActionEvent event) {

    }

    @FXML
    void syncSvn(ActionEvent event) throws Exception {
        execute.setDisable(true);
        sync.setDisable(true);
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        HepTaskDto hepTaskDto = appConfigDto.getHepTaskDto();
        String taskNumber = hepTaskDto.getTaskNumber();
        List<LogDto> logDtoList = new ArrayList<>(16);
        String versionValue = getVersion(appConfigDto, hepTaskDto.getSprintVersion());
        if (KEY_TRUNK.equals(versionValue)) {
            appConfigDto.setSvnRep(appConfigDto.getSvnUrl().get(KEY_TRUNK));
            LoggerUtils.info("svn仓库地址为: " + appConfigDto.getSvnUrl().get(KEY_TRUNK));
        } else {
            String svnUrl = appConfigDto.getSvnUrl().get(KEY_BRANCHES);
            if (versionValue.contains(KEY_FUND)) {
                svnUrl = TaCommonUtils.getSvnUrl(versionValue, svnUrl);
                versionValue += KEY_SOURCES_TA_FUND;
            }
            String svnRep = svnUrl + versionValue;
            LoggerUtils.info("svn仓库地址为: " + svnRep);
            appConfigDto.setSvnRep(svnRep);
        }
        try {
            logDtoList.addAll(SvnUtils.getSvnLog(0, taskNumber));
        } catch (Exception e) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDate("修改记录信息同步异常,请检查"));
            LoggerUtils.info(e);
            sync.setDisable(false);
            execute.setDisable(false);
            return;
        }
        Collections.sort(logDtoList, new Comparator<LogDto>() {
            @Override
            public int compare(LogDto o1, LogDto o2) {
                return o1.compareTo(o2);
            }
        });
        StringBuilder modifiedFileValue = new StringBuilder();
        StringBuilder editDescriptionValue = new StringBuilder();
        for (int i=0; i<logDtoList.size(); i++) {
            LogDto item = logDtoList.get(i);
            List<String> fileList = item.getFile();
            for (int j=0; j<fileList.size(); j++) {
                String file = TaCommonUtils.formatText(fileList.get(j), false);
                if (modifiedFileValue.indexOf(file) != -1) {
                    continue;
                }
                if (StringUtils.isNotBlank(modifiedFileValue)) {
                    modifiedFileValue.append(STR_NEXT_LINE);
                }
                modifiedFileValue.append(file);
            }
            String msg = TaCommonUtils.formatText(item.getMsg(), false);
            if (editDescriptionValue.indexOf(msg) != -1) {
                continue;
            }
            editDescriptionValue.append(msg);
            if (i != logDtoList.size() - 1) {
                editDescriptionValue.append(STR_NEXT_LINE);
            }
        }
        OutputUtils.repeatInfo(modifiedFile, modifiedFileValue.toString());
        OutputUtils.repeatInfo(editDescription, editDescriptionValue.toString());
        OutputUtils.repeatInfo(suggestion, editDescriptionValue.toString());
        if (StringUtils.isBlank(modifiedFileValue)) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDate("未查询到修改记录信息,请检查"));
        } else {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDate("修改记录信息同步完成"));
        }
        sync.setDisable(false);
        execute.setDisable(false);
    }

    private String getVersion(AppConfigDto appConfigDto, String ver) {
        String resVer;
        boolean isTrunk = true;
        if (ver.contains(KEY_FUND)) {
            Map<String, String> svnVersionMap = appConfigDto.getCopyCodeVersion();
            if (MapUtils.isNotEmpty(svnVersionMap)) {
                Iterator<String> version = svnVersionMap.keySet().iterator();
                while (version.hasNext()) {
                    String verTmp = version.next();
                    if (KEY_DESKTOP.equals(verTmp) || KEY_TRUNK.equals(verTmp) || !verTmp.contains(KEY_FUND)) {
                        continue;
                    }
                    if (ver.compareTo(verTmp) <= 0) {
                        isTrunk = false;
                        break;
                    }
                }
            }
        } else {
            isTrunk = false;
        }
        if (isTrunk) {
            resVer = KEY_TRUNK;
        } else {
            if (ver.contains("M")) {
                resVer = ver.substring(0, ver.lastIndexOf("M") + 1) + "1";
            } else if (ver.endsWith("000")) {
                resVer = ver;
            } else {
                resVer = ver.substring(0, ver.lastIndexOf(".") + 1) + "001";
            }
        }
        if ("TA6.0V202202.02.001".equals(resVer)) {
            resVer = KEY_B + resVer;
        }
        LoggerUtils.info("转换前版本号为: " + ver);
        LoggerUtils.info("转换后版本号为: " + resVer);
        return resVer;
    }

    @FXML
    void execute(ActionEvent event) throws Exception {
        StringBuilder tips = new StringBuilder();
        String realRorkloadValue = realRorkload.getText();
        String realFinishTimeValue = STR_BLANK;
        if (realFinishTime.getValue() != null) {
            realFinishTimeValue = realFinishTime.getValue().toString();
        }
        String modifiedFileValue = modifiedFile.getText();
        String editDescriptionValue = editDescription.getText();
        String suggestionValue = suggestion.getText();
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        HepTaskDto hepTaskDto = appConfigDto.getHepTaskDto();
        if (StringUtils.isBlank(realRorkloadValue)) {
            tips.append("【耗费工时】").append(STR_NEXT_LINE);
        }
        if (StringUtils.isBlank(realFinishTimeValue)) {
            tips.append("【完成时间】").append(STR_NEXT_LINE);
        }
        if (!OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
            if (StringUtils.isBlank(modifiedFileValue)) {
                tips.append("【修改文件】").append(STR_NEXT_LINE);
            }
            if (StringUtils.isBlank(editDescriptionValue)) {
                tips.append("【修改说明】").append(STR_NEXT_LINE);
            }
            if (StringUtils.isBlank(suggestionValue)) {
                tips.append("【测试建议】").append(STR_NEXT_LINE);
            }
        }
        if (StringUtils.isNotBlank(tips)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("校验失败，如下信息不能为空");
            alert.setHeaderText(tips.toString());
            Optional<ButtonType> res = alert.showAndWait();
            return;
        }

        hepTaskDto.setRealWorkload(realRorkloadValue.trim());
        hepTaskDto.setRealFinishTime(realFinishTimeValue + STR_SPACE +CommonUtils.getCurrentDateTime8(new Date()));
        hepTaskDto.setModifiedFile(TaCommonUtils.formatText(modifiedFileValue, true));
        hepTaskDto.setEditDescription(TaCommonUtils.formatText(editDescriptionValue, true));
        hepTaskDto.setSuggestion(TaCommonUtils.formatText(suggestionValue, true));
        HepTaskTodoController hep = new HepTaskTodoController();
        hep.execute(OPERATE_COMPLETE, hepTaskDto);
        HepTaskComponent hepTaskComponent = appConfigDto.getHepTaskComponent();
        JSONArray res = hep.execute(OPERATE_COMPLETE_QUERY, hepTaskDto);
        hep.dealTaskList(res, hepTaskComponent.getLogs(), hepTaskComponent.getWaitHandleTaskNum(), hepTaskComponent.getTaskList(), true);
        Stage taskStage = appConfigDto.getTaskStage();
        taskStage.close();
        appConfigDto.setTaskStage(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            HepTaskDto hepTaskDto = appConfigDto.getHepTaskDto();
            OutputUtils.clearLog(modifiedFile);
            OutputUtils.clearLog(editDescription);
            OutputUtils.clearLog(suggestion);
            OutputUtils.repeatInfo(id, hepTaskDto.getId());
            OutputUtils.repeatInfo(taskNumber, hepTaskDto.getTaskNumber());
            OutputUtils.repeatInfo(realRorkload, appConfigDto.getHepTaskTodoCostTime());
            realFinishTime.setValue(LocalDate.now());
            if (OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
                realFinishTime.setDisable(true);
                sync.setDisable(true);
                OutputUtils.repeatInfo(modifiedFile, hepTaskDto.getModifiedFile());
                OutputUtils.repeatInfo(editDescription, hepTaskDto.getEditDescription());
                OutputUtils.repeatInfo(suggestion, hepTaskDto.getSuggestion());
                OutputUtils.repeatInfo(realRorkload, hepTaskDto.getRealWorkload());

            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    modifiedFile.requestFocus();
                }
            });
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
