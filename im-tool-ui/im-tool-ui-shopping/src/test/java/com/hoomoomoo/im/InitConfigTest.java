package com.hoomoomoo.im;

import com.alibaba.fastjson.JSON;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.LicenseDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package im
 * @date 2021/05/09
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InitConfigTest {

    /**
     * 生成授权证书
     *
     * @author: humm23693
     * @date: 2021/05/22
     * @return:
     */
    @Test
    public void config_01_buildLicense() throws Exception {
        ConfigCache.initAppCodeCache(APP_CODE_SHOPPING);
        String pathAuth = FileUtils.getFilePath(PATH_AUTH);
        LinkedHashMap<String, String> content = FileUtils.readConfigFileToMapIncludePoint(pathAuth);
        // 生成证书信息
        String appFunction = content.get("app.license.function");
        String effectiveDate = content.get("app.license.effective.date");
        List<FunctionDto> function = new ArrayList<>(16);
        LicenseDto license = new LicenseDto();
        license.setEffectiveDate(StringUtils.isBlank(effectiveDate) ? "20991231" : effectiveDate);
        license.setFunction(function);
        if (StringUtils.isBlank(appFunction)) {
            // 全功能
            for (FunctionConfig tab : CommonUtils.getAppFunctionConfig(APP_CODE_SHOPPING)) {
                function.add(new FunctionDto(tab.getCode(), tab.getName()));
            }
        } else {
            // 配置功能
            String[] functionConfig = appFunction.split(SYMBOL_COMMA);
            for (String code : functionConfig) {
                String name = FunctionConfig.getName(code);
                if (StringUtils.isNotBlank(name)) {
                    function.add(new FunctionDto(code, name));
                }
            }
        }
        String licenseContent = SecurityUtils.getEncryptString(JSON.toJSONString(license));
        String licensePath = FileUtils.getFilePath(PATH_LICENSE);
        FileUtils.writeFile(licensePath, licenseContent, false);
        FileUtils.writeFile(pathAuth, SYMBOL_EMPTY, false);
    }

    /**
     * 生成配置文件
     *
     * @author: humm23693
     * @date: 2021/05/22
     * @return:
     */
    @Test
    public void config_02_buildConfig() throws Exception {
        ConfigCache.initAppCodeCache(APP_CODE_SHOPPING);
        String pathApp = FileUtils.getFilePath(PATH_APP);
        List<String> content = FileUtils.readNormalFile(pathApp, false);
        ListIterator<String> iterator = content.listIterator();
        boolean svnUpdate = false;
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.startsWith(KEY_SVN_UPDATE)) {
                if (svnUpdate) {
                    iterator.remove();
                } else {
                    svnUpdate = true;
                    iterator.set("svn.update.demo=D:/workspace/demo");
                }
            }
        }
        FileUtils.writeFile(pathApp, content, false);
    }

    /**
     * 修改配置文件信息
     *
     * @param
     * @author: humm23693
     * @date: 2021/05/10
     * @return:
     */
    @Test
    public void config_03_updateConfig() throws Exception {
        Map<String, String> keys = new LinkedHashMap<>();
        keys.put("app.tab.show", "540");

        keys.put("app.user", null);

        keys.put("app.about.detail", "false");

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

        String jdCookie = FileUtils.getFilePath(PATH_JD_COOKIE);
        FileUtils.writeFile(jdCookie, SYMBOL_EMPTY, false);

    }

    /**
     * 更新版本信息
     *
     * @param
     * @author: humm23693
     * @date: 2021/05/10
     * @return:
     */
    @Test
    public void config_04_updateVersionConfig() {
        try {
            ConfigCache.initAppCodeCache(APP_CODE_SHOPPING);
            String versionFilePath = FileUtils.getFilePath(PATH_VERSION);
            String versionFilePathSource = versionFilePath.replace("/target/classes", "/src/main/resources");
            List<String> content = FileUtils.readNormalFile(versionFilePath, false);
            String subVersion = "00";
            int times = 1;
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
            statLog.append("首发时间: ").append("2022-01-09 23:17:56").append(SYMBOL_NEXT_LINE_2);
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
     * 复制base模块fxml信息
     *
     * @param
     * @author: humm23693
     * @date: 2022/1/9
     * @return: void
     */
    @Test
    public void config_05_copyBaseConfig() {
        ConfigCache.initAppCodeCache(APP_CODE_SHOPPING);
        String pathFxml = FileUtils.getFilePath(PATH_FXML).replaceAll(APP_CODE_SHOPPING, APP_CODE_BASE);
        copyFile(pathFxml);

        String pathStyle = FileUtils.getFilePath(PATH_STYLE).replaceAll(APP_CODE_SHOPPING, APP_CODE_BASE);
        copyFile(pathStyle);
    }

    /**
     * 复制文件
     *
     * @param path
     * @author: humm23693
     * @date: 2022/1/9
     * @return: void
     */
    private void copyFile(String path) {
        File[] StyleFileList = new File(path).listFiles();
        for (File file : StyleFileList) {
            String filePath = file.getAbsolutePath();
            try {
                File targetFile = new File(filePath.replaceAll(APP_CODE_BASE, APP_CODE_SHOPPING));
                targetFile.getParentFile().mkdirs();
                FileUtils.copyFile(new File(filePath), targetFile);
            } catch (Exception e) {
                LoggerUtils.info(e);
            }
        }
    }

    /**
     * 是否需要更新
     *
     * @param item
     * @param keys
     * @author: humm23693
     * @date: 2021/05/22
     * @return:
     */
    private boolean isUpdate(String item, Map<String, String> keys) {
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


}
