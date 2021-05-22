package com.hoomoomoo.im.cache;

import com.alibaba.fastjson.JSON;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LicenseDto;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.SecurityUtils;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
        appConfigDto.setSvnUpdatePath(new ArrayList<>(16));
        appConfigDto.setScriptUpdateTable(new ArrayList<>(16));

        // 加载证书信息
        String licensePath = FileUtils.getFilePath("/conf/init/license.init").getPath();
        List<String> licenseContent = FileUtils.readNormalFile(licensePath, false);
        StringBuilder license = new StringBuilder();
        if (CollectionUtils.isEmpty(licenseContent)) {
            LoggerUtils.info("证书信息加载失败,停止应用启动");
            LoggerUtils.writeAppLog("证书信息加载失败,停止应用启动");
            System.exit(0);
        } else {
            for (String item : licenseContent) {
                license.append(item);
            }
            LicenseDto licenseDto = JSON.parseObject(SecurityUtils.getDecryptString(license.toString()), LicenseDto.class);
            appConfigDto.setLicense(licenseDto);
        }

        // 读取svn代码更新配置
        List<String> content = FileUtils.readNormalFile(confPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            for (String item : content) {
                // svn代码更新配置
                if (item.startsWith(KEY_SVN_UPDATE)) {
                    int index = item.indexOf(STR_SYMBOL_EQUALS);
                    String name = item.substring(KEY_SVN_UPDATE.length(), index);
                    String path = item.substring(index + 1);
                    if (StringUtils.isNotBlank(path)) {
                        LinkedHashMap version = new LinkedHashMap(2);
                        version.put(name, path);
                        appConfigDto.getSvnUpdatePath().add(version);
                    }
                }
                // 升级脚本配置
                if (item.startsWith(KEY_SCRIPT_UPDATE)) {
                    int index = item.indexOf(STR_SYMBOL_EQUALS);
                    String name = item.substring(KEY_SCRIPT_UPDATE.length(), index);
                    String column = item.substring(index + 1);
                    if (StringUtils.isNotBlank(column)) {
                        List<String> columnConfig = Arrays.asList(column.split(STR_SYMBOL_COMMA));
                        LinkedHashMap table = new LinkedHashMap(2);
                        table.put(name.toLowerCase(), columnConfig);
                        appConfigDto.getScriptUpdateTable().add(table);
                    }
                }
            }
        }

        // 解密
        if (appConfigDto != null && appConfigDto.getSvnPassword().endsWith(STR_SECURITY_FLAG)) {
            String password = appConfigDto.getSvnPassword();
            password = SecurityUtils.getDecryptString(password.substring(0, password.length() - 3));
            appConfigDto.setSvnPassword(password);
            return;
        }
        // 加密
        if (CollectionUtils.isNotEmpty(content)) {
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i);
                if (item.contains(STR_SYMBOL_EQUALS) && item.startsWith(KEY_SVN_PASSWORD) && !item.endsWith(STR_SECURITY_FLAG)) {
                    int index = item.indexOf(STR_SYMBOL_EQUALS) + 1;
                    if (StringUtils.isNotBlank(item.substring(index))) {
                        String password = SecurityUtils.getEncryptString(item.substring(index)) + STR_SECURITY_FLAG;
                        item = item.substring(0, index) + password;
                        content.set(i, item);
                    }
                }
            }
        }
        FileUtils.writeFile(confPath, content, false);
    }
}
