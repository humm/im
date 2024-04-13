package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.ComponentUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.ERROR_LOG;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.LACK_LOG;

public class ScriptRepairSql {

    private static String EXT_LINE_INDEX = "commit";
    private static String TSYS_SUB_TRANS_EXT_END_LINE_INDEX = "-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *";
    private static String BLOCK_LINE_INDEX = "-- ************************************************************************************************************************************************************************************";
    private static String BLOCK_LINE_INDEX_TIPS = "-- ****************************************************************************** %s ******************************************************************************";
    private static String BLOCK_LINE_SUB_TRANS_EXT_INDEX_TIPS = "-- ***************************************************************** %s *****************************************************************";
    private static String MENU_TIPS = "-- ************************************************************************* %s *************************************************************************";
    private static String TRANS_TIPS = "-- **************************************** %s ****************************************";

    private static Map<String, List<Map<String, String>>> totalLogCache = new LinkedHashMap<>();
    private static Set<String> totalSubTransExtCache = new LinkedHashSet<>();
    private static int repairFileNum = 0;

    public static void repairLackLog() throws Exception {
        repairFileNum = 0;
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
        List<String> logList = FileUtils.readNormalFile(resultPath + "\\" + LACK_LOG.getFileName(), false);
        if (CollectionUtils.isEmpty(logList) || logList.size() <= 2) {
            return;
        }
        int total = getRepairTotal(logList);
        int repair = 0;

        String transCode = STR_BLANK;
        String subTransCode = STR_BLANK;
        for (int i = 2; i < logList.size(); i++) {
            String item = logList.get(i).replace(ANNOTATION_TYPE_NORMAL, STR_BLANK).trim();
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (item.contains(STR_HYPHEN_1)) {
                repair++;
                appConfigDto.setRepairSchedule(repair + " / " + total);
                // 日志类型
                String[] trans = item.split(STR_HYPHEN_1);
                if (trans.length == 2) {
                    transCode = trans[0];
                    subTransCode = trans[1].trim().split(STR_SPACE)[0];
                    // 全量脚本
                    addSubTransExt(transCode, subTransCode, null,appConfigDto.getSystemToolCheckMenuBasePath() + ScriptSqlUtils.baseMenu);
                } else {
                    transCode = STR_BLANK;
                    subTransCode = STR_BLANK;
                }
            } else {
                // 实际路径
                addSubTransExt(transCode, subTransCode, null, item);
            }
        }
    }

    public static void repairErrorLog() throws Exception {
        repairFileNum = 0;
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
        List<String> logList = FileUtils.readNormalFile(resultPath + "\\" + ERROR_LOG.getFileName(), false);
        if (CollectionUtils.isEmpty(logList) || logList.size() <= 3) {
            return;
        }
        int total = getRepairTotal(logList);
        int repair = 0;

        String transCode = STR_BLANK;
        String subTransCode = STR_BLANK;
        String opDir = STR_BLANK;
        for (int i = 3; i < logList.size(); i++) {
            String item = logList.get(i).replace(ANNOTATION_TYPE_NORMAL, STR_BLANK).trim();
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (item.contains(STR_HYPHEN_1)) {
                // 日志类型
                String[] trans = item.split(STR_HYPHEN_1);
                if (trans.length == 2) {
                    repair++;
                    appConfigDto.setRepairSchedule(repair + " / " + total);
                    transCode = trans[0];
                    String[] other = trans[1].trim().replaceAll("\\s+", STR_SPACE).split(STR_SPACE);
                    if (other.length < 3) {
                        continue;
                    }
                    subTransCode = other[0].trim();
                    opDir = other[2].trim();
                    // 全量脚本
                    updateSubTransExt(transCode, subTransCode, opDir,appConfigDto.getSystemToolCheckMenuBasePath() + ScriptSqlUtils.baseMenu);
                } else {
                    transCode = STR_BLANK;
                    subTransCode = STR_BLANK;
                }
            } else {
                // 实际路径
                updateSubTransExt(transCode, subTransCode, opDir, item);
            }
        }
    }

    private static void updateSubTransExt(String transCode, String subTransCode, String opDir, String path) throws IOException {
        if (StringUtils.isBlank(transCode) || StringUtils.isBlank(path) || StringUtils.isBlank(opDir)) {
            return;
        }
        List<String> item = FileUtils.readNormalFile(path, false);
        String status = STR_0;
        for (int i = 0; i < item.size(); i++) {
            String ele = item.get(i).trim();
            String eleLower = ele.toLowerCase();
            if (StringUtils.isBlank(ele)) {
                continue;
            }
            if (eleLower.contains("tsys_subtrans_ext") && eleLower.contains("insert")) {
                status = STR_1;
            } else if (STR_1.equals(status)){
                status = STR_2;
            }
            if (STR_2.equals(status)) {
                status = STR_0;
                String transCodeLine = ScriptSqlUtils.getTransCode(ele);
                String subTransCodeLine = ScriptSqlUtils.getSubTransCode(ele);
                if (transCode.equals(transCodeLine) && subTransCode.equals(subTransCodeLine)) {
                    String extValue = getSubTransExtValue(transCode, subTransCode, opDir);
                    if (ele.contains("--")) {
                        extValue = "-- " + extValue;
                    }
                    item.set(i, extValue);
                    break;
                }
            }
        }
        FileUtils.writeFile(path, item, false);
    }

