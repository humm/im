package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.LEGAL_EXT_MENU;

public class ScriptSqlUtils {

    public static String basePathExt = "";
    public static String basePathRouter = "\\";
    public static String baseMenu = "\\07console-fund-ta-vue-menu.sql";
    public static String workFlow = "\\workflow\\tbworkflowsubtrans-fund.sql";
    public static String newUedPage = "\\07console-fund-ta-vue-menu-new-ued.sql";
    public static String newUedHome = "\\07console-fund-ta-vue-menu-new-ued-home.sql";
    public static String menuCondition = "\\07console-fund-ta-vue-tbmenucondition.sql";
    public static String extReport = "\\Report";

    public static String getSubTransCodeOpDir(String subTransCode, String defaultValue) {
        // 0:新增 1:修改 2:删除 3:其他 4:查询 5:下载 6:导入 7:审批 8:接口 9:复制
        if (subTransCode.endsWith("Add")) {
            return STR_0;
        } else if (subTransCode.endsWith("ReEdt") || subTransCode.endsWith("Sync") || subTransCode.endsWith("Deal")
                || subTransCode.endsWith("Check") || subTransCode.endsWith("Config") || subTransCode.endsWith("Configure")
                || subTransCode.endsWith("Effect") || subTransCode.endsWith("Effective") || subTransCode.endsWith("Invalid")
                || subTransCode.endsWith("Select") || subTransCode.endsWith("Execute") || subTransCode.endsWith("Process")
                || subTransCode.endsWith("Enable") || subTransCode.endsWith("Cancel") || subTransCode.endsWith("Push")) {
            return STR_3;
        }  else if (subTransCode.endsWith("Import") || subTransCode.endsWith("Imp")) {
            return STR_6;
        } else if (subTransCode.endsWith("Edit") || subTransCode.endsWith("Edt") || subTransCode.endsWith("Update")) {
            return STR_1;
        } else if (subTransCode.endsWith("Delete") || subTransCode.endsWith("Del")) {
            return STR_2;
        } else if (subTransCode.endsWith("Query") || subTransCode.endsWith("Qry")) {
            return STR_4;
        } else if (subTransCode.endsWith("Export") || subTransCode.endsWith("Exp") || subTransCode.endsWith("Download") || subTransCode.endsWith("Dwn")) {
            return STR_5;
        } else if (subTransCode.endsWith("Copy")) {
            return STR_9;
        }
        return defaultValue;
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

    public static String[] getTrans(String sql) {
        if (!sql.contains("(") || !sql.contains(")")) {
            return null;
        }
        return sql.substring(sql.indexOf("(") + 1, sql.lastIndexOf(")")).split(",");
    }

    public static String handleSqlForValues(String item) {
        if (item.contains("values") || item.contains("VALUES")) {
            if (item.split("values").length > 1) {
                return item.split("values")[1];
            } else if (item.split("VALUES").length > 1) {
                return item.split("VALUES")[1];
            }
        }
        return null;
    }

    public static String getSqlFieldValue(String value) {
        return value.replaceAll(STR_QUOTES_SINGLE, STR_BLANK).trim();
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
        item = handleSqlForValues(item);
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
    public static String getSubTransCodeBySubTrans(String item) {
        return getMenuElement(item, 1);
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
        String item = handleSqlForValues(ele);
        try {
            if (item != null) {
                String[] menuCodeInfo = item.split(",");
                menu = menuCodeInfo[index];
                if (menu.contains("'")) {
                    menu = menu.split("'")[1];
                }
            }
        } catch (Exception e){
            LoggerUtils.error(e);
        }
        return menu;
    }

    public static String getMenuReserve(String item) {
        String menuReserve = STR_BLANK;
        item = handleSqlForValues(item);
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
        item = handleSqlForValues(item);
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
        item = handleSqlForValues(item);
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
        item = handleSqlForValues(item);
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
        List<String> menuList = FileUtils.readNormalFile(filePath);
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

    public static Set<String> initRepairExtSkip() throws Exception {
        List<String> skipContent = FileUtils.readNormalFile(FileUtils.getFilePath(SQL_CHECK_TYPE_EXTEND.REPAIR_EXT.getPathConf()));
        Set<String> skip = new HashSet<>();
        if (CollectionUtils.isNotEmpty(skipContent)) {
            for (String item : skipContent) {
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                String[] ele = CommonUtils.trimStrToSpace(item).split(STR_SPACE);
                if (ele.length > 0) {
                    String sub = STR_HYPHEN_1;
                    if (item.contains(FILE_TYPE_SQL)) {
                        sub = STR_BLANK;
                    } else if (ele.length > 1) {
                        sub += ele[1];
                    }
                    skip.add(ele[0] + sub);
                    skip.add(ele[0]);
                }
            }
        }
        return skip;
    }

    public static Set<String> initExtLegalSkip() throws Exception {
        List<String> skipContent = FileUtils.readNormalFile(FileUtils.getFilePath(LEGAL_EXT_MENU.getPathConf()));
        Set<String> skip = new HashSet<>();
        if (CollectionUtils.isNotEmpty(skipContent)) {
            for (String item : skipContent) {
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                String[] ele = CommonUtils.trimStrToSpace(item).split(STR_SPACE);
                if (ele.length > 0) {
                    String sub = STR_HYPHEN_1;
                    if (item.contains(FILE_TYPE_SQL)) {
                        sub = STR_BLANK;
                    } else if (ele.length > 1) {
                        sub += ele[1];
                    }
                    skip.add(ele[0] + sub);
                    skip.add(ele[0]);
                }
            }
        }
        return skip;
    }
}
