package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.GenerateSqlController;
import lombok.Data;

@Data
public class GenerateSqlTaskParam {

    private GenerateSqlController generateSqlController;

    public GenerateSqlTaskParam(GenerateSqlController generateSqlController) {
        this.generateSqlController = generateSqlController;
    }
}
