package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSONArray;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.HepTaskComponentDto;
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
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.controller.HepTodoController.*;

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
    private TextArea selfTestDesc;

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
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
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
            String msg = TaCommonUtils.formatText(item.getMsg().trim(), false);
            if (editDescriptionValue.indexOf(msg) != -1) {
                continue;
            }
            editDescriptionValue.append(STR_SLASH_T + msg).append(STR_NEXT_LINE);
        }
        OutputUtils.repeatInfo(modifiedFile, modifiedFileValue.toString());
        OutputUtils.repeatInfo(editDescription, editDescriptionValue.toString().replaceAll(STR_SLASH_T, STR_BLANK));
        if (StringUtils.isBlank(editDescriptionValue)) {
            editDescriptionValue.append(STR_NEXT_LINE);
        }
        StringBuilder suggestionMsg = new StringBuilder();
        suggestionMsg.append("【功能入口】\n");
        suggestionMsg.append("\t\n");
        suggestionMsg.append("【测试准备】\n");
        suggestionMsg.append("\t更新ta-web-manager-fund-core等前台相关包及console包，执行升级脚本\n");
        suggestionMsg.append("【测试场景及预期结果】\n");
        suggestionMsg.append(editDescriptionValue);
        suggestionMsg.append("【影响范围】\n");
        suggestionMsg.append("\t\n");
        suggestionMsg.append("【其他】");
        OutputUtils.repeatInfo(suggestion, suggestionMsg.toString());
        StringBuilder selfTestDescMsg = new StringBuilder();
        selfTestDescMsg.append("【是否已自测(必填)】\n");
        selfTestDescMsg.append("\t已自测\n");
        selfTestDescMsg.append("【自测场景说明(必填)】\n");
        selfTestDescMsg.append("\t\n");
        selfTestDescMsg.append("【自测未覆盖范围(必填)】\n");
        selfTestDescMsg.append("\t无\n");
        selfTestDescMsg.append("【其他】");
        OutputUtils.repeatInfo(selfTestDesc, selfTestDescMsg.toString());
        if (StringUtils.isBlank(modifiedFileValue)) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDate("未查询到修改记录信息,请检查"));
        } else {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDate("修改记录信息同步完成"));
        }
        sync.setDisable(false);
        execute.setDisable(false);
    }

    private String getVersion(AppConfigDto appConfigDto, String ver) {
       ver = CommonUtils.getComplexVer(ver);
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
                    if (!ver.endsWith("000")) {
                        isTrunk = false;
                        break;
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
            resVer = TaCommonUtils.changeVersion(ver);
        }
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
        String selfTestDescValue = selfTestDesc.getText();
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
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
            if (StringUtils.isBlank(selfTestDescValue)) {
                tips.append("【自测说明】").append(STR_NEXT_LINE);
            }
        }
        if (StringUtils.isNotBlank(tips)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("校验失败，如下信息不能为空");
            alert.setHeaderText(tips.toString());
            alert.showAndWait();
            return;
        }

        hepTaskDto.setRealWorkload(realRorkloadValue.trim());
        hepTaskDto.setRealFinishTime(realFinishTimeValue + STR_SPACE +CommonUtils.getCurrentDateTime8(new Date()));
        hepTaskDto.setModifiedFile(TaCommonUtils.formatText(modifiedFileValue, true));
        hepTaskDto.setEditDescription(TaCommonUtils.formatText(editDescriptionValue, true));
        hepTaskDto.setSuggestion(TaCommonUtils.formatText(suggestionValue, true));
        hepTaskDto.setSelfTestDesc(TaCommonUtils.formatTextOnlyBr(selfTestDescValue));
        HepTodoController hep = JvmCache.getHepTodoController();
        hep.execute(OPERATE_COMPLETE, hepTaskDto);
        if (!OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
            JSONArray res = hep.execute(OPERATE_COMPLETE_QUERY, hepTaskDto);
            hep.dealTaskList(res, true);
        }
        appConfigDto.getChildStage().close();
        appConfigDto.setChildStage(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
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
                OutputUtils.repeatInfo(realRorkload, formatText(hepTaskDto.getRealWorkload()));
            }
            OutputUtils.repeatInfo(modifiedFile, hepTaskDto.getModifiedFile());
            OutputUtils.repeatInfo(editDescription, formatText(hepTaskDto.getEditDescription()));
            OutputUtils.repeatInfo(suggestion, formatText(hepTaskDto.getSuggestion()));
            OutputUtils.repeatInfo(selfTestDesc, formatText(hepTaskDto.getSelfTestDesc()));
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

    private String formatText(String text) {
        if (StringUtils.isBlank(text)) {
            return STR_BLANK;
        }
        return text.replaceAll("<p>", STR_BLANK).replaceAll("</p>", STR_BLANK).replaceAll("&nbsp;", STR_SPACE).replaceAll("<br>", STR_BLANK).replaceAll("\r", STR_BLANK);
    }
}