    private static void addSubTransExt(String transCode, String subTransCode, String opDir, String path) throws IOException {
        if (StringUtils.isBlank(transCode) || StringUtils.isBlank(path)) {
            return;
        }
        List<String> item = FileUtils.readNormalFile(path, false);
        StringBuilder content = new StringBuilder();
        String lineIndex = EXT_LINE_INDEX;
        for (String ele : item) {
            content.append(ele);
            if (StringUtils.isNotBlank(ele.trim())) {
                lineIndex = ele.trim();
            }
        }
        if (path.endsWith("07console-fund-ta-vue-menu.sql")) {
            lineIndex = TSYS_SUB_TRANS_EXT_END_LINE_INDEX;
        }
        // 补充原则 如果存在同菜单其他操作类型则最加补充  否则补充在文件的最后
        String[] sql = content.toString().split(STR_SEMICOLON);
        for (String ele : sql) {
            if (ele.toLowerCase().contains("tsys_subtrans_ext")) {
                String[] sqlPart = ele.split("values");
                if (sqlPart.length == 2) {
                    String valuePart = sqlPart[1];
                    String transCodeLine = ScriptSqlUtils.getTransCode(valuePart);
                    String subTransCodeLine = ScriptSqlUtils.getSubTransCode(valuePart);
                    if (transCode.equals(transCodeLine)) {
                        lineIndex = valuePart;
                    }
                    if (transCode.equals(transCodeLine) && subTransCode.equals(subTransCodeLine)) {
                        return;
                    }
                }
            }
        }
        for (int i = 0; i < item.size(); i++) {
            String ele = item.get(i);
            if (ele.contains(lineIndex)) {
                String extInfo = buildSubTransExt(transCode, subTransCode, opDir);
                if (lineIndex.toLowerCase().contains(EXT_LINE_INDEX) || TSYS_SUB_TRANS_EXT_END_LINE_INDEX.equals(lineIndex)) {
                    extInfo = STR_NEXT_LINE + extInfo + STR_NEXT_LINE_2;
                    ele = extInfo + ele;
                } else {
                    ele += STR_NEXT_LINE + extInfo;
                }
                item.set(i, ele);
                break;
            }
        }
        FileUtils.writeFile(path, item, false);
    }

    private static String buildSubTransExt(String transCode, String subTransCode, String opDir) {
        StringBuilder ext = new StringBuilder();
        if (StringUtils.isBlank(opDir)) {
            opDir = ScriptSqlUtils.getSubTransCodeOpDir(subTransCode, STR_3);
        }
        ext.append("delete from tsys_subtrans_ext where trans_code = '" + transCode + "' and sub_trans_code = '" + subTransCode + "';").append(STR_NEXT_LINE);
        ext.append("insert into tsys_subtrans_ext (trans_code, sub_trans_code, op_dir, remark, need_active, ta_status_ctrl, active_flag)").append(STR_NEXT_LINE);
        ext.append("values ('" + transCode + "', '" + subTransCode + "','" + opDir + "', ' ', '1', ' ', ' ');");
        return ext.toString();
    }

    private static String getSubTransExtValue(String transCode, String subTransCode, String opDir) {
        StringBuilder ext = new StringBuilder();
        ext.append("values ('" + transCode + "', '" + subTransCode + "','" + opDir + "', ' ', '1', ' ', ' ');");
        return ext.toString();
    }

    public static void repairLogDiff() throws Exception {
        repairFileNum = 0;
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String basePath = appConfigDto.getSystemToolCheckMenuBasePath() + ScriptSqlUtils.baseMenu;
        List<String> item = FileUtils.readNormalFile(basePath, false);
        StringBuilder content = new StringBuilder();
        for (String ele : item) {
            content.append(ele);
        }
        String[] sql = content.toString().split(STR_SEMICOLON);
        for (String ele : sql) {
            if (!ele.toLowerCase().contains("tsys_subtrans_ext")) {
                continue;
            }
            String[] sqlPart = ele.split("values");
            if (sqlPart.length == 2) {
                String valuePart = sqlPart[1];
                String transCode = ScriptSqlUtils.getTransCode(valuePart);
                String subTransCode = ScriptSqlUtils.getSubTransCode(valuePart);
                String subTransOpDir = ScriptSqlUtils.getSubTransOpDir(valuePart);
                if (totalLogCache.containsKey(transCode)) {
                    totalLogCache.get(transCode).add(buildSubTrans(transCode, subTransCode, subTransOpDir));
                } else {
                    List<Map<String, String>> logList = new ArrayList<>();
                    logList.add(buildSubTrans(transCode, subTransCode, subTransOpDir));
                    totalLogCache.put(transCode, logList);
                }
            }
        }

        File fileExt = new File(appConfigDto.getSystemToolCheckMenuBasePath() + ScriptSqlUtils.basePathExt);
        for (File file : fileExt.listFiles()) {
            repairByFile(appConfigDto, file);
        }

        boolean subTransCodeIndex = false;
        for (int i=0; i<item.size(); i++) {
            String ele = item.get(i);
            String eleLower = ele.toLowerCase().trim();
            if (!eleLower.contains("tsys_subtrans_ext") && !subTransCodeIndex) {
                continue;
            }
            String transCode = null;
            String subTransCode = null;
            if (eleLower.startsWith("delete")) {
                transCode = ScriptSqlUtils.getTransCodeByDeleteSql(ele);
                subTransCode = ScriptSqlUtils.getSubTransCodeByDeleteSql(ele);
            } else if (eleLower.startsWith("insert")){
                subTransCodeIndex = true;
            } else {
                subTransCodeIndex = false;
                transCode = ScriptSqlUtils.getTransCode(ele);
                subTransCode = ScriptSqlUtils.getSubTransCode(ele);
            }
            if (transCode == null) {
                continue;
            }
            String key = transCode + STR_HYPHEN + subTransCode;
            if (totalSubTransExtCache.contains(key)) {
                if (!ele.contains("--")) {
                    ele = "-- " + ele;
                    if (i > 0) {
                        String prevLine = item.get(i - 1);
                        if (prevLine.contains("tsys_subtrans_ext") && !prevLine.contains("--")) {
                            item.set(i - 1, "-- " + prevLine);
                        }
                    }
                }
            }
            item.set(i, ele);
        }
        FileUtils.writeFile(basePath, item, false);
    }

