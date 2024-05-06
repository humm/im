package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.VersionDto;
import com.hoomoomoo.im.extend.ScriptCompareSql;
import com.hoomoomoo.im.extend.ScriptRepairSql;
import com.hoomoomoo.im.extend.ScriptUpdateSql;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE_EXTEND.REPAIR_OLD_MENU;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.*;

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
    private Button clearVersionBtn;

    @FXML
    private Button updateVersionBtn;

    @FXML
    private Button showVersionBtn;

    private Timer shakeMouseTimer;

    private Robot robot;

    private int moveStep = 1;

    private boolean executeFlag = false;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        moveStep = Integer.valueOf(appConfigDto.getSystemToolShakeMouseStep());
        if (Boolean.valueOf(appConfigDto.getSystemToolShakeMouseAuto())) {
            shakeMouse(null);
        }
        addLog(NAME_SHAKE_MOUSE);
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
    void clearVersion(ActionEvent event) {
        clearVersionBtn.setDisable(true);
        try {
            String statPath = FileUtils.getFilePath(PATH_VERSION_EXTEND_STAT);
            FileUtils.writeFile(statPath, STR_BLANK, false);
            OutputUtils.info(logs, getUpdateVersionMsg("清除个性化成功"));
            OutputUtils.info(logs, STR_NEXT_LINE);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, e.getMessage());
        } finally {
            clearVersionBtn.setDisable(false);
        }
        addLog("同步发版时间");
    }

    @FXML
    void showVersion(ActionEvent event) {
        showVersionBtn.setDisable(true);
        try {
            new HepTaskTodoController().doShowVersion();
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
    void checkMenu(ActionEvent event) throws Exception {
        closeCheckResultStage();
        if (executeFlag) {
            OutputUtils.info(logs, getCheckMenuMsg("检查中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        ConfigCache.getAppConfigDtoCache().setRepairSchedule(STR_BLANK);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OutputUtils.info(logs, getCheckMenuMsg("检查开始"));
                        showScheduleInfo(NAME_CHECK_MENU, "检查");
                        new ScriptCompareSql().check();
                        OutputUtils.info(logs, getCheckMenuMsg("检查结束"));
                        OutputUtils.info(logs, STR_NEXT_LINE);
                        addLog(NAME_CHECK_MENU);
                    } catch (Exception e) {
                        LoggerUtils.info(e);
                        OutputUtils.info(logs, getCheckMenuMsg(e.getMessage()));
                    } finally {
                        executeFlag = false;
                    }
                }
            }).start();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg(e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
    void skipNewMenu(ActionEvent event) throws Exception {
        String title = getTitle(LACK_NEW_MENU_ALL.getName());
        TaCommonUtils.openBlankChildStage(LACK_NEW_MENU_ALL.getIndex(), title);
        addLog(title);
    }

    @FXML
    void skipOldMenu(ActionEvent event) throws Exception {
        String title = getTitle(LACK_OLD_NEW_ALL.getName());
        TaCommonUtils.openBlankChildStage(LACK_OLD_NEW_ALL.getIndex(), title);
        addLog(title);
    }

    @FXML
    void skipRepairOldMenu(ActionEvent event) throws Exception {
        String title = getTitle(REPAIR_OLD_MENU.getName());
        TaCommonUtils.openBlankChildStage(REPAIR_OLD_MENU.getIndex(), title);
        addLog(title);
    }

    @FXML
    void skipNewDiff(ActionEvent event) throws Exception {
        String title = getTitle(DIFF_NEW_ALL_EXT.getName());
        TaCommonUtils.openBlankChildStage(DIFF_NEW_ALL_EXT.getIndex(), title);
        addLog(title);
    }

    @FXML
    void skipOldDiff(ActionEvent event) throws Exception {
        String title = getTitle(DIFF_OLD_ALL_EXT.getName());
        TaCommonUtils.openBlankChildStage(DIFF_OLD_ALL_EXT.getIndex(), title);
        addLog(title);
    }

    @FXML
    void skipRouter(ActionEvent event) throws Exception {
        String title = getTitle(LACK_ROUTER.getName());
        TaCommonUtils.openBlankChildStage(LACK_ROUTER.getIndex(), title);
        addLog(title);
    }

    @FXML
    void skipLog(ActionEvent event) throws Exception {
        String title = getTitle(LACK_LOG.getName());
        TaCommonUtils.openBlankChildStage(LACK_LOG.getIndex(), title);
        addLog(title);
    }

    @FXML
    void skipErrorLog(ActionEvent event) throws Exception {
        String title = getTitle(ERROR_LOG.getName());
        TaCommonUtils.openBlankChildStage(ERROR_LOG.getIndex(), title);
        addLog(title);
    }

    @FXML
    void skipNewLegal(ActionEvent event) throws Exception {
        String title = getTitle(LEGAL_NEW_MENU.getName());
        TaCommonUtils.openBlankChildStage(LEGAL_NEW_MENU.getIndex(), title);
        addLog(title);
    }

    @FXML
    void showCheckResult(ActionEvent event) {
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_CHECK_RESULT, "检查结果");
            addLog("检查结果");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg("请检查结果文件是否不存在"));
        }
    }

    @FXML
    void showSystemLog(ActionEvent event) {
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SYSTEM_LOG, "系统日志");
            addLog("系统日志");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg("请检查结果文件是否不存在"));
        }
    }

    @FXML
    void repairLackLog(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getRepairLackExt("修复中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        ConfigCache.getAppConfigDtoCache().setRepairSchedule(STR_BLANK);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OutputUtils.info(logs, getRepairLackExt("修复开始"));
                        showScheduleInfo(NAME_REPAIR_LACK_EXT, "修复");
                        ScriptRepairSql.repairLackLog();
                        OutputUtils.info(logs, getRepairLackExt("修复结束"));
                        OutputUtils.info(logs, STR_NEXT_LINE);
                        addLog(NAME_REPAIR_LACK_EXT);
                    } catch (Exception e) {
                        LoggerUtils.info(e);
                        OutputUtils.info(logs, getRepairLackExt(e.getMessage()));
                    } finally {
                        executeFlag = false;
                    }
                }
            }).start();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getRepairLackExt(e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
    void repairLogDiff(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LOG_DIFF, "修复中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        ConfigCache.getAppConfigDtoCache().setRepairSchedule(STR_BLANK);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LOG_DIFF, "修复开始"));
                        showScheduleInfo(NAME_REPAIR_LOG_DIFF, "修复");
                        ScriptRepairSql.repairLogDiff();
                        OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LOG_DIFF, "修复结束"));
                        OutputUtils.info(logs, STR_NEXT_LINE);
                        addLog(NAME_REPAIR_LOG_DIFF);
                    } catch (Exception e) {
                        LoggerUtils.info(e);
                        OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LOG_DIFF, e.getMessage()));
                    } finally {
                        executeFlag = false;
                    }
                }
            }).start();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LOG_DIFF, e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
    void repairOldMenu(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, "修复中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        ConfigCache.getAppConfigDtoCache().setRepairSchedule(STR_BLANK);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, "修复开始"));
                        showScheduleInfo(NAME_REPAIR_OLD_MENU, "修复");
                        ScriptRepairSql.repairOldMenu();
                        OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, "修复结束"));
                        OutputUtils.info(logs, STR_NEXT_LINE);
                        addLog(NAME_REPAIR_OLD_MENU);
                    } catch (Exception e) {
                        LoggerUtils.info(e);
                        OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, e.getMessage()));
                    } finally {
                        executeFlag = false;
                    }
                }
            }).start();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
    void repairNewMenu(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, "修复中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        ConfigCache.getAppConfigDtoCache().setRepairSchedule(STR_BLANK);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, "修复开始"));
                        showScheduleInfo(NAME_REPAIR_NEW_MENU, "修复");
                        ScriptRepairSql.repairNewMenu();
                        OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, "修复结束"));
                        OutputUtils.info(logs, STR_NEXT_LINE);
                        addLog(NAME_REPAIR_NEW_MENU);
                    } catch (Exception e) {
                        LoggerUtils.info(e);
                        OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, e.getMessage()));
                    } finally {
                        executeFlag = false;
                    }
                }
            }).start();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
     void repairErrorLog(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (appConfigDto.getExecute()) {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, "修复中 ··· 请稍后 ···"));
            return;
        }
        TaCommonUtils.openBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_REPAIR_ERROR_LOG, NAME_REPAIR_ERROR_EXT);
        ConfigCache.getAppConfigDtoCache().setRepairSchedule(STR_BLANK);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean startFlag = false;
                        while (true) {
                            boolean execute = ConfigCache.getAppConfigDtoCache().getExecute();
                            if (execute && !startFlag) {
                                startFlag = true;
                            }
                            if (!executeFlag && execute) {
                                OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, "修复开始"));
                                showScheduleInfo(NAME_REPAIR_ERROR_EXT, "修复");
                                executeFlag = true;
                            }
                            if (executeFlag && startFlag && !execute) {
                                OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, "修复结束"));
                                OutputUtils.info(logs, STR_NEXT_LINE);
                                addLog(NAME_REPAIR_ERROR_EXT);
                                break;
                            }
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                            }
                        }
                    } catch (Exception e) {
                        LoggerUtils.info(e);
                        OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, e.getMessage()));
                    } finally {
                        executeFlag = false;
                    }
                }
            }).start();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, e.getMessage()));
        }
    }

    @FXML
    void showUpdateResult(ActionEvent event) {
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_UPDATE_RESULT, "升级脚本");
            addLog("升级脚本");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getUpdateMenuMsg("请检查结果文件是否不存在"));
        }
    }

    @FXML
    void updateMenu(ActionEvent event) {
        closeCheckResultStage();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 设置颜色
                        // logs.setStyle("-fx-text-fill: green;");
                        OutputUtils.info(logs, getUpdateMenuMsg("执行开始"));
                        new ScriptUpdateSql().generateSql();
                        OutputUtils.info(logs, getUpdateMenuMsg("执行结束"));
                        OutputUtils.info(logs, STR_NEXT_LINE);
                        addLog("菜单升级脚本");
                    } catch (Exception e) {
                        LoggerUtils.info(e);
                        OutputUtils.info(logs, getUpdateMenuMsg(e.getMessage()));
                    }
                }
            }).start();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getUpdateMenuMsg(e.getMessage()));
        }
    }

    private String formatDate(String date) {
        if (StringUtils.isNotBlank(date) && (date.contains("E") || date.contains("e"))) {
            date = new BigDecimal(date).toPlainString();
        }
        return date;
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

    public static String getRepairLackExt (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_REPAIR_LACK_EXT + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    public static String getUpdateMenuMsg (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_UPDATE_MENU + "】") + STR_SPACE + msg + STR_NEXT_LINE;
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
        List<String> list = new ArrayList<>();
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String filePath = appConfigDto.getSystemToolUpdateVersionPath();
        FileInputStream fileInputStream = null;
        if (StringUtils.isBlank(filePath)) {
            if (logs != null) {
                OutputUtils.info(logs, getUpdateVersionMsg("请配置【system.tool.update.version.path】"));
            } else {
                throw new Exception("请配置【system.tool.update.version.path】");
            }
        } else {
            try {
                fileInputStream = new FileInputStream(filePath);
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                XSSFSheet sheet = workbook.getSheetAt(0);
                int rows = sheet.getLastRowNum();
                for (int i = 2; i <= rows; i++) {
                    StringBuilder item = new StringBuilder();
                    XSSFRow row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    String version = row.getCell(2).toString();
                    if (StringUtils.isBlank(version)) {
                        continue;
                    }
                    String closeDate = formatDate(row.getCell(4).toString());
                    String publishDate = formatDate(row.getCell(5).toString());
                    if (StringUtils.isBlank(closeDate)) {
                        closeDate = publishDate;
                    }
                    String customer = row.getCell(6).toString();
                    String memo = row.getCell(3).toString();
                    item.append(version).append(STR_SEMICOLON).append(closeDate).append(STR_SEMICOLON).append(publishDate)
                            .append(STR_SEMICOLON).append(customer).append(STR_SEMICOLON).append(memo);
                    list.add(item.toString());
                }
                String statPath = FileUtils.getFilePath(PATH_VERSION_STAT);
                FileUtils.writeFile(statPath, list, false);
                if (logs != null) {
                    OutputUtils.info(logs, getUpdateVersionMsg("同步成功"));
                    OutputUtils.info(logs, STR_NEXT_LINE);
                }
                addLog("同步发版时间");
            } catch (Exception e) {
                LoggerUtils.info(e);
                throw new Exception(e);
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

    public static void addLog(String msg) {
        List<String> logs = new ArrayList<>();
        logs.add(msg);
        LoggerUtils.writeLogInfo(SYSTEM_TOOL.getCode(), new Date(), logs);
    }

    private static void closeCheckResultStage() {
        AppConfigDto appConfigDto = null;
        try {
            appConfigDto = ConfigCache.getAppConfigDtoCache();
            Stage stage = appConfigDto.getCheckResultStage();
            if (stage != null) {
                stage.close();
                appConfigDto.setCheckResultStage(null);
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            LoggerUtils.info(getCheckMenuMsg(e.getMessage()));
        }
    }

    private void showScheduleInfo(String functionName, String msg) {
        long start = System.currentTimeMillis();
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    String repairSchedule = ConfigCache.getAppConfigDtoCache().getRepairSchedule();
                    if (executeFlag) {
                        String tips = msg + "中 ··· ···  耗时 " + getRunTime(start) + " ··· ";
                        if (StringUtils.isNotBlank(repairSchedule)) {
                            tips += STR_SPACE_3 + repairSchedule;
                        }
                        OutputUtils.info(logs, getCommonMsg(functionName, tips));
                    } else {
                       break;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    private String getRunTime(long start) {
        String time = STR_BLANK;
        long end = System.currentTimeMillis();
        long cost = (end - start) / 1000;
        long second = 0;
        long hour = 0;
        long minute = 0;
        second = cost % 60;
        minute = cost / 60;
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        if (hour > 0) {
            time = hour + "时";
        }
        if (minute > 0) {
            time += minute + "分";
        }
        if (hour == 0 && minute == 0 && second == 0) {
            second = 1;
        }
        time += second + "秒";
        return time;
    }

    private String getTitle(String title) {
        int start = title.indexOf(".");
        int end = title.lastIndexOf(".");
        if (end > start) {
            title = title.substring(start + 1, end);
        }
        return "忽略" + title;
    }
}
