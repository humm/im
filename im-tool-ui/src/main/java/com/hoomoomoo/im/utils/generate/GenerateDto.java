package com.hoomoomoo.im.utils.generate;

import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils.generate
 * @date 2021/10/15
 */
public class GenerateDto {

    public static String init(GenerateCodeDto generateCodeDto) throws IOException {
        String fileName = CommonUtils.initialUpper(generateCodeDto.getDtoCode()) + "DTO";
        String packageName = PACKAGE_NAME_PREFIX + "dto." + generateCodeDto.getMenuList().get(0)[0] + "." + generateCodeDto.getMenuList().get(1)[0];

        generateCodeDto.setDtoNameDto(fileName);
        generateCodeDto.setDtoPackageName(packageName + SYMBOL_POINT + fileName);

        StringBuilder content = new StringBuilder(GenerateCommon.generateFileDescribe(generateCodeDto, fileName, packageName));

        content.append("import com.hundsun.lcpt.ta.pub.dto.TaBaseDto;").append(SYMBOL_NEXT_LINE);
        content.append("import lombok.Data;").append(SYMBOL_NEXT_LINE);
        content.append("import java.io.Serializable;").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateClassDescribe(generateCodeDto, fileName));

        content.append("@Data").append(SYMBOL_NEXT_LINE);
        content.append("public class " + fileName + " extends TaBaseDto implements Serializable {").append(SYMBOL_NEXT_LINE_2);
        content.append("    public static final String HUNDSUN_VERSION = \"@system 理财登记过户平台 @version 6.0.0.0 @lastModiDate " + CommonUtils.getCurrentDateTime3() + " @describe " + generateCodeDto.getAuthor() + "\";").append(SYMBOL_NEXT_LINE_2);

        content.append("    private static final long serialVersionUID = " + GenerateCommon.getSerialVersionUid() + ";").append(SYMBOL_NEXT_LINE);
        Map<String, Map<String, String>> columnMap = generateCodeDto.getColumnMap();
        Iterator<String> iterator = columnMap.keySet().iterator();
        while (iterator.hasNext()) {
            String column = iterator.next();
            content.append("    private String " + column + ";").append(SYMBOL_NEXT_LINE);
        }

        content.append("}").append(SYMBOL_NEXT_LINE);
        return GenerateCommon.generateJavaFile(generateCodeDto, packageName, fileName, content.toString());
    }
}
