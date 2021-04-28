package com.hoomoomoo.im.cache;

import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.SecurityUtils;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;

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
        String confPath = FileUtils.getFilePath("/conf/app.conf").getPath();
        // 读取配置文件
        appConfigDto = (AppConfigDto) FileUtils.readConfigFileToObject(confPath, AppConfigDto.class);
        if (appConfigDto != null && appConfigDto.getSvnPassword().endsWith(STR_SECURITY)) {
            // 解密
            String password = appConfigDto.getSvnPassword();
            password = SecurityUtils.getDecryptString(password.substring(0, password.length() - 3));
            appConfigDto.setSvnPassword(password);
            return;
        }
        // 加密配置文件
        List<String> content = FileUtils.readNormalFile(confPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i);
                if (item.contains(STR_EQUALS) && item.contains(KEY_PASSWORD) && !item.endsWith(STR_SECURITY)) {
                    int index = item.indexOf(STR_EQUALS) + 1;
                    String password = SecurityUtils.getEncryptString(item.substring(index)) + STR_SECURITY;
                    item = item.substring(0, index) + password;
                    content.set(i, item);
                }
            }
        }
        FileUtils.writeFile(confPath, content, false);
    }
}
