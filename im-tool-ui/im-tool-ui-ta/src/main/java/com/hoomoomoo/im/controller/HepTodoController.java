package com.hoomoomoo.im.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.HepTaskComponentDto;
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.dto.VersionDto;
import com.hoomoomoo.im.extend.HepWaitHandleTaskMenu;
import com.hoomoomoo.im.extend.ScriptCompareSql;
import com.hoomoomoo.im.task.HepTodoTask;
import com.hoomoomoo.im.task.HepTodoTaskParam;
import com.hoomoomoo.im.utils.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.TASK_TODO;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2022/07/30
 */
public class HepTodoController extends BaseController implements Initializable {
    private static Set<String> field = new LinkedHashSet<>();

    private final static Integer STATUS_200 = 200;
    private final static String STR_STATUS_200 = "200";
    private final static String REQUEST_URL = "http://cloudin.proxy.in.hundsun.com/openapi/invoke/defaultFormData";
    private final static String APP_ID = "dqwhyanulhrmrrnk";
    private final static String APP_KEY = "fbbbee8e31a646d3a3a45f5c0e5b3e9";
    private static String CURRENT_USER_ID = "";
    private final static String METHOD_GET_FIELD_INFO = "devtool/getFieldInfo";
    private final static String METHOD_FETCH_TASK_BY_ID = "devtool/fetchTaskById";
    private final static String METHOD_UPDATE_TASK_STATUS = "devtool/updateTaskStatus";
    private final static String METHOD_FETCH_TASK_LIST = "devtool/fetchTaskList";
    /** 0:待启动,4:开发中,5,8:待集成,14,16,17:待审核,18:审核不通过,6 */
    private final static String STATUS_LIST = "0,4,5,8,14,16,17,18,6";
    private final static String OPERATE_TYPE_START = "1";
    private final static String OPERATE_TYPE_UPDATE = "2";
    private final static String OPERATE_TYPE_COMPLETE = "3";
    private final static String STATUS_WAIT_START = "0";
    private final static String STATUS_AUDIT_FAIL = "18";
    private final static String STATUS_DEV = "4";
    private final static String STATUS_WAIT_INTEGRATE = "8";
    private final static String STATUS_WAIT_CHECK = "17";
    private final static String OPERATE_QUERY = "1";
    public final static String OPERATE_COMPLETE_QUERY = "4";
    private final static String OPERATE_START = "2";
    public final static String OPERATE_COMPLETE = "3";

    public final static String OPERATE_TYPE_CUSTOM_UPDATE = "update";

    private List<String> logs = new ArrayList<>();

    static {
        field.add(KEY_CHARSET);
        field.add(KEY_REAL_FINISH_TIME);
        field.add(KEY_OPERATE_TYPE);
        field.add(KEY_METHOD);
        field.add(KEY_SUGGESTION);
        field.add(KEY_FORMAT);
        field.add(KEY_CURRENT_USER_ID);
        field.add(KEY_SIGN);
        field.add(KEY_REAL_WORKLOAD);
        field.add(KEY_APP_KEY);
        field.add(KEY_FINISH_PERCENTAGE);
        field.add(KEY_INTEGRATE_ATTENTION);
        field.add(KEY_MODIFIED_FILE);
        field.add(KEY_STATUS_LIST);
        field.add(KEY_ID);
        field.add(KEY_APP_ID);
        field.add(KEY_EDIT_DESCRIPTION);
        field.add(KEY_TIMESTAMP);
        field.add(KEY_SELF_TEST_DESC);
    }

    @FXML
    private Label weekPublish;

    @FXML
    private Label weekClose;

    @FXML
    private Label dayPublish;

    @FXML
    private Label dayClose;

    @FXML
    private TextField taskNumber;

    @FXML
    private TextField taskNumberQuery;

    @FXML
    private TextField name;

    @FXML
    private TextField nameQuery;

    @FXML
    private TextField sprintVersion;

    @FXML
    private TextField sprintVersionQuery;

    @FXML
    private TextField statusName;

    @FXML
    private TextField id;

    @FXML
    private TextArea notice;

    @FXML
    private TableView taskList;

    @FXML
    private Button query;

    @FXML
    private Button queryCondition;

    @FXML
    private Button reset;

    @FXML
    private AnchorPane condition;

    @FXML
    private Button updateVersion;

    @FXML
    private Button showVersion;

    @FXML
    private Button scriptCheck;

    @FXML
    private Button scriptShow;

    @FXML
    private Label dayTodo;

    @FXML
    private Label weekTodo;

    @FXML
    private Label waitHandleTaskNum;

    private static Map<String, Integer> minDateCache = new HashMap<>();

    @FXML
    void showTaskInfo(MouseEvent event) throws Exception {
        HepTaskDto item = (HepTaskDto)taskList.getSelectionModel().getSelectedItem();
        item.setOperateType(STR_BLANK);
        OutputUtils.repeatInfo(taskNumber, item.getTaskNumber());
        OutputUtils.repeatInfo(name, item.getName());
        OutputUtils.repeatInfo(sprintVersion, item.getSprintVersion());
        OutputUtils.repeatInfo(statusName, item.getStatusName());
        OutputUtils.repeatInfo(id, item.getId());
        String clickType = event.getButton().toString();
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        appConfigDto.setHepTaskDto(item);
        if (LEFT_CLICKED.equals(clickType) && event.getClickCount() == SECOND_CLICKED) {
            operateTask(item);
        }
    }

