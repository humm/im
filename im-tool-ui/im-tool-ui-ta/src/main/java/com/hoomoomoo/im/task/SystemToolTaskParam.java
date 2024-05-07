package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.SystemToolController;
import lombok.Data;

@Data
public class SystemToolTaskParam {

    private SystemToolController systemToolController;

    private String taskType;

    private String functionName;

    private String msg;

    private long start;

    public SystemToolTaskParam(SystemToolController systemToolController, String taskType) {
        this.systemToolController = systemToolController;
        this.taskType = taskType;
    }
}
