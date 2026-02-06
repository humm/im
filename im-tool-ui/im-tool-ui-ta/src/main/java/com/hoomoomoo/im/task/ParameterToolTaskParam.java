package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.ParameterToolController;
import lombok.Data;

@Data
public class ParameterToolTaskParam {

    private ParameterToolController parameterToolController;

    private String functionType;

    public ParameterToolTaskParam(ParameterToolController parameterToolController, String functionType) {
        this.parameterToolController = parameterToolController;
        this.functionType = functionType;
    }
}
