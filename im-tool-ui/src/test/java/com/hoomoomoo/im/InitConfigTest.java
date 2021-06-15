package com.hoomoomoo.im;

import com.alibaba.fastjson.JSON;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.LicenseDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2021/05/09
 */

public class InitConfigTest {

    /**
     * 生成授权证书
     *
     * @author: humm23693
     * @date: 2021/05/22
     * @return:
     */
    @Test
    public void buildLicense() throws Exception {
        String confPath = FileUtils.getFilePath(PATH_APP);
        List<String> content = FileUtils.readNormalFile(confPath, false);
        // 生成证书信息
        String appFunction = content.get(1);
        String effectiveDate = content.get(3);
        String svnStat = content.get(5);
        int indexFunction = appFunction.indexOf(SYMBOL_EQUALS) + 1;
        int indexEffectiveDate = effectiveDate.indexOf(SYMBOL_EQUALS) + 1;
        int indexSvnStat = svnStat.indexOf(SYMBOL_EQUALS) + 1;
        appFunction = appFunction.substring(indexFunction);
        effectiveDate = effectiveDate.substring(indexEffectiveDate);
        svnStat = svnStat.substring(indexSvnStat);
        List<FunctionDto> function = new ArrayList<>(16);
        LicenseDto license = new LicenseDto();
        license.setEffectiveDate(effectiveDate);
        license.setFunction(function);
        if (StringUtils.isBlank(appFunction)) {
            // 全功能
            for (FunctionConfig tab : FunctionConfig.values()) {
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
        // 加密写文件
        String licenseContent = SecurityUtils.getEncryptString(JSON.toJSONString(license));
        String licensePath = FileUtils.getFilePath(PATH_LICENSE);
        FileUtils.writeFile(licensePath, licenseContent, false);
        buildAppConfig(license, svnStat);
    }

    /**
     * 生成功能配置描述信息
     *
     * @param licenseDto
     * @author: humm23693
     * @date: 2021/05/22
     * @return:
     */
    private void buildAppConfig(LicenseDto licenseDto, String svnStatFlag) throws Exception {
        StringBuilder describe = new StringBuilder("# 默认展示tab页,多个逗号分隔 ");
        List<FunctionDto> function = licenseDto.getFunction();
        for (FunctionDto functionDto : function) {
            if (functionDto.getFunctionCode().equals(SVN_REALTIME_STAT.getCode())
                    || functionDto.getFunctionCode().equals(SVN_HISTORY_STAT.getCode())
                    || functionDto.getFunctionCode().equals(SVN_HISTORY_STAT.getCode())
                    || functionDto.getFunctionCode().equals(FUNCTION_STAT_INFO.getCode())
                    || functionDto.getFunctionCode().equals(ABOUT_INFO.getCode())
            ) {
                continue;
            }
            describe.append(functionDto.getFunctionCode() + SYMBOL_COLON + FunctionConfig.getName(functionDto.getFunctionCode()));
            describe.append(SYMBOL_SPACE);
        }
        String confPath = FileUtils.getFilePath(PATH_APP);
        List<String> content = FileUtils.readNormalFile(confPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            content.set(7, describe.toString());
        }
        // 获取功能配置信息
        content = content.subList(6, content.size());
        List<String> appInfo = new ArrayList<>(16);
        List<String> svnInfo = new ArrayList<>(16);
        List<String> svnLog = new ArrayList<>(16);
        List<String> svnUpdate = new ArrayList<>(16);
        List<String> fundInfo = new ArrayList<>(16);
        List<String> processInfo = new ArrayList<>(16);
        List<String> scriptUpdate = new ArrayList<>(16);
        List<String> svnStat = new ArrayList<>(16);
        List<String> realtimeStat = new ArrayList<>(16);
        List<String> historyStat = new ArrayList<>(16);
        int configType = -1;
        int times = 0;
        boolean append = false;
        for (String item : content) {
            if (item.contains(CONFIG_PREFIX + CONFIG_APP_TITLE)
                    || item.contains(CONFIG_PREFIX + CONFIG_SVN_TITLE)
                    || item.contains(CONFIG_PREFIX + CONFIG_SVN_STAT_TITLE)
                    || item.contains(CONFIG_PREFIX + SVN_LOG.getName())
                    || item.contains(CONFIG_PREFIX + SVN_UPDATE.getName())
                    || item.contains(CONFIG_PREFIX + FUND_INFO.getName())
                    || item.contains(CONFIG_PREFIX + PROCESS_INFO.getName())
                    || item.contains(CONFIG_PREFIX + SCRIPT_UPDATE.getName())
                    || item.contains(CONFIG_PREFIX + SVN_REALTIME_STAT.getName())
            ) {
                times++;
            }

            if (item.startsWith(KEY_SVN_UPDATE_LOCATION) || item.startsWith(KEY_SCRIPT_UPDATE_TABLE) || item.startsWith(KEY_SVN_STAT_USER)) {
                if (!append) {
                    append = true;
                    if (item.startsWith(KEY_SVN_UPDATE_LOCATION)) {
                        svnUpdate.add("svn.update.ta6=");
                    } else if (item.startsWith(KEY_SCRIPT_UPDATE_TABLE)) {
                        scriptUpdate.add("script.update.table.tbscheduletask=");
                    } else {
                        svnStat.add("svn.stat.user.zhangsan10000=张三");
                    }
                }
                continue;
            }
            switch (configType) {
                case -1:
                    appInfo.add(item);
                    break;
                case 0:
                    svnInfo.add(item);
                    break;
                case 1:
                    svnLog.add(item);
                    break;
                case 2:
                    svnUpdate.add(item);
                    break;
                case 3:
                    fundInfo.add(item);
                    break;
                case 4:
                    processInfo.add(item);
                    break;
                case 5:
                    scriptUpdate.add(item);
                    break;
                case 95:
                    svnStat.add(item);
                    break;
                case 96:
                    realtimeStat.add(item);
                    break;
                case 97:
                    historyStat.add(item);
                    break;
                default:
                    break;
            }
            if (StringUtils.isBlank(item.trim()) && times == 2) {
                times = 0;
                append = false;
                if (configType == 5) {
                    configType = 95;
                } else {
                    configType++;
                }
            }
        }
        List<String> config = new ArrayList<>(16);
        List<String> svnStatConfig = new ArrayList<>(16);
        config.addAll(appInfo);
        if (authFunction(licenseDto.getFunction(), SVN_LOG.getCode())
                || authFunction(licenseDto.getFunction(), SVN_UPDATE.getCode())
                || authFunction(licenseDto.getFunction(), SVN_REALTIME_STAT.getCode())
                || authFunction(licenseDto.getFunction(), SVN_HISTORY_STAT.getCode())
        ) {
            config.addAll(svnInfo);
        }
        if (authFunction(licenseDto.getFunction(), SVN_LOG.getCode())) {
            config.addAll(svnLog);
        }
        if (authFunction(licenseDto.getFunction(), SVN_UPDATE.getCode())) {
            config.addAll(svnUpdate);
        }
        if (authFunction(licenseDto.getFunction(), FUND_INFO.getCode())) {
            config.addAll(fundInfo);
        }
        if (authFunction(licenseDto.getFunction(), PROCESS_INFO.getCode())) {
            config.addAll(processInfo);
        }
        if (authFunction(licenseDto.getFunction(), SCRIPT_UPDATE.getCode())) {
            config.addAll(scriptUpdate);
        }
        if (authFunction(licenseDto.getFunction(), SVN_REALTIME_STAT.getCode()) || authFunction(licenseDto.getFunction(), SVN_HISTORY_STAT.getCode())) {
            svnStatConfig.addAll(svnStat);
        }
        if (authFunction(licenseDto.getFunction(), SVN_REALTIME_STAT.getCode())) {
            svnStatConfig.addAll(realtimeStat);
        }
        if (authFunction(licenseDto.getFunction(), SVN_HISTORY_STAT.getCode())) {
            svnStatConfig.addAll(historyStat);
        }
        if (svnStatFlag.equals(KEY_TRUE)) {
            config.addAll(svnStatConfig);
        }
        // 保存授权功能配置信息 删除授权证书相关配置项
        FileUtils.writeFile(confPath, config, false);

        // 保存svn统计配置信息
        String svnStatPath = FileUtils.getFilePath(PATH_SVN_STAT);
        // 数据加密
        StringBuilder svnStatContent = new StringBuilder();
        for (String item : svnStatConfig) {
            svnStatContent.append(item + SYMBOL_NEXT_LINE);
        }
        FileUtils.writeFile(svnStatPath, SecurityUtils.getEncryptString(svnStatContent.toString()), false);
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
    public void updateAppConfig() throws Exception {
        List<String> keys = new ArrayList<>(16);
        keys.add("show.tab");

        keys.add("svn.username");
        keys.add("svn.password");

        keys.add("svn.url");
        keys.add("svn.default.append.path");

        keys.add("fund.excel.path");
        keys.add("fund.generate.path");

        keys.add("process.excel.path");
        keys.add("process.generate.path.trans");
        keys.add("process.generate.path.schedule");

        keys.add("script.update.generate.path");

        keys.add("app.license.show");

        String confPath = FileUtils.getFilePath(PATH_APP);
        List<String> content = FileUtils.readNormalFile(confPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i);
                if (isUpdate(item, keys)) {
                    int index = item.indexOf(SYMBOL_EQUALS) + 1;
                    item = item.substring(0, index) + SYMBOL_EMPTY;
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
     * @date: 2021/05/10
     * @return:
     */
    @Test
    public void updateVersionConfig() {
        try {
            String versionFilePath = FileUtils.getFilePath(PATH_VERSION);
            String versionFilePathSource = versionFilePath.replace("/target/classes", "/src/main/resources");
            List<String> content = FileUtils.readNormalFile(versionFilePath, false);
            String subVersion = "00";
            if (CollectionUtils.isNotEmpty(content)) {
                for (int i = 0; i < content.size(); i++) {
                    String item = content.get(i);
                    if (item.startsWith("当前版本:")) {
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
                        break;
                    }
                }
            }
            StringBuilder statLog = new StringBuilder();
            statLog.append("首发版本: ").append("2021.05.06.00").append(SYMBOL_NEXT_LINE);
            statLog.append("发布时间: ").append("2021-05-06 23:17:56").append(SYMBOL_NEXT_LINE_2);
            statLog.append("当前版本: ").append(CommonUtils.getCurrentDateTime4().replace(SYMBOL_HYPHEN, SYMBOL_POINT)).append(SYMBOL_POINT).append(subVersion).append(SYMBOL_NEXT_LINE);
            statLog.append("发布时间: ").append(CommonUtils.getCurrentDateTime1()).append(SYMBOL_NEXT_LINE);
            FileUtils.writeFile(versionFilePath, statLog.toString(), false);
            FileUtils.writeFile(versionFilePathSource, statLog.toString(), false);
        } catch (Exception e) {
            LoggerUtils.info(e);
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
    private boolean isUpdate(String item, List<String> keys) {
        if (!item.contains(SYMBOL_EQUALS)) {
            return false;
        }
        for (String key : keys) {
            if (item.startsWith(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否开通对应功能
     *
     * @param function
     * @param functionCode
     * @author: humm23693
     * @date: 2021/05/22
     * @return:
     */
    private boolean authFunction(List<FunctionDto> function, String functionCode) {
        if (CollectionUtils.isEmpty(function)) {
            return false;
        }
        for (FunctionDto functionDto : function) {
            if (functionCode.equals(functionDto.getFunctionCode())) {
                return true;
            }
        }
        return false;
    }
}
