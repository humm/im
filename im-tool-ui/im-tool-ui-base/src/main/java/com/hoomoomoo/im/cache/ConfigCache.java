package com.hoomoomoo.im.cache;

import com.alibaba.fastjson.JSON;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.LicenseDto;
import com.hoomoomoo.im.utils.CommonUtils;
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

    private ConfigCache() throws Exception {
        init(true);
    }

    public static void initAppCodeCache(String appCode) {
        appCodeCache = appCode;
    }

    public static String getAppCodeCache() {
        return appCodeCache;
    }

    public static void initConfigCache() throws Exception {
        configCache = new ConfigCache();
    }

    public static AppConfigDto getAppConfigDtoCache() throws Exception{
        return getConfigCache().getAppConfigDto();
    }

    public static AppConfigDto getAppConfigDtoCache(boolean initExtend) throws Exception{
        if (appConfigDto == null) {
            init(initExtend);
        }
        return appConfigDto;
    }

    private AppConfigDto getAppConfigDto() {
        return appConfigDto;
    }

    private static ConfigCache getConfigCache() throws Exception {
        if (configCache == null) {
            configCache = new ConfigCache();
        }
        return configCache;
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
        List<String> content = FileUtils.readNormalFile(confPath);

        // 加载配置信息
        if (CollectionUtils.isNotEmpty(content)) {
            StringBuilder replaceSkipVersion = new StringBuilder();
            StringBuilder replaceVersion = new StringBuilder("/ta/fund:");
            for (String item : content) {
                // 代码更新配置
                if (item.startsWith(KEY_SVN_UPDATE)) {
                    int index = item.indexOf(STR_EQUALS);
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
                    int index = item.indexOf(STR_EQUALS);
                    String tableName = item.substring(KEY_SCRIPT_UPDATE_TABLE.length(), index).toLowerCase() + STR_POINT;
                    String tableColumn = item.substring(index + 1);
                    if (StringUtils.isNotBlank(tableColumn)) {
                        String[] columns = tableColumn.split(STR_$_SLASH);
                        for (int i = 0; i < columns.length; i++) {
                            tableName += i;
                            String column = columns[i];
                            List<String> columnConfig = Arrays.asList(column.split(STR_COMMA));
                            LinkedHashMap table = new LinkedHashMap(2);
                            table.put(tableName, columnConfig);
                            appConfigDto.getScriptUpdateTable().add(table);
                        }
                    }
                }
                // svn统计配置
                if (item.startsWith(KEY_SVN_STAT_USER)) {
                    int index = item.indexOf(STR_EQUALS);
                    String code = item.substring(KEY_SVN_STAT_USER.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        if (!KEY_DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getSvnStatUser().put(code.toLowerCase(), name);
                        }
                    }
                }
                // 复制文件配置
                if (item.startsWith(KEY_COPY_CODE_VERSION)) {
                    int index = item.indexOf(STR_EQUALS);
                    String code = item.substring(KEY_COPY_CODE_VERSION.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        if (!KEY_DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getCopyCodeVersion().put(code, name);
                            if (!KEY_DESKTOP.equalsIgnoreCase(code) && !KEY_TRUNK.equalsIgnoreCase(code)) {
                                LinkedHashMap<String, String> version = new LinkedHashMap<>(1);
                                version.put(code, name);
                                appConfigDto.getSvnUpdatePath().add(version);
                            }
                            if (code.contains(KEY_FUND)) {
                                replaceSkipVersion.append(code).append(STR_COMMA);
                                replaceVersion.append(code).append(STR_COMMA);
                            }
                        }
                    }
                }
                // svnUrl配置
                if (item.startsWith(KEY_SVN_URL)) {
                    int index = item.indexOf(STR_EQUALS);
                    String code = item.substring(KEY_SVN_URL.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        if (!KEY_DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getSvnUrl().put(code.toLowerCase(), name);
                        }
                    }
                }
                // 替换源路径
                if (item.startsWith(KEY_COPY_CODE_LOCATION_REPLACE_SOURCE)) {
                    int index = item.indexOf(STR_EQUALS);
                    String code = item.substring(KEY_COPY_CODE_LOCATION_REPLACE_SOURCE.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        String[] location = name.split(STR_COMMA);
                        if (!KEY_DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getReplaceSourceUrl().put(location[0], location[1]);
                        }
                    }
                }
                // 替换目标路径
                if (item.startsWith(KEY_COPY_CODE_LOCATION_REPLACE_TARGET)) {
                    int index = item.indexOf(STR_EQUALS);
                    String code = item.substring(KEY_COPY_CODE_LOCATION_REPLACE_TARGET.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        String[] location = name.split(STR_COMMA);
                        if (!KEY_DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getReplaceTargetUrl().put(location[0], location[1]);
                        }
                    }
                }

                // 字段翻译配置
                if (item.startsWith(KEY_FIELD_TRANSLATE)) {
                    int index = item.indexOf(STR_EQUALS);
                    String code = item.substring(KEY_FIELD_TRANSLATE.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        if (!KEY_DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getFieldTranslateMap().put(code.toLowerCase(), name);
                        }
                    }
                }

                if (item.startsWith(KEY_FILE_SYNC_VERSION)) {
                    int index = item.indexOf(STR_EQUALS);
                    String code = item.substring(KEY_FILE_SYNC_VERSION.length(), index);
                    String name = item.substring(index + 1);
                    if (StringUtils.isNotBlank(name)) {
                        if (!KEY_DEMO.equalsIgnoreCase(code)) {
                            appConfigDto.getFileSyncVersionMap().put(code.toLowerCase(), name);
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
        List<String> licenseContent = FileUtils.readNormalFile(licensePath);
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
                    if (item.contains(STR_EQUALS) && item.startsWith(KEY_SVN_PASSWORD) && !item.endsWith(SECURITY_FLAG)) {
                        int index = item.indexOf(STR_EQUALS) + 1;
                        if (StringUtils.isNotBlank(item.substring(index))) {
                            String password = SecurityUtils.getEncryptString(item.substring(index)) + SECURITY_FLAG;
                            item = item.substring(0, index) + password;
                            content.set(i, item);
                        }
                    }
                }
            }
        }

        LoggerUtils.appStartInfo(String.format(MSG_ENCRYPT, NAME_CONFIG_USER));

        String scriptUpdateKey = SCRIPT_UPDATE.getCode() + STR_COLON + SCRIPT_UPDATE.getName();
        String configSetKey = CONFIG_SET.getCode() + STR_COLON + CONFIG_SET.getName();
        String appCode = ConfigCache.getAppCodeCache();
        List<FunctionDto> functionDtoList = appConfigDto.getLicense().getFunction();
        if (CommonUtils.isSuperUser()) {
            functionDtoList = CommonUtils.functionConfigToFunctionDto(appCode, CommonUtils.getAppFunctionConfig(appCode));
        }

        // 配置文件参数参数项控制
        Set<String> authConf = new HashSet<>();
        for (FunctionDto functionDto : functionDtoList) {
            MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(functionDto.getFunctionCode());
            String[] titleConf = functionConfig.getTitleConf().split(STR_COMMA);
            for (String title : titleConf) {
                authConf.add(title);
            }
        }
        for (int i = 0; i < content.size(); i++) {
            String item = content.get(i);
            if (item.contains(scriptUpdateKey) || item.contains(configSetKey)) {
                StringBuilder msg = new StringBuilder("#");
                for (FunctionDto functionDto : functionDtoList) {
                    int functionCode = Integer.valueOf(functionDto.getFunctionCode());
                    if ((item.contains(scriptUpdateKey) && functionCode < MAX_COMMON_FUNCTION_CODE) || (item.contains(configSetKey) && functionCode >= MAX_COMMON_FUNCTION_CODE)) {
                        msg.append(STR_SPACE).append(functionDto.getFunctionCode()).append(STR_COLON).append(functionDto.getFunctionName());
                    }
                }
                content.set(i, msg.toString());
            }
        }
        ListIterator<String> iterator = content.listIterator();
        int index = 0;
        while (iterator.hasNext()) {
            String item = iterator.next();
            String functionName = getFunctionName(item);
            if (StringUtils.isNotBlank(functionName) && !authConf.contains(functionName)) {
                index++;
                if (index == 2) {
                    index = 0;
                }
                iterator.remove();
                continue;
            }
            if (index == 1) {
                iterator.remove();
            }
        }
        boolean remove = false;
        iterator = content.listIterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (StringUtils.isNotBlank(item)) {
                remove = false;
            }
            if (remove) {
                iterator.remove();
                continue;
            }
            if (StringUtils.isBlank(item)) {
                remove = true;
            }
        }

        FileUtils.writeFile(confPath, content);

        // 设置参数默认值
        if (StringUtils.isBlank(appConfigDto.getSystemToolCheckMenuResultPath())) {
            appConfigDto.setSystemToolCheckMenuResultPath(FileUtils.getFilePath(DEFAULT_FOLDER + "/check"));
        }
        if (StringUtils.isBlank(appConfigDto.getScriptUpdateGeneratePath())) {
            appConfigDto.setScriptUpdateGeneratePath(FileUtils.getFilePath(DEFAULT_FOLDER));
        }

        // 孤版客户
        String hepTaskOnlySelf = appConfigDto.getHepTaskOnlySelf();
        if (StringUtils.isNotBlank(hepTaskOnlySelf)) {
            String[] onlySelf = hepTaskOnlySelf.split(STR_COMMA);
            for (String ele : onlySelf) {
                String[] eleList = ele.split(STR_COLON);
                if (eleList.length == 2) {
                    appConfigDto.getHepTaskOnlySelfMap().put(eleList[0], eleList[1]);
                }
            }
        }

        // 主版本指定版本
        String hepTaskAppointVersion = appConfigDto.getHepTaskAppointVersion();
        if (StringUtils.isNotBlank(hepTaskAppointVersion)) {
            String[] appointVersion = hepTaskAppointVersion.split(STR_COMMA);
            for (String ele : appointVersion) {
                String[] eleList = ele.split(STR_COLON);
                if (eleList.length == 2) {
                    appConfigDto.getHepTaskAppointVersionMap().put(eleList[0], eleList[1]);
                }
            }
        }
    }

    private static String getFunctionName(String item) {
        String functionName = STR_BLANK;
        if (item.contains(CONF_FUNCTION_PREFIX)) {
            String[] element = item.split(STR_SPACE);
            if (element.length == 4) {
                functionName = element[1];
            }
        }
        return functionName;
    }
}