    public static void repairOldMenu() throws Exception {
        List<String> res = new ArrayList<>();
        res.add(BLOCK_LINE_INDEX);
        res.add(BLOCK_LINE_INDEX);
        res.add(BLOCK_LINE_INDEX);
        res.add("-- 禁止配置脚本无规律放置");
        res.add("-- 配置数据放置对应区块内 tsys_menu tsys_trans tsys_subtrans tsys_subtrans_ext 分区块放置");
        res.add(STR_BLANK);
        res.add("delete from tsys_menu where menu_code = 'console-fund-ta-vue';");
        res.add("delete from tsys_menu where menu_code like 'fund%';");
        res.add("delete from tsys_trans where trans_code like 'fund%';");
        res.add("delete from tsys_subtrans where trans_code like 'fund%';");
        res.add("delete from tsys_subtrans_ext where trans_code like 'fund%';");
        res.add(STR_NEXT_LINE);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String basePath = appConfigDto.getSystemToolCheckMenuBasePath() + ScriptSqlUtils.baseMenu;
        List<String> menuList = FileUtils.readNormalFile(basePath, false);
        StringBuilder menu = new StringBuilder();
        for (int i=7; i<menuList.size(); i++) {
            String item = menuList.get(i).trim();
            String itemLower = item.toLowerCase();
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (!itemLower.contains("insert") && !itemLower.contains("values")) {
                continue;
            }
            menu.append(item);
        }
        Map<String, String> rootMenu =  new LinkedHashMap<>();
        Map<String, String> firstMenu =  new LinkedHashMap<>();
        Map<String, String> secondMenu =  new LinkedHashMap<>();
        Map<String, String> thirdMenu =  new LinkedHashMap<>();
        Map<String, String> otherMenu = new LinkedHashMap<>();
        Map<String, String> menuTrans = new LinkedHashMap<>();
        Map<String, List<String>> menuSubTrans = new LinkedHashMap<>();
        Map<String, List<String>> menuSubTransExt = new LinkedHashMap<>();
        String[] menuBase = menu.toString().split(STR_SEMICOLON);
        Map<String, List<String>> menuExt = initMenuExt(appConfigDto);
        List<String> menuInfo = mergeMenu(menuBase, menuExt);
        boolean exist = false;
        for (String item : menuInfo) {
            if (item.contains("select") && item.contains("from")) {
                item = item.substring(0, item.indexOf("from")).replace("select", "values (") + ")";
            }
            String menuCode = ScriptSqlUtils.getMenuCode(item);
            String subTransCode = ScriptSqlUtils.getSubTransCodeByWhole(item);
            String itemLower = item.toLowerCase().replace("--", "").replaceAll("\\s+", STR_SPACE).trim();
            if (itemLower.startsWith("insert into tsys_menu (")) {
                String parentCode = ScriptSqlUtils.getParentCode(item);
                if ("bizroot".equals(parentCode)) {
                    if (!rootMenu.containsKey(menuCode)) {
                        rootMenu.put(menuCode, item);
                    }
                } else if ("console-fund-ta-vue".equals(parentCode)){
                    if (!firstMenu.containsKey(menuCode)) {
                        firstMenu.put(menuCode, item);
                    }
                } else if (!otherMenu.containsKey(menuCode)){
                    otherMenu.put(menuCode, item);
                }
            } else if (itemLower.startsWith("insert into tsys_trans (")) {
                if (!menuTrans.containsKey(menuCode)) {
                    menuTrans.put(menuCode, item);
                }
            } else if (itemLower.startsWith("insert into tsys_subtrans (")) {
                exist = false;
                List<String> ele = new ArrayList<>();
                if (menuSubTrans.containsKey(menuCode)) {
                    ele = menuSubTrans.get(menuCode);
                    for (String single : ele) {
                        String subTransCodeTmp = ScriptSqlUtils.getSubTransCodeByWhole(single);
                        if (subTransCode.equals(subTransCodeTmp)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        ele.add(item);
                    }
                } else {
                    ele.add(item);
                }
                menuSubTrans.put(menuCode, ele);
            } else if (itemLower.startsWith("insert into tsys_subtrans_ext (")) {
                exist = false;
                List<String> ele = new ArrayList<>();
                if (menuSubTransExt.containsKey(menuCode)) {
                    ele = menuSubTransExt.get(menuCode);
                    for (String single : ele) {
                        String subTransCodeTmp = ScriptSqlUtils.getSubTransCodeByWhole(single);
                        if (subTransCode.equals(subTransCodeTmp)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        ele.add(item);
                    }
                } else {
                    ele.add(item);
                }
                menuSubTransExt.put(menuCode, ele);
            }
        }
        Iterator<String> firstMenuIterator = firstMenu.keySet().iterator();
        while (firstMenuIterator.hasNext()) {
            String firstMenuCode = firstMenuIterator.next();
            Iterator<String> iterator = otherMenu.keySet().iterator();
            while (iterator.hasNext()) {
                String menuCode = iterator.next();
                String menuDetail = otherMenu.get(menuCode);
                String parentCode = ScriptSqlUtils.getParentCode(menuDetail);
                if (firstMenuCode.equals(parentCode)) {
                    if (!secondMenu.containsKey(menuCode)) {
                        secondMenu.put(menuCode, menuDetail);
                    }
                    iterator.remove();
                }
            }
        }
        Iterator<String> secondMenuIterator = secondMenu.keySet().iterator();
        while (secondMenuIterator.hasNext()) {
            String secondMenuCode = secondMenuIterator.next();
            Iterator<String> iterator = otherMenu.keySet().iterator();
            while (iterator.hasNext()) {
                String menuCode = iterator.next();
                String menuDetail = otherMenu.get(menuCode);
                String parentCode = ScriptSqlUtils.getParentCode(menuDetail);
                if (secondMenuCode.equals(parentCode)) {
                    if (!thirdMenu.containsKey(menuCode)) {
                        thirdMenu.put(menuCode, menuDetail);
                    }
                    iterator.remove();
                }
            }
        }

        res.add(BLOCK_LINE_INDEX);
        res.add(String.format(BLOCK_LINE_INDEX_TIPS, "自建业务菜单 tsys_menu"));
        res.add(BLOCK_LINE_INDEX);
        res.add(String.format(MENU_TIPS, "根目录 开始"));
        int index = 0;
        Iterator<String> rootIterator = rootMenu.keySet().iterator();
        while (rootIterator.hasNext()) {
            index++;
            String menuCode = rootIterator.next();
            String menuDetail = rootMenu.get(menuCode);
            res.add(formatSql(menuDetail, true));
        }
        res.add(String.format(MENU_TIPS, "根目录 结束"));

        res.add(STR_BLANK);
        res.add(String.format(MENU_TIPS, "一级菜单 开始"));
        firstMenuIterator = firstMenu.keySet().iterator();
        index = 0;
        List<String> second = new ArrayList<>();
        List<String> third = new ArrayList<>();
        List<String> transAndSubTrans = new ArrayList<>();
        List<String> notTransExist = new ArrayList<>();
        List<String> subTransExt = new ArrayList<>();
        List<String> noSubTransExt = new ArrayList<>();
        while (firstMenuIterator.hasNext()) {
            index++;
            String menuCode = firstMenuIterator.next();
            String menuDetail = firstMenu.get(menuCode);
            String menuName = ScriptSqlUtils.getMenuName(menuDetail);
            res.add(formatSql(menuDetail, index == firstMenu.size()));
            List<String> subSecond = new ArrayList<>();
            Iterator<String> secondIterator = secondMenu.keySet().iterator();
            while (secondIterator.hasNext()) {
                String secondMenuCode = secondIterator.next();
                String secondMenuDetail = secondMenu.get(secondMenuCode);
                String secondMenuName = ScriptSqlUtils.getMenuName(secondMenuDetail);
                String parentCode = ScriptSqlUtils.getParentCode(secondMenuDetail);
                if (menuCode.equals(parentCode)) {
                    subSecond.add(secondMenuDetail);
                } else {
                    continue;
                }
                List<String> subThird = new ArrayList<>();
                Iterator<String> thirdIterator = thirdMenu.keySet().iterator();
                while (thirdIterator.hasNext()) {
                    String thirdMenuCode = thirdIterator.next();
                    String thirdMenuDetail = thirdMenu.get(thirdMenuCode);
                    String thirdParentCode = ScriptSqlUtils.getParentCode(thirdMenuDetail);
                    if (secondMenuCode.equals(thirdParentCode)) {
                        subThird.add(thirdMenuDetail);
                    } else {
                        continue;
                    }
                }
                menuSort(subThird);
                transAndSubTrans.add(String.format(TRANS_TIPS, "三级菜单 " + menuCode + "|" + menuName + " " + secondMenuCode + "|" + secondMenuName + " 开始"));
                subTransExt.add(String.format(TRANS_TIPS, "三级菜单 " + menuCode + "|" + menuName + " " + secondMenuCode + "|" + secondMenuName + " 开始"));
                third.add(String.format(MENU_TIPS, "三级菜单 " + menuCode + "|" + menuName + " " + secondMenuCode + "|" + secondMenuName + " 开始"));
                for (int j=0; j<subThird.size(); j++) {
                    StringBuilder subTransAndSubTrans = new StringBuilder();
                    String subThirdInfo = subThird.get(j);
                    third.add(formatSql(subThirdInfo, j == subThird.size() - 1));
                    boolean flag = getTransCodeAndSubTransCode(menuTrans, menuSubTrans, transAndSubTrans, subTransAndSubTrans, subThirdInfo);
                    if (!flag) {
                        notTransExist.add(subThirdInfo);
                    }
                    StringBuilder subTransExtTmp = new StringBuilder();
                    flag = getSubTransCodeExt(menuSubTransExt, subTransExt, subTransExtTmp, subThirdInfo);
                    if (!flag) {
                        noSubTransExt.add(subThirdInfo);
                    }
                }
                deleteNextLine(transAndSubTrans);
                deleteNextLine(subTransExt);
                third.add(String.format(MENU_TIPS, "三级菜单 " + menuCode + "|" + menuName + " " + secondMenuCode + "|" + secondMenuName + " 结束"));
                third.add(STR_BLANK);
                transAndSubTrans.add(String.format(TRANS_TIPS, "三级菜单 " + menuCode + "|" + menuName + " " + secondMenuCode + "|" + secondMenuName + " 结束"));
                transAndSubTrans.add(STR_BLANK);
                subTransExt.add(String.format(TRANS_TIPS, "三级菜单 " + menuCode + "|" + menuName + " " + secondMenuCode + "|" + secondMenuName + " 结束"));
                subTransExt.add(STR_BLANK);
            }
            menuSort(subSecond);
            second.add(String.format(MENU_TIPS, "二级菜单 " + menuCode + "|" + menuName + " 开始"));
            for (int j=0; j<subSecond.size(); j++) {
                second.add(formatSql(subSecond.get(j), j == subSecond.size() - 1));
            }
            second.add(String.format(MENU_TIPS, "二级菜单 " + menuCode + "|" + menuName +" 结束"));
            second.add(STR_BLANK);
        }
        res.add(String.format(MENU_TIPS, "一级菜单 结束"));
        res.add(STR_BLANK);
        res.addAll(second);
        res.add(STR_BLANK);
        res.addAll(third);

        List<String> account = new ArrayList<>();
        res.add(STR_BLANK);
        account.add(BLOCK_LINE_INDEX);
        account.add(String.format(BLOCK_LINE_INDEX_TIPS, "账户中心菜单 tsys_menu"));
        account.add(BLOCK_LINE_INDEX);
        Map<String, String> accountParent = initAccountMenu(otherMenu);
        Iterator<String> iterator = accountParent.keySet().iterator();
        while (iterator.hasNext()) {
            String accountParentCode = iterator.next();
            String accountParentName = accountParent.get(accountParentCode);
            Iterator<String>  otherIterator = otherMenu.keySet().iterator();
            List<String> subAccount = new ArrayList<>();
            while (otherIterator.hasNext()) {
                String menuCode = otherIterator.next();
                String menuDetail = otherMenu.get(menuCode);
                String parentCode = ScriptSqlUtils.getParentCode(menuDetail);
                if (accountParentCode.equals(parentCode)) {
                    otherIterator.remove();
                    subAccount.add(menuDetail);
                }
            }
            menuSort(subAccount);
            String menuTitle = "三级菜单";
            if ("ptaAccountManage".equals(accountParentCode)) {
                menuTitle = "二级菜单";
            }
            subTransExt.add(String.format(String.format(TRANS_TIPS, menuTitle + " " + "账户中心" + " " + accountParentCode + "|" + accountParentName + " 开始")));
            transAndSubTrans.add(String.format(String.format(TRANS_TIPS, menuTitle + " " + "账户中心" + " " + accountParentCode + "|" + accountParentName + " 开始")));
            account.add(String.format(MENU_TIPS, menuTitle + " " + "账户中心" + " " + accountParentCode + "|" + accountParentName + " 开始"));
            for (int j=0; j<subAccount.size(); j++) {
                StringBuilder subTransAndSubTrans = new StringBuilder();
                String subThirdInfo = subAccount.get(j);
                account.add(formatSql(subAccount.get(j), j == subAccount.size() - 1));
                boolean flag = getTransCodeAndSubTransCode(menuTrans, menuSubTrans, transAndSubTrans, subTransAndSubTrans, subThirdInfo);
                if (!flag) {
                    notTransExist.add(subThirdInfo);
                }
                StringBuilder subTransExtTmp = new StringBuilder();
                flag = getSubTransCodeExt(menuSubTransExt, subTransExt, subTransExtTmp, subThirdInfo);
                if (!flag) {
                    noSubTransExt.add(subThirdInfo);
                }
            }
            deleteNextLine(transAndSubTrans);
            deleteNextLine(subTransExt);
            account.add(String.format(MENU_TIPS, menuTitle + " " + "账户中心" + " " + accountParentCode + "|" + accountParentName + " 结束"));
            account.add(STR_BLANK);
            transAndSubTrans.add(String.format(String.format(TRANS_TIPS, menuTitle + " " + "账户中心" + " " + accountParentCode + "|" + accountParentName + " 结束")));
            transAndSubTrans.add(STR_BLANK);
            subTransExt.add(String.format(String.format(TRANS_TIPS, menuTitle + " " + "账户中心" + " " + accountParentCode + "|" + accountParentName + " 结束")));
            subTransExt.add(STR_BLANK);
        }
        res.addAll(account);

        res.add(STR_BLANK);
        res.add(BLOCK_LINE_INDEX);
        res.add(String.format(BLOCK_LINE_INDEX_TIPS, "  交易码  tsys_trans  "));
        res.add(String.format(BLOCK_LINE_INDEX_TIPS, "子交易码 tsys_subtrans"));
        res.add(BLOCK_LINE_INDEX);
        res.addAll(transAndSubTrans);

        res.add(STR_BLANK);
        res.add(BLOCK_LINE_INDEX);
        res.add(String.format(BLOCK_LINE_SUB_TRANS_EXT_INDEX_TIPS, "              日志 tsys_subtrans_ext            "));
        res.add(String.format(BLOCK_LINE_SUB_TRANS_EXT_INDEX_TIPS, "0-新增 1-修改 2-删除 3-其他 4-查询 5-下载 6-导入"));
        res.add(BLOCK_LINE_INDEX);
        if (CollectionUtils.isNotEmpty(subTransExt)) {
            subTransExt = subTransExt.subList(0, subTransExt.size() - 1);
        }
        res.addAll(subTransExt);
        res.add(TSYS_SUB_TRANS_EXT_END_LINE_INDEX);
        res.add(STR_BLANK);
        res.add("commit;");

        Map<String, List<String>> skipCache = initSkipCache();
        List<String> error = new ArrayList<>();
        if (MapUtils.isNotEmpty(otherMenu)) {
            List<String> skipMenu = skipCache.get(KEY_MENU);
            List<String> subError = new ArrayList<>();
            Iterator<String> otherIterator = otherMenu.keySet().iterator();
            while (otherIterator.hasNext()) {
                String menuCode = otherIterator.next();
                if (skipMenu.contains(menuCode)) {
                    continue;
                }
                subError.add(formatSql(otherMenu.get(menuCode), true));
                subError.add(STR_BLANK);
            }
            if (CollectionUtils.isNotEmpty(subError)) {
                error.add(String.format(MENU_TIPS, "未匹配菜单: " + subError.size() / 2));
                error.addAll(subError);
            }
        }
        if (CollectionUtils.isNotEmpty(error)) {
            error.add(STR_NEXT_LINE);
        }
        if (CollectionUtils.isNotEmpty(notTransExist)) {
            List<String> skipTrans = skipCache.get(KEY_TRANS);
            List<String> subError = new ArrayList<>();
            for (String item : notTransExist) {
                String transCode = ScriptSqlUtils.getTransCodeByWhole(item);
                if (skipTrans.contains(transCode)) {
                    continue;
                }
                subError.add(formatSql(item, true));
                subError.add(STR_BLANK);
            }
            if (CollectionUtils.isNotEmpty(subError)) {
                error.add(String.format(MENU_TIPS, "未匹配交易码: " + subError.size() / 2));
                error.addAll(subError);
            }
        }
        if (CollectionUtils.isNotEmpty(noSubTransExt)) {
            List<String> subError = new ArrayList<>();
            List<String> skipTransExt = skipCache.get(KEY_TRANS_EXT);
            for (String item : noSubTransExt) {
                String transCode = ScriptSqlUtils.getTransCodeAndSubTransCodeByMenu(item);
                if (skipTransExt.contains(transCode)) {
                    continue;
                }
                subError.add(formatSql(item, true));
                subError.add(STR_BLANK);
            }
            if (CollectionUtils.isNotEmpty(subError)) {
                error.add(String.format(MENU_TIPS, "未匹配日志: " + subError.size() / 2));
                error.addAll(subError);
            }
        }

        String checkFile = basePath.replace(".sql", ".check.sql");
        if (CollectionUtils.isNotEmpty(error)) {
            FileUtils.writeFile(checkFile, error, false);
        } else {
            File file = new File(checkFile);
            if (file.exists()) {
                file.delete();
            }
        }
        String resFile = basePath.replace(".sql", ".res.sql");
        if (CollectionUtils.isNotEmpty(error)) {
            FileUtils.writeFile(resFile, res, false);
            throw new Exception("检查存在未匹配项，请查看结果文件");
        } else {
            File file = new File(resFile);
            if (file.exists()) {
                file.delete();
            }
            FileUtils.writeFile(basePath, res, false);
        }
    }

    private static void menuSort(List<String> menuList) {
        Collections.sort(menuList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int orderNo1 = Integer.valueOf(ScriptSqlUtils.getOrderNo(o1).trim());
                int orderNo2 = Integer.valueOf(ScriptSqlUtils.getOrderNo(o2).trim());
                if (orderNo1 == orderNo2) {
                    return ScriptSqlUtils.getMenuCode(o1).compareTo(ScriptSqlUtils.getMenuCode(o2));
                } else {
                    return orderNo1 - orderNo2;
                }
            }
        });
    }

