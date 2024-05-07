package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class DatabaseScriptTask implements Callable<DatabaseScriptTaskParam> {

    private DatabaseScriptTaskParam databaseScriptTaskParam;

    public DatabaseScriptTask(DatabaseScriptTaskParam databaseScriptTaskParam) {
        this.databaseScriptTaskParam = databaseScriptTaskParam;
    }

    @Override
    public DatabaseScriptTaskParam call() throws Exception {
        databaseScriptTaskParam.getDatabaseScriptController().executeUpdateScript();
        return null;
    }
}
