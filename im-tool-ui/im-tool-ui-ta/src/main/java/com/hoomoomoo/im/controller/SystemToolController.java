package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.task.SystemToolTask;
import com.hoomoomoo.im.task.SystemToolTaskParam;
import com.hoomoomoo.im.timer.ImTimer;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.SYSTEM_TOOL;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class SystemToolController implements Initializable {

    @FXML
    private TextArea logs;

    @FXML
    private TextArea baseLogs;

    @FXML
    private Button shakeMouseBtn;

    @FXML
    private Button cancelShakeMouseBtn;

    @FXML
    private Button updateVersionBtn;

    @FXML
    private Button showVersionBtn;

    @FXML
    private Button syncCodeBtn;

    private ImTimer shakeMouseTimer;

    private Robot robot;

    private int moveStep = 1;
    private int syncFile = 0;
    private int updateFile = 0;
    private int skipFile = 0;

    private boolean executeFlag = false;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JvmCache.setSystemToolController(this);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        moveStep = Integer.valueOf(appConfigDto.getSystemToolShakeMouseStep());
        if (StringUtils.equals(appConfigDto.getSystemToolShakeMouseAuto(), STR_TRUE)) {
            shakeMouse(null);
            addLog(NAME_SHAKE_MOUSE);
        }
    }

    @FXML
    void shakeMouse(ActionEvent event) throws Exception {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, NAME_SHAKE_MOUSE));
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        shakeMouseBtn.setDisable(true);
        cancelShakeMouseBtn.setDisable(false);
        if (shakeMouseTimer == null) {
            shakeMouseTimer = new ImTimer(SYSTEM_TOOL_SHAKE_MOUSE_TIMER);
            appConfigDto.getTimerMap().put(SYSTEM_TOOL_SHAKE_MOUSE_TIMER, shakeMouseTimer);
        }
        shakeMouseTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doShakeMouse(appConfigDto);
            }
        }, 1, Long.valueOf(appConfigDto.getSystemToolShakeMouseTimer()) * 1000);
        addLog(NAME_SHAKE_MOUSE);
    }

    @FXML
    void cancelShakeMouse(ActionEvent event) {
        shakeMouseBtn.setDisable(false);
        cancelShakeMouseBtn.setDisable(true);
        if (shakeMouseTimer != null) {
            shakeMouseTimer.cancel();
            shakeMouseTimer = null;
        }
        addLog(NAME_SHAKE_MOUSE);
    }

    @FXML
    void updateVersion(ActionEvent event) {
        updateVersionBtn.setDisable(true);
        try {
            executeUpdateVersion();
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, e.getMessage());
        } finally {
            updateVersionBtn.setDisable(false);
        }
    }

    @FXML
    void showVersion(ActionEvent event) {
        showVersionBtn.setDisable(true);
        try {
            JvmCache.getHepTodoController().doShowVersion();
        } catch (Exception e) {
            LoggerUtils.error(e);
            LoggerUtils.error(e);
            OutputUtils.info(logs, e.getMessage());
        } finally {
            showVersionBtn.setDisable(false);
        }
        addLog("查看发版时间");
    }

    @FXML
    void showSystemLog(ActionEvent event) {
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SYSTEM_LOG, "系统日志");
            addLog("查看系统日志");
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCheckMenuMsg("查看系统日志错误"));
        }
    }

    @FXML
    void clearSystemLog(ActionEvent event) {
        try {
            FileUtils.deleteFile(new File(FileUtils.getFilePath(String.format(SUB_PATH_LOG, "appLog"))));
            MenuFunctionConfig.FunctionConfig[] functionConfigs = MenuFunctionConfig.FunctionConfig.values();
            for (MenuFunctionConfig.FunctionConfig functionConfig : functionConfigs) {
                FileUtils.deleteFile(new File(FileUtils.getFilePath(String.format(SUB_PATH_LOG, functionConfig.getLogFolder()))));
            }
            OutputUtils.info(logs, getCommonMsg(NAME_CLEAR_LOG, "日志清除成功"));
            addLog("清除系统日志");
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCheckMenuMsg("清除系统日志错误"));
        }
    }

    @FXML
    void syncCode(ActionEvent event) throws Exception {
        OutputUtils.clearLog(logs);
        OutputUtils.info(logs, getCommonMsg(NAME_SYNC_CODE, "同步源码开始"));
        TaskUtils.execute(new SystemToolTask(new SystemToolTaskParam(this, NAME_SYNC_CODE)));
        syncCodeBtn.setDisable(true);
    }

    public void doSyncCode() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String source = appConfigDto.getSystemToolSyncCodeSource();
        String target = appConfigDto.getSystemToolSyncCodeTarget();
        if (StringUtils.isBlank(source)) {
            OutputUtils.info(logs, getCommonMsg(NAME_SYNC_CODE, "请配置参数【system.tool.sync.code.source】"));
        }
        if (StringUtils.isBlank(target)) {
            OutputUtils.info(logs, getCommonMsg(NAME_SYNC_CODE, "请配置参数【system.tool.sync.code.target】"));
        }
        syncFile = 0;
        updateFile = 0;
        skipFile = 0;
        try {
            syncFile(new File(source), source, target);
            OutputUtils.info(logs, getCommonMsg(NAME_SYNC_CODE, String.format("同步文件: %s  修改文件: %s  忽略文件: %s", syncFile, updateFile, skipFile)));
            OutputUtils.info(logs, getCommonMsg(NAME_SYNC_CODE, "同步源码结束"));
        } catch (Exception e) {
            OutputUtils.info(logs, getCommonMsg(NAME_SYNC_CODE, e.getMessage()));
            LoggerUtils.error(e);
        } finally {
            syncCodeBtn.setDisable(false);
        }
    }

    private void syncFile(File source, String sourcePath, String targetPath) throws Exception {
        String fileName = source.getName();
        if ("target".equals(fileName) || ".git".equals(fileName) || ".idea".equals(fileName) || ".gitignore".equals(fileName)
                || fileName.endsWith(".iml") || fileName.endsWith(".conf")) {
            return;
        }
        if (source.isDirectory()) {
            File[] files = source.listFiles();
            for (File item : files) {
                syncFile(item, sourcePath, targetPath);
            }
        } else {
            OutputUtils.info(logs, getCommonMsg(NAME_SYNC_CODE, "同步文件 " + fileName));
            String filePath = source.getAbsolutePath();
            String toTargetPath = filePath.replace(sourcePath, targetPath);
            List<String> content =  FileUtils.readNormalFile(filePath);
            syncFile++;
            if ("HepTodoController.java".equals(fileName)) {
                updateFile++;
                OutputUtils.info(logs, getCommonMsg(NAME_SYNC_CODE, "修改文件 " + fileName));
                FileUtils.deleteFile(new File(toTargetPath));
                for (int j=0; j<content.size(); j++) {
                    String ele = content.get(j);
                    if (ele.contains("CommonUtils.proScene()")) {
                        ele = ele.replace("CommonUtils.proScene()", "true");
                    }
                    FileUtils.writeFileAppend(toTargetPath, ele + STR_NEXT_LINE);
                }
            } else if ("CheckConfigConst.java".equals(fileName)) {
                syncFile--;
                skipFile++;
                OutputUtils.info(logs, getCommonMsg(NAME_SYNC_CODE, "忽略文件 " + fileName));
                return;
            } else {
                FileUtils.writeFile(toTargetPath, content);
            }
        }
        return;
    }

    private String formatDate(String date) {
        return StringUtils.isBlank(date) ? STR_DATE_20991231 : date;
    }

    private String getShakeMouseMsg (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_SHAKE_MOUSE + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    private String getUpdateVersionMsg (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_UPDATE_VERSION + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    public static String getCheckMenuMsg (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_CHECK_MENU + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    public static String getCommonMsg (String functionName, String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ functionName + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    private void doShakeMouse(AppConfigDto appConfigDto) {
        try {
            if (robot == null) {
                robot = new Robot();
            }
        } catch (AWTException e) {
            LoggerUtils.error(getShakeMouseMsg(e.getMessage()));
            OutputUtils.info(baseLogs, getShakeMouseMsg(e.getMessage()));
        }
        Point pos = MouseInfo.getPointerInfo().getLocation();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        OutputUtils.info(baseLogs, getShakeMouseMsg("显示器分辨率: " + screenSize.getWidth() + " * " + screenSize.getHeight()));
        OutputUtils.info(baseLogs, getShakeMouseMsg("鼠标当前位置: " + pos.x + " * " + pos.y));
        if (pos.x >= screenSize.getWidth() || pos.y >= screenSize.getHeight()) {
            moveStep = moveStep * -1;
        }
        if (pos.x == 0 || pos.y == 0) {
            moveStep = Math.abs(moveStep);
        }
        OutputUtils.info(baseLogs, getShakeMouseMsg("鼠标移动步长: " + moveStep));
        OutputUtils.info(baseLogs, getShakeMouseMsg("模拟鼠标移动 ......"));
        robot.mouseMove(pos.x + moveStep, pos.y + moveStep);
        OutputUtils.info(baseLogs, getShakeMouseMsg("鼠标移动位置: " + (pos.x + moveStep) + " * " + (pos.y + moveStep)));
        OutputUtils.info(baseLogs, STR_NEXT_LINE);

        if (appConfigDto.getSystemToolShakeMouseStopTime().compareTo(CommonUtils.getCurrentDateTime13()) <= 0) {
            String stopMsg = getShakeMouseMsg("截止时间【" + appConfigDto.getSystemToolShakeMouseStopTime() + "】自动停止......" + STR_NEXT_LINE);
            OutputUtils.info(baseLogs, stopMsg);
            cancelShakeMouse(null);
        }
    }

    public void executeUpdateVersion() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        syncVersion(appConfigDto);
    }

    public void executeSyncTaskInfo() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        syncTask(appConfigDto);
    }

    public void syncTask(AppConfigDto appConfigDto) throws Exception {
        LoggerUtils.info("同步任务信息开始");
        File file = new File(appConfigDto.getHepTaskCustomerPath() + PATH_SYNC_TASK_STAT);
        if (!file.isDirectory()) {
            LoggerUtils.info(String.format("读取文件夹【%s】", file.getAbsolutePath()));
            throw new Exception(String.format(SKIP_LOG_TIPS + "读取文件夹【%s】错误，请重试", file.getAbsolutePath()));
        }
        LoggerUtils.info("同步任务信息读取文件开始");
        File[] fileList = file.listFiles();
        List<String> response = new ArrayList<>();
        if (fileList != null) {
            for (int i=0; i<fileList.length; i++) {
                String subFilePath = fileList[i].getAbsolutePath();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LoggerUtils.info(String.format("读取文件【%s】开始", subFilePath));
                            List<String> res = FileUtils.readNormalFile(subFilePath);
                            if (CollectionUtils.isNotEmpty(res)) {
                                response.addAll(res);
                            } else {
                                response.add(STR_BLANK);
                            }
                            LoggerUtils.info(String.format("读取文件【%s】结束", subFilePath));
                        } catch (IOException e) {
                            LoggerUtils.error(e);
                            LoggerUtils.error(String.format("读取文件【%s】异常", subFilePath));
                            response.add(STR_BLANK);
                        }
                    }
                }).start();
            }
        }
        while (true) {
            if (response.size() >= fileList.length) {
                break;
            }
            Thread.sleep(3 * 1000);
        }
        LoggerUtils.info("同步任务信息读取文件结束");
        Set<String> demand = new HashSet<String>(){{
            add("https://dev.hundsun.com/heppm/story/getStoryMenuListV3");
            add("https://dev.hundsun.com/heppm/onSiteDefect/getList");
            add("https://dev.hundsun.com/heppm/qualityStory/getStoryListActionsV2");
        }};
        Set<String> task = new HashSet<String>(){{
            add("https://dev.hundsun.com/heppm/task/queryByConditionV4");
        }};
        Set<String> demandList = new HashSet<>();
        Set<String> taskList = new HashSet<>();
        if (CollectionUtils.isNotEmpty(response)) {
            for (String item : response) {
                String[] element = item.split(STR_SPLIT);
                if (element.length == 2) {
                    String key = element[0];
                    String value = element[1];
                    if (demand.contains(key)) {
                        demandList.addAll(TaCommonUtils.getDemandStatus(value));
                    } else if (task.contains(key)) {
                        Map<String, Set<String>> taskMap = TaCommonUtils.getTaskStatus(value);
                        taskList.addAll(taskMap.get(KEY_TASK));
                    }
                }
            }
        }
        LoggerUtils.info("同步获取任务数量: " + taskList.size());
        if (CollectionUtils.isNotEmpty(taskList)) {
            String taskPath = FileUtils.getFilePath(PATH_TASK_INFO_STAT);
            if (new File(taskPath).exists()) {
                mergeData(FileUtils.readNormalFile(taskPath), taskList);
            }
            FileUtils.writeFile(taskPath, new ArrayList<>(taskList));
        }
        LoggerUtils.info("同步获取需求数量: " + demandList.size());
        if (CollectionUtils.isNotEmpty(demandList)) {
            String demandPath = FileUtils.getFilePath(PATH_DEMAND_STATUS_STAT);
            if (new File(demandPath).exists()) {
                mergeData(FileUtils.readNormalFile(demandPath), demandList);
            }
            FileUtils.writeFile(demandPath, new ArrayList<>(demandList));
        }
        LoggerUtils.info("同步任务信息结束");
    }

    private void mergeData(List<String> dataRes, Set<String> dataList) {
        Set<String> keys = new HashSet<>();
        for (String item : dataList) {
            String key = TaCommonUtils.getDemandTaskKey(item, true);
            keys.add(key);
        }
        for (String item : dataRes) {
            String key = TaCommonUtils.getDemandTaskKey(item, true);
            if (!keys.contains(key)) {
                dataList.add(item);
            }
        }
    }

    public void syncVersion(AppConfigDto appConfigDto) throws Exception {
        LoggerUtils.info("同步版本信息开始");
        File file = new File(appConfigDto.getHepTaskCustomerPath() + PATH_SYNC_VERSION_STAT);
        if (!file.isDirectory()) {
            LoggerUtils.info(String.format("读取文件夹【%s】", file.getAbsolutePath()));
            throw new Exception(String.format(SKIP_LOG_TIPS + "读取文件夹【%s】错误，请重试", file.getAbsolutePath()));
        }
        LoggerUtils.info("同步版本信息读取文件开始");
        File[] fileList = file.listFiles();
        List<String> response = new ArrayList<>();
        if (fileList != null) {
            String subFilePath = fileList[0].getAbsolutePath();
            try {
                LoggerUtils.info(String.format("读取文件【%s】开始", subFilePath));
                List<String> res = FileUtils.readNormalFile(subFilePath);
                response.addAll(res);
                LoggerUtils.info(String.format("读取文件【%s】结束", subFilePath));
            } catch (IOException e) {
                LoggerUtils.error(e);
                LoggerUtils.error(String.format("读取文件【%s】异常", subFilePath));
                response.add(STR_BLANK);
            }
        }
        LoggerUtils.info("同步版本信息读取文件结束");

        List<String> versionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(response)) {
            for (String item : response) {
                String[] element = item.split(STR_SPLIT);
                if (element.length == 2) {
                    String value = element[1];
                    versionList.addAll(TaCommonUtils.getVersionInfo(value));
                }
            }
        }
        String versionPath = FileUtils.getFilePath(PATH_VERSION_STAT);
        FileUtils.writeFile(versionPath, versionList);
        LoggerUtils.info("同步版本信息结束");
    }

    public static void addLog(String msg) {
        List<String> logs = new ArrayList<>();
        logs.add(msg);
        LoggerUtils.writeLogInfo(SYSTEM_TOOL.getCode(), new Date(), logs);
    }

}
