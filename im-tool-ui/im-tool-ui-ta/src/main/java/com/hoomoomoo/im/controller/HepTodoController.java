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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.util.*;
import java.util.List;
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
    public final static String TASK_NAME_SCENE = "现场问题处理";

    private static Set<String> fileSyncSourceFile = new HashSet<>();

    Map<String, HepTaskDto> taskMinCompleteDate = new HashMap<>();
    Map<String, String> sortCodeCache = new HashMap<>();

    List<String> taskLevelDict = new ArrayList<>();

    List<Label> colorList = new ArrayList<>();

    private boolean dealTask = true;

    private String PAGE_USER = "";
    private final static String EXTEND_USER_FRONT_CODE = "front";
    private final static Map<String, String> extendUserInfoCodeToName = new LinkedHashMap<>();
    private final static Map<String, String> extendUserInfoNameToCode = new LinkedHashMap();
    private final static Map<String, String> dayCompleteTipsInfo = new HashMap();


    private final static String STYLE_RED_COLOR = "-fx-text-background-color: red; -fx-font-weight: bold;";
    private final static String STYLE_BLACK_COLOR = "-fx-text-background-color: #000000; -fx-font-weight: bold;";

    private Map<String, String[]> color = new LinkedHashMap<String, String[]>(){{
        put("完成日期超期", new String[] {"-fx-text-background-color: #ff0090;"});
        put("今天待提交", new String[] {"-fx-text-background-color: #ff0000;"});
        put("本周待提交", new String[] {"-fx-text-background-color: #0015ff;"});
        put("重点关注", new String[] {"-fx-text-background-color: #ff0073;"});
        put("默认", new String[] {"-fx-text-background-color: #000000;"});
    }};

    Map<String, String> FRONT_QUERY_DEMAND_CACHE = new HashMap<>(2);

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
    private List<String> syncTaskLog = new ArrayList<>();
    private Map<String, Map<String, Integer>> focusVersion = new HashMap<>();

    @FXML
    private AnchorPane hep;

    @FXML
    private AnchorPane todoTitle;

    @FXML
    private AnchorPane sideBar;

    @FXML
    private Label weekPublish;

    @FXML
    private Label weekClose;

    @FXML
    private Label dayPublish;

    @FXML
    private Label dayClose;

    @FXML
    private Label focusVersionTips;

    @FXML
    private TextField taskNumberQuery;

    @FXML
    private TextField nameQuery;

    @FXML
    private ComboBox sprintVersionQuery;

    @FXML
    private ComboBox taskLevelQuery;

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
    public Button showDemand;

    @FXML
    public Button syncTask;

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
    private Label memoryTips;

    @FXML
    private Label filePushTips;

    @FXML
    private Label syncFileTime;

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
    private Label fileTipsVersionTitle;

    @FXML
    private Label fileTipsFileTitle;

    @FXML
    private Label fileTipsFileOperateTitle;

    @FXML
    private Label fileTipsFileStatusTitle;

    @FXML
    private Label fileTipsFileTimeTitle;

    @FXML
    private Label syncFrontVersionTips;

    @FXML
    private Button  all;

    @FXML
    private Button  only;

    @FXML
    private Button  devCompleteHide;

    @FXML
    private Button  devCompleteShow;

    private Pane mask;

    @FXML
    private SplitPane taskSplitPane;

    @FXML
    private Button sideBarBtn;

    @FXML
    private TextField demandNo;

    @FXML
    private TextField taskNo;

    private double defaultDividerPositions;

    private double defaultTaskNameWidth;

    private String queryType = STR_BLANK;

    Set<Button> queryButtonSet = new HashSet<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, TASK_TODO.getName()));
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            initUserInfo(appConfigDto);
            initExtendUser(appConfigDto);
            queryButtonSet.add(all);
            queryButtonSet.add(only);
            queryButtonSet.add(devCompleteShow);
            queryButtonSet.add(devCompleteHide);
            queryButtonSet.add(query);
            defaultDividerPositions = taskSplitPane.getDividerPositions()[0];
            defaultTaskNameWidth = ((TableColumn)taskList.getColumns().get(0)).getPrefWidth();
            queryType = devCompleteHide.getText();
            controlQueryButtonColor(devCompleteHide);
            controlComponent();
            if (isExtendUser()) {
                controlComponentByExtendUser(false, false, false);
            } else {
                initColorDesc();
                JvmCache.setHepTodoController(this);
                syncFile();
            }
            JvmCache.setHepTodoControllerMap(appConfigDto.getActivateFunction(), this);
            addTaskMenu(appConfigDto, this);
            initComponentStatus();
            executeQuery(null);
            buildTestData();
            //showExtendUserTask();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }


    @FXML
    void syncOrSuspend(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        Timer timer = appConfigDto.getTimerMap().get(KEY_FILE_SYNC_TIMER);
        if (timer != null) {
            CommonUtils.stopHepToDoSyncFile(appConfigDto);
            syncFileBtn.setText("启动文件同步");
            OutputUtils.info(filePushTips, STR_BLANK);
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
        queryType = all.getText();
        controlQueryButtonColor(all);
        executeQuery(event);
    }

    @FXML
    void selectOnly(ActionEvent event) throws Exception {
        queryType = only.getText();
        controlQueryButtonColor(only);
        executeQuery(event);
    }

    @FXML
    void selectDevCompleteHide(ActionEvent event) throws Exception {
        queryType = devCompleteHide.getText();
        controlQueryButtonColor(devCompleteHide);
        executeQuery(event);
    }

    @FXML
    void selectDevCompleteShow(ActionEvent event) throws Exception {
        queryType = devCompleteShow.getText();
        controlQueryButtonColor(devCompleteShow);
        executeQuery(event);
    }

    @FXML
    void showTaskInfo(MouseEvent event) throws Exception {
        HepTaskDto item = (HepTaskDto) taskList.getSelectionModel().getSelectedItem();
        item.setOperateType(STR_BLANK);

        OutputUtils.repeatInfo(demandNo, item.getDemandNo());
        OutputUtils.repeatInfo(taskNo, item.getTaskNumber());

        String clickType = event.getButton().toString();
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        appConfigDto.setHepTaskDto(item);
        if (!isExtendUser()) {
            String ver = item.getSprintVersion();
            String verYear = ver.split("\\.")[0];
            ver = TaCommonUtils.changeVersion(appConfigDto, ver) + STR_COMMA;
            String authVer = appConfigDto.getFileSyncAuthVersion().replaceAll(STR_VERSION_PREFIX, STR_BLANK) + STR_COMMA;
            if (authVer.contains(ver) || verYear.compareTo(KEY_GIT_VERSION_YEAR) >= 0 || verYear.compareTo(KEY_VERSION_202202) == 0) {
                setSyncFrontVersionTips(false);
            } else {
                setSyncFrontVersionTips(true);
            }
        }

        if (LEFT_CLICKED.equals(clickType) && event.getClickCount() == SECOND_CLICKED) {
            if (isExtendUser()) {
                OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr("关联用户不支持此操作"));
                CommonUtils.showTipsByError("关联用户不支持此操作", 3 * 1000);
                return;
            }
            operateTask(appConfigDto, item);
        }
    }

    void operateTask(AppConfigDto appConfigDto, HepTaskDto item) throws Exception {
        if (TaCommonUtils.restPlan()) {
            CommonUtils.showTipsByRest();
            return;
        }
        if (StringUtils.equals(appConfigDto.getHepTaskSameOne(), STR_TRUE) && StringUtils.equals(item.getAssigneeId(), item.getReviewerId())) {
            String msg = String.format("开发人员和审核人员为同一人,请检查【%s】", item.getAssigneeId());
            LoggerUtils.info(msg);
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
            CommonUtils.showTipsByError(msg, 10 * 1000);
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
            CommonUtils.showTipsByError("不支持的操作类型");
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
            LoggerUtils.info(e);
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
        } catch (Exception e) {
            String msg = e.getMessage();
            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
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
    void executeReset(ActionEvent event) throws Exception {
        OutputUtils.clearLog(taskNumberQuery);
        OutputUtils.clearLog(nameQuery);
        OutputUtils.clearLog(sprintVersionQuery);
        OutputUtils.clearLog(taskLevelQuery);
        executeQuery(null);
    }

    @FXML
    void showDemandInfo (ActionEvent event) throws Exception {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(FRONT_QUERY_DEMAND_CACHE.get(NAME_TASK_NOT_COMPLETE)), null);
        CommonUtils.showTipsByInfo("需求单号复制完成");
    }

    @FXML
    void syncTaskInfo (ActionEvent event) throws Exception {
        syncTask.setDisable(true);
        try {
            TaskUtils.execute(new HepTodoTask(new HepTodoTaskParam(this, "syncTaskInfo")));
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
            focusVersion.clear();
            syncTaskLog.clear();
            taskLevelDict.clear();
            if (frontPage()) {
                ConfigCache.getAppConfigDtoCache().setQueryUpdateTaskFileByCondition(false);
            }
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
        if (frontPage()) {
            dealTask = false;
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String userExtend = appConfigDto.getHepTaskUserExtend();
            JSONArray task = new JSONArray();
            if (StringUtils.isNotBlank(userExtend)) {
                for (String item : extendUserInfoCodeToName.keySet()) {
                    if (EXTEND_USER_FRONT_CODE.equals(item)) {
                        continue;
                    }
                    CURRENT_USER_ID = item;
                    String userInfo = String.format("查询用户: %s", CURRENT_USER_ID);
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
            CommonUtils.showTipsByError("不支持的操作类型");
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
                if (response != null) {
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
        dayCompleteTipsInfo.clear();
        // 格式 yyyyMMdd
        String thursday = CommonUtils.getWeekDayYmd(DayOfWeek.THURSDAY);
        String today = CommonUtils.getCurrentDateTime3();
        String saturday = CommonUtils.getWeekDayYmd(DayOfWeek.SATURDAY);
        String sunday = CommonUtils.getWeekDayYmd(DayOfWeek.SUNDAY);
        dayCompleteTipsInfo.put(thursday, "周四");
        dayCompleteTipsInfo.put(CommonUtils.getWeekDayYmd(DayOfWeek.FRIDAY), "周五");
        dayCompleteTipsInfo.put(saturday, "周六");
        dayCompleteTipsInfo.put(sunday, "周天");
        dayCompleteTipsInfo.put(today, "今天");
        dayCompleteTipsInfo.put(CommonUtils.getTomorrowDateTime(), "明天");
        dayCompleteTipsInfo.put(CommonUtils.getCustomDateTime(2), "后天");
        String nextMonday = CommonUtils.getNextWeekDayYmd(DayOfWeek.MONDAY);
        String nextTuesday = CommonUtils.getNextWeekDayYmd(DayOfWeek.TUESDAY);
        String nextWednesday = CommonUtils.getNextWeekDayYmd(DayOfWeek.WEDNESDAY);
        String nextThursday = CommonUtils.getNextWeekDayYmd(DayOfWeek.THURSDAY);
        String nextFriday = CommonUtils.getNextWeekDayYmd(DayOfWeek.FRIDAY);
        String nextSaturday = CommonUtils.getNextWeekDayYmd(DayOfWeek.SATURDAY);
        String nextSunday = CommonUtils.getNextWeekDayYmd(DayOfWeek.SUNDAY);
        dayCompleteTipsInfo.put(nextMonday, "下周一");
        dayCompleteTipsInfo.put(nextTuesday, "下周二");
        dayCompleteTipsInfo.put(nextWednesday, "下周三");
        dayCompleteTipsInfo.put(nextThursday, "下周四");
        dayCompleteTipsInfo.put(nextFriday, "下周五");
        dayCompleteTipsInfo.put(nextSaturday, "下周六");
        dayCompleteTipsInfo.put(nextSunday, "下周天");
        dayCompleteTipsInfo.put(CommonUtils.getCustomDateTime(nextMonday, 7), "两周一");
        dayCompleteTipsInfo.put(CommonUtils.getCustomDateTime(nextTuesday, 7), "两周二");
        dayCompleteTipsInfo.put(CommonUtils.getCustomDateTime(nextWednesday, 7), "两周三");
        dayCompleteTipsInfo.put(CommonUtils.getCustomDateTime(nextThursday, 7), "两周四");
        dayCompleteTipsInfo.put(CommonUtils.getCustomDateTime(nextFriday, 7), "两周五");
        dayCompleteTipsInfo.put(CommonUtils.getCustomDateTime(nextSaturday, 7), "两周六");
        dayCompleteTipsInfo.put(CommonUtils.getCustomDateTime(nextSunday, 7), "两周天");

        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();

        Set<String> dayTodoTask = new HashSet<>();
        Set<String> weekTodoTask = new HashSet<>();
        Set<String> finishDateError = new HashSet<>();
        Set<String> finishDateOver = new HashSet<>();
        Set<String> focusVersionTask = new HashSet<>();
        List<String> focusDemand = new ArrayList<>();
        if (StringUtils.isNotBlank(appConfigDto.getHepTaskFocusDemand())) {
            focusDemand = Arrays.asList(appConfigDto.getHepTaskFocusDemand().split(STR_COMMA));
        }
        taskList.setDisable(true);
        List<String> dayPublishVersion = appConfigDto.getDayPublishVersion();
        List<HepTaskDto> res = JSONArray.parseArray(JSONObject.toJSONString(task), HepTaskDto.class);
        initVersionTask(res);
        boolean hasBlank = false;
        int taskTotal = 0;
        Map<String, Map<String, String>> version = new HashMap<>();
        StringBuilder weekVersion = new StringBuilder();
        StringBuilder weekCloseVersion = new StringBuilder();
        StringBuilder dayVersion = new StringBuilder();
        StringBuilder dayCloseVersion = new StringBuilder();
        // 格式 yyyy-MM-dd
        String todayDate = CommonUtils.getCurrentDateTime4();
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
        List<String> cancelOnlySelf = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_LEVEL_ONLY_SELF_STAT));
        List<String> cancelErrorVersion = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_LEVEL_ERROR_VERSION_STAT));
        String taskLevelQ = CommonUtils.getComponentValue(taskLevelQuery);
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
            if (todayDate.equals(oriCloseDate)) {
                dayCloseVersion.append(versionCode).append(STR_SPACE_2);
            }
            if (todayDate.equals(oriPublishDate)) {
                dayVersion.append(versionCode).append(STR_SPACE_2);
            }

            if (todayDate.equals(oriCloseDate) || todayDate.equals(oriPublishDate)) {
                dayPublishVersion.add(versionCode);
            }

            if (weekDay.compareTo(oriCloseDate) >= 0 && todayDate.compareTo(oriCloseDate) <= 0) {
                weekCloseVersion.append(versionCode).append(STR_SPACE_2);
            }

            if (weekDay.compareTo(oriPublishDate) >= 0 && todayDate.compareTo(oriPublishDate) <= 0) {
                weekVersion.append(versionCode).append(STR_SPACE_2);
            }
            version.put(versionCode, ele);
        }
        int dayVersionNum = 0;
        int weekVersionNum = 0;
        int mergerNum = 0;
        Set<String> taskNoList = new LinkedHashSet<>();
        Set<String> demandNoList = new LinkedHashSet<>();
        Iterator<HepTaskDto> iterator = res.listIterator();
        Set<String> existTask = new HashSet<>();
        Set<String> sameAssigneeIdReviewerId = new HashSet<>();
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
            taskNoList.add(taskNumberIn);
            demandNoList.add(demandNo);
            if (StringUtils.equals(appConfigDto.getHepTaskSameOne(), STR_TRUE) && StringUtils.equals(item.getAssigneeId(), item.getReviewerId())) {
                sameAssigneeIdReviewerId.add(taskNumberIn);
            }
            boolean commitTag = false;
            if (!taskName.contains(COMMIT_TAG) && taskSubmitStatus.containsKey(taskNumberIn)) {
                commitTag = true;
                if (!taskName.contains(DEV_COMMIT_TAG)) {
                    taskName = DEV_COMMIT_TAG + taskName;
                }
            }
            if (!taskName.contains(DEV_COMMIT_TAG) && (taskDemandStatus.containsKey(demandNo) || taskDemandStatus.containsKey(taskNumberIn)) && !taskCancelDevSubmit.containsKey(taskNumberIn)) {
                taskName = DEV_COMMIT_TAG + taskName;
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

            String finishDate = item.getEstimateFinishTime().split(STR_SPACE)[0];
            String finishTime = item.getEstimateFinishTime().split(STR_SPACE)[1];
            item.setFinishDate(finishDate);
            item.setFinishTime(finishTime);

            String sprintVersion = item.getSprintVersion();

            if (appConfigDto.getHepTaskFocusVersionMap().contains(sprintVersion)) {
                focusVersionTask.add(taskNumberIn);
                String assigneeName = item.getAssigneeName();
                if (focusVersion.containsKey(sprintVersion)) {
                    Map<String, Integer> userStat = focusVersion.get(sprintVersion);
                    if (userStat.containsKey(assigneeName)) {
                        userStat.put(assigneeName, userStat.get(assigneeName) + 1);
                    } else {
                        userStat.put(assigneeName, 1);
                    }
                } else {
                    Map<String, Integer> userStat = new HashMap<>();
                    userStat.put(assigneeName, 1);
                    focusVersion.put(sprintVersion, userStat);
                }
            }

            boolean todayComplete = todayDate.compareTo(finishDate) >= 0;
            boolean needShow = todayComplete || focusVersionTask.contains(taskNumberIn);

            if (StringUtils.equals(queryType, only.getText())) {
                if (existTask.contains(taskName)) {
                    iterator.remove();
                    continue;
                }
                existTask.add(taskName);
            } else if (StringUtils.equals(queryType, devCompleteHide.getText()) && !needShow) {
                if (taskName.contains(DEV_COMMIT_TAG)) {
                    iterator.remove();
                    continue;
                }
            } else if (StringUtils.equals(queryType, devCompleteShow.getText())) {
                if (!taskName.contains(DEV_COMMIT_TAG)) {
                    iterator.remove();
                    continue;
                }
            }

            if (sprintVersion.startsWith(KEY_TA5) || (sprintVersion.contains(KEY_TA6) && !sprintVersion.contains(KEY_FUND) && !sprintVersion.contains(KEY_VERSION_YEAR_2022))) {
                iterator.remove();
                continue;
            }
            if (version.containsKey(sprintVersion)) {
                Map<String, String> versionInfo = version.get(sprintVersion);
                item.setOriCloseDate(CommonUtils.getCurrentDate(versionInfo.get(KEY_ORI_CLOSE_DATE)));
                item.setOriPublishDate(CommonUtils.getCurrentDate(versionInfo.get(KEY_ORI_PUBLISH_DATE)));
            }

            String taskNameTag = getTaskNameTag(taskName);
            switch (taskNameTag) {
                case DEFECT_TAG:
                    item.setSortDate(getValue(STR_1));
                    break;
                case SELF_TEST_TAG:
                    item.setSortDate(getValue(STR_2));
                    break;
                case SELF_BUILD_TAG:
                    item.setSortDate(getValue(STR_3));
                    break;
                case COMMIT_TAG:
                case DEV_COMMIT_TAG:
                case UPDATE_TAG:
                default:
                    if (AUDIT_FAIL.equals(item.getStatusName())) {
                        item.setSortDate(getValue(STR_4));
                    } else if (INTEGRATION_FAIL.equals(item.getStatusName())) {
                        item.setSortDate(getValue(STR_5));
                    } else if (taskName.contains(TASK_NAME_SCENE)) {
                        item.setSortDate(getValue(STR_6));
                    } else {
                        if (focusDemand.contains(demandNo)) {
                            item.setSortDate(getValue(STR_7).substring(0, 4) + finishDate.substring(4));
                        } else {
                            item.setSortDate(finishDate);
                        }
                    }
                    break;
            }

            String minCompleteByMarkYmd = getMinDate(item.getOriCloseDate(), item.getOriPublishDate(), finishDate);

            if (StringUtils.isNotBlank(finishDate) && StringUtils.isNotBlank(item.getOriCloseDate())){
                if (StringUtils.compare(finishDate, item.getOriCloseDate()) > 0) {
                    finishDateError.add(item.getTaskNumber());
                    minCompleteByMarkYmd = CommonUtils.getCurrentDateYmd(finishDate);
                }
                if (StringUtils.compare(todayDate, finishDate) > 0) {
                    finishDateOver.add(item.getTaskNumber());
                    minCompleteByMarkYmd = CommonUtils.getCurrentDateYmd(finishDate);
                }
            }

            item.setMinCompleteByMark(minCompleteByMarkYmd);

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

            if (dayCompleteTipsInfo.containsKey(minCompleteByMarkYmd)) {
                item.setTaskMark(dayCompleteTipsInfo.get(minCompleteByMarkYmd));
            }
            if (StringUtils.isBlank(item.getTaskMark()) && weekTodoTask.contains(taskNumberIn)) {
                item.setTaskMark("本周");
            }
            if (commitTag) {
                setTaskLevel(item, "已提交");
            }
            if (finishDateError.contains(taskNumberIn)) {
                setTaskLevel(item, "超期");
            } else if (finishDateOver.contains(taskNumberIn)) {
                setTaskLevel(item, "已超期");
            }
            if (taskName.contains(DEFECT_TAG)) {
                setTaskLevel(item, "缺陷");
            }

            item.setSprintVersionFull(sprintVersion);
            item.setSprintVersion(formatVersion(sprintVersion));

            if (taskCustomerName.containsKey(taskNumberIn)) {
                String name = taskCustomerName.get(taskNumberIn);
                item.setCustomerFull(StringUtils.isBlank(name) ? NAME_INNER_CUSTOMER : name);
            } else {
                if (StringUtils.isBlank(item.getCustomerFull()) && taskName.contains(DEFECT_TAG)) {
                    item.setCustomerFull(NAME_INNER_CUSTOMER);
                } else {
                    waitTaskSync = true;
                }
            }
            item.setCustomer(item.getCustomerFull());

            String customer = item.getCustomer();
            if (StringUtils.isNotBlank(customer)) {
                if (customer.contains(STR_COMMA)) {
                    customer = customer.split(STR_COMMA)[0];
                    item.setCustomer(customer);
                }
                if (customer.length() > 4) {
                    customer = customer.substring(0, 4);
                    if (StringUtils.equals("中信中证", customer)) {
                        customer = "中信托管";
                    }
                    item.setCustomer(customer);
                }
            }

            controlHepTaskOnlySelfTips(appConfigDto, item, cancelOnlySelf);

            controlHepTaskAppointVersionTips(appConfigDto, item, cancelErrorVersion);

            if (filterTaskForRemove(appConfigDto, item, taskLevelQ)) {
                iterator.remove();
                continue;
            }

            if (todayComplete) {
                dayVersionNum++;
                dayTodoTask.add(item.getTaskNumber());
            }
            boolean week = todayComplete || (StringUtils.compare(lastDayByWeek, finishDate) >= 0);
            if (week) {
                weekVersionNum++;
                weekTodoTask.add(item.getTaskNumber());
            }

            if (StringUtils.equals(appConfigDto.getHepTaskUser(), item.getCreatorId())) {
                item.setCreatorName(STR_SPACE);
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

        initTaskLevelDict(taskLevelQ);
        controlFocusVersionTips(appConfigDto);

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

        if (!isExtendUser()) {
            if (waitTaskSync) {
                syncTask.setStyle(STYLE_BOLD_RED_FOR_BUTTON);
            } else {
                syncTask.setStyle(STYLE_NORMAL_FOR_BUTTON);
            }
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
        finishDateError.addAll(finishDateOver);
        infoTaskList(taskList, res, dayTodoTask, weekTodoTask, finishDateError, focusVersionTask, focusDemand);
        taskList.setDisable(false);
        String msg = STR_BLANK;
        boolean show = false;
        if (CollectionUtils.isNotEmpty(sameAssigneeIdReviewerId)) {
            show = true;
            msg = String.format("开发人员和审核人员为同一人,请检查【%s】", sameAssigneeIdReviewerId.stream().collect(Collectors.joining(STR_COMMA)));
            LoggerUtils.info(msg);
        }
        printTaskInfo(res);
        controlTooltip(appConfigDto, show, msg.replace("【", STR_NEXT_LINE_2).replace("】", STR_BLANK), getTipsLocation(sameAssigneeIdReviewerId.size()), 175);
        controlCheckScriptTips(StringUtils.equalsAny(today, saturday, sunday));
        updateHepStatFile(appConfigDto, taskNoList, demandNoList);
    }

    private void controlHepTaskOnlySelfTips (AppConfigDto appConfigDto, HepTaskDto hepTaskDto, List<String> cancelOnlySelf) {
        if (CollectionUtils.isNotEmpty(cancelOnlySelf) && cancelOnlySelf.contains(hepTaskDto.getTaskNumber())) {
            return;
        }
        String customerFull = hepTaskDto.getCustomerFull();
        Map<String, String> hepTaskOnlySelfMap = appConfigDto.getHepTaskOnlySelfMap();
        if (hepTaskOnlySelfMap.containsKey(customerFull)) {
            if (!hepTaskDto.getSprintVersionFull().startsWith(hepTaskOnlySelfMap.get(customerFull))) {
                setTaskLevel(hepTaskDto, "孤版");
            }
        }
    }

    private void controlFocusVersionTips(AppConfigDto appConfigDto) {
        if (frontPage()) {
            int total = 0;
            int totalStat = 0;
            StringBuilder message = new StringBuilder();
            StringBuilder versionMsg = new StringBuilder();
            List<String> user = new ArrayList<>(extendUserInfoCodeToName.values());
            for (Map.Entry<String, Map<String, Integer>> entry : focusVersion.entrySet()) {
                total = 0;
                versionMsg.setLength(0);
                String version = entry.getKey();
                Map<String, Integer> stat = entry.getValue();
                for (int i=0; i<user.size() - 1; i++) {
                    String item = user.get(i);
                    int num = stat.get(item) == null ? 0 : stat.get(item);
                    total += num;
                    versionMsg.append(String.format(" %s(%s)", item, num));
                }
                totalStat += total;
                message.append(String.format("重点关注版本【%s】任务统计(%s) --> ", version, total));
                message.append(versionMsg);
                message.append(STR_NEXT_LINE);
            }
            OutputUtils.repeatInfo(focusVersionTips, message.toString());
            if (totalStat > 0) {
                if (focusVersion.size() > 1) {
                    focusVersionTips.setLayoutY(140);
                } else {
                    focusVersionTips.setLayoutY(180);
                }
                focusVersionTips.setVisible(true);
                controlColorDesc(false);
            } else {
                focusVersionTips.setVisible(false);
                controlColorDesc(true);
            }
        } else {
            String userName = extendUserInfoCodeToName.get(CURRENT_USER_ID);
            if (StringUtils.isBlank(userName)) {
                return;
            }
            int num = 0;
            StringBuilder message = new StringBuilder();
            for (Map.Entry<String, Map<String, Integer>> entry : focusVersion.entrySet()) {
                String version = entry.getKey();
                Map<String, Integer> stat = entry.getValue();
                if (stat.containsKey(userName)) {
                    int numVer = stat.get(userName);
                    if (numVer > 0) {
                        num = numVer;
                        message.append(String.format("重点关注版本【%s】任务统计(%s)", version, num));
                        break;
                    }
                }
            }
            OutputUtils.repeatInfo(focusVersionTips, message.toString());
            if (num > 0) {
                focusVersionTips.setVisible(true);
                controlColorDesc(false);
            } else {
                focusVersionTips.setVisible(false);
                controlColorDesc(true);
            }
        }
    }

    private void controlHepTaskAppointVersionTips (AppConfigDto appConfigDto, HepTaskDto hepTaskDto, List<String> cancelErrorVersion) {
        if (CollectionUtils.isNotEmpty(cancelErrorVersion) && cancelErrorVersion.contains(hepTaskDto.getTaskNumber())) {
            return;
        }
        String customerFull = hepTaskDto.getCustomerFull();
        Map<String, String> hepTaskAppointVersionMap = appConfigDto.getHepTaskAppointVersionMap();
        if (hepTaskAppointVersionMap.containsKey(customerFull)) {
            String taskVersion = hepTaskDto.getSprintVersionFull();
            String version = hepTaskAppointVersionMap.get(customerFull);
            String ver = version.substring(0, 18);
            if (taskVersion.startsWith(ver) && !StringUtils.equals(version, taskVersion)) {
                setTaskLevel(hepTaskDto, "错版");
            }
        }
    }

    private void controlCheckScriptTips(boolean checkDate) throws Exception {
        if (checkDate) {
            if (taskUserPage()) {
                checkDate = true;
            } else {
                checkDate = false;
            }
        } else {
            checkDate = false;
        }
        if (checkDate) {
            scriptCheck.setStyle(STYLE_BOLD_RED_FOR_BUTTON);
        } else {
            scriptCheck.setStyle(STYLE_NORMAL_FOR_BUTTON);
        }
    }

    private double getTipsLocation(int num) {
        double x = 800;
        if (num > 2) {
            x = 800 - (num - 2) * 65;
        }
        if (x < 100) {
            x = 100;
        }
        return x;
    }

    private void updateHepStatFile(AppConfigDto appConfigDto, Set<String> taskNoList, Set<String> demandNoList) throws IOException {
        if (frontPage()) {
            if (appConfigDto.getQueryUpdateTaskFile() || appConfigDto.getQueryUpdateTaskFileByCondition()) {
                return;
            }
            appConfigDto.setQueryUpdateTaskFile(true);
            int taskNum = taskNoList.size();
            int demandNum = demandNoList.size();
            List<String> demandStat = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEMAND_STATUS_STAT));
            List<String> taskStat = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_TASK_INFO_STAT));
            List<String> taskExtendStat = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_STATUS_STAT));
            List<String> taskDevExtendStat = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_DEV_STAT));
            List<String> taskLevelExtendStat = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_LEVEL_STAT));
            updateFile(demandStat, demandNoList, PATH_DEMAND_STATUS_STAT, demandNum, taskNum);
            updateFile(taskStat, taskNoList, PATH_TASK_INFO_STAT, demandNum, taskNum);
            updateFile(taskExtendStat, taskNoList, PATH_DEFINE_TASK_STATUS_STAT, demandNum, taskNum);
            updateFile(taskDevExtendStat, taskNoList, PATH_DEFINE_TASK_DEV_STAT, demandNum, taskNum);
            taskNoList.addAll(demandNoList);
            updateFile(taskLevelExtendStat, taskNoList, PATH_DEFINE_TASK_LEVEL_STAT, demandNum, taskNum);
            if (CollectionUtils.isNotEmpty(syncTaskLog)) {
                syncTaskLog.add(String.format(STR_NEXT_LINE + "需求总数【%s】任务总数【%s】", demandNum, taskNum));
            }
            appConfigDto.setQueryUpdateTaskFile(false);
        }
    }

    private void updateFile(List<String> content, Set<String> keys, String path, int demandNum, int taskNum ) throws IOException {
        if (CollectionUtils.isEmpty(content)) {
            return;
        }
        int oriLength = content.size();
        Iterator<String> iterator = content.listIterator();
        Set<String> deleteData = new LinkedHashSet<>();
        while (iterator.hasNext()) {
            String item = iterator.next();
            String taskKey = TaCommonUtils.getDemandTaskKey(item, false);
            if (!keys.contains(taskKey)) {
                iterator.remove();
                deleteData.add(item);
            }
        }
        int lastLength = content.size();
        int deleteLength = deleteData.size();
        String fileName = STR_BLANK;
        String errorMessage = STR_BLANK;
        if (path.endsWith(PATH_DEMAND_STATUS_STAT)) {
            fileName = "需求状态";
        } else if (path.endsWith(PATH_TASK_INFO_STAT)) {
            fileName = "任务信息";
        } else if (path.endsWith(PATH_DEFINE_TASK_STATUS_STAT)) {
            fileName = "任务状态";
        } else if (path.endsWith(PATH_DEFINE_TASK_DEV_STAT)) {
            fileName = "分支状态";
        } else if (path.endsWith(PATH_DEFINE_TASK_LEVEL_STAT)) {
            fileName = "任务描述";
        }

        if (oriLength != lastLength && StringUtils.isBlank(errorMessage)) {
            if (CollectionUtils.isEmpty(content)) {
                content.add(STR_BLANK);
            }
            FileUtils.writeFile(FileUtils.getFilePath(path), new ArrayList<>(content));
            String tipTitle = fileName + "【%s】原始数据【%s】删除数据【%s】最终数据【%s】";
            syncTaskLog.add(String.format(tipTitle, path, oriLength, deleteLength, lastLength));
            if (StringUtils.isNotBlank(errorMessage)) {
                syncTaskLog.add(errorMessage);
            }
            logs.add(String.format(tipTitle + "剔除数据详情【%s】", path, oriLength, deleteLength, lastLength, deleteData.stream().collect(Collectors.joining(STR_SPACE_3))));
        }
    }

    private void setTaskLevel(HepTaskDto item, String taskLevel) {
        if (!taskLevelDict.contains(taskLevel)) {
            taskLevelDict.add(taskLevel);
        }
        item.setTaskLevel(StringUtils.isBlank(item.getTaskLevel()) ? taskLevel : taskLevel + "/" + item.getTaskLevel() );
    }

    private String formatVersion(String ver) {
        return CommonUtils.getSimpleVer(ver);
    }

    private void initTaskLevelDict(String taskLevelQ) {
        Collections.sort(taskLevelDict, new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        ObservableList taskLevel = taskLevelQuery.getItems();
        taskLevel.clear();
        if (CollectionUtils.isNotEmpty(taskLevelDict)) {
            Iterator<String> ver = taskLevelDict.iterator();
            while (ver.hasNext()) {
                taskLevel.add(ver.next());
            }
        }
        taskLevel.add(STR_SPACE);
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

    private void infoTaskList(TableView taskListIn, List<HepTaskDto> res, Set<String> dayTodoTask, Set<String> weekTodoTask, Set<String> finishDateError,
                              Set<String> focusVersionTask, List<String> focusDemand) {
        if (taskListIn == null) {
            return;
        }

        Platform.runLater(() -> {
            for (HepTaskDto hepTaskDto : res) {
                formatData(hepTaskDto);
                taskListIn.getItems().add(hepTaskDto);
                // 设置行
                initRowStyle(taskListIn, dayTodoTask,  weekTodoTask, finishDateError, focusVersionTask, focusDemand);
            }
            OutputUtils.setEnabled(taskListIn);
        });
    }

    private void formatData(HepTaskDto hepTaskDto) {
        if (StringUtils.isNotBlank(hepTaskDto.getFinishDate())) {
            hepTaskDto.setFinishDate(hepTaskDto.getFinishDate().substring(5));
        }
        if (StringUtils.isNotBlank(hepTaskDto.getOriCloseDate())) {
            hepTaskDto.setOriCloseDate(hepTaskDto.getOriCloseDate().substring(5));
        }
        if (StringUtils.isNotBlank(hepTaskDto.getOriPublishDate())) {
            hepTaskDto.setOriPublishDate(hepTaskDto.getOriPublishDate().substring(5));
        }
    }

    private void initRowStyle(TableView taskListIn, Set<String> dayTodoTask, Set<String> weekTodoTask, Set<String> finishDateError,
                              Set<String> focusVersionTask, List<String> focusDemand) {
        taskListIn.setRowFactory(new Callback<TableView<HepTaskDto>, TableRow<HepTaskDto>>() {
            @Override
            public TableRow<HepTaskDto> call(TableView<HepTaskDto> param) {
                final TableRow<HepTaskDto> row = new TableRow<HepTaskDto>() {
                    @Override
                    protected void updateItem(HepTaskDto item, boolean empty) {
                        try {
                            super.updateItem(item, empty);
                            if (item != null && getIndex() > -1) {
                                String taskNumber = item.getTaskNumber();
                                String demandNo = item.getDemandNo();
                                String[] taskColor;
                                if (dayTodoTask.contains(taskNumber)) {
                                    taskColor = color.get("今天待提交");
                                } else if (finishDateError.contains(taskNumber)) {
                                    taskColor = color.get("完成日期超期");
                                } else if (focusVersionTask.contains(taskNumber) || focusDemand.contains(demandNo)) {
                                    taskColor = color.get("重点关注");
                                } else if (weekTodoTask.contains(taskNumber)) {
                                    taskColor = color.get("本周待提交");
                                } else {
                                    taskColor = color.get("默认");
                                }
                                String taskStyle = taskColor[0];
                                setStyle(taskStyle);
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
        closeDate = CommonUtils.getCurrentDateYmd(closeDate);
        publishDate = CommonUtils.getCurrentDateYmd(publishDate);
        endDate = CommonUtils.getCurrentDateYmd(endDate);
        return String.valueOf(Math.min(Math.min(Integer.valueOf(closeDate), Integer.valueOf(publishDate)), Integer.valueOf(endDate)));
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
        double layoutY = 215;
        while (iterator.hasNext()) {
            String tagName = iterator.next();
            layoutX = buildTag(tagName, layoutX, layoutY);
            if (layoutX == 60) {
                layoutY = 240;
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

    public void initVersionTask(List<HepTaskDto> task) {
        Iterator<HepTaskDto> iterator = task.iterator();
        String sprintVersionQ = CommonUtils.getComponentValue(sprintVersionQuery);
        Set<String> currentTaskVersion = new HashSet<>();
        while (iterator.hasNext()) {
            HepTaskDto item = iterator.next();
            String sprintVersion = item.getSprintVersion();
            currentTaskVersion.add(sprintVersion);
        }
        initVersion(currentTaskVersion, sprintVersionQ);
    }

    public boolean filterTaskForRemove(AppConfigDto appConfigDto, HepTaskDto task, String taskLevelQ) {
        String taskNumberQ = CommonUtils.getComponentValue(taskNumberQuery);
        String nameQ = CommonUtils.getComponentValue(nameQuery);
        String sprintVersionQ = CommonUtils.getComponentValue(sprintVersionQuery);
        if (frontPage()) {
            if (StringUtils.isNotBlank(taskNumberQ) || StringUtils.isNotBlank(nameQ) || StringUtils.isNotBlank(sprintVersionQ) || StringUtils.isNotBlank(taskLevelQ)) {
                appConfigDto.setQueryUpdateTaskFileByCondition(true);
            }
        }
        String taskNumber = task.getTaskNumber();
        String taskName = task.getName();
        String sprintVersion = task.getSprintVersion();
        String taskLevel = task.getTaskLevel() == null ? STR_BLANK : task.getTaskLevel();
        if (StringUtils.isNotBlank(taskNumberQ) && !taskNumber.contains(taskNumberQ)) {
           return true;
        }
        if (StringUtils.isNotBlank(nameQ) && !taskName.contains(nameQ)) {
            return true;
        }
        if (StringUtils.isNotBlank(sprintVersionQ) && !sprintVersion.contains(sprintVersionQ)) {
            return true;
        }
        if (StringUtils.isNotBlank(taskLevelQ) && !taskLevel.contains(taskLevelQ)) {
            return true;
        }
        return false;
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
        param.put(KEY_CHARSET, ENCODING_UTF8.toLowerCase());
        param.put(KEY_FORMAT, KEY_MD5);
        param.put(KEY_TIMESTAMP, System.currentTimeMillis());
        cn.hutool.json.JSONObject jsonObject = new cn.hutool.json.JSONObject();
        initRequest(param, jsonObject);
        DigestAlgorithm digestAlgorithm = DigestAlgorithm.MD5;
        String sign = SecureUtil.signParams(digestAlgorithm, jsonObject, STR_AND, STR_EQUAL, true, new String[0]).toUpperCase();
        jsonObject.set(KEY_SIGN, sign);
        if (!CommonUtils.proScene()) {
            return null;
        }
        if (CommonUtils.isDebug()) {
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
            if (CommonUtils.isDebug()) {
                Object data = result.get(KEY_DATA);
                result.remove(KEY_DATA);
                logs.add("返回结果: " + result);
                if (data instanceof JSONArray) {
                    JSONArray ele = (JSONArray) data;
                    if (ele.size() > 0) {
                        for (int i = 0; i < ele.size(); i++) {
                            logs.add(STR_SPACE_10 + ele.get(i));
                        }
                    }
                }
            } else {
                cn.hutool.json.JSONObject print = initJSONObject();
                print.set(KEY_MESSAGE, NAME_DEAL_SUCCESS);
                logs.add("返回结果: " + print);
            }
        } else {
            logs.add("返回结果: " + result);
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
                    String minCompleteBySort = hepTaskDto.getMinCompleteBySort();
                    if (taskName.contains(DEV_COMMIT_TAG)) {
                        minCompleteBySort = getValue(STR_8);
                    }
                    String sortCode = minCompleteBySort + taskName + item.getFinishDate() + item.getCustomer() + item.getSprintVersion() + item.getTaskLevel();
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

    private String getValue(String type) {
        if (StringUtils.isBlank(type)) {
            type = STR_1;
        }
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
            return "1050-00-00";
        } else if (STR_7.equals(type)) {
            return "1060-00-00";
        } else if (STR_8.equals(type)) {
            return "1070-00-00";
        }
        return "9999-00-00";
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
                            String tabName = CommonUtils.getMenuName(TASK_TODO.getCode(), TASK_TODO.getName());
                            appConfigDto.setActivateFunction(tabName);
                            Tab openTab = CommonUtils.getOpenTab(AppCache.FUNCTION_TAB_CACHE, TASK_TODO.getCode(), TASK_TODO.getName());
                            AppCache.FUNCTION_TAB_CACHE.getSelectionModel().select(openTab);
                        } catch (Exception e) {
                            LoggerUtils.info(e);
                        }
                    });
                }
            };
            timer.schedule(timerTask, 1);
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
                width = ((TableColumn)taskList.getColumns().get(0)).getPrefWidth() - 255;
                sideBarBtn.setText("隐藏侧边栏");
            }
            ((TableColumn)taskList.getColumns().get(0)).setPrefWidth(width);
            taskSplitPane.setDividerPositions(positions);
        });
    }

    private void printTaskInfo(List<HepTaskDto> taskList) {
        if (frontPage() && CollectionUtils.isNotEmpty(taskList) &&
                (StringUtils.equals(queryType, devCompleteHide.getText()) || StringUtils.equals(queryType, devCompleteShow.getText()))) {
            String printType = StringUtils.equals(queryType, devCompleteShow.getText()) ? NAME_TASK_DEV_COMPLETE : NAME_TASK_NOT_COMPLETE;
            String detail = taskList.stream().filter(hepTaskDto ->
                    StringUtils.isNotBlank(hepTaskDto.getDemandNo())).map(HepTaskDto::getDemandNo).distinct().collect(Collectors.joining(STR_COMMA)
            );
            FRONT_QUERY_DEMAND_CACHE.put(printType, detail);
            logs.add(String.format(printType + "需求(%s) %s", taskList.size(), detail).trim());
;        }
    }

    private void setSyncFrontVersionTips(boolean visible) {
        syncFrontVersionTips.setVisible(visible);
    }

    private void initComponentStatus() {
        filePushTips.setVisible(false);
        setSyncFrontVersionTips(false);
        setSideBar();
    }

    private void syncFile() throws Exception {
        TaskUtils.execute(new HepTodoTask(new HepTodoTaskParam(this, "doSyncFile")));
    }

    public void doFilePushCheck() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        Timer filePushTimer = appConfigDto.getTimerMap().get(KEY_FILE_PUSH_TIMER);
        if (filePushTimer == null) {
            filePushTimer = new Timer();
            appConfigDto.getTimerMap().put(KEY_FILE_PUSH_TIMER, filePushTimer);
        } else {
            filePushTimer.cancel();
            return;
        }
        Platform.runLater(() -> {
            TimerTask filePushTimerTask = new TimerTask() {
                @Override
                public void run() {
                AppConfigDto appConfig;
                try {
                    appConfig = ConfigCache.getAppConfigDtoCache();
                    checkCommitPush(appConfig);
                    outputMemory();
                } catch (Exception e) {
                    LoggerUtils.info(e);
                }
                }
            };
            appConfigDto.getTimerMap().get(KEY_FILE_PUSH_TIMER).schedule(filePushTimerTask, 1000, appConfigDto.getFilePushTimer() * 1000);
        });
    }

    public void doSyncFile() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (isExtendUser()) {
            return;
        }
        doFilePushCheck();
        Platform.runLater(() -> {
            syncFileBtn.setText("停止文件同步");
        });
        Timer fileSyncTimer = appConfigDto.getTimerMap().get(KEY_FILE_SYNC_TIMER);
        String timerId = CommonUtils.getCurrentDateTime2();
        if (fileSyncTimer == null) {
            fileSyncTimer = new Timer();
            appConfigDto.getTimerMap().put(KEY_FILE_SYNC_TIMER, fileSyncTimer);
        } else {
            fileSyncTimer.cancel();
            return;
        }

        TimerTask fileSyncTimerTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                AppConfigDto appConfig = ConfigCache.getAppConfigDtoCache();
                Map<String, String> syncFileVersion = appConfig.getFileSyncVersionMap();
                if (MapUtils.isEmpty(syncFileVersion)) {
                    return;
                }
                String fileSyncAuthVersion = appConfig.getFileSyncAuthVersion();
                if (StringUtils.isBlank(fileSyncAuthVersion)) {
                    OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr("未配置授权同步版本信息"));
                    OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr("请检查参数【file.sync.auth.version】"));
                    return;
                }
                List<String> authVersion = Arrays.asList(fileSyncAuthVersion.toLowerCase().split(STR_COMMA));
                OutputUtils.clearLog(noticeSync);
                String threadMsg = "轮询线程: " + timerId;
                OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContain(threadMsg));
                int num = authVersion.size();
                for (Map.Entry<String, String> version : syncFileVersion.entrySet()) {
                    String ver = version.getKey();
                    if (!authVersion.contains(ver)) {
                        num--;
                        OutputUtils.repeatInfo(notice, TaCommonUtils.getMsgContainTimeContainBr(ver.toUpperCase() + " 未授权同步"));
                        continue;
                    }
                    ver = ver.toUpperCase();
                    String[] path = version.getValue().split(STR_COMMA);
                    if (path.length != 2) {
                        num--;
                        OutputUtils.repeatInfo(notice, TaCommonUtils.getMsgContainTimeContainBr(ver + " 同步路径配置错误"));
                        continue;
                    }
                    String fileSyncSource = path[0];
                    String fileSyncTarget = path[1];
                    String versionMsg = "轮询版本: " + ver.replace(STR_VERSION_PREFIX, STR_BLANK);
                    OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr(versionMsg));
                    fileSyncSourceFile.clear();
                    try {
                        File sourceFile = new File(fileSyncSource);
                        if (!sourceFile.exists()) {
                            num--;
                            OutputUtils.repeatInfo(notice, TaCommonUtils.getMsgContainTimeContainBr(String.format("文件同步源目录不存在 %s", ver)));
                            OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(fileSyncSource));
                            continue;
                        }
                        sync(sourceFile, fileSyncSource, fileSyncTarget, ver);
                    } catch (IOException e) {
                        LoggerUtils.info(e);
                        OutputUtils.info(noticeSync, TaCommonUtils.getMsgContainTimeContainBr(e.getMessage()));
                    }
                    clearFile(new File(fileSyncTarget), ver);
                }
                OutputUtils.info(syncFileTime, String.format("轮询时间(%s) %s", num, CommonUtils.getCurrentDateTime14()));
            }
        };
        fileSyncTimer.schedule(fileSyncTimerTask, 1000, appConfigDto.getFileSyncTimer() * 1000);
    }

    private void checkCommitPush(AppConfigDto appConfigDto) {
        Map<String, String> svnRep = appConfigDto.getSvnUrl();
        Set<String> checkVer = new LinkedHashSet<>(2);
        checkVer.add(svnRep.get(KEY_TRUNK));
        checkVer.add(svnRep.get(KEY_GIT_BRANCHES));
        boolean push = false;
        String threadMsg = STR_BLANK;
        outer: for (String ver : checkVer) {
            push = false;
            threadMsg = STR_BLANK;
            if (StringUtils.isNotBlank(ver)) {
                File[] file = new File(ver).listFiles();
                if (file != null) {
                    for (File item : file) {
                        if (FileUtils.isSuffixDirectory(item, BaseConst.FILE_TYPE_GIT, false, true)) {
                            String path = item.getAbsolutePath();
                            String content = CmdUtils.exe(path, "git status");
                            if (notPush(content)) {
                                threadMsg = item.getName() + " 未推送仓库";
                                push = true;
                                break outer;
                            }
                        }
                    }
                }
            }
        }
        if (push) {
            filePushTips.setStyle(STYLE_BOLD_RED);
            filePushTips.setVisible(true);
        } else {
            filePushTips.setStyle(STYLE_NORMAL);
            filePushTips.setVisible(false);
        }
        OutputUtils.info(filePushTips, threadMsg);
    }

    private boolean notPush(String content) {
        return StringUtils.isNotBlank(content) && content.toLowerCase().contains("your branch is ahead of");
    }

    private void outputMemory() throws Exception {
        if (isExtendUser()) {
            return;
        }
        String[] memoryInfo = CommonUtils.getMemoryInfo();
        OutputUtils.info(memoryTips, "内存(MB): " + memoryInfo[0]);
        if (Integer.valueOf(memoryInfo[1]) > 2048 || Integer.valueOf(memoryInfo[2]) > 1024) {
            memoryTips.setStyle(STYLE_BOLD_RED);
        } else {
            memoryTips.setStyle(STYLE_NORMAL);
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
                List<String> content = FileUtils.readNormalFile(source);
                OutputUtils.info(fileTipsVersion, version);
                OutputUtils.info(fileTipsFile, getFileName(source));
                OutputUtils.info(fileTipsFileOperate, operate);

                if (CollectionUtils.isEmpty(content)) {
                    OutputUtils.info(fileTipsFileStatus, "略过");
                    OutputUtils.info(fileTipsFileTime, CommonUtils.getCurrentDateTime14());
                } else {
                    OutputUtils.info(fileTipsFileStatus, "开始");
                    OutputUtils.info(fileTipsFileTime, CommonUtils.getCurrentDateTime14());
                    FileUtils.writeFile(target, content, ENCODING_UTF8);
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
                if (extendUserInfoNameToCode.containsKey(tabCode)) {
                    tabCode = extendUserInfoNameToCode.get(tabCode);
                }
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
        if (frontPage()) {
            CURRENT_USER_ID = EXTEND_USER_FRONT_CODE;
        }
        String user = String.format("当前用户: %s", CURRENT_USER_ID);
        OutputUtils.info(notice, TaCommonUtils.getMsgContainTimeContainBr(user));
        if (!logs.contains(user)) {
            logs.add(user);
        }
    }

    private boolean frontPage() {
        return PAGE_USER.equals(EXTEND_USER_FRONT_CODE);
    }

    private boolean taskUserPage() throws Exception {
        return PAGE_USER.equals(ConfigCache.getAppConfigDtoCache().getHepTaskUser());
    }

    private void controlComponent() {
        if (frontPage()) {
            showDemand.setVisible(true);
        } else {
            showDemand.setVisible(false);
        }
    }

    private void controlComponentByExtendUser(boolean syncComponent, boolean checkFileComponent, boolean syncTaskComponent) {
        fileTipsFile.setVisible(syncComponent);
        fileTipsFileTitle.setVisible(syncComponent);
        fileTipsFileTime.setVisible(syncComponent);
        fileTipsFileTimeTitle.setVisible(syncComponent);
        fileTipsFileStatus.setVisible(syncComponent);
        fileTipsFileStatusTitle.setVisible(syncComponent);
        fileTipsFileOperate.setVisible(syncComponent);
        fileTipsFileOperateTitle.setVisible(syncComponent);
        fileTipsVersion.setVisible(syncComponent);
        fileTipsVersionTitle.setVisible(syncComponent);

        scriptCheck.setVisible(checkFileComponent);
        scriptShow.setVisible(checkFileComponent);

        extendUser.setVisible(syncTaskComponent);
        syncTask.setVisible(syncTaskComponent || frontPage());
        syncFileBtn.setVisible(syncTaskComponent);
    }

    private boolean isExtendUser() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        return !CURRENT_USER_ID.equals(appConfigDto.getHepTaskUser()) || frontPage();
    }

    private void initExtendUser(AppConfigDto appConfigDto) {
        if (MapUtils.isNotEmpty(extendUserInfoCodeToName)) {
            return;
        }
        String userExtend = appConfigDto.getHepTaskUserExtend();
        if (StringUtils.isNotBlank(userExtend)) {
            extendUserInfoCodeToName.put(appConfigDto.getHepTaskUser(), appConfigDto.getHepTaskUserName());
            extendUserInfoNameToCode.put(appConfigDto.getHepTaskUserName(), appConfigDto.getHepTaskUser());
            String[] user = userExtend.split(STR_COMMA);
            for (String extend : user) {
                String[] extendInfo = extend.split(STR_COLON);
                if (extendInfo.length == 2) {
                    extendUserInfoCodeToName.put(extendInfo[0], extendInfo[1]);
                    extendUserInfoNameToCode.put(extendInfo[1], extendInfo[0]);
                }
            }
        }
    }

    private void showExtendTask(boolean changeTab) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String userExtend = appConfigDto.getHepTaskUserExtend();
        if (StringUtils.isNotBlank(userExtend)) {
            Tab defaultTab = null;
            for (Map.Entry<String, String> entry : extendUserInfoCodeToName.entrySet()) {
                MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.TASK_TODO;
                String tabCode = entry.getValue();
                if (StringUtils.equals(tabCode, appConfigDto.getHepTaskUserName())) {
                    continue;
                }
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

    private void controlColorDesc(boolean visible) {
        if (CollectionUtils.isNotEmpty(colorList)) {
            for (Label label : colorList) {
                label.setVisible(visible);
            }
        }
    }

    private void initColorDesc() {
        double step = 15;
        double x = 20;
        double y = 143;
        Label label = new Label("颜色说明:");
        String boldStyle = STYLE_NORMAL;
        label.setStyle(STYLE_BOLD);
        label.setLayoutX(x);
        label.setLayoutY(y);
        todoTitle.getChildren().add(label);
        colorList.add(label);
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
        List<String> versionList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION_STAT));
        List<VersionDto> versionDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(versionList)) {
            for (String item : versionList) {
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                String[] elements = item.split(STR_SEMICOLON);
                VersionDto versionDto = new VersionDto();
                String versionCode = elements[0];
                String oriCloseDate = elements[1];
                String oriPublishDate = elements[2];
                String endDate = elements[3];
                versionDto.setCode(versionCode);
                versionDto.setCloseDate(oriCloseDate);
                versionDto.setPublishDate(oriPublishDate);
                versionDto.setEndData(endDate);
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
            List<String> taskList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_TASK_INFO_STAT));
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (String item : taskList) {
                    if (StringUtils.isBlank(item)) {
                        continue;
                    }
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
            List<String> taskList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_DEV_STAT));
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (String item : taskList) {
                    if (StringUtils.isBlank(item)) {
                        continue;
                    }
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
            List<String> taskList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_LEVEL_STAT));
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (String item : taskList) {
                    if (StringUtils.isBlank(item)) {
                        continue;
                    }
                    String[] elementList = item.split(STR_SEMICOLON);
                    String level = elementList[1];
                    switch (level) {
                        case STR_0:
                            level = NAME_MENU_TASK_LEVEL_SIMPLE.split(STR_RIGHT_FACING)[1];
                            break;
                        case STR_1:
                            level = NAME_MENU_TASK_LEVEL_GENERAL.split(STR_RIGHT_FACING)[1];
                            break;
                        case STR_2:
                            level = NAME_MENU_TASK_LEVEL_DIFFICULTY.split(STR_RIGHT_FACING)[1];
                            break;
                        case STR_3:
                            level = NAME_MENU_TASK_LEVEL_QUESTION.split(STR_RIGHT_FACING)[1];
                            break;
                        case STR_4:
                            level = STR_SPACE;
                            break;
                        default:
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
            versionList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION_STAT));
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
            List<String> taskList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEMAND_STATUS_STAT));
            taskList.addAll(FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_DEV_STAT)));
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (String item : taskList) {
                    if (StringUtils.isBlank(item)) {
                        continue;
                    }
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
            List<String> taskList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_DEFINE_TASK_STATUS_STAT));
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (String item : taskList) {
                    if (StringUtils.isBlank(item)) {
                        continue;
                    }
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

    private String getTipsMsg(String ori, String msg) {
        if (StringUtils.isNotBlank(ori)) {
            return ori + STR_SPACE_2 + msg;
        }
        return msg;
    }

    private void controlTooltip(AppConfigDto appConfigDto, boolean show, String msg, double x, double y) {
        Platform.runLater(() -> {
            Tooltip tooltip = appConfigDto.getTooltip();
            tooltip.hide();
            tooltip.setText(msg);
            if (show) {
                tooltip.show(sideBar, x, y);
            }
        });

    }

    private void addTaskMenu(AppConfigDto appConfigDto, HepTodoController hepTodoController) {
        taskList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue instanceof HepTaskDto) {
                    HepTaskDto val = (HepTaskDto) newValue;
                    String msg = STR_BLANK;
                    boolean show = false;
                    if (val != null) {
                        String version = val.getSprintVersion();
                        if (StringUtils.isNotBlank(version) && version.length() > 16) {
                            msg = version;
                        }

                        String level = val.getTaskLevel();
                        if (StringUtils.isNotBlank(level) && level.contains(STR_SLASH)) {
                            msg = getTipsMsg(msg, level);
                        }

                        String reviewerName = val.getReviewerName();
                        if (StringUtils.isNotBlank(reviewerName) && reviewerName.contains(STR_COMMA)) {
                            msg = getTipsMsg(msg, reviewerName);
                        }
                        if (StringUtils.isNotBlank(msg)) {
                            show = true;
                        }
                    }
                    controlTooltip(appConfigDto, show, msg, 1580, 265);
                }
            }
        });

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
                    if (hepTaskDto == null) {
                        return;
                    }
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
                    JvmCache.setActiveHepTodoController(hepTodoController);
                }
            }
        });
    }

    private boolean requestStatus(HttpResponse response) {
        return !CommonUtils.proScene() || STATUS_200 == response.getStatus();
    }

    private void controlQueryButtonColor(Button button) {
        for (Button item : queryButtonSet) {
            if (StringUtils.equals(button.getText(), item.getText())) {
                item.setStyle(STYLE_SELECTED_FOR_BUTTON);
            } else {
                item.setStyle(STYLE_CANCEL_FOR_BUTTON);
            }
        }
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
        if (CommonUtils.proScene()) {
            return;
        }
        JSONArray req = new JSONArray();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> item = new HashMap<>(16);
            item.put(KEY_ID, i);
            item.put(KEY_TASK_NUMBER, "T20230801000" + i);
            item.put("demand_no", "20230801000" + i);
            item.put("product_name", "HUNDSUN基金登记过户系统软件V6.0");
            item.put("assignee_name", "胡毛毛");
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
                    item.put("reviewer_name", "abc,def,abc,def,abc,def,abc,def,abc,def,abc,def");
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
