package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class CopyCodeTask  implements Callable<CopyCodeTaskParam> {

    private CopyCodeTaskParam copyCodeTaskParam;

    public CopyCodeTask(CopyCodeTaskParam copyCodeTaskParam) {
        this.copyCodeTaskParam = copyCodeTaskParam;
    }

    @Override
    public CopyCodeTaskParam call() throws Exception {
        copyCodeTaskParam.getCopyCodeController().copyCode();
        return null;
    }
}
