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
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.TASK_TODO;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2022/07/30
 */
public class HepWaitHandleTaskController extends BaseController implements Initializable {
    private static Set<String> field = new LinkedHashSet<>();

    private final static boolean TEST_FLAG = false;

    private final static Integer STATUS_200 = 200;
    private final static String STR_STATUS_200 = "200";
    private final static String REQUEST_URL = "http://cloudin.proxy.in.hundsun.com/openapi/invoke/defaultFormData";
    private final static String APP_ID = "dqwhyanulhrmrrnk";
    private final static String APP_KEY = "fbbbee8e31a646d3a3a45f5c0e5b3e9";
    private final static String CURRENT_USER_ID = "humm23693";
    private final static String METHOD_GET_FIELD_INFO = "devtool/getFieldInfo";
    private final static String METHOD_FETCH_TASK_BY_ID = "devtool/fetchTaskById";
    private final static String METHOD_UPDATE_TASK_STATUS = "devtool/updateTaskStatus";
    private final static String METHOD_FETCH_TASK_LIST = "devtool/fetchTaskList";
    /** 0:待启动,4:开发中,5,8:待集成,14,16,17:待审核,18,6 */
    private final static String STATUS_LIST = "0,4,5,8,14,16,17,18,6";
    private final static String OPERATE_TYPE_START = "1";
    private final static String OPERATE_TYPE_COMPLETE = "3";
    private final static String STATUS_WAIT_START = "0";
    private final static String STATUS_DEV = "4";
    private final static String STATUS_WAIT_INTEGRATE = "8";
    private final static String STATUS_WAIT_CHECK = "17";
    private final static String OPERATE_QUERY = "1";
    private final static String OPERATE_START = "2";
    public final static String OPERATE_COMPLETE = "3";

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
    private Label todoNum;

    @FXML
    private Label checkNum;

    @FXML
    private Label bugNum;

    @FXML
    private TextField taskNumber;

    @FXML
    private TextField name;

    @FXML
    private TextField sprintVersion;

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
    void showTaskInfo(MouseEvent event) {
        HepTaskDto item = (HepTaskDto)taskList.getSelectionModel().getSelectedItem();
        OutputUtils.repeatInfo(taskNumber, item.getTaskNumber());
        OutputUtils.repeatInfo(name, item.getName());
        OutputUtils.repeatInfo(sprintVersion, item.getSprintVersion());
        OutputUtils.repeatInfo(statusName, item.getStatusName());
        OutputUtils.repeatInfo(id, String.valueOf(item.getId()));
        String clickType = event.getButton().toString();
        if (RIGHT_CLICKED.equals(clickType)) {
            buildClipboardInfo(item);
        } else if (LEFT_CLICKED.equals(clickType) && event.getClickCount() == SECOND_CLICKED) {
            operateTask(item);
        }
    }

