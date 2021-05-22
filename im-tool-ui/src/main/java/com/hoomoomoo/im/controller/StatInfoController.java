package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.ABOUT_INFO;
import static com.hoomoomoo.im.consts.FunctionConfig.STAT_INFO;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/09
 */
public class StatInfoController implements Initializable {

    @FXML
    private TextArea stat1;

    @FXML
    private TextArea stat2;

    @FXML
    private TextArea stat3;

    @FXML
    private TextArea stat4;

    @FXML
    private TextArea stat5;

    @FXML
    private TextArea stat6;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.info(String.format(STR_MSG_USE, STAT_INFO.getName()));
        List<FunctionDto> functionDtoList = null;
        try {
            functionDtoList = CommonUtils.getAuthFunction();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
        if (CollectionUtils.isEmpty(functionDtoList)) {
            return;
        }
        for (int i = 0; i < functionDtoList.size(); i++) {
            FunctionDto functionDto = functionDtoList.get(i);
            if (STAT_INFO.getCode().equals(functionDto.getFunctionCode()) || ABOUT_INFO.getCode().equals(functionDto.getFunctionCode())) {
                continue;
            }
            String logPath = "/logs/" + FunctionConfig.getLogFolder(functionDto.getFunctionCode()) + "/00000000.log";
            TextArea stat = null;
            switch (i + 1) {
                case 1:
                    stat = stat1;
                    break;
                case 2:
                    stat = stat2;
                    break;
                case 3:
                    stat = stat3;
                    break;
                case 4:
                    stat = stat4;
                    break;
                case 5:
                    stat = stat5;
                    break;
                default:
                    break;
            }
            try {
                OutputUtils.clearLog(stat);
                OutputUtils.info(stat, functionDto.getFunctionName() + STR_SYMBOL_NEXT_LINE_2);
                File statFile = new File(FileUtils.getFilePath(logPath).getPath());
                if (!statFile.exists()) {
                    continue;
                }
                List<String> logStat = FileUtils.readNormalFile(FileUtils.getFilePath(logPath).getPath(), false);
                outputStatInfo(stat, logStat);
            } catch (Exception e) {
                if (e instanceof FileNotFoundException) {
                    continue;
                }
                LoggerUtils.info(e);
            }
        }
    }

    private void outputStatInfo(TextArea stat, List<String> logStat) {
        if (CollectionUtils.isNotEmpty(logStat)) {
            for (String item : logStat) {
                OutputUtils.info(stat, STR_SPACE_4 + item + STR_SYMBOL_NEXT_LINE_2);
            }
        }
    }
}
