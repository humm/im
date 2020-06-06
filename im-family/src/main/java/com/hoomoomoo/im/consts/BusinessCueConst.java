package com.hoomoomoo.im.consts;

/**
 * @author hoomoomoo
 * @description 提示信息常量
 * @package com.hoomoomoo.im.consts
 * @date 2019/08/03
 */

public class BusinessCueConst {

    /**
     * 提示信息：操作成功
     */
    public static final String OPERATE_SUCCESS                                  = "操作成功";

    /**
     * 提示信息：查询成功
     */
    public static final String SELECT_SUCCESS                                   = "查询成功";

    /**
     * 提示信息：重置密码成功
     */
    public static final String RESET_PASSWORD_SUCCESS                           = "重置密码成功";

    /**
     * 提示信息：修改密码成功
     */
    public static final String UPDATE_PASSWORD_SUCCESS                          = "修改密码成功";

    /**
     * 提示信息：新增成功
     */
    public static final String ADD_SUCCESS                                      = "新增成功";

    /**
     * 提示信息：删除成功
     */
    public static final String DELETE_SUCCESS                                   = "删除成功";

    /**
     * 提示信息：修改成功
     */
    public static final String UPDATE_SUCCESS                                   = "修改成功";

    /**
     * 提示信息：校验成功
     */
    public static final String CHECK_SUCCESS                                    = "校验成功";

    /**
     * 提示信息：刷新成功
     */
    public static final String REFRESH_SUCCESS                                  = "刷新成功";

    /**
     * 提示信息：账号不存在
     */
    public static final String USER_LOGON_ACCOUNT_NOT_EXIST                     = "账号不存在";

    /**
     * 提示信息：账号已冻结
     */
    public static final String USER_LOGON_ACCOUNT_FREEZE                        = "账号已冻结";

    /**
     * 提示信息：密码错误
     */
    public static final String USER_LOGON_PASSWORD_ERROR                        = "密码错误";

    /**
     * 提示信息：登入成功
     */
    public static final String USER_LOGON_SUCCESS                               = "登入成功";

    /**
     * 提示信息：登出成功
     */
    public static final String USER_LOGOUT_SUCCESS                              = "退出成功";

    /**
     * 提示信息：送礼人和收礼人都不为系统用户
     */
    public static final String GIFT_SENDER_RECEIVER_NOT_EXIST                   = "送礼人和收礼人必须有一个为系统用户";

    /**
     * 提示信息：送礼人和收礼人不能同时为系统用户
     */
    public static final String GIFT_SENDER_RECEIVER_EXIST                       = "送礼人和收礼人不能同时为系统用户";

    /**
     * 请设置系统备份路径
     */
    public static final String BACKUP_LOCATION_IS_EMPTY                         = "请设置系统备份路径";

    /**
     * 系统备份成功
     */
    public static final String BACKUP_SUCCESS                                   = "备份成功";

    /**
     * 日志状态：开始
     */
    public static final String LOG_OPERATE_TAG_START                            = "开始";

    /**
     * 日志状态：结束
     */
    public static final String LOG_OPERATE_TAG_END                              = "结束";

    /**
     * 日志操作类型：查询
     */
    public static final String LOG_OPERATE_TYPE_SELECT                          = "查询";

    /**
     * 日志操作类型：处理
     */
    public static final String LOG_OPERATE_TYPE_HANDLE                          = "处理";

    /**
     * 日志操作类型：分页查询
     */
    public static final String LOG_OPERATE_TYPE_SELECT_PAGE                     = "分页查询";

    /**
     * 日志操作类型：页面渲染参数查询
     */
    public static final String LOG_OPERATE_TYPE_SELECT_INIT                     = "页面渲染参数查询";

    /**
     * 日志操作类型：重置用户密码
     */
    public static final String LOG_OPERATE_TYPE_RESET_PASSWORD                  = "重置用户密码";

    /**
     * 日志操作类型：修改用户密码
     */
    public static final String LOG_OPERATE_TYPE_UPDATE_PASSWORD                 = "修改用户密码";

    /**
     * 日志操作类型：新增
     */
    public static final String LOG_OPERATE_TYPE_ADD                             = "新增";

    /**
     * 日志操作类型：修改
     */
    public static final String LOG_OPERATE_TYPE_UPDATE                          = "修改";

