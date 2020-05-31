package com.hoomoomoo.im.consts;

import lombok.Data;

/**
 * @Author hoomoomoo
 * @Description 常量类
 * @package com.hoomoomoo.im.consts
 * @Date 2020/05/31
 */

public class BaseConst {

    /**
     * 应用配置文件路径
     */
    public static final String APPLICATION_PROPERTIES                           = "classpath:application.properties";

    /**
     * 定时任务配置读取前缀
     */
    public static final String SCHEDULE                                         = "family.schedule";

    /**
     * 应用级别参数读取前缀
     */
    public static final String CONFIG                                           = "family.config";

    /**
     * 数据库连接配置读取前缀
     */
    public static final String SPRING_DATASOURCE                                = "spring.datasource";
    /**
     * 提示信息：分页查询成功代码
     */
    public static final String PAGE_CODE_SUCCESS                                = "0";

    /**
     * underline
     */
    public static final String UNDERLINE                                        = "_";

    /**
     * 状态码：成功
     */
    public static final String STATUS_SUCCESS                                   = "0";

    /**
     * 状态码：失败
     */
    public static final String STATUS_FAIL                                      = "-1";


    /**
     * 系统错误
     * */
    public static final String ERROR_PATH                                       = "/error";

    /**
     * 404页面
     * */
    public static final String PAGE_ERROR_404                                   = "/error/404";
    /**
     * 错误页面
     * */
    public static final String PAGE_ERROR                                       = "/error/error";

    /**
     * text/html
     */
    public static final String TEXT_HTML                                        = "text/html";

    /**
     * 提示消息：message
     * */
    public static final String MESSAGE                                          = "message";

    /**
     * 字符串：404
     */
    public static final String STR_404                                          = "404";

    /**
     * 应用名称
     */
    public static final String APP_NAME                                         = "appName";

    /**
     * 应用描述
     */
    public static final String APP_DESCRIBE                                     = "appDescribe";

    /**
     * shutdown
     */
    public static final String SHUTDOWN                                         = "shutdown";

}
