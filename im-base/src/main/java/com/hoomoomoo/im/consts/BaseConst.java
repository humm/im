package com.hoomoomoo.im.consts;

/**
 * @author hoomoomoo
 * @description 业务级别常量
 * @package com.hoomoomoo.im.consts
 * @date 2019/08/03
 */

public class BaseConst {

    /**
     * 系统版本号
     */
    public static final String SYSTEM_VERSION                               = "3.4.00";

    /**
     * 应用配置文件路径
     */
    public static final String APPLICATION_PROPERTIES                       = "classpath:application.properties";

    /**
     * 定时任务配置读取前缀
     */
    public static final String SCHEDULE                                     = "im.schedule";

    /**
     * 应用级别参数读取前缀
     */
    public static final String CONFIG                                       = "im.config";

    /**
     * 数据库连接配置读取前缀
     */
    public static final String SPRING_DATASOURCE                            = "spring.datasource";
    /**
     * 提示信息：分页查询成功代码
     */
    public static final String PAGE_CODE_SUCCESS                            = "0";

    /**
     * underline
     */
    public static final String UNDERLINE                                    = "_";

    /**
     * 状态码：成功
     */
    public static final String STATUS_SUCCESS                               = "0";

    /**
     * 状态码：失败
     */
    public static final String STATUS_FAIL                                  = "-1";

    /**
     * 系统错误
     * */
    public static final String ERROR_PATH                                   = "/error";

    /**
     * 404页面
     * */
    public static final String PAGE_ERROR_404                               = "/error/404";
    /**
     * 错误页面
     * */
    public static final String PAGE_ERROR                                   = "/error/error";

    /**
     * text/html
     */
    public static final String TEXT_HTML                                    = "text/html";

    /**
     * 提示消息：message
     * */
    public static final String MESSAGE                                      = "message";

    /**
     * 字符串：404
     */
    public static final String STR_404                                      = "404";

    /**
     * 应用名称
     */
    public static final String APP_NAME                                     = "appName";

    /**
     * 应用描述
     */
    public static final String APP_DESCRIBE                                 = "appDescribe";

    /**
     * shutdown
     */
    public static final String SHUTDOWN                                     = "shutdown";

    /**
     * 系统初始化数据
     */
    public static final String INIT_SYSTEM_DATA                             = "database/initData.sql";

    /**
     * 系统初始化表结构
     */
    public static final String INIT_SYSTEM_TABLE                            = "database/initTable.sql";

    /**
     * 系统升级数据
     */
    public static final String UPDATE_SYSTEM_DATA                           = "database/updateData.sql";

    /**
     * 系统升级数据
     */
    public static final String UPDATE_SYSTEM_PROCEDURE                      = "database/updateProcedure.sql";

    /**
     * 系统初始化存储过程
     */
    public static final String INIT_SYSTEM_PROCEDURE                        = "database/initProcedure.sql";

    /**
     * 系统配置sql
     */
    public static final String SYSTEM_CONFIG_SQL                            = "mapper/SqlConfig.xml";

    /**
     * 存储过程分隔符
     */
    public static final String SYSTEM_PROCEDURE_SPLIT                       = "-- ====== --";

    /**
     * 当前系统存在数据表
     */
    public static final String SYSTEM_TABLE                                 = new StringBuffer().append("'sys_user'").append(",")
                                                                                                .append("'sys_role'").append(",")
                                                                                                .append("'sys_user_role'").append(",")
                                                                                                .append("'sys_menu'").append(",")
                                                                                                .append("'sys_role_menu'").append(",")
                                                                                                .append("'sys_dictionary'").append(",")
                                                                                                .append("'sys_parameter'").append(",")
                                                                                                .append("'sys_notice'").append(",")
                                                                                                .append("'sys_version'").append(",")
                                                                                                .append("'sys_login_log'").append(",")
                                                                                                .append("'sys_income'").append(",")
                                                                                                .append("'sys_gift'").append(",")
                                                                                                .append("'sys_interface'").append(",")
                                                                                                .append("'sys_wechat_user'").append(",")
                                                                                                .append("'sys_wechat_flow'").append(",")
                                                                                                .append("'sys_config'").toString();

    /**
     * 字符串：0
     */
    public static final String STR_0                                        = "0";

    /**
     * 字符串：1
     */
    public static final String STR_1                                        = "1";

