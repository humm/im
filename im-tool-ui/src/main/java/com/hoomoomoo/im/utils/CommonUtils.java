package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.LicenseDto;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

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
            if (StringUtils.isBlank(appConfigDto.getSvnUrl())) {
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
            if (StringUtils.isBlank(appConfigDto.getSvnUrl())) {
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
        if (ABOUT_INFO.getCode().equals(functionCode) || FUNCTION_STAT_INFO.getCode().equals(functionCode)) {
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
            if (!appConfigDto.getSvnStat()) {
                if (SVN_REALTIME_STAT.getMenuId().equals(item.getId()) || SVN_HISTORY_STAT.getMenuId().equals(item.getId())) {
                    iterator.remove();
                }
            }
            for (FunctionDto functionDto : functionDtoList) {
                if (ABOUT_INFO.getMenuId().equals(item.getId()) || FUNCTION_STAT_INFO.getMenuId().equals(item.getId())) {
                    continue outer;
                }
                if (FunctionConfig.getMenuId(functionDto.getFunctionCode()).equals(item.getId())) {
                    continue outer;
                }
            }
            iterator.remove();
        }
    }
}
