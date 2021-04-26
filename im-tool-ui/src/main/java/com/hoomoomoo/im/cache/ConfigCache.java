package com.hoomoomoo.im.cache;

import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import lombok.Data;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.cache
 * @date 2021/04/24
 */
@Data
public class ConfigCache {

    private static ConfigCache configCache = null;

    private AppConfigDto appConfigDto;

    public AppConfigDto getAppConfig() {
        return appConfigDto;
    }

    public static ConfigCache getConfigCache() throws Exception {
        if (configCache == null) {
            configCache = new ConfigCache();
        }
        return configCache;
    }

    private ConfigCache() throws Exception {
        init();
    }

    private void init() throws Exception {
        appConfigDto = (AppConfigDto) FileUtils.readConfigFileToObject(FileUtils.getFilePath("/conf/app.conf").getPath(), AppConfigDto.class);
    }
}
