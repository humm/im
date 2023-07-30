package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.HepTaskDto;
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
    private Label notice;

    @FXML
    void changeRealFinishTime(ActionEvent event) {

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
        hepTaskDto.setRealWorkload(realRorkloadValue);
        hepTaskDto.setRealFinishTime(realFinishTimeValue + SYMBOL_SPACE +CommonUtils.getCurrentDateTime8(new Date()));
        hepTaskDto.setModifiedFile(formatText(modifiedFileValue));
        hepTaskDto.setEditDescription(formatText(editDescriptionValue));
        hepTaskDto.setSuggestion(formatText(suggestionValue));
        HepWaitHandleTaskController hep = new HepWaitHandleTaskController();
        hep.setFreshFlag(hepTaskDto);
        hep.execute(OPERATE_COMPLETE, hepTaskDto);
        Stage taskStage = appConfigDto.getTaskStage();
        taskStage.close();
        appConfigDto.setTaskStage(null);
    }

    private String formatText(String text){
        return text.replaceAll("\\n", "\r</br>");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            HepTaskDto hepTaskDto = appConfigDto.getHepTaskDto();
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