    /**
     * 字符串：2
     */
    public static final String STR_2                                        = "2";

    /**
     * 字符串：3
     */
    public static final String STR_3                                        = "3";

    /**
     * 字符串：4
     */
    public static final String STR_4                                        = "4";

    /**
     * 字符串：94
     */
    public static final String STR_9                                        = "9";

    /**
     * 字符串：10
     */
    public static final String STR_10                                       = "10";

    /**
     * 字符串：11
     */
    public static final String STR_11                                       = "11";

    /**
     * 字符串：12
     */
    public static final String STR_12                                       = "12";

    /**
     * 字符串：13
     */
    public static final String STR_13                                       = "13";

    /**
     * 字符串：13
     */
    public static final String STR_14                                       = "14";

    /**
     * 字符串：99
     */
    public static final String STR_99                                       = "99";

    /**
     * &nbsp转义
     */
    public static final String NBSP                                         = "\u00a0";

    /**
     * 字符串：数字
     */
    public static final String STR_NUMBER                                   = "[%s] ";

    /**
     * 空串
     */
    public static final String STR_EMPTY                                    = "";

    /**
     * 单引号
     */
    public static final String SINGLE_QUOTATION_MARKS                       = "'";

    /**
     * 空串
     */
    public static final String STR_SPACE                                    = " ";

    /**
     * 点号
     */
    public static final String POINT                                        = ".";

    /**
     * 点号
     */
    public static final String BACKSLASH_POINT                              = "\\.";

    /**
     * 分号
     */
    public static final String SEMICOLON                                    = ";";

    /**
     * 逗号
     */
    public static final String COMMA                                        = ",";

    /**
     * 逗号
     */
    public static final String COMMA_CHINESE                                = "，";

    /**
     * 冒号
     */
    public static final String COLON                                        = ":";

    /**
     * 冒号
     */
    public static final String COLON_CHINESE                                = "：";

    /**
     * 减号分隔符
     */
    public static final String MINUS                                        = "-";

    /**
     * 注释
     */
    public static final String EXPLAN                                       = "-- ";

    /**
     * 分隔符
     */
    public static final String SLASH                                        = "/";

    /**
     * =
     */
    public static final String EQUAL_SIGN                                   = "=";

    /**
     * underline
     */
    public static final String WELL                                         = "#";

    /**
     * 小于号
     */
    public static final String LESS_THAN                                    = "<";

    /**
     * 小于号
     */
    public static final String GREATER_THAN                                 = ">";

    /**
     * @
     */
    public static final String AT                                           = "@";

    /**
     * 反斜杠切割
     */
    public static final String BACKSLASH_S                                  = "\\s+";

    /**
     * 六个*
     */
    public static final String ASTERISK_SIX                                 = "******";

    /**
     * *
     */
    public static final String ASTERISK                                     = "*";

    /**
     * 单引号
     */
    public static final String SINGLE_QUOTES                                = "'";

    /**
     * 单引号
     */
    public static final String AMPERSAND                                    = "&";

    /**
     * $
     */
    public static final String $                                            = "$";

    /**
     * 换行
     */
    public static final String NEXT_LINE                                    = "\n";

    /**
     * text/plain
     */
    public static final String TEXT_PLAIN                                   = "text/plain";

    /**
     * multipart/*
     */
    public static final String MULTIPART                                    = "multipart/*";

    /**
     * UTF-8
     */
    public static final String UTF8                                         = "UTF-8";

    /**
     * GBK
     */
    public static final String BR                                           = "</br>";

    /**
     * 业务ID初始值
     */
    public static final String BUSINESS_ID_DEFAULT                          = "0000000001";

    /**
     * 数据权限ID
     */
    public static final String DATA_AUTHORITY_ID                            = "20190000000000";

    /**
     * 转义key
     */
    public static final String TRANSFER_KEY                                 = "user,userId,user_id,createUser,create_user,modifyUser,modify_user";

    /**
     * 是否有按钮权限
     */
    public static final String HAS_BUTTON                                   = "hasButton";

    /**
     * 业务类型：sessionBean
     */
    public static final String SESSION_BEAN                                 = "sessionBean";

    /**
     * 业务类型：cookie userCode
     */
    public static final String COOKIE_USER_CODE                             = "userCode";

