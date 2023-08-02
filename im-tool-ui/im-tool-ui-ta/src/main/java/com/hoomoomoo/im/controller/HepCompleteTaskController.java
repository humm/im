package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
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
import lombok.SneakyThrows;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.controller.HepWaitHandleTaskController.OPERATE_COMPLETE;

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
        } else {
            String svnUrl = appConfigDto.getSvnUrl().get(KEY_BRANCHES);
            if (versionValue.contains(KEY_FUND)) {
                svnUrl = TaCommonUtil.getSvnUrl(versionValue, svnUrl);
                versionValue += KEY_SOURCES_TA_FUND;
            }
            String svnRep = svnUrl + versionValue;
            LoggerUtils.info("svn仓库地址为: " + svnRep);
            appConfigDto.setSvnRep(svnRep);
        }
        try {
            logDtoList.addAll(SvnUtils.getSvnLog(0, taskNumber));
        } catch (Exception e) {
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
                if (StringUtils.isNotBlank(modifiedFileValue)) {
                    modifiedFileValue.append(SYMBOL_NEXT_LINE);
                }
                modifiedFileValue.append(formatText(fileList.get(j), false));
            }
            editDescriptionValue.append(formatText(item.getMsg(), false));
            if (i != logDtoList.size() - 1) {
                editDescriptionValue.append(SYMBOL_NEXT_LINE);
            }
        }
        OutputUtils.repeatInfo(modifiedFile, modifiedFileValue.toString());
        OutputUtils.repeatInfo(editDescription, editDescriptionValue.toString());
        OutputUtils.repeatInfo(suggestion, editDescriptionValue.toString());
        if (StringUtils.isBlank(modifiedFileValue)) {
            OutputUtils.info(notice, "未查询到修改记录信息,请检查");
        } else {
            OutputUtils.info(notice, "修改记录信息同步完成");
        }
        sync.setDisable(false);
        execute.setDisable(false);
    }

    private String getVersion(AppConfigDto appConfigDto, String ver) {
        String resVer;
        boolean isTrunk = true;
        Map<String, String> svnVersionMap = appConfigDto.getCopyCodeVersion();
        if (MapUtils.isNotEmpty(svnVersionMap)) {
            Iterator<String> version = svnVersionMap.keySet().iterator();
            while (version.hasNext()) {
                String verTmp = version.next();
                if (ver.compareTo(verTmp) < 0) {
                    isTrunk = false;
                    break;
                }
            }
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
        LoggerUtils.info("转换前版本号为: " + ver);
        LoggerUtils.info("转换后版本号为: " + resVer);
        return resVer;
    }

    @FXML
    void execute(ActionEvent event) throws Exception {
        StringBuilder tips = new StringBuilder();
        String realRorkloadValue = realRorkload.getText();
        String realFinishTimeValue = SYMBOL_EMPTY;
        if (realFinishTime.getValue() != null) {
            realFinishTimeValue = realFinishTime.getValue().toString();
        }
        String modifiedFileValue = modifiedFile.getText();
        String editDescriptionValue = editDescription.getText();
        String suggestionValue = suggestion.getText();
        if (StringUtils.isBlank(realRorkloadValue)) {
            tips.append("【耗费工时】").append(SYMBOL_NEXT_LINE);
        }
        if (StringUtils.isBlank(realFinishTimeValue)) {
            tips.append("【完成时间】").append(SYMBOL_NEXT_LINE);
        }
        if (StringUtils.isBlank(modifiedFileValue)) {
            tips.append("【修改文件】").append(SYMBOL_NEXT_LINE);
        }
        if (StringUtils.isBlank(editDescriptionValue)) {
            tips.append("【修改说明】").append(SYMBOL_NEXT_LINE);
        }
        if (StringUtils.isBlank(suggestionValue)) {
            tips.append("【测试建议】").append(SYMBOL_NEXT_LINE);
        }
        if (StringUtils.isNotBlank(tips)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("校验失败，如下信息不能为空");
            alert.setHeaderText(tips.toString());
            Optional<ButtonType> res = alert.showAndWait();
            return;
        }
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        HepTaskDto hepTaskDto = appConfigDto.getHepTaskDto();
        hepTaskDto.setRealWorkload(realRorkloadValue.trim());
        hepTaskDto.setRealFinishTime(realFinishTimeValue + SYMBOL_SPACE +CommonUtils.getCurrentDateTime8(new Date()));
        hepTaskDto.setModifiedFile(formatText(modifiedFileValue, true));
        hepTaskDto.setEditDescription(formatText(editDescriptionValue, true));
        hepTaskDto.setSuggestion(formatText(suggestionValue, true));
        HepWaitHandleTaskController hep = new HepWaitHandleTaskController();
        hep.setFreshFlag(hepTaskDto);
        hep.execute(OPERATE_COMPLETE, hepTaskDto);
        Stage taskStage = appConfigDto.getTaskStage();
        taskStage.close();
        appConfigDto.setTaskStage(null);
    }

    private String formatText(String text, boolean toBr){
        if (toBr) {
            return text.replaceAll("\\n", "\r</br>").trim();
        } else {
            return text.replaceAll("\r", SYMBOL_EMPTY).replaceAll("</br>", SYMBOL_EMPTY).trim();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            HepTaskDto hepTaskDto = appConfigDto.getHepTaskDto();
            OutputUtils.clearLog(modifiedFile);
            OutputUtils.clearLog(editDescription);
            OutputUtils.clearLog(suggestion);
            OutputUtils.repeatInfo(id, String.valueOf(hepTaskDto.getId()));
            OutputUtils.repeatInfo(taskNumber, hepTaskDto.getTaskNumber());
            OutputUtils.repeatInfo(realRorkload, appConfigDto.getHepTaskTodoCostTime());
            realFinishTime.setValue(LocalDate.now());
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