    /**
     * 日志操作类型：校验
     */
    public static final String LOG_OPERATE_TYPE_CHECK                           = "校验";

    /**
     * 日志操作类型：删除
     */
    public static final String LOG_OPERATE_TYPE_DELETE                          = "删除";

    /**
     * 日志操作类型：刷新
     */
    public static final String LOG_OPERATE_TYPE_REFRESH                         = "刷新";


    /**
     * 日志业务类型：应用启动
     */
    public static final String LOG_BUSINESS_TYPE_APP_START                      = "应用启动";

    /**
     * 日志业务类型：参数配置
     */
    public static final String LOG_BUSINESS_TYPE_PARAMETER_CONFIG               = "参数配置";

    /**
     * 日志业务类型：数据初始化
     */
    public static final String LOG_BUSINESS_TYPE_INIT                           = "应用数据";

    /**
     * 日志业务类型：登录过滤器
     */
    public static final String LOG_BUSINESS_TYPE_LOGIN_FILTER                   = "登录过滤器配置";

    /**
     * 日志业务类型：权限过滤器
     */
    public static final String LOG_BUSINESS_TYPE_AUTH_FILTER                    = "权限过滤器配置";

    /**
     * 日志业务类型：session配置
     */
    public static final String LOG_BUSINESS_TYPE_SESSION                        = "session配置";

    /**
     * 日志业务类型：Interceptor配置
     */
    public static final String LOG_BUSINESS_TYPE_INTERCEPTOR                    = "拦截器配置器配置";

    /**
     * 日志业务类型：系统拦截器配置
     */
    public static final String LOG_BUSINESS_TYPE_INTERCEPTOR_CONFIG             = "系统拦截器配置";

    /**
     * 日志业务类型：系统过滤器配置
     */
    public static final String LOG_BUSINESS_TYPE_FILTER                         = "系统过滤器配置";

    /**
     * 日志业务类型：邮件发送
     */
    public static final String LOG_BUSINESS_TYPE_MAIL_SEND                      = "邮件发送";

    /**
     * 日志业务类型：邮件读取
     */
    public static final String LOG_BUSINESS_TYPE_MAIL_RECEIVE                   = "邮件读取";

    /**
     * 日志业务类型：邮件数据处理
     */
    public static final String LOG_BUSINESS_TYPE_MAIL_HANDLE                    = "邮件数据处理";

    /**
     * 日志业务类型：收入
     */
    public static final String LOG_BUSINESS_TYPE_INCOME                         = "收入信息";

    /**
     * 日志业务类型：字典
     */
    public static final String LOG_BUSINESS_TYPE_DICTIONARY                     = "字典信息";

    /**
     * 日志业务类型：用户
     */
    public static final String LOG_BUSINESS_TYPE_USER                           = "用户信息";

    /**
     * 日志业务类型：微信用户
     */
    public static final String LOG_BUSINESS_TYPE_USER_WECHAT                    = "微信用户信息";

    /**
     * 日志业务类型：微信流程步骤
     */
    public static final String LOG_BUSINESS_TYPE_WECHAT_FLOW                    = "微信流程步骤";

    /**
     * 日志业务类型：角色
     */
    public static final String LOG_BUSINESS_TYPE_ROLE                           = "角色信息";

    /**
     * 日志业务类型：通知
     */
    public static final String LOG_BUSINESS_TYPE_NOTICE                         = "通知信息";

    /**
     * 日志业务类型：菜单
     */
    public static final String LOG_BUSINESS_TYPE_MENU                           = "菜单信息";

    /**
     * 日志业务类型：数据权限
     */
    public static final String LOG_BUSINESS_TYPE_DATA_AUTHORITY                 = "数据权限信息";

    /**
     * 日志业务类型：报表
     */
    public static final String LOG_BUSINESS_TYPE_REPORT                         = "报表信息";

    /**
     * 日志业务类型：登入日志信息
     */
    public static final String LOG_BUSINESS_TYPE_LOGIN_LOG                      = "登入日志信息";

    /**
     * 日志业务类型：数据库备份
     */
    public static final String LOG_BUSINESS_TYPE_BACKUP                         = "数据备份";

    /**
     * 日志业务类型：数据库备份
     */
    public static final String LOG_BUSINESS_TYPE_BACKUP_LIST                    = "数据备份清单";