    /**
     * 业务类型：cookie userPassword
     */
    public static final String COOKIE_USER_PASSWORD                         = "userPassword";

    /**
     * 业务类型：cookie rememberPassword
     */
    public static final String COOKIE_REMEMBER_PASSWORD                     = "rememberPassword";

    /**
     * 业务类型：用户
     */
    public static final String BUSINESS_TYPE_USER                           = "user";

    /**
     * 业务类型：角色
     */
    public static final String BUSINESS_TYPE_ROLE                           = "role";

    /**
     * 业务类型：用户角色
     */
    public static final String BUSINESS_TYPE_USER_ROLE                      = "userRole";

    /**
     * 业务类型：角色菜单
     */
    public static final String BUSINESS_TYPE_ROLE_MENU                      = "roleMenu";

    /**
     * 业务类型：收入
     */
    public static final String BUSINESS_TYPE_INCOME                         = "income";

    /**
     * 业务类型：通知
     */
    public static final String BUSINESS_TYPE_NOTICE                         = "notice";

    /**
     * 业务类型：字典
     */
    public static final String BUSINESS_TYPE_DICTIONARY                     = "dictionary";

    /**
     * 业务类型：登录日志
     */
    public static final String BUSINESS_TYPE_LOGIN_LOG                      = "loginLog";

    /**
     * 业务类型：修订
     */
    public static final String BUSINESS_TYPE_VERSION                        = "version";

    /**
     * 业务类型：接口
     */
    public static final String BUSINESS_TYPE_INTERFACE                      = "interface";

    /**
     * 业务类型：随礼
     */
    public static final String BUSINESS_TYPE_GIFT                           = "gift";

    /**
     * 字典项空白
     */
    public static final String BLANK                                        = "blank";

    /**
     * 查询条件：用户ID
     */
    public static final String SELECT_USER_ID                               = "userId";

    /**
     * 查询条件：收入来源
     */
    public static final String SELECT_INCOME_COMPANY                        = "incomeCompany";

    /**
     * 查询条件：收入类型
     */
    public static final String SELECT_INCOME_TYPE                           = "incomeType";

    /**
     * 查询条件：随礼类型
     */
    public static final String SELECT_GIFT_TYPE                             = "giftType";

    /**
     * 查询条件：送礼人
     */
    public static final String SELECT_GIFT_SENDER                           = "giftSender";

    /**
     * 查询条件：收礼人
     */
    public static final String SELECT_GIFT_RECEIVER                         = "giftReceiver";

    /**
     * 查询条件：用户状态
     */
    public static final String SELECT_USER_STATUS                           = "userStatus";

    /**
     * 查询条件：登入状态
     */
    public static final String SELECT_LOGIN_STATUS                          = "loginStatus";

    /**
     * 查询条件：通知类型
     */
    public static final String SELECT_NOTICE_TYPE                           = "noticeType";

    /**
     * 查询条件：通知状态
     */
    public static final String SELECT_NOTICE_STATUS                         = "noticeStatus";

    /**
     * 查询条件：业务类型
     */
    public static final String SELECT_BUSINESS_TYPE                         = "businessType";

    /**
     * 查询条件：业务子类型
     */
    public static final String SELECT_BUSINESS_SUB_TYPE                     = "businessSubType";

    /**
     * 查询条件：阅读状态
     */
    public static final String SELECT_READ_STATUS                           = "readStatus";

    /**
     * 报表类型：收入报表
     */
    public static final String REPORT_TYPE_INCOME                           = "income";

    /**
     * 报表类型：送礼报表
     */
    public static final String REPORT_TYPE_GIFT_SEND                        = "giftSend";

    /**
     * 报表类型：收礼礼报表
     */
    public static final String REPORT_TYPE_GIFT_RECEIVE                     = "giftReceive";

    /**
     * 报表模式：柱状图
     */
    public static final String REPORT_MODE_BAR                              = "bar";

    /**
     * 报表模式：饼图
     */
    public static final String REPORT_MODE_PIE                              = "pie";

    /**
     * 报表子类型：年度报表
     */
    public static final String REPORT_SUB_TYPE_YEAR                         = "year";

    /**
     * 报表子类型：月度报表
     */
    public static final String REPORT_SUB_TYPE_MONTH                        = "month";

    /**
     * 报表子类型：收入来源
     */
    public static final String REPORT_SUB_TYPE_SOURCE                       = "source";

