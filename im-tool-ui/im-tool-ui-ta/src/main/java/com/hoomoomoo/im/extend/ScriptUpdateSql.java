package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ScriptUpdateSql {

    private String resultPath = "";
    private String newUedPage = "";

    public ScriptUpdateSql() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String basePath = appConfigDto.getSystemToolCheckMenuBasePath();
        if (StringUtils.isBlank(basePath)) {
            throw new Exception("请配置参数【system.tool.check.menu.base.path】\n");
        }
        String resPath = appConfigDto.getSystemToolCheckMenuResultPath();
        if (StringUtils.isBlank(resPath)) {
            throw new Exception("请配置参数【system.tool.check.menu.result.path】\n");
        }
        newUedPage = basePath + ScriptSqlUtils.newUedPage;
        resultPath = resPath + "\\";
    }

    public void generateSql() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<String> config = FileUtils.readNormalFile(newUedPage, false);
        List<String> sql = getUpdateSql(appConfigDto, config);
        FileUtils.writeFile(resultPath + BaseConst.SQL_CHECK_TYPE.NEW_MENU_UPDATE.getFileName(), sql, false);
    }

    public static List<String> getUpdateSql(AppConfigDto appConfigDto, List<String> config) throws Exception {
        List<String> sql = new ArrayList<>();
        Set<String> deleteMenuCode = getNeedDeleteMenuCode(config);
        String ele = "";
        boolean nextFlag = false;
        boolean head = true;
        for(int i = 0; i < config.size(); ++i) {
            String item = config.get(i);
            if (item.contains("commit;")) {
                break;
            }
            if (!StringUtils.isBlank(item)) {
                String itemTmp = item.toLowerCase();
                if (head && (itemTmp.contains("delete") || itemTmp.contains("insert") || itemTmp.contains("values"))) {
                    head = false;
                    if (i != 0) {
                        sql.add(config.get(i-1));
                    }
                }
                if (head) {
                    continue;
                }
                if (!item.contains("开始") && !item.contains("结束")) {
                    ele = ele + item + "\n";
                    if (ele.contains(";")) {
                        String menuCode = getMenuCode(ele);
                        if (deleteMenuCode.contains(menuCode)) {
                            if (!ele.contains("values")) {
                                ele = ele.replace("\n", "");
                            }
                            sql.add(ele);
                            nextFlag = true;
                        } else {
                            if (nextFlag) {
                                nextFlag = false;
                            }
                            if (!ele.contains("delete")) {
                                sql.add(generateUpdate(ele, "where menu_code = '" + menuCode + "';", false));
                            }
                        }
                        ele = "";
                    }
                    if (item.contains(appConfigDto.getSystemToolCheckMenuEndFlag())) {
                        break;
                    }
                } else {
                    sql.add(item);
                }
            }
        }
        return sql;
    }

    private static Set<String> getNeedDeleteMenuCode(List<String> config) {
        Set<String> delete = new HashSet();
        Iterator<String> iterator = config.iterator();

        while(iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains("delete") && item.contains("tsys_trans")) {
                delete.add(getMenuCode(item));
            }
        }
        return delete;
    }

    private static String getMenuCode(String sql) {
        String menuCode = "";
        if (sql.contains("delete")) {
            menuCode = sql.split("where")[1].split("=")[1].trim();
        } else if (sql.contains("values")) {
            menuCode = sql.split("values")[1].split(",")[0].trim();
        }
        return menuCode.replaceAll("'", "").replaceAll("\\(", "").replaceAll(";", "");
    }

    private static String generateUpdate(String item, String updateKey, boolean generate) throws Exception {
        Set<String> keyColumn = new HashSet();
        String[] sql;
        if (StringUtils.isNotBlank(updateKey)) {
            updateKey = updateKey.split("where")[1].trim();
            String[] updateItem = updateKey.split("and");
            sql = updateItem;
            int length = updateItem.length;
            for(int i = 0; i < length; i++) {
                String ele = sql[i];
                keyColumn.add(ele.split("=")[0].trim());
            }
        }
        StringBuilder updateSql = new StringBuilder();
        if (generate && !item.toLowerCase().contains("tbmenucondition")) {
            return "";
        } else {
            if (item.toLowerCase().contains("tbmenucondition")) {
                updateSql.append("update tbmenuconditionuser set ");
            } else {
                updateSql.append("update " + getTableName(item) + " set ");
            }
            updateSql.append("\n");
            item = item.replaceAll("\\s+", "");
            sql = item.split("values");
            if (sql.length != 2) {
                sql = item.split("values".toUpperCase());
                if (sql.length != 2) {
                    throw new Exception("sql语句未包含或者包含多个values\n" + item);
                }
            }
            try {
                String[] column = sql[0].substring(sql[0].indexOf("(") + 1, sql[0].indexOf(")")).split(",");
                String[] value = handleValue(column.length, sql[1].substring(sql[1].indexOf("(") + 1, sql[1].lastIndexOf(")")).split(","));
                for(int i = 0; i < column.length; ++i) {
                    if (!keyColumn.contains(column[i])) {
                        updateSql.append("  ");
                        updateSql.append(column[i] + " = " + ("''".equals(value[i]) ? "' '" : value[i]));
                        if (i != column.length - 1) {
                            updateSql.append(",").append("\n");
                        }
                    }
                }
                updateSql.append("\nwhere " + updateKey + "\n");
            }catch (Exception e){
                LoggerUtils.info(sql[0]);
                LoggerUtils.info(sql[1]);
                LoggerUtils.info(e);
            }
            return updateSql.toString();
        }
    }

    public static String[] handleValue(int len, String[] value) {
        String[] res = new String[len];
        int index = 0;
        String val = "";
        String[] temp = value;
        int length = value.length;
        for(int i = 0; i < length; i++) {
            String item = temp[i].trim();
            if (item.startsWith("'") && item.endsWith("'")) {
                res[index] = item;
                index++;
            } else if (!item.contains("'")) {
                if (StringUtils.isNotBlank(val)) {
                    val = val + item + ",";
                } else {
                    res[index] = item;
                    index++;
                }
            } else if (item.startsWith("'") || item.endsWith("'")) {
                val = val + item + ",";
                if (item.endsWith("'")) {
                    if (val.endsWith(",")) {
                        val = val.substring(0, val.length() - 1);
                    }
                    res[index] = val;
                    val = "";
                    index++;
                }
            }
        }
        return res;
    }

    private static String getTableName(String sql) {
        int tableNameStartIndex = sql.toLowerCase().indexOf("into");
        int tableNameStartEnd = sql.toLowerCase().indexOf("(");
        return sql.substring(tableNameStartIndex + 4, tableNameStartEnd).toLowerCase().trim();
    }
}
