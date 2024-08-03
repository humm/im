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
            case "query":
                hepTodoTaskParam.getHepTodoController().query(hepTodoTaskParam.getEvent());
                break;
            case "doQuery":
                hepTodoTaskParam.getHepTodoController().doExecuteQuery();
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
        return null;
    }
}
