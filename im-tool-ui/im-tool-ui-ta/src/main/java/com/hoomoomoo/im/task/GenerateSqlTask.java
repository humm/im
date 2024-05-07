package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class GenerateSqlTask implements Callable<GenerateSqlTaskParam> {

    private GenerateSqlTaskParam generateSqlTaskParam;

    public GenerateSqlTask(GenerateSqlTaskParam generateSqlTaskParam) {
        this.generateSqlTaskParam = generateSqlTaskParam;
    }

    @Override
    public GenerateSqlTaskParam call() throws Exception {
        generateSqlTaskParam.getGenerateSqlController().generateSql();
        return null;
    }
}
