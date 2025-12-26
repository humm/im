package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class ParameterToolTask implements Callable<CopyCodeTaskParam> {

    private ParameterToolTaskParam parameterToolTaskParam;

    public ParameterToolTask(ParameterToolTaskParam parameterToolTaskParam) {
        this.parameterToolTaskParam = parameterToolTaskParam;
    }

    @Override
    public CopyCodeTaskParam call() throws Exception {
        String functionType = parameterToolTaskParam.getFunctionType();
        switch (functionType) {
            case STR_3:
                parameterToolTaskParam.getParameterToolController().executeRealtimeExe(parameterToolTaskParam.getDictPath(), parameterToolTaskParam.getParamPath(), parameterToolTaskParam.getTablePath());
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
        return null;
    }
}
