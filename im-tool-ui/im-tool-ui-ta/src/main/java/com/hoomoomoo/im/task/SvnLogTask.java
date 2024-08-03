package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class SvnLogTask implements Callable<SvnLogTaskParam> {

    private SvnLogTaskParam svnLogTaskParam;

    public SvnLogTask(SvnLogTaskParam svnLogTaskParam) {
        this.svnLogTaskParam = svnLogTaskParam;
    }

    @Override
    public SvnLogTaskParam call() throws Exception {
        switch (svnLogTaskParam.getTaskType()) {
            case "query":
                svnLogTaskParam.getSvnLogController().getSvnLog(svnLogTaskParam.getTimes(), svnLogTaskParam.getVersion(),
                        svnLogTaskParam.getModifyNo(), svnLogTaskParam.isUpdateLog(), svnLogTaskParam.getType());
                break;
            case "execute":
                svnLogTaskParam.getSvnLogController().execute(svnLogTaskParam.isUpdateLog(), svnLogTaskParam.getType());
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
        return null;
    }
}
