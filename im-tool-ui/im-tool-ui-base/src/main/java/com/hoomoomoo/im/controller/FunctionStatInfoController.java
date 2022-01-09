package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

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

    @FXML
    private Label notice;

    @FXML
    private Label title;

    private static int statNum = 3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, FUNCTION_STAT_INFO.getName()));

        OutputUtils.clearLog(stat1);
        OutputUtils.clearLog(stat2);
        OutputUtils.clearLog(stat3);

        List<FunctionDto> functionDtoList = new ArrayList<>();
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
        OutputUtils.info(title, "授权功能  " + (num - 2));
        for (int i = 0; i < functionDtoList.size(); i++) {
            FunctionDto functionDto = functionDtoList.get(i);
            if (FUNCTION_STAT_INFO.getCode().equals(functionDto.getFunctionCode()) || ABOUT_INFO.getCode().equals(functionDto.getFunctionCode())) {
                functionDto.setUseTimes(0);
                continue;
            }
            String logPath = String.format(BaseConst.PATH_STAT, FunctionConfig.getLogFolder(functionDto.getFunctionCode())) + BaseConst.FILE_TYPE_STAT;
            try {
                TextArea stat = statList.get(i);
                if (i >= statNum) {
                    OutputUtils.info(stat, BaseConst.SYMBOL_NEXT_LINE_2);
                }
                OutputUtils.info(stat, functionDto.getFunctionName() + BaseConst.SYMBOL_NEXT_LINE_2);
                File statFile = new File(FileUtils.getFilePath(logPath));
                List<String> logStat = new ArrayList<>();
                if (statFile.exists()) {
                    logStat = FileUtils.readNormalFile(FileUtils.getFilePath(logPath), false);
                }
                outputStatInfo(stat, logStat);
                functionDto.setUseTimes(getUseTimes(logStat));
            } catch (Exception e) {
                if (e instanceof FileNotFoundException) {
                    continue;
                }
                LoggerUtils.info(e);
            }
        }
        OutputUtils.info(notice, getOrderInfo(functionDtoList));
    }

    private int getUseTimes(List<String> logStat) {
        if (CollectionUtils.isEmpty(logStat) || logStat.size() != 3) {
            return 0;
        }
        String timeLine = logStat.get(2);
        if (timeLine.contains(BaseConst.SYMBOL_COLON)) {
            return Integer.valueOf(timeLine.split(BaseConst.SYMBOL_COLON)[1].trim());
        }
        return 0;
    }

    private String getOrderInfo(List<FunctionDto> functionDtoList) {
        StringBuilder order = new StringBuilder("使用次数排序:  ");
        Collections.sort(functionDtoList, (o1, o2) -> o2.getUseTimes() - o1.getUseTimes());
        for (int i = 0; i < functionDtoList.size(); i++) {
            FunctionDto item = functionDtoList.get(i);
            if (i > 12) {
                break;
            }
            if (FUNCTION_STAT_INFO.getCode().equals(item.getFunctionCode()) || ABOUT_INFO.getCode().equals(item.getFunctionCode())) {
                continue;
            }
            order.append(item.getFunctionName()).append(BaseConst.SYMBOL_SPACE_2);
        }
        return order.toString();

    }

    private void outputStatInfo(TextArea stat, List<String> logStat) {
        if (CollectionUtils.isEmpty(logStat)) {
            logStat.add("首次使用时间:");
            logStat.add("末次使用时间:");
            logStat.add("使用次数: 0");
        }
        for (String item : logStat) {
            OutputUtils.info(stat, BaseConst.SYMBOL_SPACE_4 + item + BaseConst.SYMBOL_NEXT_LINE_2);
        }
    }

}
