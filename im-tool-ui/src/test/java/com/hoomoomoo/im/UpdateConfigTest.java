package com.hoomoomoo.im;

import com.alibaba.fastjson.JSON;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.LicenseDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2021/05/09
 */

public class UpdateConfigTest {

    /**
     * 去除敏感信息
     *
     * @param
     * @author: humm23693
     * @date: 2021/05/10
     * @return:
     */
    @Test
    public void updateAppConfig() {
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
        try {
            String confPath = FileUtils.getFilePath("/conf/app.conf").getPath();
            List<String> content = FileUtils.readNormalFile(confPath, false);
            // 读取app.function配置项 获取授权功能
            LicenseDto licenseDto = buildLicense(content.get(1), content.get(3));
            if (CollectionUtils.isNotEmpty(content)) {
                for (int i = 0; i < content.size(); i++) {
                    String item = content.get(i);
                    // 修改功能配置描述
                    if (i == 7) {
                        content.set(i, buildFunctionDescribe(licenseDto));
                        continue;
                    }
                    if (isUpdate(item, keys)) {
                        int index = item.indexOf(STR_SYMBOL_EQUALS) + 1;
                        item = item.substring(0, index) + STR_EMPTY;
                        content.set(i, item);
                    }
                }
            }
            FileUtils.writeFile(confPath, content.subList(4, content.size()), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildFunctionDescribe(LicenseDto licenseDto) {
        StringBuilder describe = new StringBuilder("# 默认展示tab页,多个逗号分隔 ");
        List<FunctionDto> function = licenseDto.getFunction();
        for (FunctionDto functionDto : function) {
            describe.append(functionDto.getFunctionCode() + STR_SYMBOL_COLON + FunctionConfig.getName(functionDto.getFunctionCode()));
            describe.append(STR_SPACE);
        }
        return describe.toString();
    }

    /**
     * 生成授权证书
     *
     * @param appFunction
     * @author: humm23693
     * @date: 2021/05/22
     * @return:
     */
    private LicenseDto buildLicense(String appFunction, String effectiveDate) {
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
        try {
            String confPath = FileUtils.getFilePath("/conf/init/license.init").getPath();
            FileUtils.writeFile(confPath, licenseContent, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return license;
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
            e.printStackTrace();
        }
    }
}
