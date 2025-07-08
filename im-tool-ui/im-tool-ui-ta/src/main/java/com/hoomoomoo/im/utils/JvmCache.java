package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.controller.*;

import java.util.LinkedHashMap;
import java.util.Map;


public class JvmCache {

    private static Map<String, HepTodoController> hepTodoControllerMap = new LinkedHashMap<>();
    private static HepTodoController hepTodoController;
    private static HepTodoController activeHepTodoController;
    private static SystemToolController systemToolController;
    private static ScriptUpdateController scriptUpdateController;
    private static ScriptCheckController scriptCheckController;
    private static TaStarterController taStarterController;

    public static void setHepTodoControllerMap(String tabCode, HepTodoController hepTodoController) {
        hepTodoControllerMap.put(tabCode, hepTodoController);
    }

    public static Map<String, HepTodoController> getHepTodoControllerMap() {
        return hepTodoControllerMap;
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

    public static HepTodoController getActiveHepTodoController() {
            if (activeHepTodoController == null) {
                activeHepTodoController = new HepTodoController();
            }
            return activeHepTodoController;
        }

    public static void setActiveHepTodoController(HepTodoController todoController) {
        activeHepTodoController = todoController;
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

    public static ScriptCheckController getScriptCheckController() {
        if (scriptCheckController == null) {
            scriptCheckController = new ScriptCheckController();
        }
        return scriptCheckController;
    }

    public static void setScriptCheckController(ScriptCheckController checkController) {
        scriptCheckController = checkController;
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