    void operateTask(HepTaskDto item) {
        String status = item.getStatus();
        if (STATUS_WAIT_START.equals(status)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("启动任务");
            String tips = "任务【" + item.getTaskNumber() + "】 " + STR_NEXT_LINE;
            tips += "版本【" + item.getSprintVersion() + "】 " + STR_NEXT_LINE;
            String name = item.getName();
            if (name.length() > 80) {
                name = name.substring(0, 80) + "...";
            }
            tips += "名称 " + name;
            alert.setHeaderText(tips);
            Optional<ButtonType> res = alert.showAndWait();
            if (ButtonType.OK.equals(res.get())) {
                // 待启动
                try {
                    execute(OPERATE_START, item);
                } catch (Exception e) {
                    LoggerUtils.info(e);
                    OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(e.getMessage()));
                }
            }
        } else if (STATUS_DEV.equals(status) || STATUS_AUDIT_FAIL.equals(status)) {
            try {
                completeTask(item);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(e.getMessage()));
            }
        } else {
            LoggerUtils.info("不支持的操作类型");
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr("不支持的操作类型"));
        }
    }

    @FXML
    void executeUpdateVersion(ActionEvent event) throws Exception {
        updateVersion.setDisable(true);
        try {
            new SystemToolController().executeUpdateVersion();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr("同步成功"));
            executeQuery(null);
        } catch (Exception e) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(e.getMessage()));
        } finally {
            updateVersion.setDisable(false);
        }
    }

    @FXML
    void showVersion(ActionEvent event) throws Exception {
        showVersion.setDisable(true);
        try {
            doShowVersion();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(e.getMessage()));
        } finally {
            showVersion.setDisable(false);
        }
        OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(STR_BLANK));
    }

    void doShowVersion() throws Exception {
        new SystemToolController().executeUpdateVersion();
        List<VersionDto> versionDtoList = getVersionInfo();
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        appConfigDto.setVersionDtoList(versionDtoList);
        Stage stage = appConfigDto.getChildStage();
        // 每次页面都重新打开
        if (stage != null) {
            stage.close();
            appConfigDto.setChildStage(null);
        }
        Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_BLANK_TABLE_VIEW)));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
        stage = new Stage();
        stage.getIcons().add(new Image(PATH_ICON));
        stage.setScene(scene);
        stage.setWidth(850);
        stage.setTitle("发版日期");
        stage.setResizable(false);
        stage.show();
        appConfigDto.setChildStage(stage);
        stage.setOnCloseRequest(columnEvent -> {
            appConfigDto.getChildStage().close();
            appConfigDto.setChildStage(null);
        });
    }

    @FXML
    void scriptCheckBtn(ActionEvent event) throws Exception {
        TaskUtils.execute(new HepTodoTask(new HepTodoTaskParam(this, "check")));
    }

    public void doScriptCheck() {
        scriptCheck.setDisable(true);
        try {
            new ScriptCompareSql().check();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr("脚本检查完成，请查看检查结果"));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(e.getMessage()));
        } finally {
            scriptCheck.setDisable(false);
        }
    }

    @FXML
    void scriptShowBtn(ActionEvent event) throws Exception {
        scriptShow.setDisable(true);
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_CHECK_RESULT, "检查结果");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(e.getMessage()));
        } finally {
            scriptShow.setDisable(false);
        }
    }

    @FXML
    void executeQuery(ActionEvent event) throws Exception {
        HepTodoTaskParam hepTodoTaskParam = new HepTodoTaskParam(this, "query");
        hepTodoTaskParam.setEvent(event);
        TaskUtils.execute(new HepTodoTask(hepTodoTaskParam));
    }

    public void query(ActionEvent event) throws Exception {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, TASK_TODO.getName()));
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        CURRENT_USER_ID = appConfigDto.getHepTaskUser();
        if (StringUtils.isBlank(CURRENT_USER_ID)) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr("请配置【 hep.task.user 】"));
            return;
        }
        try {
            setProgress(0);
            updateProgress();
            if (event != null) {
                minDateCache.clear();
            }
            TaskUtils.execute(new HepTodoTask(new HepTodoTaskParam(this, "doQuery")));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(e.getMessage()));
        }
    }

    @FXML
    void executeReset(ActionEvent event) throws Exception {
        OutputUtils.clearLog(taskNumberQuery);
        OutputUtils.clearLog(nameQuery);
        OutputUtils.clearLog(sprintVersionQuery);
        executeQuery(null);
    }

    public void doExecuteQuery() {
        try {
            query.setDisable(true);
            queryCondition.setDisable(true);
            reset.setDisable(true);
            execute(OPERATE_QUERY, null);
            setProgress(1);
            LoggerUtils.writeLogInfo(TASK_TODO.getCode(), new Date(), logs);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(ExceptionMsgUtils.getMsg(e)));
        } finally {
            query.setDisable(false);
            queryCondition.setDisable(false);
            reset.setDisable(false);
        }
    }

    public JSONArray execute(String operateType, HepTaskDto hepTaskDto) throws Exception {
        HttpResponse response;
        if (OPERATE_QUERY.equals(operateType) || OPERATE_COMPLETE_QUERY.equals(operateType)) {
            response = sendPost(getTaskList());
        } else if (OPERATE_START.equals(operateType)) {
            response = sendPost(startTask(hepTaskDto));
        } else if (OPERATE_COMPLETE.equals(operateType)) {
            response = sendPost(executeCompletTask(hepTaskDto));
        } else {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr("不支持的操作类型"));
            throw new Exception("不支持的操作类型");
        }
        JSONArray items = new JSONArray();
        if (response != null) {
            Map<String, Object> responseInfo;
            String message = response.body();
            if (requestStatus(response)) {
                responseInfo = (Map)JSONObject.parse(response.body());
                message = String.valueOf(responseInfo.get(KEY_MESSAGE));
                String code = String.valueOf(responseInfo.get(KEY_CODE));
                if (STR_STATUS_200.equals(code)) {
                    Object data = responseInfo.get(KEY_DATA);
                    if (data instanceof Map) {
                        items.add(data);
                    } else if (data instanceof List) {
                        items = (JSONArray)data;
                    }
                    if (OPERATE_QUERY.equals(operateType)) {
                        dealTaskList(items,  logs, dayTodo, weekTodo, waitHandleTaskNum, dayPublish, weekPublish, dayClose, weekClose, taskList, true);
                    } else if (OPERATE_START.equals(operateType)) {
                        executeQuery(null);
                    }
                }
            }
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(message));
        }
        return items;
    }

    public void completeTask(HepTaskDto hepTaskDto) {
        // 获取列表数据
        Map<String, Object> request = new HashMap<>(16);
        request.put(KEY_METHOD, METHOD_GET_FIELD_INFO);
        request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_COMPLETE);
        if (OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
            request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_UPDATE);
        }
        HttpResponse response = sendPost(request);
        if (requestStatus(response)) {
            request.remove(KEY_OPERATE_TYPE);
            request.put(KEY_METHOD, METHOD_FETCH_TASK_BY_ID);
            request.put(KEY_ID, hepTaskDto.getId());
        }
        response = sendPost(request);
        if (requestStatus(response)) {
            try {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                Stage stage = appConfigDto.getChildStage();
                setTaskComponent(appConfigDto);
                if (OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
                    Map responseInfo = (Map)JSONObject.parse(response.body());
                    String code = String.valueOf(responseInfo.get(KEY_CODE));
                    if (STR_STATUS_200.equals(code)) {
                        Map data = (Map)responseInfo.get(KEY_DATA);
                        hepTaskDto.setRealWorkload(STR_1);
                        hepTaskDto.setModifiedFile(TaCommonUtils.formatTextBrToNextLine((String)data.get(KEY_MODIFIED_FILE)));
                        hepTaskDto.setEditDescription(TaCommonUtils.formatTextBrToNextLine((String)data.get(KEY_EDIT_DESCRIPTION)));
                        hepTaskDto.setSuggestion(TaCommonUtils.formatTextBrToNextLine((String)data.get(KEY_SUGGESTION)));
                    }
                }
                // 每次页面都重新打开
                if (stage != null) {
                    stage.close();
                    appConfigDto.setChildStage(null);
                }
                Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_COMPLETE_TASK_FXML)));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
                stage = new Stage();
                stage.getIcons().add(new Image(PATH_ICON));
                stage.setScene(scene);
                stage.setTitle("完成任务");
                if (OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
                    stage.setTitle("更新任务");
                }
                stage.setResizable(false);
                stage.show();
                appConfigDto.setChildStage(stage);
                stage.setOnCloseRequest(columnEvent -> {
                    appConfigDto.getChildStage().close();
                    appConfigDto.setChildStage(null);
                });
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr(e.getMessage()));
            }
        }
    }

    private void setTaskComponent(AppConfigDto appConfigDto) {
        HepTaskComponentDto hepTaskComponentDto = new HepTaskComponentDto();
        hepTaskComponentDto.setLogs(logs);
        hepTaskComponentDto.setDayTodo(dayTodo);
        hepTaskComponentDto.setWeekTodo(weekTodo);
        hepTaskComponentDto.setWaitHandleTaskNum(waitHandleTaskNum);
        hepTaskComponentDto.setTaskList(taskList);
        hepTaskComponentDto.setDayPublish(dayPublish);
        hepTaskComponentDto.setWeekPublish(weekPublish);
        hepTaskComponentDto.setDayClose(dayClose);
        hepTaskComponentDto.setWeekClose(weekClose);
        appConfigDto.setHepTaskComponentDto(hepTaskComponentDto);
    }

    private Map<String, Object> executeCompletTask(HepTaskDto hepTaskDto) throws Exception {
        Map<String, Object> request = new HashMap<>(16);
        request.put(KEY_METHOD, METHOD_UPDATE_TASK_STATUS);
        request.put(KEY_CURRENT_USER_ID, CURRENT_USER_ID);
        request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_COMPLETE);
        if (OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
            request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_UPDATE);
            // 完成百分比
            request.put(KEY_FINISH_PERCENTAGE, STR_1);
        } else {
            // 实际完成时间
            request.put(KEY_REAL_FINISH_TIME, hepTaskDto.getRealFinishTime());
            // 集成注意
            request.put(KEY_INTEGRATE_ATTENTION, STR_BLANK);
            // 完成百分比
            request.put(KEY_FINISH_PERCENTAGE, STR_1);
        }
        request.put(KEY_ID, hepTaskDto.getId());
        // 今日总工时
        request.put(KEY_REAL_WORKLOAD, hepTaskDto.getRealWorkload());
        // 修改文件
        request.put(KEY_MODIFIED_FILE, hepTaskDto.getModifiedFile());
        // 修改说明
        request.put(KEY_EDIT_DESCRIPTION, hepTaskDto.getEditDescription());
        // 测试建议
        request.put(KEY_SUGGESTION, hepTaskDto.getSuggestion());
        // 自测说明
        request.put(KEY_SELF_TEST_DESC, hepTaskDto.getSelfTestDesc());
        return request;
    }

    private Map<String, Object> startTask(HepTaskDto hepTaskDto) throws Exception {
        // 获取列表数据
        String taskId = hepTaskDto.getId();
        Map<String, Object> request = new HashMap<>(8);
        request.put(KEY_METHOD, METHOD_GET_FIELD_INFO);
        request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_START);
        HttpResponse response = sendPost(request);
        if (requestStatus(response)) {
            request.remove(KEY_OPERATE_TYPE);
            request.put(KEY_METHOD, METHOD_FETCH_TASK_BY_ID);
            request.put(KEY_ID, taskId);
        }
        response = sendPost(request);
        if (requestStatus(response)) {
            request.put(KEY_METHOD, METHOD_UPDATE_TASK_STATUS);
            request.put(KEY_CURRENT_USER_ID, CURRENT_USER_ID);
            request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_START);
            request.put(KEY_ID, taskId);
        }
        return request;
    }

    private Map<String, Object> getTaskList() {
        // 获取列表数据
        Map<String, Object> request = new HashMap<>(3);
        request.put(KEY_CURRENT_USER_ID, CURRENT_USER_ID);
        request.put(KEY_METHOD, METHOD_FETCH_TASK_LIST);
        request.put(KEY_STATUS_LIST, STATUS_LIST);
        return request;
    }

    @SneakyThrows
    public void dealTaskList(JSONArray task, List<String> logsIn, Label dayTodoIn, Label weekTodoIn, Label waitHandleTaskNumIn, Label dayPublishIn, Label weekPublishIn,
                             Label dayCloseIn, Label weekCloseIn, TableView taskListIn, boolean tagFlag) {
        taskListIn.setDisable(true);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<String> dayPublishVersion = appConfigDto.getDayPublishVersion();
        List<HepTaskDto> res = JSONArray.parseArray(JSONObject.toJSONString(task), HepTaskDto.class);
        filterTask(res);
        if (tagFlag) {
            initTag(res);
        }
        Map<String, String> taskMinCompleteDate = new HashMap<>();
        for (int i=0; i<task.size(); i++) {
            Map<String, Object> item = (Map)task.get(i);
            logsIn.add(item.toString());
            String name = String.valueOf(item.get(KEY_NAME));
            String estimateFinishTime = String.valueOf(item.get(KEY_ESTIMATE_FINISH_TIME)).split(STR_SPACE)[0];
            if (taskMinCompleteDate.containsKey(name)) {
                if (estimateFinishTime.compareTo(taskMinCompleteDate.get(name)) < 0) {
                    taskMinCompleteDate.put(name, estimateFinishTime);
                }
            } else {
                taskMinCompleteDate.put(name, estimateFinishTime);
            }
        }
        Iterator<HepTaskDto> iterator = res.listIterator();
        boolean hasBlank = false;
        int taskTotal = 0;
        Map<String, Map<String, String>> version = new HashMap<>();
        StringBuilder weekVersion = new StringBuilder();
        StringBuilder weekCloseVersion = new StringBuilder();
        StringBuilder dayVersion = new StringBuilder();
        StringBuilder dayCloseVersion = new StringBuilder();
        String currentDay = CommonUtils.getCurrentDateTime3();
        String weekDay = getLastDayByWeek();
        int weekNum = 0;
        int weekCloseNum = 0;
        try {
            List<String> versionList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION_STAT), false);
            Map<String, String[]> versionExtend = getVersionExtendInfo();
            for (String item : versionList) {
                String[] elements = item.split(STR_SEMICOLON);
                Map<String, String> ele = new HashMap<>();
                String oriCloseDate = getRealDate(elements[1]);
                String oriPublishDate = getRealDate(elements[2]);
                String oriOrderNo = STR_0;
                String versionCode = elements[0];
                if (versionExtend.containsKey(versionCode)) {
                    oriCloseDate = versionExtend.get(versionCode)[0];
                    oriPublishDate = versionExtend.get(versionCode)[1];
                    oriOrderNo = versionExtend.get(versionCode)[2];
                }
                String closeDate = CommonUtils.getIntervalDays(currentDay, oriCloseDate);
                String publishDate = CommonUtils.getIntervalDays(currentDay, oriPublishDate);
                String customer = elements[3];
                String[] customerList;
                if (customer.contains("无人使用")) {
                    customerList = new String[]{STR_BLANK};
                } else if (customer.contains(STR_COMMA_1)) {
                    customerList = customer.split(STR_COMMA_1);
                } else if (customer.contains(STR_CAESURA)) {
                    customerList = customer.split(STR_CAESURA);
                } else {
                    customerList = new String[]{customer};
                }
                customer = STR_BLANK;
                for (String element : customerList) {
                    if (element.length() > 4) {
                        customer += element.substring(0, 4) + STR_SPACE;
                    }
                }
                ele.put(KEY_ORI_CLOSE_DATE, oriCloseDate);
                ele.put(KEY_CLOSE_DATE, closeDate);
                ele.put(KEY_PUBLISH_DATE, publishDate);
                ele.put(KEY_CUSTOMER, customer);
                ele.put(KEY_ORDER_NO, oriOrderNo);
                if (currentDay.equals(oriCloseDate)) {
                    dayCloseVersion.append(versionCode).append(STR_SPACE);
                }
                if (currentDay.equals(oriPublishDate)) {
                    dayVersion.append(versionCode).append(STR_SPACE);
                }

                if (currentDay.equals(oriCloseDate) ||currentDay.equals(oriPublishDate)) {
                    dayPublishVersion.add(versionCode);
                }

                if (weekDay.compareTo(oriCloseDate) >= 0 && currentDay.compareTo(oriCloseDate) <= 0) {
                    weekCloseNum++;
                    weekCloseVersion.append(versionCode).append(STR_SPACE);
                }

                if (weekDay.compareTo(oriPublishDate) >= 0 && currentDay.compareTo(oriPublishDate) <= 0) {
                    weekNum++;
                    weekVersion.append(versionCode).append(STR_SPACE);
                }
                version.put(versionCode, ele);
            }
        } catch (IOException e) {
            LoggerUtils.info(e);
        } catch (ParseException e) {
            LoggerUtils.info(e);
        }
        int dayVersionNum = 0;
        int weekVersionNum = 0;
        Map<String, Integer> minDate = new HashMap<>();
        while (iterator.hasNext()) {
            HepTaskDto item = iterator.next();
            String status = item.getStatus();
            if (STATUS_WAIT_INTEGRATE.equals(status) || STATUS_WAIT_CHECK.equals(status)) {
                iterator.remove();
                continue;
            }
            if (hasBlank && StringUtils.isBlank(status)) {
                iterator.remove();
                continue;
            }
            String sprintVersion = item.getSprintVersion();
            if (version.containsKey(sprintVersion)) {
                Map<String, String> versionInfo = version.get(sprintVersion);
                item.setCloseDate(versionInfo.get(KEY_CLOSE_DATE));
                item.setOriCloseDate(CommonUtils.getCurrentDateTime5(versionInfo.get(KEY_ORI_CLOSE_DATE)));
                item.setPublishDate(versionInfo.get(KEY_PUBLISH_DATE));
                item.setCustomer(versionInfo.get(KEY_CUSTOMER));
                item.setOrderNo(versionInfo.get(KEY_ORDER_NO));
            }
            String endDate = item.getEstimateFinishTime().split(STR_SPACE)[0].replaceAll(STR_HYPHEN, STR_BLANK);
            item.setEndDate(CommonUtils.getIntervalDays(currentDay, endDate));
            if (dayVersion.toString().contains(sprintVersion + STR_SPACE) || dayCloseVersion.toString().contains(sprintVersion + STR_SPACE)) {
                dayVersionNum++;
            }
            if (weekVersion.toString().contains(sprintVersion + STR_SPACE) || weekCloseVersion.toString().contains(sprintVersion + STR_SPACE)) {
                weekVersionNum++;
            }

            if (StringUtils.isBlank(status)) {
                hasBlank = true;
                taskTotal++;
            } else {
                hasBlank = false;
            }
            if (StringUtils.isBlank(status)) {
                item.setEstimateFinishDate(STR_BLANK);
                item.setEstimateFinishTime(STR_BLANK);
            } else {
                item.setEstimateFinishDate(item.getEstimateFinishTime().split(STR_SPACE)[0]);
                item.setEstimateFinishTime(item.getEstimateFinishTime().split(STR_SPACE)[1]);
            }
            initMinDate(minDate, item);
        }
        for (HepTaskDto item : res) {
            String taskName = item.getName();
            if (minDate.containsKey(taskName)) {
                int min = minDate.get(taskName);
                int minCache = minDateCache.get(taskName);
                if (minCache < min) {
                    min = minCache;
                }
                String estimateFinishDate = item.getEstimateFinishDate();
                if (estimateFinishDate.startsWith(STR_99)) {
                    min = Integer.valueOf(estimateFinishDate.replaceAll(STR_HYPHEN, STR_BLANK).substring(2, 4)) * -1;
                }
                item.setEndDate(String.valueOf(min));
            }
            if (taskMinCompleteDate.containsKey(taskName)) {
                if (!StringUtils.equals(taskMinCompleteDate.get(taskName), item.getEstimateFinishDate())) {
                    item.setEstimateFinishDate(STR_BLANK);
                }
            }
        }
        res = sortTask(res);

        /*if (StringUtils.isBlank(dayVersion)) {
            dayVersion.append(". . . 今日喝茶 . . .");
        }
        if (StringUtils.isBlank(weekVersion)) {
            weekVersion.append(". . . 本周喝茶 . . .");
        }*/

        OutputUtils.clearLog(dayTodoIn);
        OutputUtils.info(dayTodoIn, String.valueOf(dayVersionNum));

        OutputUtils.clearLog(weekTodoIn);
        OutputUtils.info(weekTodoIn, String.valueOf(weekVersionNum));

        OutputUtils.clearLog(dayPublishIn);
        OutputUtils.info(dayPublishIn, dayVersion.toString());

        OutputUtils.clearLog(weekPublishIn);
        OutputUtils.info(weekPublishIn, weekVersion.toString());

        OutputUtils.clearLog(dayCloseIn);
        OutputUtils.info(dayCloseIn, dayCloseVersion.toString());

        OutputUtils.clearLog(weekCloseIn);
        OutputUtils.info(weekCloseIn, weekCloseVersion.toString());

        OutputUtils.clearLog(waitHandleTaskNumIn);
        OutputUtils.info(waitHandleTaskNumIn, String.valueOf(res.size() - taskTotal));

        OutputUtils.clearLog(taskListIn);
        infoTaskList(taskListIn, res);
        taskListIn.setDisable(false);
    }

    private static void initMinDate (Map<String, Integer> minDate, HepTaskDto item) {
        String closeDate = item.getCloseDate();
        if (StringUtils.isBlank(closeDate)) {
            closeDate = STR_99999999;
        }
        String publishDate = item.getPublishDate();
        if (StringUtils.isBlank(publishDate)) {
            publishDate = STR_99999999;
        }
        String endDate = item.getEndDate();
        if (StringUtils.isBlank(endDate)) {
            endDate = STR_99999999;
        }
        String taskName = item.getName();
        int min = Math.min(Math.min(Integer.valueOf(closeDate), Integer.valueOf(publishDate)), Integer.valueOf(endDate));
        if (minDate.containsKey(taskName)) {
            if (minDate.get(taskName) > min) {
                minDate.put(taskName, min);
            }
        } else {
            minDate.put(taskName, min);
        }
        if (minDateCache.containsKey(taskName)) {
            if (minDateCache.get(taskName) > min) {
                minDateCache.put(taskName, min);
            }
        } else {
            minDateCache.put(taskName, min);
        }
    }

    private static String getRealDate(String oriDate) {
        StringBuilder date = new StringBuilder();
        if (StringUtils.isNotBlank(oriDate)) {
            for (int i=0; i<oriDate.length(); i++) {
                char item = oriDate.charAt(i);
                if (Character.isDigit(item)) {
                    date.append(item);
                }
            }
        }
        if (date.length() > 8) {
            return date.substring(8);
        }
        return date.toString();
    }

    private String getLastDayByWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        Date date = calendar.getTime();
        return CommonUtils.getCurrentDateTime9(date);
    }

    private void infoTaskList(TableView taskListIn, List<HepTaskDto> res) {
        if (taskListIn == null) {
            return;
        }
        Platform.runLater(() -> {
            for (HepTaskDto hepTaskDto : res) {
                taskListIn.getItems().add(hepTaskDto);
                // 设置行
                initRowColor(taskListIn);

                // 设置单元格
                //initCellColor(taskListIn);
            }
            OutputUtils.setEnabled(taskListIn);
        });
    }

    private void initRowColor(TableView taskListIn) {
        taskListIn.setRowFactory(new Callback<TableView<HepTaskDto>, TableRow<HepTaskDto>>(){
            @Override
            public TableRow<HepTaskDto> call(TableView<HepTaskDto> param) {
                final TableRow<HepTaskDto> row = new TableRow<HepTaskDto>() {
                    @Override
                    protected void updateItem(HepTaskDto item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item != null && getIndex() > -1){
                            String taskName = item.getName();
                            String taskNameTag = getTaskNameTag(taskName);
                            if (taskNameTag.contains("缺陷")) {
                                setStyle("-fx-text-background-color: blue;");
                            } else if (taskNameTag.contains("问题") || taskNameTag.contains("任务")) {
                                setStyle("-fx-text-background-color: #804000;");
                            } else if (taskName.contains("已修改") || taskName.contains("已提交")) {
                                setStyle("-fx-text-background-color: #550080;");
                            } else {
                                setStyle("-fx-text-background-color: black;");
                            }
                        }
                    }
                };
                return row;
            }
        });
    }

    private void initTag(List<HepTaskDto> task) {
        if (CollectionUtils.isEmpty(task)) {
            return;
        }
        Map<String, String> tags = new LinkedHashMap<>();
        for (HepTaskDto item : task) {
            String taskName = item.getName();
            if (taskName.contains(STR_BRACKETS_3_RIGHT)) {
                String taskNameTmp = taskName.substring( taskName.indexOf(STR_BRACKETS_3_LEFT) + 1, taskName.indexOf(STR_BRACKETS_3_RIGHT));
                if (taskNameTmp.contains("缺陷") || taskName.startsWith(STR_BRACKETS_3_LEFT)) {
                    if (taskNameTmp.contains(STR_COLON)) {
                        taskNameTmp = taskNameTmp.split(STR_COLON)[0];
                    }
                    if (!tags.containsKey(taskNameTmp)) {
                        tags.put(taskNameTmp, taskNameTmp);
                    }
                }
            }
            if (taskName.contains(STR_BRACKETS_2_RIGHT)) {
                taskName = taskName.substring(1, taskName.indexOf(STR_BRACKETS_2_RIGHT));
                if (!tags.containsKey(taskName)) {
                    tags.put(taskName, taskName);
                }
            }
        }
        Platform.runLater(() -> {
            ObservableList<Node> nodeObservableList = condition.getChildren();
            Iterator<Node> nodeIterator = nodeObservableList.iterator();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.next();
                String nodeId = node.getId();
                if (StringUtils.isNotBlank(nodeId) && nodeId.contains("tag")) {
                    nodeIterator.remove();
                }
            }
        });
        Iterator<String> iterator = tags.keySet().iterator();
        double layoutX = 60;
        double layoutY = 175;
        while (iterator.hasNext()) {
            String tagName = iterator.next();
            layoutX = buildTag(tagName, layoutX, layoutY);
            if (layoutX == 60) {
                layoutY = 200;
            }
        }
    }

    private double buildTag(String tagName, final double layoutX, final double layoutY) {
        Platform.runLater(() -> {
            Label label = new Label();
            label.setId("tag" + layoutX);
            label.setLayoutX(layoutX);
            label.setLayoutY(layoutY);
            label.setText(tagName);
            label.setTextFill(Color.GRAY);
            condition.getChildren().add(label);
            label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    String tag = ((Label)event.getSource()).getText();
                    nameQuery.setText(tag);
                    JSONArray res = null;
                    try {
                        res = execute(OPERATE_COMPLETE_QUERY, null);
                    } catch (Exception e) {

                    }
                    dealTaskList(res, logs, dayTodo, weekTodo, waitHandleTaskNum, dayPublish, weekPublish, dayClose, weekClose, taskList, false);
                }
            });
        });
        double x = layoutX + 20 * tagName.length();
        return x > 240 ? 60 : x;
    }

    public void filterTask(List<HepTaskDto> task) {
        Iterator<HepTaskDto> iterator = task.iterator();
        String taskNumberQ = CommonUtils.getComponentValue(taskNumberQuery);
        String nameQ = CommonUtils.getComponentValue(nameQuery);
        String sprintVersionQ = CommonUtils.getComponentValue(sprintVersionQuery);
        Map<String, String> taskType = new HashMap<>();
        while (iterator.hasNext()) {
            HepTaskDto item = iterator.next();
            String taskNumber = item.getTaskNumber();
            String taskName = item.getName();
            String sprintVersion = item.getSprintVersion();
            String taskNameTag = getTaskNameTag(taskName);
            if (StringUtils.isNotBlank(taskNumberQ) && !taskNumber.contains(taskNumberQ)) {
                iterator.remove();
                continue;
            }
            if (StringUtils.isNotBlank(nameQ) && !taskName.contains(nameQ)) {
                iterator.remove();
                continue;
            }
            if (StringUtils.isNotBlank(sprintVersionQ) && !sprintVersion.contains(sprintVersionQ)) {
                iterator.remove();
                continue;
            }
            if (taskNameTag.contains("缺陷")) {
                item.setEstimateFinishTime(getValue(STR_BLANK, STR_2));
                taskType.put(STR_2, STR_2);
            } else if (taskNameTag.contains("问题") || taskNameTag.contains("任务") ) {
                item.setEstimateFinishTime(getValue(STR_BLANK, STR_1));
                taskType.put(STR_1, STR_1);
            } else if (taskName.contains("已提交") || taskName.contains("已修改")) {
                item.setEstimateFinishTime(getValue(STR_BLANK, STR_9));
                taskType.put(STR_9, STR_9);
            }
        }
    }

    private static String getTaskNameTag(String taskName) {
        if (taskName.contains(STR_BRACKETS_3_LEFT) && taskName.contains(STR_BRACKETS_3_RIGHT)) {
            return taskName.substring(taskName.indexOf(STR_BRACKETS_3_LEFT) + 1, taskName.indexOf(STR_BRACKETS_3_RIGHT));
        } else if (taskName.startsWith(STR_BRACKETS_2_LEFT) && taskName.contains(STR_BRACKETS_2_RIGHT)) {
            return taskName.substring(1, taskName.indexOf(STR_BRACKETS_2_RIGHT));
        }
        return taskName;
    }

    private HttpResponse sendPost(Map<String, Object> param) {
        param.put(KEY_APP_ID, APP_ID);
        param.put(KEY_APP_KEY, APP_KEY);
        param.put(KEY_CHARSET, "utf-8");
        param.put(KEY_FORMAT, "MD5");
        param.put(KEY_TIMESTAMP, System.currentTimeMillis());
        cn.hutool.json.JSONObject jsonObject = new cn.hutool.json.JSONObject();
        initRequest(param, jsonObject);
        DigestAlgorithm digestAlgorithm = DigestAlgorithm.MD5;
        String sign = SecureUtil.signParams(digestAlgorithm, jsonObject, "&", "=", true, new String[0]).toUpperCase();
        jsonObject.set(KEY_SIGN, sign);
        if (testScene()) {
            return null;
        }
        LoggerUtils.info("请求入参: " + jsonObject);
        HttpResponse response = HttpRequest.post(REQUEST_URL).timeout(10 * 1000).form(jsonObject).execute();
        Map result = (Map)JSONObject.parse(response.body());
        LoggerUtils.info("返回结果: " + result.toString());
        Object data = result.get("data");
        if (data instanceof JSONArray) {
            JSONArray ele = (JSONArray)data;
            if (ele.size() > 1) {
                StringBuilder msg = new StringBuilder(STR_NEXT_LINE);
                for (int i=0; i<ele.size(); i++) {
                    msg.append(STR_SPACE_3 + ele.get(i).toString()).append(STR_NEXT_LINE);
                }
                LoggerUtils.info(msg.toString());
            }
        }
        return response;
    }

    private void initRequest(Map<String, Object> param, cn.hutool.json.JSONObject jsonObject) {
        Iterator<String> iterator = field.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (param.containsKey(key)) {
                jsonObject.set(key, param.get(key));
            }
        }
    }

    private List<HepTaskDto> sortTask(List<HepTaskDto> task) {
        List<HepTaskDto> res = new ArrayList<>();
        Set<String> existkey = new HashSet<>();
        task.sort(new Comparator<HepTaskDto>() {
            @Override
            public int compare(HepTaskDto o1, HepTaskDto o2) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    int orderNo1 = Integer.valueOf(o1.getOrderNo() == null ? STR_0 : o1.getOrderNo());
                    int orderNo2 = Integer.valueOf(o2.getOrderNo() == null ? STR_0 : o2.getOrderNo());
                    if (orderNo1 != orderNo2) {
                        return orderNo1 - orderNo2;
                    }

                    int endDate1 = Integer.valueOf(o1.getEndDate() == null ? STR_99999999 : o1.getEndDate());
                    int endDate2 = Integer.valueOf(o2.getEndDate() == null ? STR_99999999 : o2.getEndDate());
                    if (endDate1 != endDate2) {
                        return endDate1 - endDate2;
                    }

                    int closeDate1 = Integer.valueOf(o1.getCloseDate() == null ? STR_99999999 : o1.getCloseDate());
                    int closeDate2 = Integer.valueOf(o2.getCloseDate() == null ? STR_99999999 : o2.getCloseDate());
                    if (closeDate1 != closeDate2) {
                        return closeDate1 - closeDate2;
                    }

                    int publishDate1 = Integer.valueOf(o1.getPublishDate() == null ? STR_99999999 : o1.getPublishDate());
                    int publishDate2 = Integer.valueOf(o2.getPublishDate() == null ? STR_99999999 : o2.getPublishDate());
                    if (publishDate1 != publishDate2) {
                        return publishDate1 - publishDate2;
                    }

                    if (StringUtils.isNotBlank(o1.getEstimateFinishDate()) && StringUtils.isNotBlank(o2.getEstimateFinishDate())) {
                        Date finishTime1 = simpleDateFormat.parse(getValue(o1.getEstimateFinishDate() +  STR_SPACE + o1.getEstimateFinishTime(), STR_BLANK));
                        Date finishTime2 = simpleDateFormat.parse(getValue(o2.getEstimateFinishDate() +  STR_SPACE + o2.getEstimateFinishTime(), STR_BLANK));
                        if (finishTime1.getTime() != finishTime2.getTime()) {
                            return finishTime1.compareTo(finishTime2);
                        }
                    }
                    String taskName1 = o1.getName();
                    String taskName2 = o2.getName();
                    if (taskName1.contains(STR_BRACKETS_2_RIGHT)) {
                        taskName1 = taskName1.split(STR_BRACKETS_2_RIGHT)[1];
                    }
                    if (taskName2.contains(STR_BRACKETS_2_RIGHT)) {
                        taskName2 = taskName2.split(STR_BRACKETS_2_RIGHT)[1];
                    }
                    return taskName1.compareTo(taskName2);
                } catch (ParseException e) {
                    LoggerUtils.info(e);
                }
                return 1;
            }
        });
        for (int i=0; i<task.size(); i++) {
            HepTaskDto item = task.get(i);
            String taskNumber = item.getTaskNumber();
            String taskName = item.getName();
            if (existkey.contains(taskNumber)) {
                continue;
            }
            existkey.add(taskNumber);
            res.add(item);
            for (int j=0; j<task.size(); j++) {
                HepTaskDto itemTmp = task.get(j);
                String taskNumberTmp = itemTmp.getTaskNumber();
                String taskNameTmp = itemTmp.getName();
                if (existkey.contains(taskNumberTmp)) {
                    continue;
                }
                if (taskNameTmp.equals(taskName)) {
                    res.add(itemTmp);
                    existkey.add(taskNumberTmp);
                }
            }
        }
        return res;
    }

    private String getValue(String value, String type) {
        if (StringUtils.isBlank(value)) {
            if (StringUtils.isBlank(type) || STR_1.equals(type)) {
                return "9910-12-31 23:59:59";
            } else if (STR_2.equals(type)) {
                return "9990-12-31 23:59:59";
            } else if (STR_9.equals(type)) {
                return "9999-12-31 23:59:59";
            }
        }
        return value;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, TASK_TODO.getName()));
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            CURRENT_USER_ID = appConfigDto.getHepTaskUser();
            addTaskMenu(appConfigDto);
            executeQuery(null);
            buildTestData();
            //TaskUtils.execute(new HepTodoTask(new HepTodoTaskParam(this, "check")));
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static List<VersionDto> getVersionInfo() throws Exception {
        List<String> versionList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION_STAT), false);
        Map<String, String[]> versionExtend = getVersionExtendInfo();
        List<VersionDto> versionDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(versionList)) {
            String currentDay = CommonUtils.getCurrentDateTime3();
            for (String item : versionList) {
                String[] elements = item.split(STR_SEMICOLON);
                VersionDto versionDto = new VersionDto();
                String oriCloseDate = getRealDate(elements[1]);
                String oriPublishDate = getRealDate(elements[2]);
                String versionCode = elements[0];
                if (versionExtend.containsKey(versionCode)) {
                    oriCloseDate = versionExtend.get(versionCode)[0];
                    oriPublishDate = versionExtend.get(versionCode)[1];
                    versionDto.setOrderNo(versionExtend.get(versionCode)[2]);
                }
                String closeDate = CommonUtils.getIntervalDays(currentDay, oriCloseDate);
                String publishDate = CommonUtils.getIntervalDays(currentDay, oriPublishDate);
                versionDto.setCode(versionCode);
                versionDto.setCloseDate(oriCloseDate);
                versionDto.setPublishDate(oriPublishDate);
                versionDto.setClientName(elements[3]);
                versionDto.setCloseInterval(closeDate);
                versionDto.setPublishInterval(publishDate);
                versionDtoList.add(versionDto);
            }
        }
        return versionDtoList;
    }

    private static Map<String, String[]> getVersionExtendInfo() {
        Map<String, String[]> version = new HashMap<>();
        List<String> versionExtendList = null;
        try {
            versionExtendList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION_EXTEND_STAT), false);
        } catch (IOException e) {
            LoggerUtils.info(e);
        }
        if (CollectionUtils.isNotEmpty(versionExtendList)) {
            for (String item : versionExtendList) {
                String[] elementList = item.split(STR_SEMICOLON);
                version.put(elementList[0], new String[]{elementList[1], elementList[2], elementList[3]});
            }
        }
        return version;
    }

    private void addTaskMenu(AppConfigDto appConfigDto) {
        taskList.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String clickType = event.getButton().toString();
                if (RIGHT_CLICKED.equals(clickType)) {
                    Node node = event.getPickResult().getIntersectedNode();
                    HepWaitHandleTaskMenu hepWaitHandleTaskMenu = HepWaitHandleTaskMenu.getInstance();
                    HepTaskDto hepTaskDto = (HepTaskDto) taskList.getSelectionModel().getSelectedItem();
                    hepWaitHandleTaskMenu.getItems().forEach((item) -> {
                        if (NAME_MENU_UPDATE.equals(item.getText())) {
                            if (STATUS_DEV.equals(hepTaskDto.getStatus())) {
                                item.setVisible(true);
                            } else {
                                item.setVisible(false);
                            }
                        }
                    });
                    hepWaitHandleTaskMenu.show(node, event.getScreenX(), event.getScreenY());
                    appConfigDto.setHepTaskDto(hepTaskDto);
                }
            }
        });
    }

    private void buildTestData() throws Exception {
        if (!testScene()) {
            return;
        }
        JSONArray req = new JSONArray();
        for (int i=0; i<10; i++) {
            Map<String, Object> item = new HashMap<>(16);
            item.put(KEY_ID, i);
            item.put(KEY_TASK_NUMBER, "T20230801000" + i);
            item.put("product_name", "HUNDSUN基金登记过户系统软件V6.0");
            item.put(KEY_ESTIMATE_FINISH_TIME, "2023-12-24 22:59:59");
            switch (i % 7) {
                case 0: item.put("sprint_version", "TA6.0-FUND.V202304.10.000");break;
                case 1: item.put("sprint_version", "TA6.0-FUND.V202304.00.009");break;
                case 2: item.put("sprint_version", "TA6.0-FUND.V202304.00.002M9");break;
                case 3: item.put("sprint_version", "TA6.0V202202.06.028");break;
                case 4: item.put("sprint_version", "TA6.0-FUND.V202304.07.002");
                        item.put(KEY_ESTIMATE_FINISH_TIME, "2023-11-24 22:59:59");
                    break;
                case 5: item.put("sprint_version", "TA6.0-FUND.V202304.06.001");break;
                case 6: item.put("sprint_version", "TA6.0-FUND.V202304.04.002");break;
                default:break;
            }
            item.put("status", i % 2 == 0 ? 0 : 4);
            item.put("status_name", i % 2 == 0 ? "待启动" : "开发中");
            item.put("description", i % 2 == 0 ? "洛洛洛</p>洛洛洛" : "开发中");
            switch (i % 7) {
                case 0: item.put(KEY_NAME, "「开发」问题问题问题问题问题问题问题问题问题问题问题问题问题问题问题问题问题问题问题" + i);break;
                case 1: item.put(KEY_NAME, "「开发」【缺陷:45454】问题" + i);break;
                case 2: item.put(KEY_NAME, "「自测问题」问题" + i);break;
                case 6: item.put(KEY_NAME, "「自建任务」问题" + i);break;
                case 3: item.put(KEY_NAME, "「开发」已修改 问题" + i);break;
                case 4: item.put(KEY_NAME, "「开发」 问题" + i);break;
                case 5: item.put(KEY_NAME, "「修复问题」问题" + i);break;
                default:break;
            }
            req.add(item);
        }
        dealTaskList(req, logs, dayTodo, weekTodo, waitHandleTaskNum, dayPublish, weekPublish, dayClose, weekClose, taskList, true);
        OutputUtils.info(notice, TaCommonUtils.getMsgContainDateContainBr("查询成功"));
    }

    private boolean requestStatus(HttpResponse response) {
        return testScene() || STATUS_200 == response.getStatus();
    }

    private boolean testScene() {
        return !FileUtils.startByJar();
    }
}
