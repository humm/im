package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.ConfigSetController;
import lombok.Data;

@Data
public class ConfigSetTaskParam {

    private ConfigSetController configSetController;

    public ConfigSetTaskParam(ConfigSetController configSetController) {
        this.configSetController = configSetController;
    }
}
