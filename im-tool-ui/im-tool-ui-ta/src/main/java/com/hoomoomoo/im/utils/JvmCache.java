package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.controller.*;


public class JvmCache {

    private static HepTodoController hepTodoController;
    private static SystemToolController systemToolController;
    private static ScriptUpdateController scriptUpdateController;
    private static TaStarterController taStarterController;
    private static ScriptCheckController scriptCheckController;

    public static ScriptCheckController getScriptCheckController() {
        if (scriptCheckController == null) {
            scriptCheckController = new ScriptCheckController();
        }
        return scriptCheckController;
    }

    public static void setScriptCheckController(ScriptCheckController checkController) {
        scriptCheckController = checkController;
    }

    public static HepTodoController getHepTodoController() {
        if (hepTodoController == null) {
            hepTodoController = new HepTodoController();
        }
        return hepTodoController;
    }

    public static void setHepTodoController(HepTodoController todoController) {
        hepTodoController = todoController;
    }

    public static SystemToolController getSystemToolController() {
        if (systemToolController == null) {
            systemToolController = new SystemToolController();
        }
        return systemToolController;
    }

    public static void setSystemToolController(SystemToolController toolController) {
        systemToolController = toolController;
    }

    public static ScriptUpdateController getScriptUpdateController() {
        if (scriptUpdateController == null) {
            scriptUpdateController = new ScriptUpdateController();
        }
        return scriptUpdateController;
    }

    public static void setScriptUpdateController(ScriptUpdateController updateController) {
        scriptUpdateController = updateController;
    }

    public static TaStarterController getTaStarterController() {
        if (taStarterController == null) {
            taStarterController = new TaStarterController();
        }
        return taStarterController;
    }

    public static void setTaStarterController(TaStarterController starterController) {
        taStarterController = starterController;
    }
}
