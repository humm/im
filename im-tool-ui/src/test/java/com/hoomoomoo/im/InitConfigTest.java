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
        String confPath = FileUtils.getFilePath("/conf/app.conf").getPath();
        List<String> content = FileUtils.readNormalFile(confPath, false);
        // 生成证书信息
        String appFunction = content.get(1);
        String effectiveDate = content.get(3);
        int indexFunction = appFunction.indexOf(STR_SYMBOL_EQUALS) + 1;
        int indexEffectiveDate = effectiveDate.indexOf(STR_SYMBOL_EQUALS) + 1;
        appFunction = appFunction.substring(indexFunction);
        effectiveDate = effectiveDate.substring(indexEffectiveDate);
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
            String[] functionConfig = appFunction.split(STR_SYMBOL_COMMA);
            for (String code : functionConfig) {
                String name = FunctionConfig.getName(code);
                if (StringUtils.isNotBlank(name)) {
                    function.add(new FunctionDto(code, name));
                }
            }
        }
        // 加密写文件
        String licenseContent = SecurityUtils.getEncryptString(JSON.toJSONString(license));
        String licensePath = FileUtils.getFilePath("/conf/init/license.init").getPath();
        FileUtils.writeFile(licensePath, licenseContent, false);
        // 生成功能配置描述信息
        buildFunctionDescribe(license);
    }

    /**
     * 生成功能配置描述信息
     *
     * @param licenseDto
     * @author: humm23693
     * @date: 2021/05/22
     * @return:
     */
    private void buildFunctionDescribe(LicenseDto licenseDto) throws Exception {
        StringBuilder describe = new StringBuilder("# 默认展示tab页,多个逗号分隔 ");
        List<FunctionDto> function = licenseDto.getFunction();
        for (FunctionDto functionDto : function) {
            describe.append(functionDto.getFunctionCode() + STR_SYMBOL_COLON + FunctionConfig.getName(functionDto.getFunctionCode()));
            describe.append(STR_SPACE);
        }
        String confPath = FileUtils.getFilePath("/conf/app.conf").getPath();
        List<String> content = FileUtils.readNormalFile(confPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            content.set(7, describe.toString());
        }
        // 获取功能配置信息
        content = content.subList(4, content.size());
        List<String> appInfo = new ArrayList<>(16);
        List<String> svnInfo = new ArrayList<>(16);
        List<String> svnLog = new ArrayList<>(16);
        List<String> svnUpdate = new ArrayList<>(16);
        List<String> fundInfo = new ArrayList<>(16);
        List<String> processInfo = new ArrayList<>(16);
        List<String> scriptUpdate = new ArrayList<>(16);
        int configType = -1;
        int times = 0;
        for (String item : content) {
            if (item.contains(STR_CONFIG_PREFIX + STR_CONFIG_APP_TITLE)
                    || item.contains(STR_CONFIG_PREFIX + STR_CONFIG_SVN_TITLE)
                    || item.contains(STR_CONFIG_PREFIX + SVN_LOG.getName())
                    || item.contains(STR_CONFIG_PREFIX + SVN_UPDATE.getName())
                    || item.contains(STR_CONFIG_PREFIX + FUND_INFO.getName())
                    || item.contains(STR_CONFIG_PREFIX + PROCESS_INFO.getName())
                    || item.contains(STR_CONFIG_PREFIX + SCRIPT_UPDATE.getName())
            ) {
                times++;
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
                default:
                    break;
            }
            if (StringUtils.isBlank(item.trim()) && times == 2) {
                times = 0;
                configType++;
            }
        }
        List<String> config = new ArrayList<>(16);
        config.addAll(appInfo);
        if (authFunction(licenseDto.getFunction(), SVN_LOG.getCode()) || authFunction(licenseDto.getFunction(), SVN_UPDATE.getCode())) {
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
        // 保存授权功能配置信息 删除授权证书相关配置项
        FileUtils.writeFile(confPath, config, false);
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

        keys.add("svn.update.ta6");

        keys.add("fund.excel.path");
        keys.add("fund.generate.path");

        keys.add("process.excel.path");
        keys.add("process.generate.path.trans");
        keys.add("process.generate.path.schedule");

        keys.add("script.update.table.tbscheduletask");
        keys.add("script.update.generate.path");

        String confPath = FileUtils.getFilePath("/conf/app.conf").getPath();
        List<String> content = FileUtils.readNormalFile(confPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i);
                if (isUpdate(item, keys)) {
                    int index = item.indexOf(STR_SYMBOL_EQUALS) + 1;
                    item = item.substring(0, index) + STR_EMPTY;
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
            String versionFilePath = FileUtils.getFilePath("/conf/init/version.init").getPath();
            String versionFilePathSource = versionFilePath.replace("/target/classes", "/src/main/resources");
            StringBuilder statLog = new StringBuilder();
            statLog.append("首发版本: ").append("2021.04.17").append(STR_SYMBOL_NEXT_LINE);
            statLog.append("首发时间: ").append("2021-04-17 23:17:56").append(STR_SYMBOL_NEXT_LINE_2);
            statLog.append("当前版本: ").append(CommonUtils.getCurrentDateTime4().replace(STR_SYMBOL_HYPHEN, STR_SYMBOL_POINT)).append(STR_SYMBOL_NEXT_LINE);
            statLog.append("发布时间: ").append(CommonUtils.getCurrentDateTime1()).append(STR_SYMBOL_NEXT_LINE);
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
        if (!item.contains(STR_SYMBOL_EQUALS)) {
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
