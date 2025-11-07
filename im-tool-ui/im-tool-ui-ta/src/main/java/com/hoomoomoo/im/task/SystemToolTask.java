package com.hoomoomoo.im.task;

import com.hoomoomoo.im.utils.LoggerUtils;

import java.util.concurrent.Callable;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class SystemToolTask implements Callable<SystemToolTaskParam> {

    private SystemToolTaskParam systemToolTaskParam;

    public SystemToolTask(SystemToolTaskParam systemToolTaskParam) {
        this.systemToolTaskParam = systemToolTaskParam;
    }

    @Override
    public SystemToolTaskParam call() {
        try {
            switch (systemToolTaskParam.getTaskType()) {
                case NAME_SYNC_CODE:
                    systemToolTaskParam.getSystemToolController().doSyncCode();
                default:
                    new Exception("未匹配执行方法，请检查");
            }
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
        return null;
    }
}
