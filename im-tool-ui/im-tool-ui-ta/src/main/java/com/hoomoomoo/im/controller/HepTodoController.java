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
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

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
    public final static String AUDIT_FAIL = "审核不通过";
    public final static String INTEGRATION_FAIL = "集成失败";

    private static Set<String> fileSyncSourceFile = new HashSet<>();

    Map<String, HepTaskDto> taskMinCompleteDate = new HashMap<>();
    Map<String, String> sortCodeCache = new HashMap<>();

    List<Label> colorList = new ArrayList<>();

    private final static String EXTEND_USER_FRONT_CODE = "front";
    private String PAGE_USER = "";
    private boolean dealTask = true;

    private final static String STYLE_RED_COLOR = "-fx-text-background-color: red;-fx-font-weight: bold;";
    private final static String STYLE_BLACK_COLOR = "-fx-text-background-color: #000000;-fx-font-weight: bold;";

    private Map<String, String[]> color = new LinkedHashMap<String, String[]>(){{
        put("完成日期超期", new String[] {"-fx-text-background-color: #7b00ff;", "超期"});
        put("今天待提交", new String[] {"-fx-text-background-color: #ff0000;", "今天"});
        put("本周待提交", new String[] {"-fx-text-background-color: #0015ff;", "本周"});
        put("缺陷", new String[] {"-fx-text-background-color: #ff00a6;", "缺陷"});
        put("默认", new String[] {"-fx-text-background-color: #000000;", ""});
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
    private TextField TaskDemandNo;

    @FXML
    private TextField id;

    @FXML
    public TextArea notice;

    @FXML
    public TextArea noticeSync;

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
    private Label memoryTips;

    @FXML
    private Label scrollTips;

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
    private Label frontTipsSideBar;

    @FXML
    private Label frontTips;

    @FXML
    private RadioButton all;

    @FXML
    private RadioButton only;

    @FXML
    private RadioButton devCompleteHide;

    @FXML
    private RadioButton devCompleteShow;

    private Pane mask;

    @FXML
    private SplitPane taskSplitPane;

    @FXML
    private Button sideBarBtn;

    private double defaultDividerPositions;

    private double defaultTaskNameWidth;

    @FXML
    void syncOrSuspend(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        Timer timer = appConfigDto.getTimerMap().get(KEY_FILE_SYNC_TIMER);
        if (timer != null) {
            CommonUtils.stopHepToDoSyncFile(appConfigDto);
            syncFileBtn.setText("启动文件同步");
            OutputUtils.info(scrollTips, STR_BLANK);
            OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr("停止文件同步"));
        } else {
            OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr("启动文件同步"));
            syncFile();
        }
    }

    @FXML
    void sideBarStatus(ActionEvent event) throws Exception {
        setSideBar();
    }

    @FXML
    void selectAll(ActionEvent event) throws Exception {
        OutputUtils.selected(all, true);
        OutputUtils.selected(only, false);
        OutputUtils.selected(devCompleteHide, false);
        OutputUtils.selected(devCompleteShow, false);
        executeQuery(event);
    }

    @FXML
    void selectOnly(ActionEvent event) throws Exception {
        OutputUtils.selected(only, true);
        OutputUtils.selected(all, false);
        OutputUtils.selected(devCompleteHide, false);
        OutputUtils.selected(devCompleteShow, false);
        executeQuery(event);
    }

    @FXML
    void selectDevCompleteHide(ActionEvent event) throws Exception {
        OutputUtils.selected(devCompleteHide, true);
        OutputUtils.selected(devCompleteShow, false);
        OutputUtils.selected(only, false);
        OutputUtils.selected(all, false);
        executeQuery(event);
    }

    @FXML
    void selectDevCompleteShow(ActionEvent event) throws Exception {
        OutputUtils.selected(devCompleteShow, true);
        OutputUtils.selected(devCompleteHide, false);
        OutputUtils.selected(only, false);
        OutputUtils.selected(all, false);
        executeQuery(event);
    }

    @FXML
    void showTaskInfo(MouseEvent event) throws Exception {
        HepTaskDto item = (HepTaskDto) taskList.getSelectionModel().getSelectedItem();
        item.setOperateType(STR_BLANK);
        OutputUtils.repeatInfo(taskNumber, item.getTaskNumber());
        OutputUtils.repeatInfo(name, item.getName());
        OutputUtils.repeatInfo(sprintVersion, item.getSprintVersion());
        OutputUtils.repeatInfo(TaskDemandNo, item.getDemandNo());
        OutputUtils.repeatInfo(id, item.getId());
        String clickType = event.getButton().toString();
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        appConfigDto.setHepTaskDto(item);
        String ver = item.getSprintVersion();
        String verYear = ver.split("\\.")[0];
        ver = TaCommonUtils.changeVersion(ver) + STR_COMMA;
        String authVer = appConfigDto.getFileSyncAuthVersion().replaceAll(STR_VERSION_PREFIX, STR_BLANK) + STR_COMMA;
        if (authVer.contains(ver) || verYear.compareTo("2025") >= 0 || verYear.compareTo("202202") == 0) {
            setFrontTips(false);
        } else {
            setFrontTips(true);
        }
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
        showExtentUserTask(true);
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
                sortCodeCache.clear();
                taskMinCompleteDate.clear();
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
                } else {
                    CommonUtils.showTipsByError(message, false);
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
                Map responseInfo = (Map) JSONObject.parse(response.body());
                String code = String.valueOf(responseInfo.get(KEY_CODE));
                if (STR_STATUS_200.equals(code)) {
                    Map data = (Map) responseInfo.get(KEY_DATA);
                    if (OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
                        hepTaskDto.setRealWorkload(STR_1);
                    }
                    hepTaskDto.setModifiedFile(TaCommonUtils.formatTextBrToNextLine((String) data.get(KEY_MODIFIED_FILE)));
                    hepTaskDto.setEditDescription(TaCommonUtils.formatTextBrToNextLine((String) data.get(KEY_EDIT_DESCRIPTION)));
                    hepTaskDto.setSuggestion(TaCommonUtils.formatTextBrToNextLine((String) data.get(KEY_SUGGESTION)));
                    hepTaskDto.setSelfTestDesc(TaCommonUtils.formatTextBrToNextLine((String) data.get(KEY_SELF_TEST_DESC)));
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
        Set<String> thirdDayTodoTask = new HashSet<>();
        Set<String> weekTodoTask = new HashSet<>();
        Set<String> finishDateError = new HashSet<>();
        Set<String> finishDateOver = new HashSet<>();
        taskList.setDisable(true);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<String> dayPublishVersion = appConfigDto.getDayPublishVersion();
        List<HepTaskDto> res = JSONArray.parseArray(JSONObject.toJSONString(task), HepTaskDto.class);
        filterTask(res);
        boolean hasBlank = false;
        int taskTotal = 0;
        Map<String, Map<String, String>> version = new HashMap<>();
        StringBuilder weekVersion = new StringBuilder();
        StringBuilder weekCloseVersion = new StringBuilder();
        StringBuilder dayVersion = new StringBuilder();
        StringBuilder dayCloseVersion = new StringBuilder();
        String todayDate = CommonUtils.getCurrentDateTime3();
        String currentDate = CommonUtils.getCurrentDateTime4();
        String tomorrowDate = CommonUtils.getTomorrowDateTime();
        String thirdDate = CommonUtils.getCustomDateTime(2);
        String lastDayByWeek = CommonUtils.getLastDayByWeek();
        String weekDay = CommonUtils.getLastDayByWeekYmd();
        List<String> versionList = getTaskVersionInfo();
        Map<String, Map<String, String>> taskInfo = getTaskInfo();
        Map<String, String> taskLevel = getTaskLevelInfo();
        Map<String, String> taskCustomerName = taskInfo.get(KEY_CUSTOMER);
        Map<String, String> taskDemandNo = taskInfo.get(KEY_TASK);
        Map<String, String> taskDemandStatus = getDemandInfo();
        Map<String, String> taskSubmitStatus = getTaskStatusInfo();
        Map<String, String> taskCancelDevSubmit = getCancelDevSubmitTaskInfo();
        boolean waitTaskSync = false;
        for (String item : versionList) {
            String[] elements = item.split(STR_SEMICOLON);
            Map<String, String> ele = new HashMap<>();
            String oriCloseDate = elements[1];
            String oriPublishDate = elements[2];
            String versionCode = elements[0];
            ele.put(KEY_ORI_CLOSE_DATE, oriCloseDate);
            ele.put(KEY_ORI_PUBLISH_DATE, oriPublishDate);
            ele.put(KEY_ORDER_NO, STR_0);
            if (currentDate.equals(oriCloseDate)) {
                dayCloseVersion.append(versionCode).append(STR_SPACE);
            }
            if (currentDate.equals(oriPublishDate)) {
                dayVersion.append(versionCode).append(STR_SPACE);
            }

            if (currentDate.equals(oriCloseDate) || currentDate.equals(oriPublishDate)) {
                dayPublishVersion.add(versionCode);
            }

            if (weekDay.compareTo(oriCloseDate) >= 0 && currentDate.compareTo(oriCloseDate) <= 0) {
                weekCloseVersion.append(versionCode).append(STR_SPACE);
            }

            if (weekDay.compareTo(oriPublishDate) >= 0 && currentDate.compareTo(oriPublishDate) <= 0) {
                weekVersion.append(versionCode).append(STR_SPACE);
            }
            version.put(versionCode, ele);
        }
        int dayVersionNum = 0;
        int weekVersionNum = 0;
        int mergerNum = 0;
        Iterator<HepTaskDto> iterator = res.listIterator();
        Set<String> existTask = new HashSet<>();
        while (iterator.hasNext()) {
            HepTaskDto item = iterator.next();
            String taskName = item.getName().replaceAll(STR_NEXT_LINE, STR_BLANK);
            item.setOriTaskName(taskName);
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
            }
            if (PAGE_USER.equals(EXTEND_USER_FRONT_CODE)) {
                taskName = String.format("【%s】", item.getAssigneeId()) + taskName;
            }
            item.setName(taskName);
            if (taskName.contains(DEV_COMMIT_TAG)) {
                mergerNum++;
            }

            if (taskLevel.containsKey(demandNo)) {
                item.setTaskLevel(taskLevel.get(demandNo));
            } else if (taskLevel.containsKey(taskNumberIn)) {
                item.setTaskLevel(taskLevel.get(taskNumberIn));
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

            String finishDate = item.getEstimateFinishTime().split(STR_SPACE)[0];
            String finishTime = item.getEstimateFinishTime().split(STR_SPACE)[1];
            item.setFinishDate(finishDate);
            item.setFinishTime(finishTime);

            String sprintVersion = item.getSprintVersion();
            if (sprintVersion.startsWith(KEY_TA5) || (sprintVersion.contains(KEY_TA6) && !sprintVersion.contains(KEY_FUND))) {
                iterator.remove();
                continue;
            }
            if (version.containsKey(sprintVersion)) {
                Map<String, String> versionInfo = version.get(sprintVersion);
                item.setOriCloseDate(CommonUtils.getCurrentDate(versionInfo.get(KEY_ORI_CLOSE_DATE)));
                item.setOriPublishDate(CommonUtils.getCurrentDate(versionInfo.get(KEY_ORI_PUBLISH_DATE)));
                item.setCustomer(versionInfo.get(KEY_CUSTOMER));
            }

            String taskNameTag = getTaskNameTag(taskName);
            switch (taskNameTag) {
                case DEFECT_TAG:
                    item.setSortDate(getValue(STR_BLANK, STR_1));
                    break;
                case SELF_TEST_TAG:
                    item.setSortDate(getValue(STR_BLANK, STR_2));
                    break;
                case SELF_BUILD_TAG:
                    item.setSortDate(getValue(STR_BLANK, STR_3));
                    break;
                case COMMIT_TAG:
                case DEV_COMMIT_TAG:
                case UPDATE_TAG:
                default:
                    if (AUDIT_FAIL.equals(item.getStatusName())) {
                        item.setSortDate(getValue(STR_BLANK, STR_4));
                    } else if (INTEGRATION_FAIL.equals(item.getStatusName())) {
                        item.setSortDate(getValue(STR_BLANK, STR_5));
                    } else {
                        item.setSortDate(finishDate);
                    }
                    break;
            }

            String minCompleteByMark = getMinDate(item.getOriCloseDate(), item.getOriPublishDate(), finishDate);
            item.setMinCompleteByMark(minCompleteByMark);

            if (StringUtils.isNotBlank(finishDate) && StringUtils.isNotBlank(item.getOriCloseDate())){
                if (StringUtils.compare(finishDate, item.getOriCloseDate()) > 0) {
                    finishDateError.add(item.getTaskNumber());
                }
                if (StringUtils.compare(todayDate, minCompleteByMark) > 0) {
                    finishDateOver.add(item.getTaskNumber());
                }
            }

            String minCompleteBySort = getMinDate(item.getOriCloseDate(), item.getOriPublishDate(), item.getSortDate());

            if (taskMinCompleteDate.containsKey(taskName)) {
                if (minCompleteBySort.compareTo(taskMinCompleteDate.get(taskName).getMinCompleteBySort()) < 0) {
                    taskMinCompleteDate.get(taskName).setMinCompleteBySort(minCompleteBySort);
                }
            } else {
                HepTaskDto taskMin = new HepTaskDto();
                taskMin.setMinCompleteBySort(minCompleteBySort);
                taskMinCompleteDate.put(taskName, taskMin);
            }

            boolean today = todayMustComplete(item, currentDate, finishDate);
            boolean tomorrow = todayMustComplete(item, tomorrowDate, finishDate);
            boolean thirdDay = todayMustComplete(item, thirdDate, finishDate);
            if (dayVersion.toString().contains(sprintVersion + STR_SPACE) || dayCloseVersion.toString().contains(sprintVersion + STR_SPACE) || today) {
                dayVersionNum++;
                dayTodoTask.add(item.getTaskNumber());
            }
            if (tomorrow) {
                tomorrowTodoTask.add(item.getTaskNumber());
            }
            if (thirdDay) {
                thirdDayTodoTask.add(item.getTaskNumber());
            }
            boolean week = today || (StringUtils.compare(lastDayByWeek, finishDate) >= 0);
            if (weekVersion.toString().contains(sprintVersion + STR_SPACE) || weekCloseVersion.toString().contains(sprintVersion + STR_SPACE) || week) {
                weekVersionNum++;
                weekTodoTask.add(item.getTaskNumber());
            }

            item.setSprintVersion(formatVersion(item.getSprintVersion()));
            if (taskCustomerName.containsKey(taskNumberIn)) {
                item.setCustomer(taskCustomerName.get(taskNumberIn));
            } else {
                if (StringUtils.isBlank(item.getCustomer()) && item.getName().contains(DEFECT_TAG)) {
                    item.setCustomer(NAME_INNER_CUSTOMER);
                } else {
                    waitTaskSync = true;
                }
            }

            String customer = item.getCustomer();
            if (StringUtils.isNotBlank(customer) && customer.length() > 6) {
                item.setCustomer(customer.substring(0, 6));
            }

            if (StringUtils.isBlank(status)) {
                hasBlank = true;
                taskTotal++;
            } else {
                hasBlank = false;
            }
        }

        res = sortTask(res);

        if (tagFlag) {
            initTag(res);
        }

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

        if (waitTaskSync) {
            OutputUtils.info(taskTips, "请同步任务信息");
        } else {
            OutputUtils.info(taskTips, STR_BLANK);
        }

        if (dayVersionNum > 0) {
            dayTodo.setStyle(STYLE_RED_COLOR);
        } else {
            dayTodo.setStyle(STYLE_BLACK_COLOR);
        }
        if (weekVersionNum > 0) {
            weekTodo.setStyle(STYLE_RED_COLOR);
        } else {
            weekTodo.setStyle(STYLE_BLACK_COLOR);
        }

        OutputUtils.clearLog(taskList);
        infoTaskList(taskList, res, dayTodoTask, tomorrowTodoTask, thirdDayTodoTask, weekTodoTask, finishDateError, finishDateOver);
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

    private static boolean todayMustComplete(HepTaskDto item, String currentDate, String finishDate) {
        if( StringUtils.equals(currentDate, finishDate)) {
            return true;
        }
        if (StringUtils.isNotBlank(item.getMinCompleteByMark())) {
            int date = Integer.parseInt((item.getMinCompleteByMark()));
            if (date <= 0 && date > -50) {
                return true;
            }
        }
        return false;
    }

    private void infoTaskList(TableView taskListIn, List<HepTaskDto> res, Set<String> dayTodoTask,
                              Set<String> tomorrowTodoTask, Set<String> thirdDayTodoTask, Set<String> weekTodoTask, Set<String> finishDateError, Set<String> finishDateOver) {
        if (taskListIn == null) {
            return;
        }
        int nextMonday = CommonUtils.getNextWeekDayYmd(DayOfWeek.MONDAY);
        int nextTuesday = CommonUtils.getNextWeekDayYmd(DayOfWeek.TUESDAY);
        int nextWednesday = CommonUtils.getNextWeekDayYmd(DayOfWeek.WEDNESDAY);
        int nextThursday = CommonUtils.getNextWeekDayYmd(DayOfWeek.THURSDAY);
        int nextFriday = CommonUtils.getNextWeekDayYmd(DayOfWeek.FRIDAY);
        int thursday = CommonUtils.getWeekDayYmd(DayOfWeek.THURSDAY);
        int friday = CommonUtils.getWeekDayYmd(DayOfWeek.FRIDAY);
        Platform.runLater(() -> {
            for (HepTaskDto hepTaskDto : res) {
                taskListIn.getItems().add(hepTaskDto);
                // 设置行
                initRowStyle(taskListIn, dayTodoTask, tomorrowTodoTask, thirdDayTodoTask,  weekTodoTask, finishDateError, finishDateOver,
                        nextMonday, nextTuesday, nextWednesday, nextThursday, nextFriday, thursday, friday);
            }
            OutputUtils.setEnabled(taskListIn);
        });
    }

    private void initRowStyle(TableView taskListIn, Set<String> dayTodoTask, Set<String> tomorrowTodoTask,
                              Set<String> thirdDayTodoTask, Set<String> weekTodoTask, Set<String> finishDateError, Set<String> finishDateOver,
                              int nextMonday, int nextTuesday, int nextWednesday, int nextThursday, int nextFriday,
                              int thursday, int friday) {
        taskListIn.setRowFactory(new Callback<TableView<HepTaskDto>, TableRow<HepTaskDto>>() {
            @Override
            public TableRow<HepTaskDto> call(TableView<HepTaskDto> param) {
                final TableRow<HepTaskDto> row = new TableRow<HepTaskDto>() {
                    @Override
                    protected void updateItem(HepTaskDto item, boolean empty) {
                        try {
                            super.updateItem(item, empty);
                            if (item != null && getIndex() > -1) {
                                String taskName = item.getName();
                                String taskNumber = item.getTaskNumber();
                                String[] taskColor;
                                if (taskName.contains(DEFECT_TAG)) {
                                    taskColor = color.get("缺陷");
                                } else if (finishDateError.contains(taskNumber)) {
                                    taskColor = color.get("完成日期超期");
                                } else if (dayTodoTask.contains(taskNumber)) {
                                    taskColor = color.get("今天待提交");
                                } else if (weekTodoTask.contains(taskNumber)) {
                                    taskColor = color.get("本周待提交");
                                }  else {
                                    taskColor = color.get("默认");
                                }
                                setStyle(taskColor[0]);
                                String mark = taskColor[1];
                                if (finishDateOver.contains(taskNumber)) {
                                    mark = "已超期";
                                }
                                if (taskName.contains(COMMIT_TAG)) {
                                    mark = "已提交";
                                }

                                int minComplete = Integer.parseInt(item.getMinCompleteByMark());
                                if ("本周".equals(mark)) {
                                    if (tomorrowTodoTask.contains(taskNumber)) {
                                        mark = "明天";
                                    } else if (thirdDayTodoTask.contains(taskNumber)) {
                                        mark = "后天";
                                    } else if (thursday == minComplete) {
                                        mark = "周四";
                                    } else if (friday == minComplete) {
                                        mark = "周五";
                                    }
                                }
                                if (StringUtils.isBlank(mark)) {
                                    if (nextMonday == minComplete) {
                                        mark = "下周一";
                                    } else if (nextTuesday == minComplete) {
                                        mark = "下周二";
                                    } else if (nextWednesday == minComplete) {
                                        mark = "下周三";
                                    } else if (nextThursday == minComplete) {
                                        mark = "下周四";
                                    } else if (nextFriday == minComplete) {
                                        mark = "下周五";
                                    }
                                }
                                item.setTaskMark(mark);
                            }
                        } catch (Exception e) {
                            LoggerUtils.info(e);
                        }
                    }
                };
                return row;
            }
        });
    }

    private String getMinDate(String closeDate, String publishDate, String endDate) {
        if (StringUtils.isBlank(closeDate)) {
            closeDate = STR_20991231;
        }
        if (StringUtils.isBlank(publishDate)) {
            publishDate = STR_20991231;
        }
        if (StringUtils.isBlank(endDate)) {
            endDate = STR_20991231;
        }
        closeDate = closeDate.replaceAll(STR_HYPHEN, STR_BLANK);
        publishDate = publishDate.replaceAll(STR_HYPHEN, STR_BLANK);
        endDate = endDate.replaceAll(STR_HYPHEN, STR_BLANK);
        return String.valueOf(Math.min(Math.min(Integer.valueOf(closeDate), Integer.valueOf(publishDate)), Integer.valueOf(endDate)));
    }

    private void initTag(List<HepTaskDto> task) {
        if (CollectionUtils.isEmpty(task)) {
            return;
        }
        if (StringUtils.equals(PAGE_USER, EXTEND_USER_FRONT_CODE)) {
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
        Set<String> currentTaskVersion = new HashSet<>();
        while (iterator.hasNext()) {
            HepTaskDto item = iterator.next();
            String taskNumber = item.getTaskNumber();
            String taskName = item.getName();
            String sprintVersion = item.getSprintVersion();
            currentTaskVersion.add(sprintVersion);
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
        }
        initVersion(currentTaskVersion, sprintVersionQ);
    }

    private static String getTaskNameTag(String taskName) {
        int start = 0;
        int end = taskName.length();
        if (taskName.contains(DEFECT_TAG)) {
            return DEFECT_TAG;
        } else if (taskName.contains(STR_BRACKETS_3_LEFT) && taskName.contains(STR_BRACKETS_3_RIGHT)) {
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

    private List<HepTaskDto> sortTask(List<HepTaskDto> task) {
        task = task.stream().peek(item -> {
            String taskName = item.getName();
            String cacheKey = taskName + STR_HYPHEN + item.getSprintVersion();
            if (taskMinCompleteDate.containsKey(taskName)) {
                HepTaskDto hepTaskDto = taskMinCompleteDate.get(taskName);
                if (sortCodeCache.containsKey(cacheKey)) {
                    item.setSortCode(sortCodeCache.get(cacheKey));
                } else {
                    String sortCode = hepTaskDto.getMinCompleteBySort() + item.getCustomer() + taskName + item.getFinishDate() + item.getSprintVersion();
                    item.setSortCode(sortCode);
                    sortCodeCache.put(cacheKey, sortCode);
                }
                item.setMinCompleteBySort(hepTaskDto.getMinCompleteBySort());
            }
        }).collect(Collectors.toList());
        task.sort(new Comparator<HepTaskDto>() {
            @Override
            public int compare(HepTaskDto o1, HepTaskDto o2) {
                return o1.getSortCode().compareTo(o2.getSortCode());
            }
        });
        return task;
    }

    private String getValue(String value, String type) {
        if (StringUtils.isBlank(type)) {
            type = STR_1;
        }
        if (StringUtils.isBlank(value)) {
            if (STR_1.equals(type)) {
                return "1000-00-00";
            } else if (STR_2.equals(type)) {
                return "1010-00-00";
            } else if (STR_3.equals(type)) {
                return "1020-00-00";
            } else if (STR_4.equals(type)) {
                return "1030-00-00";
            } else if (STR_5.equals(type)) {
                return "1040-00-00";
            } else if (STR_6.equals(type)) {
                return "9940-00-00";
            } else if (STR_7.equals(type)) {
                return "9950-00-00";
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
            defaultDividerPositions = taskSplitPane.getDividerPositions()[0];
            defaultTaskNameWidth = ((TableColumn)taskList.getColumns().get(0)).getPrefWidth();
            devCompleteHide.setSelected(true);
            if (isExtendUser()) {
                extendUser.setVisible(false);
                syncTask.setVisible(false);
                syncFileBtn.setVisible(false);
            } else {
                JvmCache.setHepTodoController(this);
                syncFile();
            }
            addTaskMenu(appConfigDto, this);
            initComponentStatus();
            executeQuery(null);
            initColorDesc();
            buildTestData();
            showExtendUserTask();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private void showExtentUserTask(boolean changeTab) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (StringUtils.isBlank(appConfigDto.getHepTaskUserExtend())) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("请设置关联用户信息【hep.task.user.extend】"));
        }
        OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("关联用户任务加载开始 . . ."));
        try {
            extendUser.setDisable(true);
            showExtendTask(changeTab);
        } finally {
            extendUser.setDisable(false);
        }
        OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("关联用户任务加载完成 . . ."));
    }

    private void showExtendUserTask() throws Exception {
        if (!isExtendUser()) {
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        try {
                            showExtentUserTask(false);
                            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                            Tab openTab = CommonUtils.getOpenTab(AppCache.FUNCTION_TAB_CACHE, TASK_TODO.getCode(), TASK_TODO.getName());
                            appConfigDto.setActivateFunction(openTab.getText());
                            AppCache.FUNCTION_TAB_CACHE.getSelectionModel().select(openTab);
                        } catch (Exception e) {
                            LoggerUtils.info(e);
                        }
                    });
                }
            };
            timer.schedule(timerTask, 10);
        }
    }

    private void setSideBar() {
        Platform.runLater(() -> {
            double width = defaultTaskNameWidth;
            double positions = defaultDividerPositions;
            if (StringUtils.equals(sideBarBtn.getText(), "隐藏侧边栏")) {
                positions = 1;
                sideBarBtn.setText("显示侧边栏");
            } else {
                width = 505;
                sideBarBtn.setText("隐藏侧边栏");
            }
            ((TableColumn)taskList.getColumns().get(0)).setPrefWidth(width);
            taskSplitPane.setDividerPositions(positions);
        });
    }

    private void setFrontTips(boolean visible) {
        frontTipsSideBar.setVisible(visible);
        frontTips.setVisible(visible);
    }

    private void initComponentStatus() {
        memoryTips.setVisible(false);
        scrollTips.setVisible(false);
        setFrontTips(false);
        setSideBar();
    }

    private void syncFile() throws Exception {
        TaskUtils.execute(new HepTodoTask(new HepTodoTaskParam(this, "doSyncFile")));
    }

    public void doSyncFile() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (isExtendUser()) {
            return;
        }
        Map<String,String> syncFileVersion = appConfigDto.getFileSyncVersionMap();
        if (MapUtils.isEmpty(syncFileVersion)) {
            return;
        }
        Timer timer = appConfigDto.getTimerMap().get(KEY_FILE_SYNC_TIMER);
        String timerId = CommonUtils.getCurrentDateTime2();
        if (timer == null) {
            timer = new Timer();
            appConfigDto.getTimerMap().put(KEY_FILE_SYNC_TIMER, timer);
        } else {
            timer.cancel();
            return;
        }
        String fileSyncAuthVersion = appConfigDto.getFileSyncAuthVersion();
        if (StringUtils.isBlank(fileSyncAuthVersion)) {
            OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr("未配置授权同步版本信息"));
            OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr("请检查参数【file.sync.auth.version】"));
            return;
        }
        Platform.runLater(() -> {
            syncFileBtn.setText("停止文件同步");
        });
        List<String> authVersion = Arrays.asList(fileSyncAuthVersion.toLowerCase().split(STR_COMMA));
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                OutputUtils.clearLog(noticeSync);
                String threadMsg = "轮询线程: " + timerId;
                OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContain(threadMsg));
                for (Map.Entry<String, String> version : syncFileVersion.entrySet()) {
                    String ver = version.getKey();
                    if (!authVersion.contains(ver)) {
                        OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr(ver.toUpperCase() + " 未授权同步"));
                        continue;
                    }
                    ver = ver.toUpperCase();
                    String[] path = version.getValue().split(STR_COMMA);
                    if (path.length != 2) {
                        OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr(ver + " 扫描路径配置错误"));
                        continue;
                    }
                    String fileSyncSource = path[0];
                    String fileSyncTarget = path[1];
                    String versionMsg = "轮询版本: " + ver.replace(STR_VERSION_PREFIX, STR_BLANK);
                    OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr(versionMsg));
                    fileSyncSourceFile.clear();
                    try {
                        sync(new File(fileSyncSource), fileSyncSource, fileSyncTarget, ver);
                    } catch (IOException e) {
                        LoggerUtils.info(e);
                        OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
                    }
                    clearFile(new File(fileSyncTarget), ver);
                }
                checkCommitNotPush(appConfigDto, "轮询时间: " +  CommonUtils.getCurrentDateTime14());
                outputMemory();
            }
        };
        timer.schedule(timerTask, 1000, appConfigDto.getFileSyncTimer() * 1000);
    }

    private void checkCommitNotPush(AppConfigDto appConfigDto, String threadMsg) {
        Map<String, String> svnRep = appConfigDto.getSvnUrl();
        String trunk = svnRep.get(KEY_TRUNK);
        boolean push = false;
        if (StringUtils.isNotBlank(trunk)) {
            File[] file = new File(trunk).listFiles();
            if (file != null) {
                for (File item : file) {
                    if (FileUtils.isSuffixDirectory(item, BaseConst.FILE_TYPE_GIT, false, true)) {
                        String content = CmdUtils.exe(item.getAbsolutePath(), "git status");
                        if (StringUtils.isNotBlank(content) && content.toLowerCase().contains("your branch is ahead of")) {
                            threadMsg = item.getName() + " 已commit未push";
                            push = true;
                            break;
                        }
                    }
                }
            }
        }
        if (push) {
            scrollTips.setStyle("-fx-font-weight: bold; -fx-text-background-color: red;");
            scrollTips.setVisible(true);
        } else {
            scrollTips.setStyle("-fx-font-weight: normal;");
            scrollTips.setVisible(colorList.get(0).isVisible());
        }
        OutputUtils.info(scrollTips, threadMsg);
    }

    private void outputMemory() {
        String[] memoryInfo = CommonUtils.getMemoryInfo();
        OutputUtils.info(memoryTips, "内存使用: " + memoryInfo[0]);
        if (Integer.valueOf(memoryInfo[1]) > 1024 || Integer.valueOf(memoryInfo[2]) > 1024) {
            memoryTips.setStyle("-fx-font-weight: bold; -fx-text-background-color: red;");
            memoryTips.setVisible(true);
        } else {
            memoryTips.setStyle("-fx-font-weight: normal;");
            memoryTips.setVisible(colorList.get(0).isVisible());
        }
    }

    private void sync(File sourceFile, String sourcePath, String targetPath, String version) throws IOException {
        if (sourceFile.isDirectory()) {
            File[] files = sourceFile.listFiles();
            for (File item : files) {
                sync(item, sourcePath, targetPath, version);
                CommonUtils.cleanFile(sourceFile, item);
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
                } else {
                    OutputUtils.info(fileTipsFileStatus, "开始");
                    OutputUtils.info(fileTipsFileTime, CommonUtils.getCurrentDateTime14());
                    FileUtils.writeFile(target, content, ENCODING_UTF8, false);
                    OutputUtils.info(fileTipsFileStatus, "结束");
                    OutputUtils.info(fileTipsFileTime, CommonUtils.getCurrentDateTime14());
                }
                CommonUtils.cleanFile(content);
            }
            CommonUtils.cleanFile(sourceFile, targetFile);
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
                CommonUtils.cleanFile(sourceFile, item);
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
            CommonUtils.cleanFile(sourceFile);
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

    private void showExtendTask(boolean changeTab) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String userExtend = appConfigDto.getHepTaskUserExtend();
        if (StringUtils.isNotBlank(userExtend)) {
            userExtend = userExtend + STR_COMMA + EXTEND_USER_FRONT_CODE;
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
            if (changeTab) {
                appConfigDto.setActivateFunction(defaultTab.getText());
                AppCache.FUNCTION_TAB_CACHE.getSelectionModel().select(defaultTab);
            }
        }
    }

    private void initColorDesc() {
        double step = 15;
        double x = 20;
        double y = 180;
        Label label = new Label("颜色说明:");
        label.setOnMouseClicked(event -> {
            if (CollectionUtils.isEmpty(colorList)) {
                return;
            }
            boolean visible = !colorList.get(0).isVisible();
            for (Label ele : colorList) {
                ele.setVisible(visible);
            }
            memoryTips.setVisible(visible);
            scrollTips.setVisible(visible);
        });
        String boldStyle = "-fx-font-weight: normal;";
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
            ele.setStyle(boldStyle + color.get(key)[0]);
            ele.setLayoutX(x);
            ele.setLayoutY(y);
            colorList.add(ele);
            todoTitle.getChildren().add(ele);
            prevLen = len;
            x -= step * diff;
            x += 20;
        }
    }

    public static List<VersionDto> getVersionInfo() throws Exception {
        List<String> versionList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION_STAT), false);
        List<VersionDto> versionDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(versionList)) {
            for (String item : versionList) {
                String[] elements = item.split(STR_SEMICOLON);
                VersionDto versionDto = new VersionDto();
                String oriCloseDate = elements[1];
                String oriPublishDate = elements[2];
                String versionCode = elements[0];
                versionDto.setCode(versionCode);
                versionDto.setCloseDate(oriCloseDate);
                versionDto.setPublishDate(oriPublishDate);
                versionDto.setClientName(elements[3]);
                versionDtoList.add(versionDto);
            }
        }
        return versionDtoList;
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

    public Map<String,String> getTaskLevelInfo() {
        Map<String, String> task = new HashMap<>();
        try {
            List<String> taskList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_LEVEL_EXTEND_STAT), false);
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (String item : taskList) {
                    String[] elementList = item.split(STR_SEMICOLON);
                    String level = elementList[1];
                    switch (level) {
                        case STR_0:
                            level = "简单";
                            break;
                        case STR_1:
                            level = "一般";
                            break;
                        case STR_2:
                            level = "复杂";
                            break;
                        default:
                            level = "待明确";
                            break;
                    }
                    task.put(elementList[0], level);
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

    public List<String> getTaskVersionInfo() {
        List<String> versionList = new ArrayList<>();
        try {
            versionList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION_STAT), false);;
        } catch (IOException e) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
            LoggerUtils.info(e);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
        return versionList;
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