    private static Map<String, List<String>> initSkipCache() throws IOException {
        Map<String, List<String>> cache = new HashMap<>(2);
        cache.put(KEY_MENU, new ArrayList<>());
        cache.put(KEY_TRANS, new ArrayList<>());
        cache.put(KEY_TRANS_EXT, new ArrayList<>());
        String confPath = FileUtils.getFilePath(SQL_CHECK_TYPE_EXTEND.REPAIR_OLD_MENU.getPathConf());
        List<String> content = FileUtils.readNormalFile(confPath, false);
        if (CollectionUtils.isNotEmpty(content)) {
            for (String item : content) {
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                String[] element = item.trim().replaceAll("\\s+", STR_SPACE).split(STR_SPACE);
                if (element.length >= 2) {
                    String type = element[0];
                    if (!cache.containsKey(type)) {
                        continue;
                    }
                    if (KEY_TRANS_EXT.equals(type)) {
                        cache.get(type).add(element[1] + " - " + element[2]);
                    } else {
                        cache.get(type).add(element[1]);
                    }
                }
            }
        }
        return cache;
    }

    private static boolean getTransCodeAndSubTransCode(Map<String, String> menuTrans, Map<String, List<String>> menuSubTrans,
                                                    List<String> transAndSubTrans, StringBuilder subTransAndSubTrans, String subThirdInfo) {
        boolean flag = true;
        String subThirdMenuCode = ScriptSqlUtils.getTransCodeByMenu(subThirdInfo);
        String trans = menuTrans.get(subThirdMenuCode);
        if (trans != null) {
            subTransAndSubTrans.append(formatSql(trans, false));
        }
        List<String> subTrans = menuSubTrans.get(subThirdMenuCode);
        if (CollectionUtils.isNotEmpty(subTrans)) {
            for (int k=0; k<subTrans.size(); k++) {
                subTransAndSubTrans.append(formatSql(subTrans.get(k), false));
            }
        } else {
            flag = false;
        }
        if (StringUtils.isNotBlank(subTransAndSubTrans.toString())) {
            transAndSubTrans.add(subTransAndSubTrans.toString());
        }
        return flag;
    }

