package com.hoomoomoo.im.consts;

import java.util.regex.Pattern;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public class BaseConst {

    public static final String STR_0 = "0";
    public static final String STR_1 = "1";
    public static final String STR_2 = "2";
    public static final String STR_3 = "3";
    public static final String STR_4 = "4";
    public static final String STR_5 = "5";

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
    public static final String KEY_SVN_STAT = "svn.stat";

    public static final String KEY_NOTICE = "notice";
    public static final String KEY_TRUE = "true";
    public static final String KEY_VALUES = "values";
    public static final String KEY_INSERT_INTO = "into";
    public static final String KEY_CONF = "conf";
    public static final String KEY_FILE_SLASH = "file:/";
    public static final String KEY_FILE = "file:";
    public static final String KEY_APP_LOG = "appLog";

    public static final String OPERATE_TYPE_DELETE = "D";

    public static final String FILE_TYPE_SVN = ".svn";
    public static final String FILE_TYPE_LOG = ".log";
    public static final String FILE_TYPE_JAR = ".jar";
    public static final String FILE_TYPE_BAK = ".bak";
    public static final String FILE_TYPE_EXE = ".exe";
    public static final String FILE_TYPE_VUE = ".vue";
    public static final String FILE_TYPE_STAT = ".stat";

    public static final String NAME_DELETE = " 删除";
    public static final String NAME_END = "结束";
    public static final String NAME_SVN_DESCRIBE = "[需求描述]";

    public final static String SECURITY_FLAG = "+++";

    public final static String SYMBOL_EMPTY = "";
    public final static String SYMBOL_SPACE = " ";
    public final static String SYMBOL_SPACE_2 = "  ";
    public final static String SYMBOL_SPACE_3 = "   ";
    public final static String SYMBOL_SPACE_4 = "    ";
    public final static String SYMBOL_EQUALS = "=";
    public static final String SYMBOL_COMMA = ",";
    public static final String SYMBOL_SEMICOLON = ";";
    public static final String SYMBOL_COLON = ":";
    public static final String SYMBOL_HYPHEN = "-";
    public final static String SYMBOL_POINT = ".";
    public final static String SYMBOL_$_SLASH = "\\$";
    public final static String SYMBOL_POINT_SLASH = "\\.";
    public static final String SYMBOL_SLASH = "/";
    public static final String SYMBOL_BRACKETS_LEFT = "(";
    public static final String SYMBOL_BRACKETS_RIGHT = ")";
    public static final String SYMBOL_BRACKETS_1_LEFT = "[";
    public static final String SYMBOL_BRACKETS_1_RIGHT = "]";
    public static final String SYMBOL_NEXT_LINE = "\n";
    public static final String SYMBOL_NEXT_LINE_2 = "\n\n";
    public static final String SYMBOL_PERCENT = "%";

    public final static String ANNOTATION_TYPE_NORMAL = "--";
    public final static String ANNOTATION_TYPE_CONFIG = "#";

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

    public static final String MSG_OPEN = "打开[ %s ] 功能界面";
    public static final String MSG_USE = "使用[ %s ] 功能";
    public static final String MSG_UPDATE = "更新[ %s ] 完成";
    public static final String MSG_LOAD = "加载[ %s ] 完成";
    public static final String MSG_INIT = "初始化[ %s ] 完成";
    public static final String MSG_ENCRYPT = "加密[ %s ] 完成";
    public static final String MSG_CHECK = "校验[ %s ] 完成";
    public static final String MSG_START = "开始[ %s ]";
    public static final String MSG_COMPLETE = "完成[ %s ]";
    public static final String MSG_FUNCTION_NOT_EXIST = "不存在[ %s ]功能";
    public static final String MSG_LICENSE_NOT_EXIST = "加载证书信息失败,请检查证书文件[ license.init ]";
    public static final String MSG_LICENSE_EXPIRE = "授权证书已过期,授权截止[ %s ]";
    public static final String MSG_LICENSE_NOT_AUTH = "未授权[ %s ]功能";
    public static final String MSG_DIVIDE_LINE = "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ";
    public static final String MSG_SVN_REALTIME_STAT = "%s  %s  %s  %s";


    public static final String CONFIG_PREFIX = "#################### ";

    public static final String CMD_KILL_APP = "taskill /f /t /im %s";

    public static final String PATH_APP = "/conf/app.conf";
    public static final String PATH_AUTH = "/conf/auth/auth.conf";
    public static final String PATH_LICENSE = "/conf/init/license.init";
    public static final String PATH_SVN_STAT = "/conf/init/svnStat.init";
    public static final String PATH_VERSION = "/conf/init/version.init";
    public static final String PATH_LOG = "/logs/%s/%s";
    public static final String PATH_STAT = "/stats/%s";

}
