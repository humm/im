package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class ScriptUpdateSql {

    private String resultPath = STR_BLANK;
    private String newUedPage = STR_BLANK;

    private static final String endLine = "结束 *************************************************************************";
    public ScriptUpdateSql() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String basePath = appConfigDto.getSystemToolCheckMenuFundBasePath();
        if (StringUtils.isBlank(basePath)) {
            throw new Exception("请配置参数【system.tool.check.menu.fund.base.path】\n");
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
        List<String> config = FileUtils.readNormalFile(newUedPage);
        List<String> sql = getUpdateSql(appConfigDto, config);
        sql.add(STR_NEXT_LINE);
        sql.add("commit;");
        FileUtils.writeFile(resultPath + BaseConst.SQL_FilE_TYPE.NEW_MENU_UPDATE.getFileName(), sql);
    }

    public void generateChangeMenuSql() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String resPath = appConfigDto.getSystemToolScriptChangeMenuPath();
        if (StringUtils.isBlank(resPath)) {
            throw new Exception("请配置参数【system.tool.script.change.menu.path】\n");
        }
        resPath += "\\";
        String basePath = appConfigDto.getSystemToolCheckMenuFundBasePath();
        if (StringUtils.isBlank(basePath)) {
            throw new Exception("请配置参数【system.tool.check.menu.fund.base.path】\n");
        }
        List<String> res = new ArrayList<>();
        res.add("-- 仅适用于从老版ued升级到新版ued使用");
        res.add("-- 如果环境部署时候为新版ued则无需执行");
        res.add("-- 此脚本无需手动维护");
        res.add("-- 需与平台新版ued脚本一起使用");
        res.add(STR_BLANK);
        newUedPage = basePath + ScriptSqlUtils.newUedPage;
        List<String> sqlList = FileUtils.readNormalFile(newUedPage);
        for (String ele : sqlList) {
            if (ele.equals("commit;")) {
                continue;
            }
            if (ele.startsWith("-- ") && (ele.contains("delete") || ele.contains("insert") || ele.contains("values"))) {
                ele = ele.replace("-- ", STR_BLANK);
            }
            if (ele.startsWith("values (")) {
                String reserve = ScriptSqlUtils.getMenuReserve(ele);
                if (StringUtils.isNotBlank(reserve) && !StringUtils.equals(reserve, "0") && !StringUtils.equals(reserve, " ")) {
                    res.add(ele);
                    continue;
                }
                boolean isMenu = ele.split(STR_COMMA).length > 10;
                if (!isMenu) {
                    res.add(ele);
                    continue;
                }
                String menuCode = ScriptSqlUtils.getMenuCode(ele);
                String condition = "t.menu_code = '" + menuCode + "'";
                if (StringUtils.equals(menuCode, "bizInterestRateSet")) {
                    condition = "t.menu_code in ('fundInterestInfoSet', 'bizInterestRateSet')";
                } else if (StringUtils.equals(menuCode, "bizBlackInfoSet")) {
                    condition = "t.menu_code in ('fundBlackListSet', 'bizBlackInfoSet')";
                } else if (StringUtils.equals(menuCode, "bizClerkInfoSet")) {
                    condition = "t.menu_code in ('fundClerkList', 'bizClerkInfoSet')";
                }
                ele = ele.replace("values (", "select ").replace(");", " from (select count(1) param_exists from tsys_menu t where " + condition + ") a where a.param_exists = 1;");
                res.add(ele);
                continue;
            }
            res.add(ele);
        }

        res.add(STR_BLANK);
        res.add("-- 补充tsys_trans tsys_subtrans tsys_subtrans_ext数据");
        Map<String, String[]> copyMenuCode = new LinkedHashMap<String, String[]>(){{
            put("fundCacheRefreshQuery", new String[]{"add", "bizCacheRefresh"});
        }};
        String baseMenuContents = FileUtils.readNormalFileToString(basePath + ScriptSqlUtils.baseMenu, true);
        if (StringUtils.isNotBlank(baseMenuContents)) {
            List<String> sql = Arrays.asList(baseMenuContents.split(STR_SEMICOLON));
            for (String item : sql) {
                if (item.contains("tsys_trans") || item.contains("tsys_subtrans") || item.contains("tsys_subtrans_ext")) {
                    String menuCode = ScriptSqlUtils.getMenuCode(item);
                    String[] copyConfig = copyMenuCode.get(menuCode);
                    if (item.contains("tsys_subtrans ") && copyConfig != null) {
                        res.add(ScriptRepairSql.formatSqlAddDelete(item, false));
                        if (item.contains("tsys_subtrans ")) {
                            res.add(buildUserRoleRight(item, "tsys_user_right", menuCode, copyConfig));
                            res.add(buildUserRoleRight(item, "tsys_role_right", menuCode, copyConfig));
                        }
                    }
                }
            }
        }
        List<String> uedHome = FileUtils.readNormalFile(basePath + ScriptSqlUtils.newUedHome);
        res.add(STR_BLANK);
        res.addAll(uedHome);

        FileUtils.writeFile(resPath + BaseConst.SQL_FilE_TYPE.UPDATE_CHANGE_MENU.getFileName(), res);
    }

    private static String buildUserRoleRight(String item, String tableName, String transCode, String[] copyConfig) {
        StringBuilder res = new StringBuilder();
        String subTransCode = ScriptSqlUtils.getSubTransCodeBySubTrans(item);
        String fieldCode = StringUtils.equals("tsys_user_right", tableName) ? "user_id" : "role_code";
        if ("add".equals(copyConfig[0])) {
            res.append("insert into %s (trans_code, sub_trans_code, %s, create_by, create_date, begin_date, end_date, right_flag)\n");
            res.append("select '%s', '%s', %s, create_by, create_date, begin_date, end_date, right_flag\n");
            res.append("  from %s a\n");
            res.append("where a.trans_code = '%s'\n");
            res.append("and not exists (select 1 from tsys_user_right b where b.trans_code = '%s' and b.sub_trans_code = '%s');\n");
        }
        return String.format(res.toString(), tableName, fieldCode, transCode, subTransCode, fieldCode, tableName, copyConfig[1], transCode, subTransCode);
    }

    public static List<String> getUpdateSql(AppConfigDto appConfigDto, List<String> config) throws Exception {
        List<String> sql = new ArrayList<>();
        Set<String> deleteMenuCode = getNeedDeleteMenuCode(config);
        String ele = STR_BLANK;
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
                    ele = ele + item + STR_NEXT_LINE;
                    if (ele.contains(STR_SEMICOLON)) {
                        String menuCode = getMenuCode(ele);
                        if (deleteMenuCode.contains(menuCode)) {
                            if (!ele.contains("values")) {
                                ele = ele.replace(STR_NEXT_LINE, STR_BLANK);
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
                        ele = STR_BLANK;
                    }
                    if (item.contains(appConfigDto.getSystemToolCheckMenuEndFlag())) {
                        break;
                    }
                } else {
                    if (item.endsWith("结束")) {
                        String lastEle = sql.get(sql.size() - 1);
                        if (lastEle.endsWith(STR_NEXT_LINE)) {
                            sql.set(sql.size() - 1, lastEle.substring(0, lastEle.length() - 1));
                        }
                        item += STR_NEXT_LINE;
                    }
                    if (item.endsWith(endLine)) {
                        String prevEle = sql.get(sql.size() - 1);
                        if (prevEle.endsWith(STR_NEXT_LINE)) {
                            sql.set(sql.size() - 1, prevEle.substring(0, prevEle.length() - 1));
                        }
                        item += STR_NEXT_LINE;
                    }
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
        String menuCode = STR_BLANK;
        if (sql.contains("delete")) {
            menuCode = sql.split("where")[1].split(STR_EQUAL)[1].trim();
        } else if (sql.contains("values")) {
            menuCode = sql.split("values")[1].split(STR_COMMA)[0].trim();
        }
        return menuCode.replaceAll(STR_QUOTES_SINGLE, STR_BLANK).replaceAll("\\(", STR_BLANK).replaceAll(STR_SEMICOLON, STR_BLANK);
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
                keyColumn.add(ele.split(STR_EQUAL)[0].trim());
            }
        }
        StringBuilder updateSql = new StringBuilder();
        if (generate && !item.toLowerCase().contains("tbmenucondition")) {
            return STR_BLANK;
        } else {
            if (item.toLowerCase().contains("tbmenucondition")) {
                updateSql.append("update tbmenuconditionuser set ");
            } else {
                updateSql.append("update " + ScriptSqlUtils.getTableName(item) + " set ");
            }
            updateSql.append(STR_NEXT_LINE);
            item = CommonUtils.trimStrToBlank(item);
            sql = item.split("values");
            if (sql.length != 2) {
                sql = item.split("values".toUpperCase());
                if (sql.length != 2) {
                    throw new Exception("sql语句未包含或者包含多个values\n" + item);
                }
            }
            String columnStr = sql[0];
            String valueStr = sql[1];
            try {
                String[] column = columnStr.substring(columnStr.indexOf("(") + 1, columnStr.indexOf(")")).split(STR_COMMA);
                boolean hasTabSubTransCodes = false;
                if (column.length == 19 && StringUtils.equals(column[18], "tab_sub_trans_codes")) {
                    hasTabSubTransCodes = true;
                }
                String[] value;
                valueStr = valueStr.substring(valueStr.indexOf("(") + 1, valueStr.lastIndexOf(")"));

                if (hasTabSubTransCodes) {
                    int index = valueStr.substring(0, valueStr.length() - 1).lastIndexOf(STR_QUOTES_SINGLE);
                    if (index != -1) {
                        String[] valueTmp = valueStr.split(STR_QUOTES_SINGLE);
                        String tabSubTransCodes = valueTmp[valueTmp.length - 1];
                        valueStr = valueStr.substring(0, index);
                        List<String> valueTmpList = new ArrayList<>(Arrays.asList(valueStr.split(STR_COMMA)));
                        valueTmpList.add(STR_QUOTES_SINGLE + tabSubTransCodes + STR_QUOTES_SINGLE);
                        value = handleValue(column.length, valueTmpList.toArray(new String[valueTmpList.size()]));
                    } else {
                        value = handleValue(column.length, valueStr.split(STR_COMMA));
                    }
                } else {
                    value = handleValue(column.length, valueStr.split(STR_COMMA));
                }

                for(int i = 0; i < column.length; ++i) {
                    if (!keyColumn.contains(column[i])) {
                        updateSql.append("  ");
                        updateSql.append(column[i] + " = " + ("''".equals(value[i]) ? "' '" : value[i]));
                        if (i != column.length - 1) {
                            updateSql.append(STR_COMMA).append(STR_NEXT_LINE);
                        }
                    }
                }
                updateSql.append("\nwhere " + updateKey + STR_NEXT_LINE);
            }catch (Exception e){
                LoggerUtils.info(columnStr);
                LoggerUtils.info(valueStr);
                LoggerUtils.error(e);
            }
            return updateSql.toString();
        }
    }

    public static String[] handleValue(int len, String[] value) {
        if (len != value.length) {
            return new String[]{STR_BLANK};
        }
        String[] res = new String[len];
        int index = 0;
        String val = STR_BLANK;
        String[] temp = value;
        int length = value.length;
        for(int i = 0; i < length; i++) {
            String item = temp[i].trim();
            if (item.startsWith(STR_QUOTES_SINGLE) && item.endsWith(STR_QUOTES_SINGLE)) {
                res[index] = item;
                index++;
            } else if (!item.contains(STR_QUOTES_SINGLE)) {
                if (StringUtils.isNotBlank(val)) {
                    val = val + item + STR_COMMA;
                } else {
                    res[index] = item;
                    index++;
                }
            } else if (item.startsWith(STR_QUOTES_SINGLE) || item.endsWith(STR_QUOTES_SINGLE)) {
                val = val + item + STR_COMMA;
                if (item.endsWith(STR_QUOTES_SINGLE)) {
                    if (val.endsWith(STR_COMMA)) {
                        val = val.substring(0, val.length() - 1);
                    }
                    res[index] = val;
                    val = STR_BLANK;
                    index++;
                }
            }
        }
        return res;
    }

}
