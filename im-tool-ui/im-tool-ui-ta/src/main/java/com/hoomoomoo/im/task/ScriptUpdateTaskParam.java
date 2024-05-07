package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.ScriptUpdateController;
import lombok.Data;

@Data
public class ScriptUpdateTaskParam {

    private ScriptUpdateController scriptUpdateController;

    public ScriptUpdateTaskParam(ScriptUpdateController scriptUpdateController) {
        this.scriptUpdateController = scriptUpdateController;
    }
}
