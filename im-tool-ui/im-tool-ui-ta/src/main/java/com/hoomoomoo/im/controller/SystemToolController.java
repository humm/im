package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.task.ScriptCheckTask;
import com.hoomoomoo.im.task.ScriptCheckTaskParam;
import com.hoomoomoo.im.task.SystemToolTask;
import com.hoomoomoo.im.task.SystemToolTaskParam;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

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

    private Timer shakeMouseTimer;

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
        if (Boolean.valueOf(appConfigDto.getSystemToolShakeMouseAuto())) {
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
            shakeMouseTimer = new Timer();
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
            LoggerUtils.info(e);
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
            LoggerUtils.info(e);
            LoggerUtils.info(e);
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
            LoggerUtils.info(e);
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
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg("清除系统日志错误"));
        }
    }

    @FXML
    void showExplain(ActionEvent event) {
        OutputUtils.clearLog(logs);
        OutputUtils.info(logs, "public static final String HUNDSUN_VERSION = \"@system 理财登记过户平台 @version 6.0.0.1 @lastModiDate "+ CommonUtils.getCurrentDateTime3() +" @describe humm23693\";");
        OutputUtils.info(logs, STR_NEXT_LINE_2);
        StringBuilder serialVersion = new StringBuilder();
        for (int i=0; i<=18; i++) {
            serialVersion.append((int) (Math.random() * 9));
        }
        OutputUtils.info(logs, "private static final long  serialVersionUID = " + serialVersion.toString() + "L;");
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
            LoggerUtils.info(e);
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
            if ("CommonUtils.java".equals(fileName)) {
                updateFile++;
                OutputUtils.info(logs, getCommonMsg(NAME_SYNC_CODE, "修改文件 " + fileName));
                FileUtils.deleteFile(new File(toTargetPath));
                for (int j=0; j<content.size(); j++) {
                    String ele = content.get(j);
                    if (ele.contains("FileUtils.startByJar()")) {
                        ele = ele.replace("FileUtils.startByJar()", "true");
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
            LoggerUtils.info(getShakeMouseMsg(e.getMessage()));
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
            throw new Exception(String.format("读取文件夹【%s】错误，请重试", file.getAbsolutePath()));
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
                            LoggerUtils.info(e);
                            LoggerUtils.info(String.format("读取文件【%s】异常", subFilePath));
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
            String taskPath = FileUtils.getFilePath(PATH_TASK_STAT);
            if (new File(taskPath).exists()) {
                handleDemand(FileUtils.readNormalFile(taskPath), taskList);
            }
            FileUtils.writeFile(taskPath, new ArrayList<>(taskList));
        }
        LoggerUtils.info("同步获取需求数量: " + demandList.size());
        if (CollectionUtils.isNotEmpty(demandList)) {
            String demandPath = FileUtils.getFilePath(PATH_DEFINE_DEMAND_SYNC_STAT);
            if (new File(demandPath).exists()) {
                handleDemand(FileUtils.readNormalFile(demandPath), demandList);
            }
            FileUtils.writeFile(demandPath, new ArrayList<>(demandList));
        }
        LoggerUtils.info("同步任务信息结束");
    }

    private void handleDemand(List<String> demandRes, Set<String> demandList) {
        Set<String> keys = new HashSet<>();
        for (String item : demandList) {
            String key = item.split(STR_SEMICOLON)[0].trim() + STR_SEMICOLON;
            keys.add(key);
        }
        for (String item : demandRes) {
            String key = item.split(STR_SEMICOLON)[0].trim() + STR_SEMICOLON;
            if (!keys.contains(key)) {
                demandList.add(item);
            }
        }
    }

    public void syncVersion(AppConfigDto appConfigDto) throws Exception {
        LoggerUtils.info("同步版本信息开始");
        File file = new File(appConfigDto.getHepTaskCustomerPath() + PATH_SYNC_VERSION_STAT);
        if (!file.isDirectory()) {
            LoggerUtils.info(String.format("读取文件夹【%s】", file.getAbsolutePath()));
            throw new Exception(String.format("读取文件夹【%s】错误，请重试", file.getAbsolutePath()));
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
                LoggerUtils.info(e);
                LoggerUtils.info(String.format("读取文件【%s】异常", subFilePath));
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

    private void syncExcel(String filePath, String configParam, String sheetName, String statFile, String logName, String excelType) throws Exception {
        List<String> list = new ArrayList<>();
        FileInputStream fileInputStream = null;
        if (StringUtils.isBlank(filePath)) {
            if (logs != null) {
                OutputUtils.info(logs, getUpdateVersionMsg("请配置【" + configParam + "】"));
            } else {
                throw new Exception("请配置【" + configParam + "】");
            }
        } else {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    List<File> files = Arrays.asList(file.listFiles());
                    if (CollectionUtils.isNotEmpty(files)) {
                        files = files.stream().filter(ele -> ele.getName().contains(sheetName)).collect(Collectors.toList());
                        Collections.sort(files, new Comparator<File>() {
                            @Override
                            public int compare(File o1, File o2) {
                                return o1.lastModified() - o2.lastModified() >= 0 ? -1 : 1;
                            }
                        });
                    }
                    if (CollectionUtils.isNotEmpty(files)) {
                        filePath = files.get(0).getPath();
                    }
                    File[] fileList = file.listFiles();
                    for (File ele : fileList) {
                        if (!filePath.equals(ele.getPath()) && ele.getPath().contains(sheetName)) {
                            FileUtils.deleteFile(ele);
                        }
                    }
                }
                fileInputStream = getSyncFile(null, filePath, 0);
                Sheet sheet;
                if (filePath.endsWith(FILE_TYPE_XLS)) {
                    HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
                    sheet = workbook.getSheetAt(0);
                } else {
                    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                    sheet = workbook.getSheetAt(0);
                }
                int rows = sheet.getLastRowNum();
                for (int i = 1; i <= rows; i++) {
                    StringBuilder item = new StringBuilder();
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    if ("version".equals(excelType)) {
                        String version = row.getCell(0).toString();
                        if (StringUtils.isBlank(version) || version.contains("TA6.0-PRO") || version.contains("TA6.0-PHFUND")) {
                            continue;
                        }
                        String closeDate = formatDate(row.getCell(1).toString());
                        String publishDate = formatDate(row.getCell(2).toString());
                        if (StringUtils.isBlank(closeDate)) {
                            closeDate = publishDate;
                        }
                        String customer = STR_SPACE;
                        item.append(version).append(STR_SEMICOLON).append(closeDate).append(STR_SEMICOLON).append(publishDate).append(STR_SEMICOLON).append(customer).append(STR_SEMICOLON);
                        list.add(item.toString());
                    } else if ("task".equals(excelType)) {
                        if (i == 1) {
                            continue;
                        }
                        String taskNumber = row.getCell(0).toString();
                        if (StringUtils.isBlank(taskNumber)) {
                            continue;
                        }
                        String customerName = row.getCell(1).toString().split("（")[0];
                        if (StringUtils.isNotBlank(customerName)) {
                            String[] customer = customerName.split(STR_COMMA);
                            customerName = customer[0];
                            if (customer.length > 1 && NAME_INNER_CUSTOMER.equals(customerName)) {
                                customerName = customer[1];
                            }
                        }
                        if (StringUtils.isBlank(customerName)) {
                            customerName = NAME_INNER_CUSTOMER;
                        }
                        String demandNo = row.getCell(2).toString();
                        if (StringUtils.isBlank(demandNo)) {
                            demandNo = STR_SPACE;
                        }
                        String[] demandNoList = demandNo.split(STR_COMMA);
                        for (String single : demandNoList) {
                            item.setLength(0);
                            item.append(taskNumber).append(STR_SEMICOLON).append(customerName).append(STR_SEMICOLON).append(single);
                            list.add(item.toString());
                        }
                    }
                }
                String statPath = FileUtils.getFilePath(statFile);
                if (CollectionUtils.isNotEmpty(list)) {
                    FileUtils.writeFile(statPath, list);
                }
                if (logs != null) {
                    OutputUtils.info(logs, getUpdateVersionMsg("同步成功"));
                    OutputUtils.info(logs, STR_NEXT_LINE);
                }
                addLog(logName);
            } catch (Exception e) {
                LoggerUtils.info(e);
                throw new Exception(e.getMessage());
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    private static FileInputStream getSyncFile(FileInputStream fileInputStream, String filePath, int times) throws FileNotFoundException {
        try {
            fileInputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            if (e.getMessage().contains("拒绝访问")) {
                times++;
                if (times <= 3) {
                    getSyncFile(fileInputStream, filePath, times);
                }
                throw new FileNotFoundException(NAME_NO_AUTH);
            } else {
                throw new FileNotFoundException("文件不存在,请检查");
            }
        }
        return fileInputStream;
    }

    public static void addLog(String msg) {
        List<String> logs = new ArrayList<>();
        logs.add(msg);
        LoggerUtils.writeLogInfo(SYSTEM_TOOL.getCode(), new Date(), logs);
    }

}