    /**
     * 日志业务类型：SQL数据备份
     */
    public static final String LOG_BUSINESS_TYPE_BACKUP_SQL                     = "SQL数据备份";

    /**
     * 日志业务类型：DMP数据备份
     */
    public static final String LOG_BUSINESS_TYPE_BACKUP_DMP                     = "DMP数据备份";

    /**
     * 日志业务类型：EXCEL数据备份
     */
    public static final String LOG_BUSINESS_TYPE_BACKUP_EXCEL                   = "EXCEL数据备份";

    /**
     * 日志业务类型：应用备份文件发送至邮箱
     */
    public static final String LOG_BUSINESS_TYPE_BACKUP_TO_MAIL                 = "应用备份文件发送至邮箱";

    /**
     * 日志业务类型：修订
     */
    public static final String LOG_BUSINESS_TYPE_VERSION                        = "修订信息";

    /**
     * 日志业务类型：参数
     */
    public static final String LOG_BUSINESS_TYPE_PARAMETER                      = "参数信息";

    /**
     * 日志业务类型：邮件
     */
    public static final String LOG_BUSINESS_TYPE_MAIL                           = "邮件信息";

    /**
     * 日志业务类型：随礼
     */
    public static final String LOG_BUSINESS_TYPE_GIFT                           = "随礼信息";

    /**
     * 日志业务类型：首页
     */
    public static final String LOG_BUSINESS_TYPE_CONSOLE                        = "首页信息";

    /**
     * 日志业务类型：微信
     */
    public static final String LOG_BUSINESS_TYPE_WECHAT                         = "微信信息";

    /**
     * 日志业务类型：字典转义
     */
    public static final String LOG_BUSINESS_TYPE_DICTIONARY_TRANSFER            = "字典转义";

    /**
     * 日志业务类型：业务序列号
     */
    public static final String LOG_BUSINESS_TYPE_BUSINESS_SERIAL_NO_GET         = "获取业务序列号";

    /**
     * 日志业务类型：业务序列号
     */
    public static final String LOG_BUSINESS_TYPE_BUSINESS_SERIAL_NO_LOAD        = "加载业务序列号";

    /**
     * 日志业务类型：字典信息
     */
    public static final String LOG_BUSINESS_TYPE_DICTIONARY_LOAD                = "加载字典信息";

    /**
     * 日志业务类型：用户ID
     */
    public static final String LOG_BUSINESS_TYPE_USER_ID_SELECT                 = "获取用户ID";

    /**
     * 日志业务类型：查询条件
     */
    public static final String LOG_BUSINESS_TYPE_CONDITION_SET                  = "设置查询条件";

    /**
     * 日志业务类型：按钮权限
     */
    public static final String LOG_BUSINESS_TYPE_BUTTON_AUTHORITY_SELECT        = "查询按钮权限";

    /**
     * 日志业务类型：系统初始化
     */
    public static final String LOG_BUSINESS_TYPE_INIT_SYSTEM                    = "系统初始化";

    /**
     * 日志业务类型：系统升级
     */
    public static final String LOG_BUSINESS_TYPE_UPDATE_SYSTEM                  = "系统升级";

    /**
     * 邮件信息处理中
     */
    public static final String LOG_BUSINESS_MAIL_HANDLEING                      = "邮件信息处理中...";

    /**
     * 邮件参数配置
     */
    public static final String MAIL_NOT_CONFIG                                  = "请配置邮件相关参数...";

    /**
     * 邮件参数设置
     */
    public static final String MAIL_NOT_SET                                     = "请设置邮件相关参数...";

    /**
     * 不支持的内容格式
     */
    public static final String MAIL_CONTENT_NOT_SUPPORT                         = "不支持的内容格式";

    /**
     * 业务类型不能为空
     */
    public static final String BUSINESS_TYPE_NOT_EMPTY                          = "业务类型不能为空";

    /**
     * 备份命令
     */
    public static final String TIP_BACKUP_COMMAND                               = "执行备份命令: ";

    /**
     * 最近一次登入时间
     */
    public static final String CONSOLE_LOGIN_LAST_DATE                          = "最近一次登入时间";

    /**
     * 本月登入次数
     */
    public static final String CONSOLE_LOGIN_MONTH_TIME                         = "本月登入次数";

