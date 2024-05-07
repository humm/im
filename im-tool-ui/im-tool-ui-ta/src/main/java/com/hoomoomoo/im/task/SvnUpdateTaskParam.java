package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.SvnUpdateController;
import lombok.Data;

@Data
public class SvnUpdateTaskParam {

    private SvnUpdateController svnUpdateController;

    public SvnUpdateTaskParam(SvnUpdateController svnUpdateController) {
        this.svnUpdateController = svnUpdateController;
    }
}
