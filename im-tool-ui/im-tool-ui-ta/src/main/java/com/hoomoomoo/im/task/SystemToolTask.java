package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class SystemToolTask implements Callable<SystemToolTaskParam> {

    private SystemToolTaskParam systemToolTaskParam;

    public SystemToolTask(SystemToolTaskParam systemToolTaskParam) {
        this.systemToolTaskParam = systemToolTaskParam;
    }

    @Override
    public SystemToolTaskParam call() throws Exception {
        switch (systemToolTaskParam.getTaskType()) {
            case NAME_CHECK_MENU:
                systemToolTaskParam.getSystemToolController().doCheckMenu();
                break;
            case NAME_REPAIR_LACK_EXT:
                systemToolTaskParam.getSystemToolController().doRepairLackLog();
                break;
            case NAME_REPAIR_WORK_FLOW:
                systemToolTaskParam.getSystemToolController().doRepairWorkFlow();
                break;
            case NAME_REPAIR_OLD_MENU:
                systemToolTaskParam.getSystemToolController().doRepairOldMenu();
                break;
            case NAME_REPAIR_NEW_MENU:
                systemToolTaskParam.getSystemToolController().doRepairNewMenu();
                break;
            case NAME_REPAIR_ERROR_EXT:
                systemToolTaskParam.getSystemToolController().doRepairErrorLog();
                break;
            case NAME_REPAIR_EXT:
                systemToolTaskParam.getSystemToolController().doRepairExt();
                break;
            case "updateMenu":
                systemToolTaskParam.getSystemToolController().doUpdateMenu();
                break;
            case "schedule":
                systemToolTaskParam.getSystemToolController().doShowScheduleInfo(systemToolTaskParam.getFunctionName(), systemToolTaskParam.getMsg(), systemToolTaskParam.getStart());
                break;
            default:
                new Exception("未匹配执行方法，清检查");
        }
        return null;
    }
}
