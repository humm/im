package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.extend.ScriptCompareSql;
import com.hoomoomoo.im.extend.ScriptRepairSql;
import com.hoomoomoo.im.extend.ScriptSqlUtils;
import com.hoomoomoo.im.extend.ScriptUpdateSql;
import com.hoomoomoo.im.task.ScriptCheckTask;
import com.hoomoomoo.im.task.ScriptCheckTaskParam;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE_EXTEND.REPAIR_EXT;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE_EXTEND.REPAIR_OLD_MENU;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.SCRIPT_CHECK;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.SYSTEM_TOOL;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class ScriptCheckController implements Initializable {

    @FXML
    private TextArea logs;

    private boolean executeFlag = false;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JvmCache.setScriptCheckController(this);
        String msg = String.format(BaseConst.MSG_USE, SCRIPT_CHECK.getName());
        LoggerUtils.info(msg);
        LoggerUtils.writeLogInfo(SCRIPT_CHECK.getCode(), new Date(), new ArrayList<String>(){{
            add(msg);
        }});
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
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_CHECK_MENU)));
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
    void skipExtLegal(ActionEvent event) throws Exception {
        String title = getTitle(LEGAL_EXT_MENU.getName());
        TaCommonUtils.openBlankChildStage(LEGAL_EXT_MENU.getIndex(), title);
        addLog(title);
    }

    @FXML
    void showCheckResult(ActionEvent event) {
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_CHECK_RESULT, "检查结果");
            addLog("检查结果");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg("请检查结果文件是否存在"));
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
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_LACK_EXT)));
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
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_WORK_FLOW)));
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
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_EXT)));
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
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_OLD_MENU)));
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
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_NEW_MENU)));
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
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_ERROR_EXT)));
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
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, KEY_UPDATE_MENU)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getUpdateMenuMsg(e.getMessage()));
        }
    }

    @FXML
    void updateChangeMenu(ActionEvent event) {
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, KEY_UPDATE_CHANGE_MENU)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getUpdateMenuMsg(e.getMessage()));
        }
    }

    public void doUpdateChangeMenu() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_OLD_TO_NEW_MENU,"执行开始"));
            new ScriptUpdateSql().generateChangeMenuSql();
            OutputUtils.info(logs, getCommonMsg(NAME_OLD_TO_NEW_MENU,"执行结束"));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_OLD_TO_NEW_MENU);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCommonMsg(NAME_OLD_TO_NEW_MENU, e.getMessage()));
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
        ScriptCheckTaskParam scriptCheckTaskParam = new ScriptCheckTaskParam(this, KEY_SCHEDULE);
        scriptCheckTaskParam.setStart(start);
        scriptCheckTaskParam.setFunctionName(functionName);
        scriptCheckTaskParam.setMsg(msg);
        TaskUtils.execute(new ScriptCheckTask(scriptCheckTaskParam));
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
