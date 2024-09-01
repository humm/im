package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.controller.HepTodoController;
import com.hoomoomoo.im.controller.ScriptUpdateController;
import com.hoomoomoo.im.controller.SystemToolController;
import com.hoomoomoo.im.controller.TaStarterController;

import java.util.HashMap;
import java.util.Map;

public class JvmCache {

    private static HepTodoController hepTodoController;
    private static SystemToolController systemToolController;
    private static ScriptUpdateController scriptUpdateController;
    private static TaStarterController taStarterController;

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
