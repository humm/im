package com.hoomoomoo.im.utils.generate;

import com.hoomoomoo.im.dto.GenerateCodeDto;

import static com.hoomoomoo.im.consts.BaseConst.SYMBOL_NEXT_LINE;
import static com.hoomoomoo.im.consts.BaseConst.SYMBOL_NEXT_LINE_2;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils.generate
 * @date 2021/10/15
 */
public class GenerateRoute {

    public static String init(GenerateCodeDto generateCodeDto) throws Exception {
        String fileName = "route";
        StringBuilder content = new StringBuilder();
        String firstMenu = generateCodeDto.getMenuList().get(0)[0];
        String secondMenu = generateCodeDto.getMenuList().get(1)[0];
        String secondMenuName = generateCodeDto.getMenuList().get(1)[1];
        String functionCode = generateCodeDto.getFunctionCode();
        String functionName = generateCodeDto.getFunctionName();
        content.append("// 二级菜单 " + secondMenuName).append(SYMBOL_NEXT_LINE);
        content.append("const " + secondMenu + " = {").append(SYMBOL_NEXT_LINE);
        content.append("  // " + functionName).append(SYMBOL_NEXT_LINE);
        String route = firstMenu + "/" + secondMenu + "/" + functionCode;
        content.append("  " + functionCode + ": () => import(/* webpackChunkName: \"console-fund-ta-vue/" + route + "\" */`@ConsoleFundTaVue/views/" + route + "`)").append(SYMBOL_NEXT_LINE);
        content.append("}").append(SYMBOL_NEXT_LINE_2);
        return GenerateCommon.generateRouteFile(generateCodeDto, fileName, content.toString());
    }
}
