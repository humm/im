package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.extend.ScriptCompareSql;
import com.hoomoomoo.im.extend.ScriptRepairSql;
import com.hoomoomoo.im.extend.ScriptSqlUtils;
import com.hoomoomoo.im.extend.ScriptUpdateSql;
import com.hoomoomoo.im.task.SystemToolTask;
import com.hoomoomoo.im.task.SystemToolTaskParam;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE_EXTEND.REPAIR_EXT;
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
            new HepTodoController().doShowVersion();
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
            TaskUtils.execute(new SystemToolTask(new SystemToolTaskParam(this, NAME_CHECK_MENU)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg(e.getMessage()));
            executeFlag = false;
        }
    }

    public void doCheckMenu() {
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
    void showRepairOldMenuLog(ActionEvent event) throws Exception {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String check = appConfigDto.getSystemToolCheckMenuBasePath() + ScriptSqlUtils.baseMenu.replace(".sql", ".check.sql");
            File checkFile = new File(check);
            if (!checkFile.exists()) {
                OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, "未找到错误日志文件"));
                return;
            }
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_REPAIR_OLD_MENU_LOG, "修正老版全量错误信息");
            addLog("查看修正老版全量错误信息");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg("查看修正老版全量错误信息"));
        }
    }

    @FXML
    void showRepairNewMenuLog(ActionEvent event) throws Exception {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String check = appConfigDto.getSystemToolCheckMenuBasePath() + ScriptSqlUtils.newUedPage.replace(".sql", ".check.sql");
            File checkFile = new File(check);
            if (!checkFile.exists()) {
                OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, "未找到错误日志文件"));
                return;
            }
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_REPAIR_NEW_MENU_LOG, "修正新版全量错误信息");
            addLog("查看修正新版全量错误信息");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg("查看修正新版全量错误信息"));
        }
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
            addLog("清除系统日志");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg("清除系统日志错误"));
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
            TaskUtils.execute(new SystemToolTask(new SystemToolTaskParam(this, NAME_REPAIR_LACK_EXT)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getRepairLackExt(e.getMessage()));
            executeFlag = false;
        }
    }

    public void doRepairLackLog() {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
            File errFile = new File(resultPath + "\\" + LACK_LOG.getFileName());
            if (!errFile.exists()) {
                OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LACK_EXT, "未找到缺少日志文件"));
                return;
            }
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

    @FXML
    void repairWorkFlow(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, "修复中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        ConfigCache.getAppConfigDtoCache().setRepairSchedule(STR_BLANK);
        try {
            TaskUtils.execute(new SystemToolTask(new SystemToolTaskParam(this, NAME_REPAIR_WORK_FLOW)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
    void repairWorkFlowLog(ActionEvent event) throws Exception {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String check = appConfigDto.getSystemToolCheckMenuBasePath() + ScriptSqlUtils.workFlow.replace(".sql", ".check.sql");
            File checkFile = new File(check);
            if (!checkFile.exists()) {
                OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, "未找到错误日志文件"));
                return;
            }
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_REPAIR_WORK_FLOW_LOG, "修正复核信息错误信息");
            addLog("查看修正复核信息错误信息");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg("查看修正新版全量错误信息"));
        }
    }

    public void doRepairWorkFlow() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, "修复开始"));
            showScheduleInfo(NAME_REPAIR_WORK_FLOW, "修复");
            ScriptRepairSql.repairWorkFlow();
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, "修复结束"));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_REPAIR_WORK_FLOW);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, e.getMessage()));
        } finally {
            executeFlag = false;
        }
    }

    @FXML
    void skipRepairExt(ActionEvent event) throws Exception {
        String title = getTitle(REPAIR_EXT.getName());
        TaCommonUtils.openBlankChildStage(REPAIR_EXT.getIndex(), title);
        addLog(title);
    }

    @FXML
    void repairExt(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_EXT, "修复中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        ConfigCache.getAppConfigDtoCache().setRepairSchedule(STR_BLANK);
        try {
            TaskUtils.execute(new SystemToolTask(new SystemToolTaskParam(this, NAME_REPAIR_EXT)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_EXT, e.getMessage()));
            executeFlag = false;
        }
    }

    public void doRepairExt() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_EXT, "修复开始"));
            showScheduleInfo(NAME_REPAIR_EXT, "修复");
            ScriptRepairSql.repairExt();
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_EXT, "修复结束"));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_REPAIR_EXT);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_EXT, e.getMessage()));
        } finally {
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
            TaskUtils.execute(new SystemToolTask(new SystemToolTaskParam(this, NAME_REPAIR_OLD_MENU)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, e.getMessage()));
            executeFlag = false;
        }
    }

    public void doRepairOldMenu() {
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

    @FXML
    void repairNewMenu(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, "修复中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        ConfigCache.getAppConfigDtoCache().setRepairSchedule(STR_BLANK);
        try {
            TaskUtils.execute(new SystemToolTask(new SystemToolTaskParam(this, NAME_REPAIR_NEW_MENU)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, e.getMessage()));
            executeFlag = false;
        }
    }

    public void doRepairNewMenu() {
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

    @FXML
     void repairErrorLog(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
        File errFile = new File(resultPath + "\\" + ERROR_LOG.getFileName());
        if (!errFile.exists()) {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, "未找到错误日志文件"));
            return;
        }
        if (appConfigDto.getExecute()) {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, "修复中 ··· 请稍后 ···"));
            return;
        }
        TaCommonUtils.openBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_REPAIR_ERROR_LOG, NAME_REPAIR_ERROR_EXT);
        ConfigCache.getAppConfigDtoCache().setRepairSchedule(STR_BLANK);
        try {
            TaskUtils.execute(new SystemToolTask(new SystemToolTaskParam(this, NAME_REPAIR_ERROR_EXT)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, e.getMessage()));
        }
    }

    public void doRepairErrorLog() {
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
            TaskUtils.execute(new SystemToolTask(new SystemToolTaskParam(this, "updateMenu")));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getUpdateMenuMsg(e.getMessage()));
        }
    }

    public void doUpdateMenu() {
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

    private String formatDate(String date) {
        return StringUtils.isBlank(date) ? STR_0 : date.replaceAll(STR_HYPHEN, STR_BLANK);
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


                File file = new File(filePath);
                if (file.isDirectory()) {
                    List<File> files = Arrays.asList(file.listFiles());
                    if (CollectionUtils.isNotEmpty(files)) {
                        files = files.stream().filter(ele -> ele.getName().contains("版本列表")).collect(Collectors.toList());
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
                }
                fileInputStream = getVersionFile(null, filePath, 0);
                HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
                HSSFSheet sheet = workbook.getSheetAt(0);
                int rows = sheet.getLastRowNum();
                for (int i = 1; i <= rows; i++) {
                    StringBuilder item = new StringBuilder();
                    HSSFRow row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    String version = row.getCell(1).toString();
                    if (StringUtils.isBlank(version)) {
                        continue;
                    }
                    String closeDate = formatDate(row.getCell(2).toString());
                    String publishDate = formatDate(row.getCell(3).toString());
                    if (StringUtils.isBlank(closeDate)) {
                        closeDate = publishDate;
                    }
                    String customer = STR_SPACE;
                    item.append(version).append(STR_SEMICOLON).append(closeDate).append(STR_SEMICOLON).append(publishDate)
                            .append(STR_SEMICOLON).append(customer).append(STR_SEMICOLON);
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
                LoggerUtils.info(e.getMessage());
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

    private static FileInputStream getVersionFile(FileInputStream fileInputStream, String filePath, int times) throws FileNotFoundException {
        try {
            fileInputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            if (e.getMessage().contains("拒绝访问")) {
                times++;
                if (times <= 3) {
                    getVersionFile(fileInputStream, filePath, times);
                }
                throw new FileNotFoundException("权限不够,请重试");
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

    public void showScheduleInfo(String functionName, String msg) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        SystemToolTaskParam systemToolTaskParam = new SystemToolTaskParam(this, "schedule");
        systemToolTaskParam.setStart(start);
        systemToolTaskParam.setFunctionName(functionName);
        systemToolTaskParam.setMsg(msg);
        TaskUtils.execute(new SystemToolTask(systemToolTaskParam));
    }

    public void doShowScheduleInfo(String functionName, String msg, long start) throws Exception {
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
