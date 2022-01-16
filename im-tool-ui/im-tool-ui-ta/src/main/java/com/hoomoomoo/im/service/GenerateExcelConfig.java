package com.hoomoomoo.im.service;

import com.hoomoomoo.im.dto.ColumnInfoDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.service.generate
 * @date 2021/10/15
 */
public class GenerateExcelConfig {

    public static String init(GenerateCodeDto generateCodeDto) throws Exception {
        if (PAGE_TYPE_QUERY.equals(generateCodeDto.getPageType())) {
            return SYMBOL_EMPTY;
        }
        String fileName = generateCodeDto.getImportName();
        String packageName = generateCodeDto.getImportPackageName().replace(SYMBOL_POINT + fileName, SYMBOL_EMPTY);

        StringBuilder content = new StringBuilder(GenerateCommon.generateFileDescribe(generateCodeDto, fileName, packageName));

        content.append("import com.hundsun.lcpt.fund.annotation.FundExcelUpLoadField;").append(SYMBOL_NEXT_LINE);
        content.append("import lombok.Data;").append(SYMBOL_NEXT_LINE);
        content.append("import org.springframework.web.bind.annotation.*;").append(SYMBOL_NEXT_LINE);
        content.append("import static com.hundsun.lcpt.fund.annotation.FundExcelUpLoadField.*;").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateClassDescribe(generateCodeDto, fileName));

        content.append("@Data").append(SYMBOL_NEXT_LINE);
        content.append("public class " + fileName + " {").append(SYMBOL_NEXT_LINE_2);
        content.append("    public static final String HUNDSUN_VERSION = \"@system 理财登记过户平台 @version 6.0.0.0 @lastModiDate " + CommonUtils.getCurrentDateTime3() + " @describe " + generateCodeDto.getAuthor() + "\";").append(SYMBOL_NEXT_LINE_2);

        content.append("    // 请检查各项配置数据是否正确, 特别是配置项isRequired").append(SYMBOL_NEXT_LINE_2);

        Map<String, ColumnInfoDto> tableColumn = generateCodeDto.getColumnMap();
        Iterator<String> iterator = tableColumn.keySet().iterator();
        Map<String, String> primaryKeyMap = generateCodeDto.getPrimaryKeyMap();
        while (iterator.hasNext()) {
            String column = iterator.next();
            if (GenerateCommon.skipColumn(column)) {
                continue;
            }
            ColumnInfoDto columnInfo = tableColumn.get(column);
            boolean required = STR_1.equals(columnInfo.getColumnRequired());
            content.append("    @FundExcelUpLoadField(title = \"" + columnInfo.getColumnName() + "\", colum = 1, claz = " + fileName + ".class, isRequired = " + required);
            if (StringUtils.isNotEmpty(columnInfo.getColumnDict())) {
                content.append(", type = DICT, dict = \"" + columnInfo.getColumnDict() + "\"");
            }
            if (primaryKeyMap.containsKey(columnInfo.getColumnUnderline())) {
                content.append(", primaryKey = true");
            }
            if (KEY_PRD_CODE.equals(column)) {
                content.append(SYMBOL_NEXT_LINE).append("            , checkMethod = \"[{\\\"validator\\\":\\\"checkIsAllProductExist\\\",\\\"message\\\":\\\"基金代码不存在或者状态为【6:基金终止】或【9:发行失败】\\\"}]\"");
            } else if (KEY_SELLER_CODE.equals(column)) {
                content.append(SYMBOL_NEXT_LINE).append("            , checkMethod = \"[{\\\"validator\\\":\\\"checkIsAllSeller\\\",\\\"message\\\":\\\"销售商代码不存在或者状态为【T:注销】\\\"}]\"");
            } else if (KEY_BRANCH_NO.equals(column)) {
                content.append(SYMBOL_NEXT_LINE).append("            , checkMethod = \"[{\\\"validator\\\":\\\"checkBranchInfo\\\",\\\"message\\\":\\\"销售商网点代码不存在\\\"}]\"");
            }
            content.append(")").append(SYMBOL_NEXT_LINE);
            content.append("    private String " + column + ";").append(SYMBOL_NEXT_LINE_2);
        }
        content.append("}").append(SYMBOL_NEXT_LINE_2);
        return GenerateCommon.generateJavaFile(generateCodeDto, packageName, fileName, content.toString());
    }

    public static void getPackageName(GenerateCodeDto generateCodeDto) {
        String fileName = CommonUtils.initialUpper(generateCodeDto.getFunctionCode()) + "ExcelConfig";
        String packageName = PACKAGE_JAVA_PREFIX + "bean.excelconfig";

        generateCodeDto.setImportName(fileName);
        generateCodeDto.setImportPackageName(packageName + SYMBOL_POINT + fileName);
    }
}