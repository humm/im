package com.hoomoomoo.im.task;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;

import java.util.concurrent.Callable;

public class ScriptUpdateTask implements Callable<ScriptUpdateTaskParam> {

    private ScriptUpdateTaskParam scriptUpdateTaskParam;

    public ScriptUpdateTask(ScriptUpdateTaskParam scriptUpdateTaskParam) {
        this.scriptUpdateTaskParam = scriptUpdateTaskParam;
    }

    @Override
    public ScriptUpdateTaskParam call() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        switch (scriptUpdateTaskParam.getTaskType()) {
            case "execute":
                scriptUpdateTaskParam.getScriptUpdateController().generateScript(appConfigDto);
                break;
            case "changeNewMenu":
                scriptUpdateTaskParam.getScriptUpdateController().buildSql(true, false);
                break;
            case "changeOldAllMenu":
                scriptUpdateTaskParam.getScriptUpdateController().buildSql(false, true);
                break;
            case "changeOldMenu":
                scriptUpdateTaskParam.getScriptUpdateController().buildSql(false, false);
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
        return null;
    }
}
