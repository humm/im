package com.hoomoomoo.im.consts;

import java.util.regex.Pattern;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public class BaseConst {

    public static final String SUPER_MAC_ADDRESS = "I4t5D856tNc1Yv4nEZFI9w9nPZUqIzS6GmAUeAETli3aF1kUPiF06g==";

    public static final String APP_CODE_BASE = "im-tool-ui-base";
    public static final String APP_CODE_TA = "im-tool-ui-ta";

    public static final String APP_VERSION = "1.0.0.0";

    public static final String DEMO = "demo10000";

    public static final int MAX_COMMON_FUNCTION_CODE = 3000;

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
    public static final String STR_99 = "99";
    public static final String STR_145 = "145";
    public static final String STR_549656 = "549656";
    public static final String STR_9900 = "9900";
    public static final String STR_9990 = "9990";
    public static final String STR_9999 = "9999";
    public static final String STR_99999999 = "99999999";
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
    public static final String KEY_COPY_CODE_LOCATION_REPLACE_SOURCE = "copy.code.location.replace.source.";
    public static final String KEY_COPY_CODE_LOCATION_REPLACE_TARGET = "copy.code.location.replace.target.";
    public static final String KEY_SVN_URL = "svn.url.";
    public static final String KEY_FIELD_TRANSLATE = "generate.code.field.translate.";

    public static final String KEY_CLASSES = "classes";
    public static final String KEY_NOTICE = "notice";
    public static final String KEY_VALUES = "values";
    public static final String KEY_INSERT_INTO = "into";
    public static final String KEY_CONF = "conf";
    public static final String KEY_FILE_SLASH = "file:/";
    public static final String KEY_FILE = "file:";
    public static final String KEY_APP_LOG = "appLog";
    public static final String KEY_DELETE = "delete";
    public static final String KEY_HREF = "href";
    public static final String KEY_ORDER_ID = "orderId";
    public static final String KEY_PRODUCT_ID = "productId";
    public static final String KEY_SORT = "sort";
    public static final String KEY_SCORE = "score";
    public static final String KEY_SAVE_STATUS = "saveStatus";
    public static final String KEY_ANONYMOUS_FLAG = "anonymousFlag";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_IMGS = "imgs";
    public static final String KEY_UNICK = "unick";
    public static final String KEY_LIB = "/lib";
    public static final String KEY_VERSION = "version";
    public static final String KEY_TRUNK = "trunk";
    public static final String KEY_BRANCHES = "branches";
    public static final String KEY_DESKTOP = "desktop";
    public static final String KEY_SOURCES = "Sources";
    public static final String KEY_FIX = "fix";
    public static final String KEY_B = "B";
    public static final String KEY_BACKUP = "bak";
    public static final String KEY_FUND_SLASH = "FUND/";
    public static final String KEY_FUND = "FUND";
    public static final String KEY_SOURCES_TA_FUND = "/Sources/ta/fund";
    public static final String KEY_TEMP = "temp/";

    public final static String KEY_APP_ID = "app_id";
    public final static String KEY_APP_KEY = "app_key";
    public final static String KEY_MESSAGE = "message";
    public final static String KEY_CODE = "code";
    public final static String KEY_DATA = "data";
    public final static String KEY_METHOD = "method";
    public final static String KEY_OPERATE_TYPE = "operate_type";
    public final static String KEY_ID = "id";
    public final static String KEY_ESTIMATE_FINISH_TIME = "estimate_finish_time";
    public final static String KEY_NAME = "name";
    public final static String KEY_SPRINT_VERSION = "sprint_version";
    public final static String KEY_TASK_NUMBER = "task_number";
    public final static String KEY_CURRENT_USER_ID = "current_user_id";
    public final static String KEY_CHARSET = "charset";
    public final static String KEY_FORMAT = "format";
    public final static String KEY_TIMESTAMP = "timestamp";
    public final static String KEY_STATUS_LIST = "status_list";
    public final static String KEY_SIGN = "sign";
    public final static String KEY_REAL_FINISH_TIME = "real_finish_time";
    public final static String KEY_SUGGESTION = "suggestion";
    public final static String KEY_REAL_WORKLOAD = "real_workload";
    public final static String KEY_FINISH_PERCENTAGE = "finish_percentage";
    public final static String KEY_INTEGRATE_ATTENTION = "integrate_attention";
    public final static String KEY_MODIFIED_FILE = "modified_file";
    public final static String KEY_EDIT_DESCRIPTION = "edit_description";
    public final static String KEY_NEXT = "next";
    public final static String KEY_MENU_CODE = "menu_code";
    public final static String KEY_MENU_NAME = "menu_name";
    public final static String KEY_MENU_SUB = "menu_sub";
    public final static String KEY_COLUMN_NAME = "column_name";
    public final static String KEY_PK_NAME = "pk_name";
    public final static String KEY_DATA_TYPE = "data_type";
    public final static String KEY_DATA_LENGTH = "data_length";
    public final static String KEY_DATA_PRECISION = "data_precision";
    public final static String KEY_DATA_SCALE = "data_scale";
    public final static String KEY_DATA_DEFAULT = "data_default";
    public final static String KEY_PRIMARY = "primary";
    public final static String KEY_CLOSE_DATE = "closeDate";
    public final static String KEY_PUBLISH_DATE = "publishDate";
    public final static String KEY_CUSTOMER = "customer";
    public final static String KEY_ORDER_NO = "orderNo";
    public final static String KEY_MENU_EXTEND = "menuExtend";
    public final static String KEY_TRANS_EXTEND = "transExtend";
    public final static String KEY_SUB_TRANS_EXTEND = "subTransExtend";
    public final static String KEY_SUB_TRANS_EXT_EXTEND = "subTransExtExtend";
    public final static String KEY_MENU = "menu";
    public final static String KEY_TRANS = "trans";
    public final static String KEY_TRANS_EXT = "transExt";

    public static final String METHOD_QUERY_DATA = "queryData";
    public static final String METHOD_GOODS_APPRAISE = "goodsAppraise";

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
    public static final String FILE_TYPE_RECORD = ".record";
    public static final String FILE_TYPE_RPX = ".rpx";

    public static final String NAME_DELETE = " 删除";
    public static final String NAME_END = "结束";
    public static final String NAME_SVN_DESCRIBE = "[需求描述]";
    public static final String NAME_SVN_MODIFY_NO = "[修改单编号]";
    public static final String NAME_CURRENT_VERSION = "当前版本:";
    public static final String NAME_SAVE_SUCCESS = "修改成功 ";
    public static final String NAME_APP_START = "应用";
    public static final String NAME_CONFIG_INFO = "配置信息";
    public static final String NAME_CONFIG_USER = "用户信息";
    public static final String NAME_CONFIG_LICENSE = "授权信息";
    public static final String NAME_CONFIG_VIEW = "功能界面";
    public static final String NAME_CONFIG_LICENSE_DATE = "授权有效日期";
    public static final String NAME_FILE_SAME = "文件一致性";
    public static final String NAME_CONFIG_FILE = "配置文件";
    public static final String NAME_VERSION = "版本";
    public static final String NAME_MAC = "MAC地址";
    public static final String NAME_APP_TAB_SHOW = "# 默认展示tab页";
    public static final String NAME_APP_TAB_SHOW_CODE = "app.tab.show";
    public static final String NAME_SVN_STAT = "#################### svn统计";
    public static final String NAME_SVN_STAT_REALTIME = "#################### 实时统计";
    public static final String NAME_SVN_STAT_HISTORY = "#################### 历史统计";
    public static final String NAME_MENU_HEP_TASK_TODO = "#################### 效能";
    public static final String NAME_CONFIG_COLUMN = "字段参数";

    public static final String NAME_SHAKE_MOUSE = "模拟鼠标移动";
    public static final String NAME_UPDATE_VERSION = "同步发版时间";
    public static final String NAME_CHECK_MENU = "配置脚本检查";
    public static final String NAME_UPDATE_MENU = "新版全量升级";
    public static final String NAME_REPAIR_LACK_EXT = "修复缺少日志";
    public static final String NAME_REPAIR_ERROR_EXT = "修复错误日志";
    public static final String NAME_REPAIR_WORK_FLOW = "修正复核信息";
    public static final String NAME_REPAIR_OLD_MENU = "修正老版全量";
    public static final String NAME_REPAIR_NEW_MENU = "修正新版全量";

    public static final String NAME_MENU_UPDATE = "更新";
    public static final String NAME_MENU_COPY = "复制";
    public static final String NAME_MENU_SIMPLE_COPY = "简复";
    public static final String NAME_MENU_DETAIL = "详情";
    public static final String NAME_MENU_SCRIPT = "脚本";
    public static final String NAME_MENU_TASK_NO = "单号";


    public static final String NAME_MENU_DCIT = "字典";
    public static final String NAME_MENU_DATE = "日期";
    public static final String NAME_MENU_DECIMAL = "小数";

    public final static String SECURITY_FLAG = "+++";

    public final static String STR_BLANK = "";
    public final static String STR_AND = "&";
    public final static String STR_EQUAL = "=";
    public final static String STR_SPACE = " ";
    public final static String STR_SPACE_2 = "  ";
    public final static String STR_SPACE_3 = "   ";
    public final static String STR_SPACE_4 = "    ";
    public final static String STR_EQUALS = "=";
    public static final String STR_COMMA = ",";
    public static final String STR_COMMA_1 = "，";
    public static final String STR_CAESURA  = "、";
    public static final String STR_SEMICOLON = ";";
    public static final String STR_COLON = ":";
    public static final String STR_HYPHEN = "-";
    public static final String STR_HYPHEN_1 = " - ";
    public final static String STR_POINT = ".";
    public final static String STR_$_SLASH = "\\$";
    public final static String STR_S_SLASH = "\\s+";
    public final static String STR_POINT_SLASH = "\\.";
    public static final String STR_SLASH = "/";
    public static final String STR_SLASH_1 = "\\";
    public static final String STR_BRACKETS_LEFT = "(";
    public static final String STR_BRACKETS_RIGHT = ")";
    public static final String STR_BRACKETS_1_LEFT = "[";
    public static final String STR_BRACKETS_2_LEFT = "「";
    public static final String STR_BRACKETS_3_LEFT = "【";
    public static final String STR_BRACKETS_1_RIGHT = "]";
    public final static String STR_BRACKETS_2_RIGHT = "」";
    public final static String STR_BRACKETS_3_RIGHT = "】";
    public static final String STR_NEXT_LINE = "\n";
    public static final String STR_SLASH_T = "\t";
    public static final String STR_NEXT_LINE_2 = "\n\n";
    public static final String STR_NEXT_LINE_3 = "\n\n\n";
    public static final String STR_PERCENT = "%";
    public static final String STR_QUOTES = "\"";
    public static final String STR_QUOTES_SINGLE = "'";
    public static final String STR_BR = "</br>";

    public final static String ANNOTATION_NORMAL = "--";
    public final static String ANNOTATION_CONFIG = "#";

    public final static Integer FUNCTION_CONFIG_SET = 9999;

    public static final Pattern STR_PATTERN = Pattern.compile("(\\\\u(\\w{4}))");

    public static final String MSG_SVN_USERNAME = "请设置【 svn.username 】";
    public static final String MSG_SVN_PASSWORD = "请设置【 svn.password 】";
    public static final String MSG_SVN_URL = "请设置【 svn.url 】且名称不能为【 svn.url.demo 】";
    public static final String MSG_SVN_UPDATE_TA6 = "请设置【 svn.update.ta6 】";
    public static final String MSG_FUND_GENERATE_PATH = "请设置【 fund.generate.path 】";
    public static final String MSG_PROCESS_GENERATE_PATH_SCHEDULE = "请设置【 process.generate.path.schedule 】";
    public static final String MSG_PROCESS_GENERATE_PATH_TRANS = "请设置【 process.generate.path.trans 】";
    public static final String MSG_SCRIPT_UPDATE_GENERATE_PATH = "请设置【 script.update.generate.path 】";
    public static final String MSG_SVN_STAT_USER = "请设置【 svn.stat.user. 】";
    public static final String MSG_SVN_STAT_INTERVAL = "请设置【 svn.stat.interval 】大于等于10";
    public static final String MSG_GENERATE_CODE_TIPS = "请设置【 %s 】\n";
    public static final String MSG_SET = "请设置【 %s 】\n";
    public static final String MSG_APP_TITLE = "   【%s: %s】";


    public static final String MSG_OPEN = "打开【 %s 】 功能界面";
    public static final String MSG_USE = "使用【 %s 】 功能";
    public static final String MSG_UPDATE = "更新【 %s 】 完成";
    public static final String MSG_LOAD = "加载【 %s 】 完成";
    public static final String MSG_INIT = "初始化【 %s 】 完成";
    public static final String MSG_ENCRYPT = "加密【 %s 】 完成";
    public static final String MSG_CHECK = "校验【 %s 】 完成";
    public static final String MSG_START = "启动【 %s 】 开始";
    public static final String MSG_COMPLETE = "启动【 %s 】 完成";
    public static final String MSG_FUNCTION_NOT_EXIST = "功能【 %s 】不存在";
    public static final String MSG_LICENSE_NOT_EXIST = "加载证书信息失败,请检查证书文件【 license.init 】";
    public static final String MSG_LICENSE_EXPIRE = "授权证书已过期,授权截止【 %s 】";
    public static final String MSG_LICENSE_EXPIRE_TIPS = "距离授权截止日期还有【 %s 】天";
    public static final String MSG_LICENSE_NOT_AUTH = "功能【 %s 】未授权";
    public static final String MSG_LICENSE_NOT_USE = "未启用【 %s 】功能";
    public static final String MSG_DIVIDE_LINE = "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ";
    public static final String MSG_SVN_REALTIME_STAT = "%s  %s  %s  %s";
    public static final String MSG_TIPS = "【%s】";
    public static final String MSG_WAIT_HANDLE_NUM = "-- 待处理【%s】\n\n";
    public static final String MSG_WAIT_HANDLE_NUM_1 = "-- 待处理【%s】\n";
    public static final String MSG_WAIT_HANDLE_NUM_0 = "-- 待处理【%s】";
    public static final String MSG_WAIT_HANDLE_EVENT = "-- *************************************  %s  *************************************";

    public static final String APP_MODE_NAME = "模式";
    public static final String APP_MODE_NAME_APP = "工厂模式";
    public static final String APP_MODE_NAME_JAR = "Jar模式";

    public static final String CONFIG_PREFIX = "#################### ";

    public static final String CMD_KILL_APP = "taskill /f /t /im %s";

    public static final String DEFAULT_FOLDER = "/extend";
    public static final String PATH_APP = "/conf/app.conf";
    public static final String PATH_APP_EXTEND = "/conf/extend/appExtend.conf";
    public static final String PATH_AUTH = "/conf/auth/auth.conf";
    public static final String PATH_LICENSE = "/conf/init/license.init";
    public static final String PATH_VERSION = "/conf/init/version.init";
    public static final String PATH_FXML = "/conf/fxml";
    public static final String PATH_STYLE = "/conf/style";
    public static final String PATH_LOG = "/logs/%s/%s";
    public static final String SUB_PATH_LOG = "/logs/%s";
    public static final String PATH_STAT = "/stats/%s";
    public static final String PATH_ICON = "/conf/image/icon.png";
    public static final String FACTORY_ICON = "/conf/image/factory.png";
    public static final String COPY_ICON = "/conf/image/copy.png";
    public static final String COPY_SIMPLE_ICON = "/conf/image/copySimple.png";
    public static final String SCRIPT_ICON = "/conf/image/sql.png";
    public static final String TASK_NO_ICON = "/conf/image/code.png";
    public static final String UPDATE_ICON = "/conf/image/update.png";
    public static final String DETAIL_ICON = "/conf/image/detail.png";
    public static final String PATH_STARTER_FXML = "/conf/fxml/starter.fxml";
    public static final String PATH_STARTER_CSS = "/conf/style/progressIndicator.css";
    public static final String PATH_COLUMN_SET_FXML = "/conf/fxml/generateCodeColumnSet.fxml";
    public static final String PATH_COMPLETE_TASK_FXML = "/conf/fxml/completeTask.fxml";
    public static final String PATH_BLANK_TABLE_VIEW = "/conf/fxml/blankTableView.fxml";
    public static final String PATH_BLANK_SET_FXML = "/conf/fxml/blankSet.fxml";
    public static final String PATH_BLANK_CHECK_RESULT_FXML = "/conf/fxml/checkResult.fxml";
    public static final String PATH_VERSION_STAT = "/stats/version.stat";
    public static final String PATH_VERSION_EXTEND_STAT = "/stats/versionExtend.stat";

    public static final String SERVER_HTTP = "http://127.0.0.1";
    public static final String SERVER_URL = "/tool/im";
    public static final Integer SERVER_PORT = 13579;

    public static final Integer SECOND_CLICKED = 2;
    public static final String RIGHT_CLICKED = "SECONDARY";
    public static final String LEFT_CLICKED = "PRIMARY";

    public static final Integer MENUITEM_ICON_SIZE = 20;

    public static final String MENU_CODE_QUERY = "query";
    public static final String MENU_CODE_PARAM = "param";
    public static final String MENU_CODE_BUSIN = "busin";

    public static final String PAGE_TYPE_HEP_DETAIL = "detail";
    public static final String PAGE_TYPE_SYSTEM_TOOL_CHECK_RESULT = "checkResult";
    public static final String PAGE_TYPE_SYSTEM_TOOL_UPDATE_RESULT = "updateResult";
    public static final String PAGE_TYPE_SYSTEM_TOOL_REPAIR_ERROR_LOG = "repairErrorLog";
    public static final String PAGE_TYPE_SYSTEM_TOOL_SYSTEM_LOG = "systemLog";
    public static final String PAGE_TYPE_SYSTEM_TOOL_REPAIR_OLD_MENU_LOG = "repairOldMenuLog";
    public static final String PAGE_TYPE_SYSTEM_TOOL_REPAIR_NEW_MENU_LOG = "repairNewMenuLog";
    public static final String PAGE_TYPE_SYSTEM_TOOL_REPAIR_WORK_FLOW_LOG = "repairWorkFlowLog";

    public static final String MENU_TYPE_FUND_CODE = "1";
    public static final String MENU_TYPE_FUND_NAME = "自建业务";
    public static final String MENU_TYPE_ACCOUNT_CODE = "2";
    public static final String MENU_TYPE_ACCOUNT_NAME = "账户中心";

    public static final String MENU_FUNCTION_SET_CODE = "1";
    public static final String MENU_FUNCTION_SET_NAME = "方案设置";
    public static final String MENU_FUNCTION_QUERY_CODE = "2";
    public static final String MENU_FUNCTION_QUERY_NAME = "数据查询";

    public static final String MENU_DATASOURCE_PUB_CODE = "1";
    public static final String MENU_DATASOURCE_PUB_NAME = "主库";
    public static final String MENU_DATASOURCE_QUERY_CODE = "2";
    public static final String MENU_DATASOURCE_QUERY_NAME = "查询库";
    public static final String MENU_DATASOURCE_SHARDING_CODE = "3";
    public static final String MENU_DATASOURCE_SHARDING_NAME = "sharding";

    public static final String MENU_MULTIPLE_TABLE_YES_CODE = "1";
    public static final String MENU_MULTIPLE_TABLE_YES_NAME = "是";
    public static final String MENU_MULTIPLE_TABLE_NO_CODE = "0";
    public static final String MENU_MULTIPLE_TABLE_NO_NAME = "否";

    public static final String COLUMN_TYPE_VARCHAR2 = "varchar2";
    public static final String COLUMN_TYPE_NUMBER = "number";
    public static final String COLUMN_TYPE_INTEGER = "integer";
    public static final String COLUMN_TYPE_CLOB = "clob";

    public static final String STYLE_CENTER = "-fx-alignment: center;";

    public static final String CONF_FUNCTION_PREFIX = "####################";

    public enum SQL_CHECK_TYPE {
        CHECK_RESULT_SUMMARY(10, "结果汇总", "10.结果汇总.sql", ""),
        LACK_NEW_MENU_ALL(100, "缺少新版全量", "100.缺少新版全量.sql", "/conf/extend/skipNewMenu.conf"),
        LACK_OLD_NEW_ALL(800, "缺少老版全量", "800.缺少老版全量.sql", "/conf/extend/skipOldMenu.conf"),
        DIFF_NEW_ALL_EXT(200, "新版全量开通不同", "200.新版全量开通不同.sql", "/conf/extend/skipNewDiffMenu.conf"),
        DIFF_OLD_ALL_EXT(700, "老版全量开通不同", "700.老版全量开通不同.sql", "/conf/extend/skipOldDiffMenu.conf"),
        LEGAL_NEW_MENU(300, "新版菜单合法性", "300.新版菜单合法性.sql", "/conf/extend/legalNewMenu.conf"),
        LACK_ROUTER(400, "缺少路由", "400.缺少路由.sql", "/conf/extend/skipRouter.conf"),
        LACK_LOG(500, "缺少日志", "500.缺少日志.sql", "/conf/extend/skipLog.conf"),
        ERROR_LOG(600, "错误日志", "600.错误日志.sql", "/conf/extend/skipErrorLog.conf"),
        ALL_MENU(10000, "所有菜单", "5.所有菜单.sql", ""),
        NEW_MENU_UPDATE(20000, "全量新版升级", "1.全量新版升级.sql", "");

        private int index;

        private String name;

        private String fileName;

        private String pathConf;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getPathConf() {
            return pathConf;
        }

        public void setPathConf(String pathConf) {
            this.pathConf = pathConf;
        }

        SQL_CHECK_TYPE(int index, String name, String fileName, String pathConf) {
            this.index = index;
            this.name = name;
            this.fileName = fileName;
            this.pathConf = pathConf;
        }
    }

    public enum SQL_CHECK_TYPE_EXTEND {
        REPAIR_OLD_MENU(50, "修正老版全量", "50.修正老版全量.sql", "/conf/extend/skipRepairOldMenu.conf");

        private int index;

        private String name;

        private String fileName;

        private String pathConf;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getPathConf() {
            return pathConf;
        }

        public void setPathConf(String pathConf) {
            this.pathConf = pathConf;
        }

        SQL_CHECK_TYPE_EXTEND(int index, String name, String fileName, String pathConf) {
            this.index = index;
            this.name = name;
            this.fileName = fileName;
            this.pathConf = pathConf;
        }
    }

    public enum COLUMN_TYPE {
        DICT("1", "字典"),
        DATE("2", "日期"),
        DECIMAL("3", "小数");

        private String code;

        private String name;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        COLUMN_TYPE(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

}
