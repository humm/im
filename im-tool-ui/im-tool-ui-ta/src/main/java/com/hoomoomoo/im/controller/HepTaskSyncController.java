package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.List;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.CHANGE_FUNCTION_TOOL;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class HepTaskSyncController implements Initializable {

    @FXML
    private TextArea logs;

    @FXML
    private TextArea demandStatus1;

    @FXML
    private TextArea demandStatus2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, CHANGE_FUNCTION_TOOL.getName()));
    }

    @FXML
    void executeDemandNo(ActionEvent event) {
        try {
            OutputUtils.clearLog(logs);
            JvmCache.getSystemToolController().executeSyncTaskInfoBySyncTask();
            Map<String, String> task = JvmCache.getHepTodoController().getTaskInfo().get(KEY_TASK);
            if (MapUtils.isEmpty(task)) {
                OutputUtils.infoContainBr(logs, "未获取到任务信息");
                return;
            }
            StringBuilder demandNo = new StringBuilder();
            for (String item : task.values()) {
                if (StringUtils.isNotBlank(item)) {
                    String[] element = item.split(STR_COMMA);
                    for (String ele : element) {
                        ele += STR_COMMA;
                        if (demandNo.indexOf(ele) == -1) {
                            demandNo.append(ele);
                        }
                    }
                }
            }
            String res = demandNo.substring(0, demandNo.length() - 1);
            OutputUtils.infoContainBr(logs, res);
            OutputUtils.infoContainBr(logs, "获取任务信息成功，任务记录" + task.size() + "条，需求记录" + res.split(STR_COMMA).length + "条");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.infoContainBr(logs, e.getMessage());
        } 
    }

    @FXML
    void executeDemandNoCopy(ActionEvent event) {
        try {
            String demandNo = InputUtils.getComponentValue(logs);
            if (StringUtils.isBlank(demandNo)) {
                OutputUtils.infoContainBr(logs, "请先获取需求编号");
                return;
            }
            if (demandNo.contains(STR_NEXT_LINE)) {
                demandNo = demandNo.split(STR_NEXT_LINE)[0];
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(demandNo), null);
            OutputUtils.infoContainBr(logs, "复制需求编号成功");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.infoContainBr(logs, e.getMessage());
        }
    }

    @FXML
    void executeTaskSync(ActionEvent event) {
        try {
            String demand1 = InputUtils.getComponentValue(demandStatus1);
            String demand2 = InputUtils.getComponentValue(demandStatus2);
            if (StringUtils.isBlank(demand1) && StringUtils.isBlank(demand2)) {
                OutputUtils.infoContainBr(logs, "请输入需求结果集");
                return;
            }
            List<String> res = new ArrayList<>(TaCommonUtils.getDemandStatus(demand1));
            res.addAll(TaCommonUtils.getDemandStatus(demand2));
            if (CollectionUtils.isNotEmpty(res)) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                String statPath = appConfigDto.getHepTaskSyncPath() + PATH_DEMAND_STAT;
                FileUtils.writeFile(statPath, res, false);
                OutputUtils.infoContainBr(logs, "同步需求成功，需求记录" + res.size() + "条");
            } else {
                OutputUtils.infoContainBr(logs, "未获取到需求信息");
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.infoContainBr(logs, e.getMessage());
        }
    }
}
