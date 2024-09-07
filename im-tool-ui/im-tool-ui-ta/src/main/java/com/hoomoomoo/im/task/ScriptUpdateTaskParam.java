package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.ScriptUpdateController;
import lombok.Data;

@Data
public class ScriptUpdateTaskParam {

    private ScriptUpdateController scriptUpdateController;

    private String taskType;
    public ScriptUpdateTaskParam(ScriptUpdateController scriptUpdateController, String taskType) {
        this.scriptUpdateController = scriptUpdateController;
        this.taskType = taskType;
    }
}
