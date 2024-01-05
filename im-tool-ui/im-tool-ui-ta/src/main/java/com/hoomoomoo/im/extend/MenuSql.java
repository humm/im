package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.controller.SystemToolController;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.scene.control.TextArea;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class MenuSql {

    private String resultPath = "";
    private String basePathExt = "\\sql\\pub\\001initdata\\extradata\\";
    private String newUedPage = "UED\\newUedPage.sql";


    private TextArea logs;

    public MenuSql (TextArea logs) throws Exception {
        this.logs = logs;
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String basePath = appConfigDto.getSystemToolCheckMenuBasePath();
        if (StringUtils.isBlank(basePath)) {
            OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("请配置参数【system.tool.check.menu.base.path】"));
            throw new Exception("请配置参数【system.tool.check.menu.base.path】");
        }
        String resPath = appConfigDto.getSystemToolCheckMenuResultPath();
        if (StringUtils.isBlank(resPath)) {
            OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("请配置参数【system.tool.check.menu.result.path】"));
            throw new Exception("请配置参数【system.tool.check.menu.result.path】");
        }
        basePathExt = basePath + basePathExt;
        newUedPage = basePathExt + newUedPage;
        resultPath = resPath + "\\";
    }

    public void generateSql() throws Exception {
        List<String> sql = new ArrayList<>();
        List<String> config = FileUtils.readNormalFile(newUedPage, false);
        Set<String> deleteMenuCode = getNeedDeleteMenuCode(config);
        String ele = "";
        boolean nextFlag = false;
        boolean head = true;
        for(int i = 0; i < config.size(); ++i) {
            String item = config.get(i);
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
                    if (item.contains("统一TA")) {
                        break;
                    }
                } else {
                    sql.add(item);
                }
            }
        }
        FileUtils.writeFile(resultPath + "update.sql", sql, false);
    }

    private Set<String> getNeedDeleteMenuCode(List<String> config) {
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

    private String getMenuCode(String sql) {
        String menuCode = "";
        if (sql.contains("delete")) {
            menuCode = sql.split("where")[1].split("=")[1].trim();
        } else if (sql.contains("values")) {
            menuCode = sql.split("values")[1].split(",")[0].trim();
        }
        return menuCode.replaceAll("'", "").replaceAll("\\(", "").replaceAll(";", "");
    }

    private String generateUpdate(String item, String updateKey, boolean generate) throws Exception {
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
            String[] column = sql[0].substring(sql[0].indexOf("(") + 1, sql[0].indexOf(")")).split(",");
            String[] value = handleValue(column.length, sql[1].substring(sql[1].indexOf("(") + 1, sql[1].indexOf(")")).split(","));
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
            return updateSql.toString();
        }
    }

    private String[] handleValue(int len, String[] value) {
        String[] res = new String[len];
        int index = 0;
        String val = "";
        String[] var5 = value;
        int length = value.length;
        for(int i = 0; i < length; i++) {
            String item = var5[i];
            if (item.startsWith("'") && item.endsWith("'")) {
                res[index] = item;
                ++index;
            } else if (!item.contains("'")) {
                if (StringUtils.isNotBlank(val)) {
                    val = val + item + ",";
                } else {
                    res[index] = item;
                    ++index;
                }
            } else if (item.startsWith("'") || item.endsWith("'")) {
                val = val + item + ",";
                if (item.endsWith("'")) {
                    if (val.endsWith(",")) {
                        val = val.substring(0, val.length() - 1);
                    }
                    res[index] = val;
                    val = "";
                    ++index;
                }
            }
        }
        return res;
    }

    private String getTableName(String sql) {
        int tableNameStartIndex = sql.toLowerCase().indexOf("into");
        int tableNameStartEnd = sql.toLowerCase().indexOf("(");
        return sql.substring(tableNameStartIndex + 4, tableNameStartEnd).toLowerCase().trim();
    }
}
