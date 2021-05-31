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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.ABOUT_INFO;
import static com.hoomoomoo.im.consts.FunctionConfig.FUNCTION_STAT_INFO;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/09
 */
public class FunctionStatInfoController implements Initializable {

    @FXML
    private TextArea stat1;

    @FXML
    private TextArea stat2;

    @FXML
    private TextArea stat3;

    private static int statNum = 3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.info(String.format(STR_MSG_USE, FUNCTION_STAT_INFO.getName()));

        OutputUtils.clearLog(stat1);
        OutputUtils.clearLog(stat2);
        OutputUtils.clearLog(stat3);

        List<FunctionDto> functionDtoList = null;
        try {
            functionDtoList = CommonUtils.getAuthFunction();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
        if (CollectionUtils.isEmpty(functionDtoList)) {
            return;
        }
        int num = functionDtoList.size();
        List<TextArea> statList = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            int remainder = i % statNum;
            switch (remainder) {
                case 1:
                    statList.add(stat1);
                    break;
                case 2:
                    statList.add(stat2);
                    break;
                default:
                    statList.add(stat3);
                    break;
            }
        }
        for (int i = 0; i < functionDtoList.size(); i++) {
            FunctionDto functionDto = functionDtoList.get(i);
            if (FUNCTION_STAT_INFO.getCode().equals(functionDto.getFunctionCode()) || ABOUT_INFO.getCode().equals(functionDto.getFunctionCode())) {
                continue;
            }
            String logPath = "/logs/" + FunctionConfig.getLogFolder(functionDto.getFunctionCode()) + "/00000000.log";
            try {
                TextArea stat = statList.get(i);
                if (i >= statNum) {
                    OutputUtils.info(stat, STR_SYMBOL_NEXT_LINE_2);
                }
                OutputUtils.info(stat, functionDto.getFunctionName() + STR_SYMBOL_NEXT_LINE_2);
                File statFile = new File(FileUtils.getFilePath(logPath));
                List<String> logStat = new ArrayList<>();
                if (statFile.exists()) {
                    logStat = FileUtils.readNormalFile(FileUtils.getFilePath(logPath), false);
                }
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
        if (CollectionUtils.isEmpty(logStat)) {
            logStat.add("首次使用时间:");
            logStat.add("末次使用时间:");
            logStat.add("使用次数: 0");
        }
        for (String item : logStat) {
            OutputUtils.info(stat, STR_SPACE_4 + item + STR_SYMBOL_NEXT_LINE_2);
        }
    }
}
