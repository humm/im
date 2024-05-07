package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.BaseController;
import lombok.Data;

@Data
public class BaseTaskParam {

    private BaseController baseController;

    private double step;

    public BaseTaskParam(BaseController baseController) {
        this.baseController = baseController;
    }
}
