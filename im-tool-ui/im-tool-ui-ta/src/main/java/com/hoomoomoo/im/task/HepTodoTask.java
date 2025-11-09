package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.HepTodoController;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.JvmCache;
import com.hoomoomoo.im.utils.LoggerUtils;
import javafx.application.Platform;

import java.util.Map;
import java.util.concurrent.Callable;

import static com.hoomoomoo.im.consts.BaseConst.*;

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
            case "doQuery":
                hepTodoTaskParam.getHepTodoController().doQuery(hepTodoTaskParam.getEvent());
                break;
            case "doExecuteQuery":
                hepTodoTaskParam.getHepTodoController().doExecuteQuery();
                break;
            case "doSyncFile":
                hepTodoTaskParam.getHepTodoController().doSyncFile();
                break;
            case "syncTaskInfo":
                try {
                    long start = System.currentTimeMillis();
                    hepTodoTaskParam.getHepTodoController().syncTask.setDisable(true);
                    JvmCache.getSystemToolController().executeSyncTaskInfo();
                    Map<String, HepTodoController> controllerMap = JvmCache.getHepTodoControllerMap();
                    for (HepTodoController hepTodoController : controllerMap.values()) {
                        hepTodoController.doExecuteQuery();
                    }
                    long end = System.currentTimeMillis();
                    Platform.runLater(() -> {
                        CommonUtils.showTipsByInfo(String.format("同步任务信息成功，耗时%s秒", (end - start) / 1000));
                    });
                } catch (Exception e) {
                    LoggerUtils.error(e);
                    Platform.runLater(() -> {
                        CommonUtils.showTipsByError(e.getMessage().replaceAll(SKIP_LOG_TIPS, STR_BLANK));
                    });
                } finally {
                    hepTodoTaskParam.getHepTodoController().syncTask.setDisable(false);
                }
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
        return null;
    }
}
