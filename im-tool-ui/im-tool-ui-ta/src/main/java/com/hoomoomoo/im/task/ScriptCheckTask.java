package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class ScriptCheckTask implements Callable<ScriptCheckTaskParam> {

    private ScriptCheckTaskParam scriptCheckTaskParam;

    public ScriptCheckTask(ScriptCheckTaskParam scriptCheckTaskParam) {
        this.scriptCheckTaskParam = scriptCheckTaskParam;
    }

    @Override
    public ScriptCheckTaskParam call() throws Exception {
        switch (scriptCheckTaskParam.getTaskType()) {
            case NAME_CHECK_MENU:
                scriptCheckTaskParam.getScriptCheckController().doCheckMenu();
                break;
            case NAME_REPAIR_LACK_EXT:
                scriptCheckTaskParam.getScriptCheckController().doRepairLackLog();
                break;
            case NAME_REPAIR_WORK_FLOW:
                scriptCheckTaskParam.getScriptCheckController().doRepairWorkFlow();
                break;
            case NAME_REPAIR_OLD_MENU:
                scriptCheckTaskParam.getScriptCheckController().doRepairOldMenu();
                break;
            case NAME_REPAIR_NEW_MENU:
                scriptCheckTaskParam.getScriptCheckController().doRepairNewMenu();
                break;
            case NAME_REPAIR_NEW_MENU_TREE:
                scriptCheckTaskParam.getScriptCheckController().doRepairNewMenuTree();
                break;
            case NAME_REPAIR_ERROR_EXT:
                scriptCheckTaskParam.getScriptCheckController().doRepairErrorLog();
                break;
            case NAME_REPAIR_EXT:
                scriptCheckTaskParam.getScriptCheckController().doRepairExt();
                break;
            case KEY_UPDATE_MENU:
                scriptCheckTaskParam.getScriptCheckController().doUpdateMenu();
                break;
            case KEY_UPDATE_CHANGE_MENU:
                scriptCheckTaskParam.getScriptCheckController().doUpdateChangeMenu();
                break;
            case NAME_REPAIR_REPORT:
                scriptCheckTaskParam.getScriptCheckController().doRepairReport();
                break;
            case NAME_PARAMETER_DOC:
                scriptCheckTaskParam.getScriptCheckController().doUpdateParameterDoc();
                break;
            case NAME_REPAIR_ONE_KEY:
                scriptCheckTaskParam.getScriptCheckController().doRepairOneKey();
                break;
            case KEY_SCHEDULE:
                scriptCheckTaskParam.getScriptCheckController().doShowScheduleInfo(scriptCheckTaskParam.getFunctionName(), scriptCheckTaskParam.getMsg(), scriptCheckTaskParam.getStart());
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
        return null;
    }
}
