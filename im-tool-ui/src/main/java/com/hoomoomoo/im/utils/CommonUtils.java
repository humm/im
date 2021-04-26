package com.hoomoomoo.im.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author humm23693
 * @description 工具类
 * @package com.hoomoomoo.im.utils
 * @date 2021/04/23
 */
public class CommonUtils {

    private final static String PATTERN1 = "yyyy-MM-dd HH:mm:ss";
    private final static String PATTERN2 = "yyyyMMddHHmmss";

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
}