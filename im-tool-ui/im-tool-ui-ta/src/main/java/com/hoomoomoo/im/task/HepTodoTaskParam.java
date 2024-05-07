package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.HepTodoController;
import javafx.event.ActionEvent;
import lombok.Data;

@Data
public class HepTodoTaskParam {

    private HepTodoController hepTodoController;

    private String taskType;

    private ActionEvent event;

    public HepTodoTaskParam(HepTodoController hepTodoController, String taskType) {
        this.hepTodoController = hepTodoController;
        this.taskType = taskType;
    }
}
