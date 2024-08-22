package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.controller.HepTodoController;

public class JvmCache {

    private static HepTodoController hepTodoController;

    public static HepTodoController getHepTodoController() {
        return hepTodoController;
    }

    public static void setHepTodoController(HepTodoController todoController) {
        hepTodoController = todoController;
    }

}
