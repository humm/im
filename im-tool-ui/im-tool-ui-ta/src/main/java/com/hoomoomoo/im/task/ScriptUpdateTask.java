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
        scriptUpdateTaskParam.getScriptUpdateController().generateScript(appConfigDto);
        return null;
    }
}
