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

    public static void initCache() throws Exception {
        configCache = new ConfigCache();
    }

    private ConfigCache() throws Exception {
        init();
    }

    private void init() throws Exception {
        String confPath = FileUtils.getFilePath(PATH_APP);
        // 读取配置文件
        appConfigDto = (AppConfigDto) FileUtils.readConfigFileToObject(confPath, AppConfigDto.class);

        // 加载证书信息
        String licensePath = FileUtils.getFilePath(PATH_LICENSE);
        List<String> licenseContent = FileUtils.readNormalFile(licensePath, false);
        StringBuilder license = new StringBuilder();
        if (CollectionUtils.isEmpty(licenseContent)) {
            LoggerUtils.info(MSG_LICENSE_NOT_EXIST);
            LicenseDto licenseDto = new LicenseDto();
            licenseDto.setEffectiveDate(STR_0);
            licenseDto.setFunction(new ArrayList<>());
            appConfigDto.setLicense(licenseDto);
        } else {
            for (String item : licenseContent) {
                license.append(item);
            }
            LicenseDto licenseDto = JSON.parseObject(SecurityUtils.getDecryptString(license.toString()), LicenseDto.class);
            appConfigDto.setLicense(licenseDto);
        }

        LoggerUtils.info(String.format(MSG_LOAD, "授权证书信息"));

        // 读取svn代码更新配置
        List<String> content = FileUtils.readNormalFile(confPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            for (String item : content) {
                // svn代码更新配置
                if (item.startsWith(KEY_SVN_UPDATE)) {
                    int index = item.indexOf(SYMBOL_EQUALS);
                    String name = item.substring(KEY_SVN_UPDATE.length(), index);
                    String path = item.substring(index + 1);
                    if (StringUtils.isNotBlank(path)) {
                        LinkedHashMap version = new LinkedHashMap(2);
                        version.put(name, path);
                        appConfigDto.getSvnUpdatePath().add(version);
                    }
                }
                // 升级脚本配置
                if (item.startsWith(KEY_SCRIPT_UPDATE_TABLE)) {
                    int index = item.indexOf(SYMBOL_EQUALS);
                    String tableName = item.substring(KEY_SCRIPT_UPDATE_TABLE.length(), index).toLowerCase() + SYMBOL_POINT;
                    String tableColumn = item.substring(index + 1);
                    if (StringUtils.isNotBlank(tableColumn)) {
                        String[] columns = tableColumn.split(SYMBOL_$_SLASH);
                        for (int i = 0; i < columns.length; i++) {
                            tableName += i;
                            String column = columns[i];
                            List<String> columnConfig = Arrays.asList(column.split(SYMBOL_COMMA));
                            LinkedHashMap table = new LinkedHashMap(2);
                            table.put(tableName, columnConfig);
                            appConfigDto.getScriptUpdateTable().add(table);
                        }
                    }
                }
                // svn统计配置
                if (item.startsWith(KEY_SVN_STAT_USER)) {
                    int index = item.indexOf(SYMBOL_EQUALS);
                    String code = item.substring(KEY_SVN_STAT_USER.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        if (!"demo10000".equals(code.toLowerCase())) {
                            appConfigDto.getSvnStatUser().put(code.toLowerCase(), name);
                        }
                    }
                }
            }
        }

        // 解密
        if (appConfigDto != null && appConfigDto.getSvnPassword().endsWith(SECURITY_FLAG)) {
            String password = appConfigDto.getSvnPassword();
            password = SecurityUtils.getDecryptString(password.substring(0, password.length() - 3));
            appConfigDto.setSvnPassword(password);
            return;
        }
        // 加密
        if (CollectionUtils.isNotEmpty(content)) {
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i);
                if (item.contains(SYMBOL_EQUALS) && item.startsWith(KEY_SVN_PASSWORD) && !item.endsWith(SECURITY_FLAG)) {
                    int index = item.indexOf(SYMBOL_EQUALS) + 1;
                    if (StringUtils.isNotBlank(item.substring(index))) {
                        String password = SecurityUtils.getEncryptString(item.substring(index)) + SECURITY_FLAG;
                        item = item.substring(0, index) + password;
                        content.set(i, item);
                    }
                }
            }
        }

        LoggerUtils.info(String.format(MSG_ENCRYPT, "用户信息"));

        FileUtils.writeFile(confPath, content, false);
    }
}
