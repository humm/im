package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.ScriptCheckController;
import lombok.Data;

@Data
public class ScriptCheckTaskParam {

    private ScriptCheckController scriptCheckController;

    private String taskType;

    private String functionName;

    private String msg;

    private long start;

    public ScriptCheckTaskParam(ScriptCheckController scriptCheckController, String taskType) {
        this.scriptCheckController = scriptCheckController;
        this.taskType = taskType;
    }
}
