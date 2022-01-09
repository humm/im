package im.util;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author hoomoomoo
 * @description 时间工具类
 * @package com.hoomoomoo.im.util
 * @date 2019/08/08
 */

public class SysDateUtils {

    private static final String YYYYMMDD                            = "yyyyMMdd";
    private static final String YYYY                                = "yyyy";
    private static final String HHMMSS                              = "HHmmss";
    private static final String YYYYMMDDHHMMSS                      = "yyyyMMddHHmmss";

    /**
     * 年月日
     *
     * @return
     */
    public static String yyyyMMdd(){
        return new SimpleDateFormat(YYYYMMDD).format(new Date());
    }

    /**
     * 年
     *
     * @return
     */
    public static String yyyy(){
        return new SimpleDateFormat(YYYY).format(new Date());
    }

    /**
     * 时分秒
     *
     * @return
     */
    public static String HHmmss(){
        return new SimpleDateFormat(HHMMSS).format(new Date());
    }

    /**
     * 年月日时分秒
     *
     * @return
     */
    public static String yyyyMMddHHmmss(){
        return new SimpleDateFormat(YYYYMMDDHHMMSS).format(new Date());
    }
}
