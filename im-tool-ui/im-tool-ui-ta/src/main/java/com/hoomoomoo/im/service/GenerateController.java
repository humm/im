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
public class GenerateController {

    public static String init(GenerateCodeDto generateCodeDto) throws Exception {
        String fileName = CommonUtils.initialUpper(generateCodeDto.getFunctionCode()) + "Controller";
        String packageName = PACKAGE_JAVA_PREFIX + "controller." + generateCodeDto.getMenuList().get(0)[0];

        generateCodeDto.setControllerName(fileName);
        generateCodeDto.setControllerPackageName(packageName + STR_POINT + fileName);

        StringBuilder content = new StringBuilder(GenerateCommon.generateFileDescribe(generateCodeDto, fileName, packageName));

        content.append("import com.hundsun.lcpt.bizframe.core.util.RequestResponseUtil;").append(STR_NEXT_LINE);
        content.append("import " + generateCodeDto.getDtoPackageName() + ";").append(STR_NEXT_LINE);
        content.append("import " + generateCodeDto.getInterfacePackageName() + ";").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.fund.util.FundRequestResponseUtil;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.pub.util.TaDtoUtil;").append(STR_NEXT_LINE);
        content.append("import org.springframework.beans.factory.annotation.Autowired;").append(STR_NEXT_LINE);
        content.append("import org.springframework.web.bind.annotation.*;").append(STR_NEXT_LINE);

        content.append(GenerateCommon.generateClassDescribe(generateCodeDto, fileName));

        content.append("@RestController").append(STR_NEXT_LINE);
        content.append("@CrossOrigin").append(STR_NEXT_LINE);
        content.append("public class " + fileName + " {").append(STR_NEXT_LINE_2);
        content.append("    public static final String HUNDSUN_VERSION = \"@system 理财登记过户平台 @version 6.0.0.0 @lastModiDate " + CommonUtils.getCurrentDateTime3() + " @describe " + generateCodeDto.getAuthor() + "\";").append(STR_NEXT_LINE_2);

        String serviceName = CommonUtils.initialLower(generateCodeDto.getInterfaceName().substring(1));
        content.append("    @Autowired").append(STR_NEXT_LINE);
        content.append("    private " + generateCodeDto.getInterfaceName() + " " + serviceName + ";").append(STR_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_QUERY, null, METHOD_RETURN_PARAM_STRING, METHOD_REQUEST_PARAM_DTO));
        content.append("    @PostMapping(value = \"/" + generateCodeDto.getFunctionCode() + "/" + generateCodeDto.getFunctionCode() + "Query\")").append(STR_NEXT_LINE);
        content.append("    public String queryService(" + generateCodeDto.getDtoNameDto() + " dto) throws Exception {").append(STR_NEXT_LINE);
        content.append("        TaDtoUtil.setPageRange(dto);").append(STR_NEXT_LINE);
        content.append("        return RequestResponseUtil.getResultJsonString(" + serviceName + ".queryService(dto));").append(STR_NEXT_LINE);
        content.append("    }").append(STR_NEXT_LINE_2);

        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_ADD, null, METHOD_RETURN_PARAM_STRING, METHOD_REQUEST_PARAM_DTO));
            content.append("    @PostMapping(value = \"/" + generateCodeDto.getFunctionCode() + "/" + generateCodeDto.getFunctionCode() + "Add\")").append(STR_NEXT_LINE);
            content.append("    public String addService(" + generateCodeDto.getDtoNameDto() + " dto) throws Exception {").append(STR_NEXT_LINE);
            content.append("        return RequestResponseUtil.getResultJsonString(" + serviceName + ".addService(dto));").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_EDIT, null, METHOD_RETURN_PARAM_STRING, METHOD_REQUEST_PARAM_DTO));
            content.append("    @PostMapping(value = \"/" + generateCodeDto.getFunctionCode() + "/" + generateCodeDto.getFunctionCode() + "Edit\")").append(STR_NEXT_LINE);
            content.append("    public String editService(" + generateCodeDto.getDtoNameDto() + " dto) throws Exception {").append(STR_NEXT_LINE);
            content.append("        return RequestResponseUtil.getResultJsonString(" + serviceName + ".editService(dto));").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_DELETE, null, METHOD_RETURN_PARAM_STRING, METHOD_REQUEST_PARAM_DTO));
            content.append("    @PostMapping(value = \"/" + generateCodeDto.getFunctionCode() + "/" + generateCodeDto.getFunctionCode() + "Delete\")").append(STR_NEXT_LINE);
            content.append("    public String deleteService(" + generateCodeDto.getDtoNameDto() + " dto) throws Exception {").append(STR_NEXT_LINE);
            content.append("        return RequestResponseUtil.getResultJsonString(" + serviceName + ".deleteService(dto));").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_IMPORT, null, METHOD_RETURN_PARAM_STRING, METHOD_REQUEST_PARAM_DTO));
            content.append("    @PostMapping(value = \"/" + generateCodeDto.getFunctionCode() + "/" + generateCodeDto.getFunctionCode() + "Import\")").append(STR_NEXT_LINE);
            content.append("    public String importService(" + generateCodeDto.getDtoNameDto() + " dto) throws Exception {").append(STR_NEXT_LINE);
            content.append("        return RequestResponseUtil.getResultJsonString(" + serviceName + ".importService(dto));").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);
        }
        content.append("}").append(STR_NEXT_LINE_2);
        return GenerateCommon.generateJavaFile(generateCodeDto, packageName, fileName, content.toString());
    }
}
