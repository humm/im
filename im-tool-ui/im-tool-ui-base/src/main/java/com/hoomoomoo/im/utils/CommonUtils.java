package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.LicenseDto;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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
        if (!checkUser(appConfigDto, STR_1, functionCode)) {
            if (checkUser(appConfigDto.getAppUser(), APP_USER_IM)) {
                LoggerUtils.info(String.format(MSG_LICENSE_NOT_USE, FunctionConfig.getName(functionCode)));
            }
            return false;
        }
        if (checkAuth(STR_1, functionCode)) {
            return true;
        }
        List<FunctionDto> functionDtoList = licenseDto.getFunction();
        if (CollectionUtils.isEmpty(functionDtoList)) {
            if (checkUser(appConfigDto.getAppUser(), APP_USER_IM)) {
                LoggerUtils.info(String.format(MSG_LICENSE_NOT_AUTH, FunctionConfig.getName(functionCode)));
            }
            return false;
        }
        for (FunctionDto functionDto : functionDtoList) {
            if (functionCode.equals(functionDto.getFunctionCode())) {
                return true;
            }
        }
        if (checkUser(appConfigDto.getAppUser(), APP_USER_IM)) {
            LoggerUtils.info(String.format(MSG_LICENSE_NOT_AUTH, FunctionConfig.getName(functionCode)));
        }
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
                if (!checkUser(appConfigDto, STR_2, item.getId())) {
                    if (checkUser(appConfigDto.getAppUser(), APP_USER_IM)) {
                        LoggerUtils.info(String.format(MSG_LICENSE_NOT_USE, FunctionConfig.getNameBymenuId(item.getId())));
                    }
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
            return ABOUT_INFO.getCode().equals(functionCode) || CONFIG_SET.getCode().equals(functionCode)
                    || FUNCTION_STAT_INFO.getCode().equals(functionCode) || JD_COOKIE.getCode().equals(functionCode);
        } else if (STR_2.equals(checkType)) {
            return ABOUT_INFO.getMenuId().equals(functionCode) || CONFIG_SET.getMenuId().equals(functionCode)
                    || FUNCTION_STAT_INFO.getMenuId().equals(functionCode) || JD_COOKIE.getMenuId().equals(functionCode);
        }
        return false;
    }

    public static boolean checkUser(AppConfigDto appConfigDto, String checkType, String functionCode) {
        String appUser = appConfigDto.getAppUser();
        if (STR_1.equals(checkType)) {
            if (SVN_REALTIME_STAT.getCode().equals(functionCode) || SVN_HISTORY_STAT.getCode().equals(functionCode)) {
                return checkUser(appUser, APP_USER_IM_SVN);
            } else if (GENERATE_CODE.getCode().equals(functionCode)) {
                return checkUser(appUser, APP_USER_IM_CODE);
            }
        } else if (STR_2.equals(checkType)) {
            if (SVN_REALTIME_STAT.getMenuId().equals(functionCode) || SVN_HISTORY_STAT.getMenuId().equals(functionCode)) {
                return checkUser(appUser, APP_USER_IM_SVN);
            } else if (GENERATE_CODE.getMenuId().equals(functionCode)) {
                return checkUser(appUser, APP_USER_IM_CODE);
            }
        }
        return true;
    }

    public static boolean checkUser(String appUser, String userName) {
        if (StringUtils.isBlank(appUser)) {
            return false;
        }
        String[] user = appUser.split(SYMBOL_COMMA);
        for (String item : user) {
            if (userName.equals(item)) {
                return true;
            }
        }
        return false;
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

    public static List<FunctionConfig> getAppFunctionConfig(String appCode) {
        List<FunctionConfig> functionConfigList = new ArrayList<>();
        for (FunctionConfig functionConfig : FunctionConfig.values()) {
            int functionCode = Integer.valueOf(functionConfig.getCode());
            if (APP_CODE_TA.equals(appCode) && functionCode <= 400) {
                functionConfigList.add(functionConfig);
            } else if (APP_CODE_SHOPPING.equals(appCode) && functionCode > 400 && functionCode <= 900) {
                functionConfigList.add(functionConfig);
            }
            if (functionCode > 900) {
                functionConfigList.add(functionConfig);
            }
        }
        return functionConfigList;
    }

    public static boolean isNumber(String val) {
        if (StringUtils.isBlank(val)) {
            return true;
        }
        try {
            Integer.valueOf(val);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static void sortFunctionDtoList(List<FunctionDto> functionDtoList) {
        Collections.sort(functionDtoList, (o1, o2) -> {
            String submitTimes1 = StringUtils.isBlank(o1.getSubmitTimes()) ? STR_0 : o1.getSubmitTimes().trim();
            String submitTimes2 = StringUtils.isBlank(o2.getSubmitTimes()) ? STR_0 : o2.getSubmitTimes().trim();
            return Integer.valueOf(submitTimes2) - Integer.valueOf(submitTimes1);
        });
    }

    /**
     * 获取当前版本号
     *
     * @param
     * @author: humm23693
     * @date: 2021/9/24
     * @return: java.lang.String
     */
    public static String getVersion() {
        String version = SYMBOL_EMPTY;
        try {
            List<String> versionContent = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION), false);
            if (CollectionUtils.isNotEmpty(versionContent)) {
                for (String item : versionContent) {
                    if (item.startsWith(NAME_CURRENT_VERSION)) {
                        String[] itemVersion = item.split(SYMBOL_COLON);
                        if (itemVersion.length == 2) {
                            version = itemVersion[1].trim();
                        }
                    }
                }
            }
        } catch (IOException e) {
            LoggerUtils.info(e);
        }
        return version;
    }
}
