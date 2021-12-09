package com.hoomoomoo.im.utils;

import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.dto.LicenseDto;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.*;

/**
 * @author humm23693
 * @description 工具类
 * @package com.hoomoomoo.im.utils
 * @date 2021/04/23
 */
public class CommonUtils {

    private final static String PATTERN1 = "yyyy-MM-dd HH:mm:ss";
    private final static String PATTERN2 = "yyyyMMddHHmmss";
    private final static String PATTERN3 = "yyyyMMdd";
    private final static String PATTERN4 = "yyyy-MM-dd";
    private final static String PATTERN5 = "HH:mm:ss";
    private final static String PATTERN6 = "yyyy/MM/dd";

    private static Pattern LINE_PATTERN = Pattern.compile("_(\\w)");

    /**
     * 获取当前系统时间
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime1() {
        return new SimpleDateFormat(PATTERN1).format(new Date());
    }

    /**
     * 获取指定时间
     *
     * @param date
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime1(Date date) {
        return new SimpleDateFormat(PATTERN1).format(date);
    }

    /**
     * 获取当前系统时间
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime2() {
        return new SimpleDateFormat(PATTERN2).format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime3() {
        return new SimpleDateFormat(PATTERN3).format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime4() {
        return new SimpleDateFormat(PATTERN4).format(new Date());
    }

    /**
     * 格式化日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime5(String date) {
        if (date.length() != 8) {
            return date;
        }
        return date.substring(0, 4) + SYMBOL_HYPHEN + date.substring(4, 6) + SYMBOL_HYPHEN + date.substring(6);
    }

    /**
     * 获取当前日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime6() {
        return new SimpleDateFormat(PATTERN6).format(new Date());
    }

    /**
     * 格式化日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static Date getCurrentDateTime6(String time) throws ParseException {
        return new SimpleDateFormat(PATTERN1).parse(new SimpleDateFormat(PATTERN4).format(new Date()) + " " + time);
    }

    /**
     * 格式化日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static Date getCurrentDateTime7(String date) throws ParseException {
        return new SimpleDateFormat(PATTERN1).parse(date);
    }

    /**
     * 获取指定时间
     *
     * @param date
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime8(Date date) {
        return new SimpleDateFormat(PATTERN5).format(date);
    }

    /**
     * 获取指定时间
     *
     * @param date
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime9(Date date) {
        return new SimpleDateFormat(PATTERN3).format(date);
    }

    public static boolean checkConfig(TextArea log, String functionCode) throws Exception {
        boolean flag = true;
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
        return flag;
    }

    public static boolean checkConfig(TableView<?> log, String functionType) throws Exception {
        boolean flag = true;
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

    public static boolean checkConfigGenerateCode(TableView<?> log, GenerateCodeDto generateCodeDto) throws Exception {
        boolean flag = true;
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
        StringBuilder columnTip = new StringBuilder();
        columnTip.append("{").append(SYMBOL_NEXT_LINE);
        columnTip.append("    字段代码: {").append(SYMBOL_NEXT_LINE);
        columnTip.append("        name: '字段名称',").append(SYMBOL_NEXT_LINE);
        columnTip.append("        dict: '字典',").append(SYMBOL_NEXT_LINE);
        columnTip.append("        multi: '是否多选 0:单选 1:多选',").append(SYMBOL_NEXT_LINE);
        columnTip.append("        required: '是否必填 0:非必填 1:必填'").append(SYMBOL_NEXT_LINE);
        columnTip.append("        date: '是否日期 0:非日期 1:日期'").append(SYMBOL_NEXT_LINE);
        columnTip.append("        precision: '精度 不指定则取表结构'").append(SYMBOL_NEXT_LINE);
        columnTip.append("    }").append(SYMBOL_NEXT_LINE);
        columnTip.append("}").append(SYMBOL_NEXT_LINE);
        if (StringUtils.isBlank(generateCodeDto.getColumn())) {
            OutputUtils.info(log, "请设置[ 字段信息 ] ");
            OutputUtils.info(log, columnTip.toString());
            flag = false;
        } else {
            String columnInfo = generateCodeDto.getColumn();
            try {
                JSONObject.parseObject(columnInfo, Map.class);
            } catch (Exception e) {
                OutputUtils.info(log, "[ 字段信息 ]格式错误");
                OutputUtils.info(log, columnInfo);
                flag = false;
            }
        }
        return flag;
    }

    public static boolean checkLicense(String functionCode) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        LicenseDto licenseDto = appConfigDto.getLicense();
        if (Integer.valueOf(CommonUtils.getCurrentDateTime3()) > Integer.valueOf(licenseDto.getEffectiveDate())) {
            LoggerUtils.info(String.format(MSG_LICENSE_EXPIRE, licenseDto.getEffectiveDate()));
            return false;
        }
        if (StringUtils.isBlank(functionCode)) {
            return true;
        }
        if (!checkUse(appConfigDto, STR_1, functionCode)) {
            LoggerUtils.info(String.format(MSG_LICENSE_NOT_USE, FunctionConfig.getName(functionCode)));
            return false;
        }
        if (checkAuth(STR_1, functionCode)) {
            return true;
        }
        List<FunctionDto> functionDtoList = licenseDto.getFunction();
        if (CollectionUtils.isEmpty(functionDtoList)) {
            LoggerUtils.info(String.format(MSG_LICENSE_NOT_AUTH, FunctionConfig.getName(functionCode)));
            return false;
        }
        for (FunctionDto functionDto : functionDtoList) {
            if (functionCode.equals(functionDto.getFunctionCode())) {
                return true;
            }
        }
        LoggerUtils.info(String.format(MSG_LICENSE_NOT_AUTH, FunctionConfig.getName(functionCode)));
        return false;
    }

    public static List<FunctionDto> getAuthFunction() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        LicenseDto licenseDto = appConfigDto.getLicense();
        if (Integer.valueOf(CommonUtils.getCurrentDateTime3()) > Integer.valueOf(licenseDto.getEffectiveDate())) {
            return new ArrayList<>();
        }
        return licenseDto.getFunction();
    }

    public static void showAuthFunction(Menu menu) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        List<FunctionDto> functionDtoList = CommonUtils.getAuthFunction();
        if (CollectionUtils.isEmpty(functionDtoList)) {
            menu.getItems().clear();
            return;
        }
        ObservableList<MenuItem> list = menu.getItems();
        ListIterator<MenuItem> iterator = list.listIterator();
        outer:
        while (iterator.hasNext()) {
            MenuItem item = iterator.next();
            for (FunctionDto functionDto : functionDtoList) {
                if (!checkUse(appConfigDto, STR_2, item.getId())) {
                    LoggerUtils.info(String.format(MSG_LICENSE_NOT_USE, FunctionConfig.getNameBymenuId(item.getId())));
                    iterator.remove();
                    continue outer;
                }
                if (checkAuth(STR_2, item.getId())) {
                    continue outer;
                }
                if (FunctionConfig.getMenuId(functionDto.getFunctionCode()).equals(item.getId())) {
                    continue outer;
                }
            }
            iterator.remove();
        }
    }

    public static boolean checkAuth(String checkType, String functionCode) {
        if (STR_1.equals(checkType)) {
            return ABOUT_INFO.getCode().equals(functionCode) || CONFIG_SET.getCode().equals(functionCode) || FUNCTION_STAT_INFO.getCode().equals(functionCode);
        } else if (STR_2.equals(checkType)) {
            return ABOUT_INFO.getMenuId().equals(functionCode) || CONFIG_SET.getMenuId().equals(functionCode) || FUNCTION_STAT_INFO.getMenuId().equals(functionCode);
        }
        return false;
    }

    public static boolean checkUse(AppConfigDto appConfigDto, String checkType, String functionCode) {
        String appUser = appConfigDto.getAppUser();
        if (STR_1.equals(checkType)) {
            if (SVN_REALTIME_STAT.getCode().equals(functionCode) || SVN_HISTORY_STAT.getCode().equals(functionCode)) {
                return APP_USER_IM.equals(appUser);
            }
        } else if (STR_2.equals(checkType)) {
            if (SVN_REALTIME_STAT.getMenuId().equals(functionCode) || SVN_HISTORY_STAT.getMenuId().equals(functionCode)) {
                return APP_USER_IM.equals(appUser);
            }
        }
        return true;
    }

    public static String initialUpper(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String initialLower(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
