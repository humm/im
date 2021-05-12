package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionType;
import com.hoomoomoo.im.dto.AppConfigDto;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.hoomoomoo.im.consts.BaseConst.*;

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


    public static Boolean checkConfig(TextArea log, String functionType) throws Exception {
        boolean flag = true;
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        if (functionType.equals(FunctionType.SVN_LOG.getType())) {
            if (StringUtils.isBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(log, STR_MSG_SVN_USERNAME + STR_NEXT_LINE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getSvnPassword())) {
                OutputUtils.info(log, STR_MSG_SVN_PASSWORD + STR_NEXT_LINE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getSvnUrl())) {
                OutputUtils.info(log, STR_MSG_SVN_URL + STR_NEXT_LINE);
                flag = false;
            }
        }
        if (functionType.equals(FunctionType.SVN_UPDATE.getType())) {
            if (StringUtils.isBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(log, STR_MSG_SVN_USERNAME + STR_NEXT_LINE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getSvnPassword())) {
                OutputUtils.info(log, STR_MSG_SVN_PASSWORD + STR_NEXT_LINE);
                flag = false;
            }
            if (CollectionUtils.isEmpty(appConfigDto.getSvnUpdatePath())) {
                OutputUtils.info(log, STR_MSG_SVN_UPDATE_TA6 + STR_NEXT_LINE);
                flag = false;
            }
        }
        return flag;
    }

    public static Boolean checkConfig(TableView<?> log, String functionType) throws Exception {
        boolean flag = true;
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        if (functionType.equals(FunctionType.FUND_INFO.getType())) {
            if (StringUtils.isBlank(appConfigDto.getFundGeneratePath())) {
                OutputUtils.info(log, STR_MSG_FUND_GENERATE_PATH);
                flag = false;
            }
        }
        if (functionType.equals(FunctionType.PROCESS_INFO.getType())) {
            if (StringUtils.isBlank(appConfigDto.getProcessGeneratePathSchedule())) {
                OutputUtils.info(log, STR_MSG_PROCESS_GENERATE_PATH_SCHEDULE);
                flag = false;
            }
            if (StringUtils.isBlank(appConfigDto.getProcessGeneratePathTrans())) {
                OutputUtils.info(log, STR_MSG_PROCESS_GENERATE_PATH_TRANS);
                flag = false;
            }
        }
        return flag;
    }

}
