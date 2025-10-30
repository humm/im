package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.ChangeToolController;
import lombok.Data;

@Data
public class ChangeFunctionTaskParam {

    private ChangeToolController changeToolController;

    private String taskType;

    private String functionType;

    private String dictPath;

    private String paramPath;

    private String tablePath;

    public ChangeFunctionTaskParam(ChangeToolController changeToolController, String functionType, String taskType) {
        this.changeToolController = changeToolController;
        this.functionType = functionType;
        this.taskType = taskType;
    }

    public ChangeFunctionTaskParam(ChangeToolController changeToolController, String functionType, String dictPath, String paramPath, String tablePath) {
        this.changeToolController = changeToolController;
        this.functionType = functionType;
        this.dictPath = dictPath;
        this.paramPath = paramPath;
        this.tablePath = tablePath;
    }
}
