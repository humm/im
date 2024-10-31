package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.extend.ScriptSqlUtils;
import com.hoomoomoo.im.task.ChangeFunctionTask;
import com.hoomoomoo.im.task.ChangeFunctionTaskParam;
import com.hoomoomoo.im.utils.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    // 8:已发布 16:已发放 25:测试中 27:待发布
    private Set<String> demandStatus = new HashSet<>(Arrays.asList("8", "16", "25", "27"));

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
                    demandNo.append(item).append(STR_COMMA);
                }
            }
            OutputUtils.infoContainBr(logs, demandNo.substring(0, demandNo.length() - 1));
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
            List<String> res = getDemandStatus(demand1);
            res.addAll(getDemandStatus(demand2));
            if (CollectionUtils.isNotEmpty(res)) {
                String statPath = FileUtils.getFilePath(PATH_DEMAND_STAT);
                FileUtils.writeFile(statPath, res, false);
                OutputUtils.infoContainBr(logs, "同步需求成功");
            } else {
                OutputUtils.infoContainBr(logs, "未获取到需求信息");
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.infoContainBr(logs, e.getMessage());
        }
    }

    private List<String> getDemandStatus(String demand) {
        List<String> res = new ArrayList<>();
        if (StringUtils.isNotBlank(demand)) {
            Map<String, Object> resMap = JSON.parseObject(demand, Map.class);
            JSONObject data = (JSONObject)resMap.get(KEY_DATA);
            if (data.size() != 0) {
                JSONArray items = data.getJSONArray(KEY_ITEMS);
                if (items.size() != 0) {
                    for (int i=0; i<items.size(); i++) {
                        StringBuilder demandInfo = new StringBuilder();
                        JSONObject ele = items.getJSONObject(i);
                        String demandNo = String.valueOf(ele.get(KEY_NUMBER));
                        String status = String.valueOf(ele.get(KEY_STATUS));
                        if (demandStatus.contains(status)) {
                            status = STR_1;
                        } else {
                            status = STR_0;
                        }
                        JSONArray version = (JSONArray)ele.get(KEY_STORY_VERSION_LIST);
                        if (version.size() != 0) {
                            Set<String> createDate = new HashSet<>();
                            if (StringUtils.equals(status, STR_1) && version.size() > 2) {
                                for (int j=0; j<version.size(); j++) {
                                    JSONObject ver = version.getJSONObject(j);
                                    createDate.add(String.valueOf(ver.get(KEY_CREATE_TIME)).split(STR_SPACE)[0]);
                                }
                                if (createDate.size() == version.size()) {
                                    status = STR_0;
                                }
                            }
                        }
                        demandInfo.setLength(0);
                        res.add(demandInfo.append(demandNo).append(STR_SEMICOLON).append(status).toString());
                    }
                }
            }
        }
        return res;
    }
}
