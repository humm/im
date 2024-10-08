package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.ChangeToolController;
import lombok.Data;

@Data
public class ChangeFunctionTaskParam {

    private ChangeToolController changeToolController;

    private String taskType;

    private String functionType;

    public ChangeFunctionTaskParam(ChangeToolController changeToolController, String functionType, String taskType) {
        this.changeToolController = changeToolController;
        this.functionType = functionType;
        this.taskType = taskType;
    }
}
