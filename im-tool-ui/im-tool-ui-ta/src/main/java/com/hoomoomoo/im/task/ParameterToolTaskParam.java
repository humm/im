package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.ParameterToolController;
import lombok.Data;

@Data
public class ParameterToolTaskParam {

    private ParameterToolController parameterToolController;

    private String functionType;

    private String dictPath;

    private String paramPath;

    private String tablePath;

    public ParameterToolTaskParam(ParameterToolController parameterToolController, String functionType, String dictPath, String paramPath, String tablePath) {
        this.parameterToolController = parameterToolController;
        this.functionType = functionType;
        this.dictPath = dictPath;
        this.paramPath = paramPath;
        this.tablePath = tablePath;
    }
}
