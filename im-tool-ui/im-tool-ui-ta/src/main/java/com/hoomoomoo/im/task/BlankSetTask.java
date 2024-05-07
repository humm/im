package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class BlankSetTask implements Callable<BlankSetTaskParam> {

    private BlankSetTaskParam blankSetTaskParam;

    public BlankSetTask(BlankSetTaskParam blankSetTaskParam) {
        this.blankSetTaskParam = blankSetTaskParam;
    }

    @Override
    public BlankSetTaskParam call() throws Exception {
        blankSetTaskParam.getBlankSetController().repairErrorLog();
        return null;
    }
}