    /**
     * 本年登入次数
     */
    public static final String CONSOLE_LOGIN_YEAR_TIME                          = "本年登入次数";

    /**
     * 登入总次数
     */
    public static final String CONSOLE_LOGIN_TOTAL_TIME                         = "登入总次数";

    /**
     * 登入信息
     */
    public static final String CONSOLE_LOGIN_TITLE                              = "登入信息";

    /**
     * 版本信息
     */
    public static final String CONSOLE_VERSION_TITLE                            = "版本信息";

    /**
     * 提示信息
     */
    public static final String CONSOLE_VERSION_TIPS                             = "提示信息";

    /**
     * 版本号
     */
    public static final String CONSOLE_VERSION_CODE                             = "版本号";

    /**
     * 注册信息
     */
    public static final String CONSOLE_REGISTER_TIPS                            = "注册信息";

    /**
     * 系统注册用户数
     */
    public static final String CONSOLE_REGISTER_NUM                             = "系统注册用户数";

    /**
     * 数据备份开始
     */
    public static final String BACKUP_TIPS                                      = "-- %s %s";

    /**
     * 家庭信息
     */
    public static final String FAMILY_TITLE                                     = "家庭";

    /**
     * 最近一笔收入
     */
    public static final String CONSOLE_INCOME_LAST                              = "最近一笔收入";

    /**
     * 收入环比增长
     */
    public static final String CONSOLE_INCOME_CHAIN_RATIO                       = "月度收入环比增长";

    /**
     * 收入同比增长
     */
    public static final String CONSOLE_INCOME_RATIO                             = "月度收入同比增长";

    /**
     * 月度收入
     */
    public static final String CONSOLE_INCOME_MONTH                             = "月度收入";

    /**
     * 年度收入
     */
    public static final String CONSOLE_INCOME_YEAR                              = "年度收入";

    /**
     * 总收入
     */
    public static final String CONSOLE_INCOME_TOTAL                             = "总收入";

    /**
     * 统计开始时间
     */
    public static final String CONSOLE_YEAR_START_DATE                          = "年度开始日期";

    /**
     * 最近一笔送礼
     */
    public static final String CONSOLE_GIFT_SEND_LAST                           = "最近一笔送礼";

    /**
     * 年度送礼
     */
    public static final String CONSOLE_GIFT_SEND_YEAR                           = "年度送礼";

    /**
     * 总送礼
     */
    public static final String CONSOLE_GIFT_SEND_TOTAL                          = "总送礼";

    /**
     * 最近一笔收礼
     */
    public static final String CONSOLE_GIFT_RECEIVE_LAST                        = "最近一笔收礼";

    /**
     * 年度收礼
     */
    public static final String CONSOLE_GIFT_RECEIVE_YEAR                        = "年度收礼";

    /**
     * 总收礼
     */
    public static final String CONSOLE_GIFT_RECEIVE_TOTAL                       = "总收礼";

    /**
     * 请选择
     */
    public static final String SELECT                                           = "请选择";

    /**
     * 报表单位：年
     */
    public static final String REPORT_UNIT_YEAR                                 = "年";

    /**
     * 报表单位：月
     */
    public static final String REPORT_UNIT_MONTH                                = "月";

    /**
     * 接口信息：当前用户不能为空
     */
    public static final String INTERFACE_NOT_EMPTY_USER                         = "当前用户不能为空";

    /**
     * 接口信息：当前用户不存在
     */
    public static final String INTERFACE_NOT_EXIT_USER                          = "当前用户[%s]不存在";

    /**
     * 接口信息：目标对象不能为空
     */
    public static final String INTERFACE_NOT_EMPTY_TARGET                       = "目标对象不能为空";

    /**
     * 接口信息：目标对象不存在
     */
    public static final String INTERFACE_NOT_EXIT_TARGET                        = "目标对象[%s]不存在";

    /**
     * 接口信息：当前用户和目标对象必须有一个为系统用户
     */
    public static final String INTERFACE_MUST_ONE_SYSTEM_USER                   = "当前用户[%s]和目标对象[%s]必须有一个为系统用户";

    /**
     * 接口信息：业务日期不能为空
     */
    public static final String INTERFACE_NOT_EMPTY_DATE                         = "业务日期不能为空";

    /**
     * 接口信息：业务日期不能为空
     */
    public static final String INTERFACE_FORMAT_DATE                            = "业务日期[%s]格式错误";

