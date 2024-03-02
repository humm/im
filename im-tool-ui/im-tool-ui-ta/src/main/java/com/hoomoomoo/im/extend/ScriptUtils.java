package com.hoomoomoo.im.extend;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class ScriptUtils {


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

    private static String[] getTrans(String sql) {
        if (!sql.contains("(") || !sql.contains(")")) {
            return null;
        }
        return sql.substring(sql.indexOf("(") + 1, sql.lastIndexOf(")")).split(",");
    }
}
