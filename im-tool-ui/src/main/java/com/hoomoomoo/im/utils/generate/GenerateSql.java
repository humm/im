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
        String[] packageName = generateCodeDto.getPackageCode().split(SYMBOL_POINT_SLASH);
        if (packageName == null || packageName.length != 2) {
            return SYMBOL_EMPTY;
        }
        String menuFile = "07console-fund-ta-vue-menu.sql";
        String flowFile = "tbworkflowsubtrans-fund.sql";
        StringBuilder content = new StringBuilder();
        String firstMenu = packageName[0];
        String secondMenu = packageName[1];
        String secondMenuName = generateCodeDto.getSecondMenuName();
        String classCode = generateCodeDto.getClassCode();
        String describe = generateCodeDto.getDescribe();

        content.append("-- 自建业务--二级目录 - " + secondMenuName + "").append(SYMBOL_NEXT_LINE);
        content.append("insert into tsys_menu (menu_code, kind_code, trans_code, sub_trans_code, menu_name, menu_arg, menu_icon, window_type, tip, hot_key, parent_code, order_no, open_flag, tree_idx, remark, window_model)").append(SYMBOL_NEXT_LINE);
        content.append("values ('" + secondMenu + "', 'console-fund-ta-vue', 'menu', 'menu', '" + secondMenuName + "', ' ', 'u-a-systemmenu', ' ', ' ', ' ', '" + firstMenu + "', 5, ' ', '/console-fund-ta-vue/" + firstMenu + "/" + secondMenu + "/', ' ', ' ');").append(SYMBOL_NEXT_LINE_2);

        content.append("-- 自建业务--三级目录 - " + secondMenuName + " - " + describe).append(SYMBOL_NEXT_LINE);
        content.append("insert into tsys_menu (menu_code, kind_code, trans_code, sub_trans_code, menu_name, menu_arg, menu_icon, window_type, tip, hot_key, parent_code, order_no, open_flag, tree_idx, remark, window_model)").append(SYMBOL_NEXT_LINE);
        content.append("values ('" + classCode + "', 'console-fund-ta-vue', '" + classCode + "', '" + classCode + "Query', '" + describe + "', ' ', 'u-a-systemmenu', ' ', ' ', ' ', '" + secondMenu + "', 1, ' ', '/console-fund-ta-vue/" + firstMenu + "/" + secondMenu + "/" + classCode + "', ' ', ' ');").append(SYMBOL_NEXT_LINE_2);

        content.append("-- 自建业务--三级目录 - " + secondMenuName + " - " + describe).append(SYMBOL_NEXT_LINE);
        content.append("insert into tsys_trans (trans_code, trans_name, kind_code, model_code, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
        content.append("values ('" + classCode + "', '" + describe + "', 'ifmmanage', ' ', ' ', ' ', ' ', ' ');").append(SYMBOL_NEXT_LINE);

        content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
        content.append("values ('" + classCode + "', '" + classCode + "Query', '" + describe + "查询', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);
        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + classCode + "', '" + classCode + "Add', '" + describe + "新增', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + classCode + "', '" + classCode + "Edit', '" + describe + "修改', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + classCode + "', '" + classCode + "Delete', '" + describe + "删除', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + classCode + "', '" + classCode + "Import', '" + describe + "导入', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);
        }
        content.append("insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)").append(SYMBOL_NEXT_LINE);
        content.append("values ('" + classCode + "', '" + classCode + "Export', '" + describe + "导出', ' ', ' ', '0', '1', ' ', '1', ' ', ' ');").append(SYMBOL_NEXT_LINE);

        String sqlFile = GenerateCommon.generateSqlFile(generateCodeDto, menuFile, content.toString());

        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content = new StringBuilder();
            content.append("-- " + describe).append(SYMBOL_NEXT_LINE);
            content.append("insert into tbworkflowsubtrans (trans_code, sub_trans_code, visable, action_class, reserve1, reserve2, reserve3, prd_type)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + classCode + "', '" + classCode + "Add', '1', '" + generateCodeDto.getAuditServicePackageName() + "', ' ', ' ', ' ', '5');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tbworkflowsubtrans (trans_code, sub_trans_code, visable, action_class, reserve1, reserve2, reserve3, prd_type)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + classCode + "', '" + classCode + "Edit', '1', '" + generateCodeDto.getAuditServicePackageName() + "', ' ', ' ', ' ', '5');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tbworkflowsubtrans (trans_code, sub_trans_code, visable, action_class, reserve1, reserve2, reserve3, prd_type)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + classCode + "', '" + classCode + "Delete', '1', '" + generateCodeDto.getAuditServicePackageName() + "', ' ', ' ', ' ', '5');").append(SYMBOL_NEXT_LINE);

            content.append("insert into tbworkflowsubtrans (trans_code, sub_trans_code, visable, action_class, reserve1, reserve2, reserve3, prd_type)").append(SYMBOL_NEXT_LINE);
            content.append("values ('" + classCode + "', '" + classCode + "Import', '1', '" + generateCodeDto.getAuditServicePackageName() + "', ' ', ' ', ' ', '5');").append(SYMBOL_NEXT_LINE);

            sqlFile += SYMBOL_COMMA + GenerateCommon.generateSqlFile(generateCodeDto, flowFile, content.toString());
        }
        return sqlFile;
    }
}
