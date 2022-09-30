package com.hoomoomoo.im.utils;

import com.alibaba.fastjson.JSON;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.LicenseDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022-09-24
 */
public class InitConfigUtils {

    /**
     * 生成授权证书
     *
     * @param appCode
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void buildLicense(String appCode) throws Exception {
        ConfigCache.initAppCodeCache(appCode);
        String pathAuth = FileUtils.getFilePath(PATH_AUTH);
        LinkedHashMap<String, String> content = FileUtils.readConfigFileToMapIncludePoint(pathAuth);
        String appFunction = content.get("app.license.function");
        String effectiveDate = content.get("app.license.effective.date");
        List<FunctionDto> function = functionConfigToFunctionDto(appCode, CommonUtils.getAppFunctionConfig(appCode));
        LicenseDto license = new LicenseDto();
        license.setEffectiveDate(StringUtils.isBlank(effectiveDate) ? "20991231" : effectiveDate);
        license.setFunction(function);
        if (StringUtils.isNotBlank(appFunction)) {
            String[] functionConfig = appFunction.split(SYMBOL_COMMA);
            Iterator<FunctionDto> iterator = function.iterator();
            out: while (iterator.hasNext()) {
                FunctionDto functionDto = iterator.next();
                for (String code : functionConfig) {
                    if (functionDto.getFunctionCode().equals(code)) {
                        continue out;
                    }
                }
                if (Integer.valueOf(functionDto.getFunctionCode()) < FUNCTION_CODE_2000) {
                    iterator.remove();
                }
            }
        }
        String licenseContent = SecurityUtils.getEncryptString(JSON.toJSONString(license));
        String licensePath = FileUtils.getFilePath(PATH_LICENSE);
        FileUtils.writeFile(licensePath, licenseContent, false);
        FileUtils.writeFile(pathAuth, SYMBOL_EMPTY, false);
    }

    /**
     * 生成配置文件信息
     *
     * @param appCode
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void buildConfig(String appCode) throws Exception {
        ConfigCache.initAppCodeCache(appCode);
        String pathApp = FileUtils.getFilePath(PATH_APP);
        List<String> content = FileUtils.readNormalFile(pathApp, false);
        switch (appCode) {
            case APP_CODE_TA:
                buildConfigByTa(appCode, content);
                break;
            case APP_CODE_SHOPPING:
                buildConfigByShopping(appCode, content);
                break;
        }

        FileUtils.writeFile(pathApp, content, false);
    }

    /**
     * 修改配置文件信息
     *
     * @param
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void updateConfig(String appCode) throws Exception {
        Map<String, String> keys = new LinkedHashMap<>();
        keys.put("app.user", null);
        keys.put("app.about.detail", "false");
        switch (appCode) {
            case APP_CODE_TA:
                updateConfigByTa(keys);
                break;
            case APP_CODE_SHOPPING:
                updateConfigByShopping(keys);
                break;
        }

        String confPath = FileUtils.getFilePath(PATH_APP);
        List<String> content = FileUtils.readNormalFile(confPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i);
                if (isUpdate(item, keys)) {
                    int index = item.indexOf(SYMBOL_EQUALS) + 1;
                    item = item.substring(0, index);
                    String value = keys.get(item.replace(SYMBOL_EQUALS, SYMBOL_EMPTY));
                    if (StringUtils.isNotEmpty(value)) {
                        item += value;
                    }
                    content.set(i, item);
                }
            }
        }
        FileUtils.writeFile(confPath, content, false);
    }

    /**
     * 更新版本信息
     *
     * @param
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void updateVersion(String appCode) {
        try {
            ConfigCache.initAppCodeCache(appCode);
            String versionFilePath = FileUtils.getFilePath(PATH_VERSION);
            String versionFilePathSource = versionFilePath.replace("/target/classes", "/src/main/resources");
            List<String> content = FileUtils.readNormalFile(versionFilePath, false);
            String subVersion = "01";
            int times = 60;
            if (CollectionUtils.isNotEmpty(content)) {
                for (int i = 0; i < content.size(); i++) {
                    String item = content.get(i);
                    if (item.startsWith(NAME_CURRENT_VERSION)) {
                        int index = item.indexOf(":") + 1;
                        String version = item.substring(index);
                        int versionIndex = version.lastIndexOf(SYMBOL_POINT);
                        String versionDate = version.substring(0, versionIndex).trim();
                        String versionNo = version.substring(versionIndex + 1);
                        if (CommonUtils.getCurrentDateTime3().equals(versionDate.replace(SYMBOL_POINT, SYMBOL_EMPTY))) {
                            subVersion = String.valueOf(Integer.valueOf(versionNo) + 1);
                            if (subVersion.length() == 1) {
                                subVersion = STR_0 + subVersion;
                            }
                        }
                    }
                    if (item.startsWith("发版次数:")) {
                        int index = item.indexOf(":") + 1;
                        times = Integer.valueOf(item.substring(index).trim()) + 1;
                    }
                }
            }
            StringBuilder statLog = new StringBuilder();
            statLog.append("首发时间: ").append("2021-05-06 23:17:56").append(SYMBOL_NEXT_LINE_2);
            statLog.append("当前版本: ").append(CommonUtils.getCurrentDateTime4().replace(SYMBOL_HYPHEN, SYMBOL_POINT)).append(SYMBOL_POINT).append(subVersion).append(SYMBOL_NEXT_LINE);
            statLog.append("发版时间: ").append(CommonUtils.getCurrentDateTime1()).append(SYMBOL_NEXT_LINE_2);
            statLog.append("发版次数: ").append(times).append(SYMBOL_NEXT_LINE);
            FileUtils.writeFile(versionFilePath, statLog.toString(), false);
            FileUtils.writeFile(versionFilePathSource, statLog.toString(), false);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    /**
     * 复制基类配置信息
     *
     * @param appCode
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void copyBaseConfig(String appCode) {
        ConfigCache.initAppCodeCache(appCode);
        String pathFxml = FileUtils.getFilePath(PATH_FXML).replaceAll(appCode, APP_CODE_BASE);
        copyFile(appCode, pathFxml);

        String pathStyle = FileUtils.getFilePath(PATH_STYLE).replaceAll(appCode, APP_CODE_BASE);
        copyFile(appCode, pathStyle);
    }

    /**
     * 复制配置文件
     *
     * @param appCode
     * @param path
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    private static void copyFile(String appCode, String path) {
        File[] StyleFileList = new File(path).listFiles();
        for (File file : StyleFileList) {
            String filePath = file.getAbsolutePath();
            try {
                File targetFile = new File(filePath.replaceAll(APP_CODE_BASE, appCode));
                targetFile.getParentFile().mkdirs();
                FileUtils.copyFile(new File(filePath), targetFile);
            } catch (Exception e) {
                LoggerUtils.info(e);
            }
        }
    }

    /**
     * 更新标识
     *
     * @param item
     * @param keys
     * @author: humm23693
     * @date: 2022-09-24
     * @return: boolean
     */
    public static boolean isUpdate(String item, Map<String, String> keys) {
        if (!item.contains(SYMBOL_EQUALS)) {
            return false;
        }
        Iterator<String> iterator = keys.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (item.startsWith(key)) {
                return true;
            }
        }
        return false;
    }

    public static String buildFunctionName(String appCode, String item, ListIterator<String> itemIterator) {
        if (item.contains(NAME_APP_TAB_SHOW)) {
            try {
                List<FunctionDto> functionDtoList = CommonUtils.getAuthFunction();
                Map<String, MenuFunctionConfig.FunctionConfig> noAuthFunctionConfig = CommonUtils.getNoAuthFunctionConfigMap(appCode);
                Map<String, FunctionDto> functionMap = new LinkedHashMap<>(16);
                if (CollectionUtils.isNotEmpty(functionDtoList)) {
                    for (FunctionDto functionDto : functionDtoList) {
                        if (!functionMap.containsKey(functionDto.getFunctionCode())) {
                            functionMap.put(functionDto.getFunctionCode(), functionDto);
                        }
                    }
                }
                if (MapUtils.isNotEmpty(noAuthFunctionConfig)) {
                    Iterator<String> iterator = noAuthFunctionConfig.keySet().iterator();
                    while (iterator.hasNext()) {
                        String functionCode = iterator.next();
                        if (!functionMap.containsKey(functionCode)) {
                            FunctionDto functionDto = new FunctionDto();
                            functionDto.setFunctionCode(functionCode);
                            functionDto.setFunctionName(noAuthFunctionConfig.get(functionCode).getName());
                            functionMap.put(functionCode, functionDto);
                        }
                    }
                }
                Iterator<String> iterator = functionMap.keySet().iterator();
                String content = SYMBOL_NEXT_LINE + ANNOTATION_TYPE_CONFIG + SYMBOL_SPACE;
                String noAuthContent = SYMBOL_NEXT_LINE + ANNOTATION_TYPE_CONFIG + SYMBOL_SPACE;
                int index = 0;
                while (iterator.hasNext()) {
                    String functionCode = iterator.next();
                    if (Integer.valueOf(functionCode) < FUNCTION_CODE_2000) {
                        index++;
                        if (index % 15 == 0) {
                            content += SYMBOL_NEXT_LINE + ANNOTATION_TYPE_CONFIG + SYMBOL_SPACE;
                        }
                        content += functionCode + SYMBOL_COLON + functionMap.get(functionCode).getFunctionName() + SYMBOL_SPACE;
                    } else {
                        noAuthContent += functionCode + SYMBOL_COLON + functionMap.get(functionCode).getFunctionName() + SYMBOL_SPACE;
                    }
                }
                item += content + noAuthContent;
                if (itemIterator != null) {
                    itemIterator.set(item);
                }
            } catch (Exception e) {
                LoggerUtils.info(e);
            }
        }
        return item;
    }

    /**
     * 生成TA配置信息
     *
     * @param content
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void buildConfigByTa(String appCode, List<String> content) {
        boolean svnUpdate = false;
        boolean scriptUpdateTable = false;
        boolean svnStatUser = false;
        boolean copyCode = false;
        boolean svnUrl = false;
        boolean locationReplaceSource = false;
        boolean locationReplaceTarget = false;
        ListIterator<String> iterator = content.listIterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            buildFunctionName(appCode, item,  iterator);
            if (item.startsWith(KEY_SVN_UPDATE)) {
                if (svnUpdate) {
                    iterator.remove();
                } else {
                    svnUpdate = true;
                    iterator.set("svn.update.demo=D:/workspace/demo");
                }
            } else if (item.startsWith(KEY_SCRIPT_UPDATE_TABLE)) {
                if (scriptUpdateTable) {
                    iterator.remove();
                } else {
                    scriptUpdateTable = true;
                    iterator.set("script.update.table.demo=demo");
                }
            } else if (item.startsWith(KEY_SVN_STAT_USER)) {
                if (svnStatUser) {
                    iterator.remove();
                } else {
                    svnStatUser = true;
                    iterator.set("svn.stat.user.demo10000=演示");
                }
            }  else if (item.startsWith(KEY_COPY_CODE_VERSION)) {
                if (copyCode) {
                    iterator.remove();
                } else {
                    copyCode = true;
                    iterator.set("copy.code.version.demo10000=D:/workspace/demo");
                }
            } else if (item.startsWith(KEY_SVN_URL)) {
                if (svnUrl) {
                    iterator.remove();
                } else {
                    svnUrl = true;
                    iterator.set("svn.url.demo10000=https://192.168.57.56/bank/depone/BTA6.0/trunk");
                }
            } else if (item.startsWith(KEY_COPY_CODE_LOCATION_REPLACE_SOURCE)) {
                if (locationReplaceSource) {
                    iterator.remove();
                } else {
                    locationReplaceSource = true;
                    iterator.set("copy.code.location.replace.source.0=");
                }
            } else if (item.startsWith(KEY_COPY_CODE_LOCATION_REPLACE_TARGET)) {
                if (locationReplaceTarget) {
                    iterator.remove();
                } else {
                    locationReplaceTarget = true;
                    iterator.set("copy.code.location.replace.target.0=");
                }
            }
        }
    }

    /**
     * 生成Shopping配置信息
     *
     * @param content
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void buildConfigByShopping(String appCode, List<String> content) {
        boolean svnUpdate = false;
        ListIterator<String> iterator = content.listIterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            buildFunctionName(appCode, item, iterator);
            if (item.startsWith(KEY_SVN_UPDATE)) {
                if (svnUpdate) {
                    iterator.remove();
                } else {
                    svnUpdate = true;
                    iterator.set("svn.update.demo=D:/workspace/demo");
                }
            }
        }
    }

    /**
     * 更新TA配置信息
     *
     * @param keys
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void  updateConfigByTa(Map<String, String> keys) {
        keys.put("app.tab.show", "10");

        keys.put("svn.username", null);
        keys.put("svn.password", null);
        keys.put("svn.default.append.path", null);

        keys.put("fund.excel.path", null);
        keys.put("fund.generate.path", null);

        keys.put("process.excel.path", null);
        keys.put("process.generate.path.trans", null);
        keys.put("process.generate.path.schedule", null);

        keys.put("script.update.generate.path", null);

        keys.put("generate.code.java.path", null);
        keys.put("generate.code.sql.path", null);
        keys.put("generate.code.vue.path", null);
        keys.put("generate.code.route.path", null);
        keys.put("generate.code.author", null);

        keys.put("copy.code.default.source", null);
        keys.put("copy.code.default.target", null);

        keys.put("copy.code.location.replace.skipAccountVersion", null);

        keys.put("generate.sql.database.code", null);
        keys.put("generate.sql.table.code", null);

        keys.put("database.script.location", null);
        keys.put("database.script.url", null);
        keys.put("database.script.username", null);
        keys.put("database.script.password", null);

    }

    /**
     * 更新Shopping配置信息
     *
     * @param keys
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void  updateConfigByShopping(Map<String, String> keys) {
        keys.put("app.tab.show", "540");
    }

    /**
     * 功能类型转换
     *
     * @param functionConfigList
     * @author: humm23693
     * @date: 2022-09-24
     * @return: java.util.List<com.hoomoomoo.im.dto.FunctionDto>
     */
    public static List<FunctionDto> functionConfigToFunctionDto(String appCode, List<MenuFunctionConfig.FunctionConfig> functionConfigList) {
        List<FunctionDto> functionDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(functionConfigList)) {
            for (MenuFunctionConfig.FunctionConfig item : functionConfigList) {
                functionDtoList.add(new FunctionDto(item.getCode(), item.getName()));
            }
        }
        return functionDtoList;
    }
}
