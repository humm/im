package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class SvnRealtimeStatTask implements Callable<SvnRealtimeStatTaskParam> {

    private SvnRealtimeStatTaskParam svnRealtimeStatTaskParam;

    public SvnRealtimeStatTask(SvnRealtimeStatTaskParam svnRealtimeStatTaskParam) {
        this.svnRealtimeStatTaskParam = svnRealtimeStatTaskParam;
    }

    @Override
    public SvnRealtimeStatTaskParam call() throws Exception {
        svnRealtimeStatTaskParam.getSvnRealtimeStatController().stat();
        return null;
    }
}
