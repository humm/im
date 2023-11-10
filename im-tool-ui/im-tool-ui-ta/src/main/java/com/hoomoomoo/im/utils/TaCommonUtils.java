package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.controller.ScriptUpdateController;
import com.hoomoomoo.im.dto.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022/1/9
 */
public class TaCommonUtils {

    public static boolean checkConfig(TextArea log, String functionCode) throws Exception {
        boolean flag = true;
        OutputUtils.clearLog(log);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (functionCode.equals(MenuFunctionConfig.FunctionConfig.SVN_LOG.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(log, MSG_SVN_USERNAME + STR_NEXT_LINE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getSvnPassword())) {
                OutputUtils.info(log, MSG_SVN_PASSWORD + STR_NEXT_LINE);
                flag = false;
            }
            if (MapUtils.isEmpty(appConfigDto.getSvnUrl())) {
                OutputUtils.info(log, MSG_SVN_URL + STR_NEXT_LINE);
                flag = false;
            }
        }
        if (functionCode.equals(MenuFunctionConfig.FunctionConfig.SVN_UPDATE.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(log, MSG_SVN_USERNAME + STR_NEXT_LINE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getSvnPassword())) {
                OutputUtils.info(log, MSG_SVN_PASSWORD + STR_NEXT_LINE);
                flag = false;
            }
            if (CollectionUtils.isEmpty(appConfigDto.getSvnUpdatePath())) {
                OutputUtils.info(log, MSG_SVN_UPDATE_TA6 + STR_NEXT_LINE);
                flag = false;
            }
        }
        if (functionCode.equals(MenuFunctionConfig.FunctionConfig.SCRIPT_UPDATE.getCode())) {
            if (appConfigDto.getScriptUpdateGenerateFile()) {
                if (StringUtils.isBlank(appConfigDto.getScriptUpdateGeneratePath())) {
                    OutputUtils.info(log, MSG_SCRIPT_UPDATE_GENERATE_PATH + STR_NEXT_LINE);
                    flag = false;
                }
            }
        }
        if (functionCode.equals(MenuFunctionConfig.FunctionConfig.GENERATE_SQL.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getGenerateSqlDatabaseNum())) {
                OutputUtils.info(log, String.format(MSG_SET, "generate.sql.database.num"));
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getGenerateSqlTableNum())) {
                OutputUtils.info(log, String.format(MSG_SET, "generate.sql.table.num"));
                flag = false;
            }
        }
        if (functionCode.equals(DATABASE_SCRIPT.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getDatabaseScriptUrl())) {
                OutputUtils.info(log, String.format(MSG_SET, "database.script.url"));
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getDatabaseScriptUsername())) {
                OutputUtils.info(log, String.format(MSG_SET, "database.script.username"));
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getDatabaseScriptPassword())) {
                OutputUtils.info(log, String.format(MSG_SET, "database.script.password"));
                flag = false;
            }
        }
        return flag;
    }

    public static boolean checkConfig(TableView log, String functionCode) throws Exception {
        boolean flag = true;
        OutputUtils.clearLog(log);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (functionCode.equals(MenuFunctionConfig.FunctionConfig.FUND_INFO.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getFundGeneratePath())) {
                OutputUtils.info(log, MSG_FUND_GENERATE_PATH);
                flag = false;
            }
        }
        if (functionCode.equals(MenuFunctionConfig.FunctionConfig.PROCESS_INFO.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getProcessGeneratePathSchedule())) {
                OutputUtils.info(log, MSG_PROCESS_GENERATE_PATH_SCHEDULE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getProcessGeneratePathTrans())) {
                OutputUtils.info(log, MSG_PROCESS_GENERATE_PATH_TRANS);
                flag = false;
            }
        }
        return flag;
    }

    public static boolean checkConfig(Label log, String functionCode) throws Exception {
        boolean flag = true;
        OutputUtils.clearLog(log);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (functionCode.equals(SVN_REALTIME_STAT.getCode()) || functionCode.equals(SVN_HISTORY_STAT.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(log, MSG_SVN_USERNAME + STR_NEXT_LINE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getSvnPassword())) {
                OutputUtils.info(log, MSG_SVN_PASSWORD + STR_NEXT_LINE);
                flag = false;
            }
            if (MapUtils.isEmpty(appConfigDto.getSvnUrl())) {
                OutputUtils.info(log, MSG_SVN_URL + STR_NEXT_LINE);
                flag = false;
            }
            if (appConfigDto.getSvnStatUser().size() == 0) {
                OutputUtils.info(log, MSG_SVN_STAT_USER + STR_NEXT_LINE);
                flag = false;
            }
            if (appConfigDto.getSvnStatInterval() < 10) {
                OutputUtils.info(log, MSG_SVN_STAT_INTERVAL + STR_NEXT_LINE);
                flag = false;
            }
        }
        return flag;
    }



    public static boolean checkConfigGenerateCode(TextArea log, AppConfigDto appConfigDto) throws Exception {
        boolean flag = true;
        OutputUtils.clearLog(log);
        if (StringUtils.isBlank(appConfigDto.getGenerateCodeJavaPath())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "generate.code.java.path"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getGenerateCodeVuePath())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "generate.code.vue.path"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getGenerateCodeSqlPath())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "generate.code.sql.path"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getGenerateCodeRoutePath())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "generate.code.route.path"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getGenerateCodeCheckPath())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "generate.code.check.path"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getFirstMenuCode()) || StringUtils.isBlank(appConfigDto.getFirstMenuName())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "一级菜单"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getSecondMenuCode()) || StringUtils.isBlank(appConfigDto.getSecondMenuName())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "二级菜单"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getThirdMenuCode())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "三级菜单代码"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getThirdMenuName())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "三级菜单名称"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getGenerateCodeMenuType())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "菜单类型"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getGenerateCodeMenuFunction())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "菜单功能"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getMenuOrder())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "菜单排序"));
            flag = false;
        } else {
            try {
                Integer.valueOf(appConfigDto.getMenuOrder());
            } catch (NumberFormatException e) {
                OutputUtils.info(log, "【 菜单排序 】只能为整数\n");
                flag = false;
            }
        }
        if (StringUtils.isBlank(appConfigDto.getEntityCode())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "实体类代码"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getGenerateCodeMenuAuthor())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "开发人员"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getTableCode())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "正表代码"));
            flag = false;
        }
        if (MENU_FUNCTION_SET_CODE.equals(appConfigDto.getGenerateCodeMenuFunction())) {
            if (StringUtils.isBlank(appConfigDto.getAsyTableCode())) {
                OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "复核表代码"));
                flag = false;
            }
        }
        if (StringUtils.isBlank(appConfigDto.getGenerateCodeMenuMultipleTable())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "分库分表"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getDatabaseScriptUrl())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "database.script.url"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getDatabaseScriptUsername())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "database.script.username"));
            flag = false;
        }
        if (StringUtils.isBlank(appConfigDto.getDatabaseScriptPassword())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "database.script.password"));
            flag = false;
        }
        return flag;
    }

    public static List<TextArea> getUserTextArea(AppConfigDto appConfigDto, int statNum, TextArea stat1,
                                                 TextArea stat2, TextArea stat3) {
        LinkedHashMap<String, String> userList = appConfigDto.getSvnStatUser();
        int num = userList.size();
        List<TextArea> statList = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            int remainder = i % statNum;
            switch (remainder) {
                case 1:
                    statList.add(stat1);
                    break;
                case 2:
                    statList.add(stat2);
                    break;
                default:
                    statList.add(stat3);
                    break;
            }
        }
        return statList;
    }

    public static List<SvnStatDto> sortSvnStatDtoList (AppConfigDto appConfigDto, LinkedHashMap<String, SvnStatDto> stat) {
        List<SvnStatDto> svnStatDtoList = new ArrayList<>(stat.values());
        Collections.sort(svnStatDtoList, (o1, o2) -> {
            if (BaseConst.STR_1.equals(appConfigDto.getSvnStatHistoryOrderType())) {
                return Integer.valueOf(o2.getSubmitTimes()) - Integer.valueOf(o1.getSubmitTimes());
            } else {
                return Integer.valueOf(o2.getFileNum()) - Integer.valueOf(o1.getFileNum());
            }
        });
        ListIterator<SvnStatDto> iterator = svnStatDtoList.listIterator();
        while (iterator.hasNext()) {
            SvnStatDto item = iterator.next();
            if (KEY_NOTICE.equals(item.getUserCode())) {
                iterator.remove();
            }
        }
        return svnStatDtoList;
    }

    public static List<String> buildSql(AppConfigDto appConfigDto, List<String> oldSql, List<String> newSql) throws Exception {
        List<String> sql = new ArrayList<>();
        if (CollectionUtils.isEmpty(oldSql)) {
            return sql;
        }
        Map<String, String> oldSqlMap = new LinkedHashMap<>();
        Map<String, String> newSqlMap = new LinkedHashMap<>();
        List<String> deleteSql = new ArrayList<>();
        List<String> addSql = new ArrayList<>();
        for (String item : oldSql) {
            oldSqlMap.put(item, item);
        }
        for (String item : newSql) {
            newSqlMap.put(item, item);
        }
        for (int i=0; i<oldSql.size(); i++) {
            String item = oldSql.get(i);
            if (!newSqlMap.containsKey(item) && i != 0) {
                String part = oldSql.get(i);
                if (part.toLowerCase().startsWith("delete") || part.toLowerCase().startsWith("-- delete")) {
                    continue;
                }
                part = oldSql.get(i-1) + STR_NEXT_LINE + part;
                deleteSql.add(part);
            }
        }
        for (int i=0; i<newSql.size(); i++) {
            String item = newSql.get(i);
            if (!oldSqlMap.containsKey(item) && i != 0) {
                String part =  newSql.get(i);
                if (part.toLowerCase().startsWith("delete") || part.toLowerCase().startsWith("-- delete")) {
                    continue;
                }
                part = newSql.get(i-1) + STR_NEXT_LINE + part;
                addSql.add(part);
            }
        }
        ScriptUpdateController scriptUpdateController = new ScriptUpdateController();
        appConfigDto.setScriptUpdateGenerateType(STR_1);
        List<String> delete = scriptUpdateController.generatesql(appConfigDto, String.join(STR_BLANK, deleteSql));
        appConfigDto.setScriptUpdateGenerateType(STR_2);
        List<String> add = scriptUpdateController.generatesql(appConfigDto, String.join(STR_BLANK, addSql));
        if (CollectionUtils.isEmpty(add)) {
            sql.addAll(delete);
        } else {
            for (String ele : delete) {
                boolean hasKey = false;
                inner: for (String item : add) {
                    if (item.replaceAll(STR_NEXT_LINE, STR_BLANK).contains(ele.replaceAll(STR_NEXT_LINE, STR_BLANK))) {
                        hasKey = true;
                        break inner;
                    }
                }
                if (!hasKey) {
                    sql.add(ele);
                }
            }
            for (int i=0; i<add.size(); i = i+2) {
                sql.add(add.get(i) + add.get(i+1));
            }
        }
        return sql;
    }

    public static String getSvnUrl(String ver, String svnUrl) {
        if (ver.compareTo("TA6.0-FUND.V202304.01.000") >= 0 && ver.endsWith("000")) {
            svnUrl += "temp/" + KEY_FUND_SLASH;
        } else {
            svnUrl += KEY_FUND_SLASH;
        }
        return svnUrl;
    }

    public static String formatText(String text, boolean toBr){
        if (StringUtils.isBlank(text)) {
            return STR_BLANK;
        }
        if (toBr) {
            return text.replaceAll("\\n", "\r<br>").trim();
        } else {
            return text.replaceAll("\r", STR_BLANK).replaceAll("<br>", STR_BLANK).trim();
        }
    }

    public static String formatTextBrToNextLine(String text){
        if (StringUtils.isBlank(text)) {
            return STR_BLANK;
        }
        return text.replaceAll("\r", STR_BLANK).replaceAll("<br>", STR_NEXT_LINE).trim();
    }

    public static String getMsgContainDate (String msg) {
        return CommonUtils.getCurrentDateTime1() + STR_SPACE + msg;
    }

}
