package com.hoomoomoo.im.service;

import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.service.generate
 * @date 2021/10/15
 */
public class GenerateInterface {

    public static String init(GenerateCodeDto generateCodeDto) throws Exception {
        String fileName = "I" + CommonUtils.initialUpper(generateCodeDto.getFunctionCode()) + "Service";
        String packageName = PACKAGE_JAVA_PREFIX + "interfaces." + generateCodeDto.getMenuList().get(0)[0];

        generateCodeDto.setInterfaceName(fileName);
        generateCodeDto.setInterfacePackageName(packageName + STR_POINT + fileName);

        StringBuilder content = new StringBuilder(GenerateCommon.generateFileDescribe(generateCodeDto, fileName, packageName));

        content.append("import com.hundsun.jres.impl.bizkernel.runtime.exception.BizBussinessException;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.jres.interfaces.share.dataset.IDataset;").append(STR_NEXT_LINE);
        content.append("import ").append(generateCodeDto.getDtoPackageName() + ";").append(STR_NEXT_LINE_2);

        content.append(GenerateCommon.generateClassDescribe(generateCodeDto, fileName));

        content.append("public interface " + fileName + " {").append(STR_NEXT_LINE_2);
        content.append("    public static final String HUNDSUN_VERSION = \"@system 理财登记过户平台 @version 6.0.0.0 @lastModiDate " + CommonUtils.getCurrentDateTime3() + " @describe " + generateCodeDto.getAuthor() + "\";").append(STR_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_QUERY, METHOD_RETURN_PARAM_IDATASET));
        content.append("    IDataset queryService (" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException;").append(STR_NEXT_LINE_2);
        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_ADD, METHOD_RETURN_PARAM_IDATASET));
            content.append("    IDataset addService (" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException;").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_EDIT, METHOD_RETURN_PARAM_IDATASET));
            content.append("    IDataset editService (" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException;").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_DELETE, METHOD_RETURN_PARAM_IDATASET));
            content.append("    IDataset deleteService (" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException;").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_IMPORT, METHOD_RETURN_PARAM_IDATASET));
            content.append("    IDataset importService (" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException;").append(STR_NEXT_LINE_2);
        }
        content.append("}").append(STR_NEXT_LINE);
        return GenerateCommon.generateJavaFile(generateCodeDto, packageName, fileName, content.toString());
    }
}
