package com.hoomoomoo.im.consts;

import java.util.regex.Pattern;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public class BaseConst {

    // 控制未授权功能控制台打印日志
    public static final String APP_USER_IM = "im";
    // 控制svn统计功能
    public static final String APP_USER_IM_SVN = "im-svn";
    // 控制服务端控制
    public static final String APP_USER_IM_SERVER = "im-server";

    public static final String APP_CODE_BASE = "im-tool-ui-base";
    public static final String APP_CODE_TA = "im-tool-ui-ta";
    public static final String APP_CODE_SHOPPING = "im-tool-ui-shopping";

    public static final String APP_VERSION = "1.0.0.0";

    public static final String DEMO = "demo10000";

    public static final String STR_0 = "0";
    public static final String STR_1 = "1";
    public static final String STR_2 = "2";
    public static final String STR_3 = "3";
    public static final String STR_4 = "4";
    public static final String STR_5 = "5";
    public static final String STR_6 = "6";
    public static final String STR_7 = "7";
    public static final String STR_8 = "8";
    public static final String STR_9 = "9";
    public static final String STR_10 = "10";
    public static final String STR_11 = "11";
    public static final String STR_12 = "12";
    public static final String STR_13 = "13";
    public static final String STR_14 = "14";
    public static final String STR_15 = "15";
    public static final String STR_16 = "16";
    public static final String STR_17 = "17";
    public static final String STR_18 = "18";
    public static final String STR_19 = "19";
    public static final String STR_20 = "20";
    public static final String STR_69 = "69";
    public static final String STR_145 = "145";
    public static final String STR_549656 = "549656";
    public static final String STR_999999999 = "999999999";
    public static final String STR_999999999999 = "999999999999";

    public final static String ENCODING_UTF8 = "UTF-8";
    public final static String ENCODING_GBK = "GBK";
    public final static String ENCODING_GB = "GB";

    public final static String FILE_TYPE_NORMAL = "1";
    public final static String FILE_TYPE_CONFIG = "2";

    public static final String START_MODE_JAR = ".jar!";

    public static final String KEY_SVN_PASSWORD = "svn.password";
    public static final String KEY_SVN_UPDATE = "svn.update.";
    public static final String KEY_SCRIPT_UPDATE_TABLE = "script.update.table.";
    public static final String KEY_SVN_STAT_USER = "svn.stat.user.";
    public static final String KEY_COPY_CODE_VERSION = "copy.code.version.";
    public static final String KEY_COPY_CODE_LOCATION_REPLACE_SOURCE = "copy.code.location.replace.source";
    public static final String KEY_COPY_CODE_LOCATION_REPLACE_TARGET = "copy.code.location.replace.target";
    public static final String KEY_SVN_URL = "svn.url.";

    public static final String KEY_CLASSES = "classes";
    public static final String KEY_NOTICE = "notice";
    public static final String KEY_VALUES = "values";
    public static final String KEY_INSERT_INTO = "into";
    public static final String KEY_CONF = "conf";
    public static final String KEY_FILE_SLASH = "file:/";
    public static final String KEY_FILE = "file:";
    public static final String KEY_APP_LOG = "appLog";
    public static final String KEY_DELETE = "delete";
    public static final String KEY_TRANS_CODE_AND_SUB_TRANS_CODE = "transCodeAndSubTransCode";
    public static final String KEY_TA_CODE = "taCode";
    public static final String KEY_TA_CODE_UNDERLINE = "ta_code";
    public static final String KEY_TRANS_CODE_AND_SUB_TRANS_CODE_UNDERLINE = "trans_code_and_sub_trans_code";
    public static final String KEY_PRD_CODE = "prdCode";
    public static final String KEY_SELLER_CODE = "sellerCode";
    public static final String KEY_BRANCH_NO = "branchNo";
    public static final String KEY_SEARCH_FORM = "searchForm.";
    public static final String KEY_ADD_FORM = "addForm.";
    public static final String KEY_UPDATE_FORM = "updateForm.";
    public static final String KEY_BATCH_UPDATE_FORM = "batchUpdateForm.";
    public static final String KEY_HREF = "href";
    public static final String KEY_ORDER_ID = "orderId";
    public static final String KEY_PRODUCT_ID = "productId";
    public static final String KEY_BEGIN_DATE = "begin_date";
    public static final String KEY_ORI_BEG_DATE = "ori_beg_date";
    public static final String KEY_ORI = "ori";
    public static final String KEY_SORT = "sort";
    public static final String KEY_SCORE = "score";
    public static final String KEY_SAVE_STATUS = "saveStatus";
    public static final String KEY_ANONYMOUS_FLAG = "anonymousFlag";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_IMGS = "imgs";
    public static final String KEY_UNICK = "unick";
    public static final String KEY_LIB = "/lib";
    public static final String KEY_PRD_CODE_NAME = "基金名称";
    public static final String KEY_PRIMARY_KEY = "primary key";
    public static final String KEY_DATE = "Date";
    public static final String KEY_NOT_NULL = "not null,";
    public static final String KEY_VERSION = "version";
    public static final String KEY_TRUNK = "trunk";
    public static final String KEY_DESKTOP = "desktop";
    public static final String KEY_SOURCES = "Sources";
    public static final String KEY_FIX = "fix";
    public static final String KEY_B = "B";
    public static final String KEY_BACKUP = "bak";

    public static final String KEY_COLUMN_TYPE_DATE = "date";
    public static final String KEY_COLUMN_TYPE_INTEGER = "integer";
    public static final String KEY_COLUMN_TYPE_NUMBER = "number";
    public static final String KEY_COLUMN_TYPE_VARCHAR2 = "varchar2";

    public static final String KEY_ORDER_TYPE_DESC = "desc";

    public static final String METHOD_QUERY_DATA = "queryData";
    public static final String METHOD_GOODS_APPRAISE = "goodsAppraise";
    public static final String METHOD_SERVICE_APPRAISE = "serviceAppraise";

    public static final String OPERATE_TYPE_DELETE = "D";

    public static final String FILE_TYPE_SVN = ".svn";
    public static final String FILE_TYPE_LOG = ".log";
    public static final String FILE_TYPE_JAR = ".jar";
    public static final String FILE_TYPE_BAK = ".bak";
    public static final String FILE_TYPE_EXE = ".exe";
    public static final String FILE_TYPE_VUE = ".vue";
    public static final String FILE_TYPE_STAT = ".stat";
    public static final String FILE_TYPE_JAVA = ".java";
    public static final String FILE_TYPE_CLASS = ".class";
    public static final String FILE_TYPE_SQL = ".sql";
    public static final String FILE_TYPE_ROUTE = ".js";
    public static final String FILE_TYPE_CONF = ".conf";

    public static final String NAME_DELETE = " 删除";
    public static final String NAME_END = "结束";
    public static final String NAME_SVN_DESCRIBE = "[需求描述]";
    public static final String NAME_SVN_MODIFY_NO = "[修改单编号]";
    public static final String NAME_APPRAISE = "评价";
    public static final String NAME_SERVICE_APPRAISE = "服务评价";
    public static final String NAME_ORDER_DETAIL = "订单详情";
    public static final String NAME_APPRAISEING = "评价开始";
    public static final String NAME_APPRAISE_SUCCESS = "评价成功";
    public static final String NAME_APPRAISE_FAIL = "评价失败";
    public static final String NAME_REST_MOMENT = "策略休息[%s]秒";
    public static final String NAME_APPRAISE_COMPLETE = "评价完成";
    public static final String NAME_NO_APPRAISE_GOODS = "无评价商品";
    public static final String NAME_JD_LOGIN = "欢迎登录";
    public static final String NAME_JD_SHOW_ORDER = "继续晒单";
    public static final String NAME_JD_APPEND_APPRAISEING = "追评";
    public static final String NAME_GOODS_NOT_EXIST = "商品已下架";
    public static final String NAME_NET_CONNECT = "请连接网络";
    public static final String NAME_PAUSE_START = "暂停开始";
    public static final String NAME_PAUSE_COMPLETE = "暂停完成";
    public static final String NAME_TYPE_NOT_EXIST = "事件类型不存在";
    public static final String NAME_CURRENT_VERSION = "当前版本:";
    public static final String NAME_SAVE_SUCCESS = "修改成功 ";
    public static final String NAME_APP_START = "应用启动";
    public static final String NAME_CONFIG_INFO = "配置信息";
    public static final String NAME_CONFIG_USER = "用户信息";
    public static final String NAME_CONFIG_LICENSE = "授权信息";
    public static final String NAME_CONFIG_LICENSE_DATE = "授权有效日期";
    public static final String NAME_CONFIG_FILE = "配置文件";
    public static final String NAME_VERSION = "版本号";
    public static final String NAME_APP_TAB_SHOW = "# 默认展示tab页";
    public static final String NAME_SVN_STAT = "#################### svn统计";
    public static final String NAME_SVN_STAT_REALTIME = "#################### 实时统计";
    public static final String NAME_SVN_STAT_HISTORY = "#################### 历史统计";

    public final static String SECURITY_FLAG = "+++";

    public final static String SYMBOL_EMPTY = "";
    public final static String SYMBOL_AND = "&";
    public final static String SYMBOL_EQUAL = "=";
    public final static String SYMBOL_SPACE = " ";
    public final static String SYMBOL_SPACE_2 = "  ";
    public final static String SYMBOL_SPACE_3 = "   ";
    public final static String SYMBOL_SPACE_4 = "    ";
    public final static String SYMBOL_EQUALS = "=";
    public static final String SYMBOL_COMMA = ",";
    public static final String SYMBOL_SEMICOLON = ";";
    public static final String SYMBOL_COLON = ":";
    public static final String SYMBOL_HYPHEN = "-";
    public static final String SYMBOL_HYPHEN_1 = " - ";
    public final static String SYMBOL_POINT = ".";
    public final static String SYMBOL_$_SLASH = "\\$";
    public final static String SYMBOL_S_SLASH = "\\s+";
    public final static String SYMBOL_POINT_SLASH = "\\.";
    public static final String SYMBOL_SLASH = "/";
    public static final String SYMBOL_SLASH_1 = "\\";
    public static final String SYMBOL_BRACKETS_LEFT = "(";
    public static final String SYMBOL_BRACKETS_RIGHT = ")";
    public static final String SYMBOL_BRACKETS_1_LEFT = "[";
    public static final String SYMBOL_BRACKETS_1_RIGHT = "]";
    public static final String SYMBOL_NEXT_LINE = "\n";
    public static final String SYMBOL_NEXT_LINE_2 = "\n\n";
    public static final String SYMBOL_PERCENT = "%";
    public static final String SYMBOL_QUOTES = "\"";

    public final static String ANNOTATION_TYPE_NORMAL = "--";
    public final static String ANNOTATION_TYPE_CONFIG = "#";

    public final static Integer FUNCTION_CODE_1000 = 1000;
    public final static Integer FUNCTION_CODE_2000 = 2000;

    public static final Pattern STR_PATTERN = Pattern.compile("(\\\\u(\\w{4}))");

    public static final String MSG_SVN_USERNAME = "请设置[ svn.username ]";
    public static final String MSG_SVN_PASSWORD = "请设置[ svn.password ]";
    public static final String MSG_SVN_URL = "请设置[ svn.url ]";
    public static final String MSG_SVN_UPDATE_TA6 = "请设置[ svn.update.ta6 ]";
    public static final String MSG_FUND_GENERATE_PATH = "请设置[ fund.generate.path ]";
    public static final String MSG_PROCESS_GENERATE_PATH_SCHEDULE = "请设置[ process.generate.path.schedule ]";
    public static final String MSG_PROCESS_GENERATE_PATH_TRANS = "请设置[ process.generate.path.trans ]";
    public static final String MSG_SCRIPT_UPDATE_GENERATE_PATH = "请设置[ script.update.generate.path ]";
    public static final String MSG_SVN_STAT_USER = "请设置[ svn.stat.user. ]";
    public static final String MSG_SVN_STAT_INTERVAL = "请设置[ svn.stat.interval ]大于等于5";
    public static final String MSG_GENERATE_CODE_PATH = "请设置[ %s ]路径";
    public static final String MSG_WAIT_APPRAISE_JD_COOKIE = "请更新京东Cookie";
    public static final String MSG_AUTO_APPRAISE = "自动评价评价";
    public static final String MSG_SET = "请设置[ %s ]\n";
    public static final String MSG_MENU_STYLE = "格式[ 一级菜单.二级菜单.三级菜单 ]";
    public static final String MSG_MENU_STYLE_ERROR = "[ %s ]格式错误";
    public static final String MSG_APP_TITLE = "【%s: %s】";


    public static final String MSG_OPEN = "打开[ %s ] 功能界面";
    public static final String MSG_USE = "使用[ %s ] 功能";
    public static final String MSG_UPDATE = "更新[ %s ] 完成";
    public static final String MSG_LOAD = "加载[ %s ] 完成";
    public static final String MSG_INIT = "初始化[ %s ] 完成";
    public static final String MSG_ENCRYPT = "加密[ %s ] 完成";
    public static final String MSG_CHECK = "校验[ %s ] 完成";
    public static final String MSG_START = "开始[ %s ]";
    public static final String MSG_COMPLETE = "完成[ %s ]";
    public static final String MSG_FILE_GENERATE = "%s文件生成成功";
    public static final String MSG_FUNCTION_NOT_EXIST = "功能[ %s ]不存在";
    public static final String MSG_LICENSE_NOT_EXIST = "加载证书信息失败,请检查证书文件[ license.init ]";
    public static final String MSG_LICENSE_EXPIRE = "授权证书已过期,授权截止[ %s ]";
    public static final String MSG_LICENSE_NOT_AUTH = "功能[ %s ]未授权";
    public static final String MSG_LICENSE_NOT_USE = "未启用[ %s ]功能";
    public static final String MSG_DIVIDE_LINE = "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ";
    public static final String MSG_SVN_REALTIME_STAT = "%s  %s  %s  %s";
    public static final String MSG_EXECUTE = "[%s]执行中,请稍后再试";

    public static final String CONFIG_COLUMN = "配置字段信息";
    public static final String CONFIG_TABLE = "配置正式表结构";

    public static final String APP_MODE_NAME = "模式";
    public static final String APP_MODE_NAME_APP = "工厂模式";
    public static final String APP_MODE_NAME_JAR = "Jar模式";

    public static final String CONFIG_PREFIX = "#################### ";

    public static final String CMD_KILL_APP = "taskill /f /t /im %s";

    public static final String PATH_APP = "/conf/app.conf";
    public static final String PATH_APP_EXTEND = "/conf/appExtend.conf";
    public static final String PATH_JD_COOKIE = "/conf/jdCookie.conf";
    public static final String PATH_AUTH = "/conf/auth/auth.conf";
    public static final String PATH_LICENSE = "/conf/init/license.init";
    public static final String PATH_VERSION = "/conf/init/version.init";
    public static final String PATH_FXML = "/conf/fxml";
    public static final String PATH_STYLE = "/conf/style";
    public static final String PATH_LOG = "/logs/%s/%s";
    public static final String PATH_STAT = "/stats/%s";
    public static final String PATH_ICON = "/conf/image/icon.png";
    public static final String FACTORY_ICON = "/conf/image/factory.png";
    public static final String PATH_STARTER_FXML = "/conf/fxml/starter.fxml";
    public static final String PATH_STARTER_CSS = "/conf/style/progressIndicator.css";
    public static final String PATH_COLUMN_SET_FXML = "/conf/fxml/generateCodeColumnSet.fxml";
    public static final String PATH_BLANK_SET_FXML = "/conf/fxml/blankSet.fxml";
    public static final String PATH_RECORD_LOG = "/records/%s.record";

    public static final String PACKAGE_JAVA_PREFIX = "com.hundsun.lcpt.fund.";
    public static final String PATH_JAVA_PREFIX = "/src/main/java/";
    public static final String PACKAGE_VUE_PREFIX = "src.biz.console-fund-ta-vue.views.";

    public static final String METHOD_RETURN_PARAM_IDATASET = "com.hundsun.jres.interfaces.share.dataset.IDataset";
    public static final String METHOD_RETURN_PARAM_QUERY_STAT_INFO = "com.hundsun.lcpt.ta.base.webmanager.QueryStatInfo";
    public static final String METHOD_RETURN_PARAM_HS_SQL_STRING = "com.hundsun.lcpt.util.HsSqlString";
    public static final String METHOD_RETURN_PARAM_LIST = "java.util.List<%s>";
    public static final String METHOD_RETURN_PARAM_STRING = "java.lang.String";

    public static final String METHOD_REQUEST_PARAM_STRING = "dataset";
    public static final String METHOD_REQUEST_PARAM_TABLE_NAME = "tableName";
    public static final String METHOD_REQUEST_PARAM_AUDIT_TABLE_NAME = "auditTableName";
    public static final String METHOD_REQUEST_PARAM_ENTITY_DETAILS = "entityDetails";
    public static final String METHOD_REQUEST_PARAM_SESSION = "session";
    public static final String METHOD_REQUEST_PARAM_DTO = "dto";
    public static final String METHOD_REQUEST_PARAM_MAP = "map";
    public static final String METHOD_REQUEST_PARAM_WORK_PROCESS_DTO = "workProcessDTO";
    public static final String METHOD_REQUEST_PARAM_ASYN_AUDIT_DTO = "asynAuditDTO";
    public static final String METHOD_REQUEST_PARAM_ENTRY_SERIAL_NO = "entrySerialNo";
    public static final String METHOD_REQUEST_PARAM_ENTRY_ORDER_NO = "entryOrderNo";
    public static final String METHOD_REQUEST_PARAM_OPERATOR_MODE = "operatorMode";
    public static final String METHOD_REQUEST_PARAM_FLAG = "flag";

    public static final String METHOD_TYPE_QUERY = "查询";
    public static final String METHOD_TYPE_ADD = "新增";
    public static final String METHOD_TYPE_EDIT = "修改";
    public static final String METHOD_TYPE_DELETE = "删除";
    public static final String METHOD_TYPE_IMPORT = "导入";
    public static final String METHOD_TYPE_EXPORT = "导出";

    public static final String SUB_TRANSCODE_ADD = "Add";
    public static final String SUB_TRANSCODE_EDIT = "Edit";
    public static final String SUB_TRANSCODE_DELETE = "Delete";
    public static final String SUB_TRANSCODE_IMPORT = "Import";

    public static final String DB_TYPE_PUB = "1";
    public static final String DB_TYPE_TRANS = "2";
    public static final String DB_TYPE_TRANS_QUERY = "3";
    public static final String DB_TYPE_TRANS_ORDER = "4";

    public static final String PAGE_TYPE_SET = "1";
    public static final String PAGE_TYPE_QUERY = "2";
    public static final String PAGE_TYPE_QUERY_CONFIG = "3";

    public static final Integer JD_APPRAISE_LENGTH_MIN = 30;
    public static final Integer JD_APPRAISE_LENGTH_MAX = 450;

    public static final String SERVER_HTTP = "http://172.24.52.81";
    public static final String SERVER_URL = "/tool/im";
    public static final Integer SERVER_PORT = 13579;

}