    private static boolean getSubTransCodeExt(Map<String, List<String>> menuSubTransExt, List<String> subTransExt, StringBuilder subTransExtTmp, String subThirdInfo) {
        boolean flag = true;
        String subThirdMenuCode = ScriptSqlUtils.getTransCodeByMenu(subThirdInfo);
        String subThirdMenuName = ScriptSqlUtils.getMenuName(subThirdInfo);
        List<String> subTrans = menuSubTransExt.get(subThirdMenuCode);
        if (CollectionUtils.isNotEmpty(subTrans)) {
            for (int k=0; k<subTrans.size(); k++) {
                subTransExtTmp.append(formatSql(subTrans.get(k), false));
            }
        } else {
            flag = false;
        }
        if (StringUtils.isNotBlank(subTransExtTmp.toString())) {
            subTransExt.add("-- " + subThirdMenuName + " 开始");
            subTransExt.add(subTransExtTmp.substring(0, subTransExtTmp.length() - 1));
            subTransExt.add("-- " + subThirdMenuName + " 结束" + STR_NEXT_LINE);
        }
        return flag;
    }

    private static void deleteNextLine(List<String> transAndSubTrans) {
        String lastElement = transAndSubTrans.get(transAndSubTrans.size() - 1);
        if (lastElement.endsWith(STR_NEXT_LINE)) {
            transAndSubTrans.set(transAndSubTrans.size() - 1, lastElement.substring(0, lastElement.length() - 1));
        }
    }

