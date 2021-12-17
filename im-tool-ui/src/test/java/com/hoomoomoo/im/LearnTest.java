package com.hoomoomoo.im;

import com.hoomoomoo.im.utils.FileUtils;
import lombok.SneakyThrows;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2021/12/13
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LearnTest {

    @SneakyThrows
    @Test
    public void config() {
        /*String ptah = "E:\\workspace\\ta6\\sql\\pub\\fund\\001initdata\\basedata\\07console-fund-ta-vue-menu.sql";
        List<String> content = FileUtils.readNormalFile(ptah, false);
        Map<String, String> menu = new HashMap<>();
        Map<String, String> secMenu = new HashMap<>();
        for (int i=0; i<content.size(); i++) {
            String item = content.get(i);
            boolean explain = item.contains("--");
            String insert = "";
            String value = "";
            if (explain) {
                insert += "-- ";
                value += "";
            }
            // 二级菜单
            if (item.contains("'fundAnalysis'")) {
                insert += "insert into tsys_menu (menu_code, kind_code, trans_code, sub_trans_code, menu_name, menu_arg, menu_icon, window_type, tip, hot_key, parent_code, order_no, open_flag, tree_idx, remark, window_model) \n";
                int start = item.indexOf("'");
                int end = item.indexOf(",");
                String menuCode = item.substring(start + 1, end -1);
                value += item.replace("'fundAnalysis'", "'fundAnalysisByTrans'").replaceFirst(menuCode, menuCode + "T");
                if (menuCode.equals("fundAnalysis")) {
                    continue;
                }
                item += "\n\n" + insert + value;
                content.set(i, item);
                menu.put(menuCode, menuCode);
            } else if (!item.toLowerCase().contains("insert")){
                // 三级菜单
                insert += "insert into tsys_menu (menu_code, kind_code, trans_code, sub_trans_code, menu_name, menu_arg, menu_icon, window_type, tip, hot_key, parent_code, order_no, open_flag, tree_idx, remark, window_model) \n";
                String[] subItem = item.split(",");
                if (subItem.length <10) {
                    continue;
                }
                String menuCode = subItem[0].substring(subItem[0].indexOf("'") + 1, subItem[0].lastIndexOf("'"));
                String secMenuCode = subItem[10].substring(subItem[10].indexOf("'") + 1, subItem[10].lastIndexOf("'"));
                if (menu.containsKey(secMenuCode)) {
                    value += item.replace(menuCode, menuCode + "T").replaceFirst(secMenuCode, secMenuCode + "T");
                    item += "\n\n" + insert + value;
                    content.set(i, item);
                    secMenu.put(menuCode, menuCode);
                    continue;
                }
                // 功能权限
                if (secMenu.containsKey(menuCode) && item.contains(menuCode + "Export")) {
                    String flag = item.contains("--") ? "-- " : "";
                    String temp = flag + "insert into tsys_trans (trans_code, trans_name, kind_code, model_code, remark, ext_field_1, ext_field_2, ext_field_3)\n";
                    temp += flag +"values ('"+ menuCode + "T"+"', "+subItem[2].replace("导出", "")+", 'ifmmanage', ' ', ' " + "', '" +" ', ' ', ' ');" + "\n";
                    String ext = flag + "insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)\n";

                    temp += ext;
                    temp += item.replace(menuCode, menuCode + "T").replace("Export", "Query");
                    if (item.contains("查询")) {
                        temp = temp.replace("导出", "");
                    } else {
                        temp = temp.replace("导出", "查询");
                    }

                    temp += "\n" + ext;
                    temp += item.replace(menuCode, menuCode + "T");
                    item += "\n\n" + temp;
                    content.set(i, item);
                }
            }

        }
        FileUtils.writeFile(ptah.replaceAll("\\\\", "/"), content, false);*/
    }
}

