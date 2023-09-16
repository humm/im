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
import com.hoomoomoo.im.dto.HepTaskComponent;
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.service.HepWaitHandleTaskMenu;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
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
public class HepTaskTodoController extends BaseController implements Initializable {
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
    /** 0:待启动,4:开发中,5,8:待集成,14,16,17:待审核,18,6 */
    private final static String STATUS_LIST = "0,4,5,8,14,16,17,18,6";
    private final static String OPERATE_TYPE_START = "1";
    private final static String OPERATE_TYPE_UPDATE = "2";
    private final static String OPERATE_TYPE_COMPLETE = "3";
    private final static String STATUS_WAIT_START = "0";
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
    }

    @FXML
    private Label waitHandleTaskNum;

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
    private Label notice;

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
    void showTaskInfo(MouseEvent event) throws Exception {
        HepTaskDto item = (HepTaskDto)taskList.getSelectionModel().getSelectedItem();
        item.setOperateType(STR_BLANK);
        OutputUtils.repeatInfo(taskNumber, item.getTaskNumber());
        OutputUtils.repeatInfo(name, item.getName());
        OutputUtils.repeatInfo(sprintVersion, item.getSprintVersion());
        OutputUtils.repeatInfo(statusName, item.getStatusName());
        OutputUtils.repeatInfo(id, item.getId());
        String clickType = event.getButton().toString();
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
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
                    OutputUtils.info(notice, TaCommonUtils.getMsgContainDate(e.getMessage()));
                }
            }
        } else if (STATUS_DEV.equals(status)) {
            try {
                completeTask(item);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(notice, TaCommonUtils.getMsgContainDate(e.getMessage()));
            }
        } else {
            LoggerUtils.info("不支持的操作类型");
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDate("不支持的操作类型"));
        }
    }

    @FXML
    void executeQuery(ActionEvent event) throws Exception {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, TASK_TODO.getName()));
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        CURRENT_USER_ID = appConfigDto.getHepTaskUser();
        if (StringUtils.isBlank(CURRENT_USER_ID)) {
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDate("请配置[ hep.task.user ]"));
            return;
        }
        new Thread(() -> {
            try {
                setProgress(0);
                updateProgress();
                executeQuery();
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(notice, TaCommonUtils.getMsgContainDate(e.getMessage()));
            }
        }).start();
    }

    @FXML
    void executeReset(ActionEvent event) throws Exception {
        OutputUtils.clearLog(taskNumberQuery);
        OutputUtils.clearLog(nameQuery);
        OutputUtils.clearLog(sprintVersionQuery);
        executeQuery(null);
    }

    private void executeQuery() {
        new Thread(() -> {
            try {
                query.setDisable(true);
                queryCondition.setDisable(true);
                reset.setDisable(true);
                execute(OPERATE_QUERY, null);
                setProgress(1);
                LoggerUtils.writeLogInfo(TASK_TODO.getCode(), new Date(), logs);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(notice, TaCommonUtils.getMsgContainDate(ExceptionMsgUtils.getMsg(e)));
            } finally {
                query.setDisable(false);
                queryCondition.setDisable(false);
                reset.setDisable(false);
            }
        }).start();
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
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDate("不支持的操作类型"));
            throw new Exception("不支持的操作类型");
        }
        JSONArray items = new JSONArray();
        if (response != null) {
            LoggerUtils.info("response: " + response.body());
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
                        dealTaskList(items,  logs, waitHandleTaskNum, taskList, true);
                    } else if (OPERATE_START.equals(operateType)) {
                        executeQuery(null);
                    }
                }
            }
            OutputUtils.info(notice, TaCommonUtils.getMsgContainDate(message));
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
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                Stage stage = appConfigDto.getTaskStage();
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
                    appConfigDto.setTaskStage(null);
                    stage = null;
                }

                if (stage == null) {
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
                    appConfigDto.setTaskStage(stage);
                    stage.setOnCloseRequest(columnEvent -> {
                        appConfigDto.getTaskStage().close();
                        appConfigDto.setTaskStage(null);
                    });
                } else {
                    stage.toFront();
                }
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(notice, TaCommonUtils.getMsgContainDate(e.getMessage()));
            }
        }
    }

    private void setTaskComponent(AppConfigDto appConfigDto) {
        HepTaskComponent hepTaskComponent = new HepTaskComponent();
        hepTaskComponent.setLogs(logs);
        hepTaskComponent.setWaitHandleTaskNum(waitHandleTaskNum);
        hepTaskComponent.setTaskList(taskList);
        appConfigDto.setHepTaskComponent(hepTaskComponent);
    }

    private Map<String, Object> executeCompletTask(HepTaskDto hepTaskDto) throws Exception {
        Map<String, Object> request = new HashMap<>(16);
        request.put(KEY_METHOD, METHOD_UPDATE_TASK_STATUS);
        request.put(KEY_CURRENT_USER_ID, CURRENT_USER_ID);
        request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_COMPLETE);
        if (OPERATE_TYPE_CUSTOM_UPDATE.equals(hepTaskDto.getOperateType())) {
            request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_UPDATE);
            // 完成百分比
            request.put(KEY_FINISH_PERCENTAGE, STR_0);
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

    public void dealTaskList(JSONArray task, List<String> logsIn, Label waitHandleTaskNumIn, TableView taskListIn, boolean tagFlag) {
        List<HepTaskDto> res = JSONArray.parseArray(JSONObject.toJSONString(task), HepTaskDto.class);
        filterTask(res);
        res = sortTask(res);
        if (tagFlag) {
            initTag(res);
        }
        for (int i=0; i<task.size(); i++) {
            Map<String, Object> item = (Map)task.get(i);
            logsIn.add(item.toString());
        }
        Iterator<HepTaskDto> iterator = res.listIterator();
        boolean hasBlank = false;
        int taskTotal = 0;
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
        }
        OutputUtils.clearLog(waitHandleTaskNumIn);
        OutputUtils.clearLog(taskListIn);
        OutputUtils.info(waitHandleTaskNumIn, String.valueOf(res.size() - taskTotal));
        infoTaskList(taskListIn, res);
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
                                setStyle("-fx-text-background-color: red;");
                            } else if (taskNameTag.contains("问题") || taskNameTag.contains("任务")) {
                                setStyle("-fx-text-background-color: #804000;");
                            } else if (taskName.contains("已修改") || taskName.contains("已提交")) {
                                setStyle("-fx-text-background-color: #550080;");
                            } else {
                                setStyle("-fx-text-background-color: blue;");
                            }
                        }
                    }
                };
                return row;
            }
        });
    }

    private void initCellColor(TableView taskListIn) {
        ((TableColumn)taskListIn.getColumns().get(0)).setCellFactory(column -> {
            return new TableCell<HepTaskDto, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (StringUtils.isNotBlank(item)) {
                        String taskName =item;
                        String taskNameTag = getTaskNameTag(item);
                        setText(item);
                        if(getIndex() > -1){
                            if (taskNameTag.contains("缺陷")) {
                                setStyle("-fx-text-fill: #ff6200;");
                            } else if (taskNameTag.contains("问题")) {
                                setStyle("-fx-text-fill: #510080;");
                            } else if (taskNameTag.contains("任务")) {
                                setStyle("-fx-text-fill: #800062;");
                            } else if (taskName.contains("已修改")) {
                                setStyle("-fx-text-fill: #5c8000;");
                            } else if (taskName.contains("已提交")) {
                                setStyle("-fx-text-fill: #00805e;");
                            } else {
                                setStyle("-fx-text-fill: #804000;");
                            }
                        }
                    }
                }
            };
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
                    dealTaskList(res, logs, waitHandleTaskNum, taskList, false);
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
        /*Iterator<String> iteratorTask = taskType.keySet().iterator();
        while (iteratorTask.hasNext()) {
            task.add(getDivideTask(iteratorTask.next()));
        }*/
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
        LoggerUtils.info("url: " + REQUEST_URL);
        LoggerUtils.info("method: " + jsonObject.get(KEY_METHOD));
        LoggerUtils.info("request: " + jsonObject);
        if (testScene()) {
            return null;
        }
        HttpResponse response = HttpRequest.post(REQUEST_URL).timeout(10 * 1000).form(jsonObject).execute();;
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
                    Date finishTime1 = simpleDateFormat.parse(getValue(o1.getEstimateFinishTime(), STR_BLANK));
                    Date finishTime2 = simpleDateFormat.parse(getValue(o2.getEstimateFinishTime(), STR_BLANK));
                    if (finishTime1.getTime() != finishTime2.getTime()) {
                        return finishTime1.compareTo(finishTime2);
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
                    e.printStackTrace();
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

    private HepTaskDto getDivideTask(String type) {
        int blankNumber = Integer.valueOf(type);
        String blankType = STR_BLANK;
        for (int i=0; i<blankNumber; i++) {
            blankType += STR_SPACE;
        }
        HepTaskDto item = new HepTaskDto();
        item.setEstimateFinishTime(getDivideValue(STR_BLANK, type));
        item.setId(STR_BLANK);
        item.setTaskNumber(blankType);
        item.setName(blankType);
        item.setOperateType(STR_BLANK);
        item.setSprintVersion(STR_BLANK);
        item.setProductName(STR_BLANK);
        return item;
    }

    private String getValue(String value, String type) {
        if (StringUtils.isBlank(value)) {
            if (StringUtils.isBlank(type) || STR_1.equals(type)) {
                return "9900-12-31 23:59:59";
            } else if (STR_2.equals(type)) {
                return "1900-12-31 23:59:59";
            } else if (STR_9.equals(type)) {
                return "9999-12-31 23:59:59";
            }
        }
        return value;
    }

    private String getDivideValue(String value, String type) {
        if (StringUtils.isBlank(value)) {
            if (StringUtils.isBlank(type) || STR_1.equals(type)) {
                return "9900-12-30 23:59:59";
            } else if (STR_2.equals(type)) {
                return "9800-12-30 23:59:59";
            } else if (STR_9.equals(type)) {
                return "9999-12-30 23:59:59";
            }
        }
        return value;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, TASK_TODO.getName()));
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            CURRENT_USER_ID = appConfigDto.getHepTaskUser();
            addTaskMenu(appConfigDto);
            executeQuery(null);
            buildTestData();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private void addTaskMenu(AppConfigDto appConfigDto) {
        taskList.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String clickType = event.getButton().toString();
                if (RIGHT_CLICKED.equals(clickType)) {
                    Node node = event.getPickResult().getIntersectedNode();
                    HepWaitHandleTaskMenu hepWaitHandleTaskMenu = HepWaitHandleTaskMenu.getInstance();
                    HepTaskDto hepTaskDto = (HepTaskDto)taskList.getSelectionModel().getSelectedItem();
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

    private void buildTestData() {
        if (!testScene()) {
            return;
        }
        JSONArray req = new JSONArray();
        for (int i=0; i<10; i++) {
            Map<String, Object> item = new HashMap<>(16);
            item.put(KEY_ID, i);
            item.put(KEY_TASK_NUMBER, "T20230801000" + i);
            item.put("product_name", "HUNDSUN基金登记过户系统软件V6.0");
            item.put("sprint_version", "TA6.0-FUND.V202304.07.000M6");
            item.put("status", i % 2 == 0 ? 0 : 4);
            item.put("status_name", i % 2 == 0 ? "待启动" : "开发中");
            item.put(KEY_ESTIMATE_FINISH_TIME, "2024-07-24 22:59:59");
            switch (i % 7) {
                case 0: item.put(KEY_NAME, "「开发」问题" + i);break;
                case 1: item.put(KEY_NAME, "「开发」【缺陷:45454】问题" + i);break;
                case 2: item.put(KEY_NAME, "「自测问题」问题" + i);break;
                case 6: item.put(KEY_NAME, "「自建任务」问题" + i);break;
                case 3: item.put(KEY_NAME, "「开发」已修改 问题" + i);break;
                case 4: item.put(KEY_NAME, "「开发」已提交 问题" + i);break;
                case 5: item.put(KEY_NAME, "「修复问题」问题" + i);break;
                default:break;
            }
            req.add(item);
        }
        dealTaskList(req, logs, waitHandleTaskNum, taskList, true);
    }

    private boolean requestStatus(HttpResponse response) {
        return testScene() || STATUS_200 == response.getStatus();
    }

    private boolean testScene() {
        return !FileUtils.startByJar();
    }
}
