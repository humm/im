package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.SvnRealtimeStatController;
import lombok.Data;

@Data
public class SvnRealtimeStatTaskParam {

    private SvnRealtimeStatController svnRealtimeStatController;

    public SvnRealtimeStatTaskParam(SvnRealtimeStatController svnRealtimeStatController) {
        this.svnRealtimeStatController = svnRealtimeStatController;
    }
}