    /**
     * 接口信息：业务类型不能为空
     */
    public static final String INTERFACE_NOT_EMPTY_TYPE                         = "业务类型不能为空";

    /**
     * 接口信息：业务类型不存在
     */
    public static final String INTERFACE_NOT_EXIT_TYPE                          = "业务类型[%s]不存在";

    /**
     * 接口信息：业务子类型不能为空
     */
    public static final String INTERFACE_NOT_EMPTY_SUBTYPE                      = "业务子类型不能为空";

    /**
     * 接口信息：业务子类型不存在
     */
    public static final String INTERFACE_NOT_EXIT_SUBTYPE                       = "业务子类型[%s]不存在";

    /**
     * 接口信息：业务金额不能为空
     */
    public static final String INTERFACE_NOT_EMPTY_AMOUNT                       = "业务金额不能为空";

    /**
     * 接口信息：业务金额格式错误
     */
    public static final String INTERFACE_FORMAT_AMOUNT                          = "业务金额[%s]格式错误";

    /**
     * 接口信息：业务备注不能超过150个字符
     */
    public static final String INTERFACE_LENGTH_MEMO                            = "业务备注不能超过150个字符";

    /**
     * 接口信息：业务备注不能超过150个字符
     */
    public static final String INTERFACE_TOGETHER_ERROR                         = "申请数据中有校验失败数据";

    /**
     * 接口信息：邮件处理结果反馈
     */
    public static final String INTERFACE_FEEDBACK_MAIL                          = "邮件处理结果反馈";

    /**
     * 接口信息：处理成功
     */
    public static final String INTERFACE_MAIL_SUCCESS                           = "<b style='color:green'>处理成功</b>";

    /**
     * 接口信息：处理成功
     */
    public static final String INTERFACE_WECHAT_SUCCESS                         = "处理成功";

    /**
     * 接口信息：处理失败
     */
    public static final String INTERFACE_MAIL_FAIL                              = "<b style='color:red'>处理失败</b>";

    /**
     * 接口信息：处理失败
     */
    public static final String INTERFACE_WECHAT_FAIL                            = "处理失败";

    /**
     * 暂不处理异常
     */
    public static final String EXCEPTION_NOT_HANDLE                             = "暂不处理异常: %s";

    /**
     * 定时任务：邮件消息
     */
    public static final String BUSINESS_SCHEDULE_MAIL                           = "邮件信息定时任务: %s";

    /**
     * 定时任务：系统备份定时任务
     */
    public static final String BUSINESS_SCHEDULE_BACKUP                         = "系统备份定时任务: %s";

    /**
     * 业务流水号：提示信息信息
     */
    public static final String BUSINESS_BUSINESS_NO                             = "业务流水号";

    /**
     * 定时任务：微信消息
     */
    public static final String BUSINESS_SCHEDULE_WECHAT                         = "微信流程步骤定时任务: %s";

    /**
     * 稍后再试
     */
    public static final String BUSINESS_OPERATE_WAIT                            = "请稍后再试...";

    /**
     * session超时
     */
    public static final String BUSINESS_OPERATE_TIMEOUT                         = "由于您长时间没有操作，请重新登录...";

    /**
     * 微信：身份认证
     */
    public static final String WECHAT_USER_AUTH                                 = "公众号暂不对外开放,请输入密钥完成身份认证";

    /**
     * 微信：用户信息绑定
     */
    public static final String WECHAT_USER_BIND                                 = "恭喜你,身份认证成功,请输入系统用户代码";

    /**
     * 微信：欢迎再次关注
     */
    public static final String WECHAT_SUBSCRIBE_AGAIN                           = "欢迎再次关注";

    /**
     * 微信：身份认证失败
     */
    public static final String WECHAT_USER_AUTH_FAIL                            = "身份认证失败,请重新输入";

    /**
     * 微信：绑定用户信息失败
     */
    public static final String WECHAT_USER_BIND_FAIL                            = "系统用户代码不存在,请重新输入";

    /**
     * 微信：绑定用户信息成功
     */
    public static final String WECHAT_USER_BIND_SUCCESS                         = "恭喜你,用户[%s]绑定成功";

    /**
     * 微信：请回复对应数字选择服务
     */
    public static final String WECHAT_MAIN_FLOW                                 = "请回复对应序号选择服务";

