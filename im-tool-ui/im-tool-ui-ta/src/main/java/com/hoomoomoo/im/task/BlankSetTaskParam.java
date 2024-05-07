package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.BlankSetController;
import lombok.Data;

@Data
public class BlankSetTaskParam {

    private BlankSetController blankSetController;

    public BlankSetTaskParam(BlankSetController blankSetController) {
        this.blankSetController = blankSetController;
    }
}
