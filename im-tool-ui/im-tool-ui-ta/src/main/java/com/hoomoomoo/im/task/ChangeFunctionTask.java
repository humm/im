package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class ChangeFunctionTask implements Callable<CopyCodeTaskParam> {

    private ChangeFunctionTaskParam changeFunctionTaskParam;

    public ChangeFunctionTask(ChangeFunctionTaskParam changeFunctionTaskParam) {
        this.changeFunctionTaskParam = changeFunctionTaskParam;
    }

    @Override
    public CopyCodeTaskParam call() throws Exception {
        String functionType = changeFunctionTaskParam.getFunctionType();
        String taskType = changeFunctionTaskParam.getTaskType();
        switch (functionType) {
            case STR_1:
                changeFunctionTaskParam.getChangeToolController().buildAutoModeSql(taskType);
                break;
            case STR_2:
                changeFunctionTaskParam.getChangeToolController().buildMenuModeSql(taskType);
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
        return null;
    }
}
