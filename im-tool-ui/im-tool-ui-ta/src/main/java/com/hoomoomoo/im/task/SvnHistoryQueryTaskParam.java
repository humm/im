package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.SvnHistoryQueryController;
import lombok.Data;

@Data
public class SvnHistoryQueryTaskParam {

    private SvnHistoryQueryController svnHistoryQueryController;

    public SvnHistoryQueryTaskParam(SvnHistoryQueryController svnHistoryQueryController) {
        this.svnHistoryQueryController = svnHistoryQueryController;
    }
}
