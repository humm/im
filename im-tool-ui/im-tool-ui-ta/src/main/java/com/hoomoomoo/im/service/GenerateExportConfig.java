package com.hoomoomoo.im.service;

import com.hoomoomoo.im.dto.ColumnInfoDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.TaCommonUtil;
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
public class GenerateExportConfig {

    public static String init(GenerateCodeDto generateCodeDto) throws Exception {
        String fileName = CommonUtils.initialUpper(generateCodeDto.getFunctionCode()) + "ExportConfig";
        String packageName = PACKAGE_JAVA_PREFIX + "bean.exportconfig";

        generateCodeDto.setExportName(fileName);
        generateCodeDto.setExportPackageName(packageName + SYMBOL_POINT + fileName);

        StringBuilder content = new StringBuilder(GenerateCommon.generateFileDescribe(generateCodeDto, fileName, packageName));

        content.append("import com.hundsun.lcpt.fund.annotation.FundExcelExportField;").append(SYMBOL_NEXT_LINE);
        content.append("import lombok.Data;").append(SYMBOL_NEXT_LINE);
        content.append("import static com.hundsun.lcpt.fund.annotation.FundExcelExportField.*;").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateClassDescribe(generateCodeDto, fileName));

        content.append("@Data").append(SYMBOL_NEXT_LINE);
        content.append("public class " + fileName + " {").append(SYMBOL_NEXT_LINE_2);
        content.append("    public static final String HUNDSUN_VERSION = \"@system 理财登记过户平台 @version 6.0.0.0 @lastModiDate " + CommonUtils.getCurrentDateTime3() + " @describe " + generateCodeDto.getAuthor() + "\";").append(SYMBOL_NEXT_LINE_2);

        content.append("    // 请检查各项配置数据是否正确, 特别是配置项type").append(SYMBOL_NEXT_LINE_2);

        Map<String, ColumnInfoDto> tableColumn = generateCodeDto.getColumnMap();
        Iterator<String> iterator = tableColumn.keySet().iterator();
        while (iterator.hasNext()) {
            String column = iterator.next();
            ColumnInfoDto columnInfo = tableColumn.get(column);
            if (GenerateCommon.skipColumn(columnInfo)) {
                continue;
            }
            String columnType = columnInfo.getColumnType();
            if (!KEY_COLUMN_TYPE_INTEGER.equals(columnType) && !KEY_COLUMN_TYPE_NUMBER.equals(columnType) && !KEY_COLUMN_TYPE_DATE.equals(columnType)) {
                continue;
            }
            content.append("    @FundExcelExportField(title = \"" + columnInfo.getColumnName() + "\"");
            if (KEY_COLUMN_TYPE_DATE.equals(columnType)) {
                content.append(", type = FundExcelExportField.DATE");
            } else if (KEY_COLUMN_TYPE_NUMBER.equals(columnType)) {
                if(columnInfo.getColumnName().contains(SYMBOL_PERCENT)) {
                    content.append(", type = FundExcelExportField.RATIO");
                } else {
                    content.append(", type = FundExcelExportField.AMOUNT");
                }
                if (StringUtils.isNotBlank(columnInfo.getColumnPrecision())) {
                    int precision = GenerateCommon.getColumnPrecision(columnInfo);
                    if (precision != 2) {
                        content.append(", suffixNum= " + precision);
                    }
                }
            }
            content.append(")").append(SYMBOL_NEXT_LINE);
            content.append("    private String " + column + ";").append(SYMBOL_NEXT_LINE_2);
        }
        content.append("}").append(SYMBOL_NEXT_LINE_2);
        return GenerateCommon.generateJavaFile(generateCodeDto, packageName, fileName, content.toString());
    }
}
