package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class SvnHistoryQueryTask implements Callable<SvnHistoryQueryTaskParam> {

    private SvnHistoryQueryTaskParam svnHistoryQueryTaskParam;

    public SvnHistoryQueryTask(SvnHistoryQueryTaskParam svnHistoryQueryTaskParam) {
        this.svnHistoryQueryTaskParam = svnHistoryQueryTaskParam;
    }

    @Override
    public SvnHistoryQueryTaskParam call() throws Exception {
        svnHistoryQueryTaskParam.getSvnHistoryQueryController().stat();
        return null;
    }
}
