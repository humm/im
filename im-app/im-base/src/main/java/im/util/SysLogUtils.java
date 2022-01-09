package im.util;


import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

import static im.config.RunDataConfig.LOG_REQUEST_PARAMETER;
import static im.config.RunDataConfig.LOG_REQUEST_TAG;

/**
 * @author hoomoomoo
 * @description 日志工具类
 * @package im.util
 * @date 2019/08/12
 */

public class SysLogUtils {

    private static final String LOG_FORMAT_TIP                                   = "%s %s %s %s";
    private static final String LOG_TYPE_CONTROLLER                              = "控制层";
    private static final String LOG_TYPE_SERVICE                                 = "服务层";
    private static final String LOG_OPERATE_TAG_START                            = "开始";
    private static final String LOG_OPERATE_TAG_END                              = "结束";
    private static final String LOG_OPERATE_TAG_EXCEPTION                        = "异常";
    private static final String LOG_OPERATE_TAG_LOAD                             = "加载";
    private static final String LOG_OPERATE_TAG_SUCCESS                          = "成功";
    private static final String LOG_OPERATE_TAG_FAIL                             = "失败";
    private static final String LOG_TIP                                          = "====================== %s %s ======================";
    private static final String LOG_FORMAT_STATUS_MSG                            = "%s %s：{}";
    private static final String LOG_FORMAT_STATUS                                = "%s %s";
    private static final String LOG_BUSINESS_PARAMETER                           = "服务请求入参：{}";
    private static final String LOG_FORMAT_ERROR_STATUS                          = "%s %s [%s]";


    /**
     * 控制层日志开始
     *
     * @param logger
     * @param businessType
     * @param messages
     */
    public static void controllerStart(Logger logger, String businessType, String messages) {
        if (LOG_REQUEST_TAG) {
            logger.info(String.format(LOG_FORMAT_TIP, businessType, LOG_TYPE_CONTROLLER, messages, LOG_OPERATE_TAG_START));
        }
    }

    /**
     * 控制层日志结束
     *
     * @param logger
     * @param businessType
     * @param messages
     */
    public static void controllerEnd(Logger logger, String businessType, String messages) {
        if (LOG_REQUEST_TAG) {
            logger.info(String.format(LOG_FORMAT_TIP, businessType, LOG_TYPE_CONTROLLER, messages, LOG_OPERATE_TAG_END));
        }
    }

    /**
     * 服务层日志开始
     *
     * @param logger
     * @param businessType
     * @param messages
     */
    public static void serviceStart(Logger logger, String businessType, String messages) {
        if (LOG_REQUEST_TAG) {
            logger.info(String.format(LOG_FORMAT_TIP, businessType, LOG_TYPE_SERVICE, messages, LOG_OPERATE_TAG_START));
        }
    }

    /**
     * 服务层日志结束
     *
     * @param logger
     * @param businessType
     * @param messages
     */
    public static void serviceEnd(Logger logger, String businessType, String messages) {
        if (LOG_REQUEST_TAG) {
            logger.info(String.format(LOG_FORMAT_TIP, businessType, LOG_TYPE_SERVICE, messages, LOG_OPERATE_TAG_END));
        }
    }

    /**
     * 业务成功日志
     *
     * @param logger
     * @param businessType
     * @param obj
     */
    public static void success(Logger logger, String businessType, Object obj) {
        logger.info(String.format(LOG_FORMAT_STATUS_MSG, businessType, LOG_OPERATE_TAG_SUCCESS), obj);
    }

    /**
     * 业务成功日志
     *
     * @param logger
     * @param businessType
     */
    public static void load(Logger logger, String businessType) {
        logger.info(String.format(LOG_FORMAT_STATUS, LOG_OPERATE_TAG_LOAD, businessType));
    }

    /**
     * 业务成功日志
     *
     * @param logger
     * @param businessType
     */
    public static void success(Logger logger, String businessType) {
        logger.info(String.format(LOG_FORMAT_STATUS, businessType, LOG_OPERATE_TAG_SUCCESS));
    }

    /**
     * 业务失败日志
     *
     * @param logger
     * @param businessType
     * @param obj
     */
    public static void fail(Logger logger, String businessType, Object obj) {
        logger.error(String.format(LOG_FORMAT_STATUS_MSG, businessType, LOG_OPERATE_TAG_FAIL), obj);
    }

    /**
     * 业务异常日志
     *
     * @param logger
     * @param businessType
     * @param e
     */
    public static void exception(Logger logger, String businessType, Exception e) {
        logger.error(String.format(LOG_FORMAT_STATUS, businessType, LOG_OPERATE_TAG_EXCEPTION), e);
    }

    /**
     * 业务异常日志
     *
     * @param logger
     */
    public static void exception(Logger logger, String errorInfo) {
        logger.error(errorInfo);
    }

    /**
     * 业务异常日志
     *
     * @param logger
     * @param businessType
     * @param e
     */
    public static void exception(Logger logger, String businessType, String e) {
        logger.error(String.format(LOG_FORMAT_ERROR_STATUS, businessType, LOG_OPERATE_TAG_EXCEPTION, e));
    }

    /**
     * 应用启动、参数类成功日志
     *
     * @param logger
     * @param businessType
     */
    public static void configSuccess(Logger logger, String businessType) {
        logger.info(String.format(LOG_TIP, businessType, LOG_OPERATE_TAG_SUCCESS));
    }

    /**
     * 应用启动、参数类开始日志
     *
     * @param logger
     * @param businessType
     */
    public static void configStart(Logger logger, String businessType) {
        logger.info(String.format(LOG_TIP, businessType, LOG_OPERATE_TAG_START));
    }

    /**
     * 应用启动、参数类结束日志
     *
     * @param logger
     * @param businessType
     */
    public static void configEnd(Logger logger, String businessType) {
        logger.info(String.format(LOG_TIP, businessType, LOG_OPERATE_TAG_END));
    }

    /**
     * 功能开始日志
     *
     * @param logger
     * @param businessType
     */
    public static void functionStart(Logger logger, String businessType) {
        if (LOG_REQUEST_TAG) {
            logger.info(String.format(LOG_FORMAT_STATUS, businessType, LOG_OPERATE_TAG_START));
        }
    }

    /**
     * 功能结束日志
     *
     * @param logger
     * @param businessType
     */
    public static void functionEnd(Logger logger, String businessType) {
        if (LOG_REQUEST_TAG) {
            logger.info(String.format(LOG_FORMAT_STATUS, businessType, LOG_OPERATE_TAG_END));
        }
    }

    /**
     * 请求入参日志
     *
     * @param logger
     * @param obj
     */
    public static void parameter(Logger logger, Object obj) {
        if (LOG_REQUEST_PARAMETER) {
            if (obj instanceof List) {
                logger.info(LOG_BUSINESS_PARAMETER, SysBeanUtils.beanToMap((List)obj));
            } else if (obj instanceof Map) {
                logger.info(LOG_BUSINESS_PARAMETER, obj);
            } else {
                logger.info(LOG_BUSINESS_PARAMETER, SysBeanUtils.beanToMap(obj));
            }
        }
    }

    /**
     * info日志
     *
     * @param logger
     * @param info
     */
    public static void info(Logger logger, Object info) {
        logger.info(info.toString());
    }

    /**
     * info日志
     *
     * @param logger
     * @param info
     */
    public static void error(Logger logger, Object info) {
        logger.error(info.toString());
    }


}
