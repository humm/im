package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/09
 */
public class FunctionStatInfoController implements Initializable {

    @FXML
    private TableView stat;

    @FXML
    private Label notice;

    @FXML
    private Label title;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, FUNCTION_STAT_INFO.getName()));

        OutputUtils.clearLog(stat);

        List<FunctionDto> functionDtoList = new ArrayList<>();
        try {
            functionDtoList = CommonUtils.getAuthFunction();
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
        if (CollectionUtils.isEmpty(functionDtoList)) {
            return;
        }
        Iterator<FunctionDto> iterator = functionDtoList.listIterator();
        while (iterator.hasNext()) {
            FunctionDto functionDto = iterator.next();
            if (functionDto.getFunctionCode().equals(FUNCTION_STAT_INFO.getCode()) || functionDto.getFunctionCode().equals(ABOUT_INFO.getCode())) {
                iterator.remove();
                continue;
            }
            String hide = MenuFunctionConfig.FunctionConfig.getHide(functionDto.getFunctionCode());
            if (StringUtils.equals(STR_1, hide)) {
                iterator.remove();
                continue;
            }
            String logPath = String.format(BaseConst.PATH_STAT, MenuFunctionConfig.FunctionConfig.getLogFolder(functionDto.getFunctionCode())) + BaseConst.FILE_TYPE_STAT;
            try {
                File statFile = new File(FileUtils.getFilePath(logPath));
                List<String> logStat = new ArrayList<>();
                if (statFile.exists()) {
                    logStat = FileUtils.readNormalFile(FileUtils.getFilePath(logPath));
                }
                functionDto.setFirstTime(getStatInfo(logStat, 0));
                functionDto.setLastTime(getStatInfo(logStat, 1));
                functionDto.setSubmitTimes(getStatInfo(logStat, 2));
            } catch (Exception e) {
                if (e instanceof FileNotFoundException) {
                    continue;
                }
                LoggerUtils.error(e);
            }
        }
        OutputUtils.info(title, "功能数量  " + functionDtoList.size());
        CommonUtils.sortFunctionDtoList(functionDtoList);
        for (FunctionDto functionDto : functionDtoList) {
            OutputUtils.info(stat, functionDto);
        }
        OutputUtils.info(notice, getOrderInfo(functionDtoList));
        LoggerUtils.writeLogInfo(FUNCTION_STAT_INFO.getCode(), new Date(), new ArrayList<>());
    }

    private String getStatInfo(List<String> logStat, int type) {
        if (CollectionUtils.isEmpty(logStat) || logStat.size() != 3) {
            return STR_BLANK;
        }
        String content = logStat.get(type);
        if (content.contains(BaseConst.STR_COLON)) {
            return content.substring(content.indexOf(BaseConst.STR_COLON) + 1).trim();
        }
        return STR_BLANK;
    }

    private String getOrderInfo(List<FunctionDto> functionDtoList) {
        StringBuilder order = new StringBuilder("使用次数排序:  ");
        for (int i = 0; i < functionDtoList.size(); i++) {
            FunctionDto item = functionDtoList.get(i);
            if (i > 12) {
                break;
            }
            if (FUNCTION_STAT_INFO.getCode().equals(item.getFunctionCode()) || ABOUT_INFO.getCode().equals(item.getFunctionCode())) {
                continue;
            }
            order.append(item.getFunctionName()).append(BaseConst.STR_SPACE_2);
        }
        return order.toString();

    }

}
