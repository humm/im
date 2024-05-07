package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.ProcessInfoController;
import lombok.Data;

@Data
public class ProcessInfoTaskParam {

    private ProcessInfoController processInfoController;

    public ProcessInfoTaskParam(ProcessInfoController processInfoController) {
        this.processInfoController = processInfoController;
    }
}
