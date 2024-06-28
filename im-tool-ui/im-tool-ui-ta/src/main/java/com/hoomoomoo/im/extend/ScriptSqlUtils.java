package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class ScriptSqlUtils {

    public static String basePathExt = "\\sql\\pub\\001initdata\\extradata\\";
    public static String basePathRouter = "\\front\\HUI1.0\\console-fund-ta-vue\\router\\modules\\";
    public static String baseMenu = "\\sql\\pub\\001initdata\\basedata\\07console-fund-ta-vue-menu.sql";
    public static String workFlow = "\\sql\\pub\\001initdata\\basedata\\workflow\\tbworkflowsubtrans-fund.sql";
    public static String newUedPage = "\\sql\\pub\\001initdata\\basedata\\07console-fund-ta-vue-menu-new-ued.sql";
    public static String menuCondition = "\\sql\\pub\\001initdata\\basedata\\07console-fund-ta-vue-tbmenucondition.sql";

    public static String getSubTransCodeOpDir(String subTransCode, String defaultValue) {
        // 0-新增 1-修改 2-删除 3-其他 4-查询 5-下载 6-导入
        if (subTransCode.endsWith("Add")) {
            return STR_0;
        }
        if (subTransCode.endsWith("Edit") || subTransCode.endsWith("Edt")) {
            return STR_1;
        }
        if (subTransCode.endsWith("Delete") || subTransCode.endsWith("Del")) {
            return STR_2;
        }
        if (subTransCode.endsWith("Query") || subTransCode.endsWith("Qry")) {
            return STR_4;
        }
        if (subTransCode.endsWith("Export") || subTransCode.endsWith("Exp") || subTransCode.endsWith("Download") || subTransCode.endsWith("Dwn")) {
            return STR_5;
        }
        if (subTransCode.endsWith("Import") || subTransCode.endsWith("Imp")) {
            return STR_6;
        }
        return defaultValue;
    }

    public static String getTransCodeByDeleteSql(String sql) {
        String[] trans = sql.split("where")[1].split("and")[0].split("=");
        if (trans.length == 2) {
            return trans[1].replaceAll(STR_QUOTES_SINGLE, STR_BLANK).replaceAll(STR_SEMICOLON, STR_BLANK).trim();
        }
        return null;
    }

    public static String getSubTransCodeByDeleteSql(String sql) {
        String[] trans = sql.split("where")[1].split("and");
        if (trans.length == 2) {
            trans = trans[1].split("=");
            return trans[1].replaceAll(STR_QUOTES_SINGLE, STR_BLANK).replaceAll(STR_SEMICOLON, STR_BLANK).trim();
        }
        return null;
    }

    public static String getTransCode(String sql) {
        String[] trans = getTrans(sql);
        if (trans == null) {
            return null;
        }
        return trans[0].replaceAll(STR_QUOTES_SINGLE, STR_BLANK).trim();
    }

    public static String getSubTransCode(String sql) {
        String[] trans = getTrans(sql);
        if (trans == null) {
            return null;
        }
        return trans[1].replaceAll(STR_QUOTES_SINGLE, STR_BLANK).trim();
    }

    public static String getSubTransName(String sql) {
        String[] trans = getTrans(sql);
        if (trans == null) {
            return null;
        }
        return trans[2].replaceAll(STR_QUOTES_SINGLE, STR_BLANK).trim();
    }

    public static String getSubTransOpDir(String sql) {
        String[] trans = getTrans(sql);
        if (trans == null) {
            return null;
        }
        return trans[2].replaceAll(STR_QUOTES_SINGLE, STR_BLANK).trim();
    }

    public static String[] getTrans(String sql) {
        if (!sql.contains("(") || !sql.contains(")")) {
            return null;
        }
        return sql.substring(sql.indexOf("(") + 1, sql.lastIndexOf(")")).split(",");
    }

    public static String handleMenu(String item) {
        if (item.contains("values") || item.contains("VALUES")) {
            if (item.split("values").length > 1) {
                return item.split("values")[1];
            } else if (item.split("VALUES").length > 1) {
                return item.split("VALUES")[1];
            }
        }
        return null;
    }

    public static String getMenuCode(String item) {
        return getMenuElement(item, 0);
    }

    public static String getTransCodeByWorkFlow(String item) {
        return getMenuElement(item, 0);
    }

    public static String getSubTransCodeByWorkFlow(String item) {
            return getMenuElement(item, 1);
        }

    public static String getSubTransCodeByWhole(String item) {
        String transCode = null;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            transCode = menuCodeInfo[0];
            if (transCode.contains("'")) {
                transCode = transCode.split("'")[1];
            }
            String subTransCode = menuCodeInfo[1];
            if (subTransCode.contains("'")) {
                subTransCode = subTransCode.split("'")[1];
            }
            transCode += " - " + subTransCode;
        }
        return transCode;
    }

    public static String getMenuName(String item) {
        return getMenuElement(item, 4);
    }

    public static String getParentCode(String item) {
        return getMenuElement(item, 10);
    }

    public static String getTransCodeByMenu(String item) {
        return getMenuElement(item, 2);
    }

    public static String getMenuRemark(String item) {
        return getMenuElement(item, 14);
    }

    public static String getTransCodeAndSubTransCodeByMenu(String item) {
        return getMenuElement(item, 2) + " - " + getMenuElement(item, 3);
    }

    public static String getOrderNo(String item) {
        return getMenuElement(item, 11);
    }

    public static String getMenuElement(String ele, int index) {
        String menu = null;
        String item = handleMenu(ele);
        try {
            if (item != null) {
                String[] menuCodeInfo = item.split(",");
                menu = menuCodeInfo[index];
                if (menu.contains("'")) {
                    menu = menu.split("'")[1];
                }
            }
        } catch (Exception e){
            LoggerUtils.info(e);
        }
        return menu;
    }

    public static String getMenuReserve(String item) {
        String menuReserve = STR_BLANK;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            if (menuCodeInfo.length >= 17) {
                for (int i=17; i<menuCodeInfo.length; i++) {
                    String ele =  menuCodeInfo[i];
                    if (ele.contains("'")) {
                        if (ele.trim().startsWith("'")) {
                            ele = ele.split("'")[1];
                        } else {
                            ele = ele.split("'")[0];
                        }
                    }
                    menuReserve += ele + STR_COMMA;
                }
            }
        }
        if (menuReserve.contains("'")) {
            menuReserve = menuReserve.split("'")[0];
        }
        if (menuReserve.contains(STR_BRACKETS_RIGHT)) {
            menuReserve = menuReserve.substring(0, menuReserve.lastIndexOf(STR_BRACKETS_RIGHT));
        }
        if (menuReserve.endsWith(STR_COMMA)) {
            menuReserve = menuReserve.substring(0, menuReserve.lastIndexOf(STR_COMMA));
        }
        return menuReserve;
    }

    public static int getMenuValueLen(String item) {
        if (!item.contains("(") || !item.contains(")")) {
            return -1;
        }
        if (item.toLowerCase().contains("insert") && item.toLowerCase().contains("values")) {
            return -1;
        }
        return item.substring(item.indexOf("(") + 1, item.lastIndexOf(")")).split(",").length;
    }


    public static String getTransCodeByWhole(String item) {
        String transCode = null;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            transCode = menuCodeInfo[0];
            if (transCode.contains("'")) {
                transCode = transCode.split("'")[1];
            }
        }
        return transCode;
    }

    public static String getSubTransNameByWhole(String item) {
        String transName = null;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            transName = menuCodeInfo[2];
            if (transName.contains("'")) {
                transName = transName.split("'")[1];
            }
        }
        return transName;
    }

    public static String getSubTransOpDirByWhole(String item) {
        String transName = null;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            transName = menuCodeInfo[2];
            if (transName.contains("'")) {
                transName = transName.split("'")[1];
            }
        }
        return transName;
    }

    public static List<String> getSqlByFile(String filePath) throws IOException {
        List<String> menuList = FileUtils.readNormalFile(filePath, false);
        StringBuilder menu = new StringBuilder();
        for (int i=0; i<menuList.size(); i++) {
            String item = menuList.get(i).trim();
            String itemLower = item.toLowerCase();
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if ((!itemLower.contains("insert") && !itemLower.contains("values")) || itemLower.contains(" delete ")) {
                continue;
            }
            menu.append(item);
        }
        String[] menuBase = menu.toString().split(STR_SEMICOLON);
        return new ArrayList<>(Arrays.asList(menuBase));
    }

    public static String getTableName(String sql) {
        int tableNameStartIndex = sql.toLowerCase().indexOf("into");
        int tableNameStartEnd = sql.toLowerCase().indexOf("(");
        if (tableNameStartIndex >= 0 && tableNameStartEnd >=0) {
            return sql.substring(tableNameStartIndex + 4, tableNameStartEnd).toLowerCase().trim();
        }
        return STR_BLANK;
    }
}
