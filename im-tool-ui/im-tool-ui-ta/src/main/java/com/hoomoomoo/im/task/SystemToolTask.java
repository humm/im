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
            default:
                new Exception("未匹配执行方法，请检查");
        }
        return null;
    }
}
