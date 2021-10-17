package com.hoomoomoo.im.utils.generate;

import com.hoomoomoo.im.dto.GenerateCodeDto;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils.generate
 * @date 2021/10/17
 */
public class GenerateSql {

    public static String init(GenerateCodeDto generateCodeDto) throws Exception {
        String menuFile = "07console-fund-ta-vue-menu.sql";
        String flowFile = "tbworkflowsubtrans-fund.sql";
        StringBuilder content = new StringBuilder();
        String firstMenu = generateCodeDto.getMenuList().get(0)[0];
        String secondMenu = generateCodeDto.getMenuList().get(1)[0];
        String secondMenuName = generateCodeDto.getMenuList().get(1)[1];
        String functionCode = generateCodeDto.getFunctionCode();
        String functionName = generateCodeDto.getFunctionName();

        content.append("-- 自建业务--二级目录 - " + secondMenuName + "").append(SYMBOL_NEXT_LINE);
        content.append("insert into tsys_menu (menu_code, kind_code, trans_code, sub_trans_code, menu_name, menu_arg, menu_icon, window_type, tip, hot_key, parent_code, order_no, open_flag, tree_idx, remark, window_model)").append(SYMBOL_NEXT_LINE);
        content.append("values ('" + secondMenu + "', 'console-fund-ta-vue', 'menu', 'menu', '" + secondMenuName + "', ' ', 'u-a-systemmenu', ' ', ' ', ' ', '" + firstMenu + "', 5, ' ', '/console-fund-ta-vue/" + firstMenu + "/" + secondMenu + "/', ' ', ' ');").append(SYMBOL_NEXT_LINE_2);

        content.append("-- 自建业务--三级目录 - " + secondMenuName + " - " + functionName).append(SYMBOL_NEXT_LINE);
        content.append("insert into tsys_menu (menu_code, kind_code, trans_code, sub_trans_code, menu_name, menu_arg, menu_icon, window_type, tip, hot_key, parent_code, order_no, open_flag, tree_idx, remark, window_model)").append(SYMBOL_NEXT_LINE);
        content.append("values ('" + functionCode + "', 'console-fund-ta-vue', '" + functionCode + "', '" + functionCode + "Query', '" + functionName + "', ' ', 'u-a-systemmenu', ' ', ' ', ' ', '" + secondMenu + "', 1, ' ', '/console-fund-ta-vue/" + firstMenu + "/" + secondMenu + "/" + functionCode + "', ' ', ' ');").append(SYMBOL_NEXT_LINE_2);

        content.append("-- 自建业务--三级目录 - " + secondMenuName + " - " + functionName).append(SYMBOL_NEXT_LINE);
        content.append("insert into tsys_trans (trans_code, trans_name, kind_code, model_code, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
        content.append("values ('" + functionCode + "', '" + functionName + "', 'ifmmanage', ' ', ' ', ' ', ' ', ' ');").append(SYMBOL_NEXT_LINE);

        content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
        content.append("values ('" + functionCode + "', '" + functionCode + "Query', '" + functionName + "查询', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);
        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + functionCode + "', '" + functionCode + "Add', '" + functionName + "新增', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + functionCode + "', '" + functionCode + "Edit', '" + functionName + "修改', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + functionCode + "', '" + functionCode + "Delete', '" + functionName + "删除', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + functionCode + "', '" + functionCode + "Import', '" + functionName + "导入', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);
        }
        content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
        content.append("values ('" + functionCode + "', '" + functionCode + "Export', '" + functionName + "导出', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);

        String sqlFile = GenerateCommon.generateSqlFile(generateCodeDto, menuFile, content.toString());

        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content = new StringBuilder();
            content.append("-- " + functionName).append(SYMBOL_NEXT_LINE);
            content.append("insert into tbworkflowsubtrans (trans_code, sub_trans_code, visable, action_class, reserve1, reserve2, reserve3, prd_type)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + functionCode + "', '" + functionCode + "Add', '1', '" + generateCodeDto.getAuditServicePackageName() + "', ' ', ' ', ' ', '5');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tbworkflowsubtrans (trans_code, sub_trans_code, visable, action_class, reserve1, reserve2, reserve3, prd_type)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + functionCode + "', '" + functionCode + "Edit', '1', '" + generateCodeDto.getAuditServicePackageName() + "', ' ', ' ', ' ', '5');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tbworkflowsubtrans (trans_code, sub_trans_code, visable, action_class, reserve1, reserve2, reserve3, prd_type)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + functionCode + "', '" + functionCode + "Delete', '1', '" + generateCodeDto.getAuditServicePackageName() + "', ' ', ' ', ' ', '5');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tbworkflowsubtrans (trans_code, sub_trans_code, visable, action_class, reserve1, reserve2, reserve3, prd_type)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + functionCode + "', '" + functionCode + "Import', '1', '" + generateCodeDto.getAuditServicePackageName() + "', ' ', ' ', ' ', '5');").append(SYMBOL_NEXT_LINE);

            sqlFile += SYMBOL_COMMA + GenerateCommon.generateSqlFile(generateCodeDto, flowFile, content.toString());
        }
        return sqlFile;
    }
}
