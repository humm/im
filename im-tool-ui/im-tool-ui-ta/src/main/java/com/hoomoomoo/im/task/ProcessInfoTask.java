package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class ProcessInfoTask implements Callable<ProcessInfoTaskParam> {

    private ProcessInfoTaskParam processInfoTaskParam;

    public ProcessInfoTask(ProcessInfoTaskParam processInfoTaskParam) {
        this.processInfoTaskParam = processInfoTaskParam;
    }

    @Override
    public ProcessInfoTaskParam call() throws Exception {
        processInfoTaskParam.getProcessInfoController().generateScript();
        return null;
    }
}