    private static List<String> mergeMenu(String[] base, Map<String, List<String>> menuExt) {
        List<String> res = new ArrayList<>(Arrays.asList(base));
        Iterator<String> iterator = menuExt.keySet().iterator();
        while (iterator.hasNext()) {
            String extType = iterator.next();
            List<String> ext = menuExt.get(extType);
            res.addAll(ext);
        }
        return res;
    }

    private static Map<String, String> initAccountMenu(Map<String, String> otherMenu) {
        Map<String, String> menu = new LinkedHashMap<>();
        menu.put("ptaAccountManage", getAccountParentMenuName("ptaAccountManage", otherMenu));
        menu.put("ptaAccountManageFundAccount", getAccountParentMenuName("ptaAccountManageFundAccount", otherMenu));
        menu.put("ptaAccountManageFundDaily", getAccountParentMenuName("ptaAccountManageFundDaily", otherMenu));
        menu.put("ptaAccountManageFundOther", getAccountParentMenuName("ptaAccountManageFundOther", otherMenu));
        menu.put("ptaPrdAccStd", getAccountParentMenuName("ptaPrdAccStd", otherMenu));
        return menu;
    }

    private static String getAccountParentMenuName(String menuCode, Map<String, String> menu) {
        if (menu.containsKey(menuCode)) {
            return ScriptSqlUtils.getMenuName(menu.get(menuCode));
        }
        return menuCode;
    }

