package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class ScriptRepairSql {

    private static String CONSOLE_FUND_TA_VUE_MENU_LINE_INDEX = "-- * * * * * * * * * * * * * * * * * * *";
    private static String EXT_LINE_INDEX = "commit";

    public static void addLackLog() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String resultPath = appConfigDto.getSystemToolCheckMenuResultPath();
        List<String> logList = FileUtils.readNormalFile(resultPath + "\\" + "60.缺少日志.sql", false);
        if (CollectionUtils.isEmpty(logList) || logList.size() <= 2) {
            return;
        }
        String transCode = STR_BLANK;
        String subTransCode = STR_BLANK;
        for (int i=2; i<logList.size(); i++) {
            String item = logList.get(i).replace(ANNOTATION_TYPE_NORMAL, STR_BLANK).trim();
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (item.contains(STR_HYPHEN_1)) {
                // 日志类型
                String[] trans = item.split(STR_HYPHEN_1);
                if (trans.length == 2) {
                    transCode = trans[0];
                    subTransCode = trans[1].trim().split(STR_SPACE)[0];
                    // 全量脚本
                    addSubTransExt(transCode, subTransCode,  appConfigDto.getSystemToolCheckMenuBasePath() + "\\sql\\pub\\001initdata\\basedata\\07console-fund-ta-vue-menu.sql");
                } else {
                    transCode = STR_BLANK;
                    subTransCode = STR_BLANK;
                }
            } else {
                // 实际路径
                addSubTransExt(transCode, subTransCode, item);
            }
        }
    }

    private static void addSubTransExt(String transCode, String subTransCode, String path) throws IOException {
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
                    String transCodeLine = ScriptUtils.getTransCode(valuePart);
                    String subTransCodeLine = ScriptUtils.getSubTransCode(valuePart);
                    if (transCode.equals(transCodeLine)) {
                        lineIndex = valuePart;
                    }
                    if (transCode.equals(transCodeLine) && subTransCode.equals(subTransCodeLine)) {
                        return;
                    }
                }
            }
        }
        for (int i=0; i<item.size(); i++) {
            String ele = item.get(i);
            if (ele.contains(lineIndex)) {
                String extInfo = buildSubTransExt(transCode, subTransCode);
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

    private static String buildSubTransExt(String transCode, String subTransCode) {
        StringBuilder ext = new StringBuilder();
        String opDir = ScriptUtils.getSubTransCodeOpDir(subTransCode, STR_3);
        ext.append("delete from tsys_subtrans_ext where trans_code = '" + transCode + "' and sub_trans_code = '" + subTransCode + "';").append(STR_NEXT_LINE);
        ext.append("insert into tsys_subtrans_ext (trans_code, sub_trans_code, op_dir, remark, need_active, ta_status_ctrl, active_flag)").append(STR_NEXT_LINE);
        ext.append("values ('" + transCode + "', '" + subTransCode + "','" + opDir + "', ' ', '1', ' ', ' ');");
        return ext.toString();
    }
}
