package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class BaseTask implements Callable<BaseTaskParam> {

    private BaseTaskParam baseTaskParam;

    public BaseTask(BaseTaskParam baseTaskParam) {
        this.baseTaskParam = baseTaskParam;
    }

    @Override
    public BaseTaskParam call() throws Exception {
        baseTaskParam.getBaseController().doUpdateProgress(baseTaskParam.getStep());
        return null;
    }
}