    /**
     * 微信：服务不存在
     */
    public static final String WECHAT_SERVICE_NOT_EXIST                         = "服务不存在,请重新选择";

    /**
     * 微信：事件类型不存在
     */
    public static final String WECHAT_UNSUPPORT_EVENT                           = "暂不支持事件类型,请回复文本内容";

    /**
     * 微信：yyyyMM
     */
    public static final String WECHAT_CONDITION_YYYYMM                          = "查询条件格式为: yyyyMM";

    /**
     * 微信：yyyy
     */
    public static final String WECHAT_CONDITION_YYYY                            = "查询条件格式为: yyyy";

    /**
     * 微信：业务信息title
     */
    public static final String WECHAT_BUSINESS_TITLE                            = "%s %s";

    /**
     * 微信：总信息title
     */
    public static final String WECHAT_ALL_TITLE                                 = "总%s";

    /**
     * 微信：自由查询
     */
    public static final String WECHAT_FREE_TITLE                                 = "随礼信息查询[%s]";

    /**
     * 微信：最近一次随礼
     */
    public static final String WECHAT_GIFT_LAST_TITLE                            = "最近一次随礼信息";

    /**
     * 微信：送礼信息
     */
    public static final String WECHAT_GIFT_SEND_TITLE                            = "送礼信息";

    /**
     * 微信：收礼信息
     */
    public static final String WECHAT_GIFT_RECEIVE_TITLE                         = "收礼信息";

    /**
     * 微信：未查询到相关信息
     */
    public static final String WECHAT_NO_DATA_TITLE                              = "未查询到相关信息";

    /**
     * 微信：参数数量不匹配,请重新输入
     */
    public static final String WECHAT_PARAMETER_ERROR                            = "数据不匹配,请重新输入";

    /**
     * 接口提示信息相关
     */
    public static final String INTERFACE_TIPS_USER                               = "当前用户";
    public static final String INTERFACE_TIPS_TARGET                             = "目标对象";
    public static final String INTERFACE_TIPS_BUSINESS_DATE                      = "业务日期";
    public static final String INTERFACE_TIPS_BUSINESS_SUB_TYPE                  = "业务子类型";
    public static final String INTERFACE_TIPS_BUSINESS_AMOUNT                    = "业务金额";
    public static final String INTERFACE_TIPS_BUSINESS_MEMO                      = "业务备注";

    public static final String INTERFACE_TIPS_INCOME_USER                        = "收入用户";
    public static final String INTERFACE_TIPS_INCOME_TARGET                      = "收入来源";
    public static final String INTERFACE_TIPS_INCOME_BUSINESS_DATE               = "收入日期";
    public static final String INTERFACE_TIPS_INCOME_BUSINESS_SUB_TYPE           = "收入类型";
    public static final String INTERFACE_TIPS_INCOME_BUSINESS_AMOUNT             = "收入金额";
    public static final String INTERFACE_TIPS_INCOME_BUSINESS_MEMO               = "收入备注";

    public static final String INTERFACE_TIPS_GIFT_USER                          = "送礼用户";
    public static final String INTERFACE_TIPS_GIFT_TARGET                        = "收礼用户";
    public static final String INTERFACE_TIPS_GIFT_BUSINESS_DATE                 = "随礼日期";
    public static final String INTERFACE_TIPS_GIFT_BUSINESS_SUB_TYPE             = "随礼类型";
    public static final String INTERFACE_TIPS_GIFT_BUSINESS_AMOUNT               = "随礼金额";
    public static final String INTERFACE_TIPS_GIFT_BUSINESS_MEMO                 = "随礼备注";

    /**
     * 系统错误
     */
    public static final String TIPS_ERROR                                        = "系统错误";

    /**
     * 没有访问权限
     */
    public static final String TIPS_ERROR_AUTH                                   = "无访问权限";

    /**
     * 没有菜单ID
     */
    public static final String TIPS_ERROR_MENU                                   = "无菜单权限";

    /**
     * 家庭信息平台数据备份
     */
    public static final String MAIL_SUBJECT_BACKUP                               = "家庭信息平台数据备份";

    /**
     * 备份文件详见附件
     */
    public static final String MAIL_BACKUP_FILE                                  = "备份文件详见附件...";

}
