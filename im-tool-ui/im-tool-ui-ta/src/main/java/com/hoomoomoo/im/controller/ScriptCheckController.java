package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.extend.*;
import com.hoomoomoo.im.task.ScriptCheckTask;
import com.hoomoomoo.im.task.ScriptCheckTaskParam;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.net.URL;
import java.util.*;

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

    @FXML
    private Button showRepairOldMenuLogBtn;

    @FXML
    private Button showRepairNewMenuLogBtn;

    @FXML
    private Button repairWorkFlowLogBtn;

    @FXML
    private Button showUpdateParameterDocResultBtn;

    private boolean executeFlag = false;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JvmCache.setScriptCheckController(this);
        String msg = String.format(BaseConst.MSG_USE, SCRIPT_CHECK.getName());
        LoggerUtils.info(msg);
        LoggerUtils.writeLogInfo(SCRIPT_CHECK.getCode(), new Date(), Arrays.asList(msg));
    }

    @FXML
    void checkMenu(ActionEvent event) throws Exception {
        closeCheckResultStage();
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg(NAME_CHECK_MENU, "检查中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_CHECK_MENU)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_CHECK_MENU, e.getMessage()));
            executeFlag = false;
        }
    }

    public void doCheckMenu() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_CHECK_MENU, "检查开始"));
            showScheduleInfo(NAME_CHECK_MENU, "检查");
            new ScriptCompareSql().check();
            OutputUtils.info(logs, getCommonMsg(NAME_CHECK_MENU, "检查结束"));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_CHECK_MENU);
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_CHECK_MENU, e.getMessage()));
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
            String check = appConfigDto.getSystemToolCheckMenuFundBasePath() + ScriptSqlUtils.baseMenu.replace(".sql", ".check.sql");
            File checkFile = new File(check);
            if (!checkFile.exists()) {
                OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, "未找到错误日志文件"));
                return;
            }
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_REPAIR_OLD_MENU_LOG, "修正老版全量错误信息");
            addLog("查看修正老版全量错误信息");
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, "查看修正老版全量错误信息"));
        }
    }

    @FXML
    void showRepairNewMenuLog(ActionEvent event) throws Exception {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String check = appConfigDto.getSystemToolCheckMenuFundBasePath() + ScriptSqlUtils.newUedPage.replace(".sql", ".check.sql");
            File checkFile = new File(check);
            if (!checkFile.exists()) {
                OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, "未找到错误日志文件"));
                return;
            }
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_REPAIR_NEW_MENU_LOG, "修正新版全量错误信息");
            addLog("查看修正新版全量错误信息");
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, "查看修正新版全量错误信息"));
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
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_SHOW_RESULT, "请检查结果文件是否存在" + STR_NEXT_LINE));
        }
    }

    @FXML
    void repairLackLog(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_LACK_EXT)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LACK_EXT, e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
    void updateParameterDoc(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_PARAMETER_DOC)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_PARAMETER_DOC, e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
    void showUpdateParameterDocResult(ActionEvent event) throws Exception {
        try {
            new ParameterToolController().showParamRealtimeSetResultByFile(null);
            addLog("查看" + NAME_PARAMETER_DOC);
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, "查看修正老版全量错误信息"));
        }
    }

    public void doUpdateParameterDoc() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_PARAMETER_DOC, NAME_REPAIR_START));
            showScheduleInfo(NAME_PARAMETER_DOC, NAME_REPAIR);
            new ParameterToolController().updateParameterDoc(logs);
            OutputUtils.info(logs, getCommonMsg(NAME_PARAMETER_DOC, NAME_REPAIR_END));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_PARAMETER_DOC);
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_PARAMETER_DOC, e.getMessage()));
        } finally {
            executeFlag = false;
        }
    }

    public void doRepairLackLog() {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
            File errFile = new File(resultPath + "\\" + LACK_LOG.getFileName());
            if (!errFile.exists()) {
                OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LACK_EXT, "未找到缺少日志文件" + STR_NEXT_LINE));
                return;
            }
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LACK_EXT, NAME_REPAIR_START));
            showScheduleInfo(NAME_REPAIR_LACK_EXT, NAME_REPAIR);
            ScriptRepairSql.repairLackLog();
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LACK_EXT, NAME_REPAIR_END));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_REPAIR_LACK_EXT);
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_LACK_EXT, e.getMessage()));
        } finally {
            executeFlag = false;
        }
    }

    @FXML
    void repairWorkFlow(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_WORK_FLOW)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
    void repairWorkFlowLog(ActionEvent event) throws Exception {
        try {
            if (executeFlag) {
                OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
                return;
            }
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String check = appConfigDto.getSystemToolCheckMenuFundBasePath() + ScriptSqlUtils.workFlow.replace(".sql", ".check.sql");
            File checkFile = new File(check);
            if (!checkFile.exists()) {
                OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, "未找到错误日志文件"));
                return;
            }
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_REPAIR_WORK_FLOW_LOG, "修正复核信息错误信息");
            addLog("查看修正复核信息错误信息");
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, "查看修正新版全量错误信息"));
        }
    }

    public void doRepairWorkFlow() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, NAME_REPAIR_START));
            executeFlag = true;
            showScheduleInfo(NAME_REPAIR_WORK_FLOW, NAME_REPAIR);
            ScriptRepairSql.repairWorkFlow();
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_WORK_FLOW, NAME_REPAIR_END));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_REPAIR_WORK_FLOW);
        } catch (Exception e) {
            LoggerUtils.error(e);
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
            OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_EXT)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_EXT, e.getMessage()));
            executeFlag = false;
        }
    }

    public void doRepairExt() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_EXT, NAME_REPAIR_START));
            showScheduleInfo(NAME_REPAIR_EXT, NAME_REPAIR);
            ScriptRepairSql.repairExt();
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_EXT, NAME_REPAIR_END));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_REPAIR_EXT);
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_EXT, e.getMessage()));
        } finally {
            executeFlag = false;
        }
    }

    @FXML
    void repairOneKey(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
            return;
        }
        showRepairOldMenuLogBtn.setStyle(STYLE_NORMAL_FOR_BUTTON);
        showRepairNewMenuLogBtn.setStyle(STYLE_NORMAL_FOR_BUTTON);
        repairWorkFlowLogBtn.setStyle(STYLE_NORMAL_FOR_BUTTON);
        showUpdateParameterDocResultBtn.setStyle(STYLE_NORMAL_FOR_BUTTON);
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_ONE_KEY)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ONE_KEY, e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
    void repairOldMenu(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_OLD_MENU)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, e.getMessage()));
            executeFlag = false;
        }
    }

    public void doRepairOneKey() {
        try {
            repairOldMenu(null);
            repair("repairNewMenu");
            repair("repairWorkFlow");
            repair("repairExt");
            repair("updateChangeMenu");
            repair("repairReport");
            repair("repairLackLog");
            repair("updateParameterDoc");
            addLog(NAME_REPAIR_ONE_KEY);
            while (true) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                List<String> repairErrorInfo = appConfigDto.getRepairErrorInfo();
                if (repairErrorInfo.contains(NAME_PARAMETER_DOC) || repairErrorInfo.contains(NAME_PARAMETER_DOC_SUCCESS)) {
                    if (repairErrorInfo.contains(NAME_REPAIR_OLD_MENU)) {
                        showRepairOldMenuLogBtn.setStyle(STYLE_RED_FOR_BUTTON);
                    }
                    if (repairErrorInfo.contains(NAME_REPAIR_NEW_MENU)) {
                        showRepairNewMenuLogBtn.setStyle(STYLE_RED_FOR_BUTTON);
                    }
                    if (repairErrorInfo.contains(NAME_REPAIR_WORK_FLOW)) {
                        repairWorkFlowLogBtn.setStyle(STYLE_RED_FOR_BUTTON);
                    }
                    if (repairErrorInfo.contains(NAME_PARAMETER_DOC)) {
                        showUpdateParameterDocResultBtn.setStyle(STYLE_RED_FOR_BUTTON);
                    }
                    break;
                }
                Thread.sleep(3 * 1000);
            }
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ONE_KEY, e.getMessage()));
        }
    }

    public void repair(String repairType) {
        try {
            while (true) {
                if (executeFlag) {
                    Thread.sleep(1 * 1000);
                    continue;
                }
                switch (repairType) {
                    case "repairNewMenu":
                        repairNewMenu(null);
                        break;
                    case "repairWorkFlow":
                        repairWorkFlow(null);
                        break;
                    case "repairExt":
                        repairExt(null);
                        break;
                    case "updateChangeMenu":
                        updateChangeMenu(null);
                        break;
                    case "repairReport":
                        repairReport(null);
                        break;
                    case "repairLackLog":
                        repairLackLog(null);
                        break;
                    case "updateParameterDoc":
                        updateParameterDoc(null);
                        break;
                    default:
                        break;
                }
                break;
            }
        } catch (Exception e) {
            executeFlag = false;
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ONE_KEY, e.getMessage()));
        }

    }

    public void doRepairOldMenu() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, NAME_REPAIR_START));
            showScheduleInfo(NAME_REPAIR_OLD_MENU, NAME_REPAIR);
            ScriptRepairSql.repairOldMenu();
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, NAME_REPAIR_END));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_REPAIR_OLD_MENU);
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_OLD_MENU, e.getMessage()));
        } finally {
            executeFlag = false;
        }
    }

    @FXML
    void repairNewMenu(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_NEW_MENU)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, e.getMessage()));
            executeFlag = false;
        }
    }

    public void doRepairNewMenu() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, NAME_REPAIR_START));
            showScheduleInfo(NAME_REPAIR_NEW_MENU, NAME_REPAIR);
            ScriptRepairSql.repairNewMenu();
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, NAME_REPAIR_END));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_REPAIR_NEW_MENU);
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU, e.getMessage()));
        } finally {
            executeFlag = false;
        }
    }

    @FXML
    void repairErrorLog(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
            return;
        }
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
        File errFile = new File(resultPath + "\\" + ERROR_LOG.getFileName());
        if (!errFile.exists()) {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, "未找到错误日志文件"));
            return;
        }
        if (appConfigDto.getExecute()) {
            OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
            return;
        }
        TaCommonUtils.openBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_REPAIR_ERROR_LOG, NAME_REPAIR_ERROR_EXT);
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_ERROR_EXT)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, e.getMessage()));
        }
    }

    @FXML
    void repairNewMenuTree(ActionEvent event) throws Exception {
        if (executeFlag) {
            OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
            return;
        }
        executeFlag = true;
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_NEW_MENU_TREE)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU_TREE, e.getMessage()));
            executeFlag = false;
        }
    }

   public void doRepairNewMenuTree() throws Exception {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU_TREE, NAME_REPAIR_START));
            showScheduleInfo(NAME_REPAIR_NEW_MENU_TREE, NAME_REPAIR);
            ScriptRepairSql.repairNewMenuTree();
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU_TREE, NAME_REPAIR_END));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_REPAIR_NEW_MENU_TREE);
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_NEW_MENU_TREE, e.getMessage()));
        } finally {
            executeFlag = false;
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
                    OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, NAME_REPAIR_START));
                    showScheduleInfo(NAME_REPAIR_ERROR_EXT, NAME_REPAIR);
                    executeFlag = true;
                }
                if (executeFlag && startFlag && !execute) {
                    OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_ERROR_EXT, NAME_REPAIR_END));
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
            LoggerUtils.error(e);
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
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_SHOW_RESULT, "请检查结果文件是否不存在" + STR_NEXT_LINE));
        }
    }

    @FXML
    void updateMenu(ActionEvent event) {
        closeCheckResultStage();
        try {
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, KEY_UPDATE_MENU)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(KEY_UPDATE_MENU, e.getMessage()));
        }
    }

    @FXML
    void updateChangeMenu(ActionEvent event) {
        try {
            if (executeFlag) {
                OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
                return;
            }
            executeFlag = true;
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, KEY_UPDATE_CHANGE_MENU)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(KEY_UPDATE_CHANGE_MENU, e.getMessage()));
            executeFlag = false;
        }
    }

    @FXML
    void repairReport(ActionEvent event) {
        try {
            if (executeFlag) {
                OutputUtils.info(logs, getCommonMsg("修正中 ··· 请稍后 ···"));
                return;
            }
            executeFlag = true;
            TaskUtils.execute(new ScriptCheckTask(new ScriptCheckTaskParam(this, NAME_REPAIR_REPORT)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_REPORT, e.getMessage()));
            executeFlag = false;
        }
    }

    public void doUpdateChangeMenu() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_OLD_TO_NEW_MENU,"执行开始"));
            showScheduleInfo(NAME_OLD_TO_NEW_MENU, NAME_REPAIR);
            new ScriptUpdateSql().generateChangeMenuSql();
            OutputUtils.info(logs, getCommonMsg(NAME_OLD_TO_NEW_MENU,"执行结束"));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_OLD_TO_NEW_MENU);
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_OLD_TO_NEW_MENU, e.getMessage()));
        } finally {
            executeFlag = false;
        }
    }

    public void doRepairReport() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_REPORT,"执行开始"));
            showScheduleInfo(NAME_REPAIR_REPORT, NAME_REPAIR);
            new ReportRepairSql().repair(ConfigCache.getAppConfigDtoCache());
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_REPORT,"执行结束"));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog(NAME_REPAIR_REPORT);
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_REPAIR_REPORT, e.getMessage()));
        } finally {
            executeFlag = false;
        }
    }

    public void doUpdateMenu() {
        try {
            OutputUtils.info(logs, getCommonMsg(NAME_UPDATE_MENU, "执行开始"));
            new ScriptUpdateSql().generateSql();
            OutputUtils.info(logs, getCommonMsg(NAME_UPDATE_MENU, "执行结束"));
            OutputUtils.info(logs, STR_NEXT_LINE);
            addLog("菜单升级脚本");
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, getCommonMsg(NAME_UPDATE_MENU, e.getMessage()));
        }
    }

    public static String getCommonMsg (String functionName, String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ functionName + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    public static String getCommonMsg (String msg) {
        return TaCommonUtils.getMsgContainDate(STR_BLANK) + STR_SPACE + msg + STR_NEXT_LINE;
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
            LoggerUtils.error(e);
            LoggerUtils.error(getCommonMsg(NAME_SHOW_RESULT, e.getMessage()));
        }
    }

    public void showScheduleInfo(String functionName, String msg) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        appConfigDto.setRepairSchedule(STR_BLANK);
        appConfigDto.setRepairFunctionName(functionName);
        long start = System.currentTimeMillis();
        ScriptCheckTaskParam scriptCheckTaskParam = new ScriptCheckTaskParam(this, KEY_SCHEDULE);
        scriptCheckTaskParam.setStart(start);
        scriptCheckTaskParam.setFunctionName(functionName);
        scriptCheckTaskParam.setMsg(msg);
        TaskUtils.execute(new ScriptCheckTask(scriptCheckTaskParam));
    }

    public void doShowScheduleInfo(String functionName, String msg, long start) throws Exception {
        while (true) {
            String repairFunctionName = ConfigCache.getAppConfigDtoCache().getRepairFunctionName();
            if (!StringUtils.equals(repairFunctionName, functionName)) {
                break;
            }
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
            Thread.sleep(3000);
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
