package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.ERROR_LOG;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.LACK_LOG;

public class ScriptRepairSql {

    private static String CONSOLE_FUND_TA_VUE_MENU_LINE_INDEX = "-- * * * * * * * * * * * * * * * * * * *";
    private static String EXT_LINE_INDEX = "commit";

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
            lineIndex = CONSOLE_FUND_TA_VUE_MENU_LINE_INDEX;
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
                if (lineIndex.toLowerCase().contains(EXT_LINE_INDEX) || CONSOLE_FUND_TA_VUE_MENU_LINE_INDEX.equals(lineIndex)) {
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
