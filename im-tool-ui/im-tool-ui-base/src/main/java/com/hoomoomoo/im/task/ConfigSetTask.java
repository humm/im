package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class ConfigSetTask implements Callable<ConfigSetTaskParam> {

    private ConfigSetTaskParam configSetTaskParam;

    public ConfigSetTask(ConfigSetTaskParam configSetTaskParam) {
        this.configSetTaskParam = configSetTaskParam;
    }

    @Override
    public ConfigSetTaskParam call() throws Exception {
        configSetTaskParam.getConfigSetController().doClose();
        return null;
    }
}
