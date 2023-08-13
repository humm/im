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

import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.cache
 * @date 2021/04/24
 */
@Data
public class ConfigCache {

    private static ConfigCache configCache = null;

    private static AppConfigDto appConfigDto;

    private static String appCodeCache;

    public static String getAppCodeCache() {
        return appCodeCache;
    }

    public static void initAppCodeCache(String appCode) {
        appCodeCache = appCode;
    }

    public static ConfigCache getConfigCache() throws Exception {
        if (configCache == null) {
            configCache = new ConfigCache();
        }
        return configCache;
    }

    public static void initCache() throws Exception {
        configCache = new ConfigCache();
    }

    public AppConfigDto getAppConfigDto() {
        return appConfigDto;
    }

    public static AppConfigDto getInitAppConfigDto() throws Exception{
        if (appConfigDto == null) {
            init(false);
        }
        return appConfigDto;
    }

    private ConfigCache() throws Exception {
        init(true);
    }

    private static void init(boolean extend) throws Exception {
        String confPath = FileUtils.getFilePath(PATH_APP);
        // 读取配置文件
        appConfigDto = (AppConfigDto) FileUtils.readConfigFileToObject(confPath, AppConfigDto.class);
        if (extend) {
            initExtend(confPath, appConfigDto);
        }
    }

    private static void initExtend(String confPath, AppConfigDto appConfigDto) throws Exception{
        // 更新配置信息
        List<String> content = FileUtils.readNormalFile(confPath, false);

        int configIndex = 0;
        ListIterator<String> iterator = content.listIterator();
        String svnRealtimeStatKey = SVN_REALTIME_STAT.getCode() + SYMBOL_COLON + SVN_REALTIME_STAT.getName();
        String svnHistoryStatKey = SVN_HISTORY_STAT.getCode() + SYMBOL_COLON + SVN_HISTORY_STAT.getName();
        String hepTaskTodoKey = TASK_TODO.getCode() + SYMBOL_COLON + TASK_TODO.getName();
        while (iterator.hasNext()) {
            String item = iterator.next();
            boolean svn = item.contains(svnRealtimeStatKey) || item.contains(svnHistoryStatKey);
            if (!appConfigDto.getAppUser().contains(APP_USER_IM_SVN)) {
                if (svn) {
                    item = item.replace(svnRealtimeStatKey, SYMBOL_EMPTY);
                    item = item.replace(svnHistoryStatKey, SYMBOL_EMPTY);
                    iterator.set(item.replaceAll("\\s+", SYMBOL_SPACE));
                }
            }

            boolean hep = item.contains(hepTaskTodoKey);
            if (!appConfigDto.getAppUser().contains(APP_USER_IM_HEP)) {
                if (hep) {
                    item = item.replace(hepTaskTodoKey, SYMBOL_EMPTY);
                    iterator.set(item.replaceAll("\\s+", SYMBOL_SPACE));
                }
            }

            if (true) {
                continue;
            }
            // 删除未授权功能配置信息
            if (item.contains(NAME_SVN_STAT) || item.contains(NAME_SVN_STAT_REALTIME) || item.contains(NAME_SVN_STAT_HISTORY)) {
                configIndex++;
                iterator.remove();
            } else if (configIndex == 1) {
                iterator.remove();
            }
            if (configIndex == 2) {
                configIndex = 0;
            }
        }
        FileUtils.writeFile(confPath, content, false);

        // 加载配置信息
        if (CollectionUtils.isNotEmpty(content)) {
            StringBuilder replaceSkipVersion = new StringBuilder();
            StringBuilder replaceVersion = new StringBuilder("/ta/fund:");
            for (String item : content) {
                // 代码更新配置
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
                        if (!DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getSvnStatUser().put(code.toLowerCase(), name);
                        }
                    }
                }
                // 复制代码配置
                if (item.startsWith(KEY_COPY_CODE_VERSION)) {
                    int index = item.indexOf(SYMBOL_EQUALS);
                    String code = item.substring(KEY_COPY_CODE_VERSION.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        if (!DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getCopyCodeVersion().put(code, name);
                            if (code.contains(KEY_FUND)) {
                                replaceSkipVersion.append(code).append(SYMBOL_COMMA);
                                replaceVersion.append(code).append(SYMBOL_COMMA);
                            }
                        }
                    }
                }
                // svnUrl配置
                if (item.startsWith(KEY_SVN_URL)) {
                    int index = item.indexOf(SYMBOL_EQUALS);
                    String code = item.substring(KEY_SVN_URL.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        if (!DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getSvnUrl().put(code.toLowerCase(), name);
                        }
                    }
                }
                // 替换源路径
                if (item.startsWith(KEY_COPY_CODE_LOCATION_REPLACE_SOURCE)) {
                    int index = item.indexOf(SYMBOL_EQUALS);
                    String code = item.substring(KEY_COPY_CODE_LOCATION_REPLACE_SOURCE.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        String[] location = name.split(SYMBOL_COMMA);
                        if (!DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getReplaceSourceUrl().put(location[0], location[1]);
                        }
                    }
                }
                // 替换目标路径
                if (item.startsWith(KEY_COPY_CODE_LOCATION_REPLACE_TARGET)) {
                    int index = item.indexOf(SYMBOL_EQUALS);
                    String code = item.substring(KEY_COPY_CODE_LOCATION_REPLACE_TARGET.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        String[] location = name.split(SYMBOL_COMMA);
                        if (!DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getReplaceTargetUrl().put(location[0], location[1]);
                        }
                    }
                }

                // 字段翻译配置
                if (item.startsWith(KEY_FIELD_TRANSLATE)) {
                    int index = item.indexOf(SYMBOL_EQUALS);
                    String code = item.substring(KEY_FIELD_TRANSLATE.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        if (!DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getFieldTranslateMap().put(code.toLowerCase(), name);
                        }
                    }
                }
            }

