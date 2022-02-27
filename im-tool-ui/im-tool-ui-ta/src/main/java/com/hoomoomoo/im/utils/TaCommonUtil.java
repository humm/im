package com.hoomoomoo.im.utils;

import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SYMBOL_NEXT_LINE;
import static com.hoomoomoo.im.consts.FunctionConfig.SVN_HISTORY_STAT;
import static com.hoomoomoo.im.consts.FunctionConfig.SVN_REALTIME_STAT;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022/1/9
 */
public class TaCommonUtil {

    public static boolean checkConfig(TextArea log, String functionCode) throws Exception {
        boolean flag = true;
        OutputUtils.clearLog(log);
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        if (functionCode.equals(FunctionConfig.SVN_LOG.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(log, MSG_SVN_USERNAME + SYMBOL_NEXT_LINE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getSvnPassword())) {
                OutputUtils.info(log, MSG_SVN_PASSWORD + SYMBOL_NEXT_LINE);
                flag = false;
            }
            if (MapUtils.isEmpty(appConfigDto.getSvnUrl())) {
                OutputUtils.info(log, MSG_SVN_URL + SYMBOL_NEXT_LINE);
                flag = false;
            }
        }
        if (functionCode.equals(FunctionConfig.SVN_UPDATE.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(log, MSG_SVN_USERNAME + SYMBOL_NEXT_LINE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getSvnPassword())) {
                OutputUtils.info(log, MSG_SVN_PASSWORD + SYMBOL_NEXT_LINE);
                flag = false;
            }
            if (CollectionUtils.isEmpty(appConfigDto.getSvnUpdatePath())) {
                OutputUtils.info(log, MSG_SVN_UPDATE_TA6 + SYMBOL_NEXT_LINE);
                flag = false;
            }
        }
        if (functionCode.equals(FunctionConfig.SCRIPT_UPDATE.getCode())) {
            if (appConfigDto.getScriptUpdateGenerateFile()) {
                if (StringUtils.isBlank(appConfigDto.getScriptUpdateGeneratePath())) {
                    OutputUtils.info(log, MSG_SCRIPT_UPDATE_GENERATE_PATH + SYMBOL_NEXT_LINE);
                    flag = false;
                }
            }
        }
        if (functionCode.equals(SVN_REALTIME_STAT.getCode()) || functionCode.equals(SVN_HISTORY_STAT.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(log, MSG_SVN_USERNAME + SYMBOL_NEXT_LINE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getSvnPassword())) {
                OutputUtils.info(log, MSG_SVN_PASSWORD + SYMBOL_NEXT_LINE);
                flag = false;
            }
            if (MapUtils.isEmpty(appConfigDto.getSvnUrl())) {
                OutputUtils.info(log, MSG_SVN_URL + SYMBOL_NEXT_LINE);
                flag = false;
            }
            if (appConfigDto.getSvnStatUser().size() == 0) {
                OutputUtils.info(log, MSG_SVN_STAT_USER + SYMBOL_NEXT_LINE);
                flag = false;
            }
            if (appConfigDto.getSvnStatInterval() < 5) {
                OutputUtils.info(log, MSG_SVN_STAT_INTERVAL + SYMBOL_NEXT_LINE);
                flag = false;
            }
        }

        if (functionCode.equals(FunctionConfig.GENERATE_SQL.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getGenerateSqlDatabaseNum())) {
                OutputUtils.info(log, String.format(MSG_SET, "generate.sql.database.num"));
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getGenerateSqlTableNum())) {
                OutputUtils.info(log, String.format(MSG_SET, "generate.sql.table.num"));
                flag = false;
            }
        }
        return flag;
    }

    public static boolean checkConfig(TableView log, String functionType) throws Exception {
        boolean flag = true;
        OutputUtils.clearLog(log);
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        if (functionType.equals(FunctionConfig.FUND_INFO.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getFundGeneratePath())) {
                OutputUtils.info(log, MSG_FUND_GENERATE_PATH);
                flag = false;
            }
        }
        if (functionType.equals(FunctionConfig.PROCESS_INFO.getCode())) {
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

    public static boolean checkConfigGenerateCode(TableView log, GenerateCodeDto generateCodeDto) throws Exception {
        boolean flag = true;
        OutputUtils.clearLog(log);
        if (StringUtils.isBlank(generateCodeDto.getJavaPath())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_PATH, "java文件路径"));
            flag = false;
        }
        if (StringUtils.isBlank(generateCodeDto.getVuePath())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_PATH, "vue文件路径"));
            flag = false;
        }
        if (StringUtils.isBlank(generateCodeDto.getSqlPath())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_PATH, "sql文件路径"));
            flag = false;
        }
        if (StringUtils.isBlank(generateCodeDto.getRoutePath())) {
            OutputUtils.info(log, String.format(MSG_GENERATE_CODE_PATH, "route文件路径"));
            flag = false;
        }
        if (StringUtils.isBlank(generateCodeDto.getPageType())) {
            OutputUtils.info(log, "请选择[ 页面类型 ] ");
            flag = false;
        }
        if (StringUtils.isBlank(generateCodeDto.getAuthor())) {
            OutputUtils.info(log, "请设置[ 作者 ] ");
            flag = false;
        }
        if (StringUtils.isBlank(generateCodeDto.getDbType())) {
            OutputUtils.info(log, "请选择[ 数据源 ] ");
            flag = false;
        }
        if (StringUtils.isBlank(generateCodeDto.getDtoCode())) {
            OutputUtils.info(log, "请设置[ dto代码 ] ");
            flag = false;
        }
        if (StringUtils.isBlank(generateCodeDto.getMenuCode())) {
            OutputUtils.info(log, "请设置[ 菜单代码 ] ");
            OutputUtils.info(log, "格式[ 一级菜单.二级菜单.三级菜单 ]");
            flag = false;
        } else {
            if (generateCodeDto.getMenuCode().split(SYMBOL_POINT_SLASH).length != 3) {
                OutputUtils.info(log, "[ 菜单代码 ]格式错误");
                OutputUtils.info(log, "格式[ 一级菜单.二级菜单.三级菜单 ]");
                flag = false;
            }
        }
        if (StringUtils.isBlank(generateCodeDto.getMenuName())) {
            OutputUtils.info(log, "请设置[ 菜单名称 ] ");
            OutputUtils.info(log, "格式[ 一级菜单.二级菜单.三级菜单 ]");
            flag = false;
        } else {
            if (generateCodeDto.getMenuName().split(SYMBOL_POINT_SLASH).length != 3) {
                OutputUtils.info(log, "[ 菜单名称 ]格式错误");
                OutputUtils.info(log, "格式[ 一级菜单.二级菜单.三级菜单 ]");
                flag = false;
            }
        }
        if (StringUtils.isBlank(generateCodeDto.getTable())) {
            OutputUtils.info(log, "请设置[ 正式表结构 ] ");
            OutputUtils.info(log, "格式[ oracle建表语句 ] ");
            flag = false;
        }
        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType()) && StringUtils.isBlank(generateCodeDto.getAsyTable())) {
            OutputUtils.info(log, "请设置[ 复核表结构 ] ");
            OutputUtils.info(log, "格式[ oracle建表语句 ] ");
            flag = false;
        }
        if (CollectionUtils.isEmpty(generateCodeDto.getColumn())) {
            OutputUtils.info(log, "请设置[ 配置字段信息 ] ");
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
}
