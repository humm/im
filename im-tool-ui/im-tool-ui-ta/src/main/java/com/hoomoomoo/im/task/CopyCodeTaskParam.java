package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.CopyCodeController;
import lombok.Data;

@Data
public class CopyCodeTaskParam {
    private CopyCodeController copyCodeController;

    public CopyCodeTaskParam(CopyCodeController copyCodeController) {
        this.copyCodeController = copyCodeController;
    }
}
