package com.hoomoomoo.im.task;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.JvmCache;
import com.hoomoomoo.im.utils.LoggerUtils;
import javafx.application.Platform;

import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

import static com.hoomoomoo.im.consts.BaseConst.NAME_NO_AUTH;

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
                    Platform.runLater(() -> {
                        CommonUtils.showTipsByInfo("异步同步中...请稍后...", 3 * 1000);
                    });
                    JvmCache.getSystemToolController().syncTask(ConfigCache.getAppConfigDtoCache());
                    hepTodoTaskParam.getHepTodoController().doExecuteQuery();
                    Platform.runLater(() -> {
                        CommonUtils.showTipsByInfo("同步任务信息成功", 30 * 1000);
                    });
                } catch (Exception e) {
                    LoggerUtils.info(e);
                    Platform.runLater(() -> {
                        String msg = e.getMessage();
                        if (msg.contains("拒绝访问")) {
                            msg = NAME_NO_AUTH;
                        }
                        CommonUtils.showTipsByError(msg, 5 * 1000);
                    });
                }
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
        return null;
    }
}