    private static Map<String, List<String>> initMenuExt(AppConfigDto appConfigDto) throws IOException {
        Map<String, List<String>> res = new HashMap<>();
        File fileExt = new File(appConfigDto.getSystemToolCheckMenuBasePath() + ScriptSqlUtils.basePathExt);
        initMenuByFile(res, fileExt);
        return res;
    }

    private static void initMenuByFile(Map<String,List<String>> res, File file) throws IOException {
        if (file.isDirectory()) {
            for (File item : file.listFiles()) {
                initMenuByFile(res, item);
            }
        } else {
            String fileName = file.getName();
            if (!fileName.endsWith(FILE_TYPE_SQL)) {
                return;
            }
            List<String> content = FileUtils.readNormalFile(file.getPath(), false);
            StringBuilder menu = new StringBuilder();
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i).trim();
                String itemLower = item.toLowerCase();
                if (StringUtils.isBlank(item) || item.startsWith(ANNOTATION_TYPE_NORMAL)) {
                    continue;
                }
                if (itemLower.startsWith("delete")) {
                    continue;
                }
                menu.append(item);
            }
            String[] menuInfo = menu.toString().split(STR_SEMICOLON);
            List<String> menuExtend = new ArrayList<>();
            List<String> transExtend = new ArrayList<>();
            List<String> subTransExtend = new ArrayList<>();
            List<String> subTransExtExtend = new ArrayList<>();
            for (String item : menuInfo) {
                String itemLower = item.trim().toLowerCase().replaceAll("\\s+", STR_SPACE);
                if (!item.contains("--")) {
                    item = "-- " + item;
                    itemLower = "-- " + itemLower;
                }
                if (itemLower.startsWith("-- insert into tsys_menu (")) {
                    menuExtend.add(item);
                } else if (itemLower.startsWith("-- insert into tsys_trans (")) {
                    transExtend.add(item);
                } else if (itemLower.startsWith("-- insert into tsys_subtrans (")) {
                    subTransExtend.add(item);
                } else if (itemLower.startsWith("-- insert into tsys_subtrans_ext (")) {
                    subTransExtExtend.add(item);
                }
            }
            if (res.containsKey(KEY_MENU_EXTEND)) {
                res.get(KEY_MENU_EXTEND).addAll(menuExtend);
            } else {
                res.put(KEY_MENU_EXTEND, menuExtend);
            }
            if (res.containsKey(KEY_TRANS_EXTEND)) {
                res.get(KEY_TRANS_EXTEND).addAll(transExtend);
            } else {
                res.put(KEY_TRANS_EXTEND, transExtend);
            }
            if (res.containsKey(KEY_SUB_TRANS_EXTEND)) {
                res.get(KEY_SUB_TRANS_EXTEND).addAll(subTransExtend);
            } else {
                res.put(KEY_SUB_TRANS_EXTEND, subTransExtend);
            }
            if (res.containsKey(KEY_SUB_TRANS_EXT_EXTEND)) {
                res.get(KEY_SUB_TRANS_EXT_EXTEND).addAll(subTransExtExtend);
            } else {
                res.put(KEY_SUB_TRANS_EXT_EXTEND, subTransExtExtend);
            }
        }
    }

    private static String formatSql(String sql, boolean last) {
        int index = sql.indexOf(")");
        if (index != -1) {
            String insert = sql.substring(0, index + 1).trim();
            String value = sql.substring(index + 1).trim();
            if (insert.startsWith("--") && !value.startsWith("--")) {
                value = "-- " + value;
            }
            sql = insert + STR_NEXT_LINE + value + STR_SEMICOLON;
        }
        if (!last) {
            sql += STR_NEXT_LINE;
        }
        return sql;
    }

    private static void repairByFile(AppConfigDto appConfigDto, File file) throws Exception {
        int batchNum = appConfigDto.getSystemToolScriptRepairBatchNum();
        if (batchNum > 0 && repairFileNum >= batchNum) {
            return;
        }
        if (batchNum <= 0) {
            batchNum = 999999;
        }
        if (file.isDirectory()) {
            for (File item : file.listFiles()) {
                repairByFile(appConfigDto, item);
            }
        } else {
            String fileName = file.getName();
            if (!fileName.endsWith(FILE_TYPE_SQL)) {
                return;
            }
            List<String> item = FileUtils.readNormalFile(file.getPath(), false);
            StringBuilder content = new StringBuilder();
            for (String ele : item) {
                content.append(ele);
            }
            Set<String> subTransCache = new LinkedHashSet<>();
            Set<String> subTransExtCache = new LinkedHashSet<>();
            String[] sql = content.toString().split(STR_SEMICOLON);
            for (String ele : sql) {
                if (!ele.toLowerCase().contains("tsys_subtrans_ext") && !ele.toLowerCase().contains("tsys_subtrans")) {
                    continue;
                }
                String[] sqlPart = ele.split("values");
                if (sqlPart.length == 2) {
                    String valuePart = sqlPart[1];
                    String transCode = ScriptSqlUtils.getTransCode(valuePart);
                    String subTransCode = ScriptSqlUtils.getSubTransCode(valuePart);
                    String subTransName = ScriptSqlUtils.getSubTransName(valuePart);
                    if (subTransCode.endsWith("QryC") || subTransCode.endsWith("QueryC") || subTransCode.endsWith("ColC")
                            || subTransName.endsWith("查询列") || subTransName.endsWith("结果列")) {
                        continue;
                    }
                    String key = transCode + STR_HYPHEN + subTransCode;
                    if (ele.toLowerCase().contains("tsys_subtrans_ext")) {
                        subTransExtCache.add(key);
                    } else if (ele.toLowerCase().contains("tsys_subtrans")) {
                        subTransCache.add(key);
                    }
                }
            }
            totalSubTransExtCache.addAll(subTransCache);
            Set<String> needAddLog = new HashSet<>();
            Iterator<String> iterator = subTransCache.iterator();
            while (iterator.hasNext()) {
                String transCode = iterator.next();
                if (!subTransExtCache.contains(transCode)) {
                    needAddLog.add(transCode);
                }
            }
            Iterator<String> needIterator = needAddLog.iterator();
            while (needIterator.hasNext()) {
                String[] key = needIterator.next().split(STR_HYPHEN);
                if (key.length == 2) {
                    String transCode = key[0].trim();
                    String subTransCode = key[1].trim();
                    addSubTransExt(transCode, subTransCode, getOpDir(transCode, subTransCode), file.getPath());
                }
            }
            if (CollectionUtils.isNotEmpty(needAddLog)) {
                repairFileNum++;
                appConfigDto.setRepairSchedule(repairFileNum + " / " + batchNum);
            }
        }
    }

    private static String getOpDir(String transCode, String subTransCode) {
        List<Map<String, String>> logList = totalLogCache.get(transCode);
        if (CollectionUtils.isEmpty(logList)) {
            return null;
        }
        for (Map<String, String> item : logList) {
            if (transCode.equals(item.get("transCode")) && subTransCode.equals(item.get("subTransCode"))) {
                return item.get("subTransOpDir");
            }
        }
        return null;
    }

    private static Map<String, String> buildSubTrans(String transCode, String subTransCode, String subTransOpDir) {
        Map<String, String> subTrans = new LinkedHashMap<>();
        subTrans.put("transCode", transCode);
        subTrans.put("subTransCode", subTransCode);
        subTrans.put("subTransOpDir", subTransOpDir);
        return subTrans;
    }

    private static int getRepairTotal(List<String> logList) {
        int total = 0;
        for (int i = 3; i < logList.size(); i++) {
            String item = logList.get(i).replace(ANNOTATION_TYPE_NORMAL, STR_BLANK).trim();
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (item.contains(STR_HYPHEN_1)) {
                total++;
            }
        }
        return total;
    }

}
