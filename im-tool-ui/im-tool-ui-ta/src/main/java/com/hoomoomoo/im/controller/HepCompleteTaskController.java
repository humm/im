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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
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
        String versionValueYear = versionValue.replaceAll(STR_VERSION_PREFIX, STR_BLANK).split("\\.")[0];
        if (KEY_TRUNK.equals(versionValue) || (versionValueYear.compareTo("2025") >= 0 && !versionValue.contains("2022"))) {
            String url = appConfigDto.getSvnUrl().get(KEY_TRUNK);
            if (!versionValue.endsWith("000")) {
                url = appConfigDto.getSvnUrl().get(KEY_GIT_BRANCHES);
            }
            appConfigDto.setSvnRep(url);
            LoggerUtils.info("git仓库地址为: " + url);
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
            logDtoList.addAll(SvnUtils.getSvnLog(10, taskNumber));
        } catch (Exception e) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTime("修改记录信息同步异常,请检查"));
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
        String taskDesc = STR_BLANK;
        try {
            String fileName = hepTaskDto.getOriTaskName().replaceAll(STR_COLON, STR_BLANK) + FILE_TYPE_STAT;
            String path = FileUtils.getFilePath(PATH_DEFINE_HEP_STAT + fileName);
            taskDesc = FileUtils.readNormalFileToString(path, false);
        } catch (Exception e) {
        }
        if (StringUtils.isNotBlank(taskDesc)) {
            String[] parts = taskDesc.split(MSG_TASK_DIVIDE_LINE);
            if (parts.length == 4) {
                String taskNumberIn = TaCommonUtils.formatTextBrToNextLine(parts[0]);
                if (!StringUtils.equals(taskNumber, taskNumberIn)) {
                    String editDescriptionIn = TaCommonUtils.formatTextBrToNextLine(parts[1]);
                    if (CollectionUtils.isEmpty(logDtoList)) {
                        OutputUtils.repeatInfo(editDescription, TaCommonUtils.formatText(editDescriptionIn));
                    }
                    String suggestionIn = TaCommonUtils.formatTextBrToNextLine(parts[2]);
                    String selfTestDescIn = TaCommonUtils.formatTextBrToNextLine(parts[3]);
                    OutputUtils.repeatInfo(suggestion,  TaCommonUtils.formatText(suggestionIn));
                    OutputUtils.repeatInfo(selfTestDesc,  TaCommonUtils.formatText(selfTestDescIn));
                } else {
                    setDefaultTaskDesc(editDescriptionValue.toString());
                }
            } else {
                OutputUtils.info(notice, TaCommonUtils.getMsgContainTime("已提交任务信息格式错误"));
                setDefaultTaskDesc(editDescriptionValue.toString());
            }
        } else {
            setDefaultTaskDesc(editDescriptionValue.toString());
        }
        if (StringUtils.isBlank(modifiedFileValue)) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTime("未查询到修改记录信息,请检查"));
        } else {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTime("修改记录信息同步完成"));
        }
        sync.setDisable(false);
        execute.setDisable(false);
    }

    private void setDefaultTaskDesc(String editDescriptionValue) {
        StringBuilder suggestionMsg = new StringBuilder();
        suggestionMsg.append("【功能入口】\n");
        suggestionMsg.append("\t\n");
        suggestionMsg.append("【测试准备】\n");
        suggestionMsg.append("\t更新ta-web-manager-fund-core等前台相关包及console包，执行升级脚本\n");
        suggestionMsg.append("\t清除浏览器缓存，刷新系统内存\n");
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
        String modifiedFile = TaCommonUtils.formatText(modifiedFileValue, true);
        String editDescription = TaCommonUtils.formatText(editDescriptionValue, true);
        String suggestion = TaCommonUtils.formatText(suggestionValue, true);
        String selfTestDesc = TaCommonUtils.formatTextOnlyBr(selfTestDescValue);
        hepTaskDto.setRealWorkload(realRorkloadValue.trim());
        hepTaskDto.setRealFinishTime(realFinishTimeValue + STR_SPACE +CommonUtils.getCurrentDateTime8(new Date()));
        hepTaskDto.setModifiedFile(modifiedFile);
        hepTaskDto.setEditDescription(editDescription);
        hepTaskDto.setSuggestion(suggestion);
        hepTaskDto.setSelfTestDesc(selfTestDesc);
        HepTodoController hep = JvmCache.getHepTodoController();
        hep.execute(OPERATE_COMPLETE, hepTaskDto);
        if (!OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
            JSONArray res = hep.execute(OPERATE_COMPLETE_QUERY, hepTaskDto);
            hep.dealTaskList(res, true);
            addTaskDesc(hepTaskDto, editDescription, suggestion, selfTestDesc);
        }
        appConfigDto.getChildStage().close();
        appConfigDto.setChildStage(null);
    }

    private void addTaskDesc(HepTaskDto hepTaskDto, String editDescription, String suggestion, String selfTestDesc) throws IOException {
        String fileName = hepTaskDto.getOriTaskName().replaceAll(STR_COLON, STR_BLANK) + FILE_TYPE_STAT;
        String path = FileUtils.getFilePath(PATH_DEFINE_HEP_STAT + fileName);
        if (new File(path).exists()) {
            return;
        }
        List<String> taskDesc = new ArrayList<>();
        taskDesc.add(hepTaskDto.getTaskNumber());
        taskDesc.add(MSG_TASK_DIVIDE_LINE);
        taskDesc.add(editDescription);
        taskDesc.add(MSG_TASK_DIVIDE_LINE);
        taskDesc.add(suggestion);
        taskDesc.add(MSG_TASK_DIVIDE_LINE);
        taskDesc.add(selfTestDesc);
        FileUtils.writeFile(path, taskDesc, false);
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
                OutputUtils.repeatInfo(realRorkload, TaCommonUtils.formatText(hepTaskDto.getRealWorkload()));
            }
            OutputUtils.repeatInfo(modifiedFile, hepTaskDto.getModifiedFile());
            OutputUtils.repeatInfo(editDescription, TaCommonUtils.formatText(hepTaskDto.getEditDescription()));
            OutputUtils.repeatInfo(suggestion, TaCommonUtils.formatText(hepTaskDto.getSuggestion()));
            OutputUtils.repeatInfo(selfTestDesc, TaCommonUtils.formatText(hepTaskDto.getSelfTestDesc()));
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    modifiedFile.requestFocus();
                }
            });
            if (StringUtils.isBlank(hepTaskDto.getModifiedFile())) {
                syncSvn(null);
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

}
