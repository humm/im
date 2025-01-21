package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.SvnLogController;
import lombok.Data;

@Data
public class SvnLogTaskParam {

    private SvnLogController svnLogController;

    private String taskType;

    private int times;
    private String modifyNo;
    private boolean updateLog;
    private String type;

    public SvnLogTaskParam(SvnLogController svnLogController, String taskType) {
        this.svnLogController = svnLogController;
        this.taskType = taskType;
    }
}