    /**
     * 报表子类型：随礼类型、收入类型
     */
    public static final String REPORT_SUB_TYPE_TYPE                         = "type";

    /**
     * 报表子类型：极值
     */
    public static final String REPORT_SUB_TYPE_PEAK                         = "peak";

    /**
     * 报表子类型：随礼
     */
    public static final String REPORT_SUB_TYPE_GIFT                         = "gift";

    /**
     * 报表月份数：12
     */
    public static final Integer REPORT_NUM_12                               = 12;

    /**
     * 用户状态：冻结
     */
    public static final String USER_STATUS_FREEZE                            = "2";

    /**
     * x_requested_with
     */
    public static final String X_REQUESTED_WITH                              = "x-requested-with";

    /**
     * XMLHttpRequest
     */
    public static final String XML_HTTPREQUEST                               = "XMLHttpRequest";

    /**
     * 开关：on
     */
    public static final String SWITCH_ON                                     = "on";

    /**
     * 管理员
     */
    public static final String ADMIN_CODE                                    = "admin";

    /**
     * 登录请求
     * */
    public static final String PAGE_LOGIN                                    = "/login";

    /**
     * 首页请求
     * */
    public static final String PAGE_INDEX                                    = "/index";

    /**
     * 首页请求
     * */
    public static final String PAGE_CONSOLE                                  = "/console";

    /**
     * websocket请求
     * */
    public static final String PAGE_WEBSOCKET                                = "/websocket";

    /**
     * role页面请求
     * */
    public static final String PAGE_ROLE                                     = "/role";

    /**
     * 忽略后缀
     */
    public static final String IGNORE_SUFFIX                                 = "js,css,jpg,jpeg,png,gif,woff2,woff,ttf";

    /**
     * 忽略请求
     */
    public static final String IGNORE_REQUEST                                = "/login,/user/login";

    /**
     * 状态：status
     * */
    public static final String STATUS                                        = "status";

    /**
     * 参数类型
     */
    public static final String PARAMETER_TYPE_SWITCH                         = "switch";

    /**
     * 用户密码
     */
    public static final String PASSWORD                                      = "password";

    /**
     * 金额
     */
    public static final String AMOUNT                                        = "amount";

    /**
     * 日期
     */
    public static final String DATE                                          = "date";

    /**
     * 日期格式化模板
     */
    public static final String FORMAT_DATE_TEMPLATE                          = "yyyyMMdd";

    /**
     * 日期格式化模板
     */
    public static final String FORMAT_DATE_YYYYMM                            = "yyyyMM";

    /**
     * 日期格式化模板
     */
    public static final String FORMAT_DATE_YYYYMM_CHINESE                    = "yyyy年MM月";

    /**
     * 日期格式化模板
     */
    public static final String FORMAT_DATE_YYYY                              = "yyyy";

    /**
     * 日期格式化模板
     */
    public static final String FORMAT_DATE_YYYY_CHINESE                      = "yyyy年";

    /**
     * 菜单：收入信息
     */
    public static final String MENU_ID_INCOME                                = "20190000000001";

    /**
     * 菜单：随礼信息
     */
    public static final String MENU_ID_GIFT                                  = "20190000000004";

    /**
     * 菜单：修订信息
     */
    public static final String MENU_ID_VERSION                               = "20190000000021";

    /**
     * 菜单：参数信息
     */
    public static final String MENU_ID_PARAMETER                             = "20190000000016";

    /**
     * 菜单：通知信息
     */
    public static final String MENU_ID_NOTICE                                = "20190000000017";

    /**
     * 菜单：登录信息
     */
    public static final String MENU_ID_LOGIN                                 = "20190000000019";

    /**
     * 菜单：用户信息
     */
    public static final String MENU_ID_USER                                  = "20190000000012";

    /**
     * 菜单：超级管理员菜单
     */
    public static final String MENU_ID_SUPER_MODE                            = "20190000000015,20190000000021";

    /**
     * 用户名称
     */
    public static final String USER_NAME                                     = "userName";

    /**
     * 请求url
     */
    public static final String REQUEST_URL                                   = "requestUrl";

    /**
     * websocket主题：console
     */
    public static final String WEBSOCKET_TOPIC_NAME_CONSOLE                  = "console";

    /**
     * websocket主题：notice
     */
    public static final String WEBSOCKET_TOPIC_NAME_NOTICE                   = "notice";

