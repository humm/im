package com.hoomoomoo.im.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.AppCache;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
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

    private String CURRENT_USER_ID = STR_BLANK;
    private final static Integer STATUS_200 = 200;
    private final static String STR_STATUS_200 = "200";
    private final static String REQUEST_URL = "http://cloudin.proxy.in.hundsun.com/openapi/invoke/defaultFormData";
    private final static String APP_ID = "dqwhyanulhrmrrnk";
    private final static String APP_KEY = "fbbbee8e31a646d3a3a45f5c0e5b3e9";
    private final static String METHOD_GET_FIELD_INFO = "devtool/getFieldInfo";
    private final static String METHOD_FETCH_TASK_BY_ID = "devtool/fetchTaskById";
    private final static String METHOD_UPDATE_TASK_STATUS = "devtool/updateTaskStatus";
    private final static String METHOD_FETCH_TASK_LIST = "devtool/fetchTaskList";
    /**
     * 0:待启动,4:开发中,5:开发待集成,8:待集成,14:集成失败,16:开发集成失败,17:待审核,18:审核不通过,6:开发集成成功
     */
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

    public final static String DEV_COMMIT_TAG = "【分支已完成】";
    public final static String COMMIT_TAG = "【已提交】";
    public final static String UPDATE_TAG = "【已修改】";
    public final static String SELF_BUILD_TAG = "【自建任务】";
    public final static String SELF_TEST_TAG = "【自测问题】";
    public final static String DEFECT_TAG = "【缺陷:FUNDTAVI";

    private final static String EXTEND_USER_FRONT_CODE = "front";
    private String PAGE_USER = "";
    private boolean dealTask = true;

    private Map<String, String[]> color = new LinkedHashMap<String, String[]>(){{
        put("完成日期错误", new String[] {"-fx-text-background-color: #ff0073;", "1", "错误"});
        put("今天待提交", new String[] {"-fx-text-background-color: #ff0000;", "1", "今天"});
        put("本周待提交", new String[] {"-fx-text-background-color: #0015ff;", "1", "本周"});
        put("缺陷", new String[] {"-fx-text-background-color: #ff00a6;", "1", "缺陷"});
        put("自测问题", new String[] {"-fx-text-background-color: #804000;", "0", "自测"});
        put("自建任务", new String[] {"-fx-text-background-color: #008071;", "0", "自建"});
        put("已修改", new String[] {"-fx-text-background-color: #7b00ff;", "0", "已修改"});
        put("已提交", new String[] {"-fx-text-background-color: #7b00ff;", "0", "已提交"});
        put("默认", new String[] {"-fx-text-background-color: #000000;", "0", " "});
    }};


    // 字段顺序不可调整 与约定接口保持顺序一致
    private static Set<String> field = new LinkedHashSet<String>(){{
        add(KEY_CHARSET);
        add(KEY_REAL_FINISH_TIME);
        add(KEY_OPERATE_TYPE);
        add(KEY_METHOD);
        add(KEY_SUGGESTION);
        add(KEY_FORMAT);
        add(KEY_CURRENT_USER_ID);
        add(KEY_SIGN);
        add(KEY_REAL_WORKLOAD);
        add(KEY_APP_KEY);
        add(KEY_FINISH_PERCENTAGE);
        add(KEY_INTEGRATE_ATTENTION);
        add(KEY_MODIFIED_FILE);
        add(KEY_STATUS_LIST);
        add(KEY_ID);
        add(KEY_APP_ID);
        add(KEY_EDIT_DESCRIPTION);
        add(KEY_TIMESTAMP);
        add(KEY_SELF_TEST_DESC);
    }};

    private List<String> logs = new ArrayList<>();

    @FXML
    private AnchorPane hep;

    @FXML
    private AnchorPane todoTitle;

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
    private ComboBox sprintVersionQuery;

    @FXML
    private TextField statusName;

    @FXML
    private TextField id;

    @FXML
    public TextArea notice;

    @FXML
    private TableView taskList;

    @FXML
    private Button query;

    @FXML
    private Button syncFileBtn;

    @FXML
    private Button extendUser;

    @FXML
    private Button queryCondition;

    @FXML
    private Button reset;

    @FXML
    private AnchorPane condition;

    @FXML
    private Button syncTask;

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

    @FXML
    private Label waitMergerNum;

    @FXML
    private Label taskTips;

    @FXML
    private Label fileTipsVersion;

    @FXML
    private Label fileTipsFile;

    @FXML
    private Label fileTipsFileOperate;

    @FXML
    private Label fileTipsFileStatus;

    @FXML
    private Label fileTipsFileTime;

    @FXML
    private RadioButton all;

    @FXML
    private RadioButton only;

    @FXML
    private RadioButton devCompleteHide;
    
    @FXML
    private RadioButton devCompleteShow;

    private Pane mask;

    private static Map<String, Integer> minDateCache = new HashMap<>();

    private static Set<String> fileSyncSourceFile = new HashSet<>();

    @FXML
    void syncOrSuspend(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (appConfigDto.getFileSyncThread() != null) {
            CommonUtils.stopHepToDoSyncFile(appConfigDto);
            syncFileBtn.setText("启动");
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("暂停文件同步"));
        } else {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("启动文件同步"));
            syncFile(appConfigDto);
        }
    }

    @FXML
    void selectAll(ActionEvent event) throws Exception {
        OutputUtils.selected(all, true);
        OutputUtils.selected(only, false);
        OutputUtils.selected(devCompleteHide, false);
        OutputUtils.selected(devCompleteShow, false);
        if (event != null) {
            executeQuery(null);
        }
    }

    @FXML
    void selectOnly(ActionEvent event) throws Exception {
        OutputUtils.selected(only, true);
        OutputUtils.selected(all, false);
        OutputUtils.selected(devCompleteHide, false);
        OutputUtils.selected(devCompleteShow, false);
        if (event != null) {
            executeQuery(null);
        }
    }

    @FXML
    void selectDevCompleteHide(ActionEvent event) throws Exception {
        OutputUtils.selected(devCompleteHide, true);
        OutputUtils.selected(devCompleteShow, false);
        OutputUtils.selected(only, false);
        OutputUtils.selected(all, false);
        if (event != null) {
            executeQuery(null);
        }
    }

    @FXML
    void selectDevCompleteShow(ActionEvent event) throws Exception {
        OutputUtils.selected(devCompleteShow, true);
        OutputUtils.selected(devCompleteHide, false);
        OutputUtils.selected(only, false);
        OutputUtils.selected(all, false);
        if (event != null) {
            executeQuery(null);
        }
    }

    @FXML
    void showTaskInfo(MouseEvent event) throws Exception {
        HepTaskDto item = (HepTaskDto) taskList.getSelectionModel().getSelectedItem();
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
            if (isExtendUser()) {
                OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("关联用户不支持此操作"));
                return;
            }
            operateTask(item);
        }
    }

    void operateTask(HepTaskDto item) throws Exception {
        if (TaCommonUtils.restPlan()) {
            CommonUtils.showTipsByRest();
            return;
        }
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
                    OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
                }
            }
        } else if (STATUS_DEV.equals(status) || STATUS_AUDIT_FAIL.equals(status)) {
            try {
                completeTask(item);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
            }
        } else {
            LoggerUtils.info("不支持的操作类型");
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("不支持的操作类型"));
        }
    }

    @FXML
    void executeUpdateVersion(ActionEvent event) throws Exception {
        updateVersion.setDisable(true);
        try {
            JvmCache.getSystemToolController().executeUpdateVersion();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("版本同步成功"));
            CommonUtils.showTipsByInfo("版本同步成功");
            executeQuery(null);
        } catch (Exception e) {
            String msg = e.getMessage();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
            CommonUtils.showTipsByError(msg);
        } finally {
            updateVersion.setDisable(false);
        }
    }

    @FXML
    void showVersion(ActionEvent event) throws Exception {
        showVersion.setDisable(true);
        try {
            doShowVersion();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("查看成功"));
        } catch (Exception e) {
            String msg = e.getMessage();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
            CommonUtils.showTipsByError(msg);
        } finally {
            showVersion.setDisable(false);
        }
    }

    void doShowVersion() throws Exception {
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
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("检查文件完成，请查看检查结果"));
            CommonUtils.showTipsByInfo("检查文件完成，请查看检查结果");
        } catch (Exception e) {
            String msg = e.getMessage();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
            CommonUtils.showTipsByError(msg);
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
            String msg = e.getMessage();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
            CommonUtils.showTipsByError(msg);
        } finally {
            scriptShow.setDisable(false);
        }
    }

    @FXML
    void executeExtendUser(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (StringUtils.isBlank(appConfigDto.getHepTaskUserExtend())) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("请设置关联用户信息【hep.task.user.extend】"));
        }
        OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("关联用户任务加载开始 . . ."));
        try {
            extendUser.setDisable(true);
            showExtendTask();
        } finally {
            extendUser.setDisable(false);
        }
        OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("关联用户任务加载完成 . . ."));
    }

    @FXML
    void selectSprintVersion(ActionEvent event) throws Exception {
    }

    @FXML
    void executeReset(ActionEvent event) throws Exception {
        OutputUtils.clearLog(taskNumberQuery);
        OutputUtils.clearLog(nameQuery);
        OutputUtils.clearLog(sprintVersionQuery);
        executeQuery(null);
    }

    @FXML
    void syncTaskInfo (ActionEvent event) throws Exception {
        syncTask.setDisable(true);
        try {
            JvmCache.getSystemToolController().executeSyncTaskInfo();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("同步任务信息成功"));
            CommonUtils.showTipsByInfo("同步任务信息成功");
            executeQuery(null);
        } catch (Exception e) {
            String msg = e.getMessage();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
            CommonUtils.showTipsByError(msg);
        } finally {
            syncTask.setDisable(false);
        }
    }

    @FXML
    void executeQuery(ActionEvent event) throws Exception {
        HepTodoTaskParam hepTodoTaskParam = new HepTodoTaskParam(this, "doQuery");
        hepTodoTaskParam.setEvent(event);
        TaskUtils.execute(new HepTodoTask(hepTodoTaskParam));
    }

    public void doQuery(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        initUserInfo(appConfigDto);
        if (StringUtils.isBlank(CURRENT_USER_ID)) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("请配置【 hep.task.user 】"));
            return;
        }
        try {
            setProgress(0);
            updateProgress();
            if (event != null) {
                minDateCache.clear();
            }
            TaskUtils.execute(new HepTodoTask(new HepTodoTaskParam(this, "doExecuteQuery")));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
        }
    }

    public void doExecuteQuery() {
        try {
            query.setDisable(true);
            queryCondition.setDisable(true);
            reset.setDisable(true);
            execute(OPERATE_QUERY, null);
            LoggerUtils.writeLogInfo(TASK_TODO.getCode(), new Date(), new ArrayList<>(logs));
            logs.clear();
            setProgress(1);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(ExceptionMsgUtils.getMsg(e)));
        } finally {
            query.setDisable(false);
            queryCondition.setDisable(false);
            reset.setDisable(false);
        }
    }

    public JSONArray execute(String operateType, HepTaskDto hepTaskDto) throws Exception {
        if (PAGE_USER.equals(EXTEND_USER_FRONT_CODE)) {
            dealTask = false;
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String userExtend = appConfigDto.getHepTaskUserExtend();
            JSONArray task = new JSONArray();
            if (StringUtils.isNotBlank(userExtend)) {
                userExtend = appConfigDto.getHepTaskUser() + STR_COMMA + userExtend;
                String[] user = userExtend.split(STR_COMMA);
                for (String item : user) {
                    if (EXTEND_USER_FRONT_CODE.equals(item)) {
                        continue;
                    }
                    CURRENT_USER_ID = item;
                    String userInfo = String.format("查询用户【%s】", CURRENT_USER_ID);
                    OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(userInfo));
                    if (!logs.contains(userInfo)) {
                        logs.add(userInfo);
                    }
                    task.addAll(executeReal(operateType, hepTaskDto));
                }
                dealTask = true;
                dealTaskList(task, true);
            }
            return task;
        } else {
            return executeReal(operateType, hepTaskDto);
        }
    }

    public JSONArray executeReal(String operateType, HepTaskDto hepTaskDto) throws Exception {
        String tipMsg;
        HttpResponse response;
        if (OPERATE_QUERY.equals(operateType) || OPERATE_COMPLETE_QUERY.equals(operateType)) {
            response = sendPost(getTaskList());
            tipMsg = "查询成功 . . . . . .";
        } else if (OPERATE_START.equals(operateType)) {
            response = sendPost(startTask(hepTaskDto));
            tipMsg = "启动成功 .";
        } else if (OPERATE_COMPLETE.equals(operateType)) {
            response = sendPost(executeCompleteTask(hepTaskDto));
            tipMsg = "提交成功 . .";
            if (OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
                tipMsg = "更新成功 . . .";
            }
        } else {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("不支持的操作类型"));
            throw new Exception("不支持的操作类型");
        }
        JSONArray items = new JSONArray();
        if (response != null) {
            Map<String, Object> responseInfo;
            String message = response.body();
            if (requestStatus(response)) {
                responseInfo = (Map) JSONObject.parse(response.body());
                message = String.valueOf(responseInfo.get(KEY_MESSAGE));
                String code = String.valueOf(responseInfo.get(KEY_CODE));
                if (STR_STATUS_200.equals(code)) {
                    message = tipMsg;
                    Object data = responseInfo.get(KEY_DATA);
                    if (data instanceof Map) {
                        items.add(data);
                    } else if (data instanceof List) {
                        items = (JSONArray) data;
                    }
                    if (OPERATE_QUERY.equals(operateType)) {
                        dealTaskList(items, true);
                    } else if (OPERATE_START.equals(operateType)) {
                        executeQuery(null);
                    }
                }
            }
            if (notice == null) {
                notice = JvmCache.getHepTodoController().notice;
            }
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(message));
        }
        return items;
    }

    public void completeTask(HepTaskDto hepTaskDto) throws Exception {
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
                if (OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
                    Map responseInfo = (Map) JSONObject.parse(response.body());
                    String code = String.valueOf(responseInfo.get(KEY_CODE));
                    if (STR_STATUS_200.equals(code)) {
                        Map data = (Map) responseInfo.get(KEY_DATA);
                        hepTaskDto.setRealWorkload(STR_1);
                        hepTaskDto.setModifiedFile(TaCommonUtils.formatTextBrToNextLine((String) data.get(KEY_MODIFIED_FILE)));
                        hepTaskDto.setEditDescription(TaCommonUtils.formatTextBrToNextLine((String) data.get(KEY_EDIT_DESCRIPTION)));
                        hepTaskDto.setSuggestion(TaCommonUtils.formatTextBrToNextLine((String) data.get(KEY_SUGGESTION)));
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
                OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
            }
        }
    }

    private Map<String, Object> executeCompleteTask(HepTaskDto hepTaskDto) throws Exception {
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
        // 今天总工时
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
    public void dealTaskList(JSONArray task, boolean tagFlag) {
        if (!dealTask) {
            return;
        }
        Set<String> dayTodoTask = new HashSet<>();
        Set<String> tomorrowTodoTask = new HashSet<>();
        Set<String> thirdTodoTask = new HashSet<>();
        Set<String> weekTodoTask = new HashSet<>();
        Set<String> finishDateError = new HashSet<>();
        taskList.setDisable(true);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<String> dayPublishVersion = appConfigDto.getDayPublishVersion();
        List<HepTaskDto> res = JSONArray.parseArray(JSONObject.toJSONString(task), HepTaskDto.class);
        res = sortTask(res, true);
        filterTask(res);
        if (tagFlag) {
            initTag(res);
        }
        Map<String, String> taskMinCompleteDate = new HashMap<>();
        for (int i = 0; i < task.size(); i++) {
            Map<String, Object> item = (Map) task.get(i);
            String name = String.valueOf(item.get(KEY_NAME)).replaceAll(STR_NEXT_LINE, STR_BLANK);
            String estimateFinishTime = String.valueOf(item.get(KEY_ESTIMATE_FINISH_TIME)).split(STR_SPACE)[0];
            if (taskMinCompleteDate.containsKey(name)) {
                if (estimateFinishTime.compareTo(taskMinCompleteDate.get(name)) < 0) {
                    taskMinCompleteDate.put(name, estimateFinishTime);
                }
            } else {
                taskMinCompleteDate.put(name, estimateFinishTime);
            }
        }
        boolean hasBlank = false;
        int taskTotal = 0;
        Map<String, Map<String, String>> version = new HashMap<>();
        StringBuilder weekVersion = new StringBuilder();
        StringBuilder weekCloseVersion = new StringBuilder();
        StringBuilder dayVersion = new StringBuilder();
        StringBuilder dayCloseVersion = new StringBuilder();
        String currentDay = CommonUtils.getCurrentDateTime3();
        String currentDate = CommonUtils.getCurrentDateTime4();
        String tomorrowDate = CommonUtils.getTomorrowDateTime();
        String thirdDate = CommonUtils.getCustomDateTime(2);
        String lastDayByWeek = CommonUtils.getLastDayByWeek();
        String weekDay = CommonUtils.getLastDayByWeek2();
        Map<String, String> taskCustomerName = new HashMap<>();
        Map<String, String> taskDemandNo = new HashMap<>();
        Map<String, String> taskDemandStatus = new HashMap<>();
        Map<String, String> taskSubmitStatus = new HashMap<>();
        Map<String, String> taskCancelDevSubmit = new HashMap<>();
        boolean waitTaskSync = false;
        try {
            List<String> versionList = new ArrayList<>();
            Map<String, String[]> versionExtend = new HashMap<>();
            if (proScene()) {
                versionList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION_STAT), false);
                versionExtend = getVersionExtendInfo();
                Map<String, Map<String, String>> taskInfo = getTaskInfo();
                taskCustomerName = taskInfo.get(KEY_CUSTOMER);
                taskDemandNo = taskInfo.get(KEY_TASK);
                taskDemandStatus = getDemandInfo();
                taskSubmitStatus = getTaskStatusInfo();
                taskCancelDevSubmit = getCancelDevSubmitTaskInfo();
            }
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
                    oriOrderNo = StringUtils.isBlank(versionExtend.get(versionCode)[2]) ? STR_0 : versionExtend.get(versionCode)[2];
                }
                String closeDate = CommonUtils.getIntervalDays(currentDay, oriCloseDate);
                String publishDate = CommonUtils.getIntervalDays(currentDay, oriPublishDate);
                ele.put(KEY_ORI_CLOSE_DATE, oriCloseDate);
                ele.put(KEY_ORI_PUBLISH_DATE, oriPublishDate);
                ele.put(KEY_CLOSE_DATE, closeDate);
                ele.put(KEY_PUBLISH_DATE, publishDate);
                ele.put(KEY_ORDER_NO, oriOrderNo);
                if (currentDay.equals(oriCloseDate)) {
                    dayCloseVersion.append(versionCode).append(STR_SPACE);
                }
                if (currentDay.equals(oriPublishDate)) {
                    dayVersion.append(versionCode).append(STR_SPACE);
                }

                if (currentDay.equals(oriCloseDate) || currentDay.equals(oriPublishDate)) {
                    dayPublishVersion.add(versionCode);
                }

                if (weekDay.compareTo(oriCloseDate) >= 0 && currentDay.compareTo(oriCloseDate) <= 0) {
                    weekCloseVersion.append(versionCode).append(STR_SPACE);
                }

                if (weekDay.compareTo(oriPublishDate) >= 0 && currentDay.compareTo(oriPublishDate) <= 0) {
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
        int mergerNum = 0;
        Set<String> skipVersion = new HashSet<>();
        if (StringUtils.isNotBlank(appConfigDto.getHepTaskErrorFinishDateSkipVersion())) {
            skipVersion.addAll(Arrays.asList(appConfigDto.getHepTaskErrorFinishDateSkipVersion().split(STR_COMMA)));
        }

        Map<String, Integer> minDate = new HashMap<>();
        Iterator<HepTaskDto> iterator = res.listIterator();
        Set<String> existTask = new HashSet<>();
        while (iterator.hasNext()) {
            HepTaskDto item = iterator.next();
            String taskName = item.getName().replaceAll(STR_NEXT_LINE, STR_BLANK);

            String taskNumberIn = item.getTaskNumber();
            if (taskDemandNo.containsKey(taskNumberIn)) {
                item.setDemandNo(taskDemandNo.get(taskNumberIn));
            }
            String demandNo = item.getDemandNo();
            if (StringUtils.isBlank(demandNo)) {
                demandNo = taskNumberIn;
            }
            if (!taskName.contains(COMMIT_TAG) && taskSubmitStatus.containsKey(taskNumberIn)) {
                taskName = COMMIT_TAG + taskName;
                if (!taskName.contains(DEV_COMMIT_TAG)) {
                    taskName = DEV_COMMIT_TAG + taskName;
                }
                item.setTaskMark(COMMIT_TAG.replace(STR_BRACKETS_3_LEFT, STR_BLANK).replace(STR_BRACKETS_3_RIGHT, STR_BLANK));
            }
            if (!taskName.contains(DEV_COMMIT_TAG) && (taskDemandStatus.containsKey(demandNo) || taskDemandStatus.containsKey(taskNumberIn)) && !taskCancelDevSubmit.containsKey(taskNumberIn)) {
                taskName = DEV_COMMIT_TAG + taskName;
                item.setEstimateFinishTime(getValue(STR_BLANK, STR_4));
            }
            if (PAGE_USER.equals(EXTEND_USER_FRONT_CODE)) {
                taskName = String.format("【%s】", item.getAssigneeId()) + taskName;
            }
            item.setName(taskName);
            if (taskName.contains(DEV_COMMIT_TAG)) {
                mergerNum++;
            }

            String status = item.getStatus();
            if (STATUS_WAIT_INTEGRATE.equals(status) || STATUS_WAIT_CHECK.equals(status)) {
                if (taskName.contains(DEV_COMMIT_TAG)) {
                    mergerNum--;
                }
                iterator.remove();
                continue;
            }
            if (hasBlank && StringUtils.isBlank(status)) {
                mergerNum--;
                iterator.remove();
                continue;
            }

            if (only.isSelected()) {
                if (existTask.contains(taskName)) {
                    iterator.remove();
                    continue;
                }
                existTask.add(taskName);
            } else if (devCompleteHide.isSelected()) {
                if (taskName.contains(DEV_COMMIT_TAG)) {
                    iterator.remove();
                    continue;
                }
            } else if (devCompleteShow.isSelected()) {
                if (!taskName.contains(DEV_COMMIT_TAG)) {
                    iterator.remove();
                    continue;
                }
            }

            String sprintVersion = item.getSprintVersion();
            if (version.containsKey(sprintVersion)) {
                Map<String, String> versionInfo = version.get(sprintVersion);
                item.setCloseDate(versionInfo.get(KEY_CLOSE_DATE));
                item.setOriCloseDate(CommonUtils.getCurrentDateTime5(versionInfo.get(KEY_ORI_CLOSE_DATE)));
                item.setOriPublishDate(CommonUtils.getCurrentDateTime5(versionInfo.get(KEY_ORI_PUBLISH_DATE)));
                item.setPublishDate(versionInfo.get(KEY_PUBLISH_DATE));
                item.setCustomer(versionInfo.get(KEY_CUSTOMER));
                item.setOrderNo(versionInfo.get(KEY_ORDER_NO));
            }
            String endDate = item.getEstimateFinishTime().split(STR_SPACE)[0].replaceAll(STR_HYPHEN, STR_BLANK);
            item.setEndDate(CommonUtils.getIntervalDays(currentDay, endDate));
            String estimateFinishDate = item.getEstimateFinishTime().split(STR_SPACE)[0];
            String estimateFinishTime = item.getEstimateFinishTime().split(STR_SPACE)[1];
            boolean today = todayMustComplete(item, currentDate, estimateFinishDate);
            boolean tomorrow = todayMustComplete(item, tomorrowDate, estimateFinishDate);
            boolean third = todayMustComplete(item, thirdDate, estimateFinishDate);
            if (dayVersion.toString().contains(sprintVersion + STR_SPACE) || dayCloseVersion.toString().contains(sprintVersion + STR_SPACE) || today) {
                dayVersionNum++;
                dayTodoTask.add(item.getTaskNumber());
            }
            if (tomorrow) {
                tomorrowTodoTask.add(item.getTaskNumber());
            }
            if (third) {
                thirdTodoTask.add(item.getTaskNumber());
            }
            if (!endDate.startsWith(STR_99)) {
                boolean week = today || (StringUtils.compare(lastDayByWeek, estimateFinishDate) >= 0);
                if (weekVersion.toString().contains(sprintVersion + STR_SPACE) || weekCloseVersion.toString().contains(sprintVersion + STR_SPACE) || week) {
                    weekVersionNum++;
                    weekTodoTask.add(item.getTaskNumber());
                }
            }
            if (!endDate.startsWith(STR_99) && StringUtils.isNotBlank(estimateFinishDate) && StringUtils.isNotBlank(item.getOriCloseDate())){
                if (StringUtils.compare(estimateFinishDate, item.getOriCloseDate()) > 0) {
                    if (!skipVersion.contains(item.getSprintVersion())) {
                        finishDateError.add(item.getTaskNumber());
                    }
                }
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
                item.setEstimateFinishDate(estimateFinishDate);
                item.setEstimateFinishTime(estimateFinishTime);
            }
            initMinDate(minDate, item);
        }
        iterator = res.listIterator();
        while (iterator.hasNext()) {
            HepTaskDto item = iterator.next();
            String taskName = item.getName();
            String taskNumberIn = item.getTaskNumber();
            if (minDate.containsKey(taskName)) {
                int min = minDate.get(taskName);
                int minCache = minDateCache.get(taskName);
                if (minCache < min) {
                    min = minCache;
                }
                String estimateFinishDate = item.getEstimateFinishDate();
                if (!taskName.contains(DEFECT_TAG) && (estimateFinishDate.startsWith(STR_9950) || estimateFinishDate.startsWith(STR_9960))) {
                    min = 999;
                } else if (estimateFinishDate.startsWith(STR_99)) {
                    min = Integer.valueOf(estimateFinishDate.replaceAll(STR_HYPHEN, STR_BLANK).substring(2, 4)) * -1;
                }
                item.setEndDate(String.valueOf(min));
            }
            if (taskMinCompleteDate.containsKey(taskName)) {
                if (!StringUtils.equals(taskMinCompleteDate.get(taskName), item.getEstimateFinishDate())) {
                    item.setEstimateFinishDate(STR_BLANK);
                }
            }
            item.setSprintVersion(formatVersion(item.getSprintVersion()));
            if (taskCustomerName.containsKey(taskNumberIn)) {
                item.setCustomer(taskCustomerName.get(taskNumberIn));
            } else {
                waitTaskSync = true;
            }
            if (StringUtils.isBlank(item.getCustomer()) && item.getName().contains(DEFECT_TAG)) {
                item.setCustomer(NAME_INNER_CUSTOMER);
            }
            String customer = item.getCustomer();
            if (StringUtils.isNotBlank(customer) && customer.length() > 6) {
                item.setCustomer(customer.substring(0, 6));
            }
        }

        res = sortTask(res, false);

        OutputUtils.clearLog(dayTodo);
        OutputUtils.info(dayTodo, String.valueOf(dayVersionNum));

        OutputUtils.clearLog(weekTodo);
        OutputUtils.info(weekTodo, String.valueOf(weekVersionNum));

        OutputUtils.clearLog(dayPublish);
        OutputUtils.info(dayPublish, formatVersion(dayVersion.toString()));

        OutputUtils.clearLog(weekPublish);
        OutputUtils.info(weekPublish, formatVersion(weekVersion.toString()));

        OutputUtils.clearLog(dayClose);
        OutputUtils.info(dayClose, formatVersion(dayCloseVersion.toString()));

        OutputUtils.clearLog(weekClose);
        OutputUtils.info(weekClose, formatVersion(weekCloseVersion.toString()));

        OutputUtils.clearLog(waitHandleTaskNum);
        OutputUtils.info(waitHandleTaskNum, String.valueOf(res.size() - taskTotal));

        OutputUtils.clearLog(waitMergerNum);
        OutputUtils.info(waitMergerNum, String.valueOf(mergerNum));

        OutputUtils.clearLog(taskTips);
        if (waitTaskSync) {
            OutputUtils.info(taskTips, "请同步任务信息");
        }

        if (dayVersionNum > 0) {
            dayTodo.setStyle(color.get("今天待提交")[0]);
        } else {
            dayTodo.setStyle(color.get("默认")[0]);
        }
        if (weekVersionNum > 0) {
            weekTodo.setStyle(color.get("本周待提交")[0]);
        } else {
            weekTodo.setStyle(color.get("默认")[0]);
        }

        OutputUtils.clearLog(taskList);
        infoTaskList(taskList, res, dayTodoTask, tomorrowTodoTask, thirdTodoTask, weekTodoTask, finishDateError);
        taskList.setDisable(false);
    }

    private String formatVersion(String ver) {
        return CommonUtils.getSimpleVer(ver);
    }

    private void initVersion(Set<String> currentTaskVersion, String sprintVersionQ) {
        List<String> taskVersion = new ArrayList<>(currentTaskVersion);
        Collections.sort(taskVersion, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String ver1 = o1.substring(o1.indexOf("V"));
                String ver2 = o2.substring(o2.indexOf("V"));
                return ver1.compareTo(ver2);
            }
        });
        ObservableList version = sprintVersionQuery.getItems();
        version.clear();
        if (CollectionUtils.isNotEmpty(taskVersion)) {
            Iterator<String> ver = taskVersion.iterator();
            while (ver.hasNext()) {
                version.add(formatVersion(ver.next()));
            }
        }
        version.add(STR_SPACE);
        if (StringUtils.isNotBlank(sprintVersionQ)) {
            sprintVersionQuery.getSelectionModel().select(sprintVersionQ);
        }
    }

    private static boolean todayMustComplete(HepTaskDto item, String currentDate, String estimateFinishDate) {
        if( StringUtils.equals(currentDate, estimateFinishDate)) {
            return true;
        }
        if (StringUtils.isNotBlank(item.getCloseDate())) {
            if (Integer.parseInt(item.getCloseDate()) == 0) {
                return true;
            }
        }
        if (StringUtils.isNotBlank(item.getPublishDate())) {
            if (Integer.parseInt(item.getPublishDate()) == 0) {
                return true;
            }
        }
        String endDate = item.getEndDate();
        if (StringUtils.isNotBlank(endDate)) {
            int date = Integer.parseInt(endDate);
            if (date <= 0 && date > -50) {
                return true;
            }
        }
        return false;
    }

    private static void initMinDate(Map<String, Integer> minDate, HepTaskDto item) {
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
        if (min < 0) {
            min = Integer.valueOf(endDate);
        }
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
            for (int i = 0; i < oriDate.length(); i++) {
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

    private void infoTaskList(TableView taskListIn, List<HepTaskDto> res, Set<String> dayTodoTask,
                              Set<String> tomorrowTodoTask, Set<String> thirdTodoTask, Set<String> weekTodoTask, Set<String> finishDateError) {
        if (taskListIn == null) {
            return;
        }
        Platform.runLater(() -> {
            for (HepTaskDto hepTaskDto : res) {
                taskListIn.getItems().add(hepTaskDto);
                // 设置行
                initRowColor(taskListIn, dayTodoTask, tomorrowTodoTask, thirdTodoTask,  weekTodoTask, finishDateError);
            }
            OutputUtils.setEnabled(taskListIn);
        });
    }

    private void initRowColor(TableView taskListIn, Set<String> dayTodoTask, Set<String> tomorrowTodoTask,
                              Set<String> thirdTodoTask, Set<String> weekTodoTask, Set<String> finishDateError) {
        taskListIn.setRowFactory(new Callback<TableView<HepTaskDto>, TableRow<HepTaskDto>>() {
            @Override
            public TableRow<HepTaskDto> call(TableView<HepTaskDto> param) {
                final TableRow<HepTaskDto> row = new TableRow<HepTaskDto>() {
                    @Override
                    protected void updateItem(HepTaskDto item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && getIndex() > -1) {
                            String taskName = item.getName();
                            String taskNumber = item.getTaskNumber();
                            String taskNameTag = getTaskNameTag(taskName);
                            String[] taskColor;
                            if (finishDateError.contains(taskNumber)) {
                                taskColor = color.get("完成日期错误");
                            } else if (dayTodoTask.contains(taskNumber)) {
                                taskColor = color.get("今天待提交");
                            } else if (weekTodoTask.contains(taskNumber)) {
                                taskColor = color.get("本周待提交");
                            } else if (taskNameTag.contains(SELF_TEST_TAG)) {
                                taskColor = color.get("自测问题");
                            } else if (taskNameTag.contains(SELF_BUILD_TAG)) {
                                taskColor = color.get("自建任务");
                            } else if (taskName.contains(UPDATE_TAG) || taskName.contains(DEV_COMMIT_TAG)) {
                                taskColor = color.get("已修改");
                            } else if (taskName.contains(COMMIT_TAG)) {
                                taskColor = color.get("已提交");
                            } else if (taskName.contains(DEFECT_TAG)) {
                                taskColor = color.get("缺陷");
                            } else {
                                taskColor = color.get("默认");
                            }
                            setStyle(taskColor[0]);
                            if (StringUtils.equals(STR_1, taskColor[1])) {
                                String mark = taskColor[2];
                                if ("本周".equals(mark)) {
                                    if (tomorrowTodoTask.contains(taskNumber)) {
                                        mark = "明天";
                                    } else if (thirdTodoTask.contains(taskNumber)) {
                                        mark = "后天";
                                    }
                                }
                               item.setTaskMark(mark);
                            }
                        }
                        // 颜色展示
                        if (false) {
                            // 完成时间错误
                            setStyle("-fx-text-background-color: #ff0073;");
                            // 今天待提交
                            setStyle("-fx-text-background-color: #ff0000;");
                            // 本周待提交
                            setStyle("-fx-text-background-color: #0015ff;");
                            // 缺陷
                            setStyle("-fx-text-background-color: #ff00a6;");
                            // 自测问题
                            setStyle("-fx-text-background-color: #804000;");
                            // 自建任务
                            setStyle("-fx-text-background-color: #008071;");
                            // 已修改
                            setStyle("-fx-text-background-color: #7b00ff;");
                            // 已提交
                            setStyle("-fx-text-background-color: #7b00ff;");
                            // 默认
                            setStyle("-fx-text-background-color: #000000;");
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
                String taskNameTmp = taskName.substring(taskName.indexOf(STR_BRACKETS_3_LEFT) + 1, taskName.indexOf(STR_BRACKETS_3_RIGHT));
                if (taskNameTmp.contains(DEV_COMMIT_TAG)) {
                    if (!tags.containsKey(DEV_COMMIT_TAG)) {
                        tags.put(DEV_COMMIT_TAG, DEV_COMMIT_TAG);
                    }
                }
                if (taskNameTmp.contains(DEFECT_TAG) || taskName.startsWith(STR_BRACKETS_3_LEFT)) {
                    if (taskNameTmp.contains(STR_COLON)) {
                        taskNameTmp = taskNameTmp.split(STR_COLON)[0];
                    }
                    if (!tags.containsKey(taskNameTmp)) {
                        tags.put(taskNameTmp, taskNameTmp);
                    }
                }
            } else if (taskName.contains(STR_BRACKETS_2_RIGHT)) {
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
                    String tag = ((Label) event.getSource()).getText();
                    nameQuery.setText(tag);
                    JSONArray res = null;
                    try {
                        res = execute(OPERATE_COMPLETE_QUERY, null);
                    } catch (Exception e) {

                    }
                    dealTaskList(res, false);
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
        Set<String> currentTaskVersion = new HashSet<>();
        while (iterator.hasNext()) {
            HepTaskDto item = iterator.next();
            String taskNumber = item.getTaskNumber();
            String taskName = item.getName();
            String sprintVersion = item.getSprintVersion();
            currentTaskVersion.add(sprintVersion);
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
            if (taskName.contains(DEFECT_TAG)) {
                item.setEstimateFinishTime(getValue(STR_BLANK, STR_1));
                taskType.put(STR_1, STR_1);
            } else if (taskNameTag.contains(SELF_TEST_TAG)) {
                item.setEstimateFinishTime(getValue(STR_BLANK, STR_2));
                taskType.put(STR_2, STR_2);
            } else if (taskNameTag.contains(SELF_BUILD_TAG)) {
                item.setEstimateFinishTime(getValue(STR_BLANK, STR_3));
                taskType.put(STR_3, STR_3);
            } else if (taskName.contains(COMMIT_TAG)) {
                item.setEstimateFinishTime(getValue(STR_BLANK, STR_4));
                taskType.put(STR_4, STR_4);
            } else if (taskName.contains(UPDATE_TAG) || taskName.contains(DEV_COMMIT_TAG)) {
                item.setEstimateFinishTime(getValue(STR_BLANK, STR_5));
                taskType.put(STR_5, STR_5);
            }
        }
        initVersion(currentTaskVersion, sprintVersionQ);
    }

    private static String getTaskNameTag(String taskName) {
        int start = 0;
        int end = taskName.length();
        if (taskName.contains(STR_BRACKETS_3_LEFT) && taskName.contains(STR_BRACKETS_3_RIGHT)) {
            return taskName.substring(taskName.indexOf(STR_BRACKETS_3_LEFT) , taskName.indexOf(STR_BRACKETS_3_RIGHT) + 1);
        } else if (taskName.startsWith(STR_BRACKETS_2_LEFT) && taskName.contains(STR_BRACKETS_2_RIGHT)) {
            start = taskName.indexOf(STR_BRACKETS_2_LEFT);
            end = taskName.indexOf(STR_BRACKETS_2_RIGHT) + 1;
        }
        return taskName.substring(start, end);
    }

    private HttpResponse sendPost(Map<String, Object> param) throws Exception {
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
        if (!proScene()) {
            return null;
        }
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (STR_TRUE.equals(appConfigDto.getHepTaskPrintParam())) {
            logs.add("请求入参: " + jsonObject);
        } else {
            cn.hutool.json.JSONObject print = initJSONObject();
            print.set(KEY_METHOD, param.get(KEY_METHOD));
            if (param.get(KEY_OPERATE_TYPE) != null) {
                print.set(KEY_OPERATE_TYPE, param.get(KEY_OPERATE_TYPE));
            }
            logs.add("请求入参: " + print);
        }

        HttpResponse response = HttpRequest.post(REQUEST_URL).timeout(10 * 1000).form(jsonObject).execute();
        Map<String, String> result = (Map) JSONObject.parse(response.body());
        if (StringUtils.equals(STR_SUCCESS_CODE, result.get(KEY_CODE))) {
            if (STR_TRUE.equals(appConfigDto.getHepTaskPrintParam())) {
                logs.add("返回结果: " + result);
            } else {
                cn.hutool.json.JSONObject print = initJSONObject();
                print.set(KEY_MESSAGE, NAME_DEAL_SUCCESS);
                logs.add("返回结果: " +print);
            }
        } else {
            logs.add("返回结果: " + result);
        }

        Object data = result.get("data");
        if (data instanceof JSONArray) {
            JSONArray ele = (JSONArray) data;
            if (ele.size() > 1) {
                StringBuilder msg = new StringBuilder(STR_NEXT_LINE);
                for (int i = 0; i < ele.size(); i++) {
                    msg.append(STR_SPACE_3 + ele.get(i).toString()).append(STR_NEXT_LINE);
                }
            }
        }
        return response;
    }

    private cn.hutool.json.JSONObject initJSONObject() {
        return new cn.hutool.json.JSONObject(new LinkedHashMap<>());
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

    private List<HepTaskDto> sortTask(List<HepTaskDto> task, boolean prevOrder) {
        List<HepTaskDto> res = new ArrayList<>();
        Set<String> existkey = new HashSet<>();
        boolean completeShow = devCompleteShow.isSelected();
        task.sort(new Comparator<HepTaskDto>() {
            @Override
            public int compare(HepTaskDto o1, HepTaskDto o2) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if (prevOrder) {
                        String finishTime1 = getValue(o1.getEstimateFinishTime(), STR_BLANK);
                        String finishTime2 =getValue(o2.getEstimateFinishTime(), STR_BLANK);
                        return finishTime1.compareTo(finishTime2);
                    }
                    if (completeShow) {
                        String taskMark1 = StringUtils.isBlank(o1.getTaskMark()) ? STR_BLANK : o1.getTaskMark();
                        String taskMark2 = StringUtils.isBlank(o2.getTaskMark()) ? STR_BLANK : o2.getTaskMark();
                        if (!StringUtils.equals(taskMark1, taskMark2)) {
                            return taskMark2.compareTo(taskMark1);
                        }
                        String customer1 = getSortCustomerName(o1.getCustomer());
                        String customer2 = getSortCustomerName(o2.getCustomer());
                        return customer1.compareTo(customer2);
                    }
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
                        Date finishTime1 = simpleDateFormat.parse(getValue(o1.getEstimateFinishDate() + STR_SPACE + o1.getEstimateFinishTime(), STR_BLANK));
                        Date finishTime2 = simpleDateFormat.parse(getValue(o2.getEstimateFinishDate() + STR_SPACE + o2.getEstimateFinishTime(), STR_BLANK));
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
        for (int i = 0; i < task.size(); i++) {
            HepTaskDto item = task.get(i);
            String taskNumber = item.getTaskNumber();
            String taskName = item.getName();
            if (existkey.contains(taskNumber)) {
                continue;
            }
            existkey.add(taskNumber);
            res.add(item);
            for (int j = 0; j < task.size(); j++) {
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

    private String getSortCustomerName(String customerName) {
        if (StringUtils.isBlank(customerName)) {
            customerName = STR_BLANK;
        }
        return NAME_J_S_J_J.equals(customerName) ? NAME_A_D_B_L : customerName;
    }

    private String getValue(String value, String type) {
        if (StringUtils.isBlank(type)) {
            type = STR_1;
        }
        if (StringUtils.isBlank(value)) {
            if (STR_1.equals(type)) {
                return "9990-12-31 23:59:59";
            } else if (STR_2.equals(type)) {
                return "9980-12-31 23:59:59";
            }else if (STR_4.equals(type)) {
                return "9960-12-31 23:59:59";
            } else if (STR_5.equals(type)) {
                return "9950-12-31 23:59:59";
            } else if (STR_3.equals(type)) {
                return "9940-12-31 23:59:59";
            }
        }
        return value;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, TASK_TODO.getName()));
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            initUserInfo(appConfigDto);
            devCompleteHide.setSelected(true);
            if (isExtendUser()) {
                extendUser.setVisible(false);
                syncTask.setVisible(false);
            } else {
                JvmCache.setHepTodoController(this);
            }
            addTaskMenu(appConfigDto, this);
            executeQuery(null);
            initColorDesc();
            buildTestData();
            syncFile(appConfigDto);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private void syncFile(AppConfigDto appConfigDto) throws Exception {
        if (isExtendUser()) {
            return;
        }
        Map<String,String> syncFileVersion = appConfigDto.getFileSyncVersionMap();
        if (MapUtils.isEmpty(syncFileVersion)) {
            return;
        }
        String threadId = CommonUtils.getCurrentDateTime2();
        Thread fileSyncThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    appConfigDto.getThreadId().add(threadId);
                    OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("轮询线程数: " + appConfigDto.getThreadId().size()));
                    for (Map.Entry<String, String> version : syncFileVersion.entrySet()) {
                        String ver = version.getKey().toUpperCase();
                        String[] path = version.getValue().split(STR_COMMA);
                        if (path.length != 2) {
                            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(ver + " 扫描路径配置错误"));
                        }
                        String fileSyncSource = path[0];
                        String fileSyncTarget = path[1];
                        OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("轮询版本: " + ver.replace(STR_VERSION_PREFIX, STR_BLANK)));
                        fileSyncSourceFile.clear();
                        try {
                            sync(new File(fileSyncSource), fileSyncSource, fileSyncTarget, ver);
                        } catch (IOException e) {
                            LoggerUtils.info(e);
                            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
                        }
                        clearFile(new File(fileSyncTarget), ver);
                    }
                    try {
                        Thread.sleep(appConfigDto.getFileSyncTimer() * 1000);
                    } catch (InterruptedException e) {
                        LoggerUtils.info("停止文件同步");
                        break;
                    }
                }
            }
        });
        appConfigDto.setFileSyncThread(fileSyncThread);
        fileSyncThread.start();
        syncFileBtn.setText("暂停");
    }

    private void sync(File sourceFile, String sourcePath, String targetPath, String version) throws IOException {
        if (sourceFile.isDirectory()) {
            File[] files = sourceFile.listFiles();
            for (File item : files) {
                sync(item, sourcePath, targetPath, version);
            }
        } else {
            String source = sourceFile.getAbsolutePath();
            String target = source.replace(sourcePath, targetPath);
            fileSyncSourceFile.add(target);
            File targetFile = new File(target);
            boolean needSync = false;
            String operate = "修改";
            if (targetFile.exists() && sourceFile.lastModified() > targetFile.lastModified()) {
                needSync = true;
            }
            if (!targetFile.exists()) {
                operate = "新增";
                needSync = true;
            }
            if (needSync) {
                List<String> content = FileUtils.readNormalFile(source, false);
                OutputUtils.info(fileTipsVersion, version);
                OutputUtils.info(fileTipsFile, getFileName(source));
                OutputUtils.info(fileTipsFileOperate, operate);

                if (CollectionUtils.isEmpty(content)) {
                    OutputUtils.info(fileTipsFileStatus, "略过");
                    OutputUtils.info(fileTipsFileTime, CommonUtils.getCurrentDateTime14());
                    return;
                }

                OutputUtils.info(fileTipsFileStatus, "开始");
                OutputUtils.info(fileTipsFileTime, CommonUtils.getCurrentDateTime14());
                FileUtils.writeFile(target, content, ENCODING_UTF8, false);
                OutputUtils.info(fileTipsFileStatus, "结束");
                OutputUtils.info(fileTipsFileTime, CommonUtils.getCurrentDateTime14());
            }
        }
    }

    private String getFileName(String filePath) {
        int index = filePath.lastIndexOf("\\");
        if (index > 0) {
            filePath = filePath.substring(index + 1);
        }
        return filePath;

    }

    private void clearFile(File sourceFile, String version) {
        if (sourceFile.isDirectory()) {
            File[] files = sourceFile.listFiles();
            for (File item : files) {
                clearFile(item, version);
            }
        } else {
            String source = sourceFile.getAbsolutePath();
            if (!fileSyncSourceFile.contains(source)) {
                OutputUtils.info(fileTipsVersion, version);
                OutputUtils.info(fileTipsFile, getFileName(source));
                OutputUtils.info(fileTipsFileOperate, "删除");
                OutputUtils.info(fileTipsFileStatus, "开始");
                OutputUtils.info(fileTipsFileTime, CommonUtils.getCurrentDateTime14());
                FileUtils.deleteFile(sourceFile);

                OutputUtils.info(fileTipsFileStatus, "结束");
                OutputUtils.info(fileTipsFileTime, CommonUtils.getCurrentDateTime14());
            }
        }
    }

    private void initUserInfo(AppConfigDto appConfigDto) {
        if (StringUtils.isBlank(CURRENT_USER_ID)) {
            String activateFunction = appConfigDto.getActivateFunction();
            if (StringUtils.isNotBlank(activateFunction)) {
                String tabCode = activateFunction.split(STR_COLON)[0];
                if (StringUtils.equals(tabCode, EXTEND_USER_FRONT_CODE)) {
                    CURRENT_USER_ID = EXTEND_USER_FRONT_CODE;
                } else if (!MenuFunctionConfig.FunctionConfig.TASK_TODO.getCode().equals(tabCode) && appConfigDto.getHepTaskUserExtend().contains(tabCode)) {
                    CURRENT_USER_ID = tabCode;
                } else {
                    CURRENT_USER_ID = appConfigDto.getHepTaskUser();
                }
            } else {
                CURRENT_USER_ID = appConfigDto.getHepTaskUser();
            }
        }
        if (StringUtils.isBlank(PAGE_USER)) {
            PAGE_USER = CURRENT_USER_ID;
        }
        if (PAGE_USER.equals(EXTEND_USER_FRONT_CODE)) {
            CURRENT_USER_ID = EXTEND_USER_FRONT_CODE;
        }
        String user = String.format("当前用户【%s】", CURRENT_USER_ID);
        OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(user));
        if (!logs.contains(user)) {
            logs.add(user);
        }
    }

    private boolean isExtendUser() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        return !CURRENT_USER_ID.equals(appConfigDto.getHepTaskUser());
    }

    private void showExtendTask() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String userExtend = appConfigDto.getHepTaskUserExtend();
        if (StringUtils.isNotBlank(userExtend)) {
            userExtend = EXTEND_USER_FRONT_CODE + STR_COMMA + userExtend;
            String[] user = userExtend.split(STR_COMMA);
            Tab defaultTab = null;
            for (String extend : user) {
                MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.TASK_TODO;
                String tabCode = extend;
                String tabName = functionConfig.getName();
                Tab openTab = CommonUtils.getOpenTab(AppCache.FUNCTION_TAB_CACHE, tabCode, tabName);
                if (openTab == null) {
                    appConfigDto.setActivateFunction(CommonUtils.getMenuName(tabCode, tabName));
                    openTab = CommonUtils.getFunctionTab(functionConfig.getPath(), tabName, tabCode, tabName);
                    CommonUtils.setTabStyle(openTab, functionConfig);
                    CommonUtils.bindTabEvent(openTab);
                    AppCache.FUNCTION_TAB_CACHE.getTabs().add(openTab);
                }
                if (defaultTab == null) {
                    defaultTab = openTab;
                }
            }
            appConfigDto.setActivateFunction(defaultTab.getText());
            AppCache.FUNCTION_TAB_CACHE.getSelectionModel().select(defaultTab);
        }
    }

    private void initColorDesc() {
        double step = 15;
        double x = 950;
        double y = 135;
        Label label = new Label("颜色说明:");
        label.setStyle("-fx-font-weight: bold;");
        label.setLayoutX(x);
        label.setLayoutY(y);
        todoTitle.getChildren().add(label);
        int prevLen = 4;
        int diff = 0;
        for (Map.Entry<String, String[]> entry : color.entrySet()) {
            String key = entry.getKey();
            Label ele = new Label(key);
            int len = key.length();
            if (len < prevLen) {
                diff = prevLen - len;
                len = prevLen;
            }
            x += step * len;
            ele.setStyle(color.get(key)[0]);
            ele.setLayoutX(x);
            ele.setLayoutY(y);
            todoTitle.getChildren().add(ele);
            prevLen = len;
            x -= step * diff;
            x += 20;
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

    public Map<String, Map<String, String>> getTaskInfo() {
        Map<String, Map<String, String>> task = new HashMap<>();
        Map<String, String> customerName = new HashMap<>();
        Map<String, String> demandNo = new HashMap<>();
        try {
            List<String> taskList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_TASK_STAT), false);
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (String item : taskList) {
                    String[] elementList = item.split(STR_SEMICOLON);
                    customerName.put(elementList[0], elementList[1]);
                    demandNo.put(elementList[0], elementList[2]);
                }
            }
        } catch (IOException e) {
            LoggerUtils.info(e);
        }
        task.put(KEY_CUSTOMER, customerName);
        task.put(KEY_TASK, demandNo);
        return task;
    }

    public Map<String,String> getCancelDevSubmitTaskInfo() {
        Map<String, String> task = new HashMap<>();
        try {
            List<String> taskList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_DEV_EXTEND_STAT), false);
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (String item : taskList) {
                    String[] elementList = item.split(STR_SEMICOLON);
                    if (STR_FALSE.equals(elementList[1])) {
                        task.put(elementList[0], elementList[1]);
                    }
                }
            }
        } catch (IOException e) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
            LoggerUtils.info(e);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
        return task;
    }

    public Map<String,String> getDemandInfo() {
        Map<String, String> demand = new HashMap<>();
        try {
            List<String> taskList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_DEMAND_SYNC_STAT), false);
            taskList.addAll(FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_DEV_EXTEND_STAT), false));
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (String item : taskList) {
                    String[] elementList = item.split(STR_SEMICOLON);
                    if (STR_TRUE.equals(elementList[1])) {
                        demand.put(elementList[0], elementList[1]);
                    }
                }
            }
        } catch (IOException e) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
            LoggerUtils.info(e);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
        return demand;
    }

    public Map<String,String> getTaskStatusInfo() {
        Map<String, String> task = new HashMap<>();
        try {
            List<String> taskList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_EXTEND_STAT), false);
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (String item : taskList) {
                    String[] elementList = item.split(STR_SEMICOLON);
                    if (STR_TRUE.equals(elementList[1])) {
                        task.put(elementList[0], elementList[1]);
                    }
                }
            }
        } catch (IOException e) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
            LoggerUtils.info(e);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
        return task;
    }

    private void addTaskMenu(AppConfigDto appConfigDto, HepTodoController hepTodoController) throws Exception {
        taskList.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @SneakyThrows
            @Override
            public void handle(MouseEvent event) {
                String clickType = event.getButton().toString();
                if (RIGHT_CLICKED.equals(clickType)) {
                    if (TaCommonUtils.restPlan()) {
                        CommonUtils.showTipsByRest();
                        return;
                    }
                    Node node = event.getPickResult().getIntersectedNode();
                    HepWaitHandleTaskMenu hepWaitHandleTaskMenu = HepWaitHandleTaskMenu.getInstance();
                    HepTaskDto hepTaskDto = (HepTaskDto) taskList.getSelectionModel().getSelectedItem();
                    hepWaitHandleTaskMenu.getItems().forEach((item) -> {
                        if (NAME_MENU_UPDATE.equals(item.getText())) {
                            try {
                                if (isExtendUser()) {
                                    item.setVisible(false);
                                    return;
                                }
                            } catch (Exception e) {
                                // 无需处理异常
                            }
                            if (STATUS_DEV.equals(hepTaskDto.getStatus())) {
                                item.setVisible(true);
                            } else {
                                item.setVisible(false);
                            }
                        }
                    });
                    hepWaitHandleTaskMenu.show(node, event.getScreenX(), event.getScreenY());
                    appConfigDto.setHepTaskDto(hepTaskDto);
                    JvmCache.setActiveHepTodoController(hepTodoController);
                }
            }
        });
    }

    private boolean requestStatus(HttpResponse response) {
        return !proScene() || STATUS_200 == response.getStatus();
    }

    private boolean proScene() {
        return FileUtils.startByJar();
    }

    private void addMask() {
        // 遮罩层
        mask = new Pane();
        mask.setStyle("-fx-background-color: rgba(0,0,0,0.3)");
        mask.setPrefSize(2000, 2000);
        hep.getChildren().add(mask);
        mask.setVisible(false);
    }

    private void showMask() {
        mask.setVisible(true);
    }

    private void closeMask() {
        mask.setVisible(false);
    }

    private void buildTestData() throws Exception {
        if (proScene()) {
            return;
        }
        JSONArray req = new JSONArray();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> item = new HashMap<>(16);
            item.put(KEY_ID, i);
            item.put(KEY_TASK_NUMBER, "T20230801000" + i);
            item.put("product_name", "HUNDSUN基金登记过户系统软件V6.0");
            item.put(KEY_ESTIMATE_FINISH_TIME, "2024-09-02 22:59:59");
            item.put(KEY_CLOSE_DATE, "1");
            item.put(KEY_ORI_CLOSE_DATE, "2025-01-14");
            switch (i % 7) {
                case 0:
                    item.put("sprint_version", "TA6.0-FUND.V202304.10.000");
                    break;
                case 1:
                    item.put("sprint_version", "TA6.0-FUND.V202304.00.009M12");
                    break;
                case 2:
                    item.put("sprint_version", "TA6.0-FUND.V202304.00.002M9");
                    break;
                case 3:
                    item.put("sprint_version", "TA6.0V202202.06.028");
                    item.put(KEY_ESTIMATE_FINISH_TIME, "2025-01-13 22:59:59");
                    break;
                case 4:
                    item.put("sprint_version", "TA6.0V202202.06.022");
                    item.put(KEY_ESTIMATE_FINISH_TIME, "2024-08-28 22:59:59");
                    break;
                case 5:
                    item.put("sprint_version", "TA6.0-FUND.V202304.06.001");
                    break;
                case 6:
                    item.put("sprint_version", "TA6.0-FUND.V202304.04.002");
                    break;
                default:
                    break;
            }
            item.put("status", i % 2 == 0 ? 0 : 4);
            item.put("status_name", i % 2 == 0 ? "待启动" : "开发中");
            item.put("description", i % 2 == 0 ? "洛洛洛</p>洛洛洛" : "开发中");
            item.put("taskMark", i % 2 == 0 ? "已提交" : "");
            switch (i % 7) {
                case 0:
                    item.put(KEY_NAME, "「需求」" + i);
                    break;
                case 1:
                    item.put(KEY_NAME, "「开发」【缺陷:123\n678】" + i);
                    break;
                case 2:
                    item.put(KEY_NAME, "「开发任务」" + i);
                    break;
                case 3:
                    item.put(KEY_NAME, "「开发」已修改 问题" + i);
                    break;
                case 4:
                    item.put(KEY_NAME, "「开发」【分支已完成】 问题" + i);
                    break;
                case 5:
                    item.put(KEY_NAME, "「自测问题」问题" + i);
                    break;
                case 6:
                    item.put(KEY_NAME, "「自建任务」问题" + i);
                    break;
                default:
                    break;
            }
            req.add(item);
        }
        dealTaskList(req, true);
        OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("查询成功"));
    }

}
