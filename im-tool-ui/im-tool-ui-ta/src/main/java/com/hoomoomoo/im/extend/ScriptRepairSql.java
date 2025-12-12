package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.MenuTransitionDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
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
    private static String BLOCK_LINE_SUB_TRANS_EXT_INDEX_TIPS = "-- ******************************************************* %s *******************************************************";
    private static String MENU_TIPS = "-- ************************************************************************* %s *************************************************************************";
    private static String MENU_TIPS_PART = "-- *************************************************************************";
    private static String TRANS_TIPS = "-- **************************************** %s ****************************************";

    private static Map<String, Map<String, String>> workFlowCache = new LinkedHashMap<>();
    private static Map<String, Map<String, String>> workFlowExtCache = new LinkedHashMap<>();
    private static int repairFileNum = 0;

    public static Set<String> excludeFundMenu = new HashSet<>(Arrays.asList("fundClerkList", "fundInterestInfoSet"));
    public static Set<String> includePubMenu = new HashSet<>(Arrays.asList("bizBlackInfoSet", "bizClerkInfoSet", "bizInterestRateSet", "taUnitAreaAudit"));
    public static Set<String> specialFundMenu = new HashSet<>(Arrays.asList("specialBlackInfoSet", "specialInvalidBlackInfoQuery"));

    public static void repairLackLog() throws Exception {
        repairFileNum = 0;
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
        List<String> logList = FileUtils.readNormalFile(resultPath + "\\" + LACK_LOG.getFileName());
        if (CollectionUtils.isEmpty(logList) || logList.size() <= 2) {
            return;
        }
        int total = getRepairTotal(logList);
        int repair = 0;

        String transCode = STR_BLANK;
        String subTransCode = STR_BLANK;
        for (int i = 2; i < logList.size(); i++) {
            String item = logList.get(i).replace(ANNOTATION_NORMAL, STR_BLANK).trim();
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
                    addSubTransExt(transCode, subTransCode, null,appConfigDto.getSystemToolCheckMenuFundBasePath() + ScriptSqlUtils.baseMenu);
                } else {
                    transCode = STR_BLANK;
                    subTransCode = STR_BLANK;
                }
            } else {
                // 实际路径 开通脚本无需处理
                // addSubTransExt(transCode, subTransCode, null, item);
            }
        }
    }

    public static void repairErrorLog() throws Exception {
        repairFileNum = 0;
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
        List<String> logList = FileUtils.readNormalFile(resultPath + "\\" + ERROR_LOG.getFileName());
        if (CollectionUtils.isEmpty(logList) || logList.size() <= 3) {
            return;
        }
        int total = getRepairTotal(logList);
        int repair = 0;

        String transCode = STR_BLANK;
        String subTransCode = STR_BLANK;
        String opDir = STR_BLANK;
        for (int i = 3; i < logList.size(); i++) {
            String item = logList.get(i).replace(ANNOTATION_NORMAL, STR_BLANK).trim();
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
                    String[] other = CommonUtils.trimStrToSpace(trans[1]).split(STR_SPACE);
                    if (other.length < 3) {
                        continue;
                    }
                    subTransCode = other[0].trim();
                    opDir = other[2].trim();
                    // 全量脚本
                    updateSubTransExt(transCode, subTransCode, opDir,appConfigDto.getSystemToolCheckMenuFundBasePath() + ScriptSqlUtils.baseMenu);
                } else {
                    transCode = STR_BLANK;
                    subTransCode = STR_BLANK;
                }
            } else {
                // 实际路径 开通脚本无需处理
                // updateSubTransExt(transCode, subTransCode, opDir, item);
            }
        }
    }

    private static void updateSubTransExt(String transCode, String subTransCode, String opDir, String path) throws IOException {
        if (StringUtils.isBlank(transCode) || StringUtils.isBlank(path) || StringUtils.isBlank(opDir)) {
            return;
        }
        List<String> item = FileUtils.readNormalFile(path);
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
        FileUtils.writeFile(path, item);
    }

    private static void addSubTransExt(String transCode, String subTransCode, String opDir, String path) throws IOException {
        if (StringUtils.isBlank(transCode) || StringUtils.isBlank(path)) {
            return;
        }
        List<String> item = FileUtils.readNormalFile(path);
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
        FileUtils.writeFile(path, item);
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



    public static void repairExt() throws Exception {
        repairFileNum = 0;
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        File fileExt = new File(appConfigDto.getSystemToolCheckMenuFundExtPath() + ScriptSqlUtils.basePathExt);
        Set<String> skip = ScriptSqlUtils.initRepairExtSkip();
        for (File file : fileExt.listFiles()) {
            repairExtByFile(appConfigDto, file, skip);
        }
    }

    private static void repairExtByFile(AppConfigDto appConfigDto, File file, Set<String> skip) throws Exception {
        int batchNum = Integer.valueOf(appConfigDto.getSystemToolScriptRepairBatchNum());
        if (batchNum > 0 && repairFileNum > batchNum) {
            return;
        }
        if (file.isDirectory()) {
            for (File item : file.listFiles()) {
                repairExtByFile(appConfigDto, item, skip);
            }
        } else {
            String fileName = file.getName();
            if (!fileName.endsWith(FILE_TYPE_SQL)) {
                return;
            }
            if (skip.contains(fileName)) {
                return;
            }
            List<String> item = FileUtils.readNormalFile(file.getPath());
            boolean modifyTrans = deleteSql(item, "tsys_trans", skip);
            boolean modifySubtrans = deleteSql(item, "tsys_subtrans", skip);
            boolean modifySubtransExt = deleteSql(item, "tsys_subtrans_ext", skip);
            boolean modifyWorkFlow = deleteSql(item, "tbworkflowsubtrans", skip);
            boolean modifyWorkFlowExt = deleteSql(item, "tbworkflowsubtransext", skip);
            if (modifyTrans || modifySubtrans || modifySubtransExt || modifyWorkFlow || modifyWorkFlowExt) {
                FileUtils.writeFile(file.getPath(), item);
                repairFileNum++;
            }
        }
    }

    private static boolean deleteSql(List<String> item, String tableName, Set<String> skip) {
        Iterator<String> iterator = item.listIterator();
        boolean modify = false;
        boolean endFlag = true;
        boolean subTrans = "tsys_subtrans".equals(tableName);
        while (iterator.hasNext()) {
            String element = iterator.next();
            String ele = element.toLowerCase().trim();
            if (ele.contains(" " + tableName + " ")) {
                if (ele.contains("delete ")) {
                    endFlag = true;
                } else if (ele.contains("insert into")) {
                    endFlag = false;
                }
                modify = true;
                iterator.remove();
            } else {
                if (!endFlag && (ele.contains("values ") || ele.contains("values("))) {
                    modify = true;
                    endFlag = true;
                    if (subTrans) {
                        String subTransCode = ScriptSqlUtils.getSubTransCodeByWhole(element);
                        if (skip.contains(subTransCode)) {
                            continue;
                        }
                    }
                    iterator.remove();
                }
            }
        }
        if (!modify) {
            return false;
        }
        iterator = item.listIterator();
        boolean hasBlank = false;
        while (iterator.hasNext()) {
            String ele = iterator.next().toLowerCase().trim();
            if (hasBlank && StringUtils.isBlank(ele)) {
                iterator.remove();
                continue;
            }
            if (StringUtils.isBlank(ele)) {
                hasBlank = true;
            } else {
                hasBlank = false;
            }
        }
        for (int i=0; i<item.size(); i++) {
            String ele = item.get(i).trim();
            String eleLower = CommonUtils.trimStrToBlank(ele);
            if (eleLower.startsWith("values(") && eleLower.split(STR_COMMA).length == 11) {
                String prevEle = CommonUtils.trimStrToSpace(item.get(i - 1)).trim();
                String transCode = ScriptSqlUtils.getTransCode(ele);
                String subTransCode = ScriptSqlUtils.getSubTransCode(ele);
                if (!prevEle.startsWith("insert into")) {
                    String delete = "delete from tsys_subtrans where trans_code = '" + transCode + "' and sub_trans_code = '" + subTransCode + "';" + STR_NEXT_LINE;
                    delete += "insert into tsys_subtrans (trans_code, sub_trans_code, sub_trans_name, rel_serv, rel_url, ctrl_flag, login_flag, remark, ext_field_1, ext_field_2, ext_field_3)" + STR_NEXT_LINE;
                    item.set(i, delete + ele);
                }
            }
        }
        return true;
    }
    public static void repairWorkFlow() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String workFlowPath = appConfigDto.getSystemToolCheckMenuFundBasePath() + ScriptSqlUtils.workFlow;
        List<String> workFlow = FileUtils.readNormalFile(workFlowPath);
        StringBuilder content = new StringBuilder();
        for (String ele : workFlow) {
            String lowerEle = ele.toLowerCase();
            if (!lowerEle.contains("insert") && !lowerEle.contains("values")) {
                continue;
            }
            if (lowerEle.contains("delete ")) {
                continue;
            }
            content.append(ele);
        }
        String[] sql = content.toString().split(STR_SEMICOLON);
        Map<String, Map<String, String>> totalWorkFlow = new LinkedHashMap<>();
        Map<String, Map<String, String>> totalWorkExtFlow = new LinkedHashMap<>();
        for (String ele : sql) {
            if (!ele.toLowerCase().contains("tbworkflowsubtrans")) {
                continue;
            }
            String transCode = ScriptSqlUtils.getTransCodeByWorkFlow(ele);
            String subTransCode = ScriptSqlUtils.getSubTransCodeByWorkFlow(ele);
            if (ele.toLowerCase().contains("tbworkflowsubtransext")) {
                if (!totalWorkExtFlow.containsKey(transCode)) {
                    totalWorkExtFlow.put(transCode, new LinkedHashMap<>());
                }
                totalWorkExtFlow.get(transCode).put(subTransCode, ele);
            } else {
                if (!totalWorkFlow.containsKey(transCode)) {
                    totalWorkFlow.put(transCode, new LinkedHashMap<>());
                }
                totalWorkFlow.get(transCode).put(subTransCode, ele);
            }
        }

        File fileExt = new File(appConfigDto.getSystemToolCheckMenuFundExtPath() + ScriptSqlUtils.basePathExt);
        for (File file : fileExt.listFiles()) {
            getWorkFlowByFile(appConfigDto, file);
        }

        Iterator<String> iterator = workFlowCache.keySet().iterator();
        while (iterator.hasNext()) {
            String transCode = iterator.next();
            Map<String, String> flow = workFlowCache.get(transCode);
            if (totalWorkFlow.containsKey(transCode)) {
                Iterator<String> flowIterator = flow.keySet().iterator();
                while (flowIterator.hasNext()) {
                    String flowSubTransCode = flowIterator.next();
                    if (!totalWorkFlow.get(transCode).containsKey(flowSubTransCode)) {
                        totalWorkFlow.get(transCode).put(flowSubTransCode, flow.get(flowSubTransCode));
                    }
                }
            } else {
                totalWorkFlow.put(transCode, flow);
            }
        }
        Iterator<String> extIterator = workFlowExtCache.keySet().iterator();
        while (extIterator.hasNext()) {
            String transCode = extIterator.next();
            Map<String, String> flow = workFlowExtCache.get(transCode);
            if (totalWorkExtFlow.containsKey(transCode)) {
                Iterator<String> flowIterator = flow.keySet().iterator();
                while (flowIterator.hasNext()) {
                    String flowSubTransCode = flowIterator.next();
                    if (!totalWorkExtFlow.get(transCode).containsKey(flowSubTransCode)) {
                        totalWorkExtFlow.get(transCode).put(flowSubTransCode, flow.get(flowSubTransCode));
                    }
                }
            } else {
                totalWorkExtFlow.put(transCode, workFlowExtCache.get(transCode));
            }
        }

        String basePath = appConfigDto.getSystemToolCheckMenuFundBasePath() + ScriptSqlUtils.baseMenu;
        List<String> menuList = FileUtils.readNormalFile(basePath);
        StringBuilder menu = new StringBuilder();
        Map<String, String> menuGroupTitle = new LinkedHashMap();
        for (int i=7; i<menuList.size(); i++) {
            String item = menuList.get(i).trim();
            String itemLower = item.toLowerCase();
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (itemLower.startsWith(MENU_TIPS_PART) && itemLower.contains("三级菜单")) {
                String[] element = CommonUtils.trimStrToSpace(item).split(STR_SPACE);
                if (element.length == 7) {
                    String groupCode = element[4].split("\\|")[0];
                    menuGroupTitle.put(groupCode, item);
                }
            }
            if (!itemLower.contains("insert") && !itemLower.contains("values")) {
                continue;
            }
            menu.append(item);
        }
        String[] menuInfo = menu.toString().split(STR_SEMICOLON);
        Map<String, String> allMenu = new LinkedHashMap<>();
        for (String ele : menuInfo) {
            if (!ele.toLowerCase().contains("tsys_menu")) {
                continue;
            }
            String menuCode = ScriptSqlUtils.getMenuCode(ele);
            allMenu.put(menuCode, ele);
        }
        List<String> res = new ArrayList<>();
        res.add(BLOCK_LINE_INDEX);
        res.add(BLOCK_LINE_INDEX);
        res.add(BLOCK_LINE_INDEX);
        res.add("-- 复核信息全量脚本");
        res.add("-- 禁止配置脚本无规律放置 按菜单层级放置");
        res.add(STR_BLANK);
        res.add("delete from tbworkflowsubtrans where trans_code like 'fund%';");
        res.add("delete from tbworkflowsubtransext where trans_code like 'fund%';");
        res.add(STR_BLANK);

        Iterator<String> menuIterator = allMenu.keySet().iterator();
        String groupCode = STR_BLANK;
        while (menuIterator.hasNext()) {
            String menuCode = menuIterator.next();
            String menuEle = allMenu.get(menuCode);
            String transCode = ScriptSqlUtils.getTransCodeByMenu(menuEle);
            if (!totalWorkFlow.containsKey(transCode)) {
                continue;
            }
            String parentCode = ScriptSqlUtils.getParentCode(menuEle);
            String menuName = ScriptSqlUtils.getMenuName(menuEle);
            if (StringUtils.isNotBlank(groupCode) && !groupCode.equals(parentCode)){
                res.add(menuGroupTitle.get(groupCode));
                groupCode = STR_BLANK;
            }
            if (StringUtils.isBlank(groupCode)) {
                groupCode = parentCode;
                if (menuGroupTitle.containsKey(parentCode)) {
                    res.add(STR_BLANK);
                    res.add(menuGroupTitle.get(parentCode).replace("结束", "开始"));
                }
            } else {
                res.add(STR_BLANK);
            }
            res.add(ANNOTATION_NORMAL + STR_SPACE + menuName);
            buildWorkFlow(res, totalWorkFlow.get(transCode));
            if (MapUtils.isNotEmpty(totalWorkExtFlow.get(transCode))) {
                res.add(STR_BLANK);
            }
            buildWorkFlow(res, totalWorkExtFlow.get(transCode));
            totalWorkFlow.remove(transCode);
        }
        res.add(menuGroupTitle.get(groupCode));
        res.add(STR_BLANK);

        List<String> error = new ArrayList<>();
        if (MapUtils.isNotEmpty(totalWorkFlow)) {
            Iterator<String> errorIterator = totalWorkFlow.keySet().iterator();
            while (errorIterator.hasNext()) {
                String menuCode = errorIterator.next();
                Map<String, String> ele = totalWorkFlow.get(menuCode);
                Iterator<String> eleIterator = ele.keySet().iterator();
                while (eleIterator.hasNext()) {
                    String subTransCode = eleIterator.next();
                    error.add(formatSql(ele.get(subTransCode), true, false));
                    error.add(STR_BLANK);
                }
            }
            if (CollectionUtils.isNotEmpty(error)) {
                // error.add(String.format(MENU_TIPS, "未匹配交易码: " + subError.size() / 2));
                // 错误数据补充到最后面
                res.add(String.format(MENU_TIPS, "自定义 开始"));
                res.addAll(error.subList(0, error.size() - 1));
                res.add(String.format(MENU_TIPS, "自定义 结束"));
                error.clear();
            }
        }

        res.add(STR_BLANK);
        res.add("commit;");

        String checkFile = workFlowPath.replace(".sql", ".check.sql");
        if (CollectionUtils.isNotEmpty(error)) {
            FileUtils.writeFile(checkFile, error);
        } else {
            File file = new File(checkFile);
            if (file.exists()) {
                file.delete();
            }
        }
        String resFile = workFlowPath.replace(".sql", ".res.sql");
        if (CollectionUtils.isNotEmpty(error)) {
            FileUtils.writeFile(resFile, res);
            throw new Exception("检查存在未匹配项，请查看结果文件");
        } else {
            File file = new File(resFile);
            if (file.exists()) {
                file.delete();
            }
            FileUtils.writeFile(workFlowPath, res);
        }
    }

    private static void buildWorkFlow(List<String> res, Map<String, String> flow) {
        if (MapUtils.isEmpty(flow)) {
            return;
        }
        Iterator<String> flowIterator =  flow.keySet().iterator();
        while (flowIterator.hasNext()) {
            String subTransCode = flowIterator.next();
            String subFlow = formatSql(flow.get(subTransCode), false, true);
            if (!subFlow.contains("tbworkflowsubtransext")) {
                subFlow = subFlow.replaceAll(ANNOTATION_NORMAL, STR_BLANK).trim();
            }
            res.add(subFlow);
        }
    }

    private static void getWorkFlowByFile(AppConfigDto appConfigDto, File file) throws Exception {
        if (file.isDirectory()) {
            for (File item : file.listFiles()) {
                getWorkFlowByFile(appConfigDto, item);
            }
        } else {
            String fileName = file.getName();
            if (!fileName.endsWith(FILE_TYPE_SQL)) {
                return;
            }
            List<String> item = FileUtils.readNormalFile(file.getPath());
            StringBuilder content = new StringBuilder();
            for (String ele : item) {
                String lowerEle = ele.toLowerCase();
                if (!lowerEle.contains("insert") && !lowerEle.contains("values")) {
                    continue;
                }
                if (lowerEle.contains("delete ")) {
                    continue;
                }
                content.append(ele);
            }
            String[] sql = content.toString().split(STR_SEMICOLON);
            for (String ele : sql) {
                if (!ele.toLowerCase().contains("tbworkflowsubtrans")) {
                    continue;
                }
                String transCode = ScriptSqlUtils.getTransCodeByWorkFlow(ele);
                String subTransCode = ScriptSqlUtils.getSubTransCodeByWorkFlow(ele);

                if (ele.toLowerCase().contains("tbworkflowsubtransext")) {
                    if (!workFlowExtCache.containsKey(transCode)) {
                        workFlowExtCache.put(transCode, new LinkedHashMap<>());
                    }
                    workFlowExtCache.get(transCode).put(subTransCode, ele);
                } else {
                    if (!workFlowCache.containsKey(transCode)) {
                        workFlowCache.put(transCode, new LinkedHashMap<>());
                    }
                    workFlowCache.get(transCode).put(subTransCode, ele);
                }
            }
        }
    }

    public static void repairNewMenu() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String basePath = appConfigDto.getSystemToolCheckMenuPubBasePath();
        if (StringUtils.isBlank(basePath)) {
            throw new Exception("请配置参数【system.tool.check.menu.pub.base.path】\n");
        }
        File baseFile = new File(basePath);
        if (!baseFile.exists()) {
            throw new Exception("请检查参数路径是否存在【system.tool.check.menu.pub.base.path】\n");
        }

        Map<String, String> newUedMenu = new LinkedHashMap<>();
        Map<String, String> parentMenu = new LinkedHashMap<>();
        Map<String, String> firstMenu =  new LinkedHashMap<>();
        Map<String, String> secondMenu =  new LinkedHashMap<>();
        Map<String, String> thirdMenu =  new LinkedHashMap<>();
        Map<String, String> fourthMenu =  new LinkedHashMap<>();
        Map<String, String> trans =  new LinkedHashMap<>();
        File[] parentFile = baseFile.listFiles();
        for (File file : parentFile) {
            String fileName = file.getName();
            if (!fileName.startsWith("00console-vue-menu-std-")) {
                continue;
            }
            List<String> menuInfo = ScriptSqlUtils.getSqlByFile(file.getPath());
            for (String item : menuInfo) {
                String menuCode = ScriptSqlUtils.getMenuCode(item);
                if ("bizInterestRateSet".equals(menuCode)) {
                    item = formatSql(item, true, false);
                    item = item.substring(0, item.length() - 1);
                }
                if (fileName.startsWith("00console-vue-menu-std-bizroot")) {
                    if (!menuCode.equals("bizroot") && !menuCode.equals("frame")) {
                        firstMenu.put(menuCode, item);
                    }
                } else {
                    parentMenu.put(menuCode, item);
                }
            }
        }
        String newUedPage = appConfigDto.getSystemToolCheckMenuFundBasePath() + ScriptSqlUtils.newUedPage;
        List<String> newUedMenuInfo = ScriptSqlUtils.getSqlByFile(newUedPage);
        for (String item : newUedMenuInfo) {
            String menuCode = ScriptSqlUtils.getMenuCode(item);
            if (includePubMenu.contains(menuCode)) {
                continue;
            }
            if (item.toLowerCase().contains("tsys_menu_std")) {
                newUedMenu.put(menuCode, item);
            } else {
                trans.put(menuCode, item);
            }
        }
        Iterator<String> firstIterator = firstMenu.keySet().iterator();
        while (firstIterator.hasNext()) {
            String menuCode = firstIterator.next();
            Iterator<String> secondIterator = parentMenu.keySet().iterator();
            while (secondIterator.hasNext()) {
                String secondMenuCode = secondIterator.next();
                String menuInfo = parentMenu.get(secondMenuCode);
                String parentCode = ScriptSqlUtils.getParentCode(menuInfo);
                if (menuCode.equals(parentCode)) {
                    secondMenu.put(secondMenuCode, menuInfo);
                    secondIterator.remove();
                }
            }
            Iterator<String> newUedMenuIterator = newUedMenu.keySet().iterator();
            while (newUedMenuIterator.hasNext()) {
                String uedMenuCode = newUedMenuIterator.next();
                String menuInfo = newUedMenu.get(uedMenuCode);
                String parentCode = ScriptSqlUtils.getParentCode(menuInfo);
                if (menuCode.equals(parentCode)) {
                    secondMenu.put(uedMenuCode, menuInfo);
                }
            }
        }
        Iterator<String> secondIterator = secondMenu.keySet().iterator();
        while (secondIterator.hasNext()) {
            String menuCode = secondIterator.next();
            Iterator<String> thirdIterator = parentMenu.keySet().iterator();
            while (thirdIterator.hasNext()) {
                String thirdMenuCode = thirdIterator.next();
                String menuInfo = parentMenu.get(thirdMenuCode);
                String parentCode = ScriptSqlUtils.getParentCode(menuInfo);
                if (menuCode.equals(parentCode)) {
                    thirdMenu.put(thirdMenuCode, menuInfo);
                    thirdIterator.remove();
                }
            }
            Iterator<String> newUedMenuIterator = newUedMenu.keySet().iterator();
            while (newUedMenuIterator.hasNext()) {
                String uedMenuCode = newUedMenuIterator.next();
                String menuInfo = newUedMenu.get(uedMenuCode);
                String parentCode = ScriptSqlUtils.getParentCode(menuInfo);
                if (menuCode.equals(parentCode)) {
                    thirdMenu.put(uedMenuCode, menuInfo);
                }
            }
        }
        Iterator<String> thirdIterator = thirdMenu.keySet().iterator();
        while (thirdIterator.hasNext()) {
            String menuCode = thirdIterator.next();
            Iterator<String> fourthIterator = parentMenu.keySet().iterator();
            while (fourthIterator.hasNext()) {
                String fourthMenuCode = fourthIterator.next();
                String menuInfo = parentMenu.get(fourthMenuCode);
                String parentCode = ScriptSqlUtils.getParentCode(menuInfo);
                if (menuCode.equals(parentCode)) {
                    fourthMenu.put(fourthMenuCode, menuInfo);
                    fourthIterator.remove();
                }
            }
            Iterator<String> newUedMenuIterator = newUedMenu.keySet().iterator();
            while (newUedMenuIterator.hasNext()) {
                String uedMenuCode = newUedMenuIterator.next();
                String menuInfo = newUedMenu.get(uedMenuCode);
                String parentCode = ScriptSqlUtils.getParentCode(menuInfo);
                if (menuCode.equals(parentCode)) {
                    fourthMenu.put(uedMenuCode, menuInfo);
                }
            }
        }
        // 脚本脚本
        List<String> res = new ArrayList<>();
        res.add(BLOCK_LINE_INDEX);
        res.add(BLOCK_LINE_INDEX);
        res.add(BLOCK_LINE_INDEX);
        res.add("-- 新版UED全量脚本");
        res.add("-- 禁止配置脚本无规律放置 按菜单层级放置");
        res.add(STR_BLANK);
        Iterator<String> first = firstMenu.keySet().iterator();
        while (first.hasNext()) {
            String firstMenuCode = first.next();
            String firstMenuInfo = firstMenu.get(firstMenuCode);
            String firstMenuName = ScriptSqlUtils.getMenuName(firstMenuInfo);
            List<String> subSecondMenu = getPartMenuInfo(secondMenu, firstMenuCode, false);
            for (String second : subSecondMenu) {
                String secondMenuCode = ScriptSqlUtils.getMenuCode(second);
                String secondMenuName = ScriptSqlUtils.getMenuName(second);
                List<String> subThirdMenu = getPartMenuInfo(thirdMenu, secondMenuCode, false);
                List<String> realThirdMenu = getRealPartMenuInfo(subThirdMenu);
                MenuTransitionDto menuTransitionDto = new MenuTransitionDto();
                menuTransitionDto.setHasMerger(false);
                List<String> partMenu = new ArrayList<>();
                for (int i=0; i<realThirdMenu.size(); i++) {
                    generateMenuInfo(i, realThirdMenu.get(i), realThirdMenu, newUedMenu, trans, partMenu, menuTransitionDto);
                }
                if (CollectionUtils.isNotEmpty(partMenu)) {
                    res.add(String.format(MENU_TIPS, firstMenuCode + "|" + firstMenuName + " " + secondMenuCode + "|" + secondMenuName + " 开始"));
                    res.addAll(partMenu);
                    res.add(String.format(MENU_TIPS, firstMenuCode + "|" + firstMenuName + " " + secondMenuCode + "|" + secondMenuName + " 结束"));
                    res.add(STR_BLANK);
                }
                for (String third : subThirdMenu) {
                    String thirdMenuCode = ScriptSqlUtils.getMenuCode(third);
                    String thirdMenuName = ScriptSqlUtils.getMenuName(third);
                    List<String> subFourthMenu = getPartMenuInfo(fourthMenu, thirdMenuCode, true);
                    partMenu = new ArrayList<>();
                    menuTransitionDto = new MenuTransitionDto();
                    menuTransitionDto.setHasMerger(false);
                    for (int i=0; i<subFourthMenu.size(); i++) {
                        generateMenuInfo(i, subFourthMenu.get(i), subFourthMenu, newUedMenu, trans, partMenu, menuTransitionDto);
                    }
                    if (menuTransitionDto.hasMerger) {
                        partMenu.add("-- " + menuTransitionDto.getMergerMenuName() + " 结束");
                        menuTransitionDto.setHasMerger(false);
                    }
                    if (CollectionUtils.isNotEmpty(partMenu)) {
                        res.add(String.format(MENU_TIPS, firstMenuCode + "|" + firstMenuName + " " + secondMenuCode + "|" + secondMenuName + " " + thirdMenuCode + "|" + thirdMenuName + " 开始"));
                        res.addAll(partMenu);
                        res.add(String.format(MENU_TIPS, firstMenuCode + "|" + firstMenuName + " " + secondMenuCode + "|" + secondMenuName + " " + thirdMenuCode + "|" + thirdMenuName + " 结束"));
                        res.add(STR_BLANK);
                    }
                }
            }
        }
        res.add(STR_BLANK);

        boolean logMenuStart = false;
        List<String> error = new ArrayList<>();
        if (MapUtils.isNotEmpty(newUedMenu)) {
            Iterator<String> iterator = newUedMenu.keySet().iterator();
            List<String> subError = new ArrayList<>();
            while (iterator.hasNext()) {
                String menuCode = iterator.next();
                if (excludeFundMenu.contains(menuCode)) {
                    continue;
                }
                String sql = formatSql(newUedMenu.get(menuCode), true);
                if (StringUtils.equals(ScriptSqlUtils.getParentCode(sql), KEY_LOG_PARENT_CODE)) {
                    if (!logMenuStart) {
                        logMenuStart = true;
                        res.add(String.format(MENU_TIPS, "清算流程记录日志 开始"));
                    }
                    res.add(formatSqlAddDelete(sql, true));
                    res.add(STR_BLANK);
                    continue;
                }
                subError.add(sql);
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

        if (MapUtils.isNotEmpty(trans)) {
            Iterator<String> iterator = trans.keySet().iterator();
            List<String> subError = new ArrayList<>();
            while (iterator.hasNext()) {
                String menuCode = iterator.next();
                String transCode = formatSql(trans.get(menuCode), true);
                if (StringUtils.isNotBlank(transCode)) {
                    subError.add(transCode);
                    subError.add(STR_BLANK);

                }
            }
            if (CollectionUtils.isNotEmpty(subError)) {
                error.add(String.format(MENU_TIPS, "未匹配交易码: " + subError.size() / 2));
                error.addAll(subError);
            }
        }

        String checkFile = newUedPage.replace(".sql", ".check.sql");
        if (CollectionUtils.isNotEmpty(error)) {
            FileUtils.writeFile(checkFile, error);
        } else {
            File file = new File(checkFile);
            if (file.exists()) {
                file.delete();
            }
        }
        String lastLine = res.get(res.size() - 1);
        if (StringUtils.isBlank(lastLine)) {
            res.set(res.size() - 1, String.format(MENU_TIPS, "清算流程记录日志 结束"));
        } else {
            res.add(String.format(MENU_TIPS, "清算流程记录日志 结束"));
        }
        res.add(STR_BLANK);
        res.add("commit;");
        String resFile = newUedPage.replace(".sql", ".res.sql");
        if (CollectionUtils.isNotEmpty(error)) {
            FileUtils.writeFile(resFile, res);
            throw new Exception("检查存在未匹配项，请查看结果文件");
        } else {
            File file = new File(resFile);
            if (file.exists()) {
                file.delete();
            }
            FileUtils.writeFile(newUedPage, res);
        }
    }

    public static void repairOldMenu() throws Exception {
        List<String> res = new ArrayList<>();
        res.add(BLOCK_LINE_INDEX);
        res.add(BLOCK_LINE_INDEX);
        res.add(BLOCK_LINE_INDEX);
        res.add("-- 老版UED全量脚本");
        res.add("-- 禁止配置脚本无规律放置 按菜单层级放置");
        res.add("-- 配置数据放置对应区块内 tsys_menu tsys_trans tsys_subtrans tsys_subtrans_ext 分区块放置");
        res.add(STR_BLANK);
        res.add("delete from tsys_menu where menu_code = 'console-fund-ta-vue';");
        res.add("delete from tsys_menu where menu_code like 'fund%';");
        res.add("delete from tsys_trans where trans_code like 'fund%';");
        res.add("delete from tsys_subtrans where trans_code like 'fund%';");
        res.add("delete from tsys_subtrans_ext where trans_code like 'fund%';");
        res.add(STR_NEXT_LINE);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String basePath = appConfigDto.getSystemToolCheckMenuFundBasePath() + ScriptSqlUtils.baseMenu;
        List<String> menuList = FileUtils.readNormalFile(basePath);
        StringBuilder menu = new StringBuilder();

        // 获取平台菜单
        int indexForPub = 0;
        for (int i=13; i<menuList.size(); i++) {
            String item = menuList.get(i).trim();
            if (item.contains("平台菜单")) {
                res.add(item);
                indexForPub++;
                continue;
            }
            if (indexForPub == 2) {
                break;
            }
            if (indexForPub == 1) {
                res.add(item);
            }
        }

        res.add(STR_NEXT_LINE);

        for (int i=20; i<menuList.size(); i++) {
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
        List<String> allMenu = new ArrayList<>();
        boolean exist;
        for (String item : menuInfo) {
            if (item.contains("select") && item.contains("from")) {
                item = item.substring(0, item.indexOf("from")).replace("select", "values (") + ")";
            }
            String menuCode = ScriptSqlUtils.getMenuCode(item);
            allMenu.add(menuCode);
            String subTransCode = ScriptSqlUtils.getSubTransCodeByWhole(item);
            String itemLower = CommonUtils.trimStrToSpace(item.toLowerCase().replace("--", ""));
            if (itemLower.startsWith("insert into tsys_menu (") || itemLower.startsWith("insert into tsys_menu(")) {
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
            } else if (itemLower.startsWith("insert into tsys_trans (") || itemLower.startsWith("insert into tsys_trans(")) {
                if (!menuTrans.containsKey(menuCode)) {
                    menuTrans.put(menuCode, item);
                }
            } else if (itemLower.startsWith("insert into tsys_subtrans (") || itemLower.startsWith("insert into tsys_subtrans(")) {
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
            } else if (itemLower.startsWith("insert into tsys_subtrans_ext (") || itemLower.startsWith("insert into tsys_subtrans_ext(")) {
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

        String menuConditionPath = appConfigDto.getSystemToolCheckMenuFundBasePath() + ScriptSqlUtils.menuCondition;
        List<String> menuConditionList = FileUtils.readNormalFile(menuConditionPath);
        StringBuilder menuCondition = new StringBuilder();
        for (int i=0; i<menuConditionList.size(); i++) {
            String item = menuConditionList.get(i).trim();
            String itemLower = item.toLowerCase();
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (!itemLower.contains("insert") && !itemLower.contains("values")) {
                continue;
            }
            menuCondition.append(item);
        }
        String[] menuConditionBase = menuCondition.toString().split(STR_SEMICOLON);
        for (String ele : menuConditionBase) {
            if (!ele.toLowerCase().contains("tsys_subtrans")) {
                continue;
            }
            String transCode = ScriptSqlUtils.getTransCodeByWhole(ele);
            String subTransCode = ScriptSqlUtils.getSubTransCodeByWhole(ele);
            boolean has = false;
            List<String> subList = menuSubTrans.get(transCode);
            if (CollectionUtils.isEmpty(subList)) {
                continue;
            }
            inner: for (String sub : subList) {
                String subTransCodeTmp = ScriptSqlUtils.getSubTransCodeByWhole(sub);
                if (subTransCode.equals(subTransCodeTmp)) {
                    has = true;
                    break inner;
                }
            }
            if (!has) {
                subList.add(ele);
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
        Map<String, List<String>> skipCache = initSkipCache();
        while (firstMenuIterator.hasNext()) {
            index++;
            String menuCode = firstMenuIterator.next();
            String menuDetail = firstMenu.get(menuCode);
            String menuName = ScriptSqlUtils.getMenuName(menuDetail);
            String deleteMenu = addDeleteFundMenu(menuDetail, "menu");
            if (StringUtils.isNotBlank(deleteMenu)) {
                res.add(deleteMenu);
            }
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
                    String deleteSubThirdMenu = addDeleteFundMenu(subThirdInfo, "menu");
                    if (StringUtils.isNotBlank(deleteSubThirdMenu)) {
                        third.add(deleteSubThirdMenu);
                    }
                    third.add(formatSql(subThirdInfo, j == subThird.size() - 1));
                    boolean flag = getTransCodeAndSubTransCode(menuTrans, menuSubTrans, transAndSubTrans, subTransAndSubTrans, subThirdInfo, allMenu, skipCache.get(KEY_TRANS));
                    if (!flag) {
                        notTransExist.add(subThirdInfo);
                    }
                    StringBuilder subTransExtTmp = new StringBuilder();
                    flag = getSubTransCodeExt(menuSubTransExt, subTransExt, subTransExtTmp, subThirdInfo, allMenu, skipCache.get(KEY_TRANS));
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
                String deleteSecondMenu = addDeleteFundMenu(subSecond.get(j), "menu");
                if (StringUtils.isNotBlank(deleteSecondMenu)) {
                    second.add(deleteSecondMenu);
                }
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
        account.add("delete from tsys_menu where menu_code like 'ptaAccountManageFund%';");
        account.add(STR_BLANK);
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
                boolean flag = getTransCodeAndSubTransCode(menuTrans, menuSubTrans, transAndSubTrans, subTransAndSubTrans, subThirdInfo, allMenu, skipCache.get(KEY_TRANS));
                if (!flag) {
                    notTransExist.add(subThirdInfo);
                }
                StringBuilder subTransExtTmp = new StringBuilder();
                flag = getSubTransCodeExt(menuSubTransExt, subTransExt, subTransExtTmp, subThirdInfo, allMenu, skipCache.get(KEY_TRANS));
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
        if (otherMenu.containsKey(KEY_LOG_MENU_CODE)) {
            res.add(formatSqlAddDelete(otherMenu.get(KEY_LOG_MENU_CODE), true));
            res.add(STR_BLANK);
            otherMenu.remove(KEY_LOG_MENU_CODE);
        }
        res.add(STR_BLANK);
        res.add(BLOCK_LINE_INDEX);
        res.add(String.format(BLOCK_LINE_INDEX_TIPS, "  交易码  tsys_trans  "));
        res.add(String.format(BLOCK_LINE_INDEX_TIPS, "子交易码 tsys_subtrans"));
        res.add(BLOCK_LINE_INDEX);

        if (menuTrans.containsKey("ifmCQsglAutoClearLog")) {
            String clearLog = menuTrans.get("ifmCQsglAutoClearLog");
            res.add(addDeleteFundMenu(clearLog, "trans"));
            res.add(formatSql(clearLog, false, false));
        }
        if (menuSubTrans.containsKey("ifmCQsglAutoClearLog")) {
            List<String> clearLog = menuSubTrans.get("ifmCQsglAutoClearLog");
            for (String item : clearLog) {
                res.add(formatSql(item, false, false));
            }
        }

        Set<String> skip = ScriptSqlUtils.initRepairExtSkip();
        boolean update;
        boolean nextLine = false;
        for (int i=0; i<transAndSubTrans.size(); i++) {
            update = false;
            nextLine = transAndSubTrans.get(i).endsWith(STR_NEXT_LINE);
            String[] ele = transAndSubTrans.get(i).trim().split(STR_SEMICOLON);
            for (int j=0; j<ele.length; j++) {
                String item = ele[j];
                String subTransCode = ScriptSqlUtils.getSubTransCodeByWhole(item);
                if (skip.contains(subTransCode)) {
                    update = true;
                    // item = item.replace("insert into", "insert into");
                    item = item.replace("INSERT INTO", "insert into");
                    // item = item.replace("values(", "values(");
                    item = item.replace("VALUES(", "values(");
                    // item = item.replace("values (", "values (");
                    item = item.replace("VALUES (", "values (");
                    ele[j] = item;
                }
            }
            if (update) {
                StringBuilder after = new StringBuilder();
                for (String item : ele) {
                    String itemLower = CommonUtils.trimStrToSpace(item.toLowerCase());
                    after.append(item);
                    if (itemLower.contains(" insert into") || itemLower.contains(" values") || itemLower.contains(" delete")) {
                        after.append(STR_SEMICOLON);
                    }
                }
                if (nextLine) {
                    after.append(STR_NEXT_LINE);
                }
                transAndSubTrans.set(i, after.toString());
            }
        }
        res.addAll(transAndSubTrans);

        res.add(STR_BLANK);
        res.add(BLOCK_LINE_INDEX);
        res.add(String.format(BLOCK_LINE_SUB_TRANS_EXT_INDEX_TIPS, "                      日志  tsys_subtrans_ext                     "));
        res.add(String.format(BLOCK_LINE_SUB_TRANS_EXT_INDEX_TIPS, "0:新增 1:修改 2:删除 3:其他 4:查询 5:下载 6:导入 7:审批 8:接口 9:复制"));
        res.add(BLOCK_LINE_INDEX);
        if (CollectionUtils.isNotEmpty(subTransExt)) {
            subTransExt = subTransExt.subList(0, subTransExt.size() - 1);
        }
        res.addAll(subTransExt);
        res.add(TSYS_SUB_TRANS_EXT_END_LINE_INDEX);
        res.add(STR_BLANK);
        res.add("commit;");

        List<String> error = new ArrayList<>();
        if (MapUtils.isNotEmpty(otherMenu)) {
            List<String> skipMenu = skipCache.get(KEY_MENU);
            List<String> subError = new ArrayList<>();
            Iterator<String> otherIterator = otherMenu.keySet().iterator();
            while (otherIterator.hasNext()) {
                String menuCode = otherIterator.next();
                if (skipMenu.contains(menuCode) || menuCode.startsWith("biz")) {
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
            FileUtils.writeFile(checkFile, error);
        } else {
            File file = new File(checkFile);
            if (file.exists()) {
                file.delete();
            }
        }
        String resFile = basePath.replace(".sql", ".res.sql");
        if (CollectionUtils.isNotEmpty(error)) {
            FileUtils.writeFile(resFile, res);
            throw new Exception("检查存在未匹配项，请查看结果文件");
        } else {
            File file = new File(resFile);
            if (file.exists()) {
                file.delete();
            }
            FileUtils.writeFile(basePath, res);
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

    private static String addDeleteFundMenu(String menuInfo, String checkType) {
        String menuCode = ScriptSqlUtils.getMenuCode(menuInfo);
        boolean fundMenu = menuCode.startsWith("fund");
        String delete = STR_BLANK;
        String subDelete = STR_BLANK;
        if (!fundMenu) {
            if ("menu".equals(checkType)) {
                delete = "delete from tsys_menu where menu_code = '" + menuCode + "';";
            } else if ("trans".equals(checkType)) {
                delete = "delete from tsys_trans where trans_code = '" + menuCode + "';\n";
                subDelete = "delete from tsys_subtrans where trans_code = '" + menuCode + "';\n";
            } else {
                delete = "delete from tsys_subtrans_ext where trans_code = '" + menuCode + "';";
            }
            if ("menu".equals(checkType) && menuInfo.startsWith(ANNOTATION_NORMAL)) {
                delete = ANNOTATION_NORMAL + STR_SPACE + delete;
                if (StringUtils.isNotBlank(subDelete)) {
                    subDelete = ANNOTATION_NORMAL + STR_SPACE + subDelete;
                }
            }
        }
        return delete + subDelete;
    }
    private static Map<String, List<String>> initSkipCache() throws IOException {
        Map<String, List<String>> cache = new HashMap<>(2);
        cache.put(KEY_MENU, new ArrayList<>());
        cache.put(KEY_TRANS, new ArrayList<>());
        cache.put(KEY_TRANS_EXT, new ArrayList<>());
        String confPath = FileUtils.getFilePath(SQL_CHECK_TYPE_EXTEND.REPAIR_OLD_MENU.getPathConf());
        List<String> content = FileUtils.readNormalFile(confPath);
        if (CollectionUtils.isNotEmpty(content)) {
            for (String item : content) {
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                String[] element = CommonUtils.trimStrToSpace(item).split(STR_SPACE);
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
                                                    List<String> transAndSubTrans, StringBuilder subTransAndSubTrans, String subThirdInfo,
                                                    List<String> allMenu, List<String> skipTrans) {
        boolean flag = true;
        String subThirdTransCode = ScriptSqlUtils.getTransCodeByMenu(subThirdInfo);
        String menuCode = ScriptSqlUtils.getMenuCode(subThirdInfo);
        String trans = menuTrans.get(subThirdTransCode);
        if (trans != null) {
            String menu = addDeleteFundMenu(subThirdInfo, "trans");
            if (StringUtils.isNotBlank(menu)) {
                subTransAndSubTrans.append(menu);
            }
            if (menuCode.contains(subThirdTransCode)) {
                subTransAndSubTrans.append(formatSql(trans, false, true));
            }
        }
        List<String> subTrans = menuSubTrans.get(subThirdTransCode);
        if (CollectionUtils.isNotEmpty(subTrans)) {
            for (int k=0; k<subTrans.size(); k++) {
                String subTransElement = subTrans.get(k);
                String subTransKey = ScriptSqlUtils.getSubTransCodeBySubTrans(subTransElement);
                if (!menuCode.contains(subThirdTransCode)) {
                    if (subTransKey.contains(menuCode)) {
                        subTransAndSubTrans.append(formatSql(subTransElement, false, true));
                    }
                } else {
                    int lastCapitalIndex = CommonUtils.findLastCapital(subTransKey);
                    if (lastCapitalIndex != -1) {
                        subTransKey = subTransKey.substring(0, lastCapitalIndex);
                    }
                    if (!menuCode.contains(subTransKey) && allMenu.contains(subTransKey) && !skipTrans.contains(subTransKey)) {
                        continue;
                    }
                    subTransAndSubTrans.append(formatSql(subTransElement, false, true));
                }
            }
        } else {
            flag = false;
        }
        if (StringUtils.isNotBlank(subTransAndSubTrans.toString())) {
            transAndSubTrans.add(subTransAndSubTrans.toString());
        }
        return flag;
    }

    private static boolean getSubTransCodeExt(Map<String, List<String>> menuSubTransExt, List<String> subTransExt, StringBuilder subTransExtTmp, String subThirdInfo,
                                              List<String> allMenu, List<String> skipTransExt) {
        boolean flag = true;
        String subThirdTransCode = ScriptSqlUtils.getTransCodeByMenu(subThirdInfo);
        String menuCode = ScriptSqlUtils.getMenuCode(subThirdInfo);
        String subThirdMenuName = ScriptSqlUtils.getMenuName(subThirdInfo);
        List<String> subTrans = menuSubTransExt.get(subThirdTransCode);
        if (CollectionUtils.isNotEmpty(subTrans)) {
            for (int k=0; k<subTrans.size(); k++) {
                String subTransElement = subTrans.get(k);
                String subTransKey = ScriptSqlUtils.getSubTransCodeBySubTrans(subTransElement);
                if (!menuCode.contains(subThirdTransCode)) {
                    if (subTransKey.contains(menuCode)) {
                        subTransExtTmp.append(formatSql(subTransElement, false, true));
                    }
                } else {
                    int lastCapitalIndex = CommonUtils.findLastCapital(subTransKey);
                    if (lastCapitalIndex != -1) {
                        subTransKey = subTransKey.substring(0, lastCapitalIndex);
                    }
                    if (!menuCode.contains(subTransKey) && allMenu.contains(subTransKey) && !skipTransExt.contains(subTransKey)) {
                        continue;
                    }
                    subTransExtTmp.append(formatSql(subTransElement, false, true));
                }
            }
        } else {
            flag = false;
        }
        if (StringUtils.isNotBlank(subTransExtTmp.toString())) {
            subTransExt.add("-- " + subThirdMenuName + " 开始");
            String menu = addDeleteFundMenu(subThirdInfo, "transExt");
            if (StringUtils.isNotBlank(menu)) {
                subTransExt.add(menu);
            }
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
        menu.put("bizAidManager", getAccountParentMenuName("bizAidManager", otherMenu));
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
        File fileExt = new File(appConfigDto.getSystemToolCheckMenuFundExtPath() + ScriptSqlUtils.basePathExt);
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
            List<String> content = FileUtils.readNormalFile(file.getPath());
            StringBuilder menu = new StringBuilder();
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i).trim();
                String itemLower = item.toLowerCase();
                if (StringUtils.isBlank(item) || item.startsWith(ANNOTATION_NORMAL)) {
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
                String itemLower = CommonUtils.trimStrToSpace(item.trim().toLowerCase());
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
        return formatSql(sql, last, true);
    }

    private static String formatSql(String sql, boolean last, boolean annotation) {
        String endStr = STR_SEMICOLON;
        if (sql.trim().endsWith(endStr)) {
            endStr = STR_BLANK;
        }
        int index = sql.indexOf(")");
        if (index != -1) {
            String insert = sql.substring(0, index + 1).trim();
            String value = sql.substring(index + 1).trim();
            if (annotation) {
                if (insert.startsWith("--") && !value.startsWith("--")) {
                    value = "-- " + value;
                }
            } else {
                insert = insert.replaceFirst(ANNOTATION_NORMAL, STR_BLANK).trim();
                value = value.replaceFirst(ANNOTATION_NORMAL, STR_BLANK).trim();
            }
            sql = insert + STR_NEXT_LINE + value + endStr;
        }
        if (!last) {
            sql += STR_NEXT_LINE;
        }
        return sql;
    }

    public static String formatSqlAddDelete(String sql, boolean last) {
        String res = formatSql(sql, last);
        String delete = STR_BLANK;
        String menuCode = ScriptSqlUtils.getMenuCode(sql);
        if (sql.toLowerCase().contains("tsys_menu_std")) {
            delete = "delete from tsys_menu_std where menu_code = '" + menuCode + "';" + STR_NEXT_LINE;
        } else if (sql.toLowerCase().contains("tsys_menu")) {
            delete = "delete from tsys_menu where menu_code = '" + menuCode + "';" + STR_NEXT_LINE;
        } else if (sql.toLowerCase().contains("tsys_trans")) {
            delete = "delete from tsys_trans where trans_code = '" + menuCode + "';" + STR_NEXT_LINE;
        } else if (sql.toLowerCase().contains("tsys_subtrans_ext")) {
            String subTransCode = ScriptSqlUtils.getSubTransCodeBySubTrans(sql);
            delete = "delete from tsys_subtrans_ext where trans_code = '" + menuCode + "' and sub_trans_code = '" + subTransCode + "';" + STR_NEXT_LINE;
        } else if (sql.toLowerCase().contains("tsys_subtrans")) {
            String subTransCode = ScriptSqlUtils.getSubTransCodeBySubTrans(sql);
            delete = "delete from tsys_subtrans where trans_code = '" + menuCode + "' and sub_trans_code = '" + subTransCode + "';" + STR_NEXT_LINE;
        }
        if (res.startsWith(ANNOTATION_NORMAL)) {
            delete = ANNOTATION_NORMAL + STR_SPACE + delete;
        }
        return delete + res;
    }

    private static int getRepairTotal(List<String> logList) {
        int total = 0;
        for (int i = 3; i < logList.size(); i++) {
            String item = logList.get(i).replace(ANNOTATION_NORMAL, STR_BLANK).trim();
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (item.contains(STR_HYPHEN_1)) {
                total++;
            }
        }
        return total;
    }

    private static List<String> getPartMenuInfo(Map<String, String> menu, String menuCode, boolean fundMenu) {
        List<String> res = new ArrayList<>();
        Iterator<String> iterator = menu.keySet().iterator();
        while (iterator.hasNext()) {
            String secondMenuCode = iterator.next();
            String secondMenuInfo = menu.get(secondMenuCode);
            String secondParentMenuCode = ScriptSqlUtils.getParentCode(secondMenuInfo);
            if (fundMenu) {
                String remark = ScriptSqlUtils.getMenuRemark(secondMenuInfo);
                if (!isNeedMenu(secondMenuCode, remark)) {
                    continue;
                }
                if (excludeFundMenu.contains(secondMenuCode)) {
                    continue;
                }
            }
            if (secondParentMenuCode.equals(menuCode)) {
                res.add(secondMenuInfo);
                iterator.remove();
            }
        }
        res = menuSortByNewUed(res);
        return res;
    }

    private static List<String> getRealPartMenuInfo(List<String> menuList) {
        List<String> res = new ArrayList<>();
        for (String menu : menuList) {
            String transCode = ScriptSqlUtils.getTransCodeByMenu(menu);
            String remark = ScriptSqlUtils.getMenuRemark(menu);
            if ("menu".equals(transCode)) {
                continue;
            }
            if (!isNeedMenu(transCode, remark)) {
                continue;
            }
            res.add(menu);
        }
        menuSortByNewUed(res);
        return res;
    }

    private static boolean isNeedMenu(String menuCode, String remark) {
        if (menuCode.startsWith("fund") || specialFundMenu.contains(menuCode)) {
            return true;
        }
        if (includePubMenu.contains(menuCode)) {
            return true;
        }
        return false;
    }

    private static List<String> menuSortByNewUed(List<String> menuList) {
        List<String> res = new ArrayList<>();
        List<String> first = new ArrayList<>();
        List<String> second = new ArrayList<>();
        for (String menu : menuList) {
            String reserve = ScriptSqlUtils.getMenuReserve(menu).trim();
            if (STR_0.equals(reserve)) {
                second.add(menu);
            } else {
                first.add(menu);
            }
        }
        menuSort(first);
        for (String menu : first) {
            res.add(menu);
            String reserve = ScriptSqlUtils.getMenuReserve(menu).trim();
            if (!STR_0.equals(reserve)) {
                List<String> subMenu = new ArrayList<>();
                String[] subReserve = reserve.split(STR_COMMA);
                for (int i=0; i<subReserve.length; i++) {
                    String ele = subReserve[i];
                    for (String secondMenu : second) {
                        if (ScriptSqlUtils.getMenuCode(secondMenu).equals(ele)) {
                            subMenu.add(secondMenu);
                        }
                    }
                }
                menuSort(subMenu);
                res.addAll(subMenu);
            }
        }
        return res;
    }

    private static void generateMenuInfo(int index, String menu, List<String> subFourthMenu, Map<String, String> newUedMenu,
                                                      Map<String, String> trans, List<String> partMenu, MenuTransitionDto menuTransitionDto) {
        boolean hasMerger = menuTransitionDto.getHasMerger();
        String mergerMenuName = menuTransitionDto.getMergerMenuName();
        String fourthMenuCode = ScriptSqlUtils.getMenuCode(menu);
        String fourthMenuName = ScriptSqlUtils.getMenuName(menu);
        String reserve = ScriptSqlUtils.getMenuReserve(menu).trim();
        if (StringUtils.isNotBlank(menu) && !STR_0.equals(reserve) && hasMerger) {
            String lastEle = partMenu.get(partMenu.size() - 1);
            if (lastEle.endsWith(STR_NEXT_LINE)) {
                partMenu.set(partMenu.size() - 1, lastEle.substring(0, lastEle.length() - 1));
            }
            partMenu.add("-- " + mergerMenuName + " 结束" + STR_NEXT_LINE);
            hasMerger = false;
        }
        if (StringUtils.isNotBlank(reserve) && reserve.contains(STR_COMMA)) {
            mergerMenuName = fourthMenuName;
            partMenu.add("-- " + mergerMenuName + " 开始");
            hasMerger = true;
        }
        if (trans.containsKey(fourthMenuCode)) {
            partMenu.add(formatSqlAddDelete(menu, true));
        } else {
            partMenu.add(formatSqlAddDelete(menu, index == subFourthMenu.size() - 1));
        }
        if (trans.containsKey(fourthMenuCode)) {
            partMenu.add(formatSqlAddDelete(trans.get(fourthMenuCode), false));
            trans.remove(fourthMenuCode);
        }
        newUedMenu.remove(fourthMenuCode);
        menuTransitionDto.setHasMerger(hasMerger);
        menuTransitionDto.setMergerMenuName(mergerMenuName);
    }
}
