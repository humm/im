package com.hoomoomoo.im.task;

import com.hoomoomoo.im.utils.CommonUtils;

import java.util.concurrent.Callable;

import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.CONFIG_SET;

public class ConfigSetTask implements Callable<ConfigSetTaskParam> {

    private ConfigSetTaskParam configSetTaskParam;

    public ConfigSetTask(ConfigSetTaskParam configSetTaskParam) {
        this.configSetTaskParam = configSetTaskParam;
    }

    @Override
    public ConfigSetTaskParam call() throws Exception {
        CommonUtils.closeTab(CONFIG_SET, true);
        return null;
    }
}