    /**
     * sql模式：新增
     *
     */
    public static final String SQL_MODEL_ADD                                = "insert";

    /**
     * sql模式：删除
     *
     */
    public static final String SQL_MODEL_DELETE                             = "delete";

    /**
     * sql模式：修改
     *
     */
    public static final String SQL_MODEL_UPDATE                             = "update";

    /**
     * sql模式：查询
     *
     */
    public static final String SQL_MODEL_SELECT                             = "select";

    /**
     * 配置模块：首页
     *
     */
    public static final String MODULE_CONSOLE                               = "console";

    /**
     * 备份相关
     */
    public static final String COLUMN_NUMBER                                = "long,number,integer,float,decimal";
    public static final String COLUMN_TIMESTAMP                             = "timestamp";
    public static final String COLUMN_DATE                                  = "date";

    public static final String TO_DATE_LEFT                                 = "to_timestamp('";
    public static final String TO_DATE_RIGHT                                = "', 'yyyy-MM-dd')";
    public static final String TO_TIMESTAMP_LEFT                            = "to_timestamp('";
    public static final String TO_TIMESTAMP_RIGHT                           = "', 'yyyy-MM-dd hh24:mi:ss')";
    public static final String CHR_38                                       = "chr(38) || '";
    public static final String BRACKET_LEFT                                 = "(";
    public static final String BRACKET_RIGHT                                = ")";
    public static final String INSERT_LEFT                                  = "insert into ";
    public static final String VALUES_LEFT                                  = "values (";
    public static final String COMMIT                                       = "commit;";
    public static final String TRUNCATE_LEFT                                = "truncate table ";

    public static final String BACKUP_MODE_START                            = "start";
    public static final String BACKUP_MODE_SCHEDULE                         = "schedule";
    public static final String BACKUP_SQL_SUFFIX                            = ".sql";
    public static final String BACKUP_DMP_SUFFIX                            = ".dmp";
    public static final String BACKUP_EXCEL_SUFFIX                          = ".xlsx";
    public static final String BACKUP_LOG                                   = "export.log";

    public static final String BACKUP_COMMAND                               = "expdp %s/%s@%s directory=backup_dir dumpfile=%s";

    public static final Integer BACKUP_DATA_LIMIT                           = 100;

    /**
     * 接口业务类型：收入
      */
    public static final String INTERFACE_TYPE_INCOME                        = "收入";

    /**
     * 接口业务类型：随礼
      */
    public static final String INTERFACE_TYPE_GIFT                          = "随礼";

    /**
     * 系统当前状态：初始化
      */
    public static final String SYSTEM_STATUS_INIT                           = "init";

    /**
     * 系统当前状态：升级
     */
    public static final String SYSTEM_STATUS_UPDATE                         = "update";

    /**
     * 系统当前状态：备份
     */
    public static final String SYSTEM_STATUS_BACKUP                         = "backup";

    /**
     * 状态：timeout
     * */
    public static final String SYSTEM_STATUS_TIMEOUT                        = "timeout";

    /**
     * 状态：无菜单ID
     * */
    public static final String SYSTEM_STATUS_MENU_ID                        = "menuId";

    /**
     * Excel备份清单
     */
    public static final String BACKUP_EXCEL                                 = "sys_income,收入信息;sys_gift,随礼信息";

    /**
     * Excel单元格宽度
     */
    public static final Integer BACKUP_EXCEL_CELL_WIDTH                     = 20;

    /**
     * Excel 数据备份日期格式化
     */
    public static final String BACKUP_EXCEL_DATE_FORMAT                     = "00:00:00.0";

    /**
     * Excel 数据备份过滤字段
     */
    public static final String BACKUP_EXCEL_FILTER_COLUMN                   = "income_id,gift_id,row_id";

    /**
     * 异常类型：表或视图不存在
     */
    public static final String EXCEPTION_TYPE_TABLE_NOT_EXISTS              = "ORA-00942";

    /**
     * menuId
     */
    public static final String STR_KEY_MEND_ID                              = "menuId";

    /**
     * 过滤器配置路径
     */
    public static final String FILTER_URL_PATTERNS                          = "/*";

    /**
     * 菜单ID skip
     */
    public static final String STR_KEY_MEND_ID_SKIP                         = "skip";

}
