package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.DatabaseScriptController;
import lombok.Data;

@Data
public class DatabaseScriptTaskParam {

    private DatabaseScriptController databaseScriptController;

    public DatabaseScriptTaskParam(DatabaseScriptController databaseScriptController) {
        this.databaseScriptController = databaseScriptController;
    }
}

