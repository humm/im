package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class HepTodoTask implements Callable<HepTodoTaskParam> {

    private HepTodoTaskParam hepTodoTaskParam;

    public HepTodoTask(HepTodoTaskParam hepTodoTaskParam) {
        this.hepTodoTaskParam = hepTodoTaskParam;
    }

    @Override
    public HepTodoTaskParam call() throws Exception {
        switch (hepTodoTaskParam.getTaskType()) {
            case "check":
                hepTodoTaskParam.getHepTodoController().doScriptCheck();
                break;
            case "doQuery":
                hepTodoTaskParam.getHepTodoController().doQuery(hepTodoTaskParam.getEvent());
                break;
            case "doExecuteQuery":
                hepTodoTaskParam.getHepTodoController().doExecuteQuery();
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
        return null;
    }
}