            // 路径替换跳过版本号
            appConfigDto.setCopyCodeLocationReplaceSkipVersion(replaceSkipVersion.toString());

            // 路径替换版本号
            appConfigDto.setCopyCodeLocationReplaceVersion(replaceVersion.toString());

        }

        LoggerUtils.appStartInfo(String.format(MSG_LOAD, NAME_CONFIG_INFO));

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

        LoggerUtils.appStartInfo(String.format(MSG_LOAD, NAME_CONFIG_LICENSE));

        // 解密
        if (appConfigDto != null && appConfigDto.getSvnPassword() != null && appConfigDto.getSvnPassword().endsWith(SECURITY_FLAG)) {
            String password = appConfigDto.getSvnPassword();
            password = SecurityUtils.getDecryptString(password.substring(0, password.length() - 3));
            appConfigDto.setSvnPassword(password);
        } else {
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
            FileUtils.writeFile(confPath, content, false);
        }

        // 京东配置文件
        if (appConfigDto != null && APP_CODE_SHOPPING.equals(appCodeCache)) {
            String jdCookiePath = FileUtils.getFilePath(PATH_JD_COOKIE);
            List<String> jdCookieContent = FileUtils.readNormalFile(jdCookiePath, false);
            StringBuilder jdCookie = new StringBuilder();
            if (!CollectionUtils.isEmpty(jdCookieContent)) {
                for (String item : jdCookieContent) {
                    jdCookie.append(item);
                }
                String cookie = jdCookie.toString();
                if (cookie.endsWith(SECURITY_FLAG)) {
                    // 解密
                    cookie = SecurityUtils.getDecryptString(cookie.substring(0, cookie.length() - 3));
                    appConfigDto.setJdCookie(cookie);
                } else {
                    // 加密
                    appConfigDto.setJdCookie(cookie);
                    cookie = SecurityUtils.getEncryptString(cookie) + SECURITY_FLAG;
                    FileUtils.writeFile(jdCookiePath, cookie, false);
                }
            }
        }
        LoggerUtils.appStartInfo(String.format(MSG_ENCRYPT, NAME_CONFIG_USER));
    }
}
