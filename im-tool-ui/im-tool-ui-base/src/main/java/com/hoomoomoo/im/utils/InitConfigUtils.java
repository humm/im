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
import java.io.IOException;
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
        String pathAuth = CommonUtils.dealFilePath(FileUtils.getFilePath(PATH_AUTH), appCode);
        LinkedHashMap<String, String> content = FileUtils.readConfigFileToMapIncludePoint(pathAuth);
        String appFunction = content.get("app.license.function");
        String effectiveDate = content.get("app.license.effective.date");
        List<FunctionDto> function = CommonUtils.functionConfigToFunctionDto(appCode, CommonUtils.getAppFunctionConfig(appCode));
        LicenseDto license = new LicenseDto();
        license.setEffectiveDate(StringUtils.isBlank(effectiveDate) ? "20991231" : effectiveDate);
        license.setFunction(function);
        if (StringUtils.isNotBlank(appFunction)) {
            String[] functionConfig = appFunction.split(STR_COMMA);
            Iterator<FunctionDto> iterator = function.iterator();
            out: while (iterator.hasNext()) {
                FunctionDto functionDto = iterator.next();
                for (String code : functionConfig) {
                    if (functionDto.getFunctionCode().equals(code)) {
                        continue out;
                    }
                }
                if (Integer.valueOf(functionDto.getFunctionCode()) < FUNCTION_CONFIG_SET) {
                    iterator.remove();
                }
            }
        }
        String licenseContent = SecurityUtils.getEncryptString(JSON.toJSONString(license));
        String licensePath = CommonUtils.dealFilePath(FileUtils.getFilePath(PATH_LICENSE), appCode);
        FileUtils.writeFile(licensePath, licenseContent, false);
        FileUtils.writeFile(pathAuth, STR_BLANK, false);
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
        String pathApp = CommonUtils.dealFilePath(FileUtils.getFilePath(PATH_APP), appCode);
        List<String> content = FileUtils.readNormalFile(pathApp, false);
        Iterator<String> iterator = content.iterator();
        boolean flag = false;
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains(NAME_APP_TAB_SHOW_CODE)) {
                break;
            }
            if (flag) {
                iterator.remove();
            }
            if (NAME_APP_TAB_SHOW.equals(item)) {
                flag = true;
            }
        }
        switch (appCode) {
            case APP_CODE_TA:
                buildConfigByTa(appCode, content);
                break;
            default:
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
        keys.put("app.about.detail", "false");
        switch (appCode) {
            case APP_CODE_TA:
                updateConfigByTa(keys);
                break;
            default:
                break;
        }

        String confPath = CommonUtils.dealFilePath(FileUtils.getFilePath(PATH_APP), appCode);
        List<String> content = FileUtils.readNormalFile(confPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i);
                if (isUpdate(item, keys)) {
                    int index = item.indexOf(STR_EQUALS) + 1;
                    item = item.substring(0, index);
                    String value = keys.get(item.replace(STR_EQUALS, STR_BLANK));
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
            String versionFilePath = CommonUtils.dealFilePath(FileUtils.getFilePath(PATH_VERSION), appCode);
            String versionFilePathSource = versionFilePath.replace("/target/classes", "/src/main/resources");
            List<String> content = FileUtils.readNormalFile(versionFilePath, false);
            String subVersion = "000001";
            String currentVersion = STR_BLANK;
            int times = 0;
            if (CollectionUtils.isNotEmpty(content)) {
                for (int i = 0; i < content.size(); i++) {
                    String item = content.get(i);
                    if (item.startsWith(NAME_CURRENT_VERSION)) {
                        int index = item.indexOf(":") + 1;
                        currentVersion = item.substring(index).trim();
                        String versionNo = currentVersion.substring(currentVersion.indexOf(STR_POINT) + 1);
                        if (CommonUtils.getCurrentDateTime12().equals(currentVersion.substring(0, 4).trim())) {
                            subVersion = String.valueOf(Long.valueOf(versionNo) + 1);
                            for (int j=subVersion.length(); j<6; j++) {
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
            String nextVersion = CommonUtils.getCurrentDateTime12() + STR_POINT + subVersion;
            StringBuilder statLog = new StringBuilder();
            statLog.append("当前版本: ").append(currentVersion.trim()).append(STR_NEXT_LINE);
            statLog.append("发版时间: ").append(CommonUtils.getCurrentDateTime1()).append(STR_NEXT_LINE_2);
            statLog.append("首版时间: ").append("2021-05-06 23:17:56").append(STR_NEXT_LINE);
            statLog.append("发版次数: ").append(times).append(STR_NEXT_LINE);
            FileUtils.writeFile(versionFilePath, statLog.toString(), false);
            FileUtils.writeFile(versionFilePathSource, statLog.toString().replace(currentVersion, nextVersion), false);
            updatePom(versionFilePath.substring(0, versionFilePath.indexOf("target")), nextVersion);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    /**
     * 修改pom版本
     *
     * @param appPath
     * @param ver
     */
    private static void updatePom(String appPath, String ver) throws IOException {
        String appPomPath = appPath + "pom.xml";
        String basePomPath = appPath.replace("im-tool-ui-ta/", "im-tool-ui-base/") + "pom.xml";
        String parentPomPath = appPath.replace("im-tool-ui-ta/", STR_BLANK) + "pom.xml";
        updatePomFile(basePomPath, ver);
        updatePomFile(appPomPath, ver);
        updatePomFile(parentPomPath, ver);
    }

    private static void updatePomFile(String pomPath, String ver) throws IOException {
        List<String> content = FileUtils.readNormalFile(pomPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            boolean updateMark = false;
            for (int i=0; i<content.size(); i++) {
                String item = content.get(i);
                if (StringUtils.equals("<artifactId>im-tool-ui</artifactId>", item.trim())
                        || StringUtils.equals("<artifactId>im-tool-ui-ta</artifactId>", item.trim())
                        || StringUtils.equals("<artifactId>im-tool-ui-base</artifactId>", item.trim())) {
                    updateMark = true;
                }
                if (updateMark && item.contains("<version>")) {
                    String verLeft = item.substring(0, item.indexOf("<version>"));
                    content.set(i, verLeft + "<version>" + ver + "</version>");
                    updateMark = false;
                }
            }
            FileUtils.writeFile(pomPath, content, false);
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
        String pathFxml = CommonUtils.dealFilePath(FileUtils.getFilePath(PATH_FXML), appCode).replaceAll(appCode, APP_CODE_BASE);
        copyFile(appCode, pathFxml);

        String pathStyle = CommonUtils.dealFilePath(FileUtils.getFilePath(PATH_STYLE), appCode).replaceAll(appCode, APP_CODE_BASE);
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
        if (!item.contains(STR_EQUALS)) {
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
                List<FunctionDto> functionDtoList = CommonUtils.getAuthFunctionByMavenInstall(appCode);
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
                String content = STR_NEXT_LINE + ANNOTATION_CONFIG + STR_SPACE;
                String noAuthContent = STR_NEXT_LINE + ANNOTATION_CONFIG + STR_SPACE;
                while (iterator.hasNext()) {
                    String functionCode = iterator.next();
                    if (Integer.valueOf(functionCode) < FUNCTION_CONFIG_SET) {
                        content += functionCode + STR_COLON + functionMap.get(functionCode).getFunctionName() + STR_SPACE;
                    } else {
                        noAuthContent += functionCode + STR_COLON + functionMap.get(functionCode).getFunctionName() + STR_SPACE;
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
        boolean fieldTranslate = false;
        boolean fileSyncVersion = false;
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
            } else if (item.startsWith(KEY_FIELD_TRANSLATE)) {
                if (fieldTranslate) {
                    iterator.remove();
                } else {
                    fieldTranslate = true;
                    iterator.set("generate.code.field.translate.demo=");
                }
            } else if (item.startsWith(KEY_FILE_SYNC_VERSION)) {
                if (fileSyncVersion) {
                    iterator.remove();
                } else {
                    fileSyncVersion = true;
                    iterator.set("file.sync.version.trunk.demo=");
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
        keys.put("app.tab.show", "200");
        keys.put("app.log.level", "info");
        keys.put("app.mode", null);

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
        keys.put("copy.code.location.replace.skipVersion", null);
        keys.put("copy.code.location.replace.version", null);

        keys.put("generate.sql.database.code", null);
        keys.put("generate.sql.table.code", null);

        keys.put("database.script.location", null);
        keys.put("database.script.url", null);
        keys.put("database.script.username", null);
        keys.put("database.script.password", null);

        keys.put("hep.task.user", null);
        keys.put("hep.task.user.extend", null);
        keys.put("hep.task.error.finish.date.skip.version", null);
        keys.put("hep.task.rest.plan", null);
        keys.put("hep.task.rest.plan.time", null);
        keys.put("hep.task.rest.plan.date", null);

        keys.put("hep.task.customer.path", null);

        keys.put("hep.sync.only", null);
        keys.put("hep.task.sync.path", null);

        keys.put("system.tool.update.version.path", null);
        keys.put("hep.task.todo.end.date.version", null);

        keys.put("system.tool.check.menu.base.path", null);
        keys.put("system.tool.check.menu.result.path", null);
        keys.put("system.tool.check.menu.pub.path", null);
        keys.put("system.tool.script.change.menu.path", null);

        keys.put("system.tool.sync.code.source", null);
        keys.put("system.tool.sync.code.target", null);

        keys.put("file.sync.source", null);
        keys.put("file.sync.target", null);

    }

    /**
     * 清理文件
     *
     * @param appCode
     * @author: humm23693
     * @date: 2022-10-11
     * @return: void
     */
    public static void cleanFile(String appCode) {
        ConfigCache.initAppCodeCache(appCode);
        String pathAuth = CommonUtils.dealFilePath(FileUtils.getFilePath(PATH_AUTH), appCode);
        String pathLogs = CommonUtils.dealFilePath(FileUtils.getFilePath("/logs"), appCode);
        FileUtils.deleteFile(new File(pathAuth).getParentFile());
        FileUtils.deleteFile(new File(pathLogs));
    }
}