    void buildClipboardInfo (HepTaskDto item) {
        StringBuilder info = new StringBuilder();
        info.append("[需求编号]").append(SYMBOL_SPACE).append(SYMBOL_NEXT_LINE);
        info.append("[修改单编号]").append(SYMBOL_SPACE).append(item.getTaskNumber()).append(SYMBOL_NEXT_LINE);
        info.append("[修改单版本]").append(SYMBOL_SPACE).append(item.getSprintVersion()).append(SYMBOL_NEXT_LINE);
        info.append("[需求引入行]").append(SYMBOL_SPACE).append(SYMBOL_NEXT_LINE);
        String name = item.getName();
        if (name.contains(SYMBOL_BRACKETS_2_RIGHT)) {
            name = name.split(SYMBOL_BRACKETS_2_RIGHT)[1];
        }
        info.append("[需求描述]").append(SYMBOL_SPACE).append(name);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(info.toString()), null);
        OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + "任务单【" + item.getTaskNumber() + "】复制成功");
    }
    void operateTask(HepTaskDto item) {
        String status = item.getStatus();
        if (STATUS_WAIT_START.equals(status)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("启动任务");
            String tips = "任务【" + item.getTaskNumber() + "】 " + SYMBOL_NEXT_LINE;
            tips += "版本【" + item.getSprintVersion() + "】 " + SYMBOL_NEXT_LINE;
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
                    OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + e.getMessage());
                }
            }
        } else if (STATUS_DEV.equals(status)) {
            try {
                completeTask(item);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + e.getMessage());
            }
        } else {
            LoggerUtils.info("不支持的操作类型");
            OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + "不支持的操作类型");
        }
    }

    @FXML
    void executeQuery(ActionEvent event) {
        new Thread(() -> {
            try {
                setProgress(0);
                updateProgress();
                OutputUtils.clearLog(taskList);
                OutputUtils.clearLog(waitHandleTaskNum);
                OutputUtils.clearLog(todoNum);
                OutputUtils.clearLog(checkNum);
                OutputUtils.clearLog(bugNum);
                executeQuery();
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + e.getMessage());
            }
        }).start();
    }

    private void executeQuery() {
        new Thread(() -> {
            try {
                query.setDisable(true);
                execute(OPERATE_QUERY, null);
                setProgress(1);
                LoggerUtils.writeLogInfo(TASK_TODO.getCode(), new Date(), logs);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + SYMBOL_SPACE + ExceptionMsgUtils.getMsg(e));
            } finally {
                query.setDisable(false);
            }
        }).start();
    }

    public void execute(String operateType, HepTaskDto hepTaskDto) throws Exception {
        HttpResponse response;
        if (OPERATE_QUERY.equals(operateType)) {
            response = sendPost(getTaskList());
        } else if (OPERATE_START.equals(operateType)) {
            response = sendPost(startTask(hepTaskDto));
        } else if (OPERATE_COMPLETE.equals(operateType)) {
            response = sendPost(executeCompletTask(hepTaskDto));
        } else {
            OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + "不支持的操作类型");
            throw new Exception("不支持的操作类型");
        }
        if (response != null) {
            LoggerUtils.info("response: " + response.body());
            Map<String, Object> responseInfo;
            String message = response.body();
            if (STATUS_200 == response.getStatus()) {
                responseInfo = (Map)JSONObject.parse(response.body());
                message = String.valueOf(responseInfo.get(KEY_MESSAGE));
                String code = String.valueOf(responseInfo.get(KEY_CODE));
                if (STR_STATUS_200.equals(code)) {
                    Object data = responseInfo.get(KEY_DATA);
                    JSONArray items = new JSONArray();
                    if (data instanceof Map) {
                        items.add(data);
                    } else if (data instanceof List) {
                        items = (JSONArray)data;
                    }
                    if (OPERATE_QUERY.equals(operateType)) {
                        dealTaskList(items);
                    } else if (OPERATE_START.equals(operateType)) {
                        executeQuery();
                    }
                }
            }
            OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + SYMBOL_SPACE + message);
        }
    }

    private void completeTask(HepTaskDto hepTaskDto) {
        // 获取列表数据
        Map<String, Object> request = new HashMap<>(16);
        request.put(KEY_METHOD, METHOD_GET_FIELD_INFO);
        request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_COMPLETE);
        HttpResponse response = sendPost(request);
        if (STATUS_200 == response.getStatus()) {
            request.remove(KEY_OPERATE_TYPE);
            request.put(KEY_METHOD, METHOD_FETCH_TASK_BY_ID);
            request.put(KEY_ID, hepTaskDto.getId());
        }
        response = sendPost(request);
        if (STATUS_200 == response.getStatus()) {
            try {
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                Stage stage = appConfigDto.getTaskStage();
                appConfigDto.setHepTaskDto(hepTaskDto);
                if (stage == null) {
                    Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_COMPLETE_TASK_FXML)));
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
                    stage = new Stage();
                    stage.getIcons().add(new Image(PATH_ICON));
                    stage.setScene(scene);
                    stage.setTitle("完成任务");
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
                OutputUtils.info(notice, e.getMessage());
            }
        }
    }

    private Map<String, Object> executeCompletTask(HepTaskDto hepTaskDto) throws Exception {
        Map<String, Object> request = new HashMap<>(16);
        request.put(KEY_METHOD, METHOD_UPDATE_TASK_STATUS);
        request.put(KEY_CURRENT_USER_ID, CURRENT_USER_ID);
        request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_COMPLETE);
        request.put(KEY_ID, hepTaskDto.getId());
        // 实际完成时间
        request.put(KEY_REAL_FINISH_TIME, hepTaskDto.getRealFinishTime());
        // 完成百分比
        request.put(KEY_FINISH_PERCENTAGE, OPERATE_TYPE_START);
        // 集成注意
        request.put(KEY_INTEGRATE_ATTENTION, SYMBOL_EMPTY);
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
        int taskId = hepTaskDto.getId();
        setFreshFlag(hepTaskDto);
        Map<String, Object> request = new HashMap<>(8);
        request.put(KEY_METHOD, METHOD_GET_FIELD_INFO);
        request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_START);
        HttpResponse response = sendPost(request);
        if (STATUS_200 == response.getStatus()) {
            request.remove(KEY_OPERATE_TYPE);
            request.put(KEY_METHOD, METHOD_FETCH_TASK_BY_ID);
            request.put(KEY_ID, taskId);
        }
        response = sendPost(request);
        if (STATUS_200 == response.getStatus()) {
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

    private void dealTaskList(JSONArray items) {
        items = sortItems(items);
        for (int i=0; i<items.size(); i++) {
            Map<String, Object> item = (Map)items.get(i);
            logs.add(item.toString());
        }
        List<HepTaskDto> res = JSONArray.parseArray(JSONObject.toJSONString(items), HepTaskDto.class);
        Iterator<HepTaskDto> iterator = res.listIterator();
        while (iterator.hasNext()) {
            HepTaskDto item = iterator.next();
            if (STATUS_WAIT_INTEGRATE.equals(item.getStatus()) || STATUS_WAIT_CHECK.equals(item.getStatus())) {
                iterator.remove();
            }
        }
        OutputUtils.info(waitHandleTaskNum, String.valueOf(res.size()));
        OutputUtils.info(todoNum, String.valueOf(res.size()));
        OutputUtils.clearLog(taskList);
        OutputUtils.infoList(taskList, res, false);
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
        HttpResponse response;
        if (TEST_FLAG) {
            response = HttpRequest.get("https://www.hutool.cn/").execute();
        } else {
            response = HttpRequest.post(REQUEST_URL).timeout(10 * 1000).form(jsonObject).execute();
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

    private JSONArray sortItems(JSONArray items) {
        JSONArray res = new JSONArray();
        Set<String> existkey = new HashSet<>();
        items.sort(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date finishTime1 = simpleDateFormat.parse(getValue((Map)o1, KEY_ESTIMATE_FINISH_TIME));
                    Date finishTime2 = simpleDateFormat.parse(getValue((Map)o2, KEY_ESTIMATE_FINISH_TIME));
                    if (finishTime1.getTime() != finishTime2.getTime()) {
                        return finishTime1.compareTo(finishTime2);
                    }
                    String taskName1 = getValue((Map)o1, KEY_NAME);
                    String taskName2 = getValue((Map)o2, KEY_NAME);
                    if (taskName1.contains(SYMBOL_BRACKETS_2_RIGHT)) {
                        taskName1 = taskName1.split(SYMBOL_BRACKETS_2_RIGHT)[1];
                    }
                    if (taskName2.contains(SYMBOL_BRACKETS_2_RIGHT)) {
                        taskName2 = taskName2.split(SYMBOL_BRACKETS_2_RIGHT)[1];
                    }
                    return taskName1.compareTo(taskName2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });
        for (int i=0; i<items.size(); i++) {
            Map item = (Map)items.get(i);
            String taskNumber = getValue(item, KEY_TASK_NUMBER);
            String taskName = getValue(item, KEY_NAME);
            if (existkey.contains(taskNumber)) {
                continue;
            }
            existkey.add(taskNumber);
            res.add(item);
            for (int j=0; j<items.size(); j++) {
                Map itemTmp = (Map)items.get(j);
                String taskNumberTmp = getValue(itemTmp, KEY_TASK_NUMBER);
                String taskNameTmp = getValue(itemTmp, KEY_NAME);
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


    private String getValue(Map<String, Object> item, String key) {
        if (item.containsKey(key)) {
            return String.valueOf(item.get(key));
        }
        return SYMBOL_EMPTY;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, TASK_TODO.getName()));
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (TEST_FLAG) {
                buildTestData();
                return;
            } else {
                executeQuery();
            }

            Timer operateTimer = new Timer();
            TimerTask operateTimerTask = new TimerTask() {
                @Override
                public void run() {
                    HepTaskDto hepTaskDto = appConfigDto.getHepTaskDto();
                    if (hepTaskDto != null && hepTaskDto.getFreshFlag() != null && hepTaskDto.getFreshFlag()) {
                        hepTaskDto.setFreshFlag(false);
                        executeQuery(null);
                    }
                }
            };
            operateTimer.scheduleAtFixedRate(operateTimerTask, 1, new BigDecimal(appConfigDto.getHepTaskTodoOperateFreshTime()).multiply(new BigDecimal(1000)).longValue());
            appConfigDto.getTimerList().add(operateTimer);

            Timer todoTimer = new Timer();
            TimerTask todoTimerTask = new TimerTask() {
                @Override
                public void run() {
                    executeQuery(null);
                }
            };
            todoTimer.scheduleAtFixedRate(todoTimerTask, 1, new BigDecimal(appConfigDto.getHepTaskTodoTaskTime()).multiply(new BigDecimal(1000)).longValue());
            appConfigDto.getTimerList().add(todoTimer);

        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public void setFreshFlag(HepTaskDto taskDto) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        if (taskDto != null) {
            appConfigDto.setHepTaskDto(taskDto);
            taskDto.setFreshFlag(true);
        } else {
            HepTaskDto hepTaskDto = appConfigDto.getHepTaskDto();
            if (hepTaskDto != null) {
                hepTaskDto.setFreshFlag(true);
            }
        }
    }

    private void buildTestData() {
        List<Map> req = new ArrayList<>();
        for (int i=0; i<10; i++) {
            Map<String, Object> item = new HashMap<>(16);
            item.put(KEY_ID, i);
            item.put(KEY_TASK_NUMBER, "T20230801000" + i);
            item.put(KEY_NAME, "「修复问题」修复问题" + i);
            item.put("product_name", "基金登记过户系统软件V6.0");
            item.put("sprint_version", "TA6.0V202202.06.001");
            item.put("status", i % 2 == 0 ? 0 : 4);
            item.put("status_name", i % 2 == 0 ? "待启动" : "开发中");
            item.put(KEY_ESTIMATE_FINISH_TIME, "2024-07-24 22:59:59");
            req.add(item);
        }
        List<HepTaskDto> res = JSONArray.parseArray(JSONObject.toJSONString(req), HepTaskDto.class);
        OutputUtils.clearLog(taskList);
        OutputUtils.infoList(taskList, res, true);
    }
}
